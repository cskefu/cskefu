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

package com.chatopera.cc.webim.web.handler.apps.internet;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.chatopera.cc.core.UKDataContext;
import com.chatopera.cc.util.extra.DataExchangeInterface;
import com.chatopera.cc.util.webim.WebIMClient;
import com.chatopera.cc.webim.service.acd.ServiceQuene;
import com.chatopera.cc.webim.service.cache.CacheHelper;
import com.chatopera.cc.webim.service.es.ContactsRepository;
import com.chatopera.cc.webim.util.MessageUtils;
import com.chatopera.cc.webim.util.OnlineUserUtils;
import com.chatopera.cc.webim.web.handler.Handler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.chatopera.cc.util.BrowserClient;
import com.chatopera.cc.util.CheckMobile;
import com.chatopera.cc.util.IP;
import com.chatopera.cc.util.IPTools;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.UKTools;
import com.chatopera.cc.webim.service.repository.AgentServiceSatisRepository;
import com.chatopera.cc.webim.service.repository.AgentUserContactsRepository;
import com.chatopera.cc.webim.service.repository.AttachmentRepository;
import com.chatopera.cc.webim.service.repository.ChatMessageRepository;
import com.chatopera.cc.webim.service.repository.ConsultInviteRepository;
import com.chatopera.cc.webim.service.repository.InviteRecordRepository;
import com.chatopera.cc.webim.service.repository.LeaveMsgRepository;
import com.chatopera.cc.webim.service.repository.SNSAccountRepository;
import com.chatopera.cc.webim.web.model.AgentReport;
import com.chatopera.cc.webim.web.model.AgentServiceSatis;
import com.chatopera.cc.webim.web.model.AgentUserContacts;
import com.chatopera.cc.webim.web.model.AiConfig;
import com.chatopera.cc.webim.web.model.AttachmentFile;
import com.chatopera.cc.webim.web.model.BlackEntity;
import com.chatopera.cc.webim.web.model.Contacts;
import com.chatopera.cc.webim.web.model.CousultInvite;
import com.chatopera.cc.webim.web.model.InviteRecord;
import com.chatopera.cc.webim.web.model.KnowledgeType;
import com.chatopera.cc.webim.web.model.LeaveMsg;
import com.chatopera.cc.webim.web.model.SNSAccount;
import com.chatopera.cc.webim.web.model.SessionConfig;
import com.chatopera.cc.webim.web.model.SystemConfig;
import com.chatopera.cc.webim.web.model.Topic;
import com.chatopera.cc.webim.web.model.UKeFuDic;
import com.chatopera.cc.webim.web.model.UploadStatus;
import com.chatopera.cc.webim.web.model.User;
import com.chatopera.cc.webim.web.model.UserHistory;

@Controller
@RequestMapping("/im")
@EnableAsync
public class IMController extends Handler {
	
	@Value("${uk.im.server.host}")  
    private String host;  
  
    @Value("${uk.im.server.port}")  
    private Integer port; 
    
    @Value("${web.upload-path}")
    private String path;
    
	@Autowired
	private ConsultInviteRepository inviteRepository;
	
	@Autowired
	private ChatMessageRepository chatMessageRes ;
	
	@Autowired
	private AgentServiceSatisRepository agentServiceSatisRes ;
	
	@Autowired
	private InviteRecordRepository inviteRecordRes ;
	
	@Autowired
	private LeaveMsgRepository leaveMsgRes ;
	

	@Autowired
	private AttachmentRepository attachementRes;
	
	@Autowired
	private ContactsRepository contactsRes ;
	
	@Autowired
	private AgentUserContactsRepository agentUserContactsRes ;
	
	@Autowired
	private SNSAccountRepository snsAccountRepository ;
	
