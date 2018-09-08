package com.chatopera.chatbot;

import com.mashape.unirest.http.exceptions.UnirestException;

public class ChatbotRuntimeException extends Exception{
    public ChatbotRuntimeException(String msg) {
        super(msg);
    }
}
