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
package com.chatopera.cc.webim.util.router;

import com.chatopera.cc.core.UKDataContext;
import com.chatopera.cc.util.UKTools;
import com.chatopera.cc.util.client.NettyClients;
import com.chatopera.cc.webim.service.acd.ServiceQuene;
import com.chatopera.cc.webim.web.model.AgentService;
import com.chatopera.cc.webim.web.model.MessageDataBean;
import com.chatopera.cc.webim.web.model.MessageOutContent;

public class MessageRouter extends Router{

	@Override
	public MessageDataBean handler(MessageDataBean inMessage) {
		MessageOutContent outMessage = new MessageOutContent() ;
		try {
			outMessage.setOrgi(inMessage.getOrgi());
			outMessage.setFromUser(inMessage.getToUser());
			outMessage.setToUser(inMessage.getFromUser());
			outMessage.setId(UKTools.genID());
			outMessage.setMessageType(inMessage.getMessageType());
			outMessage.setUser(inMessage.getUser());
			outMessage.setAgentUser(inMessage.getAgentUser());
			/**
			 * 首先交由 IMR处理 MESSAGE指令 ， 如果当前用户是在 坐席对话列表中， 则直接推送给坐席，如果不在，则执行 IMR
			 */
			if(outMessage.getAgentUser()!=null && outMessage.getAgentUser().getStatus()!=null){
				if(outMessage.getAgentUser().getStatus().equals(UKDataContext.AgentUserStatusEnum.INQUENE.toString())){
					int queneIndex = ServiceQuene.getQueneIndex(inMessage.getAgentUser().getAgent() , inMessage.getOrgi(), inMessage.getAgentUser().getSkill()) ;
					if(UKDataContext.AgentUserStatusEnum.INQUENE.toString().equals(outMessage.getAgentUser().getStatus())){
						outMessage.setMessage(ServiceQuene.getQueneMessage(queneIndex , outMessage.getAgentUser().getChannel(),inMessage.getOrgi()));
					}
				}else if(outMessage.getAgentUser().getStatus().equals(UKDataContext.AgentUserStatusEnum.INSERVICE.toString())){
					
				}
			}else if(UKDataContext.MessageTypeEnum.NEW.toString().equals(inMessage.getMessageType())){
				/**
				 * 找到空闲坐席，如果未找到坐席， 则将该用户放入到 排队队列 
				 * 
				 */
				AgentService agentService = ServiceQuene.allotAgent(inMessage.getAgentUser(), inMessage.getOrgi()) ;
				if(agentService!=null && UKDataContext.AgentUserStatusEnum.INSERVICE.toString().equals(agentService.getStatus())){
					outMessage.setMessage(ServiceQuene.getSuccessMessage(agentService , inMessage.getAgentUser().getChannel(),inMessage.getOrgi()));
					NettyClients.getInstance().sendAgentEventMessage(agentService.getAgentno(), UKDataContext.MessageTypeEnum.NEW.toString(), inMessage.getAgentUser());
				}else{
					if(agentService.getQueneindex() > 0){	//当前有坐席
						outMessage.setMessage(ServiceQuene.getQueneMessage(agentService.getQueneindex(), inMessage.getAgentUser().getChannel(), inMessage.getOrgi()));
					}else{
						outMessage.setMessage(ServiceQuene.getNoAgentMessage(agentService.getQueneindex(), inMessage.getAgentUser().getChannel(), inMessage.getOrgi()));
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return outMessage ;
	}

}
