package com.chatopera.cc.plugins.chatbot;

import com.chatopera.cc.socketio.message.ChatMessage;
import com.chatopera.compose4j.AbstractContext;

public class ChatbotContext extends AbstractContext {
    private ChatMessage resp;

    public ChatMessage getResp() {
        return resp;
    }

    public void setResp(ChatMessage resp) {
        this.resp = resp;
    }
}
