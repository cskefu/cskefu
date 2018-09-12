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
package com.chatopera.cc.app.service.quene;

import com.chatopera.cc.app.service.cache.CacheHelper;
import com.chatopera.cc.app.model.AgentUser;
import org.apache.commons.lang.StringUtils;

import com.hazelcast.mapreduce.KeyPredicate;

@SuppressWarnings("deprecation")
public class AgentUserOrgiFilter implements KeyPredicate<String>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1236581634096258855L;
	private String orgi ;
	private String status ;
	/**
	 * 
	 */
	public AgentUserOrgiFilter(String orgi , String status){
		this.orgi = orgi ;
		this.status = status ;
	}
	public boolean evaluate(String key) {
		AgentUser user = (AgentUser) CacheHelper
				.getAgentUserCacheBean().getCacheObject(key , orgi);
		return user!=null && user.getStatus()!=null && !StringUtils.isBlank(orgi) && orgi.equals(user.getOrgi()) && user.getStatus()!=null && user.getStatus().equals(status);
	}
}