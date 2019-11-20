/*
 * Copyright (C) 2019 Chatopera Inc, All rights reserved.
 * <https://www.chatopera.com>
 * This software and related documentation are provided under a license agreement containing
 * restrictions on use and disclosure and are protected by intellectual property laws.
 * Except as expressly permitted in your license agreement or allowed by law, you may not use,
 * copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform,
 * publish, or display any part, in any form, or by any means. Reverse engineering, disassembly,
 * or decompilation of this software, unless required by law for interoperability, is prohibited.
 */
package com.chatopera.cc.activemq;

import com.chatopera.cc.acd.ACDAgentDispatcher;
import com.chatopera.cc.acd.ACDWorkMonitor;
import com.chatopera.cc.acd.basic.ACDComposeContext;
import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.model.AgentStatus;
import com.chatopera.cc.persistence.repository.AgentStatusRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * 处理SocketIO的离线事件
 */
@Component
public class SocketioConnEventSubscription {

    private final static Logger logger = LoggerFactory.getLogger(SocketioConnEventSubscription.class);

    @Autowired
    private ACDAgentDispatcher acdAgentDispatcher;

    @Autowired
    private ACDWorkMonitor acdWorkMonitor;

    @Autowired
    private AgentStatusRepository agentStatusRes;

    @Autowired
    private Cache cache;

    @Value("${application.node.id}")
    private String appNodeId;

    @PostConstruct
    public void setup() {
        logger.info("ActiveMQ Subscription is setup successfully.");
    }

    @JmsListener(destination = Constants.WEBIM_SOCKETIO_AGENT_DISCONNECT, containerFactory = "jmsListenerContainerQueue")
    public void onMessage(final String payload) {
        logger.info("[onMessage] payload {}", payload);

        try {
            JsonParser parser = new JsonParser();
            JsonObject j = parser.parse(payload).getAsJsonObject();
            if (j.has("userId") && j.has("orgi") && j.has("isAdmin")) {
                final AgentStatus agentStatus = cache.findOneAgentStatusByAgentnoAndOrig(
                        j.get("userId").getAsString(),
                        j.get("orgi").getAsString());
                if (agentStatus != null && (!agentStatus.isConnected())) {
                    /**
                     * 处理该坐席为离线
                     */
                    // 重分配坐席
                    ACDComposeContext ctx = new ACDComposeContext();
                    ctx.setAgentno(agentStatus.getAgentno());
                    ctx.setOrgi(agentStatus.getOrgi());
                    acdAgentDispatcher.dequeue(ctx);
                    if (ctx.isResolved()) {
                        logger.info("[onMessage] re-allotAgent for user's visitors successfully.");
                    } else {
                        logger.info("[onMessage] re-allotAgent, error happens.");
                    }

                    // 更新数据库
                    agentStatus.setBusy(false);
                    agentStatus.setStatus(MainContext.AgentStatusEnum.OFFLINE.toString());
                    agentStatus.setUpdatetime(new Date());

                    // 设置该坐席状态为离线
                    cache.deleteAgentStatusByAgentnoAndOrgi(agentStatus.getAgentno(), agentStatus.getOrgi());
                    agentStatusRes.save(agentStatus);

                    // 记录坐席工作日志
                    acdWorkMonitor.recordAgentStatus(agentStatus.getAgentno(),
                                                     agentStatus.getUsername(),
                                                     agentStatus.getAgentno(),
                                                     j.get("isAdmin").getAsBoolean(),
                                                     agentStatus.getAgentno(),
                                                     agentStatus.getStatus(),
                                                     MainContext.AgentStatusEnum.OFFLINE.toString(),
                                                     MainContext.AgentWorkType.MEIDIACHAT.toString(),
                                                     agentStatus.getOrgi(), null);
                } else if (agentStatus == null) {
                    // 该坐席已经完成离线设置
                    logger.info("[onMessage] agent is already offline, skip any further operations");
                } else {
                    // 该坐席目前在线，忽略该延迟事件
                    logger.info("[onMessage] agent is online now, ignore this message.");
                }
            }
        } catch (Exception e) {
            logger.error("onMessage", e);
        }
    }
}
