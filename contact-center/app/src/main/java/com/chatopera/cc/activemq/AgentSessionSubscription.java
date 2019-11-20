/*
 * Copyright (C) 2019 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.activemq;

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.socketio.client.NettyClients;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class AgentSessionSubscription {
    private final static Logger logger = LoggerFactory.getLogger(AgentSessionSubscription.class);

    /**
     * 接收坐席会话监控消息
     *
     * @param msg
     */
    @JmsListener(destination = Constants.MQ_TOPIC_WEB_SESSION_SSO, containerFactory = "jmsListenerContainerTopic")
    public void onMessage(final String msg) {
        logger.info("[onMessage] payload {}", msg);
        try {
            final JsonObject json = new JsonParser().parse(msg).getAsJsonObject();
            // 把登出消息通知给浏览器
            NettyClients.getInstance().publishLeaveEventMessage(
                    json.get("agentno").getAsString(),
                    json.get("expired").getAsString());
        } catch (Exception e) {
            logger.warn("[onMessage] error", e);
        }
    }


}
