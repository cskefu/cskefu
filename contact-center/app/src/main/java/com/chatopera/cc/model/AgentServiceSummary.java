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
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "uk_servicesummary")
@Proxy(lazy = false)
public class AgentServiceSummary implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1435057931574776533L;
	private String agentusername;
	private String agentno;
	private String status;
	private long times;
	private Date servicetime;
	private Date createtime ;

	private String agentserviceid ;
	private String userid;
	
	private String statuseventid ;
	private String contactsid ;
	
	private String orgi;
	private String id;
	
	private String creater ;
	private String username;
	private String channel;
	private Date logindate;
	
	private String servicetype ;
	private boolean reservation ;
	private String reservtype ;
	
	private Date reservtime ;
	private String email ;
	private String phonenumber;
	
	private String ani ;
	private String caller;
	private String called ;
	private String agent ;
	
	private String summary ;	//服务小结 ， 备注

	private boolean process ;	//已处理
	private String updateuser ;	//处理人
	private Date updatetime ;	//处理时间
	private String processmemo;	//处理备注
	
	public String getAgentusername() {
		return agentusername;
	}

	public void setAgentusername(String agentusername) {
		this.agentusername = agentusername;
	}

	public String getAgentno() {
		return agentno;
	}

	public void setAgentno(String agentno) {
		this.agentno = agentno;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getTimes() {
		return times;
	}

	public void setTimes(long times) {
		this.times = times;
	}

	public Date getServicetime() {
		return servicetime;
	}

	public void setServicetime(Date servicetime) {
		this.servicetime = servicetime;
	}

	public String getOrgi() {
		return orgi;
	}

	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}

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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public Date getLogindate() {
		return logindate;
	}

	public void setLogindate(Date logindate) {
		this.logindate = logindate;
	}

	public String getServicetype() {
		return servicetype;
	}

	public void setServicetype(String servicetype) {
		this.servicetype = servicetype;
	}

	public boolean isReservation() {
		return reservation;
	}

	public void setReservation(boolean reservation) {
		this.reservation = reservation;
	}

	public String getReservtype() {
		return reservtype;
	}

	public void setReservtype(String reservtype) {
		this.reservtype = reservtype;
	}

	public Date getReservtime() {
		return reservtime;
	}

	public void setReservtime(Date reservtime) {
		this.reservtime = reservtime;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getAgentserviceid() {
		return agentserviceid;
	}

	public void setAgentserviceid(String agentserviceid) {
		this.agentserviceid = agentserviceid;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getStatuseventid() {
		return statuseventid;
	}

	public void setStatuseventid(String statuseventid) {
		this.statuseventid = statuseventid;
	}

	public String getContactsid() {
		return contactsid;
	}

	public void setContactsid(String contactsid) {
		this.contactsid = contactsid;
	}

	public String getAni() {
		return ani;
	}

	public void setAni(String ani) {
		this.ani = ani;
	}

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public String getCalled() {
		return called;
	}

	public void setCalled(String called) {
		this.called = called;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public boolean isProcess() {
		return process;
	}

	public void setProcess(boolean process) {
		this.process = process;
	}

	public String getUpdateuser() {
		return updateuser;
	}

	public void setUpdateuser(String updateuser) {
		this.updateuser = updateuser;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getProcessmemo() {
		return processmemo;
	}

	public void setProcessmemo(String processmemo) {
		this.processmemo = processmemo;
	}
}
