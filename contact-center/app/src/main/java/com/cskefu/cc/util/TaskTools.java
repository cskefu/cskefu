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
package com.cskefu.cc.util;

import com.cskefu.cc.model.JobDetail;

import java.util.Date;

public class TaskTools {
	public static Date updateTaskNextFireTime(JobDetail jobDetail){
		Date nextFireDate = new Date();
		Date date = new Date();
		if(jobDetail!=null && jobDetail.getCronexp()!=null && jobDetail.getCronexp().length()>0){
			try {
				nextFireDate = (CronTools.getFinalFireTime(jobDetail.getCronexp(), jobDetail.getNextfiretime()!=null ? jobDetail.getNextfiretime() : date)) ;
			} catch (Exception e) {
				nextFireDate = new Date(System.currentTimeMillis() + 1000*60*60*24) ; 	//一旦任务的 Cron表达式错误，将下次执行时间自动设置为一天后，避免出现任务永远无法终止的情况
				e.printStackTrace();
			}
		}
		return nextFireDate ;
	}
	
}
