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
package com.chatopera.cc.app.handler.admin.system;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.chatopera.cc.app.MainContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.chatopera.cc.util.Menu;
import com.chatopera.cc.app.service.repository.LogRepository;
import com.chatopera.cc.app.handler.Handler;

@Controller
@RequestMapping("/admin/log")
public class LogController extends Handler{
	
	
	@Autowired
	private LogRepository logRes;

    @RequestMapping("/index")
    @Menu(type = "admin" , subtype = "syslog")
    public ModelAndView index(ModelMap map , HttpServletRequest request) {
    	map.addAttribute("logList", logRes.findByOrgi(MainContext.SYSTEM_ORGI , new PageRequest(super.getP(request), super.getPs(request) , Direction.DESC , "createdate")));
        return request(super.createAdminTempletResponse("/admin/system/log/index"));
    }
    
    @RequestMapping("/levels")
    @Menu(type = "admin" , subtype = "levels")
    public ModelAndView levels(ModelMap map , HttpServletRequest request , @Valid String levels) {
    	map.addAttribute("logList", logRes.findByOrgiAndLevels(MainContext.SYSTEM_ORGI , levels , new PageRequest(super.getP(request), super.getPs(request) , Direction.DESC , "createdate")));
    	map.addAttribute("levels" , levels) ;
        return request(super.createAdminTempletResponse("/admin/system/log/levels"));
    }
    
    @RequestMapping("/detail")
    @Menu(type = "admin" , subtype = "detail")
    public ModelAndView detail(ModelMap map , HttpServletRequest request , @Valid String id) {
    	map.addAttribute("log", logRes.findOne(id));
        return request(super.createRequestPageTempletResponse("/admin/system/log/detail"));
    }
}