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
import java.util.concurrent.atomic.AtomicInteger;

@Entity
@Table(name = "uk_act_callagent")
@org.hibernate.annotations.Proxy(lazy = false)
public class CallAgent implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2258870729818431384L;

	private String id;

	private String orgi; // 租户ID
	private String organ; // 创建部门
	private String creater; // 创建人

	private String name; // 筛选表单名称

	private Date createtime = new Date(); // 创建时间

	private Date updatetime = new Date();
	
	private String parentid ;	//增加分类目录管理的 目录ID	
	
	private String actid ;		//活动ID
	
	private String batid ;	//筛选表单使用导入批次模板
	private String filtertype ;	//筛选表单使用导入批次模板
	private String tableid ;	//筛选表单使用元数据
	
	private String distype ;	//f分配类型 ： 技能组 |坐席|机器人
	private String distarget ;	//分配对象， 技能组|坐席|智能IVR的ID
	private int disnum ;		//分配数量
	

	private String datastatus; // 数据状态（逻辑删除）
	private String status; // 状态 正常，已处理完，已过期

	private String description; // 备注
	
	/**
	 * 已分配名单数量
	 */
	@Transient
	private AtomicInteger disnames = new AtomicInteger();

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

	public String getOrgi() {
		return orgi;
	}

	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}

	public String getOrgan() {
		return organ;
	}

	public void setOrgan(String organ) {
		this.organ = organ;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	public String getBatid() {
		return batid;
	}

	public void setBatid(String batid) {
		this.batid = batid;
	}

	public String getFiltertype() {
		return filtertype;
	}

	public void setFiltertype(String filtertype) {
		this.filtertype = filtertype;
	}

	public String getTableid() {
		return tableid;
	}

	public void setTableid(String tableid) {
		this.tableid = tableid;
	}

	public String getDatastatus() {
		return datastatus;
	}

	public void setDatastatus(String datastatus) {
		this.datastatus = datastatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDistype() {
		return distype;
	}

	public void setDistype(String distype) {
		this.distype = distype;
	}

	public String getDistarget() {
		return distarget;
	}

	public void setDistarget(String distarget) {
		this.distarget = distarget;
	}

	public int getDisnum() {
		return disnum;
	}

	public void setDisnum(int disnum) {
		this.disnum = disnum;
	}

	public String getActid() {
		return actid;
	}

	public void setActid(String actid) {
		this.actid = actid;
	}
	@Transient
	public AtomicInteger getDisnames() {
		return disnames;
	}

	public void setDisnames(AtomicInteger disnames) {
		this.disnames = disnames;
	}
}
