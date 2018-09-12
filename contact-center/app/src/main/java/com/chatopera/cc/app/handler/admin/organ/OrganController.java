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
package com.chatopera.cc.app.handler.admin.organ;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.app.cache.CacheHelper;
import com.chatopera.cc.app.persistence.repository.OrganRepository;
import com.chatopera.cc.app.persistence.repository.SysDicRepository;
import com.chatopera.cc.util.OnlineUserUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.chatopera.cc.app.persistence.repository.AreaTypeRepository;
import com.chatopera.cc.app.persistence.repository.OrganRoleRepository;
import com.chatopera.cc.app.persistence.repository.RoleRepository;
import com.chatopera.cc.app.persistence.repository.UserRepository;
import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.model.AgentStatus;
import com.chatopera.cc.app.model.Organ;
import com.chatopera.cc.app.model.OrganRole;
import com.chatopera.cc.app.model.SysDic;
import com.chatopera.cc.app.model.UKeFuDic;
import com.chatopera.cc.app.model.User;

/**
 *
 * @author 程序猿DD
 * @version 1.0.0
 * @blog http://blog.didispace.com
 *
 */
@Controller
@RequestMapping("/admin/organ")
public class OrganController extends Handler{
	
	@Autowired
	private OrganRepository organRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private SysDicRepository sysDicRepository;
	
	@Autowired
	private AreaTypeRepository areaRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrganRoleRepository organRoleRes ;
	
	@Autowired
	private SysDicRepository sysDicRes; 

    @RequestMapping("/index")
    @Menu(type = "admin" , subtype = "organ")
    public ModelAndView index(ModelMap map , HttpServletRequest request , @Valid String organ, @Valid String msg) {
    	List<Organ> organList = organRepository.findByOrgiAndOrgid(super.getOrgiByTenantshare(request),super.getOrgid(request)) ;
    	map.addAttribute("organList", organList);
    	if(organList.size() > 0){
    		Organ organData = null ;
    		if(!StringUtils.isBlank(organ) && !"null".equals(organ)){
    			for(Organ data : organList){
    				if(data.getId().equals(organ)){
    					map.addAttribute("organData", data);
    					organData = data;
    				}
    			}
    		}else{
    			map.addAttribute("organData", organData = organList.get(0));
    		}
    		if(organData!=null){
    			map.addAttribute("userList", userRepository.findByOrganAndOrgiAndDatastatus(organData.getId() , super.getOrgiByTenantshare(request),false));
    		}
    	}
    	map.addAttribute("areaList", areaRepository.findByOrgi(super.getOrgiByTenantshare(request))) ;
    	map.addAttribute("roleList", roleRepository.findByOrgiAndOrgid(super.getOrgiByTenantshare(request),super.getOrgid(request)));
        map.put("msg", msg);
    	return request(super.createAdminTempletResponse("/admin/organ/index"));
    }
    
    @RequestMapping("/add")
    @Menu(type = "admin" , subtype = "organ")
    public ModelAndView add(ModelMap map , HttpServletRequest request , @Valid String parent, @Valid String area) {
    	map.addAttribute("areaList", areaRepository.findByOrgi(super.getOrgiByTenantshare(request))) ;
    	if(!StringUtils.isBlank(parent)){
    		map.addAttribute("organ", organRepository.findByIdAndOrgi(parent, super.getOrgiByTenantshare(request))) ;
    	}
    	if(!StringUtils.isBlank(area)){
    		map.addAttribute("area", areaRepository.findByIdAndOrgi(area, super.getOrgiByTenantshare(request))) ;
    	}
    	
    	map.addAttribute("organList", organRepository.findByOrgiAndOrgid(super.getOrgiByTenantshare(request),super.getOrgid(request)));
    	
        return request(super.createRequestPageTempletResponse("/admin/organ/add"));
    }
    
