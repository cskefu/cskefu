package com.chatopera.cc.app.persistence.repository;

import com.chatopera.cc.app.model.Chatbot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public abstract interface ChatbotRepository extends JpaRepository<Chatbot, String> {

    public abstract boolean existsByClientIdAndOrgi(String clientId, String orgi);

    public abstract boolean existsBySnsAccountIdentifierAndOrgi(String snsid, String orgi);

    public abstract List<Chatbot> findByIdAndOrgi(String id, String orgi);

    public abstract Chatbot findBySnsAccountIdentifierAndOrgi(String snsid, String orgi);

    @Query(value = "select c from Chatbot c where " +
            "(:myorgans is null or c.organ IN :myorgans)")
    public Page<Chatbot> findByOrgans(@Param("myorgans") List<String> myorgans, Pageable pageRequest);
}
