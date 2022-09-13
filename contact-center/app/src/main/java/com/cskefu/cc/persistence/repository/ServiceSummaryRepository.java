/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
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


import com.cskefu.cc.model.AgentServiceSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ServiceSummaryRepository extends JpaRepository<AgentServiceSummary, String>{
	
	List<AgentServiceSummary> findByAgentserviceidAndOrgi(String agentserviceid, String orgi);
	
	AgentServiceSummary findByIdAndOrgi(String id, String orgi) ;
	
	AgentServiceSummary findByStatuseventidAndOrgi(String statuseventid, String orgi);
	
	Page<AgentServiceSummary> findAll(Specification<AgentServiceSummary> spec, Pageable pageable);  //分页按条件查询

	Page<AgentServiceSummary> findByChannelAndOrgi(String string, String orgi, Pageable pageable);
	
	Page<AgentServiceSummary> findByChannelNotAndOrgi(String string, String orgi, Pageable pageable);

	Page<AgentServiceSummary> findByChannelNotAndOrgiAndSkillIn(String string, String orgi, Set<String> agentskill , Pageable pageable);

	Page<AgentServiceSummary> findByChannelNotAndOrgiAndProcessTrueAndSkillIn(String string, String orgi, Set<String> agentskill , Pageable pageable);
}
