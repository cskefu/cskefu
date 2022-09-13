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

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 处理操作日志
 * @author Administrator
 * @date 2013-10-31
 */
@Entity
@Table(name = "uk_log_request")
@org.hibernate.annotations.Proxy(lazy = false)
public class RequestLog implements java.io.Serializable  {
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String funtype;
	private String fundesc ;
	private String username;//操作人
	private String userid ;	//操作人员ID
	private String usermail ; //操作人员的EMAIL
	private Date starttime;//登陆时间或者操作时间
	private Date endtime;//退出
	private String url;//操作url
	private String parameters;//传入参数
	private String reportname;//报表名称
	private String type;// 1操作日志  2系统日志  3登录日志  4报表日志
	private String detailtype;// 日志小分类
	private String orgi;//组织
	private String ip;//ip地址
	private String hostname ; //hostname
	private String statues;//操作状态
	private String error;//错误提示
	private String reportdic;//报表目录
	private String flowid ;	//请求标识ID
	private Date createdate = new Date();	//创建时间

	private String classname;//类名
	private String methodname;//方法名
	private String filename;//字段名
	private String linenumber;//行号
	private String throwable;//线程
	private long   querytime ;
	private String optext ;	//报表时长 
	private String field6;
	private String field7;
	private String field8;
	private String triggerwarning ; //触发预警服务
	private String   triggertime ;// 触发预警服务时间
	
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
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getStarttime() {
		return starttime;
	}
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
	public Date getEndtime() {
		return endtime;
	}
	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}
	public String getDetailtype() {
		return detailtype;
	}
	public void setDetailtype(String detailtype) {
		this.detailtype = detailtype;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getReportdic() {
		return reportdic;
	}
	public void setReportdic(String reportdic) {
		this.reportdic = reportdic;
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	public String getReportname() {
		return reportname;
	}
	public void setReportname(String reportname) {
		this.reportname = reportname;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getStatues() {
		return statues;
	}
	public void setStatues(String statues) {
		this.statues = statues;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public String getMethodname() {
		return methodname;
	}
	public void setMethodname(String methodname) {
		this.methodname = methodname;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getLinenumber() {
		return linenumber;
	}
	public void setLinenumber(String linenumber) {
		this.linenumber = linenumber;
	}
	public String getThrowable() {
		return throwable;
	}
	public void setThrowable(String throwable) {
		this.throwable = throwable;
	}
	public String getField6() {
		return field6;
	}
	public void setField6(String field6) {
		this.field6 = field6;
	}
	public String getField7() {
		return field7;
	}
	public void setField7(String field7) {
		this.field7 = field7;
	}
	public String getField8() {
		return field8;
	}
	public void setField8(String field8) {
		this.field8 = field8;
	}
	public long getQuerytime() {
		return querytime;
	}
	public void setQuerytime(long querytime) {
		this.querytime = querytime;
	}
	public String getOptext() {
		return optext;
	}
	public void setOptext(String optext) {
		this.optext = optext;
	}
	public String getFlowid() {
		return flowid;
	}
	public void setFlowid(String flowid) {
		this.flowid = flowid;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFuntype() {
		return funtype;
	}
	public void setFuntype(String funtype) {
		this.funtype = funtype;
	}
	public String getFundesc() {
		return fundesc;
	}
	public void setFundesc(String fundesc) {
		this.fundesc = fundesc;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getTriggerwarning() {
		return triggerwarning;
	}
	public void setTriggerwarning(String triggerwarning) {
		this.triggerwarning = triggerwarning;
	}
	public String getTriggertime() {
		return triggertime;
	}
	public void setTriggertime(String triggertime) {
		this.triggertime = triggertime;
	}
	public Date getCreatedate() {
		return createdate;
	}
	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}
	public String getUsermail() {
		return usermail;
	}
	public void setUsermail(String usermail) {
		this.usermail = usermail;
	}
}
