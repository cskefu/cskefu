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

/**
 * 坐席绩效表 -- 实体类
 */
@Entity
@Table(name = "uk_call_performance")
@org.hibernate.annotations.Proxy(lazy = false)
public class CallMonitorPerformance implements java.io.Serializable {


	private static final long serialVersionUID = 1L;
	
	private String id;
	private String agent;//坐席工号
	private String username;
	private String agentno;//'分机号（坐席登录的分机号码）
	private String name;
	private String startstatus;
	private String status;//坐席历史状态
	private String code;//对应字典管理CODE
	private String orgi;
	private String agentserviceid;
	private String skill;
	private String skillname;
	private String busy;
	private Date createtime;//状态开始时间（取值（坐席监控表的记录创建时间））'
	private Date endtime;//记录创建时间（取值（状态更换时的时间））
	private long intervaltime;//状态持续时间（endtime - endtime = intervaltime）
	private String ani;
	private String called;
	private String direction;
	private Date callstarttime;
	private Date callendtime;
	private int ringduration;
	private int duration;
	private int misscall;
	private int record;
	private int recordtime;
	private String startrecord;
	private String endrecord;
	private String recordfilename;
	private String recordfile;
	private String source;
	private Date answertime;
	private int current;
	private int init;
	private String action;
	private String host;
	private String ipaddr;
	private String servicesummary;
	private String serviceid;
	private String servicestatus;
	private String channelstatus;
	private String country;
	private String province;
	private String city;
	private String isp;
	private String contactsid;
	private String extention;
	private String hostid;
	private String calltype;
	private String calldir;
	private String otherdir;
	private String bridgeid;
	private String bridre;
	private String discaller;
	private String discalled;
	private String satisf;
	private String satisfaction;
	private String satisfdate;
	private String userid;//登录人ID
	private String organ;
	
	
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
	
	
	
	
	
	
	

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getOrgan() {
		return organ;
	}
	public void setOrgan(String organ) {
		this.organ = organ;
	}
	public String getStartstatus() {
		return startstatus;
	}
	public void setStartstatus(String startstatus) {
		this.startstatus = startstatus;
	}
	public long getIntervaltime() {
		return intervaltime;
	}
	public void setIntervaltime(long intervaltime) {
		this.intervaltime = intervaltime;
	}
	public String getAgent() {
		return agent;
	}
	public void setAgent(String agent) {
		this.agent = agent;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getAgentno() {
		return agentno;
	}
	public void setAgentno(String agentno) {
		this.agentno = agentno;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getAgentserviceid() {
		return agentserviceid;
	}
	public void setAgentserviceid(String agentserviceid) {
		this.agentserviceid = agentserviceid;
	}
	public String getSkill() {
		return skill;
	}
	public void 	setSkill(String skill) {
		this.skill = skill;
	}
	public String getSkillname() {
		return skillname;
	}
	public void setSkillname(String skillname) {
		this.skillname = skillname;
	}
	public String getBusy() {
		return busy;
	}
	public void setBusy(String busy) {
		this.busy = busy;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Date getEndtime() {
		return endtime;
	}
	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}
	public String getAni() {
		return ani;
	}
	public void setAni(String ani) {
		this.ani = ani;
	}
	public String getCalled() {
		return called;
	}
	public void setCalled(String called) {
		this.called = called;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public Date getCallstarttime() {
		return callstarttime;
	}
	public void setCallstarttime(Date callstarttime) {
		this.callstarttime = callstarttime;
	}
	public Date getCallendtime() {
		return callendtime;
	}
	public void setCallendtime(Date callendtime) {
		this.callendtime = callendtime;
	}
	public int getRingduration() {
		return ringduration;
	}
	public void setRingduration(int ringduration) {
		this.ringduration = ringduration;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public int getMisscall() {
		return misscall;
	}
	public void setMisscall(int misscall) {
		this.misscall = misscall;
	}
	public int getRecord() {
		return record;
	}
	public void setRecord(int record) {
		this.record = record;
	}
	public int getRecordtime() {
		return recordtime;
	}
	public void setRecordtime(int recordtime) {
		this.recordtime = recordtime;
	}
	public String getStartrecord() {
		return startrecord;
	}
	public void setStartrecord(String startrecord) {
		this.startrecord = startrecord;
	}
	public String getEndrecord() {
		return endrecord;
	}
	public void setEndrecord(String endrecord) {
		this.endrecord = endrecord;
	}
	public String getRecordfilename() {
		return recordfilename;
	}
	public void setRecordfilename(String recordfilename) {
		this.recordfilename = recordfilename;
	}
	public String getRecordfile() {
		return recordfile;
	}
	public void setRecordfile(String recordfile) {
		this.recordfile = recordfile;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public Date getAnswertime() {
		return answertime;
	}
	public void setAnswertime(Date answertime) {
		this.answertime = answertime;
	}
	public int getCurrent() {
		return current;
	}
	public void setCurrent(int current) {
		this.current = current;
	}
	public int getInit() {
		return init;
	}
	public void setInit(int init) {
		this.init = init;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getIpaddr() {
		return ipaddr;
	}
	public void setIpaddr(String ipaddr) {
		this.ipaddr = ipaddr;
	}
	public String getServicesummary() {
		return servicesummary;
	}
	public void setServicesummary(String servicesummary) {
		this.servicesummary = servicesummary;
	}
	public String getServiceid() {
		return serviceid;
	}
	public void setServiceid(String serviceid) {
		this.serviceid = serviceid;
	}
	public String getServicestatus() {
		return servicestatus;
	}
	public void setServicestatus(String servicestatus) {
		this.servicestatus = servicestatus;
	}
	public String getChannelstatus() {
		return channelstatus;
	}
	public void setChannelstatus(String channelstatus) {
		this.channelstatus = channelstatus;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getIsp() {
		return isp;
	}
	public void setIsp(String isp) {
		this.isp = isp;
	}
	public String getContactsid() {
		return contactsid;
	}
	public void setContactsid(String contactsid) {
		this.contactsid = contactsid;
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
	public String getCalltype() {
		return calltype;
	}
	public void setCalltype(String calltype) {
		this.calltype = calltype;
	}
	public String getCalldir() {
		return calldir;
	}
	public void setCalldir(String calldir) {
		this.calldir = calldir;
	}
	public String getOtherdir() {
		return otherdir;
	}
	public void setOtherdir(String otherdir) {
		this.otherdir = otherdir;
	}
	public String getBridgeid() {
		return bridgeid;
	}
	public void setBridgeid(String bridgeid) {
		this.bridgeid = bridgeid;
	}
	public String getBridre() {
		return bridre;
	}
	public void setBridre(String bridre) {
		this.bridre = bridre;
	}
	public String getDiscaller() {
		return discaller;
	}
	public void setDiscaller(String discaller) {
		this.discaller = discaller;
	}
	public String getDiscalled() {
		return discalled;
	}
	public void setDiscalled(String discalled) {
		this.discalled = discalled;
	}
	public String getSatisf() {
		return satisf;
	}
	public void setSatisf(String satisf) {
		this.satisf = satisf;
	}
	public String getSatisfaction() {
		return satisfaction;
	}
	public void setSatisfaction(String satisfaction) {
		this.satisfaction = satisfaction;
	}
	public String getSatisfdate() {
		return satisfdate;
	}
	public void setSatisfdate(String satisfdate) {
		this.satisfdate = satisfdate;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
	
}
