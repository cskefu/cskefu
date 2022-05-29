/*
 * Copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.plugins.chatbot;

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.plugins.PluginRegistry;
import com.chatopera.cc.socketio.message.ChatMessage;
import com.chatopera.compose4j.Composer;
import com.chatopera.compose4j.Middleware;
import com.chatopera.compose4j.exception.Compose4jRuntimeException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

@Component
public class ChatbotComposer implements ApplicationContextAware {
    private final static Logger logger = LoggerFactory.getLogger(
            ChatbotComposer.class);

    private static ApplicationContext applicationContext;

    private Composer<ChatbotContext> composer;

    @Autowired
    private PluginRegistry pluginRegistry;

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        applicationContext = ac;
    }

    @PostConstruct
    public void postConstruct() {
        applicationContext.getBeansWithAnnotation(Configuration.class);
        composer = new Composer<>();

        pluginRegistry.getPlugin(Constants.CSKEFU_MODULE_MESSENGER).ifPresent(p -> {
            composer.use((Middleware) applicationContext.getBean(
                    p.getPluginId() + PluginRegistry.PLUGIN_CHATBOT_MESSAGER_SUFFIX));
        });

    }

    public void handle(ChatMessage resp) {
        ChatbotContext ctx = new ChatbotContext();
        ctx.setResp(resp);

        try {
            composer.handle(ctx);
        } catch (Compose4jRuntimeException e) {
            logger.info("[chatbot send] error", e);
        }
    }
}
