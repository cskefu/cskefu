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

import com.cskefu.cc.basic.MainUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "uk_act_formfilter_item")
@org.hibernate.annotations.Proxy(lazy = false)
public class FormFilterItem  implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id  = MainUtils.getUUID();
	
	private String formfilterid;
	
	private String field ;	//字段
	private String cond;	//条件
	private String value;	//值
	
	
	private String contype;	//条件类型
	
	private String orgi ;
	private String creater;
	private Date createtime = new Date();
	private Date updatetime = new Date();
	
	private String itemtype ;	//类型， 
	
	private String comp ;		//逻辑条件
	
	
	/**
	 * @return the id
	 */
	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "assigned")	
	public String getId() {
		return id;
	}


	public String getField() {
		return field;
	}


	public void setField(String field) {
		this.field = field;
	}


	public String getCond() {
		return cond;
	}


	public void setCond(String cond) {
		this.cond = cond;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public String getContype() {
		return contype;
	}


	public void setContype(String contype) {
		this.contype = contype;
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


	public String getItemtype() {
		return itemtype;
	}


	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}

	public String getComp() {
		return comp;
	}


	public void setComp(String comp) {
		this.comp = comp;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getFormfilterid() {
		return formfilterid;
	}


	public void setFormfilterid(String formfilterid) {
		this.formfilterid = formfilterid;
	}
	
	
}