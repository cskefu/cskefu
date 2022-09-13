/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
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
package com.cskefu.cc.util.freeswitch.model;

import com.cskefu.cc.model.Extension;

import java.util.Date;

public class CallCenterAgent implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -884536468331333053L;
	private String userid ;
	private String extno ;
	
	private String orgi ;
	private Date updatetime ;
	private String status ;
	private String workstatus ;
	private String siptrunk ;
	
	private String eventid ;
	
	private Extension extension;
	
	private String nameid ;
	
	public CallCenterAgent(String userid, String extno , String orgi) {
		this.userid = userid ;
		this.extno = extno ;
		this.orgi = orgi ;
	}
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getExtno() {
		return extno;
	}
	public void setExtno(String extno) {
		this.extno = extno;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getOrgi() {
		return orgi;
	}

	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}

	public String getEventid() {
		return eventid;
	}

	public void setEventid(String eventid) {
		this.eventid = eventid;
	}

	public Extension getExtension() {
		return extension;
	}

	public void setExtension(Extension extension) {
		this.extension = extension;
	}

	public String getWorkstatus() {
		return workstatus;
	}

	public void setWorkstatus(String workstatus) {
		this.workstatus = workstatus;
	}

	public String getNameid() {
		return nameid;
	}

	public void setNameid(String nameid) {
		this.nameid = nameid;
	}

	public String getSiptrunk() {
		return siptrunk;
	}

	public void setSiptrunk(String siptrunk) {
		this.siptrunk = siptrunk;
	}
}
