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

/**
 * @author jaddy0302 Rivulet TableProperties.java 2010-3-22
 * 
 */
@Entity
@Table(name = "uk_tableproperties")
@org.hibernate.annotations.Proxy(lazy = false)
public class TableProperties implements java.io.Serializable,Cloneable{
	private static final long serialVersionUID = 3601436061896426576L;


	public TableProperties(){}
	public TableProperties(String fieldname , String datatypename , int datatypecode , String tablename){
		this(fieldname, datatypename, datatypecode, tablename, null , null , false) ;
	}
	
	public TableProperties(String fieldname , String datatypename , int datatypecode , String tablename , String orgi , String tableid , boolean fieldstatus){
		this(fieldname, fieldname, datatypename, datatypecode, tablename, orgi, tableid, fieldstatus) ;
	}
	
	public TableProperties(String fieldname , String datatypename , int datatypecode , String tablename , String orgi , String tableid , boolean fieldstatus , boolean token , String tokentype){
		this(fieldname, fieldname, datatypename, datatypecode, tablename, orgi, tableid, fieldstatus) ;
		this.token = token ;
		this.tokentype = tokentype ;
	}
	
	public TableProperties(String title , String fieldname , String datatypename , int datatypecode , String tablename , String orgi , String tableid , boolean fieldstatus){
		if(fieldname!=null){
			fieldname = fieldname.toLowerCase() ;
		}
		if(tablename!=null){
			tablename = tablename.toLowerCase() ;
		}
		this.fieldname = fieldname ;
		this.name = title ;
		this.datatypecode = datatypecode ;
		this.datatypename = datatypename ;
		this.tablename = tablename;
		this.dbtableid = tableid ;
		this.fieldstatus = fieldstatus ;
		this.orgi = orgi ;
	}
	
	private String id ;
	private String tablename ;
	private String dbtableid ;
	private String name ;
	private String code ;
	private String fieldname ;
	private int datatypecode ;		//变更用处，修改为字段长度
	private String datatypename ;
	private String indexdatatype ;
	private String groupid ;
	private String userid ;
	private boolean pk = false;
	private boolean modits = false ;
	private String orgi ;
	private String viewtype;				//变更用处，修改为 在哪些地方显示  ， view | edit | add | detail
	private int sortindex = 100;
	private boolean token ;
	private String tokentype ;	//分词方式
	private boolean inx = true;
	private boolean title = false ;
	private boolean systemfield  = false ;	//变更用处，是否流程变量
	private int length = 255 ;
	private boolean fieldstatus ;			
	
	private boolean secfield ;			//隐藏字段
	private String secdistype;			//字段隐藏方式
	
	private boolean phonenumber ;	//是否电话号码
	private String phonetype ;		//号码类型   ， 第一联系号码|家庭电话|工作电话
	private String phonememo ;		//号码备注
	private String styletype ;		//样式类型  ： 文本框、文本域 ， 单选列表、复选框、单选按钮等等
	
	
	private boolean seldata ;
	private String seldatatype ;	//选择数据方式  ： 字典数据  ， 表数据  ， 如果是表数据，则需要选择 表ID
	private String seldatacode ;
	private String seldatakey ;
	private String seldatavalue ;
	private String reftbid ;
	private String reftbname ;
	private String reftpid ;
	private String reftpname;
	private String reftype ;
	private String reftptitlefield ;
	private boolean defaultsort ;		//是否默认排序字段
	private boolean descorder ;			//默认倒叙排列
	private String defaultvalue ;
	private String defaultvaluetitle ;	//改变用途，修改为 数据完整性校验
	private String defaultfieldvalue ;	//改变用处，是否需要转换日期
	private boolean multpartfile = true;
	private String uploadtype ;
	private String cascadetype;//级联删除 none不删除，deleteself删除主数据，deleteall删除关联数据
	private boolean impfield = false ;
	
	private boolean reffk = false ; 		//是否外键关联
	
