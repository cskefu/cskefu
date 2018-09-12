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
package com.chatopera.cc.util;

import java.util.Date;

import com.chatopera.cc.app.im.client.NettyClients;
import com.chatopera.cc.app.service.cache.CacheHelper;
import com.chatopera.cc.app.service.repository.AgentUserTaskRepository;
import com.chatopera.cc.app.im.message.ChatMessage;
import com.chatopera.cc.app.model.AgentUser;
import org.apache.commons.lang.StringUtils;

import com.chatopera.cc.app.MainContext;
import com.chatopera.cc.app.service.repository.ChatMessageRepository;
import com.chatopera.cc.app.model.AgentUserTask;
import com.chatopera.cc.app.model.AiUser;
import com.chatopera.cc.app.model.MessageOutContent;

public class MessageUtils {
	/**
	 * 
	 * @param image
	 * @param userid
	 */
	public static ChatMessage uploadImage(String image , String attachid, int size , String name  , String userid){
		return createMessage(image , size , name , MainContext.MediaTypeEnum.IMAGE.toString(), userid , attachid);
	}
	public static ChatMessage uploadImage(String image  , String attachid, int size , String name , String channel , String userid , String username , String appid , String orgi){
		return createMessage(image , size , name , channel , MainContext.MediaTypeEnum.IMAGE.toString(), userid , username, appid , orgi , attachid);
	}
	
	/**
	 * 
	 * @param image
	 * @param userid
	 */
	public static ChatMessage uploadFile(String url , int size , String name , String userid , String attachid){
		return createMessage(url , size , name , MainContext.MediaTypeEnum.FILE.toString(), userid , attachid);
	}
	public static ChatMessage uploadFile(String url , int size , String name, String channel , String userid , String username , String appid , String orgi , String attachid){
		return createMessage(url , size , name , channel , MainContext.MediaTypeEnum.FILE.toString(), userid , username, appid , orgi , attachid);
	}
	public static ChatMessage createMessage(String message , int length , String name , String msgtype , String userid , String attachid){
		AgentUser agentUser = (AgentUser) CacheHelper.getAgentUserCacheBean().getCacheObject(userid, MainContext.SYSTEM_ORGI);
		ChatMessage data = new ChatMessage() ;
		data.setFilesize(length);
		data.setFilename(name);
		data.setAttachmentid(attachid);
		
		data.setMessage(message);
		
		data.setMsgtype(msgtype);
		
		data.setType(MainContext.MessageTypeEnum.MESSAGE.toString());
		
		if(agentUser != null){
			data.setUserid(agentUser.getUserid());
			data.setUsername(agentUser.getUsername());
			data.setTouser(agentUser.getAgentno());
			data.setAppid(agentUser.getAppid());
			data.setOrgi(agentUser.getOrgi());
			createMessage(data, msgtype, userid);
		}else {
			AiUser aiUser = (AiUser) CacheHelper.getOnlineUserCacheBean().getCacheObject(userid, MainContext.SYSTEM_ORGI) ;
			data.setUserid(userid);
			data.setAppid(aiUser.getAppid());
			data.setAiid(aiUser.getAiid());
			data.setUsername(aiUser.getUsername());
			data.setOrgi(aiUser.getOrgi());
			createAiMessage(data , data.getAppid() , aiUser.getChannel() , MainContext.CallTypeEnum.IN.toString() , MainContext.AiItemType.USERINPUT.toString() , MainContext.MediaTypeEnum.IMAGE.toString(), data.getUserid());
			sendMessage(data, msgtype);
			UKTools.chatbot(data);
		}
		return data ;
	}
	/**
	 * 发送消息
	 * @param data
	 * @param msgtype
	 */
	private static void sendMessage(ChatMessage data , String msgtype) {
		MessageOutContent outMessage = new MessageOutContent() ;
    	
    	outMessage.setMessage(data.getMessage());
    	outMessage.setFilename(data.getFilename());
    	outMessage.setFilesize(data.getFilesize());
    	
    	
    	outMessage.setMessageType(msgtype);
    	outMessage.setCalltype(MainContext.CallTypeEnum.IN.toString());
    	outMessage.setSnsAccount(null);
    	
    	outMessage.setContextid(data.getContextid());
		outMessage.setFromUser(data.getUserid());
		outMessage.setToUser(data.getTouser());
		outMessage.setChannelMessage(data);
		outMessage.setNickName(data.getUsername());
		outMessage.setCreatetime(data.getCreatetime());
    	
		if(!StringUtils.isBlank(data.getUserid()) && MainContext.MessageTypeEnum.MESSAGE.toString().equals(data.getType())){
    		NettyClients.getInstance().sendIMEventMessage(data.getUserid(), MainContext.MessageTypeEnum.MESSAGE.toString(), outMessage);
    	}
	}
	
