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
package com.chatopera.cc.app.im.handler;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;

import com.chatopera.cc.app.MainContext;
import com.chatopera.cc.app.service.cache.CacheHelper;
import com.chatopera.cc.app.im.message.ChatMessage;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.chatopera.cc.util.UKTools;
import com.chatopera.cc.app.im.client.NettyClients;
import com.chatopera.cc.app.service.acd.ServiceQuene;
import com.chatopera.cc.app.service.impl.AgentUserService;
import com.chatopera.cc.app.service.repository.AgentServiceRepository;
import com.chatopera.cc.app.service.repository.ConsultInviteRepository;
import com.chatopera.cc.util.MessageUtils;
import com.chatopera.cc.util.OnlineUserUtils;
import com.chatopera.cc.app.im.message.AgentStatusMessage;
import com.chatopera.cc.app.im.message.NewRequestMessage;
import com.chatopera.cc.app.model.AgentService;
import com.chatopera.cc.app.model.AgentUser;
import com.chatopera.cc.app.model.Contacts;
import com.chatopera.cc.app.model.CousultInvite;
import com.chatopera.cc.app.model.MessageOutContent;

public class IMEventHandler     
{  
	protected SocketIOServer server;
	
    @Autowired  
    public IMEventHandler(SocketIOServer server)   
    {  
        this.server = server ;
    }  
    
