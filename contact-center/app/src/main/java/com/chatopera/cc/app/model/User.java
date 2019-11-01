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


import java.util.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;


/**
 * @author jaddy0302 Rivulet User.java 2010-3-17
 * 
 */
@Entity
@Table(name = "uk_user")
@org.hibernate.annotations.Proxy(lazy = false)
public class User implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
    @Id
	private String id;
	
	private String sessionid;
	
	private String username;
	private String password;
	private String email;
	private String uname;
	private String firstname;
	private String midname;
	private String lastname;
	private String language;
	private String jobtitle;
	private String department;
	private String gender;
	private String mobile;
	private String birthday;
	private String nickname;
	private String secureconf = "5";
	private String usertype; // 0 Admin User  : !0  Other User
	
	private boolean superuser = false;	//是否是超级管理员
	
	private String orgi;
	private String orgid;
	
	private String creater;
	private Date createtime = new Date();
	private Date passupdatetime = new Date();
	private Date updatetime = new Date();
	private String memo;
	private String organ;
	private boolean agent;	//是否开通坐席功能
	private boolean callcenter;	//是否启用呼叫中心 坐席功能
	private String sipaccount;
	private String skill;
	private String city;	//城市
	private String province;//省份
	private boolean login;		//是否登录
	private boolean online; 	//是否在线
	private String status;		//
	private boolean datastatus;//数据状态，是否已删除
	
	private int maxuser;		//排队队列最大数值 ， 开启坐席功能后启用
	private String ordertype;		//坐席的工作队列排序方式
	
	
	private Date lastlogintime = new Date();	//最后登录时间
	
	private AgentStatus agentStatus;
	
	private int fans;			//粉丝
	private int follows;		//关注
	private int integral;		//积分
	private HashSet<String> myorgans = new HashSet<>();
	private List<Role> roleList = new ArrayList<Role>();
	private Map<String ,Object> roleAuthMap  = new HashMap<String ,Object>();
	
	public User(){}
	public User(String id){
		this.id = id;
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

	@Transient
	public String getSessionid() {
		return sessionid;
	}


	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getUname() {
		return uname;
	}


	public void setUname(String uname) {
		this.uname = uname;
	}


	public String getFirstname() {
		return firstname;
	}


	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}


	public String getMidname() {
		return midname;
	}


	public void setMidname(String midname) {
		this.midname = midname;
	}


	public String getLastname() {
		return lastname;
	}


	public void setLastname(String lastname) {
		this.lastname = lastname;
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	public String getJobtitle() {
		return jobtitle;
	}


	public void setJobtitle(String jobtitle) {
		this.jobtitle = jobtitle;
	}


	public String getDepartment() {
		return department;
	}


	public void setDepartment(String department) {
		this.department = department;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}


	public String getBirthday() {
		return birthday;
	}


	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}


	public String getNickname() {
		return nickname;
	}


	public void setNickname(String nickname) {
		this.nickname = nickname;
	}


	public String getSecureconf() {
		return secureconf;
	}


	public void setSecureconf(String secureconf) {
		this.secureconf = secureconf;
	}


	public String getUsertype() {
		return usertype;
	}


	public void setUsertype(String usertype) {
		this.usertype = usertype;
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


	public Date getPassupdatetime() {
		return passupdatetime;
	}


	public void setPassupdatetime(Date passupdatetime) {
		this.passupdatetime = passupdatetime;
	}


	public Date getUpdatetime() {
		return updatetime;
	}


	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}


	public String getMemo() {
		return memo;
	}


	public void setMemo(String memo) {
		this.memo = memo;
	}


	public String getOrgan() {
		return organ;
	}


	public void setOrgan(String organ) {
		this.organ = organ;
	}


	public void setId(String id) {
		this.id = id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public boolean isAgent() {
		return agent;
	}

	public void setAgent(boolean agent) {
		this.agent = agent;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getSipaccount() {
		return sipaccount;
	}

	public void setSipaccount(String sipaccount) {
		this.sipaccount = sipaccount;
	}
	@Transient
	public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

	public int getFans() {
		return fans;
	}

	public void setFans(int fans) {
		this.fans = fans;
	}

	public int getFollows() {
		return follows;
	}

	public void setFollows(int follows) {
		this.follows = follows;
	}

	public int getIntegral() {
		return integral;
	}

	public void setIntegral(int integral) {
		this.integral = integral;
	}

	public Date getLastlogintime() {
		return lastlogintime;
	}

	public void setLastlogintime(Date lastlogintime) {
		this.lastlogintime = lastlogintime;
	}
	@Transient
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		this.online = online;
	}
	@Transient
	public List<Role> getRoleList() {
		return roleList;
	}
	public void setRoleList(List<Role> roleList) {
		this.roleList = roleList;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isDatastatus() {
		return datastatus;
	}
	public void setDatastatus(boolean datastatus) {
		this.datastatus = datastatus;
	}
	@Transient
	public AgentStatus getAgentStatus() {
		return agentStatus;
	}
	public void setAgentStatus(AgentStatus agentStatus) {
		this.agentStatus = agentStatus;
	}
	public boolean isCallcenter() {
		return callcenter;
	}
	public void setCallcenter(boolean callcenter) {
		this.callcenter = callcenter;
	}
	public boolean isSuperuser() {
		return superuser;
	}
	@Transient
	public Map<String, Object> getRoleAuthMap() {
		return roleAuthMap;
	}
	public void setRoleAuthMap(Map<String, Object> roleAuthMap) {
		this.roleAuthMap = roleAuthMap;
	}
	public void setSuperuser(boolean superuser) {
		this.superuser = superuser;
	}
	public String getOrgid() {
		return orgid;
	}
	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}
	public int getMaxuser() {
		return maxuser;
	}
	public void setMaxuser(int maxuser) {
		this.maxuser = maxuser;
	}
	public String getOrdertype() {
		return ordertype;
	}
	public void setOrdertype(String ordertype) {
		this.ordertype = ordertype;
	}

	public boolean inMyorgans(final String organ){
		return myorgans.contains(organ);
	}

    @Transient
    public HashSet<String> getMyorgans() {
        return myorgans;
    }

    public void setMyorgans(HashSet<String> myorgans) {
        this.myorgans = myorgans;
    }
}
