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
package com.chatopera.cc.webim.util.server.handler;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.chatopera.cc.core.UKDataContext;
import com.chatopera.cc.util.UKTools;
import com.chatopera.cc.util.client.NettyClients;
import com.chatopera.cc.webim.service.repository.ChatMessageRepository;
import com.chatopera.cc.webim.service.repository.IMGroupUserRepository;
import com.chatopera.cc.webim.service.repository.RecentUserRepository;
import com.chatopera.cc.webim.util.server.message.ChatMessage;
import com.chatopera.cc.webim.web.model.IMGroupUser;
import com.chatopera.cc.webim.web.model.MessageOutContent;
import com.chatopera.cc.webim.web.model.RecentUser;
import com.chatopera.cc.webim.web.model.User;

public class EntIMEventHandler     
{  
	protected SocketIOServer server;
	
    @Autowired  
    public EntIMEventHandler(SocketIOServer server)   
    {  
        this.server = server ;
    }  
    
    @OnConnect  
    public void onConnect(SocketIOClient client)  
    {  
    	try {
			String user = client.getHandshakeData().getSingleUrlParam("userid") ;
			String name = client.getHandshakeData().getSingleUrlParam("name") ;
			String group = client.getHandshakeData().getSingleUrlParam("group") ;
			String orgi = client.getHandshakeData().getSingleUrlParam("orgi") ;
			if(!StringUtils.isBlank(group)){
				client.joinRoom(group);
			}else{
				if(NettyClients.getInstance().getEntIMClientsNum(user) == 0){
					MessageOutContent outMessage = new MessageOutContent() ;
			    	outMessage.setMessage("online");
			    	outMessage.setNickName(name);
			    	outMessage.setContextid(user);
			    	outMessage.setMessageType(UKDataContext.MessageTypeEnum.MESSAGE.toString());
			    	outMessage.setCalltype(UKDataContext.CallTypeEnum.IN.toString());
					client.getNamespace().getBroadcastOperations().sendEvent("status", outMessage); //广播所有人，用户上线
				}
				if(!StringUtils.isBlank(user)){
					NettyClients.getInstance().putEntIMEventClient(user, client);
				}
				IMGroupUserRepository imGroupUserRes = UKDataContext.getContext().getBean(IMGroupUserRepository.class) ;
				User imUser = new User(user);
				List<IMGroupUser> imGroupUserList = imGroupUserRes.findByUserAndOrgi(imUser , orgi) ;
				for(IMGroupUser imGroupUser : imGroupUserList){
					if(imGroupUser.getImgroup()!=null){
						client.joinRoom(imGroupUser.getImgroup().getId());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }  
      
    @OnDisconnect  
    public void onDisconnect(SocketIOClient client)  
    {  
    	try {
			String user = client.getHandshakeData().getSingleUrlParam("userid") ;
			String name = client.getHandshakeData().getSingleUrlParam("name") ;
			String group = client.getHandshakeData().getSingleUrlParam("group") ;
			String orgi = client.getHandshakeData().getSingleUrlParam("orgi") ;
			if(!StringUtils.isBlank(group)){
				client.leaveRoom(group);
			}else{
				if(!StringUtils.isBlank(user)){
					NettyClients.getInstance().removeEntIMEventClient(user, client.getSessionId().toString());
				}
				if(NettyClients.getInstance().getEntIMClientsNum(user) == 0){
					MessageOutContent outMessage = new MessageOutContent() ;
			    	outMessage.setMessage("offline");
			    	outMessage.setNickName(name);
			    	outMessage.setContextid(user);
			    	outMessage.setMessageType(UKDataContext.MessageTypeEnum.MESSAGE.toString());
			    	outMessage.setCalltype(UKDataContext.CallTypeEnum.IN.toString());
					client.getNamespace().getBroadcastOperations().sendEvent("status", outMessage); //广播所有人，用户上线
				}
				
				IMGroupUserRepository imGroupUserRes = UKDataContext.getContext().getBean(IMGroupUserRepository.class) ;
				User imUser = new User(user);
				List<IMGroupUser> imGroupUserList = imGroupUserRes.findByUserAndOrgi(imUser , orgi) ;
				for(IMGroupUser imGroupUser : imGroupUserList){
					if(imGroupUser.getImgroup()!=null){
						client.leaveRoom(imGroupUser.getImgroup().getId());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }  
    
    @OnEvent(value = "message")  
    public void onEvent(SocketIOClient client, AckRequest request, ChatMessage data)   
    {
    	if(data.getType() == null){
    		data.setType("message");
    	}
    	String user = client.getHandshakeData().getSingleUrlParam("userid") ;
//		String name = client.getHandshakeData().getSingleUrlParam("name") ;
		String group = client.getHandshakeData().getSingleUrlParam("group") ;
		String orgi = client.getHandshakeData().getSingleUrlParam("orgi") ;
		
    	data.setUserid(user);
//		data.setUsername(name);
		data.setId(UKTools.getUUID());
		data.setUsession(user);	
		data.setCalltype(UKDataContext.CallTypeEnum.OUT.toString());
		
		
		if(UKDataContext.getContext()!=null){
			ChatMessageRepository chatMessageRes = UKDataContext.getContext().getBean(ChatMessageRepository.class) ;
			RecentUserRepository recentUserRes = UKDataContext.getContext().getBean(RecentUserRepository.class) ;
			
			if(!StringUtils.isBlank(group)){	//如果是群聊
				data.setContextid(group);
				chatMessageRes.save(data) ;
				client.getNamespace().getRoomOperations(group).sendEvent(UKDataContext.MessageTypeEnum.MESSAGE.toString(), data);
			}else{	//单聊
				data.setContextid(data.getTouser());
				chatMessageRes.save(data) ;
				NettyClients.getInstance().sendEntIMEventMessage(data.getUserid(), UKDataContext.MessageTypeEnum.MESSAGE.toString(), data);	//同时将消息发送给自己
				data.setCalltype(UKDataContext.CallTypeEnum.IN.toString());
				data.setContextid(user);
				data.setUserid(data.getTouser());
//				data.setId(null);
				chatMessageRes.save(data) ; 	//每条消息存放两条，一个是我的对话记录 ， 另一条是对方的对话历史， 情况当前聊天记录的时候，只清理自己的
				NettyClients.getInstance().sendEntIMEventMessage(data.getTouser(), UKDataContext.MessageTypeEnum.MESSAGE.toString(), data);	//发送消息给目标用户
				
				RecentUser recentUser = recentUserRes.findByCreaterAndUserAndOrgi(data.getTouser() , new User(user) , orgi) ;
				if(recentUser!=null){
					recentUser.setNewmsg(recentUser.getNewmsg()+1);
					if(data.getMessage()!=null && data.getMessage().length()>50){
						recentUser.setLastmsg(data.getMessage().substring(0 , 50));
					}else{
						recentUser.setLastmsg(data.getMessage());
					}
					recentUserRes.save(recentUser) ;
				}
			}
		}
    } 
}  