	public static ChatMessage createMessage(String message , int length , String name ,String channel ,String msgtype , String userid , String username , String appid , String orgi , String attachid){
		ChatMessage data = new ChatMessage() ;
		if(!StringUtils.isBlank(userid)){
			data.setUserid(userid);
			data.setUsername(username);
			data.setTouser(userid);
			data.setAppid(appid);
			data.setOrgi(orgi);
			data.setChannel(channel);
			data.setMessage(message);
			
			data.setFilesize(length);
			data.setFilename(name);
			data.setAttachmentid(attachid);
			
			data.setMsgtype(msgtype);
			
			data.setType(MainContext.MessageTypeEnum.MESSAGE.toString());
			createAiMessage(data , appid , channel, MainContext.CallTypeEnum.IN.toString() , MainContext.AiItemType.USERINPUT.toString() , msgtype, data.getUserid());
		}
		return data ;
	}
	
	public static void createMessage(ChatMessage data , String msgtype , String userid){
		AgentUser agentUser = (AgentUser) CacheHelper.getAgentUserCacheBean().getCacheObject(userid, MainContext.SYSTEM_ORGI);
    	MessageOutContent outMessage = new MessageOutContent() ;
    	
    	outMessage.setMessage(data.getMessage());
    	outMessage.setFilename(data.getFilename());
    	outMessage.setFilesize(data.getFilesize());
    	
    	
    	outMessage.setMessageType(msgtype);
    	outMessage.setCalltype(MainContext.CallTypeEnum.IN.toString());
    	outMessage.setAgentUser(agentUser);
    	outMessage.setSnsAccount(null);
    	
    	MessageOutContent statusMessage = null ;
    	if(agentUser==null){
    		statusMessage = new MessageOutContent() ;
    		statusMessage.setMessage(data.getMessage());
    		statusMessage.setMessageType(MainContext.MessageTypeEnum.STATUS.toString());
    		statusMessage.setCalltype(MainContext.CallTypeEnum.OUT.toString());
    		statusMessage.setMessage("当前坐席全忙，请稍候");
    	}else{
    		data.setUserid(agentUser.getUserid());
    		data.setUsername(agentUser.getUsername());
    		data.setTouser(agentUser.getAgentno());
    		
    		data.setAgentuser(agentUser.getId());
    		
    		data.setAgentserviceid(agentUser.getAgentserviceid());
    		
    		data.setAppid(agentUser.getAppid());
    		data.setOrgi(agentUser.getOrgi());
    		
    		data.setMsgtype(msgtype);
    		
    		
    		data.setUsername(agentUser.getUsername());
    		data.setUsession(agentUser.getUserid());				//agentUser作为 session id
    		data.setContextid(agentUser.getContextid());
    		data.setCalltype(MainContext.CallTypeEnum.IN.toString());
    		if(!StringUtils.isBlank(agentUser.getAgentno())){
    			data.setTouser(agentUser.getAgentno());
    		}
    		data.setChannel(agentUser.getChannel());
    		
    		outMessage.setContextid(agentUser.getContextid());
    		outMessage.setFromUser(data.getUserid());
    		outMessage.setToUser(data.getTouser());
    		outMessage.setChannelMessage(data);
    		outMessage.setNickName(agentUser.getUsername());
    		outMessage.setCreatetime(data.getCreatetime());
    		
    		if(data.getType()!=null && data.getType().equals(MainContext.MessageTypeEnum.MESSAGE.toString())){
	    		AgentUserTaskRepository agentUserTaskRes = MainContext.getContext().getBean(AgentUserTaskRepository.class) ;
	    		AgentUserTask agentUserTask = agentUserTaskRes.getOne(agentUser.getId()) ;
	    		if(agentUserTask!=null){
	    			if(agentUserTask.getLastgetmessage() != null && agentUserTask.getLastmessage()!=null){
		    			data.setLastagentmsgtime(agentUserTask.getLastgetmessage());
		    			data.setLastmsgtime(agentUserTask.getLastmessage());
		    			data.setAgentreplyinterval((int)((System.currentTimeMillis() - agentUserTask.getLastgetmessage().getTime())/1000));	//坐席上次回复消息的间隔
		    			data.setAgentreplytime((int)((System.currentTimeMillis() - agentUserTask.getLastmessage().getTime())/1000));		//坐席回复消息花费时间
	    			}
	    			agentUserTask.setUserasks(agentUserTask.getUserasks()+1);	//总咨询记录数量
	    			agentUserTask.setAgentreplytime(agentUserTask.getAgentreplytime() + data.getAgentreplyinterval());	//总时长
	    			if(agentUserTask.getUserasks()>0){
	    				agentUserTask.setAvgreplytime(agentUserTask.getAgentreplytime() / agentUserTask.getUserasks());
	    			}
	    			
		    		agentUserTask.setLastmessage(new Date());
		    		agentUserTask.setWarnings("0");
		    		agentUserTask.setWarningtime(null);
		    		
		    		/**
		    		 * 去掉坐席超时回复消息提醒
		    		 */
		    		agentUserTask.setReptime(null);
		    		agentUserTask.setReptimes("0");
		    		
		    		agentUserTask.setLastmsg(data.getMessage().length() > 100 ? data.getMessage().substring(0 , 100) : data.getMessage());
		    		agentUserTask.setTokenum(agentUserTask.getTokenum()+1);
		    		data.setTokenum(agentUserTask.getTokenum());
		    		agentUserTaskRes.save(agentUserTask) ;
	    		}
    		}
    		
    		/**
    		 * 保存消息
    		 */
    		if(MainContext.MessageTypeEnum.MESSAGE.toString().equals(data.getType())){
    			MainContext.getContext().getBean(ChatMessageRepository.class).save(data) ;
    		}
    	}
    	if(!StringUtils.isBlank(data.getUserid()) && MainContext.MessageTypeEnum.MESSAGE.toString().equals(data.getType())){
    		NettyClients.getInstance().sendIMEventMessage(data.getUserid(), MainContext.MessageTypeEnum.MESSAGE.toString(), outMessage);
    		if(statusMessage!=null){
    			NettyClients.getInstance().sendIMEventMessage(data.getUserid(), MainContext.MessageTypeEnum.STATUS.toString(), statusMessage);
    		}
    	}
    	if(agentUser!=null && !StringUtils.isBlank(agentUser.getAgentno())){
    		//将消息发送给 坐席
    		NettyClients.getInstance().sendAgentEventMessage(agentUser.getAgentno(), MainContext.MessageTypeEnum.MESSAGE.toString(), data);
    	}
	}
	
