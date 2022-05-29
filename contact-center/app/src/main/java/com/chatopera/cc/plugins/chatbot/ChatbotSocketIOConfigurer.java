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

import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.socketio.SocketIOServing;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class ChatbotSocketIOConfigurer {
    private final static Logger logger = LoggerFactory.getLogger(ChatbotSocketIOConfigurer.class);
    private SocketIONamespace socketIONameSpace;

    @Autowired
    private SocketIOServing socketIOServing;

    @PostConstruct
    public void setup() {
        socketIONameSpace = socketIOServing.getServer().addNamespace(MainContext.NameSpaceEnum.CHATBOT.getNamespace());
    }

    @Bean(name = "chatbotNamespace")
    public SocketIONamespace getSocketIONameSpace(SocketIOServer server) {
        socketIONameSpace.addListeners(new ChatbotEventHandler(server));
        return socketIONameSpace;
    }
}
