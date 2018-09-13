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

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "uk_log")
@org.hibernate.annotations.Proxy(lazy = false)
public class Log implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4749028407912157882L;
	private String id ;
	private String orgi;
	private String flowid;
	private String logtype ;
	private Date createdate ;
	private String msg;
	private String levels ;
	private String thread ;
	private String clazz ;
	private String files ;
	private String linenumber ;
	private String  method ;
	private String startid ;
	private String errorinfo ;
	private String triggerwarning = "false";
	private String   triggertime ;
	private int 	triggertimes ;
	private String logtime ;
	
	private String name;			//备用字段
	private String code ;			//备用字段
	private String userid;			//备用字段
	private String username;		//备用字段
	private String memo ;			//备用字段
	private String ipaddr;			//ip
	private String port ;		
	public Log(){}
	public Log(String orgi , String flowid , String msg,String levels , String thread){
		this.id = String.valueOf(System.nanoTime()) ;
		this.orgi = orgi ;
		this.flowid = flowid ;
		this.createdate = new Date();
		this.msg = msg ;
		this.levels = levels;
		this.thread = thread ;
	}
	/**
	 * @return the id
	 */
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
	public String getFlowid() {
		return flowid;
	}
	public void setFlowid(String flowid) {
		this.flowid = flowid;
	}
	
	public Date getCreatedate() {
		return createdate;
	}
	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}
	public String getMsg() {
		return msg ;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getLevels() {
		return levels;
	}
	public void setLevels(String levels) {
		this.levels = levels;
	}
	public String getThread() {
		return thread;
	}
	public void setThread(String thread) {
		this.thread = thread;
	}
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	public String getFiles() {
		return files;
	}
	public void setFiles(String files) {
		this.files = files;
	}
	public String getLinenumber() {
		return linenumber;
	}
	public void setLinenumber(String linenumber) {
		this.linenumber = linenumber;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		if(method!=null && method.length() > 255){
			method = method.substring(0 , 255) ;
		}
		this.method = method;
	}
	public String getLogtype() {
		return logtype;
	}
	public void setLogtype(String logtype) {
		this.logtype = logtype;
	}
	public String getStartid() {
		return startid;
	}
	public void setStartid(String startid) {
		this.startid = startid;
	}
	public String getErrorinfo() {
		return errorinfo;
	}
	public void setErrorinfo(String errorinfo) {
		this.errorinfo = errorinfo;
	}
	public String getTriggerwarning() {
		return triggerwarning;
	}
	public void setTriggerwarning(String triggerwarning) {
		this.triggerwarning = triggerwarning;
	}
	public String getTriggertime() {
		return triggertime;
	}
	public void setTriggertime(String triggertime) {
		this.triggertime = triggertime;
	}
	public int getTriggertimes() {
		return triggertimes;
	}
	public void setTriggertimes(int triggertimes) {
		this.triggertimes = triggertimes;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getLogtime() {
		return logtime;
	}
	public void setLogtime(String logtime) {
		this.logtime = logtime;
	}
	public String getIpaddr() {
		return ipaddr;
	}
	public void setIpaddr(String ipaddr) {
		this.ipaddr = ipaddr;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
}
