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
import com.chatopera.cc.basic.MainUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "uk_agentservice")
@Proxy(lazy = false)
public class AgentService implements Serializable {
	private static final long serialVersionUID = -5052623717164550681L;
	private String agentusername;
	private String agentno;
	private String status;
	private long times;
	private Date servicetime;
	private String orgi;
	private String id = MainUtils.getUUID();
	private String username;
	private String userid;
	private String channel;
	private Date logindate;
	
	private String sessionid ;
	
	private int queneindex = -1;
	
	private String source;
	private Date endtime;
	private String ipaddr;
	
	private String owner ;	//变更用处，修改为 智能IVR的 EventID
	private String osname;
	private String browser;
	private String nickname;
	protected String city;
	protected String province;
	protected String country;
	protected String headimgurl;
	private String region;
	private long sessiontimes = 0L;
	private int waittingtime;
	private int tokenum;
	private Date createtime = new Date();
	
	private String agent ;
	private String skill ;
	
	private String agentuserid ;
	
	private String createdate = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) ;
	private Date updatetime;
	private String appid;
	private String sessiontype;
	
	private String contactsid ;
	
	private boolean satisfaction ;
	private Date satistime ;
	private String satislevel ;
	private String satiscomment ;
	
	private int agentreplyinterval;
	private int agentreplytime;
	private int avgreplyinterval;
	private int avgreplytime;
	private int agentreplys;
	private int userasks;
	
	private boolean trans ;	//是否转接
	private String transmemo ;	//转接附言
	private Date transtime ;	//转件时间
	
	private String initiator ;	//对话发起方
	
	
	private String endby ;				//终止方 ， agent  ， user ， system
	private String aiid ;				//AI 的ID
	private boolean aiservice ;//是否是AI在提供服务
	private boolean foragent ;	//AI直接转人工
	
	private String solvestatus ;		//问题解决状态
	private boolean leavemsg ;	//是否留言
	private String leavemsgstatus = MainContext.LeaveMsgStatus.NOTPROCESS.toString();	//已处理、未处理
	
	
	private String qualitystatus ;//质检状态  ， 已分配/未分配
	private String qualitydisorgan ;	//分配的质检部门
	private String qualitydisuser;		//分配的质检人
	
	private String qualityorgan ;		//实际的质检部门
	private String qualityuser;			//实际的质检人
	private int qualityscore ;			//质检评分
	private Date qualitytime ;			//质检时间
	private Date qualitytype ;			//质检类型
	
	
	
	
	
	private String name ;
	private String email ;
	private String phone ;
	private String resion ;
	
	private String contextid;
	private String dataid; // 用户记录 OnlineUser对象的ID
	private String agentserviceid;
	private long ordertime = System.currentTimeMillis();
	private String snsuser;
	@Transient
	private Date lastmessage = new Date();
	private Date waittingtimestart = new Date();
	@Transient
	private Date lastgetmessage = new Date();
	@Transient
	private String lastmsg;
	@Transient
	private boolean tip = false;
	@Transient
	private String agentservice;
	@Transient
	private boolean agentTip = false;

	public String getAgentno() {
		return this.agentno;
	}

	public void setAgentno(String agentno) {
		this.agentno = agentno;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getTimes() {
		return this.times;
	}

	public void setTimes(long times) {
		this.times = times;
	}

	public String getOrgi() {
		return this.orgi;
	}

	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}

	public Date getServicetime() {
		return this.servicetime;
	}

	public void setServicetime(Date servicetime) {
		this.servicetime = servicetime;
	}

	public String getAgentusername() {
		return agentusername;
	}

	public void setAgentusername(String agentusername) {
		this.agentusername = agentusername;
	}

	@Transient
	public String getTopic() {
		return "/" + this.orgi + "/" + this.agentno;
	}

	@Transient
	public boolean isAgentTip() {
		return this.agentTip;
	}

	public void setAgentTip(boolean agentTip) {
		this.agentTip = agentTip;
	}

	@Transient
	private boolean fromhis = false;
	@Transient
	private boolean online = false;
	@Transient
	private boolean disconnect = false;
	private String agentskill;

	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "assigned")
	public String getId() {
		return this.id;
	}

	public Date getCreatetime() {
		return this.createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Date getUpdatetime() {
		return this.updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserid() {
		return this.userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getChannel() {
		return this.channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public Date getLogindate() {
		return this.logindate;
	}

	public void setLogindate(Date logindate) {
		this.logindate = logindate;
	}

	public String getContextid() {
		return this.contextid;
	}

	public void setContextid(String contextid) {
		this.contextid = contextid;
	}

	public String getAgentserviceid() {
		return this.agentserviceid;
	}

	public void setAgentserviceid(String agentserviceid) {
		this.agentserviceid = agentserviceid;
	}

	@Transient
	public Date getLastmessage() {
		return this.lastmessage;
	}

	public void setLastmessage(Date lastmessage) {
		this.lastmessage = lastmessage;
	}

	@Transient
	public boolean isTip() {
		return this.tip;
	}

	public void setTip(boolean tip) {
		this.tip = tip;
	}

	@Transient
	public boolean isDisconnect() {
		return this.disconnect;
	}

	public void setDisconnect(boolean disconnect) {
		this.disconnect = disconnect;
	}

	public Date getEndtime() {
		return this.endtime;
	}

	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}

	public long getSessiontimes() {
		return this.sessiontimes;
	}

	public void setSessiontimes(long sessiontimes) {
		this.sessiontimes = sessiontimes;
	}

	public String getSessiontype() {
		return this.sessiontype;
	}

	public void setSessiontype(String sessiontype) {
		this.sessiontype = sessiontype;
	}

	public String getAgentskill() {
		return this.agentskill;
	}

	public void setAgentskill(String agentskill) {
		this.agentskill = agentskill;
	}

	@Transient
	public boolean isOnline() {
		return this.online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	@Transient
	public boolean isFromhis() {
		return this.fromhis;
	}

	public void setFromhis(boolean fromhis) {
		this.fromhis = fromhis;
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Transient
	public Date getLastgetmessage() {
		return this.lastgetmessage;
	}

	public void setLastgetmessage(Date lastgetmessage) {
		this.lastgetmessage = lastgetmessage;
	}

	@Transient
	public String getLastmsg() {
		return this.lastmsg;
	}

	public void setLastmsg(String lastmsg) {
		this.lastmsg = lastmsg;
	}

	public String getSnsuser() {
		return this.snsuser;
	}

	public void setSnsuser(String snsuser) {
		this.snsuser = snsuser;
	}

	public int getWaittingtime() {
		return this.waittingtime;
	}

	public void setWaittingtime(int waittingtime) {
		this.waittingtime = waittingtime;
	}

	public int getTokenum() {
		return this.tokenum;
	}

	public void setTokenum(int tokenum) {
		this.tokenum = tokenum;
	}

	@Transient
	public Date getWaittingtimestart() {
		return this.waittingtimestart;
	}

	public void setWaittingtimestart(Date waittingtimestart) {
		this.waittingtimestart = waittingtimestart;
	}

	public String getAppid() {
		return this.appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return this.province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getHeadimgurl() {
		return this.headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public String getNickname() {
		return this.nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getIpaddr() {
		return this.ipaddr;
	}

	public void setIpaddr(String ipaddr) {
		this.ipaddr = ipaddr;
	}

	public String getOsname() {
		return this.osname;
	}

	public void setOsname(String osname) {
		this.osname = osname;
	}

	public String getBrowser() {
		return this.browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	@Transient
	public long getOrdertime() {
		return this.ordertime;
	}

	public void setOrdertime(long ordertime) {
		this.ordertime = ordertime;
	}

	public String getRegion() {
		return this.region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@Transient
	public String getAgentservice() {
		return this.agentservice;
	}

	public void setAgentservice(String agentservice) {
		this.agentservice = agentservice;
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

	public String getCreatedate() {
		return createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getResion() {
		return resion;
	}

	public void setResion(String resion) {
		this.resion = resion;
	}

	public boolean isSatisfaction() {
		return satisfaction;
	}

	public void setSatisfaction(boolean satisfaction) {
		this.satisfaction = satisfaction;
	}

	public String getSatislevel() {
		return satislevel;
	}

	public void setSatislevel(String satislevel) {
		this.satislevel = satislevel;
	}

	public String getSatiscomment() {
		return satiscomment;
	}

	public void setSatiscomment(String satiscomment) {
		this.satiscomment = satiscomment;
	}

	public Date getSatistime() {
		return satistime;
	}

	public void setSatistime(Date satistime) {
		this.satistime = satistime;
	}

	public boolean isTrans() {
		return trans;
	}

	public void setTrans(boolean trans) {
		this.trans = trans;
	}

	public String getTransmemo() {
		return transmemo;
	}

	public void setTransmemo(String transmemo) {
		this.transmemo = transmemo;
	}

	public Date getTranstime() {
		return transtime;
	}

	public void setTranstime(Date transtime) {
		this.transtime = transtime;
	}

	public int getAgentreplyinterval() {
		return agentreplyinterval;
	}

	public void setAgentreplyinterval(int agentreplyinterval) {
		this.agentreplyinterval = agentreplyinterval;
	}

	public int getAgentreplytime() {
		return agentreplytime;
	}

	public void setAgentreplytime(int agentreplytime) {
		this.agentreplytime = agentreplytime;
	}

	public int getAvgreplyinterval() {
		return avgreplyinterval;
	}

	public void setAvgreplyinterval(int avgreplyinterval) {
		this.avgreplyinterval = avgreplyinterval;
	}

	public int getAvgreplytime() {
		return avgreplytime;
	}

	public void setAvgreplytime(int avgreplytime) {
		this.avgreplytime = avgreplytime;
	}

	public int getAgentreplys() {
		return agentreplys;
	}

	public void setAgentreplys(int agentreplys) {
		this.agentreplys = agentreplys;
	}

	public int getUserasks() {
		return userasks;
	}

	public void setUserasks(int userasks) {
		this.userasks = userasks;
	}

	public String getAgentuserid() {
		return agentuserid;
	}

	public void setAgentuserid(String agentuserid) {
		this.agentuserid = agentuserid;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}
	@Transient
	public int getQueneindex() {
		return queneindex;
	}

	public void setQueneindex(int queneindex) {
		this.queneindex = queneindex;
	}

	public String getQualitystatus() {
		return qualitystatus;
	}

	public void setQualitystatus(String qualitystatus) {
		this.qualitystatus = qualitystatus;
	}

	public String getQualitydisorgan() {
		return qualitydisorgan;
	}

	public void setQualitydisorgan(String qualitydisorgan) {
		this.qualitydisorgan = qualitydisorgan;
	}

	public String getQualitydisuser() {
		return qualitydisuser;
	}

	public void setQualitydisuser(String qualitydisuser) {
		this.qualitydisuser = qualitydisuser;
	}

	public String getQualityorgan() {
		return qualityorgan;
	}

	public void setQualityorgan(String qualityorgan) {
		this.qualityorgan = qualityorgan;
	}

	public String getQualityuser() {
		return qualityuser;
	}

	public void setQualityuser(String qualityuser) {
		this.qualityuser = qualityuser;
	}

	public int getQualityscore() {
		return qualityscore;
	}

	public void setQualityscore(int qualityscore) {
		this.qualityscore = qualityscore;
	}

	public Date getQualitytime() {
		return qualitytime;
	}

	public void setQualitytime(Date qualitytime) {
		this.qualitytime = qualitytime;
	}

	public Date getQualitytype() {
		return qualitytype;
	}

	public void setQualitytype(Date qualitytype) {
		this.qualitytype = qualitytype;
	}

	public String getSolvestatus() {
		return solvestatus;
	}

	public void setSolvestatus(String solvestatus) {
		this.solvestatus = solvestatus;
	}

	public boolean isLeavemsg() {
		return leavemsg;
	}

	public void setLeavemsg(boolean leavemsg) {
		this.leavemsg = leavemsg;
	}

	public String getInitiator() {
		return initiator;
	}

	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}

	public String getLeavemsgstatus() {
		return leavemsgstatus;
	}

	public void setLeavemsgstatus(String leavemsgstatus) {
		this.leavemsgstatus = leavemsgstatus;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public String getEndby() {
		return endby;
	}

	public void setEndby(String endby) {
		this.endby = endby;
	}

	public String getAiid() {
		return aiid;
	}

	public void setAiid(String aiid) {
		this.aiid = aiid;
	}

	public boolean isAiservice() {
		return aiservice;
	}

	public void setAiservice(boolean aiservice) {
		this.aiservice = aiservice;
	}

	public boolean isForagent() {
		return foragent;
	}

	public void setForagent(boolean foragent) {
		this.foragent = foragent;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
}