	public static ChatMessage createMessage(String message , int length , String name ,String channel ,String msgtype , String userid , String username , String appid , String orgi , String attachid ,String aiid){
		ChatMessage data = new ChatMessage() ;
		if(!StringUtils.isBlank(userid)){
			data.setUserid(userid);
			data.setUsername(username);
			data.setTouser(userid);
			data.setAppid(appid);
			data.setOrgi(orgi);
			data.setChannel(channel);
			data.setMessage(message);
			
			data.setAiid(aiid);
			
			data.setFilesize(length);
			data.setFilename(name);
			data.setAttachmentid(attachid);
			
			data.setMsgtype(msgtype);
			
			data.setType(MainContext.MessageTypeEnum.MESSAGE.toString());
			createAiMessage(data , appid , channel, MainContext.CallTypeEnum.IN.toString() , MainContext.AiItemType.USERINPUT.toString() , msgtype, data.getUserid());
		}
		return data ;
	}
	
	public static MessageOutContent createAiMessage(ChatMessage data , String appid,String channel , String direction , String chatype, String msgtype , String userid){
    	MessageOutContent outMessage = new MessageOutContent() ;
    	
    	outMessage.setMessage(data.getMessage());
    	outMessage.setMessageType(msgtype);
    	outMessage.setCalltype(direction);
    	outMessage.setAgentUser(null);
    	outMessage.setSnsAccount(null);
    	
    	{
    		data.setUserid(userid);
    		data.setUsername(data.getUsername());
    		data.setTouser(userid);
    		
    		data.setAgentuser(userid);
    		
    		
    		data.setAgentserviceid(data.getContextid());
    		data.setChatype(chatype);
    		
    		data.setChannel(channel);
    		
    		data.setAppid(data.getAppid());
    		data.setOrgi(data.getOrgi());
    		
    		data.setMsgtype(msgtype);
    		
    		data.setUsername(data.getUsername());
    		data.setUsession(data.getUserid());				//agentUser作为 session id
    		data.setContextid(data.getContextid());
    		data.setCalltype(direction);
    		
    		outMessage.setContextid(data.getContextid());
    		outMessage.setFromUser(data.getUserid());
    		outMessage.setToUser(data.getTouser());
    		outMessage.setChannelMessage(data);
    		outMessage.setNickName(data.getUsername());
    		outMessage.setCreatetime(data.getCreatetime());
    		
    		if(!StringUtils.isBlank(data.getSuggestmsg())) {
    			outMessage.setSuggest(data.getSuggest());
    		}
    		
    		data.setUpdatetime(System.currentTimeMillis());
    		
    		
    		/**
    		 * 保存消息
    		 */
    		if(MainContext.MessageTypeEnum.MESSAGE.toString().equals(data.getType())){
    			MainContext.getContext().getBean(ChatMessageRepository.class).save(data) ;
    		}
    		AgentUser agentUser = (AgentUser) CacheHelper.getAgentUserCacheBean().getCacheObject(userid, MainContext.SYSTEM_ORGI);
    		if(agentUser!=null && !StringUtils.isBlank(agentUser.getAgentno())){
        		//将消息发送给 坐席
    			
    			if(MainContext.CallTypeEnum.OUT.toString().equals(direction)) {
    				data.setUserid(agentUser.getAgentno());
    			}
        		NettyClients.getInstance().sendAgentEventMessage(agentUser.getAgentno(), MainContext.MessageTypeEnum.MESSAGE.toString(), data);
        	}
    	}
    	return outMessage ;
	}
	
}
