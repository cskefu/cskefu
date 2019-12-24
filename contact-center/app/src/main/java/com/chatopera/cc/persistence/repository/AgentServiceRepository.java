/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.persistence.repository;

import com.chatopera.cc.model.AgentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AgentServiceRepository
  extends JpaRepository<AgentService, String>
{
  AgentService findByIdAndOrgi(String paramString, String orgi);
  
  List<AgentService> findByUseridAndOrgiOrderByLogindateDesc(String paramString, String orgi);

  @Query(value = "SELECT * FROM uk_agentservice WHERE userid= ?1 AND orgi = ?2 ORDER BY logindate DESC LIMIT 1", nativeQuery = true)
  Optional<AgentService> findOneByUseridAndOrgiOrderByLogindateDesc(String userid, String orgi);

  AgentService findFirstByUserid(String userid);

  Page<AgentService> findByOrgi(String orgi, Pageable paramPageable);
  
  Page<AgentService> findByOrgiAndSatisfaction(String orgi, boolean satisfaction, Pageable paramPageable);
  
  Page<AgentService> findByOrgiAndStatus(String orgi, String status, Pageable paramPageable);
  
  List<AgentService> findByAgentnoAndStatusAndOrgi(String agentno, String status, String orgi);
  
  int countByUseridAndOrgiAndStatus(String userid, String orgi, String status);
  
  List<AgentService> findByUseridAndOrgiAndStatus(String userid, String orgi, String status, Sort sort);
  
  Page<AgentService> findAll(Specification<AgentService> spec, Pageable pageable);  //分页按条件查询

}
