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
