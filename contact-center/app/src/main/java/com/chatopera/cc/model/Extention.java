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

import com.chatopera.cc.basic.MainContext;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "uk_callcenter_extention")
@org.hibernate.annotations.Proxy(lazy = false)
public class Extention implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3932323765657445180L;
	private String id;
	private String extention;
	private String hostid ;	//Or IP
	private String agentno;
	private String password ;	//pbx host password
	
	private String extype ;		//分机类型 ： 直线分机 : IVR分机：组分机 ： 队列分机 ：会议分机
	private String subtype = MainContext.DTMFType.SATISF.toString() ;	//二级类型，如果是 IVR分机，则显示IVR类型：满意度调查，密码，身份证号 ， 银行卡号
	
	private boolean callout;	//是否允许外呼 OutBound
	private boolean playnum ;	//是否播报工号
	
	private boolean record ;	//是否录音
	
	private String description ;	//描述信息·
	private String mediapath ;		//播报工号录音
	
	private String siptrunk ;
	

	private String enableai ;
	private String aiid ;
	private String sceneid ;
	
	private String welcomemsg ;		//欢迎提示语
	private String waitmsg ;		//等待提示语
	private String tipmessage ;		//识别完成提示语
	
	private String errormessage ;		//异常完成提示语
	
	private boolean enablewebrtc ;	//启用WEBRTC
	
	private String bustype;//业务类型（电销/问卷）
	private String proid;//（产品ID）
	private String queid;//（问卷ID）
	private String aitype;//机器人类型（smartai/businessai）
	
	private String orgi;
	private String creater ;
	private Date createtime = new Date();
	private Date updatetime = new Date();
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getExtention() {
		return extention;
	}
	public void setExtention(String extention) {
		this.extention = extention;
	}
	public String getHostid() {
		return hostid;
	}
	public void setHostid(String hostid) {
		this.hostid = hostid;
	}
	public String getAgentno() {
		return agentno;
	}
	public void setAgentno(String agentno) {
		this.agentno = agentno;
	}
	public boolean isCallout() {
		return callout;
	}
	public void setCallout(boolean callout) {
		this.callout = callout;
	}
	public boolean isPlaynum() {
		return playnum;
	}
	public void setPlaynum(boolean playnum) {
		this.playnum = playnum;
	}
	@Column(name="srecord")
	public boolean isRecord() {
		return record;
	}
	public void setRecord(boolean record) {
		this.record = record;
	}
	public String getExtype() {
		return extype;
	}
	public void setExtype(String extype) {
		this.extype = extype;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSubtype() {
		return subtype;
	}
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}
	public String getMediapath() {
		return mediapath;
	}
	public void setMediapath(String mediapath) {
		this.mediapath = mediapath;
	}
	public String getSiptrunk() {
		return siptrunk;
	}
	public void setSiptrunk(String siptrunk) {
		this.siptrunk = siptrunk;
	}
	public String getEnableai() {
		return enableai;
	}
	public void setEnableai(String enableai) {
		this.enableai = enableai;
	}
	public String getAiid() {
		return aiid;
	}
	public void setAiid(String aiid) {
		this.aiid = aiid;
	}
	public String getSceneid() {
		return sceneid;
	}
	public void setSceneid(String sceneid) {
		this.sceneid = sceneid;
	}
	public String getWelcomemsg() {
		return welcomemsg;
	}
	public void setWelcomemsg(String welcomemsg) {
		this.welcomemsg = welcomemsg;
	}
	public String getWaitmsg() {
		return waitmsg;
	}
	public void setWaitmsg(String waitmsg) {
		this.waitmsg = waitmsg;
	}
	public String getTipmessage() {
		return tipmessage;
	}
	public void setTipmessage(String tipmessage) {
		this.tipmessage = tipmessage;
	}
	public String getErrormessage() {
		return errormessage;
	}
	public void setErrormessage(String errormessage) {
		this.errormessage = errormessage;
	}
	public String getBustype() {
		return bustype;
	}
	public void setBustype(String bustype) {
		this.bustype = bustype;
	}
	public String getProid() {
		return proid;
	}
	public void setProid(String proid) {
		this.proid = proid;
	}
	public String getQueid() {
		return queid;
	}
	public void setQueid(String queid) {
		this.queid = queid;
	}
	public String getAitype() {
		return aitype;
	}
	public void setAitype(String aitype) {
		this.aitype = aitype;
	}
	public boolean isEnablewebrtc() {
		return enablewebrtc;
	}
	public void setEnablewebrtc(boolean enablewebrtc) {
		this.enablewebrtc = enablewebrtc;
	}
}
