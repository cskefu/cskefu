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
 * 坐席监控表 -- 实体类
 */
@Entity
@Table(name = "uk_work_monitor")
@org.hibernate.annotations.Proxy(lazy = false)
public class WorkMonitor implements java.io.Serializable {

	/**
	 * 坐席监控表 -- 实体类
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String agent;//坐席工号
	private String username;
	private String agentno;//'分机号（坐席登录的分机号码）
	
	private String extno ;
	
	private boolean admin ;	//管理员账号
	
	private boolean firsttime ;		//是否首次在线
	private int firsttimes ;			//首次在线时长
	
	private String name;
	private String status;//实时坐席状态
	private String code;//对应字典管理CODE
	private String worktype ;	//状态模式 ，  文本坐席，语音坐席
	private String orgi;
	private String agentserviceid;
	private String skill;
	private String skillname;
	private boolean busy;
	private String workstatus;
	private Date createtime;//记录创建时间
	private String ani;
	private String called;
	private String direction;
	private Date callstarttime;
	private Date callendtime;
	private int duration;
	private String serviceid;
	private String datestr;
	private String servicestatus;
	private String userid;////登录人ID
	private String organ;//所属组织机构ID
	
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
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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
	public void setSkill(String skill) {
		this.skill = skill;
	}
	public String getSkillname() {
		return skillname;
	}
	public void setSkillname(String skillname) {
		this.skillname = skillname;
	}
	public boolean isBusy() {
		return busy;
	}
	public void setBusy(boolean busy) {
		this.busy = busy;
	}
	public String getWorkstatus() {
		return workstatus;
	}
	public void setWorkstatus(String workstatus) {
		this.workstatus = workstatus;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
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
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public String getServiceid() {
		return serviceid;
	}
	public void setServiceid(String serviceid) {
		this.serviceid = serviceid;
	}
	public String getDatestr() {
		return datestr;
	}
	public void setDatestr(String datestr) {
		this.datestr = datestr;
	}
	public String getWorktype() {
		return worktype;
	}
	public void setWorktype(String worktype) {
		this.worktype = worktype;
	}
	public String getServicestatus() {
		return servicestatus;
	}
	public void setServicestatus(String servicestatus) {
		this.servicestatus = servicestatus;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getOrgan() {
		return organ;
	}
	public void setOrgan(String organ) {
		this.organ = organ;
	}
	public String getExtno() {
		return extno;
	}
	public void setExtno(String extno) {
		this.extno = extno;
	}
	public boolean isAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	public boolean isFirsttime() {
		return firsttime;
	}
	public void setFirsttime(boolean firsttime) {
		this.firsttime = firsttime;
	}
	public int getFirsttimes() {
		return firsttimes;
	}
	public void setFirsttimes(int firsttimes) {
		this.firsttimes = firsttimes;
	}
}
