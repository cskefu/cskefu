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

package com.chatopera.cc.app.schedule;

import com.chatopera.cc.app.im.client.NettyClients;
import com.chatopera.cc.app.im.util.IMServiceUtils;
import com.chatopera.cc.util.Constants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Base64;

/**
 * 坐席消息分发
 */
@Component
public class WebIMAgentDispatcher implements MessageListener {
    private final static Logger logger = LoggerFactory.getLogger(WebIMAgentDispatcher.class);

    private ListOperations<String, String> redisListOps;
    private HashOperations<String, String, String> redisHashOps;

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private String redisPort;

    @Value("${spring.redis.database}")
    private String redisDB;

    @Value("${application.node.id}")
    private String appNodeId;


    /**
     * 使用StringRedisTemplate而不是RedisTemplate解决序列化问题
     * https://stackoverflow.com/questions/13215024/weird-redis-key-with-spring-data-jedis
     */
    @Autowired
    private StringRedisTemplate redis;

    @PostConstruct
    private void init() {
        redisListOps = redis.opsForList();
        redisHashOps = redis.opsForHash();
    }

    /**
     * Publish Message into Channel with redis PubSub
     *
     * @param j
     */
    public void publish(JsonObject j) {
        ChannelTopic ct = new ChannelTopic(String.format(Constants.INSTANT_MESSTRING_WEBIM_AGENT_PATTERN, appNodeId));
        j.addProperty("node", appNodeId);
        redis.convertAndSend(ct.getTopic(), j.toString());
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        logger.debug("[instant messaging] onMessage {}", message);
        String payload = new String(message.getBody());
        JsonParser parser = new JsonParser();
        JsonObject j = parser.parse(payload).getAsJsonObject();
        logger.debug("[instant messaging] message body {}", j.toString());
        try {
            NettyClients.getInstance().sendAgentEventMessage(j.get("id").getAsString(),
                    j.get("event").getAsString(),
                    IMServiceUtils.deserialize(j.get("data").getAsString()));
        } catch (Exception e) {
            logger.error("onMessage", e);
        }

    }

}
