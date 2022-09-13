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
@Table(name = "uk_sysdic")
@org.hibernate.annotations.Proxy(lazy = false)
public class SysDic  implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8699781935163431952L;
	private String id ;
	private String name ;
	private String title = "pub";			//改变用处， 变更为 CubeDic 的 目录类型， 个人文件夹  / 公共文件夹
	private String code ;
	private String orgi ;
	private String ctype ;
	private String parentid;
	private String iconstr ;
	private String iconskin ;
	private String description;
	private String catetype ;				
	private String memo ;					
	private String creater;
	private boolean haschild ;				//修改用处，改为 是否允许租户扩展 字典项目
	private boolean discode ;				//是否显示code
	private Date createtime;
	private Date updatetime;
	private int sortindex ;
	private String dicid ;
	
	private String menutype ;				//菜单类型，顶部导航菜单， 左侧菜单
	
	private String rules ;					//角色要求 ， 管理员 / 普通用户 / 多媒体坐席  / 呼叫中心坐席
	
	private String module ;
	private String url ;
	private String mlevel ;					//菜单级别， 一级 菜单， 二级菜单
	
	private boolean defaultvalue = false;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	public String getCtype() {
		return ctype;
	}
	public void setCtype(String ctype) {
		this.ctype = ctype;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getIconstr() {
		return iconstr;
	}
	public void setIconstr(String iconstr) {
		this.iconstr = iconstr;
	}
	public String getIconskin() {
		return iconskin;
	}
	public void setIconskin(String iconskin) {
		this.iconskin = iconskin;
	}
	public String getCatetype() {
		return catetype;
	}
	public void setCatetype(String catetype) {
		this.catetype = catetype;
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
	public boolean isHaschild() {
		return haschild;
	}
	public void setHaschild(boolean haschild) {
		this.haschild = haschild;
	}
	public int getSortindex() {
		return sortindex;
	}
	public void setSortindex(int sortindex) {
		this.sortindex = sortindex;
	}
	public String getDicid() {
		return dicid;
	}
	public void setDicid(String dicid) {
		this.dicid = dicid;
	}
	public boolean isDefaultvalue() {
		return defaultvalue;
	}
	public void setDefaultvalue(boolean defaultvalue) {
		this.defaultvalue = defaultvalue;
	}
	
	public String toString(){
		return this.name ;
	}
	public boolean isDiscode() {
		return discode;
	}
	public void setDiscode(boolean discode) {
		this.discode = discode;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMlevel() {
		return mlevel;
	}
	public void setMlevel(String mlevel) {
		this.mlevel = mlevel;
	}
	public String getRules() {
		return rules;
	}
	public void setRules(String rules) {
		this.rules = rules;
	}
	public String getMenutype() {
		return menutype;
	}
	public void setMenutype(String menutype) {
		this.menutype = menutype;
	}
}

