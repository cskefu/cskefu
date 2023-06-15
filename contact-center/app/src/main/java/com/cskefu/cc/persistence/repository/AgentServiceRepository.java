/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2023 Chatopera Inc, <https://www.chatopera.com>
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
package com.cskefu.cc.persistence.repository;

import com.cskefu.cc.model.AgentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface AgentServiceRepository
        extends JpaRepository<AgentService, String> {

    AgentService findById(String paramString);

    List<AgentService> findByUseridOrderByLogindateDesc(String paramString);

    @Query(value = "SELECT * FROM uk_agentservice WHERE userid= ?1 ORDER BY logindate DESC LIMIT 1", nativeQuery = true)
    Optional<AgentService> findOneByUseridOrderByLogindateDesc(String userid);

    AgentService findFirstByUserid(String userid);

    Page<AgentService> findAll(Pageable paramPageable);

    Page<AgentService> findBySatisfaction(boolean satisfaction, Pageable paramPageable);

    Page<AgentService> findBySatisfactionAndSkillIn(boolean satisfaction, Set<String> skill, Pageable paramPageable);

    Page<AgentService> findByStatus(String status, Pageable paramPageable);

    Page<AgentService> findByStatusAndAgentskillIn(String status, Set<String> agentskill, Pageable paramPageable);

    int countByStatusAndAgentskillIn(String status, Set<String> agentskill);

    List<AgentService> findByAgentnoAndStatus(String agentno, String status);

    int countByUseridAndStatus(String userid, String status);

    List<AgentService> findByUseridAndStatus(String userid, String status, Sort sort);

    Page<AgentService> findAll(Specification<AgentService> spec, Pageable pageable);  //分页按条件查询

}
