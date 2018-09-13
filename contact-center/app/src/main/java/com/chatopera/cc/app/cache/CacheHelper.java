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

import com.chatopera.cc.app.cache.hazelcast.HazlcastCacheHelper;

public class CacheHelper {
	private static CacheHelper instance = new CacheHelper();
	
	/**
	 * 获取缓存实例
	 */
	public static CacheHelper getInstance(){
		return instance ;
	}
	private static CacheInstance cacheInstance = new HazlcastCacheHelper();
	
	public static CacheBean getAgentStatusCacheBean() {
		return cacheInstance!=null ? cacheInstance.getAgentStatusCacheBean() : null;
	}
	public static CacheBean getAgentUserCacheBean() {
		return cacheInstance!=null ? cacheInstance.getAgentUserCacheBean() : null ;
	}
	public static CacheBean getOnlineUserCacheBean() {
		return cacheInstance!=null ? cacheInstance.getOnlineCacheBean() : null;
	}
	public static CacheBean getSystemCacheBean() {
		return cacheInstance!=null ? cacheInstance.getSystemCacheBean() : null ;
	}
	
	public static CacheBean getIMRCacheBean() {
		return cacheInstance!=null ? cacheInstance.getIMRCacheBean() : null ;
	}
	public static CacheBean getCallCenterCacheBean() {
		return cacheInstance!=null ? cacheInstance.getCallCenterCacheBean() : null ;
	}
	public static CacheBean getCallCenterAgentCacheBean() {
		return cacheInstance!=null ? cacheInstance.getCallCenterAgentCacheBean() : null ;
	}
	public static CacheBean getApiUserCacheBean() {
		return cacheInstance!=null ? cacheInstance.getApiUserCacheBean() : null ;
	}
	public static CacheBean getJobCacheBean() {
		return cacheInstance!=null ? cacheInstance.getJobCacheBean(): null ;
	}
	public static CacheBean getCallOutCacheBean() {
		return cacheInstance!=null ? cacheInstance.getCallOutCacheBean(): null ;
	}
	
}
