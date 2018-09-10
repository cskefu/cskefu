package com.chatopera.cc.webim.service.repository;

import com.chatopera.cc.webim.web.model.Chatbot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public abstract interface ChatbotRepository extends JpaRepository<Chatbot, String> {

    public abstract boolean existsByChatbotIDAndOrgi(String chatbotID, String orgi);

    public abstract boolean existsBySnsAccountIdentifierAndOrgi(String snsid, String orgi);

    @Query(value = "select c from Chatbot c where " +
            "(:myorgans is null or c.organ IN :myorgans)")
    public Page<Chatbot> findByOrgans(@Param("myorgans") List<String> myorgans, Pageable pageRequest);
}
