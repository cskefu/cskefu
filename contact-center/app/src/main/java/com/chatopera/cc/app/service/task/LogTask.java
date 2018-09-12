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
package com.chatopera.cc.app.service.task;

import com.chatopera.cc.app.MainContext;
import com.chatopera.cc.app.service.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.chatopera.cc.app.model.Log;

@Configuration
@EnableScheduling
public class LogTask {
	
	@Autowired
	private LogRepository logRes;
	
	@Scheduled(fixedDelay= 1000) // 每5秒执行一次
	public void log(){
		/**
    	 * 日志处理
    	 */
    	Log log = null ;
    	while((log = MainContext.tempLogQueue.poll()) != null){
			logRes.save(log) ;
		}
	}
}
