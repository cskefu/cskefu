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
@Table(name = "uk_onlineuser_his")
@org.hibernate.annotations.Proxy(lazy = false)
public class OnlineUserHis implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8302250763919426452L;
	private String creater;
	private String datastatus;
	private String id;
	private String impid;
	private String appid ;
	private String ipcode;
	private String orgi;
	private String channel;
	private String owner;
	private String processid;
	private String shares;
	private Date updatetime = new Date();
	private String updateuser;
	private String username;
	private String wfstatus;
	private String resolution;
	private String opersystem;
	
	private String dataid ;		//记录当前 OnlineUser对象的ID，用于 在 根据服务记录找到当前在线用户的时候 的关联字段

	private String browser;
	private String status;
	private String userid;
	private Date logintime;
	private String sessionid;
	private Date createtime = new Date();
	private String usertype;
	private String optype;
	private String mobile;
	private String olduser;
	private String ip;
	private String hostname;
	private String country;
	private String region;
	private String city;
	private String isp;
	private String province;
	private int betweentime;
	private String datestr;
	private String keyword;
	private String source;
	private String title;
	private String url;
	private String useragent;
	
	private String contactsid ;

	private int invitetimes; // 邀请次数
	private String invitestatus; // 邀请状态
	private int refusetimes ;
	
	
	public String getCreater() {
		return creater;
	}
	public void setCreater(String creater) {
		this.creater = creater;
	}
	public String getDatastatus() {
		return datastatus;
	}
	public void setDatastatus(String datastatus) {
		this.datastatus = datastatus;
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
	public String getImpid() {
		return impid;
	}
	public void setImpid(String impid) {
		this.impid = impid;
	}
	public String getIpcode() {
		return ipcode;
	}
	public void setIpcode(String ipcode) {
		this.ipcode = ipcode;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getProcessid() {
		return processid;
	}
	public void setProcessid(String processid) {
		this.processid = processid;
	}
	public String getShares() {
		return shares;
	}
	public void setShares(String shares) {
		this.shares = shares;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getWfstatus() {
		return wfstatus;
	}
	public void setWfstatus(String wfstatus) {
		this.wfstatus = wfstatus;
	}
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getBrowser() {
		return browser;
	}
	public void setBrowser(String browser) {
		this.browser = browser;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public Date getLogintime() {
		return logintime;
	}
	public void setLogintime(Date logintime) {
		this.logintime = logintime;
	}
	public String getSessionid() {
		return sessionid;
	}
	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}
	public String getUsertype() {
		return usertype;
	}
	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}
	public String getOptype() {
		return optype;
	}
	public void setOptype(String optype) {
		this.optype = optype;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getOlduser() {
		return olduser;
	}
	public void setOlduser(String olduser) {
		this.olduser = olduser;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
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
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getDatestr() {
		return datestr;
	}
	public void setDatestr(String datestr) {
		this.datestr = datestr;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUseragent() {
		return useragent;
	}
	public void setUseragent(String useragent) {
		this.useragent = useragent;
	}
	public int getInvitetimes() {
		return invitetimes;
	}
	public void setInvitetimes(int invitetimes) {
		this.invitetimes = invitetimes;
	}
	public String getInvitestatus() {
		return invitestatus;
	}
	public void setInvitestatus(String invitestatus) {
		this.invitestatus = invitestatus;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public String getUpdateuser() {
		return updateuser;
	}
	public void setUpdateuser(String updateuser) {
		this.updateuser = updateuser;
	}
	public String getOpersystem() {
		return opersystem;
	}
	public void setOpersystem(String opersystem) {
		this.opersystem = opersystem;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public int getBetweentime() {
		return betweentime;
	}
	public void setBetweentime(int betweentime) {
		this.betweentime = betweentime;
	}
	public int getRefusetimes() {
		return refusetimes;
	}
	public void setRefusetimes(int refusetimes) {
		this.refusetimes = refusetimes;
	}
	public String getDataid() {
		return dataid;
	}
	public void setDataid(String dataid) {
		this.dataid = dataid;
	}
	public String getContactsid() {
		return contactsid;
	}
	public void setContactsid(String contactsid) {
		this.contactsid = contactsid;
	}
}
