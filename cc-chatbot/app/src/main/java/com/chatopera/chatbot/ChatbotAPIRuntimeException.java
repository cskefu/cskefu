package com.chatopera.chatbot;

import com.mashape.unirest.http.exceptions.UnirestException;

public class ChatbotAPIRuntimeException extends Exception{
    public ChatbotAPIRuntimeException(String msg) {
        super(msg);
    }
}
