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

import javax.persistence.*;
import java.util.Date;

/**
 * @author ricy Tenant.java 2010-3-17
 * 
 */
@Entity
@Table(name = "uk_tenant")
@org.hibernate.annotations.Proxy(lazy = false)
public class Tenant implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id ;
	private String datasourceid;
	private String tenantname;
	private String tenantcode;
	private boolean systemtenant ;
	private boolean inited ;		//是否已经初始化
	private boolean inites ;
	private boolean initdb ;
	private String adminuser ;		
	private String remark;
	
	
	private Date lastmenutime;
	private Date lastbasetime;
	private String tenantlogo;
	private String tenantvalid;//0未认证，1邮箱认证，2手机认证
	private Date createtime  = new Date();//创建时间
	private String password ;
	private String genpasstype ;//密码生成方式
	private String sign;
	private String orgid ;	//企业ID
	
	
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
	public String getDatasourceid() {
		return datasourceid;
	}
	public void setDatasourceid(String datasourceid) {
		this.datasourceid = datasourceid;
	}
	public String getTenantname() {
		return tenantname;
	}
	public void setTenantname(String tenantname) {
		this.tenantname = tenantname;
	}
	public String getTenantcode() {
		return tenantcode;
	}
	public void setTenantcode(String tenantcode) {
		this.tenantcode = tenantcode;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Date getLastmenutime() {
		return lastmenutime;
	}
	public void setLastmenutime(Date lastmenutime) {
		this.lastmenutime = lastmenutime;
	}
	public Date getLastbasetime() {
		return lastbasetime;
	}
	public void setLastbasetime(Date lastbasetime) {
		this.lastbasetime = lastbasetime;
	}
	public String getTenantlogo() {
		return tenantlogo;
	}
	public void setTenantlogo(String tenantlogo) {
		this.tenantlogo = tenantlogo;
	}
	public String getTenantvalid() {
		return tenantvalid;
	}
	public void setTenantvalid(String tenantvalid) {
		this.tenantvalid = tenantvalid;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public boolean isSystemtenant() {
		return systemtenant;
	}
	public void setSystemtenant(boolean systemtenant) {
		this.systemtenant = systemtenant;
	}
	public boolean isInited() {
		return inited;
	}
	public void setInited(boolean inited) {
		this.inited = inited;
	}
	public String getAdminuser() {
		return adminuser;
	}
	public void setAdminuser(String adminuser) {
		this.adminuser = adminuser;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getGenpasstype() {
		return genpasstype;
	}
	public void setGenpasstype(String genpasstype) {
		this.genpasstype = genpasstype;
	}
	public boolean isInites() {
		return inites;
	}
	public void setInites(boolean inites) {
		this.inites = inites;
	}
	public boolean isInitdb() {
		return initdb;
	}
	public void setInitdb(boolean initdb) {
		this.initdb = initdb;
	}
	public String getOrgid() {
		return orgid;
	}
	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}
}
