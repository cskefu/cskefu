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
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(indexName = "cskefu", type = "report")
@Entity
@Table(name = "uk_report")
@org.hibernate.annotations.Proxy(lazy = false)
public class Report extends ESBean implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5781401948807231526L;
	private String id  = MainUtils.getUUID();
	private String name ;	
	private String reporttype ; //0 代表动态报表  1 代表自助查询报表
	private String viewtype;
	private String code ;		 //变更用处，修改为 报表code		
	private String orgi ;
	private int objectcount ;    //用来标记报表默认打开是否加载数据
	private String dicid ;	//目录ID
	private String description ;			
	private Date createtime = new Date();
	private String html ;			//改变用处，用于存储 是否允许里面访问移动端报表
	private String status;
	private String rolename ;		//变更用处，标记为 动态报表 默认为 null 或者 0 都是 自助查询，1表示自定义报表
	private String userid ;			//变更用处，标记为 仪表盘的 属主ID
	private String blacklist ;		//变更用处，用于区分是否是  仪表盘
	private String reportpackage ;	//报表路径
	private String useacl ;			//启用权限控制    ,  变更用处，  用于控制是否覆盖上级目录的权限
	private String reportmodel	;	//自助查询的是 保存 Model 的ID
	private Date updatetime;		//修改时间 
	
	
	private boolean datastatus ;
	private String creater;
	private int reportversion ;
	private String publishedtype ;
	private String tabtype ;
	private String username ;
	private String useremail ;
	private boolean cache;//1启用缓存，0不启用
	private String extparam;		//默认使用 player 打开
	private String targetreport;//reporttype=shortcuts 的时候的目标报表
	private String source ;			//报表来源，如果是在  事件设计器里创建的 报表，则此字段不为空，无法保存
	
	private List<ReportModel> reportModels = new ArrayList<ReportModel>();
	
	private List<ReportFilter> reportFilters = new ArrayList<ReportFilter>();
	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "assigned")	
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
	public String getReporttype() {
		return reporttype;
	}
	public void setReporttype(String reporttype) {
		this.reporttype = reporttype;
	}
	public String getViewtype() {
		return viewtype;
	}
	public void setViewtype(String viewtype) {
		this.viewtype = viewtype;
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
	public int getObjectcount() {
		return objectcount;
	}
	public void setObjectcount(int objectcount) {
		this.objectcount = objectcount;
	}
	public String getDicid() {
		return dicid;
	}
	public void setDicid(String dicid) {
		this.dicid = dicid;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRolename() {
		return rolename;
	}
	public void setRolename(String rolename) {
		this.rolename = rolename;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getBlacklist() {
		return blacklist;
	}
	public void setBlacklist(String blacklist) {
		this.blacklist = blacklist;
	}
	public String getReportpackage() {
		return reportpackage;
	}
	public void setReportpackage(String reportpackage) {
		this.reportpackage = reportpackage;
	}
	public String getUseacl() {
		return useacl;
	}
	public void setUseacl(String useacl) {
		this.useacl = useacl;
	}
	public String getReportmodel() {
		return reportmodel;
	}
	public void setReportmodel(String reportmodel) {
		this.reportmodel = reportmodel;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public boolean isDatastatus() {
		return datastatus;
	}
	public void setDatastatus(boolean datastatus) {
		this.datastatus = datastatus;
	}
	public String getCreater() {
		return creater;
	}
	public void setCreater(String creater) {
		this.creater = creater;
	}
	public int getReportversion() {
		return reportversion;
	}
	public void setReportversion(int reportversion) {
		this.reportversion = reportversion;
	}
	public String getPublishedtype() {
		return publishedtype;
	}
	public void setPublishedtype(String publishedtype) {
		this.publishedtype = publishedtype;
	}
	public String getTabtype() {
		return tabtype;
	}
	public void setTabtype(String tabtype) {
		this.tabtype = tabtype;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUseremail() {
		return useremail;
	}
	public void setUseremail(String useremail) {
		this.useremail = useremail;
	}
	public boolean isCache() {
		return cache;
	}
	public void setCache(boolean cache) {
		this.cache = cache;
	}
	public String getExtparam() {
		return extparam;
	}
	public void setExtparam(String extparam) {
		this.extparam = extparam;
	}
	public String getTargetreport() {
		return targetreport;
	}
	public void setTargetreport(String targetreport) {
		this.targetreport = targetreport;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	@Transient
	public List<ReportModel> getReportModels() {
		return reportModels;
	}
	public void setReportModels(List<ReportModel> reportModels) {
		this.reportModels = reportModels;
	}
	@Transient
	public List<ReportFilter> getReportFilters() {
		return reportFilters;
	}
	public void setReportFilters(List<ReportFilter> reportFilters) {
		this.reportFilters = reportFilters;
	}
	
}
