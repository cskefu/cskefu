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
package com.chatopera.cc.webim.service.cache.hazelcast.impl;

import java.util.Collection;
import java.util.concurrent.locks.Lock;

import com.chatopera.cc.webim.service.cache.CacheBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hazelcast.com.eclipsesource.json.JsonObject;
import com.hazelcast.core.HazelcastInstance;

@Service("system_cache")
public class SystemCache implements CacheBean {
	
	@Autowired
	public HazelcastInstance hazelcastInstance;	
	
	private String cacheName ; 
	
	public HazelcastInstance getInstance(){
		return hazelcastInstance ;
	}
	public CacheBean getCacheInstance(String cacheName){
		this.cacheName = cacheName ;
		return this ;
	}
	
	@Override
	public void put(String key, Object value, String orgi) {
		getInstance().getMap(getName()).put(key, value) ;
	}

	@Override
	public void clear(String orgi) {
		getInstance().getMap(getName()).clear();
	}

	@Override
	public Object delete(String key, String orgi) {
		return getInstance().getMap(getName()).remove(key) ;
	}

	@Override
	public void update(String key, String orgi, Object value) {
		getInstance().getMap(getName()).put(key, value);
	}

	@Override
	public Object getCacheObject(String key, String orgi) {
		return getInstance().getMap(getName()).get(key);
	}

	public String getName() {
		return cacheName ;
	}

//	@Override
	public void service() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<?> getAllCacheObject(String orgi) {
		return getInstance().getMap(getName()).keySet();
	}
	@Override
	public Object getCacheObject(String key, String orgi, Object defaultValue) {
		return getCacheObject(key, orgi);
	}
	@Override
	public Object getCache() {
		return getInstance().getMap(cacheName);
	}
	
	@Override
	public Lock getLock(String lock , String orgi) {
		// TODO Auto-generated method stub
		return getInstance().getLock(lock);
	}
	@Override
	public long getSize() {
		return getInstance().getMap(getName()).size();
	}
	@Override
	public long getAtomicLong(String cacheName) {
		return getInstance().getAtomicLong(getName()).incrementAndGet();
	}
	@Override
	public void setAtomicLong(String cacheName, long start) {
		getInstance().getAtomicLong(getName()).set(start);
	}
	
	@Override
	public JsonObject getStatics() {
		// TODO Auto-generated method stub
		return getInstance().getMap(getName()).getLocalMapStats().toJson();
	}
}