    @OnConnect  
    public void onConnect(SocketIOClient client)  
    {  
    	try {
			String user = client.getHandshakeData().getSingleUrlParam("userid") ;
			String orgi = client.getHandshakeData().getSingleUrlParam("orgi") ;
			String session = client.getHandshakeData().getSingleUrlParam("session") ;
			String appid = client.getHandshakeData().getSingleUrlParam("appid") ;
			String agent = client.getHandshakeData().getSingleUrlParam("agent") ;
			String skill = client.getHandshakeData().getSingleUrlParam("skill") ;
			
			String title = client.getHandshakeData().getSingleUrlParam("title") ;
			String url = client.getHandshakeData().getSingleUrlParam("url") ;
			String traceid = client.getHandshakeData().getSingleUrlParam("traceid") ;
			
			String nickname = client.getHandshakeData().getSingleUrlParam("nickname") ;
			
			if(!StringUtils.isBlank(user)){
				/**
				 * 用户进入到对话连接 ， 排队用户请求 , 如果返回失败，表示当前坐席全忙，用户进入排队状态，当前提示信息 显示 当前排队的队列位置，不可进行对话，用户发送的消息作为留言处理
				 */
				InetSocketAddress address = (InetSocketAddress) client.getRemoteAddress()  ;
				String ip = UKTools.getIpAddr(client.getHandshakeData().getHttpHeaders(), address.getHostString()) ;
				NewRequestMessage newRequestMessage = OnlineUserUtils.newRequestMessage(user, orgi , session , appid , ip , client.getHandshakeData().getSingleUrlParam("osname") , client.getHandshakeData().getSingleUrlParam("browser") , MainContext.ChannelTypeEnum.WEBIM.toString() , skill , agent , nickname , title , url , traceid , MainContext.ChatInitiatorType.USER.toString()) ;
//				/**
//				 * 加入到 缓存列表
//				 */
				NettyClients.getInstance().putIMEventClient(user, client);
//				
				if(newRequestMessage!=null && !StringUtils.isBlank(newRequestMessage.getMessage())){
					MessageOutContent outMessage = new MessageOutContent() ;
			    	outMessage.setMessage(newRequestMessage.getMessage());
			    	outMessage.setMessageType(MainContext.MessageTypeEnum.MESSAGE.toString());
			    	outMessage.setCalltype(MainContext.CallTypeEnum.IN.toString());
			    	outMessage.setNickName(newRequestMessage.getUsername());
					outMessage.setCreatetime(UKTools.dateFormate.format(new Date()));
					outMessage.setAgentserviceid(newRequestMessage.getAgentserviceid());
					
					outMessage.setNoagent(newRequestMessage.isNoagent());
					
					client.sendEvent(MainContext.MessageTypeEnum.STATUS.toString(), outMessage);
				}
			}else{//非法链接
				client.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
			client.disconnect();
		}
    }  
      
    //添加@OnDisconnect事件，客户端断开连接时调用，刷新客户端信息  
    @OnDisconnect  
    public void onDisconnect(SocketIOClient client)  
    {  
    	String user = client.getHandshakeData().getSingleUrlParam("userid") ;
		String orgi = client.getHandshakeData().getSingleUrlParam("orgi") ;
		if(user!=null){
			try {
				/**
				 * 用户主动断开服务
				 */
				ServiceQuene.serviceFinish((AgentUser) CacheHelper.getAgentUserCacheBean().getCacheObject(user, MainContext.SYSTEM_ORGI), orgi);
			} catch (Exception e) {
				e.printStackTrace();
			}
			NettyClients.getInstance().removeIMEventClient(user ,UKTools.getContextID(client.getSessionId().toString()));
		}
    }  
      
    //消息接收入口，用于接受网站资源用户传入的 个人信息
    @OnEvent(value = "new")  
    public void onEvent(SocketIOClient client, AckRequest request, Contacts contacts)   
    {
    	String user = client.getHandshakeData().getSingleUrlParam("userid") ;
		String orgi = client.getHandshakeData().getSingleUrlParam("orgi") ;
		AgentUser agentUser = (AgentUser) CacheHelper.getAgentUserCacheBean().getCacheObject(user, orgi) ;
		AgentUserService service = MainContext.getContext().getBean(
				AgentUserService.class);
		if(agentUser == null){
			agentUser = service.findByUseridAndOrgi(user , orgi);
		}
		if(agentUser!=null){
			agentUser.setName(contacts.getName());
			agentUser.setPhone(contacts.getPhone());
			agentUser.setEmail(contacts.getEmail());
			agentUser.setResion(contacts.getMemo());
			service.save(agentUser);
			CacheHelper.getAgentUserCacheBean().put(agentUser.getUserid(), agentUser , MainContext.SYSTEM_ORGI) ;
		}
			
		AgentServiceRepository agentServiceRes = MainContext.getContext().getBean(AgentServiceRepository.class) ;
		List<AgentService> agentServiceList = agentServiceRes.findByUseridAndOrgi(user, orgi) ;
		if(agentServiceList.size() > 0){
			AgentService agentService = agentServiceList.get(0) ;
			agentService.setName(contacts.getName());
			agentService.setPhone(contacts.getName());
			agentService.setEmail(contacts.getName());
			agentService.setRegion(contacts.getMemo());
			agentServiceRes.save(agentService) ;
		}
    }  
    
  //消息接收入口，坐席状态更新  
    @OnEvent(value = "agentstatus")  
    public void onEvent(SocketIOClient client, AckRequest request, AgentStatusMessage data)   
    {
    	System.out.println(data.getMessage());
    } 
    
    //消息接收入口，收发消息，用户向坐席发送消息和 坐席向用户发送消息  
    @OnEvent(value = "message")  
    public void onEvent(SocketIOClient client, AckRequest request, ChatMessage data) throws UnsupportedEncodingException
    {
    	if(data.getType() == null){
    		data.setType("message");
    	}
    	/**
    	 * 以下代码主要用于检查 访客端的字数限制
    	 */
    	CousultInvite invite = OnlineUserUtils.cousult(data.getAppid(),data.getOrgi(), MainContext.getContext().getBean(ConsultInviteRepository.class));
    	if(invite!=null && invite.getMaxwordsnum() > 0) {
	    	if(!StringUtils.isBlank(data.getMessage()) && data.getMessage().length() > invite.getMaxwordsnum()){
	    		data.setMessage(data.getMessage().substring(0 , invite.getMaxwordsnum()));
	    	}
    	}else if(!StringUtils.isBlank(data.getMessage()) && data.getMessage().length() > 300){
    		data.setMessage(data.getMessage().substring(0 , 300));
    	}
    	/**
		 * 处理表情
		 */
    	data.setMessage(UKTools.processEmoti(data.getMessage()));
		
    	MessageUtils.createMessage(data , MainContext.MediaTypeEnum.TEXT.toString(), data.getUserid());
    } 
}  