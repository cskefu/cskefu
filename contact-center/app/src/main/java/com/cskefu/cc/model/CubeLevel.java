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
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name = "uk_cubelevel")
@org.hibernate.annotations.Proxy(lazy = false)
public class CubeLevel implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private String id ;
	private String name ;	//维度名称	
	private String code ;
	private String formatstr ;
	private String columname;
	private boolean uniquemembers ;
	private String type ;	//类型：Numeric
	private String leveltype ;	// 类型 ， TimeMonths ： TimeWeeks ： TimeYears
	private String tablename ; 
	private String cubeid;
	private TableProperties tableproperty ;
	private String orgi ;
	private String dimid;
	private int sortindex ;
	private boolean permissions = false;
	private String parameters;
	private String attribue;
	private Date createtime = new Date();
	private String creater;
	//private String childid ;
	private String description;
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
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getColumname() {
		return columname;
	}
	public void setColumname(String columname) {
		this.columname = columname;
	}
	public boolean isUniquemembers() {
		return uniquemembers;
	}
	public void setUniquemembers(boolean uniquemembers) {
		this.uniquemembers = uniquemembers;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLeveltype() {
		return leveltype;
	}
	public void setLeveltype(String leveltype) {
		this.leveltype = leveltype;
	}
	public String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	public String getCubeid() {
		return cubeid;
	}
	public void setCubeid(String cubeid) {
		this.cubeid = cubeid;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	public int getSortindex() {
		return sortindex;
	}
	public void setSortindex(int sortindex) {
		this.sortindex = sortindex;
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	public String getAttribue() {
		return attribue;
	}
	public void setAttribue(String attribue) {
		this.attribue = attribue;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getDimid() {
		return dimid;
	}
	public void setDimid(String dimid) {
		this.dimid = dimid;
	}
	public boolean isPermissions() {
		return permissions;
	}
	public void setPermissions(boolean permissions) {
		this.permissions = permissions;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="tableproperty")
	@NotFound(action=NotFoundAction.IGNORE)
	public TableProperties getTableproperty() {
		return tableproperty;
	}
	public void setTableproperty(TableProperties tableproperty) {
		this.tableproperty = tableproperty;
	}
	public String getFormatstr() {
		return formatstr;
	}
	public void setFormatstr(String formatstr) {
		this.formatstr = formatstr;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCreater() {
		return creater;
	}
	public void setCreater(String creater) {
		this.creater = creater;
	}
	@Transient
	public String getNameAlias(){
		return this.columname ;
	}
}
