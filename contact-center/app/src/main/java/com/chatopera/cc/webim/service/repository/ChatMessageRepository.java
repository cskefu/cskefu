/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.webim.service.repository;

import java.util.List;

import com.chatopera.cc.webim.util.server.message.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract interface ChatMessageRepository
  extends JpaRepository<ChatMessage, String>
{
  public abstract List<ChatMessage> findByUsessionAndOrgi(String usession , String orgi);
  
  public abstract ChatMessage findById(String id);
  
  public abstract Page<ChatMessage> findByUsessionAndOrgi(String usession, String orgi , Pageable page );
  
  public abstract Page<ChatMessage> findByUseridAndOrgi(String userid, String orgi , Pageable page );
  
  public abstract List<ChatMessage> findByContextidAndOrgi(String contextid , String orgi);
  
  public abstract Page<ChatMessage> findByContextidAndOrgi(String contextid , String orgi, Pageable page );
  
  public abstract Page<ChatMessage> findByChatypeAndOrgi(String chatype , String orgi, Pageable page );
  
  public abstract Page<ChatMessage> findByAgentserviceidAndOrgi(String agentserviceid, String orgi , Pageable page );
  
  public abstract Page<ChatMessage> findByContextidAndUseridAndOrgi(String contextid ,String userid , String orgi, Pageable page);
}
