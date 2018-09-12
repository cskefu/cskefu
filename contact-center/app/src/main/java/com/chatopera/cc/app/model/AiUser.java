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
package com.chatopera.cc.app.model;

import com.chatopera.cc.util.IP;

public class AiUser implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id ;
	private String userid ;
	private long time ;
	private IP ipdata ;
	private String orgi;
	private String agentserviceid ;
	private String sessionid ;
	
	private String contextid ;
	private String appid ;
	private String channel ;
	private String username ;
	private String aiid ;
	
	private String busstype ;
	private String aitype ;
	private String bussid ;
	private String dataid ;
	private boolean bussend;
	
	private int userask ;	//访客提问数量
	private boolean agent ; //直接转人工
	
	private int timeoutnums ;	//超时次数
	private int retimes ;		//重复次数
	private int errortimes ;	//错误次数
	
	public AiUser(String id , String userid, long time,String orgi , IP ipdata){
		this.id = id.replace("-", "") ;
		this.userid = userid ;
		this.time = time ;
		this.ipdata = ipdata ;
		this.orgi = orgi;
	}
	
	public String getOrgi() {
		return orgi;
	}

	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}

	public IP getIpdata() {
		return ipdata;
	}

	public void setIpdata(IP ipdata) {
		this.ipdata = ipdata;
	}

	public String getAgentserviceid() {
		return agentserviceid;
	}

	public void setAgentserviceid(String agentserviceid) {
		this.agentserviceid = agentserviceid;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public int getUserask() {
		return userask;
	}

	public void setUserask(int userask) {
		this.userask = userask;
	}

	public boolean isAgent() {
		return agent;
	}

	public void setAgent(boolean agent) {
		this.agent = agent;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getAiid() {
		return aiid;
	}

	public void setAiid(String aiid) {
		this.aiid = aiid;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getContextid() {
		return contextid;
	}

	public void setContextid(String contextid) {
		this.contextid = contextid;
	}

	public String getBusstype() {
		return busstype;
	}

	public void setBusstype(String busstype) {
		this.busstype = busstype;
	}

	public String getAitype() {
		return aitype;
	}

	public void setAitype(String aitype) {
		this.aitype = aitype;
	}

	public String getBussid() {
		return bussid;
	}

	public void setBussid(String bussid) {
		this.bussid = bussid;
	}

	public String getDataid() {
		return dataid;
	}

	public void setDataid(String dataid) {
		this.dataid = dataid;
	}

	public boolean isBussend() {
		return bussend;
	}

	public void setBussend(boolean bussend) {
		this.bussend = bussend;
	}

	public int getTimeoutnums() {
		return timeoutnums;
	}

	public void setTimeoutnums(int timeoutnums) {
		this.timeoutnums = timeoutnums;
	}

	public int getRetimes() {
		return retimes;
	}

	public void setRetimes(int retimes) {
		this.retimes = retimes;
	}

	public int getErrortimes() {
		return errortimes;
	}

	public void setErrortimes(int errortimes) {
		this.errortimes = errortimes;
	}
}
