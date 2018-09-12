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
package com.chatopera.cc.app.aop;

import java.util.List;

import com.chatopera.cc.app.MainContext;
import com.chatopera.cc.util.UKeFuList;
import com.chatopera.cc.event.MultiUpdateEvent;
import com.chatopera.cc.app.service.hibernate.BaseService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chatopera.cc.util.UKTools;
import com.chatopera.cc.app.model.ESBean;

@Aspect
@Component
public class SyncDatabaseExt {
	
	@Autowired
	private BaseService<?> dbDataRes ;
	/** 
     * 定义拦截规则：拦截org.springframework.data.elasticsearch.repository。 
     */  
    @Pointcut("execution(* org.springframework.data.elasticsearch.repository.*.save(*))")  
    public void syncSaveEsData(){}  
    
    /** 
     * 定义拦截规则：拦截org.springframework.data.elasticsearch.repository。 
     */  
    @Pointcut("execution(* org.springframework.data.elasticsearch.repository.*.delete(*))")  
    public void syncDeleteEsData(){}  
      
    @SuppressWarnings("unchecked")
	@Around("syncSaveEsData()")  
    public Object syncSaveEsData(ProceedingJoinPoint pjp) throws Throwable{  
    	Object[] args  = pjp.getArgs() ;
    	if(args.length == 1){
    		Object data = args[0] ;
    		if(data!=null){
	    		if(data instanceof UKeFuList){
	    			/**只有一个地方用到，从DB同步数据到ES**/
	    		}else if(data instanceof List){
	    			List<Object> dataList = (List<Object>)data ;
	    			for(Object dbData : dataList){
	    				UKTools.multiupdate(new MultiUpdateEvent<Object>(dbData , dbDataRes, MainContext.MultiUpdateType.SAVE.toString()));
	    			}
	    		}else{
	    			UKTools.multiupdate(new MultiUpdateEvent<Object>(data, dbDataRes, MainContext.MultiUpdateType.SAVE.toString()));
	    		}
    		}
    	}
        return pjp.proceed();  
    }  
    
    @SuppressWarnings("unchecked")
	@Around("syncDeleteEsData()")  
    public Object syncDeleteEsData(ProceedingJoinPoint pjp) throws Throwable{  
    	Object[] args  = pjp.getArgs() ;
    	if(args.length == 1){
    		Object data = args[0] ;
    		if(data instanceof List){
    			List<Object> dataList = (List<Object>)data ;
    			for(Object dbData : dataList){
    				UKTools.multiupdate(new MultiUpdateEvent<Object>(dbData , dbDataRes, MainContext.MultiUpdateType.DELETE.toString()));
    			}
    		}else{
    			if(data instanceof ESBean){
    				UKTools.multiupdate(new MultiUpdateEvent<Object>(data, dbDataRes, MainContext.MultiUpdateType.DELETE.toString()));
    			}else{
    				UKTools.multiupdate(new MultiUpdateEvent<Object>(data, dbDataRes, MainContext.MultiUpdateType.DELETE.toString()));
    			}
    		}
    	}
        return pjp.proceed();  
    }  
}
