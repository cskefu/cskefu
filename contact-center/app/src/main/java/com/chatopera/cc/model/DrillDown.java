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
import java.util.Map;

@Entity
@Table(name = "uk_drilldown")
@org.hibernate.annotations.Proxy(lazy = false)
public class DrillDown implements java.io.Serializable  {
	private static final long serialVersionUID = 1L;
	private String id ;
	private boolean enable = true;
	private String modelid;
	private String drillpos ;	//钻取位置，指标或者维度
	private String dataid ;
	private String dataname ; 	//如果存放的是指标，则此处为指标名称 
	private String paramtype ;//是否带参数钻取,paramvalue为带参数，否则不带参数
	private String drilltype ;	//钻取类型 ， URL , DETAIL , REPORT,chart
	private String tdstyle ;	
	private String paramtarget ;
	private String paramreport ;
	private String paramurl ;
	private String paramname ;
	private String targetmime ;
	private String orgi ;
	private String reportid ;
	private String reportdicid;
	private String name ;
	private String code ;
	private String memo ;
	private String chartid ;
	private String paramvalue;//用户自定义参数；
	private String gotomaxvalue;//钻取最大阀值；如果大于这个设置不允许跳转；
	
	private Map<String, String> paramvalues;//钻取到别的报表要带的参数
	
	
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
	public String getModelid() {
		return modelid;
	}
	public void setModelid(String modelid) {
		this.modelid = modelid;
	}
	public String getDataid() {
		return dataid;
	}
	public void setDataid(String dataid) {
		this.dataid = dataid;
	}
	public String getParamtype() {
		return paramtype;
	}
	public void setParamtype(String paramtype) {
		this.paramtype = paramtype;
	}
	public String getParamtarget() {
		return paramtarget;
	}
	public void setParamtarget(String paramtarget) {
		this.paramtarget = paramtarget;
	}
	public String getParamreport() {
		return paramreport;
	}
	public void setParamreport(String paramreport) {
		this.paramreport = paramreport;
	}
	public String getParamurl() {
		return paramurl;
	}
	public void setParamurl(String paramurl) {
		this.paramurl = paramurl;
	}
	public String getParamname() {
		return paramname;
	}
	public void setParamname(String paramname) {
		this.paramname = paramname;
	}
	
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	public String getReportid() {
		return reportid;
	}
	public void setReportid(String reportid) {
		this.reportid = reportid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getDataname() {
		return dataname;
	}
	public void setDataname(String dataname) {
		this.dataname = dataname;
	}
	public String getTdstyle() {
		return tdstyle;
	}
	public void setTdstyle(String tdstyle) {
		this.tdstyle = tdstyle;
	}
	public String getTargetmime() {
		return targetmime;
	}
	public void setTargetmime(String targetmime) {
		this.targetmime = targetmime;
	}
	public String getDrilltype() {
		return drilltype;
	}
	public void setDrilltype(String drilltype) {
		this.drilltype = drilltype;
	}
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public String getDrillpos() {
		return drillpos;
	}
	public void setDrillpos(String drillpos) {
		this.drillpos = drillpos;
	}
	@Transient
	public Map<String, String> getParamvalues() {
		return paramvalues;
	}
	public void setParamvalues(Map<String, String> paramvalues) {
		this.paramvalues = paramvalues;
	}
	public String getParamvalue() {
		return paramvalue;
	}
	public void setParamvalue(String paramvalue) {
		this.paramvalue = paramvalue;
	}
	public String getReportdicid() {
		return reportdicid;
	}
	public void setReportdicid(String reportdicid) {
		this.reportdicid = reportdicid;
	}
	
	@Transient
	public String getGotomaxvalue() {
		return gotomaxvalue;
	}
	public void setGotomaxvalue(String gotomaxvalue) {
		this.gotomaxvalue = gotomaxvalue;
	}
	/**
	 * @return the chartid
	 */
	public String getChartid() {
		return chartid;
	}
	/**
	 * @param chartid the chartid to set
	 */
	public void setChartid(String chartid) {
		this.chartid = chartid;
	}
	
}
