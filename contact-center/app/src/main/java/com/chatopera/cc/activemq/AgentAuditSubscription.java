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
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.exception.CSKefuException;
import com.chatopera.cc.model.AgentUser;
import com.chatopera.cc.model.AgentUserAudit;
import com.chatopera.cc.persistence.repository.AgentUserRepository;
import com.chatopera.cc.proxy.AgentAuditProxy;
import com.chatopera.cc.socketio.client.NettyClients;
import com.chatopera.cc.util.SerializeUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * 会话监控
 */
@Component
public class AgentAuditSubscription {
    private final static Logger logger = LoggerFactory.getLogger(AgentAuditSubscription.class);

    @Autowired
    private Cache cache;

    @Autowired
    private AgentAuditProxy agentAuditProxy;

    @Autowired
    private AgentUserRepository agentUserRes;


    /**
     * 接收坐席会话监控消息
     *
     * @param msg
     */
    @JmsListener(destination = Constants.AUDIT_AGENT_MESSAGE, containerFactory = "jmsListenerContainerTopic")
    public void onMessage(final String msg) {
        logger.info("[onMessage] payload {}", msg);
        try {
            final JsonObject json = new JsonParser().parse(msg).getAsJsonObject();

            if (json.has("orgi") && json.has("data") &&
                    json.has("agentUserId") &&
                    json.has("event") && json.has("agentno")) {

                // 查找关联的会话监控信息
                final AgentUserAudit agentUserAudit = cache.findOneAgentUserAuditByOrgiAndId(
                        json.get("orgi").getAsString(),
                        json.get("agentUserId").getAsString()).orElseGet(() -> {
                    final AgentUser agentUser = agentUserRes.findOne(json.get("agentUserId").getAsString());
                    if (agentUser != null) {
                        return agentAuditProxy.updateAgentUserAudits(agentUser);
                    } else {
                        logger.warn(
                                "[onMessage] can not find agent user by id {}", json.get("agentUserId").getAsString());
                    }
                    return null;
                });

                if (agentUserAudit != null) {
                    final String agentno = json.get("agentno").getAsString();
                    logger.info(
                            "[onMessage] agentno {}, subscribers size {}, subscribers {}", agentno,
                            agentUserAudit.getSubscribers().size(),
                            StringUtils.join(agentUserAudit.getSubscribers().keySet(), "|"));

                    // 发送消息给坐席监控，不需要分布式，因为这条消息已经是从ActiveMQ使用Topic多机广播
                    for (final String subscriber : agentUserAudit.getSubscribers().keySet()) {
                        logger.info("[onMessage] process subscriber {}", subscriber);
                        if (!StringUtils.equals(subscriber, agentno)) {
                            logger.info("[onMessage] publish event to {}", subscriber);
                            NettyClients.getInstance().publishAuditEventMessage(
                                    subscriber,
                                    json.get("event").getAsString(),
                                    SerializeUtil.deserialize(json.get("data").getAsString()));
                        }
                    }
                } else {
                    logger.warn(
                            "[onMessage] can not resolve agent user audit object for agent user id {}",
                            json.get("agentUserId").getAsString());
                }
            } else {
                throw new CSKefuException("Invalid payload.");
            }
        } catch (Exception e) {
            logger.error("[onMessage] error", e);
        }
    }
}
