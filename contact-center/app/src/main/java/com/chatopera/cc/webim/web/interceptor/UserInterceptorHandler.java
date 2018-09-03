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
package com.chatopera.cc.webim.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.chatopera.cc.util.Menu;
import com.chatopera.cc.webim.service.cache.CacheHelper;
import com.chatopera.cc.webim.web.model.User;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.chatopera.cc.core.UKDataContext;
import com.chatopera.cc.util.UKTools;
import com.chatopera.cc.webim.service.acd.ServiceQuene;
import com.chatopera.cc.webim.web.model.SystemConfig;
import com.chatopera.cc.webim.web.model.UKeFuDic;

public class UserInterceptorHandler extends HandlerInterceptorAdapter {
	
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
    	boolean filter = false; 
        User user = (User) request.getSession(true).getAttribute(UKDataContext.USER_SESSION_NAME) ;
        if(handler instanceof HandlerMethod) {
	        HandlerMethod  handlerMethod = (HandlerMethod ) handler ;
	        Menu menu = handlerMethod.getMethod().getAnnotation(Menu.class) ;
	        if(user != null || (menu!=null && menu.access()) || handlerMethod.getBean() instanceof BasicErrorController){
	        	filter = true;
	        }
	        
	        if(!filter){
	        	response.sendRedirect("/login.html");
	        }
        }else {
        	filter =true ;
        }
        return filter ; 
    }

    public void postHandle(HttpServletRequest arg0, HttpServletResponse response, Object arg2,
            ModelAndView view) throws Exception {
    	User user = (User) arg0.getSession().getAttribute(UKDataContext.USER_SESSION_NAME) ;
    	String infoace = (String) arg0.getSession().getAttribute(UKDataContext.UKEFU_SYSTEM_INFOACQ) ;		//进入信息采集模式
    	SystemConfig systemConfig = UKTools.getSystemConfig();
    	if( view!=null){
	    	if(user!=null){
				view.addObject("user", user) ;
				
				if(systemConfig!=null && systemConfig.isEnablessl()) {
					view.addObject("schema","https") ;
					if(arg0.getServerPort() == 80) {
						view.addObject("port", 443) ;
					}else {
						view.addObject("port", arg0.getServerPort()) ;
					}
				}else {
					view.addObject("schema",arg0.getScheme()) ;
					view.addObject("port",arg0.getServerPort()) ;
				}
				view.addObject("hostname",arg0.getServerName()) ;
				
				HandlerMethod  handlerMethod = (HandlerMethod ) arg2 ;
				Menu menu = handlerMethod.getMethod().getAnnotation(Menu.class) ;
				if(menu!=null){
					view.addObject("subtype", menu.subtype()) ;
					view.addObject("maintype", menu.type()) ;
					view.addObject("typename", menu.name()) ;
				}
				view.addObject("orgi", user.getOrgi()) ;
			}
	    	if(!StringUtils.isBlank(infoace)){
	    		view.addObject("infoace", infoace) ;		//进入信息采集模式
	    	}
	    	view.addObject("webimport",UKDataContext.getWebIMPort()) ;
	    	view.addObject("sessionid", UKTools.getContextID(arg0.getSession().getId())) ;
	    	
	    	view.addObject("models", UKDataContext.model) ;
	    	
	    	if(user!=null){
	    		view.addObject("agentStatusReport",ServiceQuene.getAgentReport(user.getOrgi())) ;
	    	}
			/**
			 * WebIM共享用户
			 */
			User imUser = (User) arg0.getSession().getAttribute(UKDataContext.IM_USER_SESSION_NAME) ;
			if(imUser == null && view!=null){
				imUser = new User();
				imUser.setUsername(UKDataContext.GUEST_USER) ;
				imUser.setId(UKTools.getContextID(arg0.getSession(true).getId())) ;
				imUser.setSessionid(imUser.getId()) ;
				view.addObject("imuser", imUser) ;
			}
			
			if(arg0.getParameter("msg") != null){
				view.addObject("msg", arg0.getParameter("msg")) ;
			}
			view.addObject("uKeFuDic", UKeFuDic.getInstance()) ;	//处理系统 字典数据 ， 通过 字典code 获取
			
			view.addObject("uKeFuSecField", CacheHelper.getSystemCacheBean().getCacheObject(UKDataContext.UKEFU_SYSTEM_SECFIELD, UKDataContext.SYSTEM_ORGI)) ;	//处理系统 需要隐藏号码的字段， 启动的时候加载
			
			if(systemConfig != null){
				view.addObject("systemConfig", systemConfig)  ;
			}else{
				view.addObject("systemConfig", new SystemConfig())  ;
			}
			view.addObject("tagTypeList", UKeFuDic.getInstance().getDic("com.dic.tag.type")) ;
	    	
			view.addObject("advTypeList", UKeFuDic.getInstance().getDic("com.dic.adv.type")) ;
			
			Logger logger = LogManager.getLogger("com.chatopera.cc.webim.web.handler.apps.internet.UCKeFuWeiXinController") ;
 			if(logger!=null && logger.getLevel() != null){
				systemConfig.setLoglevel(logger.getLevel().toString());
			}
 			view.addObject("ip", arg0.getRemoteAddr()) ;
    	}
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }

}
