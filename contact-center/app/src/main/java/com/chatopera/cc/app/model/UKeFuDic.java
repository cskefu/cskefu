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
package com.chatopera.cc.app.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.chatopera.cc.app.MainContext;
import com.chatopera.cc.app.service.cache.CacheHelper;

public class UKeFuDic<K,V> extends HashMap<K,V>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2110217015030751243L;
	private static UKeFuDic<Object, Object> uKeFuDic = new UKeFuDic<Object, Object>();
	
	public static UKeFuDic<?, ?> getInstance(){
		return uKeFuDic ;
	}
	
	@SuppressWarnings("unchecked")
	public List<SysDic> getSysDic(String key){
		return (List<SysDic>) CacheHelper.getSystemCacheBean().getCacheObject(key, MainContext.SYSTEM_ORGI)  ;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public V get(Object key) {
		Object obj = CacheHelper.getSystemCacheBean().getCacheObject(String.valueOf(key), MainContext.SYSTEM_ORGI) ;
		if(obj!=null && obj instanceof List){
			obj = getDic((String) key) ;
		}else if(obj == null && (String.valueOf(key)).endsWith(".subdic") && (String.valueOf(key)).lastIndexOf(".subdic") > 0){
			String id = (String.valueOf(key)).substring(0  , (String.valueOf(key)).lastIndexOf(".subdic")) ;
			SysDic dic = (SysDic) CacheHelper.getSystemCacheBean().getCacheObject(id, MainContext.SYSTEM_ORGI) ;
			if(dic!=null){
				SysDic sysDic = (SysDic) CacheHelper.getSystemCacheBean().getCacheObject(dic.getDicid(), MainContext.SYSTEM_ORGI) ;
				obj = getDic(sysDic.getCode(), dic.getParentid()) ;
			}
		}
		return (V) obj;
	}
	
	@SuppressWarnings("unchecked")
	public List<SysDic> getDic(String code){
		List<SysDic> dicList = new ArrayList<SysDic>() ;
		List<SysDic> sysDicList = (List<SysDic>) CacheHelper.getSystemCacheBean().getCacheObject(code, MainContext.SYSTEM_ORGI)  ;
		if(sysDicList!=null){
			for(SysDic dic : sysDicList){
				if(dic.getParentid().equals(dic.getDicid())){
					dicList.add(dic) ;
				}
			}
		}
		return dicList ;
	}
	
	@SuppressWarnings("unchecked")
	public List<SysDic> getDic(String code , String id){
		List<SysDic> dicList = new ArrayList<SysDic>() ;
		List<SysDic> sysDicList = (List<SysDic>) CacheHelper.getSystemCacheBean().getCacheObject(code, MainContext.SYSTEM_ORGI)  ;
		if(sysDicList!=null){
			for(SysDic dic : sysDicList){
				if(dic.getParentid().equals(id)){
					dicList.add(dic) ;
				}
			}
		}
		return dicList ;
	}
	
	
	
	public List<SysDic> getEpt(){
		return new ArrayList<SysDic>() ;
	}
	
	public SysDic getDicItem(String id){
		return (SysDic) CacheHelper.getSystemCacheBean().getCacheObject(id, MainContext.SYSTEM_ORGI) ;
	}
}
