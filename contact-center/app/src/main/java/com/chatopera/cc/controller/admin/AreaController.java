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

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.AreaType;
import com.chatopera.cc.model.Dict;
import com.chatopera.cc.model.SysDic;
import com.chatopera.cc.persistence.repository.AreaTypeRepository;
import com.chatopera.cc.persistence.repository.SysDicRepository;
import com.chatopera.cc.util.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;

/**
 *
 * @author 程序猿DD
 * @version 1.0.0
 * @blog http://blog.didispace.com
 *
 */
@Controller
@RequestMapping("/admin/area")
public class AreaController extends Handler{
	
	@Autowired
	private AreaTypeRepository areaRepository;
	
	@Autowired
	private SysDicRepository sysDicRepository;

    @RequestMapping("/index")
    @Menu(type = "admin" , subtype = "area")
    public ModelAndView index(ModelMap map , HttpServletRequest request) throws IOException {
    	map.addAttribute("areaList", areaRepository.findByOrgi(super.getOrgi(request)));
    	return request(super.createAdminTempletResponse("/admin/area/index"));
    }
    
    @RequestMapping("/add")
    @Menu(type = "admin" , subtype = "area")
    public ModelAndView add(ModelMap map , HttpServletRequest request) {
    	SysDic sysDic = sysDicRepository.findByCode(Constants.CSKEFU_SYSTEM_AREA_DIC) ;
    	if(sysDic!=null){
	    	map.addAttribute("sysarea", sysDic) ;
	    	map.addAttribute("areaList", sysDicRepository.findByDicid(sysDic.getId())) ;
    	}
    	map.addAttribute("cacheList", Dict.getInstance().getDic(Constants.CSKEFU_SYSTEM_AREA_DIC)) ;
        return request(super.createRequestPageTempletResponse("/admin/area/add"));
    }
    
    @RequestMapping("/save")
    @Menu(type = "admin" , subtype = "area")
    public ModelAndView save(HttpServletRequest request ,@Valid AreaType area) {
    	int areas = areaRepository.countByNameAndOrgi(area.getName(), super.getOrgi(request)) ;
    	if(areas == 0){
    		area.setOrgi(super.getOrgi(request));
    		area.setCreatetime(new Date());
    		area.setCreater(super.getUser(request).getId());
    		areaRepository.save(area) ;
    		MainUtils.initSystemArea();
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/admin/area/index.html"));
    }
    
    @RequestMapping("/edit")
    @Menu(type = "admin" , subtype = "area")
    public ModelAndView edit(ModelMap map ,HttpServletRequest request , @Valid String id) {
    	map.addAttribute("area", areaRepository.findByIdAndOrgi(id, super.getOrgi(request))) ;
    	
    	SysDic sysDic = sysDicRepository.findByCode(Constants.CSKEFU_SYSTEM_AREA_DIC) ;
    	if(sysDic!=null){
	    	map.addAttribute("sysarea", sysDic) ;
	    	map.addAttribute("areaList", sysDicRepository.findByDicid(sysDic.getId())) ;
    	}
    	map.addAttribute("cacheList", Dict.getInstance().getDic(Constants.CSKEFU_SYSTEM_AREA_DIC)) ;
        return request(super.createRequestPageTempletResponse("/admin/area/edit"));
    }
    
    @RequestMapping("/update")
    @Menu(type = "admin" , subtype = "area" , admin = true)
    public ModelAndView update(HttpServletRequest request ,@Valid AreaType area) {
    	AreaType areaType = areaRepository.findByIdAndOrgi(area.getId(), super.getOrgi(request)) ;
    	if(areaType != null){
    		area.setCreatetime(areaType.getCreatetime());
    		area.setOrgi(super.getOrgi(request));
    		area.setCreater(areaType.getCreater());
    		areaRepository.save(area) ;
    		MainUtils.initSystemArea();
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/admin/area/index.html"));
    }
    
    @RequestMapping("/delete")
    @Menu(type = "admin" , subtype = "area")
    public ModelAndView delete(HttpServletRequest request ,@Valid AreaType area) {
    	AreaType areaType = areaRepository.findByIdAndOrgi(area.getId(), super.getOrgi(request)) ;
    	if(areaType!=null){
    		areaRepository.delete(areaType);
    		MainUtils.initSystemArea();
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/admin/area/index.html"));
    }
}