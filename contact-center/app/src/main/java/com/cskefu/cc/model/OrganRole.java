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
@Table(name = "uk_organrole")
@org.hibernate.annotations.Proxy(lazy = false)
public class OrganRole implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 707069993826500239L;
	private String id ;
	private Organ organ;
	private String dicid ;
	private String dicvalue ;
	private String creater ;
	private String orgi ;
	private Date createtime = new Date();
	
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
	@OneToOne(optional = true)
	public Organ getOrgan() {
		return organ;
	}
	public void setOrgan(Organ organ) {
		this.organ = organ;
	}
	public String getDicid() {
		return dicid;
	}
	public void setDicid(String dicid) {
		this.dicid = dicid;
	}
	public String getDicvalue() {
		return dicvalue;
	}
	public void setDicvalue(String dicvalue) {
		this.dicvalue = dicvalue;
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
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
}