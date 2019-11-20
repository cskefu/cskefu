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
import com.chatopera.cc.model.Extention;
import com.chatopera.cc.persistence.repository.ExtentionRepository;
import com.chatopera.cc.persistence.repository.IvrMenuRepository;
import com.chatopera.cc.persistence.repository.PbxHostRepository;
import com.chatopera.cc.util.Menu;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("/admin/callcenter")
public class CallCenterIvrController extends Handler{
	
	@Autowired
	private PbxHostRepository pbxHostRes ;
	
	@Autowired
	private ExtentionRepository extentionRes ;
	
	@Autowired
	private IvrMenuRepository ivrMenuRes;
	
	@RequestMapping(value = "/ivr")
    @Menu(type = "callcenter" , subtype = "callcenterivr" , access = false , admin = true)
    public ModelAndView ivr(ModelMap map , HttpServletRequest request , @Valid String hostid) {
		if(!StringUtils.isBlank(hostid)){
			map.addAttribute("pbxHost" , pbxHostRes.findByIdAndOrgi(hostid, super.getOrgi(request)));
			map.addAttribute("ivrList" , extentionRes.findByExtypeAndOrgi("ivr", super.getOrgi(request)));
		}
		return request(super.createRequestPageTempletResponse("/admin/callcenter/ivr/index"));
    }
	
	@RequestMapping(value = "/ivr/edit")
    @Menu(type = "callcenter" , subtype = "extention" , access = false , admin = true)
    public ModelAndView extentionedit(ModelMap map , HttpServletRequest request , @Valid String id , @Valid String hostid) {
		map.addAttribute("extention" , extentionRes.findByIdAndOrgi(id, super.getOrgi(request)));
		map.put("pbxHost", pbxHostRes.findByIdAndOrgi(hostid, super.getOrgi(request))) ;
    	return request(super.createRequestPageTempletResponse("/admin/callcenter/ivr/edit"));
    }
	
	@RequestMapping(value = "/ivr/update")
    @Menu(type = "callcenter" , subtype = "extention" , access = false , admin = true)
    public ModelAndView extentionupdate(ModelMap map , HttpServletRequest request , @Valid Extention extention) {
		if(!StringUtils.isBlank(extention.getId())){
			Extention ext = extentionRes.findByIdAndOrgi(extention.getId(), super.getOrgi(request)) ;
			ext.setExtention(extention.getExtention());
			ext.setDescription(extention.getDescription());
			extentionRes.save(ext) ;
		}
		return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/ivr.html?hostid="+extention.getHostid()));
    }
	

	@RequestMapping(value = "/ivr/delete")
    @Menu(type = "callcenter" , subtype = "ivr" , access = false , admin = true)
    public ModelAndView extentiondelete(ModelMap map , HttpServletRequest request , @Valid String id , @Valid String hostid) {
		if(!StringUtils.isBlank(id)){
			extentionRes.delete(id);
		}
		return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/ivr.html?hostid="+hostid));
    }
	
	@RequestMapping(value = "/ivr/design")
    @Menu(type = "callcenter" , subtype = "callcenterivr" , access = false , admin = true)
    public ModelAndView design(ModelMap map , HttpServletRequest request , @Valid String hostid , @Valid String id) {
		if(!StringUtils.isBlank(hostid)){
			map.addAttribute("extention" , extentionRes.findByIdAndOrgi(id, super.getOrgi(request)));
			map.addAttribute("ivrMenuList" , ivrMenuRes.findByExtentionidAndHostidAndOrgi(id, hostid, super.getOrgi(request)));
			map.addAttribute("pbxHost" , pbxHostRes.findByIdAndOrgi(hostid, super.getOrgi(request)));
		}
		return request(super.createRequestPageTempletResponse("/admin/callcenter/ivr/design"));
    }
	
	@RequestMapping(value = "/ivr/menu/add")
    @Menu(type = "callcenter" , subtype = "callcenterivr" , access = false , admin = true)
    public ModelAndView ivrmenuadd(ModelMap map , HttpServletRequest request , @Valid String id , @Valid String hostid , @Valid String parentid) {
		map.addAttribute("extention" , extentionRes.findByIdAndOrgi(id, super.getOrgi(request)));
		map.put("pbxHost", pbxHostRes.findByIdAndOrgi(hostid, super.getOrgi(request))) ;
    	return request(super.createRequestPageTempletResponse("/admin/callcenter/ivr/menuadd"));
    }
}
