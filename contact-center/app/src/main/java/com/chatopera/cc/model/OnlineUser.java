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
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "uk_onlineuser")
@Proxy(lazy = false)
public class OnlineUser implements java.io.Serializable  {
    /**
     *
     */
    private static final long serialVersionUID = -5919027181023620097L;
    private String creater;
    private String datastatus;
    private String id;
    private String impid;
    private String appid;
    private String ipcode;
    private String orgi;
    private String channel;
    private String owner;
    private String processid;
    private String shares;
    private Date updatetime = new Date();
    private String updateuser;
    private String username;
    private String wfstatus;
    private String resolution;
    private String opersystem;

    private String browser;
    private String status;
    private String userid;
    private Date logintime;
    private String sessionid;
    private Date createtime = new Date();
    private String usertype;
    private String optype;
    private String mobile;
    private String olduser;
    private String ip;
    private String hostname;
    private String country;
    private String region;
    private String city;
    private String isp;
    private String province;
    private int betweentime;
    private String datestr;
    private String keyword;
    private String source;
    private String title;
    private String url;
    private String useragent;
    private String phone;
    private String contactsid;


    private int invitetimes; // 邀请次数
    private String invitestatus; // 邀请状态
    private int refusetimes;

    private Contacts contacts;

    public String getCreater() {
        return this.creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getDatastatus() {
        return this.datastatus;
    }

    public void setDatastatus(String datastatus) {
        this.datastatus = datastatus;
    }

    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "assigned")
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImpid() {
        return this.impid;
    }

    public void setImpid(String impid) {
        this.impid = impid;
    }

    public String getIpcode() {
        return this.ipcode;
    }

    public void setIpcode(String ipcode) {
        this.ipcode = ipcode;
    }

    public String getOrgi() {
        return this.orgi;
    }

    public void setOrgi(String orgi) {
        this.orgi = orgi;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getProcessid() {
        return this.processid;
    }

    public void setProcessid(String processid) {
        this.processid = processid;
    }

    public String getShares() {
        return this.shares;
    }

    public void setShares(String shares) {
        this.shares = shares;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWfstatus() {
        return this.wfstatus;
    }

    public void setWfstatus(String wfstatus) {
        this.wfstatus = wfstatus;
    }

    public String getResolution() {
        return this.resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostname() {
        return this.hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getBrowser() {
        return this.browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLogintime() {
        return this.logintime;
    }

    public void setLogintime(Date logintime) {
        this.logintime = logintime;
    }

    public String getSessionid() {
        return this.sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public Date getCreatetime() {
        return this.createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getUsertype() {
        return this.usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getOptype() {
        return this.optype;
    }

    public void setOptype(String optype) {
        this.optype = optype;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOlduser() {
        return this.olduser;
    }

    public void setOlduser(String olduser) {
        this.olduser = olduser;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return this.region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIsp() {
        return this.isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDatestr() {
        return this.datestr;
    }

    public void setDatestr(String datestr) {
        this.datestr = datestr;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUseragent() {
        return this.useragent;
    }

    public void setUseragent(String useragent) {
        this.useragent = useragent;
    }

    public Date getUpdatetime() {
        return this.updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public String getUpdateuser() {
        return this.updateuser;
    }

    public void setUpdateuser(String updateuser) {
        this.updateuser = updateuser;
    }

    public String getOpersystem() {
        return this.opersystem;
    }

    public void setOpersystem(String opersystem) {
        this.opersystem = opersystem;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getBetweentime() {
        return this.betweentime;
    }

    public void setBetweentime(int betweentime) {
        this.betweentime = betweentime;
    }

    public int getInvitetimes() {
        return invitetimes;
    }

    public void setInvitetimes(int invitetimes) {
        this.invitetimes = invitetimes;
    }

    public String getInvitestatus() {
        return invitestatus;
    }

    public void setInvitestatus(String invitestatus) {
        this.invitestatus = invitestatus;
    }

    public int getRefusetimes() {
        return refusetimes;
    }

    public void setRefusetimes(int refusetimes) {
        this.refusetimes = refusetimes;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getContactsid() {
        return contactsid;
    }

    public void setContactsid(String contactsid) {
        this.contactsid = contactsid;
    }

    @Transient
    public Contacts getContacts() {
        return contacts;
    }

    public void setContacts(Contacts contacts) {
        this.contacts = contacts;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
