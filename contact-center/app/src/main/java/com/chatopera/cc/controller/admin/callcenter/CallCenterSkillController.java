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
import com.chatopera.cc.model.CallCenterSkill;
import com.chatopera.cc.model.Extention;
import com.chatopera.cc.model.PbxHost;
import com.chatopera.cc.model.SkillExtention;
import com.chatopera.cc.persistence.repository.CallCenterSkillRepository;
import com.chatopera.cc.persistence.repository.ExtentionRepository;
import com.chatopera.cc.persistence.repository.PbxHostRepository;
import com.chatopera.cc.persistence.repository.SkillExtentionRepository;
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

@Controller
@RequestMapping("/admin/callcenter")
public class CallCenterSkillController extends Handler {
	
	@Autowired
	private PbxHostRepository pbxHostRes ;
	
	@Autowired
	private ExtentionRepository extentionRes;
	
	@Autowired
	private CallCenterSkillRepository skillRes ;
	
	@Autowired
	private SkillExtentionRepository skillExtentionRes;
	
	@RequestMapping(value = "/skill")
    @Menu(type = "callcenter" , subtype = "callcenterresource" , access = false , admin = true)
    public ModelAndView skill(ModelMap map , HttpServletRequest request , @Valid String hostid) {
		List<PbxHost> pbxHostList = pbxHostRes.findByOrgi(super.getOrgi(request)) ;
		map.addAttribute("pbxHostList" , pbxHostList);
		PbxHost pbxHost = null ;
		if(pbxHostList.size() > 0){
			map.addAttribute("pbxHost" , pbxHost = getPbxHost(pbxHostList, hostid));
			map.addAttribute("skillGroups" , skillRes.findByHostidAndOrgi(pbxHost.getId() , super.getOrgi(request)));
			map.addAttribute("skillExtentionList" , skillExtentionRes.findByHostidAndOrgi(hostid, super.getOrgi(request)));
		}
		return request(super.createRequestPageTempletResponse("/admin/callcenter/skill/index"));
    }
	
	private PbxHost getPbxHost(List<PbxHost> pbxHostList ,String hostid){
		PbxHost pbxHost = pbxHostList.get(0) ;
		if(!StringUtils.isBlank(hostid)){
			for(PbxHost pbx : pbxHostList){
				if(pbx.getId().equals(hostid)){
					pbxHost = pbx; break ;
				}
			}
		}
		return pbxHost ;
	}
	
	@RequestMapping(value = "/skill/add")
    @Menu(type = "callcenter" , subtype = "extention" , access = false , admin = true)
    public ModelAndView extentionadd(ModelMap map , HttpServletRequest request , @Valid String hostid) {
		map.put("pbxHost", pbxHostRes.findByIdAndOrgi(hostid, super.getOrgi(request))) ;
    	return request(super.createRequestPageTempletResponse("/admin/callcenter/skill/add"));
    }
	
	@RequestMapping(value = "/skill/save")
    @Menu(type = "callcenter" , subtype = "extention" , access = false , admin = true)
    public ModelAndView extentionsave(ModelMap map , HttpServletRequest request , @Valid CallCenterSkill skill) {
		if(!StringUtils.isBlank(skill.getSkill())){
			int count = skillRes.countBySkillAndOrgi(skill.getSkill(), super.getOrgi(request)) ;
			if(count == 0){
				skill.setOrgi(super.getOrgi(request));
				skill.setCreater(super.getUser(request).getId());
				skillRes.save(skill) ;
			}
		}
		return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/skill.html?hostid="+skill.getHostid()));
    }
	
	@RequestMapping(value = "/skill/edit")
    @Menu(type = "callcenter" , subtype = "extention" , access = false , admin = true)
    public ModelAndView extentionedit(ModelMap map , HttpServletRequest request , @Valid String id , @Valid String hostid) {
		map.addAttribute("extention" , extentionRes.findByIdAndOrgi(id, super.getOrgi(request)));
		map.put("pbxHost", pbxHostRes.findByIdAndOrgi(hostid, super.getOrgi(request))) ;
    	return request(super.createRequestPageTempletResponse("/admin/callcenter/skill/edit"));
    }
	
