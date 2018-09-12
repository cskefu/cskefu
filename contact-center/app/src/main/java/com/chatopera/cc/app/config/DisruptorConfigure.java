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
package com.chatopera.cc.app.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.chatopera.cc.concurrent.chatbot.ChatbotDisruptorExceptionHandler;
import com.chatopera.cc.concurrent.chatbot.ChatbotEventFactory;
import com.chatopera.cc.concurrent.multiupdate.MultiUpdateEventFactory;
import com.chatopera.cc.concurrent.multiupdate.MultiUpdateEventHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.chatopera.cc.concurrent.user.UserDataEvent;
import com.chatopera.cc.concurrent.user.UserDataEventFactory;
import com.chatopera.cc.concurrent.user.UserEventHandler;

@Component
public class DisruptorConfigure {
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Bean(name="disruptor")   
    public Disruptor<UserDataEvent> disruptor() {   
		Executor executor = Executors.newCachedThreadPool();
    	 UserDataEventFactory factory = new UserDataEventFactory();
    	 Disruptor<UserDataEvent> disruptor = new Disruptor<UserDataEvent>(factory, 1024, executor, ProducerType.SINGLE , new SleepingWaitStrategy());
    	 disruptor.setDefaultExceptionHandler(new UKeFuExceptionHandler());
    	 disruptor.handleEventsWith(new UserEventHandler());
    	 disruptor.start();
         return disruptor;   
    }  
    
    @SuppressWarnings({ "unchecked", "deprecation" })
	@Bean(name="multiupdate")   
    public Disruptor<UserDataEvent> multiupdate() {   
    	Executor executor = Executors.newCachedThreadPool();
    	 MultiUpdateEventFactory factory = new MultiUpdateEventFactory();
    	 Disruptor<UserDataEvent> disruptor = new Disruptor<UserDataEvent>(factory, 1024, executor, ProducerType.SINGLE , new SleepingWaitStrategy());
    	 disruptor.handleEventsWith(new MultiUpdateEventHandler());
    	 disruptor.setDefaultExceptionHandler(new UKeFuExceptionHandler());
    	 disruptor.start();
         return disruptor;   
    }

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Bean(name="chatbot")
	public Disruptor<UserDataEvent> chatbot() {
		Executor executor = Executors.newCachedThreadPool();
		ChatbotEventFactory factory = new ChatbotEventFactory();
		Disruptor<UserDataEvent> disruptor = new Disruptor<UserDataEvent>(factory, 1024, executor, ProducerType.SINGLE , new SleepingWaitStrategy());
		disruptor.handleEventsWith(new MultiUpdateEventHandler());
		disruptor.setDefaultExceptionHandler(new ChatbotDisruptorExceptionHandler());
		disruptor.start();
		return disruptor;
	}
}
