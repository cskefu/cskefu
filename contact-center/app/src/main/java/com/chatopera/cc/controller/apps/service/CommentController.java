/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.controller.apps.service;

import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.AgentService;
import com.chatopera.cc.persistence.repository.AgentServiceRepository;
import com.chatopera.cc.util.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("/service")
public class CommentController extends Handler{
	@Autowired
	private AgentServiceRepository agentServiceRes ;
	
	
	@RequestMapping("/comment/index")
    @Menu(type = "service" , subtype = "comment" , admin= true)
    public ModelAndView index(ModelMap map , HttpServletRequest request , String userid , String agentservice , @Valid String channel) {
		Page<AgentService> agentServiceList = agentServiceRes.findByOrgiAndSatisfaction(super.getOrgi(request) , true ,new PageRequest(super.getP(request), super.getPs(request))) ;
		map.addAttribute("serviceList", agentServiceList) ;
		return request(super.createAppsTempletResponse("/apps/service/comment/index"));
    }
}
