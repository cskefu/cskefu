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
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.util.Date;

@Document(indexName = "cskefu", type = "contacts")
@Entity
@Table(name = "uk_contacts")
@org.hibernate.annotations.Proxy(lazy = false)
public class Contacts extends ESBean implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5781401948807231526L;
	private String id  = MainUtils.getUUID();
	private String gender;
	private String cusbirthday;
	private String ctype;
	private String ckind;
	private String clevel;
	private String ccode;
	private String nickname;
	private String sarea;
	private String csource;
	private String language;

	public String getWluid() {
		return wluid;
	}

	public void setWluid(String wluid) {
		this.wluid = wluid;
	}

	private String wluid;

	public String getWlusername() {
		return wlusername;
	}

	public void setWlusername(String wlusername) {
		this.wlusername = wlusername;
	}

	private String wlusername;

	public String getWlcid() {
		return wlcid;
	}

	public void setWlcid(String wlcid) {
		this.wlcid = wlcid;
	}

	private String wlcid;

	public String getWlcompany_name() {
		return wlcompany_name;
	}

	public void setWlcompany_name(String wlcompany_name) {
		this.wlcompany_name = wlcompany_name;
	}

	private String wlcompany_name;

	public String getWlsid() {
		return wlsid;
	}

	public void setWlsid(String wlsid) {
		this.wlsid = wlsid;
	}

	public String getWlsystem_name() {
		return wlsystem_name;
	}

	public void setWlsystem_name(String wlsystem_name) {
		this.wlsystem_name = wlsystem_name;
	}

	private String wlsid;

	private String wlsystem_name;


	private String organ ;
	
	private String marriage;
	
	private String entcusid;	//客户ID
	
	private String education;
	private String identifytype;
	private String identifynumber;
	private String website;
	private String email;
	private String emailalt;
	private String skypeid;
	private String mobileno;
	private String mobilealt;
	private String phone;
	private String extension;
	private String phonealt;
	private String extensionalt;
	private String familyphone;
	private String familyphonealt;
	private String fax;
	private String faxalt;
	private String country;
	private String province;
	private String city;
	private String address;
	private String postcode;
	private String enterpriseid;
	private String company;
	private String department;
	private String duty;
	private String deptpr;
	private String validstatus;
	private String weixin;
	private String weixinname;
	private String weixinid;
	private String weibo;
	private String weiboid;
	private String qqcode;
	
	private Date touchtime;
	private boolean datastatus;
	private String processid;
	private String creater;
	private String username;
	private String updateuser;
	private String memo;
	private String updateusername;
	
	private Date updatetime = new Date();
	private String orgi;
	private String compper;
	
	private Date createtime = new Date();
	private String name;
	private String assignedto;
	private String wfstatus;
	private String shares;
	private String owner;
	private String datadept;
	
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
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getCusbirthday() {
		return cusbirthday;
	}
	public void setCusbirthday(String cusbirthday) {
		this.cusbirthday = cusbirthday;
	}
	public String getCtype() {
		return ctype;
	}
	public void setCtype(String ctype) {
		this.ctype = ctype;
	}
	public String getCkind() {
		return ckind;
	}
	public void setCkind(String ckind) {
		this.ckind = ckind;
	}
	public String getClevel() {
		return clevel;
	}
	public void setClevel(String clevel) {
		this.clevel = clevel;
	}
	public String getCcode() {
		return ccode;
	}
	public void setCcode(String ccode) {
		this.ccode = ccode;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getSarea() {
		return sarea;
	}
	public void setSarea(String sarea) {
		this.sarea = sarea;
	}
	public String getCsource() {
		return csource;
	}
	public void setCsource(String csource) {
		this.csource = csource;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getMarriage() {
		return marriage;
	}
	public void setMarriage(String marriage) {
		this.marriage = marriage;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public String getIdentifytype() {
		return identifytype;
	}
	public void setIdentifytype(String identifytype) {
		this.identifytype = identifytype;
	}
	public String getIdentifynumber() {
		return identifynumber;
	}
	public void setIdentifynumber(String identifynumber) {
		this.identifynumber = identifynumber;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getSkypeid() {
		return skypeid;
	}
	public void setSkypeid(String skypeid) {
		this.skypeid = skypeid;
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
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public String getMobilealt() {
		return mobilealt;
	}
	public void setMobilealt(String mobilealt) {
		this.mobilealt = mobilealt;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public String getPhonealt() {
		return phonealt;
	}
	public void setPhonealt(String phonealt) {
		this.phonealt = phonealt;
	}
	public String getExtensionalt() {
		return extensionalt;
	}
	public void setExtensionalt(String extensionalt) {
		this.extensionalt = extensionalt;
	}
	public String getFamilyphone() {
		return familyphone;
	}
	public void setFamilyphone(String familyphone) {
		this.familyphone = familyphone;
	}
	public String getFamilyphonealt() {
		return familyphonealt;
	}
	public void setFamilyphonealt(String familyphonealt) {
		this.familyphonealt = familyphonealt;
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
	public String getEnterpriseid() {
		return enterpriseid;
	}
	public void setEnterpriseid(String enterpriseid) {
		this.enterpriseid = enterpriseid;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getDuty() {
		return duty;
	}
	public void setDuty(String duty) {
		this.duty = duty;
	}
	public String getDeptpr() {
		return deptpr;
	}
	public void setDeptpr(String deptpr) {
		this.deptpr = deptpr;
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
	public String getWeixinname() {
		return weixinname;
	}
	public void setWeixinname(String weixinname) {
		this.weixinname = weixinname;
	}
	public String getWeixinid() {
		return weixinid;
	}
	public void setWeixinid(String weixinid) {
		this.weixinid = weixinid;
	}
	public String getWeibo() {
		return weibo;
	}
	public void setWeibo(String weibo) {
		this.weibo = weibo;
	}
	public String getWeiboid() {
		return weiboid;
	}
	public void setWeiboid(String weiboid) {
		this.weiboid = weiboid;
	}
	public String getQqcode() {
		return qqcode;
	}
	public void setQqcode(String qqcode) {
		this.qqcode = qqcode;
	}
	public Date getTouchtime() {
		return touchtime;
	}
	public void setTouchtime(Date touchtime) {
		this.touchtime = touchtime;
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
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
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
	public String getCompper() {
		return compper;
	}
	public void setCompper(String compper) {
		this.compper = compper;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getEntcusid() {
		return entcusid;
	}
	public void setEntcusid(String entcusid) {
		this.entcusid = entcusid;
	}
	public String getOrgan() {
		return organ;
	}
	public void setOrgan(String organ) {
		this.organ = organ;
	}
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
}
