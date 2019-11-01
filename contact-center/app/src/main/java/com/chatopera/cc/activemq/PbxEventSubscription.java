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

import com.chatopera.cc.config.conditions.CalloutBeanCondition;
import com.chatopera.cc.persistence.interfaces.CalloutWireEvent;
import com.chatopera.cc.schedule.CalloutWireTask;
import com.chatopera.cc.basic.Constants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * FreeSWITCH
 */
@Component
@Conditional(CalloutBeanCondition.class)
public class PbxEventSubscription {

    private final static Logger logger = LoggerFactory.getLogger(PbxEventSubscription.class);

    @PostConstruct
    public void setup() {
        logger.info("ActiveMQ Subscription is setup successfully.");
    }

    @Autowired
    private BrokerPublisher brokerPublisher;

    @Autowired
    private CalloutWireTask callOutWireTask;

    public void publish(final String dest, final String payload) {
        brokerPublisher.send(dest, payload);
    }

    @JmsListener(destination = Constants.INSTANT_MESSAGING_MQ_QUEUE_PBX, containerFactory = "jmsListenerContainerQueue")
    public void onMessage(final String payload) {
        logger.info("[onMessage] payload {}", payload);
        JsonParser parser = new JsonParser();
        JsonObject j = parser.parse(payload).getAsJsonObject();
        // validate message
        if (!(j.has("type")
                && j.has("to")
                && j.has("ops")
                && j.has("channel")
                && j.has("createtime"))) {
            logger.error(String.format("[callout wire] 接线数据格式不对, %s", payload));
        } else {
            try {
                CalloutWireEvent event = CalloutWireEvent.parse(j);
                switch (event.getEventType()) {
                    case 1: // 自动外呼接通
                        logger.info("[callout wire] 自动外呼接通 {}", j.toString());
                        callOutWireTask.callOutConnect(event);
                        break;
                    case 2: // 自动外呼挂断
                        logger.info("[callout wire] 自动外呼挂断 {}", j.toString());
                        callOutWireTask.callOutDisconnect(event);
                        break;
                    case 3: // 自动外呼失败
                        logger.info("[callout wire] 自动外呼失败 {}", j.toString());
                        callOutWireTask.callOutFail(event);
                        break;
                    case 4: // 手动外呼接通
                        logger.info("[callout wire] 手动外呼接通 {}", j.toString());
                        callOutWireTask.callOutConnect(event);
                        break;
                    case 5: // 手动外呼挂断
                        logger.info("[callout wire] 手动外呼挂断 {}", j.toString());
                        callOutWireTask.callOutDisconnect(event);
                        break;
                    case 6: // 手动外呼失败
                        logger.info("[callout wire] 手动外呼失败 {}", j.toString());
                        callOutWireTask.callOutFail(event);
                        break;
                    case 7: // 呼入接通
                        logger.info("[callin wire] 呼入接通    {}", j.toString());
                        break;
                    case 8: // 呼入挂断
                        logger.info("[callin wire] 呼入挂断    {}", j.toString());
                        break;
                    case 9: // 呼入失败
                        logger.info("[callin wire] 呼入失败    {}", j.toString());
                        break;
                }
            } catch (Exception e) {
                logger.error("[callout wire] ", e);
            }
        }
    }
}
