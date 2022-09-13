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
@Table(name = "uk_instruction")
@org.hibernate.annotations.Proxy(lazy = false)
public class Instruction  implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1793100417083554155L;
	private String id ;
	private String orgi ;
	private String name ;
	private String keyword;
	private String code ;
	private String plugin;
	private String type ;		//系统指令， 业务指令
	private String scope ;		//指令作用域 ， 提示条件： 1：人工坐席状态下提示 ， 0：自动状态下提示 ， 2：所有状态下都提示
	private String parent ;
	private String memo ;
	private Date createtime = new Date() ;
	
	private String snsid ;
	
	private String userid ;
	private String username ;
	private String matcherule ;		//匹配规则， 1：完全匹配，0：模糊匹配
	private boolean tipdefault ;	//默认提示
	private String status ;
	private boolean userbind ;		//是否需要绑定用户身份
	private String interfacetype ;	//接口处理类型
	private String interfaceurl ;	//接口访问地址
	private String interfaceparam ;	//接口参数描述
	private String adapter 	;		//接口适配器
	private String messagetype ;	//适用于消息类型 ： exchange ： image ： pos
	private String eventype ;
	
	
	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	public String getId() {
		return id;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
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
	public String getPlugin() {
		return plugin;
	}
	public void setPlugin(String plugin) {
		this.plugin = plugin;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getMatcherule() {
		return matcherule;
	}
	public void setMatcherule(String matcherule) {
		this.matcherule = matcherule;
	}
	public boolean isTipdefault() {
		return tipdefault;
	}
	public void setTipdefault(boolean tipdefault) {
		this.tipdefault = tipdefault;
	}
	
	public boolean isUserbind() {
		return userbind;
	}
	public void setUserbind(boolean userbind) {
		this.userbind = userbind;
	}
	public String getInterfacetype() {
		return interfacetype;
	}
	public void setInterfacetype(String interfacetype) {
		this.interfacetype = interfacetype;
	}
	public String getInterfaceurl() {
		return interfaceurl;
	}
	public void setInterfaceurl(String interfaceurl) {
		this.interfaceurl = interfaceurl;
	}
	public String getInterfaceparam() {
		return interfaceparam;
	}
	public void setInterfaceparam(String interfaceparam) {
		this.interfaceparam = interfaceparam;
	}
	public String getAdapter() {
		return adapter;
	}
	public void setAdapter(String adapter) {
		this.adapter = adapter;
	}
	public String getMessagetype() {
		return messagetype;
	}
	public void setMessagetype(String messagetype) {
		this.messagetype = messagetype;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getEventype() {
		return eventype;
	}
	public void setEventype(String eventype) {
		this.eventype = eventype;
	}
	public String getSnsid() {
		return snsid;
	}
	public void setSnsid(String snsid) {
		this.snsid = snsid;
	}
	@Transient
	public String getTopic(){
		StringBuffer strb = new StringBuffer() ;
		strb.append(this.snsid) ;
		
		if(this.matcherule.equals("keyword")){
			strb.append(".").append("text").append(".").append(this.getKeyword()) ;
		}else if(this.matcherule.equals("message")){
			strb.append(".").append(this.getMessagetype()).append(".").append(this.getMessagetype()) ;
		}else if(this.matcherule.equals("exchange")){
			strb.append(".").append("exchange").append(".").append(this.getEventype()) ;
			if(!this.eventype.equals("subscribe")){
				strb.append(".").append(this.getKeyword()) ;
			}
		}
		
		return strb.toString() ;
	}
}
