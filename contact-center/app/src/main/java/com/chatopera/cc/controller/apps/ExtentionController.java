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

import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.Extention;
import com.chatopera.cc.model.PbxHost;
import com.chatopera.cc.model.SystemConfig;
import com.chatopera.cc.model.Template;
import com.chatopera.cc.persistence.repository.*;
import com.chatopera.cc.util.Menu;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/apps/callcenter")
public class ExtentionController extends Handler{
	
	@Autowired
	private PbxHostRepository pbxHostRes ;
	
	@Autowired
	private ExtentionRepository extentionRes;
	
	@Autowired
	private AclRepository aclRes;
	
	@Autowired
	private RouterRulesRepository routerRes;
	
	@Autowired
	private SkillExtentionRepository skillExtentionRes ;
	
	@Autowired
	private CallCenterSkillRepository skillRes ;
	
	@Autowired
	private SipTrunkRepository sipTrunkRes ;
	
	@RequestMapping(value = "/extention")
    @Menu(type = "callcenter" , subtype = "extention" , access = true)
    public ModelAndView index(ModelMap map , HttpServletRequest request , @Valid String hostname , @Valid String key_value) {
		ModelAndView view = request(super.createRequestPageTempletResponse("/apps/business/callcenter/extention/index")) ;
		List<PbxHost> pbxHostList = pbxHostRes.findByHostnameOrIpaddr(hostname, hostname) ;
		PbxHost pbxHost = null ;
		SystemConfig systemConfig = MainUtils.getSystemConfig() ;
		if(pbxHostList!=null && pbxHostList.size() > 0){
			pbxHost = pbxHostList.get(0) ;
			map.addAttribute("pbxHost" , pbxHost);
			map.addAttribute("skillGroups" , skillRes.findByHostidAndOrgi(pbxHost.getId() , super.getOrgi(request)));
			map.addAttribute("extentionList" , extentionRes.findByHostidAndOrgi(pbxHost.getId() , super.getOrgi(request)));
		}
		if(systemConfig!=null && systemConfig.isCallcenter()){
			if(!StringUtils.isBlank(systemConfig.getCc_extention())){
				Template template = MainUtils.getTemplate(systemConfig.getCc_extention()) ;
				if(template!=null){
					map.addAttribute("template" , template);
					view = request(super.createRequestPageTempletResponse("/apps/business/callcenter/template")) ;
				}
			}
		}
    	return view ;
    }
	
	@RequestMapping(value = "/configuration")
    @Menu(type = "callcenter" , subtype = "configuration" , access = true)
    public ModelAndView configuration(ModelMap map , HttpServletRequest request , @Valid String hostname , @Valid String key_value ,  @Valid String profile) {
		
		List<PbxHost> pbxHostList = pbxHostRes.findByHostnameOrIpaddr(hostname, hostname) ;
		PbxHost pbxHost = null ;
		SystemConfig systemConfig = MainUtils.getSystemConfig() ;
		if(pbxHostList!=null && pbxHostList.size() > 0){
			pbxHost = pbxHostList.get(0) ;
			map.addAttribute("pbxHost" , pbxHost);
			map.addAttribute("skillGroups" , skillRes.findByHostidAndOrgi(pbxHost.getId() , super.getOrgi(request)));
			map.addAttribute("skillExtentionList" , skillExtentionRes.findByHostidAndOrgi(pbxHost.getId() , super.getOrgi(request)));
			map.addAttribute("extentionList" , extentionRes.findByHostidAndOrgi(pbxHost.getId() , super.getOrgi(request)));
			map.addAttribute("aclList" , aclRes.findByHostidAndOrgi(pbxHost.getId() , super.getOrgi(request)));
			map.addAttribute("sipTrunkList" , sipTrunkRes.findByHostidAndOrgi(pbxHost.getId() , super.getOrgi(request)));
		}
		Template template = null ;
		ModelAndView view = request(super.createRequestPageTempletResponse("/apps/business/callcenter/notfound"));
		if(key_value!=null && key_value.equals("callcenter.conf")){
			view = request(super.createRequestPageTempletResponse("/apps/business/callcenter/configure/callcenter"));
			if(systemConfig!=null && systemConfig.isCallcenter()){
				if(!StringUtils.isBlank(systemConfig.getCc_quene())){
					template = MainUtils.getTemplate(systemConfig.getCc_quene()) ;
				}
			}
		}else if(key_value!=null && key_value.equals("acl.conf")){
			view = request(super.createRequestPageTempletResponse("/apps/business/callcenter/configure/acl"));
			if(systemConfig!=null && systemConfig.isCallcenter()){
				if(!StringUtils.isBlank(systemConfig.getCc_acl())){
					template = MainUtils.getTemplate(systemConfig.getCc_acl()) ;
				}
			}
		}else if(key_value!=null && key_value.equals("ivr.conf")){
			view = request(super.createRequestPageTempletResponse("/apps/business/callcenter/configure/ivr"));
			if(systemConfig!=null && systemConfig.isCallcenter()){
				if(!StringUtils.isBlank(systemConfig.getCc_ivr())){
					template = MainUtils.getTemplate(systemConfig.getCc_ivr()) ;
				}
			}
		}
		if(template!=null){
			map.addAttribute("template" , template);
			view = request(super.createRequestPageTempletResponse("/apps/business/callcenter/template")) ;
		}
    	return view;
    }
	
