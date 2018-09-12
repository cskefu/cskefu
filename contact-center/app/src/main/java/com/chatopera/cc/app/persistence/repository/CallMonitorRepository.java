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
package com.chatopera.cc.app.persistence.repository;

import java.util.List;

import com.chatopera.cc.app.model.CallMonitor;
import org.springframework.data.jpa.repository.JpaRepository;


/*
 * 坐席监控表 -- JPA接口
 */
public interface CallMonitorRepository extends JpaRepository<CallMonitor, String> {
	
	public abstract CallMonitor findByUseridAndOrgi(String userid,String orgi);
	
	public abstract CallMonitor findByOrgiAndAgentno(String orgi,String agentno);
	
	public abstract List<CallMonitor> findByOrgi(String orgi);

	public abstract List<CallMonitor> findByOrgiAndStatus(String orgi, String status);
	
	public abstract List<CallMonitor> findByOrgiAndCode(String orgi, String code);
	
	public abstract Long countByOrgiAndStatus(String orgi, String status);
	
	public abstract List<CallMonitor> findByOrgiAndOrgan(String orgi ,String organ);
	
	
	
}
