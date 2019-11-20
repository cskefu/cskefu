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
package com.chatopera.cc.controller.admin;

import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.Skill;
import com.chatopera.cc.persistence.repository.SkillRepository;
import com.chatopera.cc.util.Menu;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 *
 * @author 程序猿DD
 * @version 1.0.0
 * @blog http://blog.didispace.com
 *
 */
@Controller
@RequestMapping("/admin/skill")
public class AgentSkillController extends Handler{
	
	@Autowired
	private SkillRepository skillRepository;

    @RequestMapping("/index")
    @Menu(type = "admin" , subtype = "skill")
    public ModelAndView index(ModelMap map , HttpServletRequest request , @Valid String skill) {
    	List<Skill> skillGroups = skillRepository.findAll() ;
    	map.addAttribute("skillGroups", skillGroups);
    	if(skillGroups.size() > 0){
    		if(!StringUtils.isBlank(skill)){
    			for(Skill data : skillGroups){
    				if(data.getId().equals(skill)){
    					map.addAttribute("skillData", data);
    				}
    			}
    		}else{
    			map.addAttribute("skillData", skillGroups.get(0));
    		}
    		
//    		map.addAttribute("userList", userRepository.findBySkill(skill));
    	}
        return request(super.createAdminTempletResponse("/admin/skill/index"));
    }
    
    @RequestMapping("/add")
    @Menu(type = "admin" , subtype = "skill")
    public ModelAndView add(ModelMap map , HttpServletRequest request) {
        return request(super.createRequestPageTempletResponse("/admin/skill/add"));
    }
    
    @RequestMapping("/save")
    @Menu(type = "admin" , subtype = "skill")
    public ModelAndView save(HttpServletRequest request ,@Valid Skill skill) {
    	Skill tempSkill = skillRepository.findByNameAndOrgi(skill.getName() , super.getOrgi(request)) ;
    	String msg = "admin_skill_save_success" ;
    	if(tempSkill != null){
    		msg =  "admin_skill_save_exist";
    	}else{
    		skillRepository.save(skill) ;
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/admin/skill/index.html?msg="+msg));
    }
    
    @RequestMapping("/edit")
    @Menu(type = "admin" , subtype = "skill")
    public ModelAndView edit(ModelMap map ,HttpServletRequest request ,  @Valid String id) {
    	ModelAndView view = request(super.createRequestPageTempletResponse("/admin/skill/edit")) ;
    	view.addObject("skillData", skillRepository.findByIdAndOrgi(id , super.getOrgi(request))) ;
        return view;
    }
    
    @RequestMapping("/update")
    @Menu(type = "admin" , subtype = "skill")
    public ModelAndView update(HttpServletRequest request ,@Valid Skill skill) {
    	Skill tempSkill = skillRepository.findByIdAndOrgi(skill.getId() , super.getOrgi(request)) ;
    	String msg = "admin_skill_update_success" ;
    	if(tempSkill != null){
    		tempSkill.setName(skill.getName());
    		tempSkill.setUpdatetime(new Date());
    		skillRepository.save(tempSkill) ;
    	}else{
    		msg =  "admin_skill_update_not_exist";
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/admin/skill/index.html?msg="+msg));
    }
    
    @RequestMapping("/delete")
    @Menu(type = "admin" , subtype = "skill")
    public ModelAndView delete(HttpServletRequest request ,@Valid Skill skill) {
    	String msg = "admin_skill_delete" ;
    	if(skill!=null){
	    	skillRepository.delete(skill);
    	}else{
    		msg = "admin_skill_not_exist" ;
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/admin/skill/index.html?msg="+msg));
    }
}