	@RequestMapping(value = "/dialplan")
    @Menu(type = "callcenter" , subtype = "dialplan" , access = true)
    public ModelAndView dialplan(ModelMap map , HttpServletRequest request , @Valid String hostname , @Valid String key_value) {
		ModelAndView view = request(super.createRequestPageTempletResponse("/apps/business/callcenter/dialplan/index"));
		List<PbxHost> pbxHostList = pbxHostRes.findByHostnameOrIpaddr(hostname, hostname) ;
		PbxHost pbxHost = null ;
		SystemConfig systemConfig = MainUtils.getSystemConfig() ;
		Template template = null ;
		if(pbxHostList!=null && pbxHostList.size() > 0){
			pbxHost = pbxHostList.get(0) ;
			map.addAttribute("pbxHost" , pbxHost);
			map.addAttribute("routerList" , routerRes.findByHostidAndOrgi(pbxHost.getId() , super.getOrgi(request)));
		}
		if(systemConfig!=null && systemConfig.isCallcenter()){
			if(!StringUtils.isBlank(systemConfig.getCc_siptrunk())){
				template = MainUtils.getTemplate(systemConfig.getCc_router()) ;
			}
			if(template!=null){
				map.addAttribute("template" , template);
				view = request(super.createRequestPageTempletResponse("/apps/business/callcenter/template")) ;
			}
		}
		
		return view;
    }
	
	@RequestMapping(value = "/extention/detail")
    @Menu(type = "callcenter" , subtype = "extention" , access = false)
    public ModelAndView detail(ModelMap map , HttpServletRequest request , HttpServletResponse response ,@Valid String extno) {
		List<Extention> extentionList = extentionRes.findByExtentionAndOrgi(extno, super.getOrgi(request)) ;
		if(extentionList!=null && extentionList.size() == 1){
			Extention extention = extentionList.get(0) ;
			if(!StringUtils.isBlank(extention.getHostid())) {
				PbxHost pbxHost = pbxHostRes.findById(extention.getHostid()) ;
				if(pbxHost!=null) {
					map.addAttribute("pbxhost" , pbxHost);
				}
			}
			map.addAttribute("extention" , extention);
		}
		response.setContentType("Content-type: text/json; charset=utf-8"); 
    	return request(super.createRequestPageTempletResponse("/apps/business/callcenter/extention/detail"));
    }
	
	@RequestMapping(value = "/ivr")
    @Menu(type = "callcenter" , subtype = "ivr" , access = false)
    public ModelAndView ivr(ModelMap map , HttpServletRequest request , HttpServletResponse response ,@Valid String hostid) {
		map.addAttribute("ivrList" , extentionRes.findByHostidAndExtypeAndOrgi(hostid, MainContext.ExtentionType.BUSINESS.toString() , super.getOrgi(request)));
    	return request(super.createRequestPageTempletResponse("/apps/business/callcenter/extention/ivr"));
    }
	
}
