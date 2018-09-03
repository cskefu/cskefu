
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

package com.chatopera.cc.webim.web.model;


public interface MessageDataBean {
	
	public String getId() ;
	
	public String getNickName();
	
	
	public String getOrgi() ;
	
	/**
	 * 对话的文本内容
	 * @return
	 */
	public String getMessage() ;
	
	/**
	 * 消息类型
	 * @return
	 */
	public String getMessageType() ;
	
	/**
	 * 来源用户
	 * @return
	 */
	public String getFromUser() ;
	
	/**
	 * 目标用户
	 * @return
	 */
	public String getToUser();
	/**
	 * 渠道信息
	 * @return
	 */
	public SNSAccount getSnsAccount();
	/**
	 * 坐席用户信息
	 * @return
	 */
	public AgentUser getAgentUser() ;
	
	/**
	 * 获取渠道来源的消息信息
	 * @return
	 */
	public Object getChannelMessage() ;
	
	/**
	 * 渠道上对应的用户信息
	 * @return
	 */
	public Object getUser();
	
	
	public void setContextid(String contextid) ;
	
	public String getContextid() ;
	
}
