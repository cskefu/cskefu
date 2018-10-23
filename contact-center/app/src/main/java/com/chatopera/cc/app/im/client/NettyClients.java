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
package com.chatopera.cc.app.im.client;

import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.app.basic.MainUtils;
import com.chatopera.cc.app.im.util.IMServiceUtils;
import com.chatopera.cc.app.schedule.WebIMAgentDispatcher;
import com.chatopera.cc.app.schedule.WebIMOnlineUserDispatcher;
import com.corundumstudio.socketio.SocketIOClient;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;


public class NettyClients {

    private final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private static NettyClients clients = new NettyClients();

    private NettyIMClient imClients = new NettyIMClient();
    private NettyAgentClient agentClients = new NettyAgentClient();
    private NettyIMClient entIMClients = new NettyIMClient();
    private NettyCallCenterClient callCenterClients = new NettyCallCenterClient();
    private NettyCalloutClient calloutClients = new NettyCalloutClient();
    private NettyChatbotClient chatbotClients = new NettyChatbotClient();

    public int size() {
        return imClients.size();
    }

    public static NettyClients getInstance() {
        return clients;
    }

    public NettyCallCenterClient getCallCenterClients() {
        return this.callCenterClients;
    }

    /**
     * 访客连接
     */
    public void setImClients(NettyIMClient imClients) {
        this.imClients = imClients;
    }

    public void putIMEventClient(String id, SocketIOClient userClient) {
        imClients.putClient(id, userClient);
    }

    public void closeIMEventClient(String id, String sessionid, String orgi) {
        List<SocketIOClient> userClients = imClients.getClients(id);
        for (SocketIOClient userClient : userClients) {
            if (MainUtils.getContextID(userClient.getSessionId().toString()).equals(sessionid)) {
                userClient.disconnect();
            }
        }
    }

    public void removeIMEventClient(String id, String sessionid) {
        imClients.removeClient(id, sessionid);
    }

    public void publishIMEventMessage(final String id, final String event, Serializable data) {
        // 检测client是否在这台机器上
        if (!sendIMEventMessage(id, event, data)) {
            try {
                JsonObject payload = new JsonObject();
                payload.addProperty("event", event);
                payload.addProperty("id", id);
                payload.addProperty("data", IMServiceUtils.serialize(data));
                MainContext.getContext().getBean(WebIMOnlineUserDispatcher.class).publish(payload);
            } catch (IOException e) {
                logger.error("publishIMEventMessage", e);
            }
        }
    }

    public boolean sendIMEventMessage(final String id, final String event, Object data) {
        List<SocketIOClient> userClients = imClients.getClients(id);
        for (SocketIOClient userClient : userClients) {
            userClient.sendEvent(event, data);
        }
        return userClients.size() > 0;
    }

    /**
     * 坐席连接
     */
    public void setAgentClients(NettyAgentClient agentClients) {
        this.agentClients = agentClients;
    }

    public void putAgentEventClient(String id, SocketIOClient agentClient) {
        agentClients.putClient(id, agentClient);
    }

    public void removeAgentEventClient(String id, String sessionid) {
        agentClients.removeClient(id, sessionid);
    }

    // publish to Redis
    public void publishAgentEventMessage(String id, String event, Serializable data) {
        // 检测client是否在这台机器上
        if (!sendAgentEventMessage(id, event, data)) {
            try {
                JsonObject payload = new JsonObject();
                payload.addProperty("event", event);
                payload.addProperty("id", id);
                payload.addProperty("data", IMServiceUtils.serialize(data));
                MainContext.getContext().getBean(WebIMAgentDispatcher.class).publish(payload);
            } catch (IOException e) {
                logger.error("publishAgentEventMessage", e);
            }
        }
    }

    // 向坐席发送消息
    public boolean sendAgentEventMessage(String id, String event, Object data) {
        List<SocketIOClient> agents = agentClients.getClients(id);
        for (SocketIOClient agentClient : agents) {
            agentClient.sendEvent(event, data);
        }

        return agents.size() > 0;
    }

    /**
     * 企业聊天
     */
    public void setEntImClients(NettyIMClient entIMClients) {
        this.entIMClients = entIMClients;
    }

    public void putEntIMEventClient(String id, SocketIOClient userClient) {
        entIMClients.putClient(id, userClient);
    }

    public void removeEntIMEventClient(String id, String sessionid) {
        entIMClients.removeClient(id, sessionid);
    }

    public void sendEntIMEventMessage(String id, String event, Object data) {
        List<SocketIOClient> entims = entIMClients.getClients(id);
        for (SocketIOClient userClient : entims) {
            userClient.sendEvent(event, data);
        }
    }

    public int getEntIMClientsNum(String user) {
        return entIMClients.getClients(user) != null ? entIMClients.getClients(user).size() : 0;
    }

    public void sendCallCenterMessage(String id, String event, Object data) {
        List<SocketIOClient> ccClients = callCenterClients.getClients(id);
        for (SocketIOClient ccClient : ccClients) {
            ccClient.sendEvent(event, data);
        }
    }

    /**
     * Callout Event Server Methods.
     */
    public void putCalloutEventClient(String id, SocketIOClient client) {
        calloutClients.putClient(id, client);
    }

    public void removeCalloutEventClient(String id, String sessionId) {
        calloutClients.removeClient(id, sessionId);
    }

    public void sendCalloutEventMessage(String id, String event, Object data) {
        List<SocketIOClient> _clients = calloutClients.getClients(id);
        logger.info("sendCalloutEventMessage get clients size {}", _clients.size());
        for (SocketIOClient c : _clients) {
            c.sendEvent(event, data);
        }
    }


    /**
     * Chatbot Event Server Methods.
     */
    public void putChatbotEventClient(String id, SocketIOClient client) {
        chatbotClients.putClient(id, client);
    }

    public void removeChatbotEventClient(String id, String sessionId) {
        chatbotClients.removeClient(id, sessionId);
    }

    public void sendChatbotEventMessage(String id, String event, Object data) {
        List<SocketIOClient> _clients = chatbotClients.getClients(id);
        logger.info("sendChatbotEventMessage get clients size {}", _clients.size());
        for (SocketIOClient c : _clients) {
            c.sendEvent(event, data);
        }
    }

}
