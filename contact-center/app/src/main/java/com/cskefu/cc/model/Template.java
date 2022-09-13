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
 * @author iceworld
 *
 */
@Entity
@Table(name = "uk_templet")
@org.hibernate.annotations.Proxy(lazy = false)
public class Template implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1946579239823440392L;
	private String id ;
	private String name ;
	private String code ;		//修改用处，变更为 SysDic 的 code 		
	private String userid ;					
	private String groupid ;			
	private String description ;
	private String templettitle;//邮件头
	private String templettext ;
	private String templettype ; //List OR Preview
	private Date createtime = new Date();
	private String orgi ;
	private String iconstr;	//模板图标
	private String memo ; 	//模板说明内容
	private String typeid;//分组id
	private int layoutcols ;		//列数
	private String datatype ;		//样例数据
	private String charttype ;		//报表类型
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
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
	public String getGroupid() {
		return groupid;
	}
	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTemplettext() {
		return templettext;
	}
	public void setTemplettext(String templettext) {
		this.templettext = templettext;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getTemplettype() {
		return templettype;
	}
	public void setTemplettype(String templettype) {
		this.templettype = templettype;
	}
	@Transient
	public String getTitle(){
		return this.groupid ;
	}
	public String getIconstr() {
		return iconstr;
	}
	public void setIconstr(String iconstr) {
		this.iconstr = iconstr;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getTypeid() {
		return typeid;
	}
	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}
	public String getTemplettitle() {
		return templettitle;
	}
	public void setTemplettitle(String templettitle) {
		this.templettitle = templettitle;
	}
	public int getLayoutcols() {
		return layoutcols;
	}
	public void setLayoutcols(int layoutcols) {
		this.layoutcols = layoutcols;
	}
	public String getDatatype() {
		return datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	public String getCharttype() {
		return charttype;
	}
	public void setCharttype(String charttype) {
		this.charttype = charttype;
	}
	
}
