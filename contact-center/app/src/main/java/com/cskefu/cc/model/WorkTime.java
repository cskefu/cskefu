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

@Entity
@Table(name = "uk_worktime")
@org.hibernate.annotations.Proxy(lazy = false)
public class WorkTime implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3932323765657445180L;
	private String id;
	private String name;
	private String orgi;
	private String creater ;
	private String type;
	private Date createtime = new Date();
	private Date updatetime = new Date();
	
	private String day ;		//默认 allow
	private String begintime;		//工作时间开始  ：例如：201707010830
	private String endtime 	;		//工作时间结束	例如： 201707011530
	private int wfrom ;				//工作时间 星期开始 
	private int wto ;				//工作时间 星期结束
	
	private String wbegintime ;		//星期 时间开始
	private String wendtime ;		//星期 时间结束
	
	private int dfrom ;				//工作时间 日期开始
	private int dto ;				//工作时间 日期结束
	
	private String  dbegintime ;	//日期 时间开始
	private String dendtime 	;	//日期 时间结束
	
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
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
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getBegintime() {
		return begintime;
	}
	public void setBegintime(String begintime) {
		this.begintime = begintime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public int getWfrom() {
		return wfrom;
	}
	public void setWfrom(int wfrom) {
		this.wfrom = wfrom;
	}
	public int getWto() {
		return wto;
	}
	public void setWto(int wto) {
		this.wto = wto;
	}
	public String getWbegintime() {
		return wbegintime;
	}
	public void setWbegintime(String wbegintime) {
		this.wbegintime = wbegintime;
	}
	public String getWendtime() {
		return wendtime;
	}
	public void setWendtime(String wendtime) {
		this.wendtime = wendtime;
	}
	public int getDfrom() {
		return dfrom;
	}
	public void setDfrom(int dfrom) {
		this.dfrom = dfrom;
	}
	public int getDto() {
		return dto;
	}
	public void setDto(int dto) {
		this.dto = dto;
	}
	public String getDbegintime() {
		return dbegintime;
	}
	public void setDbegintime(String dbegintime) {
		this.dbegintime = dbegintime;
	}
	public String getDendtime() {
		return dendtime;
	}
	public void setDendtime(String dendtime) {
		this.dendtime = dendtime;
	}
}
