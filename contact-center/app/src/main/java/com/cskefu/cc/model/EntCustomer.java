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
import java.util.Date;

@Document(indexName = "cskefu", type = "entcustomer")
@Entity
@Table(name = "uk_entcustomer")
@org.hibernate.annotations.Proxy(lazy = false)
public class EntCustomer extends ESBean implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id  = MainUtils.getUUID();
	
	private String name;			
	private String etype;			
	private String ekind;	
	private String maturity ;
	private String elevel;			
	private String ecode;			
	private String nickname;		
	private String esource;			
	private String organ;		
	private String corporation;		
	private String leadername;
	private String leadermobile;
	private String leadermobile2;
	private String leaderphone;
	private String leaderemail;		
	private String website;			
	private String email;			
	private String emailalt;		
	private String phone;	
	private String phonealt;
	private String fax;	
	private String faxalt;	
	private String country;			
	private String province;		
	
	private String entcusid;	//客户ID
	
	private String city;			
	private String sarea;			
	private String address;			
	private String postcode;
	private String businessscope;			
	private String capital;	
	private String stockcode;
	private String bankaccount;
	private String registeredaddress;		
	private String esize;			
	private String industry;		
	private String validstatus;		
	private String weixin;			
	private String weibo;		
	
	private Date touchtime ;						
	private String dzip;			
	private String daddress;		
	private String darea;			
	private String dcity;			
	private String dprovince;		
	private boolean datastatus;		
	private String processid;		
	private String description;
	
	private String creater;			
	private String username;		
	private String updateuser;		
	private String updateusername;		
	
	private Date updatetime = new Date();						
	private String orgi;		
	
	private Date createtime = new Date();						
	private String assignedto;		
	private String wfstatus;
	private String shares;			
	private String owner;			
	private String datadept;		
	private String batid;
	
	private String pinyin ;		//拼音首字母
	
	
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
	public String getEtype() {
		return etype;
	}
	public void setEtype(String etype) {
		this.etype = etype;
	}
	public String getEkind() {
		return ekind;
	}
	public void setEkind(String ekind) {
		this.ekind = ekind;
	}
	public String getElevel() {
		return elevel;
	}
	public void setElevel(String elevel) {
		this.elevel = elevel;
	}
	public String getEcode() {
		return ecode;
	}
	public void setEcode(String ecode) {
		this.ecode = ecode;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getEsource() {
		return esource;
	}
	public void setEsource(String esource) {
		this.esource = esource;
	}
	public String getOrgan() {
		return organ;
	}
	public void setOrgan(String organ) {
		this.organ = organ;
	}
	public String getCorporation() {
		return corporation;
	}
	public void setCorporation(String corporation) {
		this.corporation = corporation;
	}
	public String getLeadername() {
		return leadername;
	}
	public void setLeadername(String leadername) {
		this.leadername = leadername;
	}
	public String getLeadermobile() {
		return leadermobile;
	}
	public void setLeadermobile(String leadermobile) {
		this.leadermobile = leadermobile;
	}
	public String getLeadermobile2() {
		return leadermobile2;
	}
	public void setLeadermobile2(String leadermobile2) {
		this.leadermobile2 = leadermobile2;
	}
	public String getLeaderphone() {
		return leaderphone;
	}
	public void setLeaderphone(String leaderphone) {
		this.leaderphone = leaderphone;
	}
	public String getLeaderemail() {
		return leaderemail;
	}
	public void setLeaderemail(String leaderemail) {
		this.leaderemail = leaderemail;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEmailalt() {
		return emailalt;
	}
	public void setEmailalt(String emailalt) {
		this.emailalt = emailalt;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPhonealt() {
		return phonealt;
	}
	public void setPhonealt(String phonealt) {
		this.phonealt = phonealt;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getFaxalt() {
		return faxalt;
	}
	public void setFaxalt(String faxalt) {
		this.faxalt = faxalt;
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
	public String getSarea() {
		return sarea;
	}
	public void setSarea(String sarea) {
		this.sarea = sarea;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public String getBusinessscope() {
		return businessscope;
	}
	public void setBusinessscope(String businessscope) {
		this.businessscope = businessscope;
	}
	public String getCapital() {
		return capital;
	}
	public void setCapital(String capital) {
		this.capital = capital;
	}
	public String getStockcode() {
		return stockcode;
	}
	public void setStockcode(String stockcode) {
		this.stockcode = stockcode;
	}
	public String getBankaccount() {
		return bankaccount;
	}
	public void setBankaccount(String bankaccount) {
		this.bankaccount = bankaccount;
	}
	public String getRegisteredaddress() {
		return registeredaddress;
	}
	public void setRegisteredaddress(String registeredaddress) {
		this.registeredaddress = registeredaddress;
	}
	public String getEsize() {
		return esize;
	}
	public void setEsize(String esize) {
		this.esize = esize;
	}
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public String getValidstatus() {
		return validstatus;
	}
	public void setValidstatus(String validstatus) {
		this.validstatus = validstatus;
	}
	public String getWeixin() {
		return weixin;
	}
	public void setWeixin(String weixin) {
		this.weixin = weixin;
	}
	public String getWeibo() {
		return weibo;
	}
	public void setWeibo(String weibo) {
		this.weibo = weibo;
	}
	public Date getTouchtime() {
		return touchtime;
	}
	public void setTouchtime(Date touchtime) {
		this.touchtime = touchtime;
	}
	public String getDzip() {
		return dzip;
	}
	public void setDzip(String dzip) {
		this.dzip = dzip;
	}
	public String getDaddress() {
		return daddress;
	}
	public void setDaddress(String daddress) {
		this.daddress = daddress;
	}
	public String getDarea() {
		return darea;
	}
	public void setDarea(String darea) {
		this.darea = darea;
	}
	public String getDcity() {
		return dcity;
	}
	public void setDcity(String dcity) {
		this.dcity = dcity;
	}
	public String getDprovince() {
		return dprovince;
	}
	public void setDprovince(String dprovince) {
		this.dprovince = dprovince;
	}
	public boolean isDatastatus() {
		return datastatus;
	}
	public void setDatastatus(boolean datastatus) {
		this.datastatus = datastatus;
	}
	public String getProcessid() {
		return processid;
	}
	public void setProcessid(String processid) {
		this.processid = processid;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCreater() {
		return creater;
	}
	public void setCreater(String creater) {
		this.creater = creater;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUpdateuser() {
		return updateuser;
	}
	public void setUpdateuser(String updateuser) {
		this.updateuser = updateuser;
	}
	public String getUpdateusername() {
		return updateusername;
	}
	public void setUpdateusername(String updateusername) {
		this.updateusername = updateusername;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getAssignedto() {
		return assignedto;
	}
	public void setAssignedto(String assignedto) {
		this.assignedto = assignedto;
	}
	public String getWfstatus() {
		return wfstatus;
	}
	public void setWfstatus(String wfstatus) {
		this.wfstatus = wfstatus;
	}
	public String getShares() {
		return shares;
	}
	public void setShares(String shares) {
		this.shares = shares;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getDatadept() {
		return datadept;
	}
	public void setDatadept(String datadept) {
		this.datadept = datadept;
	}
	public String getBatid() {
		return batid;
	}
	public void setBatid(String batid) {
		this.batid = batid;
	}
	public String getMaturity() {
		return maturity;
	}
	public void setMaturity(String maturity) {
		this.maturity = maturity;
	}
	public String getEntcusid() {
		return entcusid!=null ? entcusid : id ;
	}
	public void setEntcusid(String entcusid) {
		this.entcusid = entcusid;
	}
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	
}
