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


package com.chatopera.cc.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "uk_tabletask")
@org.hibernate.annotations.Proxy(lazy = false)
public class MetadataTable implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3728229777159531557L;
	private String id;
	private String name;
	private String dbid ;
	private String tabledirid;
	private String tablename;
	private String code;
	private String secure;
	private String tabletype = "1"; // 1:Table : 2:SQL
	private String datasql ;
	private int startindex = 0;
	private Date updatetime ;
	private long updatetimenumber ;
	
	private String tabtype ; //project
	private String pid ; 	//product
	private String secmenuid ; //Sec Menu
	private String reportid ;	//report
	private boolean timeline ;
	private String eventname ;
	
	private int tbversion ;	 //table schedule version
	
	// private SecureConfigure secureconfigure;
	private Date lastupdate;
	private String taskname;
	private String taskplan; //改变用处， 改为 left
	private String taskstatus ;	//改变用处 ， 改为 top
	private String tasktype; // R3 CRM修改用处，修改为   二级菜单下的主表
	private Date createtime;
	private String configure;	//改变用处，改为   链接对象 一
	private String secureconf;	//改变用处，改为   链接对象 二
	private String userid;
	private String groupid;		//如果为结算过后的表，储存模型名称
	private String previewtemplet ;	//修改用处，改为JAVABean对象名
	private String listblocktemplet ;//修改用处，改为存储 ES的 JPA
	private String orgi ;
	private String creater ;
	private String creatername ;
	private boolean userpage = false ;
	private boolean fromdb ;
	private boolean workflow ; 
	private List<TableProperties> tableproperty;
	
	/**
	 * @return the tableproperty
	 */
	@Where(clause="impfield=0")	//不载入 设置为 禁用 导入导出的字段
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	@JoinColumn(name = "dbtableid")
	@OrderBy("sortindex")
	public List<TableProperties> getTableproperty() {
		return tableproperty;
	}

	/**
	 * @param tableproperty
	 *            the tableproperty to set
	 */
	public void setTableproperty(List<TableProperties> tableproperty) {
		this.tableproperty = tableproperty;
	}

	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
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

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the secure
	 */
	public String getSecure() {
		return secure;
	}

	/**
	 * @param secure
	 *            the secure to set
	 */
	public void setSecure(String secure) {
		this.secure = secure;
	}

	/**
	 * @return the lastupdate
	 */
	public Date getLastupdate() {
		return lastupdate;
	}

	/**
	 * @param lastupdate
	 *            the lastupdate to set
	 */
	public void setLastupdate(Date lastupdate) {
		this.lastupdate = lastupdate;
	}

	/**
	 * @return the taskname
	 */
	public String getTaskname() {
		return taskname != null ? taskname : tablename;
	}

	/**
	 * @param taskname
	 *            the taskname to set
	 */
	public void setTaskname(String taskname) {
		this.taskname = taskname;
	}

	/**
	 * @return the taskplan
	 */
	public String getTaskplan() {
		return taskplan;
	}

	/**
	 * @param taskplan
	 *            the taskplan to set
	 */
	public void setTaskplan(String taskplan) {
		this.taskplan = taskplan;
	}

	/**
	 * @return the taskstatus
	 */
	public String getTaskstatus() {
		return taskstatus;
	}

	/**
	 * @param taskstatus
	 *            the taskstatus to set
	 */
	public void setTaskstatus(String taskstatus) {
		this.taskstatus = taskstatus;
	}

	/**
	 * @return the tasktype
	 */
	public String getTasktype() {
		return tasktype;
	}

	/**
	 * @param tasktype
	 *            the tasktype to set
	 */
	public void setTasktype(String tasktype) {
		this.tasktype = tasktype;
	}

	/**
	 * @return the createtime
	 */
	public Date getCreatetime() {
		return createtime;
	}

	/**
	 * @param createtime
	 *            the createtime to set
	 */
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	/**
	 * @return the userid
	 */
	public String getUserid() {
		return userid;
	}

	/**
	 * @param userid
	 *            the userid to set
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}

	/**
	 * @return the groupid
	 */
	public String getGroupid() {
		return groupid;
	}

	/**
	 * @param groupid
	 *            the groupid to set
	 */
	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	@Transient
	public String getType() {
		return "table";
	}

	/**
	 * @return the configure
	 */
	public String getConfigure() {
		return configure;
	}

	/**
	 * @param configure
	 *            the configure to set
	 */
	public void setConfigure(String configure) {
		this.configure = configure;
	}

	/**
	 * @return the secureconf
	 */
	public String getSecureconf() {
		return secureconf;
	}

	/**
	 * @param secureconf
	 *            the secureconf to set
	 */
	public void setSecureconf(String secureconf) {
		this.secureconf = secureconf;
	}

	/**
	 * @return the tablename
	 */
	public String getTablename() {
		return tablename;
	}

	/**
	 * @param tablename
	 *            the tablename to set
	 */
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}


	/**
	 * @return the tabletype
	 */
	public String getTabletype() {
		return tabletype != null ? tabletype : "1";
	}

	/**
	 * @param tabletype
	 *            the tabletype to set
	 */
	public void setTabletype(String tabletype) {
		this.tabletype = tabletype;
	}

	
	/**
	 * @return the startindex
	 */
	public int getStartindex() {
		return startindex;
	}

	/**
	 * @param startindex the startindex to set
	 */
	public void setStartindex(int startindex) {
		this.startindex = startindex;
	}

	/**
	 * @return the updatetime
	 */
	public Date getUpdatetime() {
		return updatetime;
	}

	/**
	 * @param updatetime the updatetime to set
	 */
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	/**
	 * @return the updatetimenumber
	 */
	public long getUpdatetimenumber() {
		return updatetimenumber;
	}

	/**
	 * @param updatetimenumber the updatetimenumber to set
	 */
	public void setUpdatetimenumber(long updatetimenumber) {
		this.updatetimenumber = updatetimenumber;
	}
	
	public String getDatasql() {
		return datasql;
	}


	public void setDatasql(String datasql) {
		this.datasql = datasql;
	}

	public String getPreviewtemplet() {
		return previewtemplet;
	}

	public void setPreviewtemplet(String previewtemplet) {
		this.previewtemplet = previewtemplet;
	}

	public String getListblocktemplet() {
		return listblocktemplet;
	}

	public void setListblocktemplet(String listblocktemplet) {
		this.listblocktemplet = listblocktemplet;
	}
	@Transient
	public boolean isUserpage() {
		return userpage;
	}
	public void setUserpage(boolean userpage) {
		this.userpage = userpage;
	}
	public String getDbid() {
		return dbid;
	}
	public void setDbid(String dbid) {
		this.dbid = dbid;
	}
	public String getTabledirid() {
		return tabledirid;
	}
	public void setTabledirid(String tabledirid) {
		this.tabledirid = tabledirid;
	}
	public String getCreater() {
		return creater;
	}
	public void setCreater(String creater) {
		this.creater = creater;
	}
	public String getCreatername() {
		return creatername;
	}
	public void setCreatername(String creatername) {
		this.creatername = creatername;
	}

	public boolean isWorkflow() {
		return workflow;
	}

	public void setWorkflow(boolean workflow) {
		this.workflow = workflow;
	}

	public boolean isFromdb() {
		return fromdb;
	}

	public void setFromdb(boolean fromdb) {
		this.fromdb = fromdb;
	}

	public String getTabtype() {
		return tabtype;
	}

	public void setTabtype(String tabtype) {
		this.tabtype = tabtype;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getSecmenuid() {
		return secmenuid;
	}

	public void setSecmenuid(String secmenuid) {
		this.secmenuid = secmenuid;
	}

	public String getReportid() {
		return reportid;
	}

	public void setReportid(String reportid) {
		this.reportid = reportid;
	}

	public boolean isTimeline() {
		return timeline;
	}

	public void setTimeline(boolean timeline) {
		this.timeline = timeline;
	}

	public String getEventname() {
		return eventname;
	}

	public void setEventname(String eventname) {
		this.eventname = eventname;
	}

	public int getTbversion() {
		return tbversion;
	}

	public void setTbversion(int tbversion) {
		this.tbversion = tbversion;
	}

}
