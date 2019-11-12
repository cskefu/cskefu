/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.socketio.handler;

import com.chatopera.cc.acd.AutomaticServiceDist;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainContext.ReceiverType;
import com.chatopera.cc.basic.MainContext.MessageType;
import com.chatopera.cc.basic.MainContext.ChannelType;
import com.chatopera.cc.basic.MainContext.CallType;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.model.AgentService;
import com.chatopera.cc.model.Contacts;
import com.chatopera.cc.model.CousultInvite;
import com.chatopera.cc.persistence.repository.AgentServiceRepository;
import com.chatopera.cc.proxy.AgentUserProxy;
import com.chatopera.cc.proxy.OnlineUserProxy;
import com.chatopera.cc.socketio.client.NettyClients;
import com.chatopera.cc.socketio.message.AgentStatusMessage;
import com.chatopera.cc.socketio.message.ChatMessage;
import com.chatopera.cc.socketio.message.Message;
import com.chatopera.cc.socketio.util.HumanUtils;
import com.chatopera.cc.socketio.util.IMServiceUtils;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;

public class IMEventHandler {
    private final static Logger logger = LoggerFactory.getLogger(IMEventHandler.class);
    protected SocketIOServer server;

    public IMEventHandler(SocketIOServer server) {
        this.server = server;
    }

    static private AgentUserProxy agentUserProxy;
    static private AgentServiceRepository agentServiceRepository;

    /**
     * 接入访客并未访客寻找坐席服务人员
     *
     * @param client
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        try {
            final String user = client.getHandshakeData().getSingleUrlParam("userid");
            final String orgi = client.getHandshakeData().getSingleUrlParam("orgi");
            final String session = MainUtils.getContextID(client.getHandshakeData().getSingleUrlParam("session"));
            final String appid = client.getHandshakeData().getSingleUrlParam("appid");
            final String agent = client.getHandshakeData().getSingleUrlParam("agent");
            final String skill = client.getHandshakeData().getSingleUrlParam("skill");

            final String title = client.getHandshakeData().getSingleUrlParam("title");
            final String url = client.getHandshakeData().getSingleUrlParam("url");
            final String traceid = client.getHandshakeData().getSingleUrlParam("traceid");

            final String nickname = client.getHandshakeData().getSingleUrlParam("nickname");

            final String osname = client.getHandshakeData().getSingleUrlParam("osname");
            final String browser = client.getHandshakeData().getSingleUrlParam("browser");

            logger.info(
                    "[onConnect] user {}, orgi {}, session {}, appid {}, agent {}, skill {}, title {}, url {}, traceid {}, nickname {}",
                    user, orgi, session, appid, agent, skill, title, url, traceid, nickname);

            // save connection info
            client.set("session", session);
            client.set("userid", user);
            client.set("appid", appid);

            if (StringUtils.isNotBlank(user)) {
                InetSocketAddress address = (InetSocketAddress) client.getRemoteAddress();
                String ip = MainUtils.getIpAddr(client.getHandshakeData().getHttpHeaders(), address.getHostString());

                /**
                 * 加入到 缓存列表
                 */
                NettyClients.getInstance().putIMEventClient(user, client);

                /**
                 * 更新坐席服务类型
                 */
                IMServiceUtils.shiftOpsType(user, orgi, MainContext.OptType.HUMAN);

                /**
                 * 用户进入到对话连接 ， 排队用户请求 , 如果返回失败，
                 * 表示当前坐席全忙，用户进入排队状态，当前提示信息 显示 当前排队的队列位置，
                 * 不可进行对话，用户发送的消息作为留言处理
                 */
                Message agentServiceMessage = OnlineUserProxy.allocateAgentService(
                        user,
                        orgi,
                        session,
                        appid,
                        ip,
                        osname,
                        browser,
                        MainContext.ChannelType.WEBIM.toString(),
                        skill,
                        agent,
                        nickname,
                        title,
                        url,
                        traceid,
                        MainContext.ChatInitiatorType.USER.toString());