    @RequestMapping("/save")
    @Menu(type = "admin" , subtype = "organ")
    public ModelAndView save(HttpServletRequest request ,@Valid Organ organ) {
    	Organ tempOrgan = organRepository.findByNameAndOrgiAndOrgid(organ.getName(), super.getOrgiByTenantshare(request),super.getOrgid(request)) ;
    	String msg = "admin_organ_save_success" ;
    	String firstId = null;
    	if(tempOrgan != null){
    		msg =  "admin_organ_update_name_not";//分类名字重复
    	}else{
    		organ.setOrgi(super.getOrgiByTenantshare(request));
    		
    		if(!StringUtils.isBlank(super.getUser(request).getOrgid())) {
    			organ.setOrgid(super.getUser(request).getOrgid());
    		}else {
    			organ.setOrgid(MainContext.SYSTEM_ORGI);
    		}
    		firstId = organ.getId();
    		
    		organRepository.save(organ) ;
    		
    		OnlineUserUtils.clean(super.getOrgi(request));
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/admin/organ/index.html?msg="+msg+"&organ="+firstId));
    }
    
    @RequestMapping("/seluser")
    @Menu(type = "admin" , subtype = "seluser" , admin = true)
    public ModelAndView seluser(ModelMap map , HttpServletRequest request , @Valid String organ) {
    	map.addAttribute("userList", userRepository.findByOrgiAndDatastatusAndOrgid(super.getOrgiByTenantshare(request) , false,super.getOrgid(request))) ;
    	Organ organData = organRepository.findByIdAndOrgi(organ, super.getOrgiByTenantshare(request)) ;
    	map.addAttribute("userOrganList", userRepository.findByOrganAndOrgiAndDatastatus(organ, super.getOrgiByTenantshare(request),false)) ;
    	map.addAttribute("organ", organData) ;
        return request(super.createRequestPageTempletResponse("/admin/organ/seluser"));
    }
    
    
    @RequestMapping("/saveuser")
    @Menu(type = "admin" , subtype = "saveuser" , admin = true)
    public ModelAndView saveuser(HttpServletRequest request ,@Valid String[] users , @Valid String organ) {
    	List<String> userList = new ArrayList<String>();
    	if(users!=null && users.length > 0){
	    	for(String user : users){
	    		userList.add(user) ;
	    	}
	    	Organ organData = organRepository.findByIdAndOrgi(organ, super.getOrgiByTenantshare(request)) ;
	    	List<User> organUserList = userRepository.findAll(userList) ;
	    	for(User user : organUserList){
	    		user.setOrgan(organ);
	    		/**
	    		 * 以下更新技能组状态
	    		 */
	    		AgentStatus agentStatus = (AgentStatus) CacheHelper.getAgentStatusCacheBean().getCacheObject(user.getId(), super.getOrgiByTenantshare(request)) ;
	    		if(agentStatus!=null){
		    		agentStatus.setSkill(organ);
		    		agentStatus.setSkillname(organData.getName());
		    		CacheHelper.getAgentStatusCacheBean().delete(user.getId(), user.getOrgi());
		    		CacheHelper.getAgentStatusCacheBean().put(user.getId(), agentStatus, super.getOrgiByTenantshare(request));
	    		}
	    	}
	    	userRepository.save(organUserList) ;
	    	OnlineUserUtils.clean(super.getOrgi(request));
    	}
    	
    	return request(super.createRequestPageTempletResponse("redirect:/admin/organ/index.html?organ="+organ));
    }
    
    @RequestMapping("/user/delete")
    @Menu(type = "admin" , subtype = "role")
    public ModelAndView userroledelete(HttpServletRequest request ,@Valid String id , @Valid String organ) {
    	if(id!=null){
	    	User user= userRepository.getOne(id) ;
	    	user.setOrgan(null);
	    	userRepository.save(user) ;
	    	OnlineUserUtils.clean(super.getOrgi(request));
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/admin/organ/index.html?organ="+organ));
    }
    
    @RequestMapping("/edit")
    @Menu(type = "admin" , subtype = "organ")
    public ModelAndView edit(ModelMap map ,HttpServletRequest request , @Valid String id) {
    	ModelAndView view = request(super.createRequestPageTempletResponse("/admin/organ/edit")) ;
    	map.addAttribute("areaList", areaRepository.findByOrgi(super.getOrgiByTenantshare(request))) ;
    	view.addObject("organData", organRepository.findByIdAndOrgi(id, super.getOrgiByTenantshare(request))) ;
    	
    	map.addAttribute("organList", organRepository.findByOrgiAndOrgid(super.getOrgiByTenantshare(request),super.getOrgid(request)));
        return view;
    }
    
    @RequestMapping("/update")
    @Menu(type = "admin" , subtype = "organ")
    public ModelAndView update(HttpServletRequest request ,@Valid Organ organ) {
    	Organ org = organRepository.findByNameAndOrgi(organ.getName(), super.getOrgi(request));
    	String msg = "admin_organ_update_success" ;
    	
    	if(org != null){
    		if(org.getId().equals(organ.getId())){
    			Organ tempOrgan = organRepository.findByIdAndOrgi(organ.getId(), super.getOrgiByTenantshare(request)) ;
            	
            	if(tempOrgan != null){
            		tempOrgan.setName(organ.getName());
            		tempOrgan.setUpdatetime(new Date());
            		tempOrgan.setOrgi(super.getOrgiByTenantshare(request));
            		tempOrgan.setSkill(organ.isSkill());
            		
            		tempOrgan.setParent(organ.getParent());
            		
            		tempOrgan.setArea(organ.getArea());
            		
            		if(!StringUtils.isBlank(super.getUser(request).getOrgid())) {
            			tempOrgan.setOrgid(super.getUser(request).getOrgid());
            		}else {
            			tempOrgan.setOrgid(MainContext.SYSTEM_ORGI);
            		}
            		organRepository.save(tempOrgan) ;
            		OnlineUserUtils.clean(super.getOrgi(request));
            	}else{
            		msg =  "admin_organ_update_not_exist";//修改失败
            	}
    		}else{
        		msg = "admin_organ_update_name_not" ;//分类名字重复
        	}
    	}else{
    		Organ tempOrgan = organRepository.findByIdAndOrgi(organ.getId(), super.getOrgiByTenantshare(request)) ;
        	
        	if(tempOrgan != null){
        		tempOrgan.setName(organ.getName());
        		tempOrgan.setUpdatetime(new Date());
        		tempOrgan.setOrgi(super.getOrgiByTenantshare(request));
        		tempOrgan.setSkill(organ.isSkill());
        		
        		tempOrgan.setParent(organ.getParent());
        		
        		tempOrgan.setArea(organ.getArea());
        		
        		if(!StringUtils.isBlank(super.getUser(request).getOrgid())) {
        			tempOrgan.setOrgid(super.getUser(request).getOrgid());
        		}else {
        			tempOrgan.setOrgid(MainContext.SYSTEM_ORGI);
        		}
        		organRepository.save(tempOrgan) ;
        		OnlineUserUtils.clean(super.getOrgi(request));
        	}else{
        		msg =  "admin_organ_update_not_exist";//修改失败
        	}
    	}
    	
    	return request(super.createRequestPageTempletResponse("redirect:/admin/organ/index.html?msg="+msg+"&organ="+organ.getId()));
    }
    
    @RequestMapping("/area")
    @Menu(type = "admin" , subtype = "area")
    public ModelAndView area(ModelMap map ,HttpServletRequest request , @Valid String id) {
    	
    	SysDic sysDic = sysDicRepository.findByCode(MainContext.UKEFU_SYSTEM_AREA_DIC) ;
    	if(sysDic!=null){
	    	map.addAttribute("sysarea", sysDic) ;
	    	map.addAttribute("areaList", sysDicRepository.findByDicid(sysDic.getId())) ;
    	}
    	map.addAttribute("cacheList", UKeFuDic.getInstance().getDic(MainContext.UKEFU_SYSTEM_AREA_DIC)) ;
    	
    	map.addAttribute("organData", organRepository.findByIdAndOrgi(id, super.getOrgiByTenantshare(request))) ;
        return request(super.createRequestPageTempletResponse("/admin/organ/area"));
    }
    
    
    @RequestMapping("/area/update")
    @Menu(type = "admin" , subtype = "organ")
    public ModelAndView areaupdate(HttpServletRequest request ,@Valid Organ organ) {
    	Organ tempOrgan = organRepository.findByIdAndOrgi(organ.getId(), super.getOrgiByTenantshare(request)) ;
    	String msg = "admin_organ_update_success" ;
    	if(tempOrgan != null){
    		tempOrgan.setArea(organ.getArea());
    		organRepository.save(tempOrgan) ;
    		OnlineUserUtils.clean(super.getOrgi(request));
    	}else{
    		msg =  "admin_organ_update_not_exist";
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/admin/organ/index.html?msg="+msg+"&organ="+organ.getId()));
    }
    
    @RequestMapping("/delete")
    @Menu(type = "admin" , subtype = "organ")
    public ModelAndView delete(HttpServletRequest request ,@Valid Organ organ) {
    	String msg = "admin_organ_delete" ;
    	if(organ!=null){
	    	organRepository.delete(organ);
	    	OnlineUserUtils.clean(super.getOrgi(request));
    	}else{
    		msg = "admin_organ_not_exist" ;
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/admin/organ/index.html?msg="+msg));
    }

    @RequestMapping("/auth/save")
    @Menu(type = "admin" , subtype = "role")
    public ModelAndView authsave(HttpServletRequest request ,@Valid String id ,@Valid String menus) {
    	Organ organData = organRepository.findByIdAndOrgi(id, super.getOrgiByTenantshare(request)) ;
    	List<OrganRole>  organRoleList = organRoleRes.findByOrgiAndOrgan(super.getOrgiByTenantshare(request), organData) ;
    	organRoleRes.delete(organRoleList);
    	if(!StringUtils.isBlank(menus)){
    		String[] menusarray = menus.split(",") ;
    		for(String menu : menusarray){
    			OrganRole organRole = new OrganRole();
    			SysDic sysDic = UKeFuDic.getInstance().getDicItem(menu) ;
    			if(sysDic!=null && !"0".equals(sysDic.getParentid())) {
    				organRole.setDicid(menu);
        			organRole.setDicvalue(sysDic.getCode());
        			
        			organRole.setOrgan(organData);
        			organRole.setCreater(super.getUser(request).getId());
        			organRole.setOrgi(super.getOrgiByTenantshare(request));
        			organRole.setCreatetime(new Date());
        			organRoleRes.save(organRole) ;
    			}
    			
    		}
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/admin/organ/index.html?organ="+organData.getId()));
    }
}