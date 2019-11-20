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
package com.chatopera.cc.util.es;

import com.chatopera.cc.model.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UKDataBean implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8610410476273340864L;
	
	public String id ;
	private String creater ;	//创建人
	private String username;	//创建人用户名
	private String orgi ;		//租户ID
	private Date createtime ;	//创建时间
	private Date updatetime ;	//修改时间
	private MetadataTable table ;
	
	private String type ;
	
	private User user ;
	private User assuser;
	private Organ organ;
	
	private UKefuCallOutTask task ;
	private JobDetail activity ;
	private JobDetail batch ;
	
	private Map<String , Object> values = new HashMap<String , Object>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getOrgi() {
		return orgi;
	}

	public void setOrgi(String orgi) {
		this.orgi = orgi;
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

	public MetadataTable getTable() {
		return table;
	}

	public void setTable(MetadataTable table) {
		this.table = table;
	}

	public Map<String, Object> getValues() {
		return values;
	}

	public void setValues(Map<String, Object> values) {
		this.values = values;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Organ getOrgan() {
		return organ;
	}

	public void setOrgan(Organ organ) {
		this.organ = organ;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public UKefuCallOutTask getTask() {
		return task;
	}

	public void setTask(UKefuCallOutTask task) {
		this.task = task;
	}

	public JobDetail getActivity() {
		return activity;
	}

	public void setActivity(JobDetail activity) {
		this.activity = activity;
	}

	public JobDetail getBatch() {
		return batch;
	}

	public void setBatch(JobDetail batch) {
		this.batch = batch;
	}

	public User getAssuser() {
		return assuser;
	}

	public void setAssuser(User assuser) {
		this.assuser = assuser;
	}
}
