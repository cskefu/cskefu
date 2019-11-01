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
package com.chatopera.cc.app.config;

import com.chatopera.cc.app.schedule.CallOutWireTask;
import com.chatopera.cc.app.schedule.WebIMAgentDispatcher;
import com.chatopera.cc.app.schedule.WebIMOnlineUserDispatcher;
import com.chatopera.cc.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.util.concurrent.Executors;

@Configuration
public class RedisConfigure {


    @Autowired
    JedisConnectionFactory jedisConnectionFactory;


    @Autowired
    CallOutWireTask callOutWireTask;

    @Autowired
    WebIMAgentDispatcher webIMAgentDispatcher;

    @Autowired
    WebIMOnlineUserDispatcher webIMOnlineUserDispatcher;

    @Bean
    RedisMessageListenerContainer redisContainer() {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory);
        container.addMessageListener(pbxMessageListener(), pbxEvents());
        container.addMessageListener(imAgentDispatchListener(), imAgentEvents());
        container.addMessageListener(imOnlineUserDispatchListener(), imOnlineUserEvents());
        container.setTaskExecutor(Executors.newFixedThreadPool(50));
        return container;
    }

    @Bean
    MessageListenerAdapter pbxMessageListener() {
        return new MessageListenerAdapter(callOutWireTask);
    }

    @Bean
    PatternTopic pbxEvents() {
        return new PatternTopic(Constants.FS_CHANNEL_FS_TO_CC);
    }

    @Bean
    MessageListenerAdapter imAgentDispatchListener() {
        return new MessageListenerAdapter(webIMAgentDispatcher);
    }

    @Bean
    PatternTopic imAgentEvents() {
        return new PatternTopic(Constants.INSTANT_MESSAGING_WEBIM_AGENT_CHANNEL);
    }

    @Bean
    MessageListenerAdapter imOnlineUserDispatchListener(){
        return new MessageListenerAdapter(webIMOnlineUserDispatcher);
    }

    @Bean
    PatternTopic imOnlineUserEvents() {
        return new PatternTopic(Constants.INSTANT_MESSAGING_WEBIM_ONLINE_USER_CHANNEL);
    }


}
