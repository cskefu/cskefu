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
package com.chatopera.cc.util.dsdata;

import com.chatopera.cc.model.JobDetail;
import com.chatopera.cc.model.MetadataTable;
import com.chatopera.cc.model.Reporter;
import com.chatopera.cc.model.User;
import com.chatopera.cc.util.dsdata.process.JPAProcess;

import java.io.File;

public class DSData {
	/**
	 * 生成的 TableTask
	 */
	private MetadataTable task ;
	/**
	 * 上传的文件 
	 */
	private File file ;
	
	private String contentType ;
	
	private Class<?> clazz ;
	
	private JPAProcess process ;
	
	private User user ;
	
	private Reporter report = new Reporter();
	
	private JobDetail jobDetail ;
	
	public DSData(){}
	
	public DSData(MetadataTable task  , File file , String contentType , User user){
		this.task = task ; 
		this.file = file ;
		this.contentType = contentType ;
		this.user = user ;
	}
	
	public MetadataTable getTask() {
		return task;
	}

	public void setTask(MetadataTable task) {
		this.task = task;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Reporter getReport() {
		return report;
	}

	public void setReport(Reporter report) {
		this.report = report;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public JPAProcess getProcess() {
		return process;
	}

	public void setProcess(JPAProcess process) {
		this.process = process;
	}

	public JobDetail getJobDetail() {
		return jobDetail;
	}

	public void setJobDetail(JobDetail jobDetail) {
		this.jobDetail = jobDetail;
	}
}