    @RequestMapping("/{id}")
    @Menu(type = "im" , subtype = "point" , access = true)
    public ModelAndView point(HttpServletRequest request , HttpServletResponse response, @PathVariable String id , @Valid String orgi , @Valid String userid , @Valid String title, @Valid String aiid) {
    	ModelAndView view = request(super.createRequestPageTempletResponse("/apps/im/point")) ; 
    	String sessionid = request.getSession().getId() ;
    	if(!StringUtils.isBlank(id)){
	    	view.addObject("hostname", request.getServerName()) ;
	    	
			SystemConfig systemConfig = UKTools.getSystemConfig();
			if(systemConfig!=null && systemConfig.isEnablessl()) {
				view.addObject("schema","https") ;
				if(request.getServerPort() == 80) {
					view.addObject("port", 443) ;
				}else {
					view.addObject("port", request.getServerPort()) ;
				}
			}else {
				view.addObject("schema",request.getScheme()) ;
				view.addObject("port", request.getServerPort()) ;
			}
			view.addObject("appid", id) ;
			
			
			view.addObject("client", UKTools.getUUID()) ;
			view.addObject("sessionid", sessionid) ;
			
			view.addObject("ip", UKTools.md5(request.getRemoteAddr())) ;
			
			view.addObject("mobile", CheckMobile.check(request.getHeader("User-Agent"))) ;
			
			
			CousultInvite invite = OnlineUserUtils.cousult(id,orgi, inviteRepository);
	    	if(invite!=null){
	    		orgi = invite.getOrgi() ;
	    		view.addObject("inviteData", invite);
	    		view.addObject("orgi",invite.getOrgi());
	    		view.addObject("appid",id);
	    		
	    		if(!StringUtils.isBlank(aiid)) {
					view.addObject("aiid", aiid) ;
				}else if(!StringUtils.isBlank(invite.getAiid())){
					view.addObject("aiid", invite.getAiid()) ;
				}
	    	//记录用户行为日志
				UserHistory userHistory = new UserHistory() ;
				String url = request.getHeader("referer");
				if(!StringUtils.isBlank(url)){
					if(url.length() >255){
						userHistory.setUrl(url.substring( 0 , 255));
					}else{
						userHistory.setUrl(url);
					}
					userHistory.setReferer(userHistory.getUrl());
				}
				userHistory.setParam(UKTools.getParameter(request));
				if(userHistory!=null){
					userHistory.setMaintype("im");
					userHistory.setSubtype("point");
					userHistory.setName("online");
					userHistory.setAdmin(false);
					userHistory.setAccessnum(true);
				}
				User imUser = super.getIMUser(request , userid, null) ;
				if(imUser!=null){
					userHistory.setCreater(imUser.getId());
					userHistory.setUsername(imUser.getUsername());
					userHistory.setOrgi(orgi);
				}
				if(!StringUtils.isBlank(title)){
					if(title.length() > 255){
						userHistory.setTitle(title.substring(0 , 255));
					}else{
						userHistory.setTitle(title);
					}
				}
				userHistory.setOrgi(invite.getOrgi());
				userHistory.setAppid(id);
				userHistory.setSessionid(sessionid);
				
				String ip = UKTools.getIpAddr(request);
				userHistory.setHostname(ip);
				userHistory.setIp(ip);
				IP ipdata = IPTools.getInstance().findGeography(ip);
				userHistory.setCountry(ipdata.getCountry());
				userHistory.setProvince(ipdata.getProvince());
				userHistory.setCity(ipdata.getCity());
			    userHistory.setIsp(ipdata.getIsp());
			    
			    BrowserClient client = UKTools.parseClient(request) ;
			    userHistory.setOstype(client.getOs());
			    userHistory.setBrowser(client.getBrowser());
			    userHistory.setMobile(CheckMobile.check(request.getHeader("User-Agent")) ? "1" : "0");
			    
			    if(invite.isSkill()){
				    /***
				     * 查询 技能组 ， 缓存？ 
				     */
				    view.addObject("skillList", OnlineUserUtils.organ(orgi, ipdata , invite,true))  ;
				    /**
				     * 查询坐席 ， 缓存？
				     */
				    view.addObject("agentList", OnlineUserUtils.agents(orgi,true))  ;
			    }
			    view.addObject("traceid", userHistory.getId()) ;
			    if(invite.isRecordhis()){
			    	UKTools.published(userHistory);
			    }
			    
			    view.addObject("pointAd", UKTools.getPointAdv(UKDataContext.AdPosEnum.POINT.toString(),orgi)) ;
			    view.addObject("inviteAd", UKTools.getPointAdv(UKDataContext.AdPosEnum.INVITE.toString(),orgi)) ;
			}
    	}
		
        return view;
    }
    