	@RequestMapping(value = "/skill/update")
    @Menu(type = "callcenter" , subtype = "extention" , access = false , admin = true)
    public ModelAndView pbxhostupdate(ModelMap map , HttpServletRequest request , @Valid Extention extention) {
		if(!StringUtils.isBlank(extention.getId())){
			Extention ext = extentionRes.findByIdAndOrgi(extention.getId(), super.getOrgi(request)) ;
			if(ext!=null && !StringUtils.isBlank(ext.getExtention()) && ext.getExtention().matches("[\\d]{3,8}")){
				ext.setExtention(extention.getExtention());
				if(!StringUtils.isBlank(extention.getPassword())){
					ext.setPassword(extention.getPassword());
				}
				ext.setUpdatetime(new Date());
				extentionRes.save(ext) ;
			}
		}
		return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/skill.html?hostid="+extention.getHostid()));
    }
	
	@RequestMapping(value = "/skill/delete")
    @Menu(type = "callcenter" , subtype = "extention" , access = false , admin = true)
    public ModelAndView extentiondelete(ModelMap map , HttpServletRequest request , @Valid String id , @Valid String hostid) {
		if(!StringUtils.isBlank(id)){
			extentionRes.delete(id);
		}
		return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/skill.html?hostid="+hostid));
    }
	
	@RequestMapping(value = "/skill/imp")
    @Menu(type = "callcenter" , subtype = "extention" , access = false , admin = true)
    public ModelAndView skillimp(ModelMap map , HttpServletRequest request , @Valid String hostid) {
		if(!StringUtils.isBlank(hostid)){
			map.put("pbxHost", pbxHostRes.findByIdAndOrgi(hostid, super.getOrgi(request))) ;
			map.put("extentionList", extentionRes.findByHostidAndOrgi(hostid, super.getOrgi(request))) ;
			map.addAttribute("skillGroups" , skillRes.findByHostidAndOrgi(hostid , super.getOrgi(request)));
		}
    	return request(super.createRequestPageTempletResponse("/admin/callcenter/skill/imp"));
    }
	
	@RequestMapping(value = "/skill/extention/delete")
    @Menu(type = "callcenter" , subtype = "extention" , access = false , admin = true)
    public ModelAndView skillextentiondelete(ModelMap map , HttpServletRequest request , @Valid String id , @Valid String hostid) {
		if(!StringUtils.isBlank(id)){
			skillExtentionRes.delete(id);
		}
		return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/skill.html?hostid="+hostid));
    }
	
	@RequestMapping(value = "/skill/extention/save")
    @Menu(type = "callcenter" , subtype = "extention" , access = false , admin = true)
    public ModelAndView skillextentionsave(ModelMap map , HttpServletRequest request , @Valid SkillExtention skillExtention, @Valid String hostid , @Valid String[] exts) {
		if(exts!=null && exts.length > 0){
			List<SkillExtention> skillExtentionList = skillExtentionRes.findByHostidAndOrgi(hostid, super.getOrgi(request)) ;
			for(String ext :exts){
				SkillExtention skillExt = new SkillExtention() ;
				skillExt.setOrgi(super.getOrgi(request));
				skillExt.setCreater(super.getUser(request).getId());
				skillExt.setCreatetime(new Date());
				skillExt.setExtention(ext);
				skillExt.setHostid(hostid);
				skillExt.setSkillid(skillExtention.getSkillid());
				skillExt.setUpdatetime(new Date());
				boolean ingroup = false;
				for(SkillExtention temp : skillExtentionList){
					if(temp.getSkillid().equals(skillExt.getSkillid()) && temp.getExtention().equals(skillExt.getExtention())){
						ingroup = true ;
					}
				}
				if(ingroup == false){
					skillExtentionRes.save(skillExt) ;
				}
			}
		}
		return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/skill.html?hostid="+hostid));
    }
	
}
