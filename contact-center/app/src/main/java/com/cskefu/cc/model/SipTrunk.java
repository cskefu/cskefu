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
@Table(name = "uk_callcenter_siptrunk")
@org.hibernate.annotations.Proxy(lazy = false)
public class SipTrunk implements java.io.Serializable{
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
	
	private String sipserver ;	//SIP Server
	private int port ;			//SIP 端口， 5060 / 5080
	private String extention ;	//注册分机号
	private String outnumber;	//外呼 号码
	private String prefix ;		//前缀号码， 手机拨打的时候 需要加 0
	
	private String dtmf ;		//DTMF模式  ， rfc 2833 , inbound ,sip info
	
	private boolean register ;	//是否注册
	
	private boolean defaultsip ;//是否默认SIP网关
	private String title ;		//网关标题
	
	private String username ;
	private String authuser;
	private String password ;
	private String fromuser ; 	
	private boolean transprotocol;
	private String protocol;
	private int exptime = 1800;		//认证过期时间
	private int retry = 60;		//重试时间间隔
	private int heartbeat = 5; 	//心跳
	
	private String sipcontent ;		//SIP配置代码
	
	private String busyext ;	//坐席忙的时候转入号码
	private String notready ;	//坐席不在线的的时候转入号码
	private String noname ;		//无名单或未分配的时候转入号码
	private boolean enablecallagent ;	//坐席不在线的时候，转坐席手机号
	
	private String province ;		//号码省份
	private String city ;			//号码城市
	
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSipserver() {
		return sipserver;
	}
	public void setSipserver(String sipserver) {
		this.sipserver = sipserver;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getOutnumber() {
		return outnumber;
	}
	public void setOutnumber(String outnumber) {
		this.outnumber = outnumber;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public boolean isRegister() {
		return register;
	}
	public void setRegister(boolean register) {
		this.register = register;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getAuthuser() {
		return authuser;
	}
	public void setAuthuser(String authuser) {
		this.authuser = authuser;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFromuser() {
		return fromuser;
	}
	public void setFromuser(String fromuser) {
		this.fromuser = fromuser;
	}
	public boolean isTransprotocol() {
		return transprotocol;
	}
	public void setTransprotocol(boolean transprotocol) {
		this.transprotocol = transprotocol;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public int getExptime() {
		return exptime;
	}
	public void setExptime(int exptime) {
		this.exptime = exptime;
	}
	public String getSipcontent() {
		return sipcontent;
	}
	public void setSipcontent(String sipcontent) {
		this.sipcontent = sipcontent;
	}
	public int getRetry() {
		return retry;
	}
	public void setRetry(int retry) {
		this.retry = retry;
	}
	public int getHeartbeat() {
		return heartbeat;
	}
	public void setHeartbeat(int heartbeat) {
		this.heartbeat = heartbeat;
	}
	public String getDtmf() {
		return dtmf;
	}
	public void setDtmf(String dtmf) {
		this.dtmf = dtmf;
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
	public boolean isDefaultsip() {
		return defaultsip;
	}
	public void setDefaultsip(boolean defaultsip) {
		this.defaultsip = defaultsip;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBusyext() {
		return busyext;
	}
	public void setBusyext(String busyext) {
		this.busyext = busyext;
	}
	public String getNotready() {
		return notready;
	}
	public void setNotready(String notready) {
		this.notready = notready;
	}
	public String getNoname() {
		return noname;
	}
	public void setNoname(String noname) {
		this.noname = noname;
	}
	public boolean isEnablecallagent() {
		return enablecallagent;
	}
	public void setEnablecallagent(boolean enablecallagent) {
		this.enablecallagent = enablecallagent;
	}
}
