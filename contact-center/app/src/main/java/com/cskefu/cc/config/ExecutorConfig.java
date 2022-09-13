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
package com.cskefu.cc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 线程池 ， 作业调度平台
 * @author iceworld
 *
 */
@Configuration
public class ExecutorConfig {
	private static int CORE_POOL_SIZE = 7;
	private static int MAX_POOL_SIZE = 100;

	/**
	 * 作业平台使用的线程池
	 * @return
	 */
	@Bean(name = "webimTaskExecutor")
	public ThreadPoolTaskExecutor common() {

		ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
		// 线程池维护线程的最少数量
		poolTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
		// 线程池维护线程的最大数量
		poolTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
		// 线程池所使用的缓冲队列
		poolTaskExecutor.setQueueCapacity(200);
		// 线程池维护线程所允许的空闲时间
		poolTaskExecutor.setKeepAliveSeconds(30000);
		poolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
		poolTaskExecutor.setThreadNamePrefix("cs-webim-task-");

		return poolTaskExecutor;
	}

    @Bean(name = "scheduleTaskExecutor")
    public ThreadPoolTaskScheduler schedule(){
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(CORE_POOL_SIZE);
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        taskScheduler.setThreadNamePrefix("cs-schedule-");
        return  taskScheduler;
    }
}
