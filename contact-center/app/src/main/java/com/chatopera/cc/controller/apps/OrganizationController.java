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

package com.chatopera.cc.controller.apps;

import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.Organization;
import com.chatopera.cc.model.SystemConfig;
import com.chatopera.cc.model.User;
import com.chatopera.cc.persistence.repository.OrganizationRepository;
import com.chatopera.cc.persistence.repository.UserRepository;
import com.chatopera.cc.util.Menu;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Controller
@RequestMapping("/apps/organization")
public class OrganizationController extends Handler {

	@Autowired
	private OrganizationRepository organizationRes;

	@Autowired
	private UserRepository userRes;
	
	
    @RequestMapping("/add")
    @Menu(type = "apps" , subtype = "organization")
    public ModelAndView add(ModelMap map , HttpServletRequest request) {
        return request(super.createRequestPageTempletResponse("/apps/organization/add"));
    }
    
    @RequestMapping("/save")
    @Menu(type = "apps" , subtype = "organization")
    public ModelAndView save(HttpServletRequest request ,@Valid Organization organization) throws NoSuchAlgorithmException, IOException {
    	if(StringUtils.isBlank(organization.getName())|| organization.getName().length()>100) {
    		return request(super.createRequestPageTempletResponse("redirect:/apps/tenant/index?msg=max_illegal"));	
    	}
    	organizationRes.save(organization) ;
    	User user = super.getUser(request);
    	if(user!=null) {
    		User userTemp = userRes.getOne(user.getId());
    		if(userTemp!=null&&StringUtils.isBlank(user.getOrgid())) {
    			userTemp.setOrgid(organization.getId());
    			userTemp.setOrgi(organization.getId());
    			userRes.save(userTemp);
    			super.setUser(request, userTemp);
    		}
    	}
    	ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/"));
    	//登录成功 判断是否进入多租户页面
    	SystemConfig systemConfig = MainUtils.getSystemConfig();
    	if(systemConfig!=null&&systemConfig.isEnabletneant()&&systemConfig.isTenantconsole()) {
    		view = request(super.createRequestPageTempletResponse("redirect:/apps/tenant/index"));
    	}
    	return view;
    }
    
    @RequestMapping("/edit")
    @Menu(type = "apps" , subtype = "organization")
    public ModelAndView edit(ModelMap map , HttpServletRequest request , @Valid String id) {
    	map.addAttribute("organization", organizationRes.findById(id)) ;
        return request(super.createRequestPageTempletResponse("/apps/organization/edit"));
    }
    
    @RequestMapping("/update")
    @Menu(type = "apps" , subtype = "organizationRes" , admin = true)
    public ModelAndView update(HttpServletRequest request ,@Valid Organization organization) throws NoSuchAlgorithmException, IOException {
    	if(StringUtils.isBlank(organization.getName())|| organization.getName().length()>100) {
    		return request(super.createRequestPageTempletResponse("redirect:/apps/tenant/index?msg=max_illegal"));	
    	}
    	Organization temp = organizationRes.findById(organization.getId()) ;
    	if(organization!=null) {
    		organization.setCreatetime(temp.getCreatetime());
    		organizationRes.save(organization) ;
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/apps/tenant/index"));
    }
    
}