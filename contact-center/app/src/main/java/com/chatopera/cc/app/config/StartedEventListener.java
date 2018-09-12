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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.app.cache.CacheHelper;
import com.chatopera.cc.app.persistence.repository.GenerationRepository;
import com.chatopera.cc.app.persistence.repository.SysDicRepository;
import com.chatopera.cc.app.persistence.repository.SystemConfigRepository;
import com.chatopera.cc.app.persistence.repository.TablePropertiesRepository;
import com.chatopera.cc.app.model.Generation;
import com.chatopera.cc.app.model.SysDic;
import com.chatopera.cc.app.basic.MainUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.chatopera.cc.app.persistence.repository.BlackListRepository;
import com.chatopera.cc.app.model.BlackEntity;
import com.chatopera.cc.app.model.SystemConfig;

@Component
public class StartedEventListener implements ApplicationListener<ContextRefreshedEvent> {
	
	@Value("${web.upload-path}")
    private String path;
	
	private SysDicRepository sysDicRes;
	private BlackListRepository blackListRes ;
	
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
    	if(MainContext.getContext() == null){
    		MainContext.setApplicationContext(event.getApplicationContext());
    	}
    	sysDicRes = event.getApplicationContext().getBean(SysDicRepository.class) ;
    	blackListRes = event.getApplicationContext().getBean(BlackListRepository.class) ;
    	List<SysDic> sysDicList = sysDicRes.findAll() ;
    	
    	for(SysDic dic : sysDicList){
    		CacheHelper.getSystemCacheBean().put(dic.getId(), dic, dic.getOrgi());
			if(dic.getParentid().equals("0")){
				List<SysDic> sysDicItemList = new ArrayList<SysDic>();
				for(SysDic item : sysDicList){
					if(item.getDicid()!=null && item.getDicid().equals(dic.getId())){
						sysDicItemList.add(item) ;
					}
				}
				CacheHelper.getSystemCacheBean().put(dic.getCode(), sysDicItemList, dic.getOrgi());
			}
		}
    	List<BlackEntity> blackList = blackListRes.findByOrgi(MainContext.SYSTEM_ORGI) ;
    	for(BlackEntity black : blackList){
    		if(!StringUtils.isBlank(black.getUserid())) {
	    		if(black.getEndtime()==null || black.getEndtime().after(new Date())){
	    			CacheHelper.getSystemCacheBean().put(black.getUserid(), black, black.getOrgi());
	    		}
    		}
    	}
    	/**
    	 * 加载系统全局配置
    	 */
    	SystemConfigRepository systemConfigRes = event.getApplicationContext().getBean(SystemConfigRepository.class) ;
    	SystemConfig config = systemConfigRes.findByOrgi(MainContext.SYSTEM_ORGI) ;
    	if(config != null){
    		CacheHelper.getSystemCacheBean().put("systemConfig", config, MainContext.SYSTEM_ORGI);
    	}
    	GenerationRepository generationRes = event.getApplicationContext().getBean(GenerationRepository.class) ;
    	List<Generation> generationList = generationRes.findAll() ;
    	for(Generation generation : generationList){
    		CacheHelper.getSystemCacheBean().setAtomicLong(MainContext.ModelType.WORKORDERS.toString(), generation.getStartinx());
    	}
    	
    	MainUtils.initSystemArea();
    	
    	MainUtils.initSystemSecField(event.getApplicationContext().getBean(TablePropertiesRepository.class));
    	//MainUtils.initAdv();//初始化广告位
    }
}