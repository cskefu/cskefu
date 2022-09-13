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
 * @author ricy Site.java 2010-3-1
 */
@Entity
@Table(name = "uk_system_message")
@org.hibernate.annotations.Proxy(lazy = false)
public class SystemMessage implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 98876333686462570L;
    private String id;

    private String name;

    private String msgtype;    //配置类型 ： 邮件 | 短信

    private String smtpserver;
    private String smtpuser;
    private String smtppassword;
    private String mailfrom;
    private String seclev;   //ssl...
    private String sslport;   //sslport...
    private String orgi;

    private String organ;

    private String smstype;    //短信发送类型 ,SDK或者 URL提交方式
    private String url;        //短信网关的URL ， 通过URL方式提交短信
    private String appkey;        //短信网关的APPKEY
    private String appsec;        //短信网关的 APPSEC
    private String sign;        //短信网关的签名

    private Date createtime = new Date();    //创建时间

    private String tpcode;        //短信网关的模板代码

    private String moreparam;    //更多参数

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

    public String getSmtpserver() {
        return smtpserver;
    }

    public void setSmtpserver(String smtpserver) {
        this.smtpserver = smtpserver;
    }

    public String getSmtpuser() {
        return smtpuser;
    }

    public void setSmtpuser(String smtpuser) {
        this.smtpuser = smtpuser;
    }

    public String getSmtppassword() {
        return smtppassword;
    }

    public void setSmtppassword(String smtppassword) {
        this.smtppassword = smtppassword;
    }

    public String getMailfrom() {
        return mailfrom;
    }

    public void setMailfrom(String mailfrom) {
        this.mailfrom = mailfrom;
    }

    public String getSeclev() {
        return seclev;
    }

    public void setSeclev(String seclev) {
        this.seclev = seclev;
    }

    public String getOrgi() {
        return orgi;
    }

    public void setOrgi(String orgi) {
        this.orgi = orgi;
    }

    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }

    public String getSmstype() {
        return smstype;
    }

    public void setSmstype(String smstype) {
        this.smstype = smstype;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getAppsec() {
        return appsec;
    }

    public void setAppsec(String appsec) {
        this.appsec = appsec;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTpcode() {
        return tpcode;
    }

    public void setTpcode(String tpcode) {
        this.tpcode = tpcode;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
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

    public String getSslport() {
        return sslport;
    }

    public void setSslport(String sslport) {
        this.sslport = sslport;
    }

    public String getMoreparam() {
        return moreparam;
    }

    public void setMoreparam(String moreparam) {
        this.moreparam = moreparam;
    }
}
