/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chatopera.cc.app.im.handler;

import com.chatopera.cc.app.algorithm.AutomaticServiceDist;
import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.app.basic.MainUtils;
import com.chatopera.cc.app.cache.CacheHelper;
import com.chatopera.cc.app.im.client.NettyClients;
import com.chatopera.cc.app.im.message.AgentServiceMessage;
import com.chatopera.cc.app.im.message.AgentStatusMessage;
import com.chatopera.cc.app.im.message.ChatMessage;
import com.chatopera.cc.app.im.router.OutMessageRouter;
import com.chatopera.cc.app.model.*;
import com.chatopera.cc.app.persistence.repository.*;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;

public class AgentEventHandler {
    protected SocketIOServer server;

    @Autowired
    public AgentEventHandler(SocketIOServer server) {
        this.server = server;
    }

    @OnConnect
    public void onConnect(SocketIOClient client) {
        String user = client.getHandshakeData().getSingleUrlParam("userid");
        String orgi = client.getHandshakeData().getSingleUrlParam("orgi");
        String session = client.getHandshakeData().getSingleUrlParam("session");
        String admin = client.getHandshakeData().getSingleUrlParam("admin");
        if (StringUtils.isNotBlank(user) && StringUtils.isNotBlank(user)) {
            client.set("agentno", user);
            AgentStatusRepository agentStatusRepository = MainContext.getContext().getBean(AgentStatusRepository.class);
            List<AgentStatus> agentStatusList = agentStatusRepository.findByAgentnoAndOrgi(user, orgi);
            if (agentStatusList.size() > 0) {
                AgentStatus agentStatus = agentStatusList.get(0);
                agentStatus.setUpdatetime(new Date());
                agentStatusRepository.save(agentStatus);
                if (CacheHelper.getAgentStatusCacheBean().getCacheObject(user, orgi) != null) {
                    CacheHelper.getAgentStatusCacheBean().put(user, agentStatus, orgi);
                }
            }
            InetSocketAddress address = (InetSocketAddress) client.getRemoteAddress();
            String ip = MainUtils.getIpAddr(client.getHandshakeData().getHttpHeaders(), address.getHostString());


            WorkSessionRepository workSessionRepository = MainContext.getContext().getBean(WorkSessionRepository.class);
            int count = workSessionRepository.countByAgentAndDatestrAndOrgi(user, MainUtils.simpleDateFormat.format(new Date()), orgi);

            workSessionRepository.save(MainUtils.createWorkSession(user, MainUtils.getContextID(client.getSessionId().toString()), session, orgi, ip, address.getHostName(), admin, count == 0));

            NettyClients.getInstance().putAgentEventClient(user, client);
        }
    }

