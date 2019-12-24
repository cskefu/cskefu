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
package com.chatopera.cc.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "uk_blacklist")
@org.hibernate.annotations.Proxy(lazy = false)
public class BlackEntity implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7852633351774613958L;
	private String id ;
	private String orgi ;			//orgi
	private String userid ;			//加黑用户ID
	private String contactid ;		//联系人ID
	private String sessionid ;		//当前会话
	private Date createtime = new Date() ;		//加黑时间
	
	private int controltime ;	//加黑时间，1小时~N小时
	private Date endtime ;			//结束时间
	
	private String agentuser ;		//用户名
	
	private String channel ;		//渠道
	private String creater ;		//创建人，和 加黑坐席同一个人
	private String agentid ;		//加黑坐席
	private String phone ;			//用户电话
	private String openid ;			//用户openid
	private String agentserviceid ;	//agent service id		
	private String description ;	//备注黑名单原因
	private int times ;				//对话次数
	private int chattime ;     //最后一次对话时长
	
	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getContactid() {
		return contactid;
	}
	public void setContactid(String contactid) {
		this.contactid = contactid;
	}
	public String getSessionid() {
		return sessionid;
	}
	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getCreater() {
		return creater;
	}
	public void setCreater(String creater) {
		this.creater = creater;
	}
	public String getAgentid() {
		return agentid;
	}
	public void setAgentid(String agentid) {
		this.agentid = agentid;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAgentserviceid() {
		return agentserviceid;
	}
	public void setAgentserviceid(String agentserviceid) {
		this.agentserviceid = agentserviceid;
	}
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	public int getChattime() {
		return chattime;
	}
	public void setChattime(int chattime) {
		this.chattime = chattime;
	}
	public int getControltime() {
		return controltime;
	}
	public void setControltime(int controltime) {
		this.controltime = controltime;
	}
	public Date getEndtime() {
		return endtime;
	}
	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}
	public String getAgentuser() {
		return agentuser;
	}
	public void setAgentuser(String agentuser) {
		this.agentuser = agentuser;
	}
}
