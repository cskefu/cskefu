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
package com.chatopera.cc.app.handler.resource;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.chatopera.cc.util.Menu;
import com.chatopera.cc.app.persistence.repository.OrganRepository;
import com.chatopera.cc.app.persistence.repository.OrgiSkillRelRepository;
import com.chatopera.cc.app.model.OrgiSkillRel;
import com.chatopera.cc.app.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.chatopera.cc.app.persistence.repository.UserRepository;
import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.model.Organ;

@Controller
@RequestMapping("/res")
public class UsersResourceController extends Handler {
	@Autowired
	private UserRepository userRes ;
	
	@Autowired
	private OrgiSkillRelRepository orgiSkillRelService;
	
	@Autowired
	private OrganRepository organRes ;
	
	@RequestMapping("/users")
    @Menu(type = "res" , subtype = "users")
    public ModelAndView add(ModelMap map , HttpServletRequest request , @Valid String q , @Valid String id) {
		if(q==null){
			q = "" ;
		}
    	map.addAttribute("usersList",getUsers(request, q)) ;
        return request(super.createRequestPageTempletResponse("/public/users"));
    }
	
	@RequestMapping("/bpm/users")
    @Menu(type = "res" , subtype = "users")
    public ModelAndView bpmusers(ModelMap map , HttpServletRequest request , @Valid String q , @Valid String id) {
		if(q==null){
			q = "" ;
		}
		map.addAttribute("usersList", getUsers(request, q)) ;
        return request(super.createRequestPageTempletResponse("/public/bpmusers"));
    }
	
	@RequestMapping("/bpm/organ")
    @Menu(type = "res" , subtype = "users")
    public ModelAndView organ(ModelMap map , HttpServletRequest request , @Valid String q , @Valid String ids) {
    	map.addAttribute("organList", getOrgans(request)) ;
    	map.addAttribute("usersList", getUsers(request)) ;
    	map.addAttribute("ids", ids) ;
        return request(super.createRequestPageTempletResponse("/public/organ"));
    }
	private List<User> getUsers(HttpServletRequest request){
		List<User> list = null;
    	if(super.isTenantshare()) {
			List<String> organIdList = new ArrayList<>();
			List<OrgiSkillRel> orgiSkillRelList = orgiSkillRelService.findByOrgi(super.getOrgi(request)) ;
			if(!orgiSkillRelList.isEmpty()) {
				for(OrgiSkillRel rel:orgiSkillRelList) {
					organIdList.add(rel.getSkillid());
				}
			}
			list = userRes.findByOrganInAndDatastatus(organIdList,false);
		}else {
			list = userRes.findByOrgiAndDatastatus(super.getOrgi(request),false) ;
		}
    	return list;
    }
	/**
	 * 获取当前产品下人员信息
	 * @param request
	 * @param q
	 * @return
	 */
	private Page<User> getUsers(HttpServletRequest request,String q){
		if(q==null){
			q = "" ;
		}
		Page<User> list = null;
    	if(super.isTenantshare()) {
			List<String> organIdList = new ArrayList<>();
			List<OrgiSkillRel> orgiSkillRelList = orgiSkillRelService.findByOrgi(super.getOrgi(request)) ;
			if(!orgiSkillRelList.isEmpty()) {
				for(OrgiSkillRel rel:orgiSkillRelList) {
					organIdList.add(rel.getSkillid());
				}
			}
			list = userRes.findByOrganInAndDatastatusAndUsernameLike(organIdList,false, "%"+q+"%", new PageRequest(0, 10) );
		}else {
			list = userRes.findByDatastatusAndOrgiAndOrgidAndUsernameLike(false,super.getOrgi(request),super.getOrgid(request), "%"+q+"%" , new PageRequest(0, 10)) ;
		}
    	return list;
    }
	/**
	 * 获取当前产品下 技能组 组织信息
	 * @param request
	 * @return
	 */
	private List<Organ> getOrgans(HttpServletRequest request){
    	List<Organ> list = null;
    	if(super.isTenantshare()) {
			List<String> organIdList = new ArrayList<>();
			List<OrgiSkillRel> orgiSkillRelList = orgiSkillRelService.findByOrgi(super.getOrgi(request)) ;
			if(!orgiSkillRelList.isEmpty()) {
				for(OrgiSkillRel rel:orgiSkillRelList) {
					organIdList.add(rel.getSkillid());
				}
			}
			list = organRes.findByIdInAndSkill(organIdList,true);
		}else {
			list = organRes.findByOrgiAndSkillAndOrgid(super.getOrgi(request),true,super.getOrgid(request)) ;
		}
    	return list;
    }
}
