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

import com.chatopera.cc.basic.MainUtils;
import org.apache.commons.codec.binary.Base64;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.util.Date;
@Document(indexName = "cskefu", type = "publishedreport")
@Entity
@Table(name = "uk_publishedreport")
@org.hibernate.annotations.Proxy(lazy = false)
public class PublishedReport implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private String id ;
	private String name ;	
	private String code ;
	private String reporttype ;
	private String dicid ;	//目录ID
	private String orgi ;
	private String dataid ;
	private String dataflag ;
	private int startindex ;
	private Date startdate ;
	private int dataversion;
	private String creater ;
	private String reportcontent;
	private Date createtime;
	private Report report ;
	
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
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getReporttype() {
		return reporttype;
	}
	public void setReporttype(String reporttype) {
		this.reporttype = reporttype;
	}
	public String getDicid() {
		return dicid;
	}
	public void setDicid(String dicid) {
		this.dicid = dicid;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	public String getDataid() {
		return dataid;
	}
	public void setDataid(String dataid) {
		this.dataid = dataid;
	}
	public String getDataflag() {
		return dataflag;
	}
	public void setDataflag(String dataflag) {
		this.dataflag = dataflag;
	}
	public int getStartindex() {
		return startindex;
	}
	public void setStartindex(int startindex) {
		this.startindex = startindex;
	}
	public Date getStartdate() {
		return startdate;
	}
	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}
	public int getDataversion() {
		return dataversion;
	}
	public void setDataversion(int dataversion) {
		this.dataversion = dataversion;
	}
	public String getCreater() {
		return creater;
	}
	public void setCreater(String creater) {
		this.creater = creater;
	}
	public String getReportcontent() {
		return reportcontent;
	}
	public void setReportcontent(String reportcontent) {
		this.reportcontent = reportcontent;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	@Transient
	public Report getReport() {
		Base64 base64 = new Base64();
		try {
			return report!=null ? report : (report = (this.reportcontent==null?null:(Report) MainUtils.toObject(base64.decode(this.reportcontent))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return report;
	}
	
}
