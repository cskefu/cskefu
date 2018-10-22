/*
 * Copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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
import com.chatopera.cc.app.im.message.AgentStatusMessage;
import com.chatopera.cc.app.im.message.ChatMessage;
import com.chatopera.cc.app.im.message.NewRequestMessage;
import com.chatopera.cc.app.im.util.ChatbotUtils;
import com.chatopera.cc.app.im.util.IMServiceUtils;
import com.chatopera.cc.app.model.*;
import com.chatopera.cc.app.persistence.repository.AgentUserRepository;
import com.chatopera.cc.app.persistence.repository.ChatbotRepository;
import com.chatopera.cc.app.persistence.repository.ConsultInviteRepository;
import com.chatopera.cc.app.persistence.repository.OnlineUserRepository;
import com.chatopera.cc.concurrent.chatbot.ChatbotEvent;
import com.chatopera.cc.util.Constants;
import com.chatopera.cc.util.IP;
import com.chatopera.cc.util.IPTools;
import com.chatopera.cc.util.OnlineUserUtils;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;
import java.util.Date;

public class ChatbotEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ChatbotEventHandler.class);

    protected SocketIOServer server;

    private AgentUserRepository agentUserRes;
    private OnlineUserRepository onlineUserRes;
    private ChatbotRepository chatbotRes;

    @Autowired
    public ChatbotEventHandler(SocketIOServer server) {
        this.server = server;
    }

    @OnConnect
    public void onConnect(SocketIOClient client) {
        try {
            String user = client.getHandshakeData().getSingleUrlParam("userid");
            String nickname = client.getHandshakeData().getSingleUrlParam("nickname");
            String orgi = client.getHandshakeData().getSingleUrlParam("orgi");
            String session = client.getHandshakeData().getSingleUrlParam("session");
            String appid = client.getHandshakeData().getSingleUrlParam("appid");
            String aiid = client.getHandshakeData().getSingleUrlParam("aiid");
//			String agent = client.getHandshakeData().getSingleUrlParam("agent") ;
//			String skill = client.getHandshakeData().getSingleUrlParam("skill") ;
            Date now = new Date();

            if (StringUtils.isNotBlank(user)) {
                /**
                 * 加入到 缓存列表
                 */
                NettyClients.getInstance().putChatbotEventClient(user, client);
                CousultInvite invite = OnlineUserUtils.cousult(appid, orgi, MainContext.getContext().getBean(ConsultInviteRepository.class));

                /**
                 * 更新坐席服务类型
                 */
                IMServiceUtils.shiftOpsType(user, orgi, MainContext.OptTypeEnum.CHATBOT);

                // send out tip
                MessageOutContent tip = new MessageOutContent();
                tip.setMessage("您正在使用机器人客服！");
                tip.setMessageType(MainContext.MessageTypeEnum.MESSAGE.toString());
                tip.setCalltype(MainContext.CallTypeEnum.IN.toString());
                tip.setNickName(invite.getAiname());
                tip.setCreatetime(MainUtils.dateFormate.format(now));

                client.sendEvent(MainContext.MessageTypeEnum.STATUS.toString(), tip);

                // send out welcome message
                if (invite != null && StringUtils.isNotBlank(invite.getAisuccesstip())) {
                    ChatMessage welcome = new ChatMessage();
                    welcome.setCalltype(MainContext.CallTypeEnum.OUT.toString());
                    welcome.setAppid(appid);
                    welcome.setOrgi(orgi);
                    welcome.setAiid(aiid);
                    welcome.setMessage(invite.getAisuccesstip());
                    welcome.setTouser(user);
                    welcome.setTousername(nickname);
                    welcome.setMsgtype(MainContext.MessageTypeEnum.MESSAGE.toString());
                    welcome.setUserid(user);
                    welcome.setUsername(invite.getAiname());
                    welcome.setUpdatetime(System.currentTimeMillis());
                    client.sendEvent(MainContext.MessageTypeEnum.MESSAGE.toString(), welcome);
                }

                InetSocketAddress address = (InetSocketAddress) client.getRemoteAddress();
                String ip = MainUtils.getIpAddr(client.getHandshakeData().getHttpHeaders(), address.getHostString());
                OnlineUser onlineUser = getOnlineUserRes().findOne(user);

                if (onlineUser == null) {
                    onlineUser = new OnlineUser();
                    onlineUser.setAppid(appid);
                    if (StringUtils.isNotBlank(nickname)) {
                        onlineUser.setUsername(nickname);
                    } else {
                        onlineUser.setUsername(MainContext.GUEST_USER + "_" + MainUtils.genIDByKey(user));
                    }

                    onlineUser.setSessionid(session);
                    onlineUser.setOptype(MainContext.OptTypeEnum.CHATBOT.toString());
                    onlineUser.setUserid(user);
                    onlineUser.setId(user);
                    onlineUser.setOrgi(orgi);
                    onlineUser.setChannel(MainContext.ChannelTypeEnum.WEBIM.toString());
                    onlineUser.setIp(ip);
                    onlineUser.setUpdatetime(now);
                    onlineUser.setLogintime(now);
                    onlineUser.setCreatetime(now);
                    IP ipdata = IPTools.getInstance().findGeography(ip);
                    onlineUser.setCity(ipdata.getCity());
                    onlineUser.setCountry(ipdata.getCountry());
                    onlineUser.setProvince(ipdata.getProvince());
                    onlineUser.setIsp(ipdata.getIsp());
                    onlineUser.setRegion(ipdata.getRegion());
                    onlineUser.setStatus(MainContext.OnlineUserOperatorStatus.ONLINE.toString());
                }

                // 在线客服访客咨询记录
                AgentUser agentUser = new AgentUser(onlineUser.getId(),
                        MainContext.ChannelTypeEnum.WEBIM.toString(), // callout
                        onlineUser.getId(),
                        onlineUser.getUsername(),
                        MainContext.SYSTEM_ORGI,
                        appid);

                agentUser.setServicetime(now);
                agentUser.setCreatetime(now);
                agentUser.setUpdatetime(now);
                agentUser.setSessionid(session);

                // 聊天机器人处理的请求
                agentUser.setOpttype(MainContext.OptTypeEnum.CHATBOT.toString());
                agentUser.setAgentno(aiid); // 聊天机器人ID
                agentUser.setCity(onlineUser.getCity());
                agentUser.setProvince(onlineUser.getProvince());
                agentUser.setCountry(onlineUser.getCountry());
                AgentService agentService = AutomaticServiceDist.processChatbotService(agentUser, orgi);
                agentUser.setAgentserviceid(agentService.getId());

                // 标记为机器人坐席
                agentUser.setChatbotops(true);

                getAgentUserRes().save(agentUser);
                getOnlineUserRes().save(onlineUser);
                CacheHelper.getAgentUserCacheBean().put(user, agentUser, orgi);
                CacheHelper.getOnlineUserCacheBean().put(user, onlineUser, orgi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //添加@OnDisconnect事件，客户端断开连接时调用，刷新客户端信息  
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) throws Exception {
        String user = client.getHandshakeData().getSingleUrlParam("userid");
        String orgi = client.getHandshakeData().getSingleUrlParam("orgi");
        if (StringUtils.isNotBlank(user)) {
            NettyClients.getInstance().removeChatbotEventClient(user, MainUtils.getContextID(client.getSessionId().toString()));
            AgentUser agentUser = (AgentUser) CacheHelper.getAgentUserCacheBean().getCacheObject(user, orgi);
            OnlineUser onlineUser = (OnlineUser) CacheHelper.getOnlineUserCacheBean().getCacheObject(user, orgi);
            if (agentUser != null) {
                AutomaticServiceDist.processChatbotService(agentUser, orgi);
                CacheHelper.getAgentUserCacheBean().delete(user, MainContext.SYSTEM_ORGI);
                CacheHelper.getOnlineUserCacheBean().delete(user, orgi);
                agentUser.setStatus(MainContext.OnlineUserOperatorStatus.OFFLINE.toString());
                onlineUser.setStatus(MainContext.OnlineUserOperatorStatus.OFFLINE.toString());
                getAgentUserRes().save(agentUser);
                getOnlineUserRes().save(onlineUser);
            }
        }
        client.disconnect();
    }

    //消息接收入口，网站有新用户接入对话  
    @OnEvent(value = "new")
    public void onEvent(SocketIOClient client, AckRequest request, NewRequestMessage data) {

    }

    //消息接收入口，坐席状态更新
    @OnEvent(value = "agentstatus")
    public void onEvent(SocketIOClient client, AckRequest request, AgentStatusMessage data) {
        System.out.println(data.getMessage());
    }

    //消息接收入口，收发消息，用户向坐席发送消息和 坐席向用户发送消息  
    @OnEvent(value = "message")
    public void onEvent(SocketIOClient client, AckRequest request, ChatMessage data) {
        String orgi = client.getHandshakeData().getSingleUrlParam("orgi");
        String aiid = client.getHandshakeData().getSingleUrlParam("aiid");
        String user = client.getHandshakeData().getSingleUrlParam("userid");
        String sessionid = MainUtils.getContextID(client.getSessionId().toString());
        logger.info("[chatbot] onEvent message: orgi {}, aiid {}, userid {}, dataType {}", orgi, aiid, user, data.getType());
        // ignore event if dataType is not message.
        if (!StringUtils.equals(data.getType(), Constants.IM_MESSAGE_TYPE_MESSAGE)) {
            return;
        }

        AgentUser agentUser = (AgentUser) CacheHelper.getAgentUserCacheBean().getCacheObject(user, orgi);

        // ignore event if no agentUser found.
        if (agentUser == null)
            return;

        /**
         * 以下代码主要用于检查 访客端的字数限制
         */
        CousultInvite invite = OnlineUserUtils.cousult(data.getAppid(), data.getOrgi(), MainContext.getContext().getBean(ConsultInviteRepository.class));
        // ignore event if no invite found.
        if (invite == null)
            return;

        // ignore if Chatbot is turnoff.
        if (!invite.isAi())
            return;

        Date now = new Date();
        if (invite.getMaxwordsnum() > 0) {
            if (StringUtils.isNotBlank(data.getMessage()) && data.getMessage().length() > invite.getMaxwordsnum()) {
                data.setMessage(data.getMessage().substring(0, invite.getMaxwordsnum()));
            }
        } else if (StringUtils.isNotBlank(data.getMessage()) && data.getMessage().length() > 300) {
            data.setMessage(data.getMessage().substring(0, 300));
        }

        data.setUsession(user); // 绑定唯一用户
        data.setSessionid(sessionid);
        data.setMessage(MainUtils.processEmoti(data.getMessage())); // 处理表情
        data.setTouser(aiid);
        data.setTousername(invite.getAiname());
        data.setUsername(agentUser.getUsername());
        data.setAiid(aiid);
        data.setAgentserviceid(agentUser.getAgentserviceid());
        data.setChannel(agentUser.getChannel());
        data.setContextid(agentUser.getAgentserviceid()); // 一定要设置 ContextID
        data.setCalltype(MainContext.CallTypeEnum.IN.toString());

        ChatbotUtils.createTextMessage(data,
                MainContext.CallTypeEnum.IN.toString(),
                MainContext.ChatbotItemType.USERINPUT.toString());

        // 更新访客咨询记录
        agentUser.setUpdatetime(now);
        agentUser.setLastmessage(now);
        agentUser.setLastmsg(data.getMessage());
        CacheHelper.getAgentUserCacheBean().put(user, agentUser, orgi);
        getAgentUserRes().save(agentUser);

        // 发送消息给Bot
        MainUtils.chatbot(new ChatbotEvent<ChatMessage>(data,
                Constants.CHATBOT_EVENT_TYPE_CHAT));
    }

    /**
     * Lazy load
     *
     * @return
     */
    private AgentUserRepository getAgentUserRes() {
        if (agentUserRes == null)
            agentUserRes = MainContext.getContext().getBean(AgentUserRepository.class);
        return agentUserRes;
    }

    /**
     * Lazy load
     *
     * @return
     */
    private OnlineUserRepository getOnlineUserRes() {
        if (onlineUserRes == null)
            onlineUserRes = MainContext.getContext().getBean(OnlineUserRepository.class);
        return onlineUserRes;
    }

    private ChatbotRepository getChatbotRes() {
        if (chatbotRes == null)
            chatbotRes = MainContext.getContext().getBean(ChatbotRepository.class);
        return chatbotRes;
    }

}