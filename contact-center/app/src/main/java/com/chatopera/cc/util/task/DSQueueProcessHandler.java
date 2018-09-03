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
package com.chatopera.cc.util.task;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.lmax.disruptor.dsl.Disruptor;

public class DSQueueProcessHandler {
	private  Disruptor<DSDataEvent> disruptor ;
	private Executor executor = Executors.newCachedThreadPool();
	private static DSQueueProcessHandler dsQueneProcess = new DSQueueProcessHandler() ;
	private DSDataEventProducer producer ;
	static{
		try {
			dsQueneProcess.process();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @throws InterruptedException
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void process() throws InterruptedException{
        disruptor = new Disruptor<DSDataEvent>(new DSDataEventFactory(), 1024, executor);

        disruptor.handleEventsWith(new DSDataEventHandler());

        disruptor.start();
        
        producer = new DSDataEventProducer(disruptor.getRingBuffer());
	}
	
	public DSData doTask(DSData dsData){
		producer.onData(dsData) ;
		return dsData ;
	}
	
	public static DSQueueProcessHandler getInstance(){
		return dsQueneProcess ;
	}
}
