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
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.*;
import java.util.Date;

@Document(indexName = "cskefu", type = "orderscomment")
@Entity
@Table(name = "uk_orderscomment")
@org.hibernate.annotations.Proxy(lazy = false)
public class OrdersComment implements UKAgg{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4911955236794918875L;
	private String id = MainUtils.getUUID();
	private String username;
	private String creater ;
	
	private Date createtime = new Date() ;
	
	@Field(index = FieldIndex.not_analyzed , type = FieldType.String)
	private String dataid ;
	
	private String content ; 	//评论内容
	
	private Date updatetime = new Date() ;
	
	private boolean optimal ;	//变更用处，流程回复
	
	private boolean prirep ;	//变更用处， 是否私密回复
	
	private int up ;			//点赞数量
	private int comments ;		//回复数量
	
	private boolean admin ;		//变更用处 ， 是否审批流程
	private boolean datastatus ;	//数据状态，是否已删除
	
	private String orgi ;
	
	private String cate ;		
	
	private String optype ;
	
	private String approval ;	//审批结果
	private String retback ;	//退回位置 ， 退回到 创建人
	
	private String accdept ;	//转办 部门
	private String accuser ;	//转办人
	
	
	private String ipcode ;
	private String country ;
	private String province ;
	private String city ;
	private String isp ;
	private String region ;
	
	private int rowcount ;
	private String key ;
	
	private User user ;
	
	@Id
	@Column(length = 32)
	@GeneratedValue(generator= "paymentableGenerator")
	@GenericGenerator(name= "paymentableGenerator",strategy = "assigned")	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
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
	public String getIpcode() {
		return ipcode;
	}
	public void setIpcode(String ipcode) {
		this.ipcode = ipcode;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getIsp() {
		return isp;
	}
	public void setIsp(String isp) {
		this.isp = isp;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getDataid() {
		return dataid;
	}
	public void setDataid(String dataid) {
		this.dataid = dataid;
	}
	public String getOptype() {
		return optype;
	}
	public void setOptype(String optype) {
		this.optype = optype;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public boolean isOptimal() {
		return optimal;
	}
	public void setOptimal(boolean optimal) {
		this.optimal = optimal;
	}
	public boolean isAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	public int getUp() {
		return up;
	}
	public void setUp(int up) {
		this.up = up;
	}
	public int getComments() {
		return comments;
	}
	public void setComments(int comments) {
		this.comments = comments;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	@Transient
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getCate() {
		return cate;
	}
	public void setCate(String cate) {
		this.cate = cate;
	}
	@Transient
	public int getRowcount() {
		return rowcount;
	}
	public void setRowcount(int rowcount) {
		this.rowcount = rowcount;
	}
	@Transient
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public boolean isPrirep() {
		return prirep;
	}
	public void setPrirep(boolean prirep) {
		this.prirep = prirep;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	public boolean isDatastatus() {
		return datastatus;
	}
	public void setDatastatus(boolean datastatus) {
		this.datastatus = datastatus;
	}
	public String getApproval() {
		return approval;
	}
	public void setApproval(String approval) {
		this.approval = approval;
	}
	public String getRetback() {
		return retback;
	}
	public void setRetback(String retback) {
		this.retback = retback;
	}
	public String getAccdept() {
		return accdept;
	}
	public void setAccdept(String accdept) {
		this.accdept = accdept;
	}
	public String getAccuser() {
		return accuser;
	}
	public void setAccuser(String accuser) {
		this.accuser = accuser;
	}
}
