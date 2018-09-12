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
package com.chatopera.cc.app.service.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.chatopera.cc.app.model.AgentService;

public abstract interface AgentServiceRepository
  extends JpaRepository<AgentService, String>
{
  public abstract AgentService findByIdAndOrgi(String paramString , String orgi);
  
  public abstract List<AgentService> findByUseridAndOrgi(String paramString, String orgi);
  
  public abstract Page<AgentService> findByOrgi(String orgi, Pageable paramPageable);
  
  public abstract Page<AgentService> findByOrgiAndSatisfaction(String orgi , boolean satisfaction, Pageable paramPageable);
  
  public abstract Page<AgentService> findByOrgiAndStatus(String orgi ,String status , Pageable paramPageable);
  
  public abstract List<AgentService> findByAgentnoAndStatusAndOrgi(String agentno, String status , String orgi);
  
  public abstract int countByUseridAndOrgiAndStatus(String userid, String orgi, String status);
  
  public abstract List<AgentService> findByUseridAndOrgiAndStatus(String userid, String orgi, String status, Sort sort);
  
  public Page<AgentService> findAll(Specification<AgentService> spec, Pageable pageable);  //分页按条件查询 
}
