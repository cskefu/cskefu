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
package com.chatopera.cc.basic.resource;

import com.chatopera.cc.model.JobDetail;
import com.chatopera.cc.util.es.UKDataBean;

import java.util.HashMap;
import java.util.Map;

public class OutputTextFormat {
	private String id ;
	private String title ;
	private String parent ;
	
	private Map<String , Object> data = new HashMap<String , Object>();
	private JobDetail job ;
	private UKDataBean dataBean ;
	
	public OutputTextFormat(JobDetail job){
		this.job = job ;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	public JobDetail getJob() {
		return job;
	}
	public void setJob(JobDetail job) {
		this.job = job;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public UKDataBean getDataBean() {
		return dataBean;
	}
	public void setDataBean(UKDataBean dataBean) {
		this.dataBean = dataBean;
	}
}
