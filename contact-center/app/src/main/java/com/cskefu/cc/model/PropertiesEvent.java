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
@Table(name = "uk_propertiesevent")
@org.hibernate.annotations.Proxy(lazy = false)
public class PropertiesEvent implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7632315428995964771L;
	
	private String id ;
	private String name ;
	private String tpid ;
	private String propertity;
	private String field ;
	private String newvalue ;
	private String oldvalue;
	
	private String textvalue ;
	
	private String orgi ;
	private String modifyid;	//变更 ID， UUID，随机生成
	
	private String creater ;
	private Date createtime ;
	
	private String dataid ;

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

	public String getTpid() {
		return tpid;
	}

	public void setTpid(String tpid) {
		this.tpid = tpid;
	}

	public String getPropertity() {
		return propertity;
	}

	public void setPropertity(String propertity) {
		this.propertity = propertity;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getNewvalue() {
		return newvalue;
	}

	public void setNewvalue(String newvalue) {
		this.newvalue = newvalue;
	}

	public String getOldvalue() {
		return oldvalue;
	}

	public void setOldvalue(String oldvalue) {
		this.oldvalue = oldvalue;
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

	public String getDataid() {
		return dataid;
	}

	public void setDataid(String dataid) {
		this.dataid = dataid;
	}

	public String getModifyid() {
		return modifyid;
	}

	public void setModifyid(String modifyid) {
		this.modifyid = modifyid;
	}

	public String getOrgi() {
		return orgi;
	}

	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}

	public String getTextvalue() {
		return textvalue;
	}

	public void setTextvalue(String textvalue) {
		this.textvalue = textvalue;
	}
	
}
