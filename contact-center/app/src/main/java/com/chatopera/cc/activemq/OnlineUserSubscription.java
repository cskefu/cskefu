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

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.socketio.client.NettyClients;
import com.chatopera.cc.util.SerializeUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * IM OnlineUser
 */
@Component
public class OnlineUserSubscription {

    private final static Logger logger = LoggerFactory.getLogger(OnlineUserSubscription.class);

    @Value("${application.node.id}")
    private String appNodeId;


    @Autowired
    private BrokerPublisher brokerPublisher;

    @PostConstruct
    public void setup() {
        logger.info("ActiveMQ Subscription is setup successfully.");
    }

    /**
     * Publish Message into ActiveMQ
     *
     * @param j
     */
    public void publish(final JsonObject j) {
        j.addProperty("node", appNodeId);
        brokerPublisher.send(Constants.INSTANT_MESSAGING_MQ_TOPIC_ONLINEUSER, j.toString(), true);

    }

    @JmsListener(destination = Constants.INSTANT_MESSAGING_MQ_TOPIC_ONLINEUSER, containerFactory = "jmsListenerContainerTopic")
    public void onMessage(final String payload){
        logger.info("[onMessage] payload {}", payload);
        JsonParser parser = new JsonParser();
        JsonObject j = parser.parse(payload).getAsJsonObject();
        logger.debug("[instant messaging] message body {}", j.toString());
        try {
            NettyClients.getInstance().publishIMEventMessage(j.get("id").getAsString(),
                    j.get("event").getAsString(),
                    SerializeUtil.deserialize(j.get("data").getAsString()));
        } catch (Exception e) {
            logger.error("onMessage", e);
        }
    }
}
