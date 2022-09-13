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
@Table(name = "uk_snsaccount")
@org.hibernate.annotations.Proxy(lazy = false)
public class SNSAccount {
    private String id;

    @Column(unique = true)
    private String snsid;        //表示 SNSAccount
    private String name;
    private String code;
    private String username;
    private String password;
    private String snstype;//改字段为为大类  微信/微博/易信等
    private Date createtime = new Date();    //创建时间
    private Date updatetime;        //更新时间
    private int expirestime;        //过期时间
    private String account;    //该字段修改为子类型   订阅号(sub)/服务号(sev)/企业号(enpt)

    private String verify;        // 是否认证
    private String headimg;
    private String qrcode;
    private String alias;
    private String openpay;
    private String openshake;
    private String oepnscan;
    private String opencard;
    private String openstore;
    private String refreshtoken;
    private String authaccesstoken;


    private String allowremote;
    private String email;
    private String userno;
    private String token;
    private String apipoint;
    private String appkey;
    private String secret;
    private String aeskey;


    private String baseURL;    //网站URL

    private String apptoken;
    private String sessionkey;
    private boolean defaultaccount;
    private String moreparam;                //改变用处，用于记录 爬虫的 爬取位置(微博)/如果是微信记录Secret
    private String orgi;
    private String organ;
    private String creater;
    private String lastatupdate;
    private String lastprimsgupdate;

    private String status = "0";
    private boolean agent = false;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSnstype() {
        return snstype;
    }

    public void setSnstype(String snstype) {
        this.snstype = snstype;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAllowremote() {
        return allowremote;
    }

    public void setAllowremote(String allowremote) {
        this.allowremote = allowremote;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserno() {
        return userno;
    }

    public void setUserno(String userno) {
        this.userno = userno;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getApptoken() {
        return apptoken;
    }

    public void setApptoken(String apptoken) {
        this.apptoken = apptoken;
    }

    public String getSessionkey() {
        return sessionkey;
    }

    public void setSessionkey(String sessionkey) {
        this.sessionkey = sessionkey;
    }

    public String getMoreparam() {
        return moreparam;
    }

    public void setMoreparam(String moreparam) {
        this.moreparam = moreparam;
    }

    public String getOrgi() {
        return orgi;
    }

    public void setOrgi(String orgi) {
        this.orgi = orgi;
    }

    public String getApipoint() {
        return apipoint;
    }

    public void setApipoint(String apipoint) {
        this.apipoint = apipoint;
    }

    public boolean isDefaultaccount() {
        return defaultaccount;
    }

    public void setDefaultaccount(boolean defaultaccount) {
        this.defaultaccount = defaultaccount;
    }

    public String getLastatupdate() {
        return lastatupdate;
    }

    public void setLastatupdate(String lastatupdate) {
        this.lastatupdate = lastatupdate;
    }

    public String getLastprimsgupdate() {
        return lastprimsgupdate;
    }

    public void setLastprimsgupdate(String lastprimsgupdate) {
        this.lastprimsgupdate = lastprimsgupdate;
    }

    public boolean isAgent() {
        return agent;
    }

    public void setAgent(boolean agent) {
        this.agent = agent;
    }

    @Transient
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVerify() {
        return verify;
    }

    public void setVerify(String verify) {
        this.verify = verify;
    }

    public String getHeadimg() {
        return headimg;
    }

    public void setHeadimg(String headimg) {
        this.headimg = headimg;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getOpenpay() {
        return openpay;
    }

    public void setOpenpay(String openpay) {
        this.openpay = openpay;
    }

    public String getOpenshake() {
        return openshake;
    }

    public void setOpenshake(String openshake) {
        this.openshake = openshake;
    }

    public String getOpencard() {
        return opencard;
    }

    public void setOpencard(String opencard) {
        this.opencard = opencard;
    }

    public String getOpenstore() {
        return openstore;
    }

    public void setOpenstore(String openstore) {
        this.openstore = openstore;
    }

    public String getOepnscan() {
        return oepnscan;
    }

    public void setOepnscan(String oepnscan) {
        this.oepnscan = oepnscan;
    }

    public String getRefreshtoken() {
        return refreshtoken;
    }

    public void setRefreshtoken(String refreshtoken) {
        this.refreshtoken = refreshtoken;
    }

    public String getAuthaccesstoken() {
        return authaccesstoken;
    }

    public void setAuthaccesstoken(String authaccesstoken) {
        this.authaccesstoken = authaccesstoken;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public int getExpirestime() {
        return expirestime;
    }

    public void setExpirestime(int expirestime) {
        this.expirestime = expirestime;
    }

    public String toString() {
        return this.id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getAeskey() {
        return aeskey;
    }

    public void setAeskey(String aeskey) {
        this.aeskey = aeskey;
    }

    public String getSnsid() {
        return snsid;
    }

    public void setSnsid(String snsid) {
        this.snsid = snsid;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }
}
