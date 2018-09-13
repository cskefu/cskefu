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
package com.chatopera.cc.app.cache;


public interface CacheInstance {
	/**
	 * 坐席状态
	 * @return
	 */
	public CacheBean getAgentStatusCacheBean() ;
	
	
	/**
	 * 服务中用户
	 * @return
	 */
	public CacheBean getAgentUserCacheBean();
	
	
	/**
	 * 在线用户
	 * @return
	 */
	public CacheBean getOnlineCacheBean();
	
	/**
	 * 系统缓存
	 * @return
	 */
	public CacheBean getSystemCacheBean();
	
	
	/**
	 * IMR指令
	 * @return
	 */
	public CacheBean getIMRCacheBean();
	
	/**
	 * IMR指令
	 * @return
	 */
	public CacheBean getCallCenterCacheBean();
	
	/**
	 * IMR指令
	 * @return
	 */
	public CacheBean getCallCenterAgentCacheBean();
	
	/**
	 * IMR指令
	 * @return
	 */
	public CacheBean getApiUserCacheBean();
	
	/**
	 * IMR指令
	 * @return
	 */
	public CacheBean getJobCacheBean();
	
	/**
	 * 外呼
	 * @return
	 */
	public CacheBean getCallOutCacheBean();
	
}