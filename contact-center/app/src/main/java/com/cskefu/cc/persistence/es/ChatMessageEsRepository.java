package com.cskefu.cc.persistence.es;

import com.cskefu.cc.socketio.message.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ChatMessageEsRepository extends ElasticsearchRepository<ChatMessage,String> {

    Page<ChatMessage> findByUsessionAndMessageContaining(String usession, String message, Pageable page);

}
