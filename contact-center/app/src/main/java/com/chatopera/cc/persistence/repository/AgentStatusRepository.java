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

import com.chatopera.cc.model.AgentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AgentStatusRepository extends
        JpaRepository<AgentStatus, String> {
    AgentStatus findByIdAndOrgi(String paramString, String orgi);

    @Query(value = "SELECT * FROM uk_agentstatus WHERE agentno = ?1 AND orgi = ?2 ORDER BY createtime DESC LIMIT 1", nativeQuery = true)
    Optional<AgentStatus> findOneByAgentnoAndOrgi(final String agentid, final String orgi);

    AgentStatus findByAgentno(String agentid);

    Page<AgentStatus> findByOrgi(String orgi, Pageable page);

    List<AgentStatus> findByOrgi(String orgi);
}
