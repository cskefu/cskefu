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
package com.chatopera.cc.app.handler;

import javax.servlet.http.HttpServletRequest;

import com.chatopera.cc.app.MainContext;
import com.chatopera.cc.app.service.cache.CacheHelper;
import com.chatopera.cc.app.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.chatopera.cc.app.service.acd.ServiceQuene;

@Controller
public class ApplicationController extends Handler{

	@RequestMapping("/")
    public ModelAndView admin(HttpServletRequest request) {
		ModelAndView view = request(super.createRequestPageTempletResponse("/apps/index"));
		User user = super.getUser(request) ;
        view.addObject("agentStatusReport",ServiceQuene.getAgentReport(user.getOrgi())) ;
        view.addObject("tenant",super.getTenant(request));
        view.addObject("istenantshare",super.isEnabletneant());
        if(super.isEnabletneant()) {
        	//多租户启用 非超级管理员 一定要选择租户才能进入界面
        	if(!user.isSuperuser() && !StringUtils.isBlank(user.getOrgid()) && super.isTenantconsole() && MainContext.SYSTEM_ORGI.equals(user.getOrgi())) {
        		view = request(super.createRequestPageTempletResponse("redirect:/apps/tenant/index"));
        	}
        	if(StringUtils.isBlank(user.getOrgid())) {
        		view = request(super.createRequestPageTempletResponse("redirect:/apps/organization/add.html"));
        	}
        }
		view.addObject("agentStatus", CacheHelper.getAgentStatusCacheBean().getCacheObject(user.getId(), user.getOrgi())) ;
        return view;
    }
}