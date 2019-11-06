/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.socketio;

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.plugins.PluginRegistry;
import com.chatopera.cc.basic.plugins.PluginsLoader;
import com.chatopera.cc.config.plugins.CalloutPluginPresentCondition;
import com.chatopera.cc.config.plugins.ChatbotPluginPresentCondition;
import com.chatopera.cc.socketio.handler.AgentEventHandler;
import com.chatopera.cc.socketio.handler.EntIMEventHandler;
import com.chatopera.cc.socketio.handler.IMEventHandler;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Component
public class ServerRunner implements CommandLineRunner {
    private final static Logger logger = LoggerFactory.getLogger(ServerRunner.class);

    private final SocketIOServer server;
    private final SocketIONamespace imSocketNameSpace;
    private final SocketIONamespace agentSocketIONameSpace;
    private final SocketIONamespace entIMSocketIONameSpace;
    private final SocketIONamespace chatbotSocketIONameSpace;
    private final SocketIONamespace callCenterSocketIONameSpace;
    private final SocketIONamespace calloutSocketIONameSpace;

    @Autowired
    public ServerRunner(SocketIOServer server) {
        this.server = server;
        imSocketNameSpace = server.addNamespace(MainContext.NameSpaceEnum.IM.getNamespace());
        agentSocketIONameSpace = server.addNamespace(MainContext.NameSpaceEnum.AGENT.getNamespace());
        entIMSocketIONameSpace = server.addNamespace(MainContext.NameSpaceEnum.ENTIM.getNamespace());

        if (MainContext.hasModule(Constants.CSKEFU_MODULE_CALLOUT)) {
            calloutSocketIONameSpace = server.addNamespace(MainContext.NameSpaceEnum.CALLOUT.getNamespace());
        } else {
            calloutSocketIONameSpace = null;
        }

        if (MainContext.hasModule(Constants.CSKEFU_MODULE_CALLCENTER)) {
            callCenterSocketIONameSpace = server.addNamespace(MainContext.NameSpaceEnum.CALLCENTER.getNamespace());
        } else {
            callCenterSocketIONameSpace = null;
        }

        if (MainContext.hasModule(Constants.CSKEFU_MODULE_CHATBOT)) {
            chatbotSocketIONameSpace = server.addNamespace(MainContext.NameSpaceEnum.CHATBOT.getNamespace());
        } else {
            chatbotSocketIONameSpace = null;
        }
    }

    @Bean(name = "imNamespace")
    public SocketIONamespace getIMSocketIONameSpace(SocketIOServer server) {
        imSocketNameSpace.addListeners(new IMEventHandler(server));
        return imSocketNameSpace;
    }

    @Bean(name = "agentNamespace")
    public SocketIONamespace getAgentSocketIONameSpace(SocketIOServer server) {
        agentSocketIONameSpace.addListeners(new AgentEventHandler(server));
        return agentSocketIONameSpace;
    }

    @Bean(name = "entimNamespace")
    public SocketIONamespace getEntIMSocketIONameSpace(SocketIOServer server) {
        entIMSocketIONameSpace.addListeners(new EntIMEventHandler(server));
        return entIMSocketIONameSpace;
    }

    @Bean(name = "chatbotNamespace")
    @Conditional(ChatbotPluginPresentCondition.class)
    public SocketIONamespace getChatbotSocketIONameSpace(SocketIOServer server) {
        Constructor<?> constructor;
        try {
            constructor = Class.forName(
                    PluginsLoader.getIOEventHandler(PluginRegistry.PLUGIN_ENTRY_CHATBOT)).getConstructor(
                    SocketIOServer.class);
            chatbotSocketIONameSpace.addListeners(constructor.newInstance(server));
        } catch (NoSuchMethodException | SecurityException
                | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return chatbotSocketIONameSpace;

    }

    @Bean(name = "callCenterNamespace")
    public SocketIONamespace getCallCenterIMSocketIONameSpace(SocketIOServer server) {
        if (MainContext.hasModule(Constants.CSKEFU_MODULE_CALLCENTER)) {
            Constructor<?> constructor;
            try {
                constructor = Class.forName(
                        "com.chatopera.cc.socketio.server.handler.CallCenterEventHandler").getConstructor(
                        SocketIOServer.class);
                callCenterSocketIONameSpace.addListeners(constructor.newInstance(server));
            } catch (NoSuchMethodException | SecurityException
                    | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return callCenterSocketIONameSpace;
    }

    @Conditional(CalloutPluginPresentCondition.class)
    @Bean(name = "calloutNamespace")
    public SocketIONamespace getCalloutIMSocketIONameSpace(SocketIOServer server) {
        if (MainContext.hasModule(Constants.CSKEFU_MODULE_CALLOUT)) {
            Constructor<?> constructor;
            try {
                constructor = Class.forName(
                        PluginsLoader.getIOEventHandler(PluginRegistry.PLUGIN_ENTRY_CALLOUT)).getConstructor(
                        SocketIOServer.class);
                calloutSocketIONameSpace.addListeners(constructor.newInstance(server));
            } catch (NoSuchMethodException | SecurityException
                    | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.error("[calloutNamespace] error", e);
            }
        }
        return calloutSocketIONameSpace;
    }

    @Override
    public void run(String... args) throws Exception {
        server.start();
        MainContext.setIMServerStatus(true);    // IMServer 启动成功
    }
}  
