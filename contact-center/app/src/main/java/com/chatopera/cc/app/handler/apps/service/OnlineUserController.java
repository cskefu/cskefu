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
package com.chatopera.cc.app.handler.apps.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.app.persistence.es.ContactsRepository;
import com.chatopera.cc.app.persistence.impl.AgentUserService;
import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.model.AgentServiceSummary;
import com.chatopera.cc.app.model.AgentUser;
import com.chatopera.cc.app.model.AgentUserContacts;
import com.chatopera.cc.app.model.OnlineUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.chatopera.cc.app.basic.MainUtils;
import com.chatopera.cc.app.persistence.repository.AgentServiceRepository;
import com.chatopera.cc.app.persistence.repository.AgentUserContactsRepository;
import com.chatopera.cc.app.persistence.repository.ChatMessageRepository;
import com.chatopera.cc.app.persistence.repository.OnlineUserHisRepository;
import com.chatopera.cc.app.persistence.repository.OnlineUserRepository;
import com.chatopera.cc.app.persistence.repository.ServiceSummaryRepository;
import com.chatopera.cc.app.persistence.repository.TagRelationRepository;
import com.chatopera.cc.app.persistence.repository.TagRepository;
import com.chatopera.cc.app.persistence.repository.UserEventRepository;
import com.chatopera.cc.app.persistence.repository.WeiXinUserRepository;
import com.chatopera.cc.app.model.AgentService;
import com.chatopera.cc.app.model.WeiXinUser;

@Controller
@RequestMapping("/service")
public class OnlineUserController extends Handler {
	@Autowired
	private AgentServiceRepository agentServiceRes ;
	
	@Autowired
	private AgentUserService agentUserRes ;
	
	@Autowired
	private OnlineUserRepository onlineUserRes;
	
	@Autowired
	private UserEventRepository userEventRes;
	
	@Autowired
	private ServiceSummaryRepository serviceSummaryRes;
	
	
	@Autowired
	private OnlineUserHisRepository onlineUserHisRes;
	
	@Autowired
	private WeiXinUserRepository weiXinUserRes;
	
	@Autowired
	private TagRepository tagRes ;
	
	@Autowired
	private TagRelationRepository tagRelationRes ;
	
	@Autowired
	private ChatMessageRepository chatMessageRepository ;
	
	@Autowired
	private ContactsRepository contactsRes ;
	
	@Autowired
	private AgentUserContactsRepository agentUserContactsRes ;
	
