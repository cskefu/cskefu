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

import com.chatopera.cc.webim.web.model.AgentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract interface AgentUserRepository  extends JpaRepository<AgentUser, String>
{
	public abstract AgentUser findByIdAndOrgi(String paramString, String orgi);

	public abstract List<AgentUser> findByUseridAndOrgi(String userid, String orgi);

	public abstract List<AgentUser> findByUseridAndStatus(String userid, String status);

	public abstract List<AgentUser> findByAgentnoAndOrgi(String agentno , String orgi , Sort sort);

	public abstract Page<AgentUser> findByOrgiAndStatus(String orgi ,String status , Pageable page);
	
	public abstract List<AgentUser> findByAgentnoAndStatusAndOrgi(String agentno ,String status , String orgi);
	
	public abstract int countByAgentnoAndStatusAndOrgi(String agentno ,String status , String orgi);

	public abstract AgentUser findOneByAgentnoAndStatusAndOrgi(String id, String status, String orgi);
}
