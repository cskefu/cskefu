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


package com.cskefu.cc.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "uk_callcenter_ivr")
@org.hibernate.annotations.Proxy(lazy = false)
public class IvrMenu implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3932323765657445180L;
	private String id;
	private String name;
	private String orgi;
	private String creater ;
	private String type;
	private Date createtime = new Date();
	private Date updatetime = new Date();
	private String hostid ;
	
	private String extentionid ;
	
	private String greetlong;
	private String greetshort;
	private String invalidsound;
	private String exitsound;
	private String confirmmacro;
	private String confirmkey;
	private String ttsengine;
	private String ttsvoice;
	private String confirmattempts;
	private int timeout;
	private int interdigittimeout;
	private int maxfailures;
	private int maxtimeouts;
	private int digitlen;
	private String menucontent ;
	private String action;
	private String digits;
	private String param;
	private String parentid ;

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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
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
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getHostid() {
		return hostid;
	}
	public void setHostid(String hostid) {
		this.hostid = hostid;
	}
	public String getGreetlong() {
		return greetlong;
	}
	public void setGreetlong(String greetlong) {
		this.greetlong = greetlong;
	}
	public String getGreetshort() {
		return greetshort;
	}
	public void setGreetshort(String greetshort) {
		this.greetshort = greetshort;
	}
	public String getInvalidsound() {
		return invalidsound;
	}
	public void setInvalidsound(String invalidsound) {
		this.invalidsound = invalidsound;
	}
	public String getExitsound() {
		return exitsound;
	}
	public void setExitsound(String exitsound) {
		this.exitsound = exitsound;
	}
	public String getConfirmmacro() {
		return confirmmacro;
	}
	public void setConfirmmacro(String confirmmacro) {
		this.confirmmacro = confirmmacro;
	}
	public String getConfirmkey() {
		return confirmkey;
	}
	public void setConfirmkey(String confirmkey) {
		this.confirmkey = confirmkey;
	}
	public String getTtsengine() {
		return ttsengine;
	}
	public void setTtsengine(String ttsengine) {
		this.ttsengine = ttsengine;
	}
	public String getTtsvoice() {
		return ttsvoice;
	}
	public void setTtsvoice(String ttsvoice) {
		this.ttsvoice = ttsvoice;
	}
	public String getConfirmattempts() {
		return confirmattempts;
	}
	public void setConfirmattempts(String confirmattempts) {
		this.confirmattempts = confirmattempts;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public int getInterdigittimeout() {
		return interdigittimeout;
	}
	public void setInterdigittimeout(int interdigittimeout) {
		this.interdigittimeout = interdigittimeout;
	}
	public int getMaxfailures() {
		return maxfailures;
	}
	public void setMaxfailures(int maxfailures) {
		this.maxfailures = maxfailures;
	}
	public int getMaxtimeouts() {
		return maxtimeouts;
	}
	public void setMaxtimeouts(int maxtimeouts) {
		this.maxtimeouts = maxtimeouts;
	}
	public int getDigitlen() {
		return digitlen;
	}
	public void setDigitlen(int digitlen) {
		this.digitlen = digitlen;
	}
	public String getMenucontent() {
		return menucontent;
	}
	public void setMenucontent(String menucontent) {
		this.menucontent = menucontent;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getDigits() {
		return digits;
	}
	public void setDigits(String digits) {
		this.digits = digits;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	public String getExtentionid() {
		return extentionid;
	}
	public void setExtentionid(String extentionid) {
		this.extentionid = extentionid;
	}
}
