/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2018- Jun. 2023 Chatopera Inc, <https://www.chatopera.com>,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (C) 2017 优客服-多渠道客服系统,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.cskefu.cc.util.dsdata;

import com.cskefu.cc.model.JobDetail;
import com.cskefu.cc.model.MetadataTable;
import com.cskefu.cc.model.Reporter;
import com.cskefu.cc.model.User;
import com.cskefu.cc.util.dsdata.process.JPAProcess;

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
