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

import com.alibaba.fastjson.JSONObject;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class BrokerPublisher {

    final static private Logger logger = LoggerFactory.getLogger(BrokerPublisher.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @PostConstruct
    public void setup() {
        logger.info("[ActiveMQ Publisher] setup successfully.");
    }


    /**
     * 时延消息
     *
     * @param destination
     * @param payload
     * @param delay       available by delayed seconds
     */
    public void send(final String destination, final String payload, final boolean isTopic, final int delay) {
        try {
            if (isTopic) {
                jmsTemplate.convertAndSend(new ActiveMQTopic(destination), payload, m -> {
                    m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 1000 * delay);
                    return m;
                });
            } else {
                // 默认为Queue
                jmsTemplate.convertAndSend(destination, payload, m -> {
                    m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 1000 * delay);
                    return m;
                });
            }
            logger.debug("[send] send succ, dest {}, payload {}", destination, payload);
        } catch (Exception e) {
            logger.warn("[send] error happens.", e);
        }
    }

    /**
     * @param destination
     * @param payload
     * @param isTopic
     */
    public void send(final String destination, final String payload, boolean isTopic) {
        try {
            if (isTopic) {
                jmsTemplate.convertAndSend(new ActiveMQTopic(destination), payload);
            } else {
                // 默认为Queue
                jmsTemplate.convertAndSend(destination, payload);
            }
            logger.debug("[send] send succ, dest {}, payload {}", destination, payload);
        } catch (Exception e) {
            logger.warn("[send] error happens.", e);
        }
    }

    public void send(final String destination, final String payload) {
        send(destination, payload, false);
    }

    public void send(final String destination, final JSONObject payload) {
        send(destination, payload.toJSONString());
    }

    public void send(final String destination, final org.json.JSONObject payload) {
        send(destination, payload.toString());
    }

    public void send(final String destination, final Map<String, String> payload) {
        JSONObject obj = new JSONObject();

        for (Map.Entry<String, String> entry : payload.entrySet()) {
            obj.put(entry.getKey(), entry.getValue());
        }

        send(destination, obj.toJSONString());
    }
}