                if (agentServiceMessage != null && StringUtils.isNotBlank(
                        agentServiceMessage.getMessage())) {
                    logger.info("[onConnect] find available agent for onlineUser id {}", user);

                    /**
                     * 发送消息给坐席
                     */
                    if (agentServiceMessage.getAgentService() != null) {
                        // 通知消息到坐席
                        MainContext.getPeerSyncIM().send(ReceiverType.AGENT,
                                                         ChannelType.WEBIM,
                                                         appid,
                                                         MessageType.NEW,
                                                         agentServiceMessage.getAgentService().getAgentno(),
                                                         agentServiceMessage, true);
                    }

                    /**
                     * 发送消息给访客
                     */
                    Message outMessage = new Message();
                    outMessage.setMessage(agentServiceMessage.getMessage());
                    outMessage.setMessageType(MessageType.MESSAGE.toString());
                    outMessage.setCalltype(CallType.IN.toString());
                    outMessage.setCreatetime(MainUtils.dateFormate.format(new Date()));
                    outMessage.setNoagent(agentServiceMessage.isNoagent());
                    if (agentServiceMessage.getAgentService() != null) {
                        outMessage.setAgentserviceid(agentServiceMessage.getAgentService().getId());
                    }

                    MainContext.getPeerSyncIM().send(ReceiverType.VISITOR,
                                                     ChannelType.WEBIM, appid,
                                                     MessageType.NEW, user, outMessage, true);


                } else {
                    logger.info("[onConnect] can not find available agent for user {}", user);
                }
            } else {
                logger.warn("[onConnect] invalid connection, no user present.");
                //非法链接
                client.disconnect();
            }
        } catch (Exception e) {
            logger.error("[onConnect] error", e);
            client.disconnect();
        }
    }

    //添加@OnDisconnect事件，客户端断开连接时调用，刷新客户端信息
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        final String user = client.getHandshakeData().getSingleUrlParam("userid");
        final String orgi = client.getHandshakeData().getSingleUrlParam("orgi");
        logger.info("[onDisconnect] user {}, orgi {}", user, orgi);
        if (user != null) {
            try {
                /**
                 * 用户主动断开服务
                 */
                MainContext.getCache().findOneAgentUserByUserIdAndOrgi(user, orgi).ifPresent(p -> {
                    AutomaticServiceDist.serviceFinish(p
                            , orgi);
                });
            } catch (Exception e) {
                logger.warn("[onDisconnect] error", e);
            }
            NettyClients.getInstance().removeIMEventClient(
                    user, MainUtils.getContextID(client.getSessionId().toString()));
        }
    }

    // 消息接收入口，用于接受网站资源用户传入的 个人信息
    @OnEvent(value = "new")
    public void onNewEvent(SocketIOClient client, AckRequest request, Contacts contacts) {
        String user = client.getHandshakeData().getSingleUrlParam("userid");
        String orgi = client.getHandshakeData().getSingleUrlParam("orgi");

        MainContext.getCache().findOneAgentUserByUserIdAndOrgi(user, orgi).ifPresent(p -> {
            p.setName(contacts.getName());
            p.setPhone(contacts.getPhone());
            p.setEmail(contacts.getEmail());
            p.setChatbotops(false); // 非机器人客服
            p.setOpttype(MainContext.OptType.HUMAN.toString());
            getAgentUserProxy().save(p);
        });

        getAgentServiceRepository().findOneByUseridAndOrgiOrderByLogindateDesc(
                user, orgi).ifPresent(p -> {
            p.setName(contacts.getName());
            p.setPhone(contacts.getPhone());
            p.setEmail(contacts.getEmail());
            agentServiceRepository.save(p);
        });
    }

    // 消息接收入口，坐席状态更新
    @OnEvent(value = "agentstatus")
    public void onAgentStatusEvent(SocketIOClient client, AckRequest request, AgentStatusMessage data) {
        logger.info("[onEvent] {}", data.getMessage());
    }

    // 消息接收入口，收发消息，用户向 坐席发送消息 和 向用户发送消息
    @OnEvent(value = "message")
    public void onMessageEvent(SocketIOClient client, AckRequest request, ChatMessage data) {
        if (data.getType() == null) {
            data.setType("message");
        }
        /**
         * 以下代码主要用于检查 访客端的字数限制
         */
        CousultInvite invite = OnlineUserProxy.consult(data.getAppid(), data.getOrgi());

        int dataLength = data.getMessage().length();
        if (invite != null && invite.getMaxwordsnum() > 0) {
            if (StringUtils.isNotBlank(data.getMessage()) && dataLength > invite.getMaxwordsnum()) {
                data.setMessage(data.getMessage().substring(0, invite.getMaxwordsnum()));
            }
        }
//        else if (StringUtils.isNotBlank(data.getMessage()) && dataLength > 600) {
//            data.setMessage(data.getMessage().substring(0, 600));
//        }
        /**
         * 处理表情
         */
        data.setMessage(MainUtils.processEmoti(data.getMessage()));
        HumanUtils.processMessage(data, data.getUserid());
    }

    private static AgentUserProxy getAgentUserProxy() {
        if (agentUserProxy == null) {
            agentUserProxy = MainContext.getContext().getBean(AgentUserProxy.class);
        }
        return agentUserProxy;
    }

    private static AgentServiceRepository getAgentServiceRepository() {
        if (agentServiceRepository == null) {
            agentServiceRepository = MainContext.getContext().getBean(AgentServiceRepository.class);
        }
        return agentServiceRepository;
    }

}