    @RequestMapping("/{id}/userlist")
    @Menu(type = "im" , subtype = "inlist" , access = true)
    public void inlist(HttpServletRequest request , HttpServletResponse response, @PathVariable String id , @Valid String userid) throws IOException {
    	response.setHeader("Content-Type", "text/html;charset=utf-8"); 
    	if(!StringUtils.isBlank(userid)){
	    	BlackEntity black = (BlackEntity) CacheHelper.getSystemCacheBean().getCacheObject(userid, UKDataContext.SYSTEM_ORGI) ;
	    	if((black != null && (black.getEndtime()==null || black.getEndtime().after(new Date()))) ){
	    		response.getWriter().write("in");;
	    	}
    	}
    }
    /**
     * 延时获取用户端浏览器的跟踪ID
     * @param request
     * @param response
     * @param orgi
     * @param appid
     * @param userid
     * @param sign
     * @return
     */
    @RequestMapping("/online")
    @Menu(type = "im" , subtype = "online" , access = true)
    public SseEmitter callable(HttpServletRequest request , HttpServletResponse response , @Valid Contacts contacts, final @Valid String orgi , final @Valid String sessionid, @Valid String appid, final @Valid String userid , @Valid String sign , final @Valid String client, final @Valid String title, final @Valid String traceid) {
    	BlackEntity black = (BlackEntity) CacheHelper.getSystemCacheBean().getCacheObject(userid,  orgi) ;
    	SseEmitter retSseEmitter = null ;
    	if((black == null || (black.getEndtime()!=null && black.getEndtime().before(new Date()))) ){
	    	final SseEmitter emitter = new SseEmitter(30000L);
			if(CacheHelper.getSystemCacheBean().getCacheObject(userid, orgi) == null){
				if(!StringUtils.isBlank(userid)){
					emitter.onCompletion(new Runnable() {
						@Override
						public void run() {	
							try {
								OnlineUserUtils.webIMClients.removeClient(userid , client , false) ; //执行了 邀请/再次邀请后终端的
							} catch (Exception e) {
								e.printStackTrace();
							}	
						}
					});
					emitter.onTimeout(new Runnable() {	
						@Override
						public void run() {
							try {
								if(emitter!=null){
									emitter.complete();
								}
								OnlineUserUtils.webIMClients.removeClient(userid , client , true) ;	//正常的超时断开
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					CousultInvite invite = OnlineUserUtils.cousult(appid, orgi, inviteRepository);
					if(invite!=null && invite.isTraceuser()){
						contacts = processContacts(orgi, contacts, appid, userid);
					}
			    	if(!StringUtils.isBlank(sign)){
			    		OnlineUserUtils.online(super.getIMUser(request , sign , contacts!=null ? contacts.getName() : null) , orgi , sessionid , UKDataContext.OnlineUserTypeStatus.WEBIM.toString(), request , UKDataContext.ChannelTypeEnum.WEBIM.toString() , appid , contacts , invite);
			    	}
			    	
			    	OnlineUserUtils.webIMClients.putClient(userid, new WebIMClient(userid  , client , emitter)) ;
				}
			}
			retSseEmitter = emitter ;
    	}
		return retSseEmitter;
	}
    
    @RequestMapping("/index")
    @Menu(type = "im" , subtype = "index" , access = true)
    public ModelAndView index(ModelMap map ,HttpServletRequest request , HttpServletResponse response, @Valid String orgi, @Valid String aiid, @Valid String traceid ,@Valid String exchange, @Valid String title ,@Valid String url,@Valid String mobile ,@Valid String phone ,  @Valid String ai , @Valid String client , @Valid String type, @Valid String appid, @Valid String userid, @Valid String sessionid , @Valid String skill, @Valid String agent , @Valid Contacts contacts,@Valid String product,@Valid String description,@Valid String imgurl,@Valid String pid,@Valid String purl) throws Exception {
    	ModelAndView view = request(super.createRequestPageTempletResponse("/apps/im/index")) ; 
    	BlackEntity black = (BlackEntity) CacheHelper.getSystemCacheBean().getCacheObject(userid, UKDataContext.SYSTEM_ORGI) ;
    	if(!StringUtils.isBlank(appid) &&  (black == null || (black.getEndtime()!=null && black.getEndtime().before(new Date()))) ){
    		CousultInvite invite = OnlineUserUtils.cousult(appid, orgi, inviteRepository);
    		String userID = null;
    		if(!StringUtils.isBlank(userid)){
    			userID = UKTools.genIDByKey(userid) ;
    		}else{
    			userID = UKTools.genIDByKey(sessionid);	
    		}
			String nickname = "Guest_" + userID;
			boolean consult = true ;				//是否已收集用户信息
			SessionConfig sessionConfig = ServiceQuene.initSessionConfig(orgi) ;
			
			map.addAttribute("sessionConfig", sessionConfig);
			
    		
    		map.addAttribute("hostname", request.getServerName()) ;
			map.addAttribute("port", port) ;
			map.addAttribute("appid", appid) ;
			map.addAttribute("userid", userid) ;
			map.addAttribute("schema", request.getScheme()) ;
			map.addAttribute("sessionid", sessionid) ;
			
			view.addObject("product", product) ;
			view.addObject("description", description) ;
			view.addObject("imgurl", imgurl) ;
			view.addObject("pid", pid) ;
			view.addObject("purl", purl) ;
			
			map.addAttribute("ip", UKTools.md5(request.getRemoteAddr())) ;
			
			if(!StringUtils.isBlank(traceid)){
				map.addAttribute("traceid", traceid) ;
			}
			if(!StringUtils.isBlank(exchange)){
				map.addAttribute("exchange", exchange) ;
			}
			if(!StringUtils.isBlank(title)){
				map.addAttribute("title", title) ;
			}
			if(!StringUtils.isBlank(traceid)){
				map.addAttribute("url", url) ;
			}
			
			map.addAttribute("ukefport", request.getServerPort()) ; 
			/**
			 * 先检查 invite不为空
			 */
			if(invite!=null) {
	    		map.addAttribute("orgi",invite.getOrgi());
	    		map.addAttribute("inviteData", invite);
	    		
	    		if(!StringUtils.isBlank(aiid)) {
	    			map.addAttribute("aiid", aiid) ;
				}else if(!StringUtils.isBlank(invite.getAiid())){
					map.addAttribute("aiid", invite.getAiid()) ;
				}
			
	    		AgentReport report = ServiceQuene.getAgentReport(invite.getOrgi()) ;
			
				if(report.getAgents() ==0 ||  (sessionConfig.isHourcheck() && !UKTools.isInWorkingHours(sessionConfig.getWorkinghours()) && invite.isLeavemessage())){
					view = request(super.createRequestPageTempletResponse("/apps/im/leavemsg")) ;
				}else if(invite.isConsult_info()){	//启用了信息收集 , 从Request获取 ， 或从 Cookies 里去
	    			//验证 OnlineUser 信息
	    			if(contacts!=null && !StringUtils.isBlank(contacts.getName())){	//contacts用于传递信息，并不和 联系人表发生 关联，contacts信息传递给 Socket.IO，然后赋值给 AgentUser，最终赋值给 AgentService永久存储
	    				consult = true ;
	    				//存入 Cookies
	    				if(invite.isConsult_info_cookies()){
		    				Cookie name = new Cookie("name",UKTools.encryption(URLEncoder.encode(contacts.getName(), "UTF-8")));
		    				response.addCookie(name);
		    				name.setMaxAge(3600);
		    				if(!StringUtils.isBlank(contacts.getPhone())){
			    				Cookie phonecookie = new Cookie("phone",UKTools.encryption(URLEncoder.encode(contacts.getPhone(), "UTF-8")));
			    				phonecookie.setMaxAge(3600);
			    				response.addCookie(phonecookie);
		    				}
		    				if(!StringUtils.isBlank(contacts.getEmail())){
			    				Cookie email = new Cookie("email",UKTools.encryption(URLEncoder.encode(contacts.getEmail(), "UTF-8")));
			    				email.setMaxAge(3600);
			    				response.addCookie(email);
		    				}
		    				if(!StringUtils.isBlank(contacts.getMemo())){
			    				Cookie memo = new Cookie("memo",UKTools.encryption(URLEncoder.encode(contacts.getName(), "UTF-8")));
			    				memo.setMaxAge(3600);
			    				response.addCookie(memo);
		    				}
	    				}
	    			}else{
	    				//从 Cookies里尝试读取 
	    				if(invite.isConsult_info_cookies()){
		    				Cookie[] cookies = request.getCookies();//这样便可以获取一个cookie数组
		    				contacts = new Contacts();
		    				if(cookies!=null){
			    				for(Cookie cookie : cookies){
			    					if(cookie!=null && !StringUtils.isBlank(cookie.getName()) && !StringUtils.isBlank(cookie.getValue())){
			    						if(cookie.getName().equals("name")){
			    							contacts.setName(URLDecoder.decode(UKTools.decryption(cookie.getValue()) , "UTF-8"));
			    						}
			    						if(cookie.getName().equals("phone")){
			    							contacts.setPhone(URLDecoder.decode(UKTools.decryption(cookie.getValue()) , "UTF-8"));
			    						}
			    						if(cookie.getName().equals("email")){
			    							contacts.setEmail(URLDecoder.decode(UKTools.decryption(cookie.getValue()) , "UTF-8"));
			    						}
			    						if(cookie.getName().equals("memo")){
			    							contacts.setMemo(URLDecoder.decode(UKTools.decryption(cookie.getValue()) , "UTF-8"));
			    						}
			    					}
			    				}
		    				}
	    				}
	    				if(StringUtils.isBlank(contacts.getName())){
	    					consult = false ;
		    				view = request(super.createRequestPageTempletResponse("/apps/im/collecting")) ;
	    				}
	    			}
	    		}else{
	    			contacts = processContacts(invite.getOrgi(), contacts, appid, userid);
	    		}
				
				if(!StringUtils.isBlank(client)){
					map.addAttribute("client", client) ;
				}
				if(!StringUtils.isBlank(skill)){
					map.addAttribute("skill", skill) ;
				}
				if(!StringUtils.isBlank(agent)){
					map.addAttribute("agent", agent) ;
				}
				
				map.addAttribute("contacts", contacts) ;
				
				if(!StringUtils.isBlank(type)){
					map.addAttribute("type", type) ;
				}
				IP ipdata = IPTools.getInstance().findGeography(UKTools.getIpAddr(request));
				map.addAttribute("skillList", OnlineUserUtils.organ(invite.getOrgi() , ipdata , invite,true))  ;
				
	    		if(invite!=null && consult){
					if(contacts!=null && !StringUtils.isBlank(contacts.getName())){
						nickname = contacts.getName() ;
					}
					map.addAttribute("username", nickname) ;
	    			if(UKDataContext.model.get("xiaoe")!=null && !StringUtils.isBlank(invite.getAiid()) && invite.isAi() && ((!StringUtils.isBlank(ai) && ai.equals("true")) || (invite.isAifirst() && ai == null))){	//启用 AI ， 并且 AI优先 接待
	    				DataExchangeInterface dataInterface = (DataExchangeInterface) UKDataContext.getContext().getBean("aiconfig") ;
	    				AiConfig aiConfig = (AiConfig) dataInterface.getDataByIdAndOrgi(aiid, invite.getOrgi()) ;
	    				if(aiConfig!=null){
	    					map.addAttribute("aiConfig", aiConfig) ;
	    				}
	    				view = request(super.createRequestPageTempletResponse("/apps/im/ai/index")) ;
	    				if(CheckMobile.check(request.getHeader("User-Agent")) || !StringUtils.isBlank(mobile)){
	    					view = request(super.createRequestPageTempletResponse("/apps/im/ai/mobile")) ;		//智能机器人 移动端
	    				}
	    				if(UKDataContext.model.get("xiaoe")!=null){
	    					List<Topic> topicList = OnlineUserUtils.cacheHotTopic((DataExchangeInterface) UKDataContext.getContext().getBean("topic") , super.getUser(request) , orgi , aiid)  ;
	    					
	    					/**
	    					 * 初步按照地区匹配分类筛选
	    					 */
	    					List<KnowledgeType> topicTypeList = OnlineUserUtils.topicType(orgi,ipdata,OnlineUserUtils.cacheHotTopicType((DataExchangeInterface) UKDataContext.getContext().getBean("topictype") , super.getUser(request) , orgi , aiid)) ; 
	    					
	    					/**
	    					 * 第二步按照 有 热点主题的 分类做筛选
	    					 */
	    					map.addAttribute("topicList", OnlineUserUtils.topic(orgi, topicTypeList, topicList)) ;
	    					/**
	    					 * 第三步筛选 分类，如果无热点知识，则不显示分类
	    					 */
	    					map.addAttribute("topicTypeList",OnlineUserUtils.filterTopicType(topicTypeList, topicList)) ;
	    				}
	    			}else{
	    				if(CheckMobile.check(request.getHeader("User-Agent")) || !StringUtils.isBlank(mobile)){
	    					view = request(super.createRequestPageTempletResponse("/apps/im/mobile")) ;	//WebIM移动端。再次点选技能组？
	    				}
	    			}
	    			map.addAttribute("chatMessageList", chatMessageRes.findByUsessionAndOrgi(userid , orgi, new PageRequest(0, 20, Direction.DESC , "updatetime"))) ;
		    	}
	    		view.addObject("commentList" , UKeFuDic.getInstance().getDic(UKDataContext.UKEFU_SYSTEM_COMMENT_DIC)) ;
	    		view.addObject("commentItemList" , UKeFuDic.getInstance().getDic(UKDataContext.UKEFU_SYSTEM_COMMENT_ITEM_DIC)) ;
	    		view.addObject("welcomeAd", UKTools.getPointAdv(UKDataContext.AdPosEnum.WELCOME.toString(),orgi)) ;
	    		view.addObject("imageAd", UKTools.getPointAdv(UKDataContext.AdPosEnum.IMAGE.toString(),orgi)) ;
		//    	OnlineUserUtils.sendWebIMClients(userid , "accept");
	    		 
	    		if(invite.isTraceuser()){
			    	Page<InviteRecord> inviteRecordList = inviteRecordRes.findByUseridAndOrgi(userid, orgi , new PageRequest(0, 1, Direction.DESC, "createtime")) ;
			    	if(inviteRecordList.getContent()!=null && inviteRecordList.getContent().size()>0){
			    		InviteRecord record = inviteRecordList.getContent().get(0) ;
			    		record.setUpdatetime(new Date());
			    		record.setTraceid(traceid);
			    		record.setTitle(title);
			    		record.setUrl(url);
			    		record.setResponsetime((int) (System.currentTimeMillis() - record.getCreatetime().getTime()));
			    		record.setResult(UKDataContext.OnlineUserInviteStatus.ACCEPT.toString());
			    		inviteRecordRes.save(record) ;
			    	}
	    		}
			}
    	}
        return view;
    }
    
    private Contacts processContacts(String orgi ,Contacts contacts , String appid , String userid){
    	if(contacts!=null){
			if(contacts != null && (!StringUtils.isBlank(contacts.getName()) || !StringUtils.isBlank(contacts.getPhone()) || !StringUtils.isBlank(contacts.getEmail()))){
				StringBuffer query = new StringBuffer();
				query.append(contacts.getName()) ;
				if(!StringUtils.isBlank(contacts.getPhone())){
					query.append(" OR ").append(contacts.getPhone()) ;
				}
				if(!StringUtils.isBlank(contacts.getEmail())){
					query.append(" OR ").append(contacts.getEmail()) ;
				}
				Page<Contacts> contactsList = contactsRes.findByOrgi(orgi, false, query.toString(), new PageRequest(0, 1)) ;
				if(contactsList.getContent().size() > 0){
					contacts = contactsList.getContent().get(0) ;
				}else{
//					contactsRes.save(contacts) ;	//需要增加签名验证，避免随便产生垃圾信息，也可以自行修改？
					contacts.setId(null);
				}
			}else{
				contacts.setId(null);
			}
			if(contacts!=null && !StringUtils.isBlank(contacts.getId())){
				List<AgentUserContacts> agentUserContactsList = agentUserContactsRes.findByUseridAndOrgi(userid, orgi) ;
				if(agentUserContactsList.size() == 0){
    				AgentUserContacts agentUserContacts = new AgentUserContacts() ;
    				agentUserContacts.setAppid(appid);
    				agentUserContacts.setChannel(UKDataContext.ChannelTypeEnum.WEBIM.toString());
    				agentUserContacts.setContactsid(contacts.getId());
    				agentUserContacts.setUserid(userid);
    				agentUserContacts.setOrgi(orgi);
    				agentUserContacts.setCreatetime(new Date());
    				agentUserContactsRes.save(agentUserContacts) ;
				}
			}else if(!StringUtils.isBlank(userid)){
				List<AgentUserContacts> agentUserContactsList = agentUserContactsRes.findByUseridAndOrgi(userid, orgi) ;
				if(agentUserContactsList.size() > 0){
					AgentUserContacts agentUserContacts = agentUserContactsList.get(0) ;
					contacts = contactsRes.findOne(agentUserContacts.getContactsid()) ;
				}
			}
		}
    	return contacts ;
    }
    
    @RequestMapping("/text/{appid}")
    @Menu(type = "im" , subtype = "index" , access = true)
    public ModelAndView text(HttpServletRequest request , HttpServletResponse response, @PathVariable String appid ,@Valid String traceid,@Valid String aiid ,@Valid String exchange, @Valid String title ,@Valid String url, @Valid String skill, @Valid String id , @Valid String userid , @Valid String agent , @Valid String name , @Valid String email ,@Valid String phone,@Valid String ai,@Valid String orgi ,@Valid String product,@Valid String description,@Valid String imgurl,@Valid String pid,@Valid String purl) throws Exception {
    	ModelAndView view = request(super.createRequestPageTempletResponse("/apps/im/text")) ; 
    	
    	view.addObject("hostname", request.getServerName()) ;
		view.addObject("port", request.getServerPort()) ;
		view.addObject("schema", request.getScheme()) ;
		view.addObject("appid", appid) ;
		
		
		view.addObject("ip", UKTools.md5(request.getRemoteAddr())) ;
		
		if(!StringUtils.isBlank(skill)){
			view.addObject("skill", skill) ;
		}
		if(!StringUtils.isBlank(agent)){
			view.addObject("agent", agent) ;
		}
		
		view.addObject("client", UKTools.getUUID()) ;
		view.addObject("sessionid", request.getSession().getId()) ;
		
		view.addObject("id", id) ;
		if(!StringUtils.isBlank(ai)){
			view.addObject("ai", ai) ;
		}
		if(!StringUtils.isBlank(exchange)){
			view.addObject("exchange", exchange) ;
		}
		
		view.addObject("name", name) ;
		view.addObject("email", email) ;
		view.addObject("phone", phone) ;
		view.addObject("userid", userid) ;
		
		view.addObject("product", product) ;
		view.addObject("description", description) ;
		view.addObject("imgurl", imgurl) ;
		view.addObject("pid", pid) ;
		view.addObject("purl", purl) ;
		
		
		
		if(!StringUtils.isBlank(traceid)){
			view.addObject("traceid", traceid) ;
		}
		if(!StringUtils.isBlank(title)){
			view.addObject("title", title) ;
		}
		if(!StringUtils.isBlank(traceid)){
			view.addObject("url", url) ;
		}
		CousultInvite invite = OnlineUserUtils.cousult(appid, orgi, inviteRepository);
    	if(invite!=null){
    		view.addObject("inviteData", invite);
    		view.addObject("orgi",invite.getOrgi());
    		view.addObject("appid",appid);
    		
    		if(!StringUtils.isBlank(aiid)) {
				view.addObject("aiid", aiid) ;
			}else if(!StringUtils.isBlank(invite.getAiid())){
				view.addObject("aiid", invite.getAiid()) ;
			}
    	}
    	
		return view;
    }
    
    
    @RequestMapping("/leavemsg/save")
    @Menu(type = "admin" , subtype = "user")
    public ModelAndView leavemsgsave(HttpServletRequest request ,@Valid String appid ,@Valid LeaveMsg msg) {
    	if(!StringUtils.isBlank(appid)){
    		SNSAccount snsAccount = snsAccountRepository.findBySnsid(appid);
			String orgi = snsAccount.getOrgi();
    		CousultInvite invite = inviteRepository.findBySnsaccountidAndOrgi(appid, orgi) ; ;
	    	List<LeaveMsg> msgList = leaveMsgRes.findByOrgiAndUserid(invite.getOrgi(), msg.getUserid()) ;
	    	// if(msg!=null && msgList.size() == 0){
	    	if(msg!=null){
	    		msg.setOrgi(invite.getOrgi());
	    		leaveMsgRes.save(msg);
	    	}
    	}
    	return request(super.createRequestPageTempletResponse("/apps/im/leavemsgsave"));
    }
    
    @RequestMapping("/refuse")
    @Menu(type = "im" , subtype = "refuse" , access = true)
    public void refuse(HttpServletRequest request , HttpServletResponse response, @Valid String orgi , @Valid String appid, @Valid String userid, @Valid String sessionid, @Valid String client) throws Exception {
    	OnlineUserUtils.refuseInvite(userid, orgi);
//    	OnlineUserUtils.sendWebIMClients(userid , "refuse");
    	Page<InviteRecord> inviteRecordList = inviteRecordRes.findByUseridAndOrgi(userid, orgi , new PageRequest(0, 1, Direction.DESC, "createtime")) ;
    	if(inviteRecordList.getContent()!=null && inviteRecordList.getContent().size()>0){
    		InviteRecord record = inviteRecordList.getContent().get(0) ;
    		record.setUpdatetime(new Date());
    		record.setResponsetime((int) (System.currentTimeMillis() - record.getCreatetime().getTime()));
    		record.setResult(UKDataContext.OnlineUserInviteStatus.REFUSE.toString());
    		inviteRecordRes.save(record) ;
    	}
        return;
    }
    
    @RequestMapping("/satis")
    @Menu(type = "im" , subtype = "satis" , access = true)
    public void satis(HttpServletRequest request , HttpServletResponse response, @Valid AgentServiceSatis satis) throws Exception {
    	if(satis!=null && !StringUtils.isBlank(satis.getId())){
    		int count = agentServiceSatisRes.countById(satis.getId()) ;
    		if(count == 1){
    			if(!StringUtils.isBlank(satis.getSatiscomment()) && satis.getSatiscomment().length() > 255){
    				satis.setSatiscomment(satis.getSatiscomment().substring(0 , 255));
    			}
    			satis.setSatisfaction(true);
    			satis.setSatistime(new Date());
    			agentServiceSatisRes.save(satis) ;
    		}
    	}
        return;
    }
    
    @RequestMapping("/image/upload")
    @Menu(type = "im" , subtype = "image" , access = true)
    public ModelAndView upload(ModelMap map,HttpServletRequest request , @RequestParam(value = "imgFile", required = false) MultipartFile imgFile , @Valid String channel, @Valid String userid, @Valid String username , @Valid String appid , @Valid String orgi, @Valid String paste) throws IOException {
    	ModelAndView view = request(super.createRequestPageTempletResponse("/apps/im/upload")) ; 
    	UploadStatus upload = null ;
    	String fileName = null ;
    	if(imgFile!=null && imgFile.getOriginalFilename().lastIndexOf(".") > 0 && !StringUtils.isBlank(userid)){
    		File uploadDir = new File(path , "upload");
    		if(!uploadDir.exists()){
    			uploadDir.mkdirs() ;
    		}
    		String fileid = UKTools.md5(imgFile.getBytes()) ;
    		if(imgFile.getContentType()!=null && imgFile.getContentType().indexOf("image") >= 0){
	    		fileName = "upload/"+fileid+"_original" ;
	    		File imageFile = new File(path , fileName) ;
	    		FileCopyUtils.copy(imgFile.getBytes(), imageFile);
	    		String thumbnailsFileName = "upload/"+fileid ;
	    		UKTools.processImage(new File(path , thumbnailsFileName), imageFile) ;
	    		
	    		
	    		upload = new UploadStatus("0" , "/res/image.html?id="+thumbnailsFileName);
	    		
	    		String image =  "/res/image.html?id="+thumbnailsFileName ;
	    		if(request.getServerPort() == 80){
	    			image = "/res/image.html?id="+thumbnailsFileName;
				}else{
					image = "/res/image.html?id="+thumbnailsFileName;
				}
	    		if(paste == null){
	    			if(!StringUtils.isBlank(channel)){
		    			MessageUtils.uploadImage(image , fileid ,(int)imgFile.getSize() , imgFile.getName() , channel, userid , username , appid , orgi);
		    		}else{
		    			MessageUtils.uploadImage(image , fileid ,(int)imgFile.getSize() , imgFile.getName() , userid);
		    		}
	    		}
    		}else{
    			
    			String id = processAttachmentFile(imgFile, request);
    			
    			upload = new UploadStatus("0" , "/res/file.html?id="+id);
    			String file =  "/res/file.html?id="+id ;
	    		if(request.getServerPort() == 80){
	    			file = "/res/file.html?id="+id;
				}else{
					file = "/res/file.html?id="+id;
				}
	    		File tempFile = new File(imgFile.getOriginalFilename());
	    		if(!StringUtils.isBlank(channel)){
	    			MessageUtils.uploadFile(file ,(int)imgFile.getSize() , tempFile.getName() , channel, userid , username , appid , orgi , id);
	    		}else{
	    			MessageUtils.uploadFile(file ,(int)imgFile.getSize() , tempFile.getName() , userid , id);
	    		}
    		}
    	}else{
    		upload = new UploadStatus("请选择文件");
    	}
    	map.addAttribute("upload", upload) ;
        return view ; 
    }
    
    private String processAttachmentFile(MultipartFile file , HttpServletRequest request) throws IOException{
    	String id = null ;
    	if(file.getSize() > 0){			//文件尺寸 限制 ？在 启动 配置中 设置 的最大值，其他地方不做限制
			String fileid = UKTools.md5(file.getBytes()) ;	//使用 文件的 MD5作为 ID，避免重复上传大文件
			if(!StringUtils.isBlank(fileid)){
    			AttachmentFile attachmentFile = new AttachmentFile() ;
    			attachmentFile.setCreater(super.getUser(request).getId());
    			attachmentFile.setOrgi(super.getOrgi(request));
    			attachmentFile.setOrgan(super.getUser(request).getOrgan());
    			attachmentFile.setModel(UKDataContext.ModelType.WEBIM.toString());
    			attachmentFile.setFilelength((int) file.getSize());
    			if(file.getContentType()!=null && file.getContentType().length() > 255){
    				attachmentFile.setFiletype(file.getContentType().substring(0 , 255));
    			}else{
    				attachmentFile.setFiletype(file.getContentType());
    			}
    			File uploadFile = new File(file.getOriginalFilename());
    			if(uploadFile.getName()!=null && uploadFile.getName().length() > 255){
    				attachmentFile.setTitle(uploadFile.getName().substring(0 , 255));
    			}else{
    				attachmentFile.setTitle(uploadFile.getName());
    			}
    			if(!StringUtils.isBlank(attachmentFile.getFiletype()) && attachmentFile.getFiletype().indexOf("image") >= 0){
    				attachmentFile.setImage(true);
    			}
    			attachmentFile.setFileid(fileid);
    			attachementRes.save(attachmentFile) ;
    			FileUtils.writeByteArrayToFile(new File(path , "app/webim/"+fileid), file.getBytes());
    			id = attachmentFile.getId();
			}
		}
    	return id  ;
    }
}