    //添加@OnDisconnect事件，客户端断开连接时调用，刷新客户端信息  
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        String user = client.getHandshakeData().getSingleUrlParam("userid");
        String orgi = client.getHandshakeData().getSingleUrlParam("orgi");
        String admin = client.getHandshakeData().getSingleUrlParam("admin");
        if (StringUtils.isNotBlank(user)) {
            AutomaticServiceDist.deleteAgentStatus(user, orgi, StringUtils.isNotBlank(admin) && admin.equals("true"));
            NettyClients.getInstance().removeAgentEventClient(user, MainUtils.getContextID(client.getSessionId().toString()));

            WorkSessionRepository workSessionRepository = MainContext.getContext().getBean(WorkSessionRepository.class);
            List<WorkSession> workSessionList = workSessionRepository.findByOrgiAndClientid(orgi, MainUtils.getContextID(client.getSessionId().toString()));
            if (workSessionList.size() > 0) {
                WorkSession workSession = workSessionList.get(0);
                workSession.setEndtime(new Date());
                if (workSession.getBegintime() != null) {
                    workSession.setDuration((int) (System.currentTimeMillis() - workSession.getBegintime().getTime()));
                } else if (workSession.getCreatetime() != null) {
                    workSession.setDuration((int) (System.currentTimeMillis() - workSession.getCreatetime().getTime()));
                }
                if (workSession.isFirsttime()) {
                    workSession.setFirsttimes(workSession.getDuration());
                }
                workSessionRepository.save(workSession);
            }
        }
    }

    //消息接收入口，当接收到消息后，查找发送目标客户端，并且向该客户端发送消息，且给自己发送消息  
    @OnEvent(value = "service")
    public void onEvent(SocketIOClient client, AckRequest request, AgentServiceMessage data) {

    }

    //消息接收入口，当接收到消息后，查找发送目标客户端，并且向该客户端发送消息，且给自己发送消息
    @OnEvent(value = "status")
    public void onEvent(SocketIOClient client, AckRequest request, AgentStatusMessage data) {

    }

    //消息接收入口，当接收到消息后，查找发送目标客户端，并且向该客户端发送消息，且给自己发送消息
    @OnEvent(value = "message")
    public void onEvent(SocketIOClient client, AckRequest request, ChatMessage data) {
        String user = client.getHandshakeData().getSingleUrlParam("userid");
        AgentUser agentUser = (AgentUser) CacheHelper.getAgentUserCacheBean().getCacheObject(data.getTouser(), data.getOrgi());
        MessageOutContent outMessage = new MessageOutContent();
        outMessage.setMessage(data.getMessage());
        if (MainContext.MediaTypeEnum.COOPERATION.toString().equals(data.getMsgtype())) {
            outMessage.setMessageType(MainContext.MediaTypeEnum.COOPERATION.toString());
        } else {
            outMessage.setMessageType(MainContext.MediaTypeEnum.TEXT.toString());
        }

        outMessage.setAttachmentid(data.getAttachmentid());

        outMessage.setCalltype(MainContext.CallTypeEnum.OUT.toString());
        outMessage.setAgentUser(agentUser);
        outMessage.setSnsAccount(null);
        AgentStatus agentStatus = (AgentStatus) CacheHelper.getAgentStatusCacheBean().getCacheObject(data.getUserid(), data.getOrgi());

        if (agentUser == null) {
            agentUser = MainContext.getContext().getBean(AgentUserRepository.class).findByIdAndOrgi(data.getTouser(), data.getOrgi());
            try {
                AutomaticServiceDist.serviceFinish(agentUser, data.getOrgi());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (agentUser != null && user != null && user.equals(agentUser.getAgentno())) {
            data.setId(MainUtils.getUUID());
            data.setContextid(agentUser.getContextid());

            data.setAgentserviceid(agentUser.getAgentserviceid());
            data.setCreater(agentUser.getAgentno());

            if (MainContext.MediaTypeEnum.COOPERATION.toString().equals(data.getMsgtype())) {
                data.setMsgtype(MainContext.MediaTypeEnum.COOPERATION.toString());
            } else {
                data.setMsgtype(MainContext.MediaTypeEnum.TEXT.toString());
            }

            data.setCalltype(MainContext.CallTypeEnum.OUT.toString());
            if (StringUtils.isNotBlank(agentUser.getAgentno())) {
                data.setTouser(agentUser.getUserid());
            }
            data.setChannel(agentUser.getChannel());

            data.setUsession(agentUser.getUserid());

            outMessage.setContextid(agentUser.getContextid());
            outMessage.setFromUser(data.getUserid());
            outMessage.setToUser(data.getTouser());
            outMessage.setChannelMessage(data);
            if (agentStatus != null) {
                data.setUsername(agentStatus.getUsername());
                outMessage.setNickName(agentStatus.getUsername());
            } else {
                outMessage.setNickName(data.getUsername());
            }
            outMessage.setCreatetime(data.getCreatetime());

            AgentUserTaskRepository agentUserTaskRes = MainContext.getContext().getBean(AgentUserTaskRepository.class);
            AgentUserTask agentUserTask = agentUserTaskRes.getOne(agentUser.getId());

            if (agentUserTask != null) {
                if (agentUserTask.getLastgetmessage() != null && agentUserTask.getLastmessage() != null) {
                    data.setLastagentmsgtime(agentUserTask.getLastgetmessage());
                    data.setLastmsgtime(agentUserTask.getLastmessage());
                    data.setAgentreplyinterval((int) ((System.currentTimeMillis() - agentUserTask.getLastgetmessage().getTime()) / 1000));    //坐席上次回复消息的间隔
                    data.setAgentreplytime((int) ((System.currentTimeMillis() - agentUserTask.getLastmessage().getTime()) / 1000));        //坐席回复消息花费时间
                }

                agentUserTask.setAgentreplys(agentUserTask.getAgentreplys() + 1);    //总咨询记录数量
                agentUserTask.setAgentreplyinterval(agentUserTask.getAgentreplyinterval() + data.getAgentreplyinterval());    //总时长
                if (agentUserTask.getAgentreplys() > 0) {
                    agentUserTask.setAvgreplyinterval(agentUserTask.getAgentreplyinterval() / agentUserTask.getAgentreplys());
                }

                agentUserTask.setLastgetmessage(new Date());

//	    		agentUserTask.setReptime(null);
//	    		agentUserTask.setReptimes("0");

                agentUserTaskRes.save(agentUserTask);
            }

            /**
             * 保存消息
             */
            MainContext.getContext().getBean(ChatMessageRepository.class).save(data);

            client.sendEvent(MainContext.MessageTypeEnum.MESSAGE.toString(), data);

            if (StringUtils.isNotBlank(data.getTouser())) {
                OutMessageRouter router = null;
                router = (OutMessageRouter) MainContext.getContext().getBean(agentUser.getChannel());
                if (router != null) {
                    router.handler(data.getTouser(), MainContext.MessageTypeEnum.MESSAGE.toString(), agentUser.getAppid(), outMessage);
                }
            }
        } else if (user != null && agentUser != null && !user.equals(agentUser.getAgentno())) {
            client.sendEvent(MainContext.MessageTypeEnum.END.toString(), agentUser);
        }
    }
}  