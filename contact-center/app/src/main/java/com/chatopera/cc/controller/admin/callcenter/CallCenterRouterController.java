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
package com.chatopera.cc.controller.admin.callcenter;

import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.PbxHost;
import com.chatopera.cc.model.RouterRules;
import com.chatopera.cc.persistence.repository.PbxHostRepository;
import com.chatopera.cc.persistence.repository.RouterRulesRepository;
import com.chatopera.cc.util.Menu;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/callcenter")
public class CallCenterRouterController extends Handler{
	
	@Autowired
	private PbxHostRepository pbxHostRes ;
	
	
	@Autowired
	private RouterRulesRepository routerRulesRes ;
	
	
	@RequestMapping(value = "/router")
    @Menu(type = "callcenter" , subtype = "callcenterresource" , access = false , admin = true)
    public ModelAndView skill(ModelMap map , HttpServletRequest request , @Valid String hostid) {
		List<PbxHost> pbxHostList = pbxHostRes.findByOrgi(super.getOrgi(request)) ;
		map.addAttribute("pbxHostList" , pbxHostList);
		if(pbxHostList.size() > 0){
			map.addAttribute("pbxHost" , pbxHostRes.findByIdAndOrgi(hostid, super.getOrgi(request)));
			map.addAttribute("routerRulesList" , routerRulesRes.findByHostidAndOrgi(hostid, super.getOrgi(request)));
		}
		return request(super.createRequestPageTempletResponse("/admin/callcenter/peer/index"));
    }
	
	@RequestMapping(value = "/router/add")
    @Menu(type = "callcenter" , subtype = "extention" , access = false , admin = true)
    public ModelAndView extentionadd(ModelMap map , HttpServletRequest request , @Valid String hostid) {
		map.put("pbxHost", pbxHostRes.findByIdAndOrgi(hostid, super.getOrgi(request))) ;
    	return request(super.createRequestPageTempletResponse("/admin/callcenter/peer/add"));
    }
	
	@RequestMapping(value = "/router/save")
    @Menu(type = "callcenter" , subtype = "extention" , access = false , admin = true)
    public ModelAndView extentionsave(ModelMap map , HttpServletRequest request , @Valid RouterRules router) {
		if(!StringUtils.isBlank(router.getName())){
			int count = routerRulesRes.countByNameAndOrgi(router.getName(), super.getOrgi(request)) ;
			if(count == 0){
				router.setOrgi(super.getOrgi(request));
				router.setCreater(super.getUser(request).getId());
				routerRulesRes.save(router) ;
			}
		}
		return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/peer.html?hostid="+router.getHostid()));
    }
	
	@RequestMapping(value = "/router/edit")
    @Menu(type = "callcenter" , subtype = "extention" , access = false , admin = true)
    public ModelAndView routeredit(ModelMap map , HttpServletRequest request , @Valid String id , @Valid String hostid) {
		map.addAttribute("routerRules" , routerRulesRes.findByIdAndOrgi(id, super.getOrgi(request)));
		map.put("pbxHost", pbxHostRes.findByIdAndOrgi(hostid, super.getOrgi(request))) ;
    	return request(super.createRequestPageTempletResponse("/admin/callcenter/peer/edit"));
    }
	
	@RequestMapping(value = "/router/update")
    @Menu(type = "callcenter" , subtype = "extention" , access = false , admin = true)
    public ModelAndView pbxhostupdate(ModelMap map , HttpServletRequest request , @Valid RouterRules router) {
		if(!StringUtils.isBlank(router.getId())){
			RouterRules oldRouter = routerRulesRes.findByIdAndOrgi(router.getId(), super.getOrgi(request)) ;
			if(oldRouter!=null){
				oldRouter.setName(router.getName());
				oldRouter.setField(router.getField());
				oldRouter.setRegex(router.getRegex());
				oldRouter.setRouterinx(router.getRouterinx());
				oldRouter.setFalsebreak(router.isFalsebreak());
				routerRulesRes.save(oldRouter);
			}
		}
		return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/peer.html?hostid="+router.getHostid()));
    }
	
	@RequestMapping(value = "/router/code")
    @Menu(type = "callcenter" , subtype = "extention" , access = false , admin = true)
    public ModelAndView routercode(ModelMap map , HttpServletRequest request , @Valid String id , @Valid String hostid) {
		map.addAttribute("routerRules" , routerRulesRes.findByIdAndOrgi(id, super.getOrgi(request)));
		map.put("pbxHost", pbxHostRes.findByIdAndOrgi(hostid, super.getOrgi(request))) ;
    	return request(super.createRequestPageTempletResponse("/admin/callcenter/peer/code"));
    }
	
	@RequestMapping(value = "/router/code/update")
    @Menu(type = "callcenter" , subtype = "extention" , access = false , admin = true)
    public ModelAndView routercodeupdate(ModelMap map , HttpServletRequest request , @Valid RouterRules router) {
		if(!StringUtils.isBlank(router.getId())){
			RouterRules oldRouter = routerRulesRes.findByIdAndOrgi(router.getId(), super.getOrgi(request)) ;
			if(!StringUtils.isBlank(router.getRoutercontent())){
				oldRouter.setRoutercontent(router.getRoutercontent());
				routerRulesRes.save(oldRouter);
			}
		}
		return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/peer.html?hostid="+router.getHostid()));
    }
	
	@RequestMapping(value = "/router/delete")
    @Menu(type = "callcenter" , subtype = "extention" , access = false , admin = true)
    public ModelAndView extentiondelete(ModelMap map , HttpServletRequest request , @Valid String id , @Valid String hostid) {
		if(!StringUtils.isBlank(id)){
			routerRulesRes.delete(id);
		}
		return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/peer.html?hostid="+hostid));
    }
}
