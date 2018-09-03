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
package com.chatopera.cc.webim.util.log;

import java.util.Date;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;

import com.chatopera.cc.core.UKDataContext;
import com.chatopera.cc.util.UKTools;
import com.chatopera.cc.webim.web.model.Log;

public class UKeFuAppender extends ch.qos.logback.core.ConsoleAppender<ILoggingEvent> {
	@Override
	public void append(ILoggingEvent event) {
		super.append(event);
		try {
			Log log = new Log(UKDataContext.SYSTEM_ORGI , null , event.getFormattedMessage() , event.getLevel().toString() , event.getThreadName());
			log.setClazz(event.getLoggerName()) ;
			if(event.getFormattedMessage()!=null && event.getFormattedMessage().length() < 255){
				log.setMemo(event.getFormattedMessage());
			}else{
				log.setMemo(event.getFormattedMessage().substring(0 ,255));
			}
			if(event.getThrowableProxy()!=null){
				log.setMsg(event.getThrowableProxy().getMessage());
			}
			
			
			log.setMethod(event.getThreadName());
			log.setLogtype(event.getLevel().toString().equals(Level.ERROR.toString()) ? "1" : "0") ;
			log.setLogtime(String.valueOf(UKTools.dateFormate.format(new Date()))) ;
			/**
			 * 临时缓存
			 */
			UKDataContext.tempLogQueue.add(log) ;
		} catch (Throwable sqle) {
			sqle.printStackTrace();
		}

	}
}
