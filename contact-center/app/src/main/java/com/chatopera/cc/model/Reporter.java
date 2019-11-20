/**
 * Licensed to the Rivulet under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     webapps/LICENSE-Rivulet-1.0
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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jaddy0302 Rivulet Reporter.java 2010-3-7
 * 
 */
@Entity
@Table(name = "uk_historyreport")
@org.hibernate.annotations.Proxy(lazy = false)
public class Reporter implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1620523470991367318L;
	
	private String dataid ;
	private String title ;

	private String id;
	private String status ;
	private String amount ;
	private long pages ;
	private long total ;//数据总条数
	private int errors ;
	private long bytes ;
	private int threads ;
	private String type ;
	private int filter = 0 ;
	private Date createtime = new Date();
	private Date starttime = new Date();
	private Date endtime = new Date();
	private String errormsg ;
	private String detailmsg ;
	private String orgi ;
	private String tableid ;
	private String tabledirid;
	private String userid ;
	private String username ;
	private boolean error ;	
	private boolean out ;
	private long start = System.currentTimeMillis() ;
	private AtomicInteger atompages = new AtomicInteger() ;
	
	private String organ;
	
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
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the amount
	 */
	public String getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}
	/**
	 * @return the pages
	 */
	public long getPages() {
		return this.pages;
	}
	/**
	 * @param pages the pages to set
	 */
	public void setPages(long pages) {
		this.pages = pages;
	}
	/**
	 * @return the errors
	 */
	public int getErrors() {
		return errors;
	}
	/**
	 * @param errors the errors to set
	 */
	public void setErrors(int errors) {
		this.errors = errors;
	}
	/**
	 * @return the bytes
	 */
	public long getBytes() {
		return bytes;
	}
	/**
	 * @param bytes the bytes to set
	 */
	public void setBytes(long bytes) {
		this.bytes = bytes;
	}
	/**
	 * @return the threads
	 */
	public int getThreads() {
		return threads;
	}
	/**
	 * @param threads the threads to set
	 */
	public void setThreads(int threads) {
		this.threads = threads;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the speed
	 */
	@Transient
	public double getSpeed() {
		long times = (this.endtime.getTime() - start ) / 1000;
		return times!= 0 ? this.atompages.intValue() / times : this.atompages.intValue() ;
	}
	
	/**
	 * @return the bytespeed
	 */
	@Transient
	public long getBytespeed() {
		long times = (this.endtime.getTime() - start ) / 1000;
		return times!=0 ? this.bytes / times : this.bytes;
	}
	/**
	 * @return the createtime
	 */
	public Date getCreatetime() {
		return createtime;
	}
	/**
	 * @param createtime the createtime to set
	 */
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	/**
	 * @return the starttime
	 */
	public Date getStarttime() {
		return starttime;
	}
	/**
	 * @param starttime the starttime to set
	 */
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
	/**
	 * @return the endtime
	 */
	public Date getEndtime() {
		return endtime;
	}
	/**
	 * @param endtime the endtime to set
	 */
	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}
	/**
	 * @return the errormsg
	 */
	public String getErrormsg() {
		return errormsg;
	}
	/**
	 * @param errormsg the errormsg to set
	 */
	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}
	@Transient
	public int getFilter() {
		return filter;
	}
	public void setFilter(int filter) {
		this.filter = filter;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	@Transient
	public String getDetailmsg() {
		return detailmsg;
	}
	public void setDetailmsg(String detailmsg) {
		this.detailmsg = detailmsg;
	}
	@Transient
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	@Transient
	public boolean isOut() {
		return out;
	}
	public void setOut(boolean out) {
		this.out = out;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	@Transient
	public AtomicInteger getAtompages() {
		return atompages;
	}
	public void setAtompages(AtomicInteger atompages) {
		this.atompages = atompages;
	}
	@Transient
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public String getTableid() {
		return tableid;
	}
	public void setTableid(String tableid) {
		this.tableid = tableid;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getTabledirid() {
		return tabledirid;
	}
	public void setTabledirid(String tabledirid) {
		this.tabledirid = tabledirid;
	}
	public String getDataid() {
		return dataid;
	}
	public void setDataid(String dataid) {
		this.dataid = dataid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getOrgan() {
		return organ;
	}
	public void setOrgan(String organ) {
		this.organ = organ;
	}
	
}