	private boolean sysfield ; 				//系统字段
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
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
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
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the fieldname
	 */
	public String getFieldname() {
		return fieldname!=null ? fieldname.toLowerCase() : null;
	}
	/**
	 * @param fieldname the fieldname to set
	 */
	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}
	
	/**
	 * @return the datatypecode
	 */
	public int getDatatypecode() {
		return datatypecode;
	}
	/**
	 * @param datatypecode the datatypecode to set
	 */
	public void setDatatypecode(int datatypecode) {
		this.datatypecode = datatypecode;
	}
	/**
	 * @return the datatypename
	 */
	public String getDatatypename() {
		return datatypename;
	}
	/**
	 * @param datatypename the datatypename to set
	 */
	public void setDatatypename(String datatypename) {
		this.datatypename = datatypename;
	}
	
	/**
	 * @return the dbtableid
	 */
	public String getDbtableid() {
		return dbtableid;
	}
	/**
	 * @param dbtableid the dbtableid to set
	 */
	public void setDbtableid(String dbtableid) {
		this.dbtableid = dbtableid;
	}
	/**
	 * @return the indexdatatype
	 */
	public String getIndexdatatype() {
		return indexdatatype;
	}
	/**
	 * @param indexdatatype the indexdatatype to set
	 */
	public void setIndexdatatype(String indexdatatype) {
		this.indexdatatype = indexdatatype;
	}
	/**
	 * @return the groupid
	 */
	public String getGroupid() {
		return groupid;
	}
	/**
	 * @param groupid the groupid to set
	 */
	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}
	/**
	 * @return the userid
	 */
	public String getUserid() {
		return userid;
	}
	/**
	 * @param userid the userid to set
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public boolean isPk() {
		return pk;
	}
	public void setPk(boolean pk) {
		this.pk = pk;
	}
	public boolean isModits() {
		return modits;
	}
	public void setModits(boolean modits) {
		this.modits = modits;
	}
	public String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	public String getViewtype() {
		return viewtype;
	}
	public void setViewtype(String viewtype) {
		this.viewtype = viewtype;
	}
	public int getSortindex() {
		return sortindex;
	}
	public void setSortindex(int sortindex) {
		this.sortindex = sortindex;
	}
	public TableProperties clone(){
		try {
			return (TableProperties) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	public boolean isSystemfield() {
		return systemfield;
	}
	public void setSystemfield(boolean systemfield) {
		this.systemfield = systemfield;
	}
	public boolean isToken() {
		return token;
	}
	public void setToken(boolean token) {
		this.token = token;
	}
	public boolean isInx() {
		return inx;
	}
	public void setInx(boolean inx) {
		this.inx = inx;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public boolean isFieldstatus() {
		return fieldstatus;
	}
	public void setFieldstatus(boolean fieldstatus) {
		this.fieldstatus = fieldstatus;
	}
	public boolean isSeldata() {
		return seldata;
	}
	public void setSeldata(boolean seldata) {
		this.seldata = seldata;
	}
	public String getSeldatatype() {
		return seldatatype != null ? seldatatype : "";
	}
	public void setSeldatatype(String seldatatype) {
		this.seldatatype = seldatatype;
	}
	public String getSeldatacode() {
		return seldatacode;
	}
	public void setSeldatacode(String seldatacode) {
		this.seldatacode = seldatacode;
	}
	public String getSeldatakey() {
		return seldatakey;
	}
	public void setSeldatakey(String seldatakey) {
		this.seldatakey = seldatakey;
	}
	public String getSeldatavalue() {
		return seldatavalue;
	}
	public void setSeldatavalue(String seldatavalue) {
		this.seldatavalue = seldatavalue;
	}
	public String getReftbid() {
		return reftbid;
	}
	public void setReftbid(String reftbid) {
		this.reftbid = reftbid;
	}
	public String getReftpid() {
		return reftpid;
	}
	public void setReftpid(String reftpid) {
		this.reftpid = reftpid;
	}
	public String getReftype() {
		return reftype;
	}
	public void setReftype(String reftype) {
		this.reftype = reftype;
	}
	public String getReftbname() {
		return reftbname;
	}
	public void setReftbname(String reftbname) {
		this.reftbname = reftbname;
	}
	public String getReftpname() {
		return reftpname;
	}
	public void setReftpname(String reftpname) {
		this.reftpname = reftpname;
	}
	public String getReftptitlefield() {
		return reftptitlefield;
	}
	public void setReftptitlefield(String reftptitlefield) {
		this.reftptitlefield = reftptitlefield;
	}
	public boolean isReffk() {
		return reffk;
	}
	public void setReffk(boolean reffk) {
		this.reffk = reffk;
	}
	public boolean isDefaultsort() {
		return defaultsort;
	}
	public void setDefaultsort(boolean defaultsort) {
		this.defaultsort = defaultsort;
	}
	public String getDefaultvalue() {
		return defaultvalue;
	}
	public void setDefaultvalue(String defaultvalue) {
		this.defaultvalue = defaultvalue;
	}
	public String getDefaultvaluetitle() {
		return defaultvaluetitle;
	}
	public void setDefaultvaluetitle(String defaultvaluetitle) {
		this.defaultvaluetitle = defaultvaluetitle;
	}
	public String getDefaultfieldvalue() {
		return defaultfieldvalue;
	}
	public void setDefaultfieldvalue(String defaultfieldvalue) {
		this.defaultfieldvalue = defaultfieldvalue;
	}
	public boolean isMultpartfile() {
		return multpartfile;
	}
	public void setMultpartfile(boolean multpartfile) {
		this.multpartfile = multpartfile;
	}
	public String getUploadtype() {
		return uploadtype;
	}
	public void setUploadtype(String uploadtype) {
		this.uploadtype = uploadtype;
	}
	public String getCascadetype() {
		return cascadetype;
	}
	public void setCascadetype(String cascadetype) {
		this.cascadetype = cascadetype;
	}
	public boolean isTitle() {
		return title;
	}
	public void setTitle(boolean title) {
		this.title = title;
	}
	public boolean isDescorder() {
		return descorder;
	}
	public void setDescorder(boolean descorder) {
		this.descorder = descorder;
	}
	public String getTokentype() {
		return tokentype;
	}
	public void setTokentype(String tokentype) {
		this.tokentype = tokentype;
	}
	public boolean isImpfield() {
		return impfield;
	}
	public void setImpfield(boolean impfield) {
		this.impfield = impfield;
	}
	public boolean isPhonenumber() {
		return phonenumber;
	}
	public void setPhonenumber(boolean phonenumber) {
		this.phonenumber = phonenumber;
	}
	public String getPhonetype() {
		return phonetype;
	}
	public void setPhonetype(String phonetype) {
		this.phonetype = phonetype;
	}
	public String getPhonememo() {
		return phonememo;
	}
	public void setPhonememo(String phonememo) {
		this.phonememo = phonememo;
	}
	public boolean isSecfield() {
		return secfield;
	}
	public void setSecfield(boolean secfield) {
		this.secfield = secfield;
	}
	public String getSecdistype() {
		return secdistype;
	}
	public void setSecdistype(String secdistype) {
		this.secdistype = secdistype;
	}
	public String getStyletype() {
		return styletype;
	}
	public void setStyletype(String styletype) {
		this.styletype = styletype;
	}
	public boolean isSysfield() {
		return sysfield;
	}
	public void setSysfield(boolean sysfield) {
		this.sysfield = sysfield;
	}
	
}
