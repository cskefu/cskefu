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
package com.chatopera.cc.model;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "uk_systemconfig")
@org.hibernate.annotations.Proxy(lazy = false)
public class SystemConfig implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -8675632756915176657L;
    private String id;
    private String name;
    private String title;

    private String theme = "01";    //默认绿色

    private String loginlogo;        //登录页 LOGO
    private String consolelogo;    //后台页面LOGO
    private String favlogo;        //系统Fav图片，icon格式，小图标

    private String code;
    private String orgi;
    private String description;
    private String memo;
    private String creater;
    private Date createtime;
    private Date updatetime;
    private String loglevel;
    private boolean enablessl;
    private String jksfile;
    private String jkspassword;
    private String mapkey;
    private boolean workorders;    //工单启用三栏布局
    private String iconstr;            //修改服务器url地址

    private boolean callout;        //允许在电话号码上点击呼出
    private boolean auth;            //启用权限控制

    private boolean callcenter;    //启用呼叫中心模板配置
    private String cc_extention;
    private String cc_quene;
    private String cc_router;
    private String cc_ivr;
    private String cc_acl;
    private String cc_siptrunk;
    private String cc_callcenter;


    private boolean enablemail;    //启用电子邮件
    private String emailid;        //电子邮件服务器
    private String emailworkordertp;//工单电子邮件默认模板
    private String mailcreatetp;    //工单创建的邮件模板
    private String mailupdatetp;    //工单状态更新的邮件模板
    private String mailprocesstp;    //工单审批信息的邮件模板

    private boolean emailtocreater;    //工单状态有更新的时候通知创建人
    private String emailtocreatertp;    //发送给创建人的邮件模板

    private boolean emailshowrecipient; //是否显示收件人名称

    private boolean enablesms;        //启用短信
    private String smsid;            //短信网关ID
    private String smsworkordertp;    //工单短信通知的模板

    private String smscreatetp;    //工单创建的邮件模板
    private String smsupdatetp;    //工单状态更新的邮件模板
    private String smsprocesstp;    //工单审批信息的邮件模板

    private boolean smstocreater;    //工单状态有更新的时候通知创建人
    private String smstocreatertp;//工单状态有更新的时候通知创建人的短信模板


    private boolean enabletneant;    //启用多租户管理模式

    @Transient
    private boolean userExpTelemetrySetting; // 加入用户体验计划的设置

    public boolean isEmailshowrecipient() {
        return emailshowrecipient;
    }

    public void setEmailshowrecipient(boolean emailshowrecipient) {
        this.emailshowrecipient = emailshowrecipient;
    }

    private boolean tenantshare;    //多租户模式下共享组织机构
    private String namealias;        //多租户模式的名称
    private boolean tenantconsole;    //登录后进入租户选择模式

    private boolean enableregorgi; //启用自主注册功能


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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOrgi() {
        return orgi;
    }

    public void setOrgi(String orgi) {
        this.orgi = orgi;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
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

    public String getLoglevel() {
        return loglevel;
    }

    public void setLoglevel(String loglevel) {
        this.loglevel = loglevel;
    }

    public boolean isEnablessl() {
        return enablessl;
    }

    public void setEnablessl(boolean enablessl) {
        this.enablessl = enablessl;
    }

    public String getJksfile() {
        return jksfile;
    }

    public void setJksfile(String jksfile) {
        this.jksfile = jksfile;
    }

    public String getJkspassword() {
        return jkspassword;
    }

    public void setJkspassword(String jkspassword) {
        this.jkspassword = jkspassword;
    }

    public String getMapkey() {
        return mapkey;
    }

    public void setMapkey(String mapkey) {
        this.mapkey = mapkey;
    }

    public boolean isWorkorders() {
        return workorders;
    }

    public void setWorkorders(boolean workorders) {
        this.workorders = workorders;
    }

    public boolean isCallcenter() {
        return callcenter;
    }

    public void setCallcenter(boolean callcenter) {
        this.callcenter = callcenter;
    }

    public String getCc_extention() {
        return cc_extention;
    }

    public void setCc_extention(String cc_extention) {
        this.cc_extention = cc_extention;
    }

    public String getCc_quene() {
        return cc_quene;
    }

    public void setCc_quene(String cc_quene) {
        this.cc_quene = cc_quene;
    }

    public String getCc_router() {
        return cc_router;
    }

    public void setCc_router(String cc_router) {
        this.cc_router = cc_router;
    }

    public String getCc_ivr() {
        return cc_ivr;
    }

    public void setCc_ivr(String cc_ivr) {
        this.cc_ivr = cc_ivr;
    }

    public String getCc_acl() {
        return cc_acl;
    }

    public void setCc_acl(String cc_acl) {
        this.cc_acl = cc_acl;
    }

    public String getCc_siptrunk() {
        return cc_siptrunk;
    }

    public void setCc_siptrunk(String cc_siptrunk) {
        this.cc_siptrunk = cc_siptrunk;
    }

    public String getCc_callcenter() {
        return cc_callcenter;
    }

    public void setCc_callcenter(String cc_callcenter) {
        this.cc_callcenter = cc_callcenter;
    }

    public boolean isCallout() {
        return callout;
    }

    public void setCallout(boolean callout) {
        this.callout = callout;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public boolean isEnablemail() {
        return enablemail;
    }

    public void setEnablemail(boolean enablemail) {
        this.enablemail = enablemail;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getEmailworkordertp() {
        return emailworkordertp;
    }

    public void setEmailworkordertp(String emailworkordertp) {
        this.emailworkordertp = emailworkordertp;
    }

    public boolean isEnablesms() {
        return enablesms;
    }

    public void setEnablesms(boolean enablesms) {
        this.enablesms = enablesms;
    }

    public String getSmsid() {
        return smsid;
    }

    public void setSmsid(String smsid) {
        this.smsid = smsid;
    }

    public String getSmsworkordertp() {
        return smsworkordertp;
    }

    public void setSmsworkordertp(String smsworkordertp) {
        this.smsworkordertp = smsworkordertp;
    }

    public String getMailcreatetp() {
        return mailcreatetp;
    }

    public void setMailcreatetp(String mailcreatetp) {
        this.mailcreatetp = mailcreatetp;
    }

    public String getMailupdatetp() {
        return mailupdatetp;
    }

    public void setMailupdatetp(String mailupdatetp) {
        this.mailupdatetp = mailupdatetp;
    }

    public String getMailprocesstp() {
        return mailprocesstp;
    }

    public void setMailprocesstp(String mailprocesstp) {
        this.mailprocesstp = mailprocesstp;
    }

    public boolean isEmailtocreater() {
        return emailtocreater;
    }

    public void setEmailtocreater(boolean emailtocreater) {
        this.emailtocreater = emailtocreater;
    }

    public String getSmscreatetp() {
        return smscreatetp;
    }

    public void setSmscreatetp(String smscreatetp) {
        this.smscreatetp = smscreatetp;
    }

    public String getSmsupdatetp() {
        return smsupdatetp;
    }

    public void setSmsupdatetp(String smsupdatetp) {
        this.smsupdatetp = smsupdatetp;
    }

    public String getSmsprocesstp() {
        return smsprocesstp;
    }

    public void setSmsprocesstp(String smsprocesstp) {
        this.smsprocesstp = smsprocesstp;
    }

    public boolean isSmstocreater() {
        return smstocreater;
    }

    public void setSmstocreater(boolean smstocreater) {
        this.smstocreater = smstocreater;
    }

    public String getEmailtocreatertp() {
        return emailtocreatertp;
    }

    public void setEmailtocreatertp(String emailtocreatertp) {
        this.emailtocreatertp = emailtocreatertp;
    }

    public String getSmstocreatertp() {
        return smstocreatertp;
    }

    public void setSmstocreatertp(String smstocreatertp) {
        this.smstocreatertp = smstocreatertp;
    }

    public boolean isEnabletneant() {
        return enabletneant;
    }

    public void setEnabletneant(boolean enabletneant) {
        this.enabletneant = enabletneant;
    }

    public boolean isTenantshare() {
        return tenantshare;
    }

    public void setTenantshare(boolean tenantshare) {
        this.tenantshare = tenantshare;
    }

    public String getNamealias() {
        return namealias;
    }

    public void setNamealias(String namealias) {
        this.namealias = namealias;
    }

    public boolean isTenantconsole() {
        return tenantconsole;
    }

    public void setTenantconsole(boolean tenantconsole) {
        this.tenantconsole = tenantconsole;
    }

    public boolean isEnableregorgi() {
        return enableregorgi;
    }

    public void setEnableregorgi(boolean enableregorgi) {
        this.enableregorgi = enableregorgi;
    }

    public String getLoginlogo() {
        return loginlogo;
    }

    public void setLoginlogo(String loginlogo) {
        this.loginlogo = loginlogo;
    }

    public String getConsolelogo() {
        return consolelogo;
    }

    public void setConsolelogo(String consolelogo) {
        this.consolelogo = consolelogo;
    }

    public String getFavlogo() {
        return favlogo;
    }

    public void setFavlogo(String favlogo) {
        this.favlogo = favlogo;
    }

    @Transient
    public String getBackgroundColor() {
        String backgroundColor = "background-color:#32c24d !important;";
        if (!StringUtils.isBlank(this.theme) && this.theme.equals("01")) {
            backgroundColor = "background-color:#32c24d !important;";
        } else if (!StringUtils.isBlank(this.theme) && this.theme.equals("02")) {
            backgroundColor = "background-color:#373d41 !important;";
        } else if (!StringUtils.isBlank(this.theme) && this.theme.equals("03")) {
            backgroundColor = "background-image: -webkit-linear-gradient(right,#00c89d 0,#1E90FF 100%) !important;";
        }
        return backgroundColor;
    }

    @Transient
    public String getColor() {
        String color = "color:#32c24d;";
        if (!StringUtils.isBlank(this.theme) && this.theme.equals("01")) {
            color = "color:#32c24d;";
        } else if (!StringUtils.isBlank(this.theme) && this.theme.equals("02")) {
            color = "color:#32c24d;";
        } else if (!StringUtils.isBlank(this.theme) && this.theme.equals("03")) {
            color = "color:#1E90FF;";
        }
        return color;
    }

    @Transient
    public String getBgColor() {
        String color = "background-color:#32c24d;";
        if (!StringUtils.isBlank(this.theme) && this.theme.equals("01")) {
            color = "background-color:#32c24d !important;";
        } else if (!StringUtils.isBlank(this.theme) && this.theme.equals("02")) {
            color = "background-color:#32c24d !important;";
        } else if (!StringUtils.isBlank(this.theme) && this.theme.equals("03")) {
            color = "background-color:#1E90FF !important;";
        }
        return color;
    }

    @Transient
    public String getStyleColor() {
        String color = "#32c24d  !important;";
        if (!StringUtils.isBlank(this.theme) && this.theme.equals("01")) {
            color = "#32c24d  !important;";
        } else if (!StringUtils.isBlank(this.theme) && this.theme.equals("02")) {
            color = "#32c24d  !important;";
        } else if (!StringUtils.isBlank(this.theme) && this.theme.equals("03")) {
            color = "#1E90FF  !important;";
        }
        return color;
    }

    public String getIconstr() {
        return iconstr;
    }

    public void setIconstr(String iconstr) {
        this.iconstr = iconstr;
    }

    @Transient
    public boolean getUserExpTelemetrySetting() {
        return userExpTelemetrySetting;
    }

    public void setUserExpTelemetrySetting(boolean userExpTelemetrySetting) {
        this.userExpTelemetrySetting = userExpTelemetrySetting;
    }
}
