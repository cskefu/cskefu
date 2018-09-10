package com.chatopera.cc.webim.service.repository;

import com.chatopera.cc.webim.web.model.Chatbot;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract interface ChatbotRepository extends JpaRepository<Chatbot, String> {

    public abstract boolean existsByChatbotIDAndOrgi(String chatbotID, String orgi);
    public abstract boolean existsBySnsAccountIdentifierAndOrgi(String snsid, String orgi);
}
