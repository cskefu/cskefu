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

package com.chatopera.cc.proxy;

import com.chatopera.cc.activemq.BrokerPublisher;
import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.exception.CSKefuCacheException;
import com.chatopera.cc.model.AgentUser;
import com.chatopera.cc.model.AgentUserAudit;
import com.chatopera.cc.util.SerializeUtil;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 会话监控常用方法
 */
@Component
public class AgentAuditProxy {

    private final static Logger logger = LoggerFactory.getLogger(AgentAuditProxy.class);

    @Autowired
    private BrokerPublisher brokerPublisher;

    @Autowired
    private Cache cache;

    @Autowired
    private AgentUserProxy agentUserProxy;

    /**
     * 更新agentuser 监控人员列表
     *
     * @param agentUser
     */
    public AgentUserAudit updateAgentUserAudits(final AgentUser agentUser) {
        try {
            // get interests
            HashMap<String, String> subscribers = agentUserProxy.getAgentUserSubscribers(
                    agentUser.getOrgi(), agentUser);
            AgentUserAudit audit = new AgentUserAudit(agentUser.getOrgi(), agentUser.getId(), subscribers);
            cache.putAgentUserAuditByOrgi(agentUser.getOrgi(), audit);
            return audit;
        } catch (CSKefuCacheException e) {
            logger.error("[updateAgentUserAudits] exception", e);
        }
        return null;
    }

    /**
     * 使用ActiveMQ，异步且支持分布式
     *
     * @param agentUser
     * @param data
     * @param event
     */
    public void publishMessage(final AgentUser agentUser, Serializable data, final MainContext.MessageType event) {
        JsonObject json = new JsonObject();
        json.addProperty("orgi", agentUser.getOrgi());
        json.addProperty("data", SerializeUtil.serialize(data));
        json.addProperty("agentUserId", agentUser.getId());
        json.addProperty("event", event.toString());
        // 发送或者接收的对应的坐席的ID
        json.addProperty("agentno", agentUser.getAgentno());
        brokerPublisher.send(
                Constants.AUDIT_AGENT_MESSAGE, json.toString(), true);
    }
}
