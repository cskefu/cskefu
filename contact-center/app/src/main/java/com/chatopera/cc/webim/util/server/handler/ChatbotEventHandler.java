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
package com.chatopera.cc.webim.util.server.handler;

import com.chatopera.cc.core.UKDataContext;
import com.chatopera.cc.util.IPTools;
import com.chatopera.cc.util.UKTools;
import com.chatopera.cc.util.client.NettyClients;
import com.chatopera.cc.webim.service.acd.ServiceQuene;
import com.chatopera.cc.webim.service.cache.CacheHelper;
import com.chatopera.cc.webim.service.repository.ConsultInviteRepository;
import com.chatopera.cc.webim.util.MessageUtils;
import com.chatopera.cc.webim.util.OnlineUserUtils;
import com.chatopera.cc.webim.util.router.OutMessageRouter;
import com.chatopera.cc.webim.util.server.message.AgentStatusMessage;
import com.chatopera.cc.webim.util.server.message.ChatMessage;
import com.chatopera.cc.webim.util.server.message.NewRequestMessage;
import com.chatopera.cc.webim.web.model.*;
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

    @Autowired
    public ChatbotEventHandler(SocketIOServer server) {
        this.server = server;
    }

    @OnConnect
    public void onConnect(SocketIOClient client) {
        try {
            String user = client.getHandshakeData().getSingleUrlParam("userid");
            String orgi = client.getHandshakeData().getSingleUrlParam("orgi");
//			String session = client.getHandshakeData().getSingleUrlParam("session") ;
            String appid = client.getHandshakeData().getSingleUrlParam("appid");
            String aiid = client.getHandshakeData().getSingleUrlParam("aiid");
//			String agent = client.getHandshakeData().getSingleUrlParam("agent") ;
//			String skill = client.getHandshakeData().getSingleUrlParam("skill") ;
            logger.info("[chatbot socket.io] onConnect user {}, orgi {}, appid {}, aiid {}", user, orgi, appid, aiid);

            if (StringUtils.isNotBlank(user)) {
//				/**
//				 * 加入到 缓存列表
//				 */
                NettyClients.getInstance().putIMEventClient(user, client);
                MessageOutContent outMessage = new MessageOutContent();
                CousultInvite invite = OnlineUserUtils.cousult(appid, orgi, UKDataContext.getContext().getBean(ConsultInviteRepository.class));
                if (invite != null && !StringUtils.isBlank(invite.getAisuccesstip())) {
                    outMessage.setMessage(invite.getAisuccesstip());
                } else {
                    outMessage.setMessage("欢迎使用华夏春松机器人客服！");
                }

                outMessage.setMessageType(UKDataContext.MessageTypeEnum.MESSAGE.toString());
                outMessage.setCalltype(UKDataContext.CallTypeEnum.IN.toString());
                outMessage.setNickName("AI");
                outMessage.setCreatetime(UKTools.dateFormate.format(new Date()));

                client.sendEvent(UKDataContext.MessageTypeEnum.STATUS.toString(), outMessage);

                InetSocketAddress address = (InetSocketAddress) client.getRemoteAddress();
                String ip = UKTools.getIpAddr(client.getHandshakeData().getHttpHeaders(), address.getHostString());
                AiUser aiUser = new AiUser(user, user, System.currentTimeMillis(), orgi, IPTools.getInstance().findGeography(ip));
                aiUser.setSessionid(UKTools.getContextID(client.getSessionId().toString()));
                aiUser.setAppid(appid);
                aiUser.setAiid(aiid);
                aiUser.setUsername(UKDataContext.GUEST_USER + "_" + UKTools.genIDByKey(aiUser.getId()));
                aiUser.setChannel(UKDataContext.ChannelTypeEnum.WEBIM.toString());

                AgentService agentService = ServiceQuene.processAiService(aiUser, orgi);
                aiUser.setAgentserviceid(agentService.getId());

                CacheHelper.getOnlineUserCacheBean().put(user, aiUser, UKDataContext.SYSTEM_ORGI);

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
        if (!StringUtils.isBlank(user)) {
            NettyClients.getInstance().removeIMEventClient(user, UKTools.getContextID(client.getSessionId().toString()));
            AiUser aiUser = (AiUser) CacheHelper.getOnlineUserCacheBean().getCacheObject(user, orgi);
            if (aiUser != null) {
                ServiceQuene.processAiService(aiUser, orgi);
                CacheHelper.getOnlineUserCacheBean().delete(user, UKDataContext.SYSTEM_ORGI);
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
        if (data.getType() == null) {
            data.setType("message");
        }
        /**
         * 以下代码主要用于检查 访客端的字数限制
         */
        CousultInvite invite = OnlineUserUtils.cousult(data.getAppid(), data.getOrgi(), UKDataContext.getContext().getBean(ConsultInviteRepository.class));
        if (invite != null && invite.getMaxwordsnum() > 0) {
            if (!StringUtils.isBlank(data.getMessage()) && data.getMessage().length() > invite.getMaxwordsnum()) {
                data.setMessage(data.getMessage().substring(0, invite.getMaxwordsnum()));
            }
        } else if (!StringUtils.isBlank(data.getMessage()) && data.getMessage().length() > 300) {
            data.setMessage(data.getMessage().substring(0, 300));
        }
        data.setSessionid(UKTools.getContextID(client.getSessionId().toString()));
        /**
         * 处理表情
         */
        data.setMessage(UKTools.processEmoti(data.getMessage()));
        data.setTousername(UKDataContext.ChannelTypeEnum.AI.toString());

        data.setAiid(aiid);

        Object cacheData = (AiUser) CacheHelper.getOnlineUserCacheBean().getCacheObject(user, orgi);
        if (cacheData != null && cacheData instanceof AiUser) {
            AiUser aiUser = (AiUser) cacheData;
            data.setAgentserviceid(aiUser.getAgentserviceid());
            data.setChannel(aiUser.getChannel());
            /**
             * 一定要设置 ContextID
             */
            data.setContextid(aiUser.getAgentserviceid());
        }
        MessageOutContent outMessage = MessageUtils.createAiMessage(data, data.getAppid(), data.getChannel(), UKDataContext.CallTypeEnum.IN.toString(), UKDataContext.AiItemType.USERINPUT.toString(), UKDataContext.MediaTypeEnum.TEXT.toString(), data.getUserid());
        if (!StringUtils.isBlank(data.getUserid()) && UKDataContext.MessageTypeEnum.MESSAGE.toString().equals(data.getType())) {
            if (!StringUtils.isBlank(data.getTouser())) {
                OutMessageRouter router = null;
                router = (OutMessageRouter) UKDataContext.getContext().getBean(data.getChannel());
                if (router != null) {
                    router.handler(data.getTouser(), UKDataContext.MessageTypeEnum.MESSAGE.toString(), data.getAppid(), outMessage);
                }
            }
            if (cacheData != null && cacheData instanceof AiUser) {
                AiUser aiUser = (AiUser) cacheData;
                aiUser.setTime(System.currentTimeMillis());
                CacheHelper.getOnlineUserCacheBean().put(user, aiUser, UKDataContext.SYSTEM_ORGI);
            }
        }
    }
}  