/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
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
package com.cskefu.cc.socketio.client;

import com.cskefu.cc.activemq.AgentSubscription;
import com.cskefu.cc.activemq.OnlineUserSubscription;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.util.SerializeUtil;
import com.corundumstudio.socketio.SocketIOClient;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public NettyIMClient getIMClients() {
        return imClients;
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

    public int removeIMEventClient(String id, String sessionid) {
        return imClients.removeClient(id, sessionid);
    }


    /**
     * 向坐席访客发送消息，不使用多机通道
     *
     * @param id
     * @param event
     * @param data
     */
    public void publishIMEventMessage(final String id, final String event, Object data) {
        sendIMEventMessage(id, event, data);
    }

    /**
     * 向坐席访客发送消息
     *
     * @param id
     * @param event
     * @param data
     */
    public void publishIMEventMessage(final String id, final String event, Serializable data, boolean distribute) {
        // 检测client是否在这台机器上
        if ((!sendIMEventMessage(id, event, data)) && distribute) {
            JsonObject payload = new JsonObject();
            payload.addProperty("event", event);
            payload.addProperty("id", id);
            payload.addProperty("data", SerializeUtil.serialize(data));
            MainContext.getContext().getBean(OnlineUserSubscription.class).publish(payload);
        }
    }

    private boolean sendIMEventMessage(final String id, final String event, Object data) {
        List<SocketIOClient> userClients = imClients.getClients(id);
        for (SocketIOClient userClient : userClients) {
            userClient.sendEvent(event, data);
        }
        return userClients.size() > 0;
    }

    /**
     * 坐席连接
     */
    public void putAgentEventClient(String id, SocketIOClient agentClient) {
        agentClients.putClient(id, agentClient);
    }

    public int removeAgentEventClient(final String id, final String sessionid, final String connectid) {
        List<SocketIOClient> keyClients = agentClients.getClients(id);
        logger.debug(
                "[removeAgentEventClient] userId {}, sessionId {}, client size {}, connectid {}", id, sessionid,
                keyClients.size(), connectid);

        for (final SocketIOClient client : keyClients) {
            if (StringUtils.equals(client.get("connectid"), connectid)) {
                keyClients.remove(client);
                logger.info("[removeAgentEventClient] socketClient userid {} connectid {} is removed.", id, connectid);
                break;
            }
        }

        if (keyClients.size() == 0) {
            logger.info(
                    "[removeAgentEventClient] 0 clients for userId {} after remove, remove all keys from NettyClientMap",
                    id);
            agentClients.removeAll(id);
        } else {
            //  以下代码打印剩余的SocketIO的连接的信息
            StringBuffer sb = new StringBuffer();
            for (SocketIOClient client : keyClients) {
                sb.append(MainUtils.getContextID(client.getSessionId().toString()));
                sb.append(", ");
            }

            logger.info(
                    "[removeAgentEventClient] still get userId {} remaining clients[{}]: {}", id, keyClients.size(),
                    sb.toString());
        }
        return keyClients.size();
    }

    /**
     * SSO
     * 发布浏览器登出消息，保证单点登录
     *
     * @param id
     * @param session 当前有效的session信息，不发送leave事件
     */
    public void publishLeaveEventMessage(final String id, final String session) {
        final List<SocketIOClient> agents = agentClients.getClients(id);
        logger.info("publishLeaveEventMessage:  agentno {}, agentSize {}", id, agents.size());
        for (final SocketIOClient agentClient : agents) {
            if (!StringUtils.equals(agentClient.get("session"), session)) {
                agentClient.sendEvent(MainContext.MessageType.LEAVE.toString());
            }
        }
    }


    /**
     * 发布消息到会话监控
     *
     * @param id
     * @param event
     * @param data
     */
    public void publishAuditEventMessage(final String id, final String event, final Serializable data) {
        sendAgentEventMessage(id, event, data);
    }

    /**
     * 发布消息到坐席
     *
     * @param id
     * @param event
     * @param data
     * @param distribute 是否分布式的发布消息
     */
    public void publishAgentEventMessage(final String id, final String event, final Serializable data, boolean distribute) {
        // 检测client是否在这台机器上
        if (!sendAgentEventMessage(id, event, data)) {
            if (distribute) {
                JsonObject payload = new JsonObject();
                payload.addProperty("event", event);
                payload.addProperty("id", id);
                payload.addProperty("data", SerializeUtil.serialize(data));
                MainContext.getContext().getBean(AgentSubscription.class).publish(payload);
            }
        }
    }

    // 向坐席发送消息
    public boolean sendAgentEventMessage(final String id, final String event, final Object data) {
        final List<SocketIOClient> agents = agentClients.getClients(id);
        logger.info("sendAgentEventMessage:  event {}, agentno {}, agentSize {}", event, id, agents.size());

        for (final SocketIOClient agentClient : agents) {
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

    public int removeEntIMEventClient(String id, String sessionid) {
        return entIMClients.removeClient(id, sessionid);
    }

    public void sendEntIMEventMessage(String id, String event, Object data) {
        List<SocketIOClient> entims = entIMClients.getClients(id);
        entims.stream().forEach(c -> c.sendEvent(event, data));
    }

    public void sendEntIMGroupEventMessage(String id, String group, String event, Object data) {
        List<SocketIOClient> entims = entIMClients.getClients(id);
        entims.stream().findAny().ifPresent(c -> c.getNamespace().getRoomOperations(group).sendEvent(event, data));
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

    public int removeCalloutEventClient(String id, String sessionId) {
        return calloutClients.removeClient(id, sessionId);
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

    public int removeChatbotEventClient(String id, String sessionId) {
        return chatbotClients.removeClient(id, sessionId);
    }

    public void sendChatbotEventMessage(String id, String event, Object data) {
        List<SocketIOClient> _clients = chatbotClients.getClients(id);
        logger.info("sendChatbotEventMessage get clients size {}", _clients.size());
        for (SocketIOClient c : _clients) {
            c.sendEvent(event, data);
        }
    }

}