	@RequestMapping("/online/index")
    @Menu(type = "service" , subtype = "online" , admin= true)
    public ModelAndView index(ModelMap map , HttpServletRequest request , String userid , String agentservice , @Valid String channel) {
		if(!StringUtils.isBlank(userid)){
			map.put("inviteResult", MainUtils.getWebIMInviteResult(onlineUserRes.findByOrgiAndUserid(super.getOrgi(request), userid))) ;
			map.put("tagRelationList", tagRelationRes.findByUserid(userid)) ;
			map.put("onlineUserHistList", onlineUserHisRes.findByUseridAndOrgi(userid, super.getOrgi(request))) ;
			map.put("agentServicesAvg", onlineUserRes.countByUserForAvagTime(super.getOrgi(request), MainContext.AgentUserStatusEnum.END.toString(),userid)) ;
			
			List<AgentService> agentServiceList = agentServiceRes.findByUseridAndOrgi(userid, super.getOrgi(request)) ; 
			
			map.put("agentServiceList", agentServiceList) ;
			if(agentServiceList.size()>0){
				map.put("serviceCount", Integer
						.valueOf(this.agentServiceRes
								.countByUseridAndOrgiAndStatus(userid, super.getOrgi(request),
										MainContext.AgentUserStatusEnum.END.toString())));
				
				AgentService agentService = agentServiceList.get(0) ;
				if(!StringUtils.isBlank(agentservice)){
					for(AgentService as : agentServiceList){
						if(as.getId().equals(agentservice)){
							agentService = as ; break ;
						}
					}
				}
				
				if(agentService!=null){
					List<AgentServiceSummary> summaries = serviceSummaryRes.findByAgentserviceidAndOrgi(agentService.getId(), super.getOrgi(request)) ;
					if(summaries.size() > 0)
						map.put("summary" , summaries.get(0)) ;
				}
				
				List<AgentUserContacts> agentUserContactsList = agentUserContactsRes.findByUseridAndOrgi(userid, super.getOrgi(request)) ;
				if(agentUserContactsList.size() > 0){
					AgentUserContacts agentUserContacts = agentUserContactsList.get(0) ;
					map.put("contacts", contactsRes.findOne(agentUserContacts.getContactsid())) ;
				}
				
				map.put("tags", tagRes.findByOrgiAndTagtype(super.getOrgi(request) , MainContext.ModelType.USER.toString())) ;
				map.put("summaryTags", tagRes.findByOrgiAndTagtype(super.getOrgi(request) , MainContext.ModelType.SUMMARY.toString())) ;
				map.put("curAgentService", agentService) ;
				
				
				map.put("agentUserMessageList", chatMessageRepository.findByAgentserviceidAndOrgi(agentService.getId() , super.getOrgi(request), new PageRequest(0, 50, Direction.DESC , "updatetime")));
			}
			
			if(MainContext.ChannelTypeEnum.WEIXIN.toString().equals(channel)){
				List<WeiXinUser> weiXinUserList = weiXinUserRes.findByOpenidAndOrgi(userid, super.getOrgi(request)) ;
				if(weiXinUserList.size() > 0){
					WeiXinUser weiXinUser = weiXinUserList.get(0) ;
					map.put("weiXinUser",weiXinUser);
				}
			}else if(MainContext.ChannelTypeEnum.WEBIM.toString().equals(channel)){
				List<OnlineUser> onlineUserList = onlineUserRes.findByUseridAndOrgi(userid, super.getOrgi(request)) ;
				if(onlineUserList.size() >0){
					map.put("onlineUser", onlineUserList.get(0)) ;
				}
			}
			map.put("agentUser", agentUserRes.findByUseridAndOrgi(userid, super.getOrgi(request))) ;
			map.put("curragentuser", agentUserRes.findByUseridAndOrgi(userid, super.getOrgi(request))) ;
		}
        return request(super.createAppsTempletResponse("/apps/service/online/index"));
    }
	
	@RequestMapping("/online/chatmsg")
    @Menu(type = "service" , subtype = "chatmsg" , admin= true)
    public ModelAndView onlinechat(ModelMap map , HttpServletRequest request , String id , String title) {
		AgentService agentService = agentServiceRes.getOne(id) ; 
		AgentUser curragentuser = agentUserRes.findByUseridAndOrgi(agentService.getUserid(), super.getOrgi(request)) ;
		
		map.put("curAgentService", agentService) ;
		map.put("curragentuser", curragentuser) ;
		if(!StringUtils.isBlank(title)){
			map.put("title", title) ;
		}
		
		map.put("summaryTags", tagRes.findByOrgiAndTagtype(super.getOrgi(request) , MainContext.ModelType.SUMMARY.toString())) ;
		
		if(agentService!=null){
			List<AgentServiceSummary> summaries = serviceSummaryRes.findByAgentserviceidAndOrgi(agentService.getId(), super.getOrgi(request)) ;
			if(summaries.size() > 0)
                map.put("summary" , summaries.get(0)) ;
		}
		
		map.put("agentUserMessageList", chatMessageRepository.findByAgentserviceidAndOrgi(agentService.getId() , super.getOrgi(request), new PageRequest(0, 50, Direction.DESC , "updatetime")));
		
        return request(super.createRequestPageTempletResponse("/apps/service/online/chatmsg"));
    }
	
	@RequestMapping("/trace")
    @Menu(type = "service" , subtype = "trace" , admin= false)
    public ModelAndView trace(ModelMap map , HttpServletRequest request , @Valid String sessionid) {
		if(!StringUtils.isBlank(sessionid)){
			map.addAttribute("traceHisList", userEventRes.findBySessionidAndOrgi(sessionid, super.getOrgi(request), new PageRequest(0, 100))) ;
		}
        return request(super.createRequestPageTempletResponse("/apps/service/online/trace"));
    }
}
