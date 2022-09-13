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
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "uk_agentuser")
@Proxy(lazy = false)
public class AgentUser implements Serializable, Comparable<AgentUser> {
    private static final long serialVersionUID = -8657469468192323550L;
    private String id;

    // 访客的信息
    private String username;

    private String userid;
    private String channel;
    private Date logindate;
    private String source;
    private Date endtime;

    private String title;
    private String url;
    private String traceid;

    private String owner;    //变更用处，修改为 智能IVR的 EventID

    private String ipaddr;
    private String osname;
    private String browser;
    private String nickname;
    protected String city;
    private String sessionid;
    protected String province;
    protected String country;
    protected String headimgurl;
    private String region;
    private long sessiontimes = 0L;
    private int waittingtime;
    private int tokenum;
    private Date createtime = new Date();
    private Date updatetime;
    private String status;
    private String appid;
    private String sessiontype;
    private String contextid = MainUtils.getUUID();
    private String agentserviceid;
    private String orgi;
    private long ordertime = System.currentTimeMillis();
    private String snsuser;
    private Date lastmessage = new Date();
    private Date servicetime;
    private Date waittingtimestart = new Date();
    private Date lastgetmessage = new Date();
    private String lastmsg;
    private String opttype;

    // 客服的信息
    private String skill;        // 请求的技能组
    private String skillname;    // 技能组名称
    private String agentno;      // 请求的坐席
    private String agentname;    // 客服的名字

    @Transient
    private String agentskill;
    private String agentservice;
    // 访客的信息
    private String name;
    private String email;
    private String phone;
    private String resion;



    private boolean chatbotops;    // 是否是机器人客服
    private int chatbotlogicerror; // 机器人客服逻辑错误回复累计
    private int chatbotround;      // 机器人客服对话轮次(一问一答是一轮)

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    private String creater;

    @Transient
    private boolean tip = false;
    @Transient
    private boolean agentTip = false;

    @Transient
    public boolean isAgentTip() {
        return this.agentTip;
    }

    public void setAgentTip(boolean agentTip) {
        this.agentTip = agentTip;
    }

    @Transient
    private boolean fromhis = false;
    @Transient
    private boolean online = false;
    @Transient
    private boolean disconnect = false;


    public AgentUser() {
    }

    public AgentUser(String userid, String channel, String snsuser,
                     String username, String orgi, String appid) {
        this.userid = userid;
        this.channel = channel;
        this.snsuser = snsuser;
        this.appid = appid;
        this.username = username;
        this.orgi = orgi;
    }

    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    public String getId() {
        return this.id;
    }

    public Date getCreatetime() {
        return this.createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return this.updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getChannel() {
        return this.channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Date getLogindate() {
        return this.logindate;
    }

    public void setLogindate(Date logindate) {
        this.logindate = logindate;
    }

    public String getContextid() {
        return this.contextid;
    }

    public void setContextid(String contextid) {
        this.contextid = contextid;
    }

    public String getAgentno() {
        return this.agentno;
    }

    public void setAgentno(String agentno) {
        this.agentno = agentno;
    }

    public String getAgentserviceid() {
        return this.agentserviceid;
    }

    public void setAgentserviceid(String agentserviceid) {
        this.agentserviceid = agentserviceid;
    }

    public String getOrgi() {
        return this.orgi;
    }

    public void setOrgi(String orgi) {
        this.orgi = orgi;
    }

    public Date getLastmessage() {
        return this.lastmessage;
    }

    public void setLastmessage(Date lastmessage) {
        this.lastmessage = lastmessage;
    }

    @Transient
    public boolean isTip() {
        return this.tip;
    }

    public void setTip(boolean tip) {
        this.tip = tip;
    }

    @Transient
    public boolean isDisconnect() {
        return this.disconnect;
    }

    public void setDisconnect(boolean disconnect) {
        this.disconnect = disconnect;
    }

    public Date getEndtime() {
        return this.endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public long getSessiontimes() {
        return this.sessiontimes;
    }

    public void setSessiontimes(long sessiontimes) {
        this.sessiontimes = sessiontimes;
    }

    @Transient
    public String getSessiontype() {
        return this.sessiontype;
    }

    public void setSessiontype(String sessiontype) {
        this.sessiontype = sessiontype;
    }

    public String getAgentskill() {
        return this.agentskill;
    }

    public void setAgentskill(String agentskill) {
        this.agentskill = agentskill;
    }

    @Transient
    public boolean isOnline() {
        return this.online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Transient
    public boolean isFromhis() {
        return this.fromhis;
    }

    public void setFromhis(boolean fromhis) {
        this.fromhis = fromhis;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getLastgetmessage() {
        return this.lastgetmessage;
    }

    public void setLastgetmessage(Date lastgetmessage) {
        this.lastgetmessage = lastgetmessage;
    }

    public String getLastmsg() {
        return this.lastmsg;
    }

    public void setLastmsg(String lastmsg) {
        this.lastmsg = lastmsg;
    }

    public String getSnsuser() {
        return this.snsuser;
    }

    public void setSnsuser(String snsuser) {
        this.snsuser = snsuser;
    }

    public int getWaittingtime() {
        return this.waittingtime;
    }

    public void setWaittingtime(int waittingtime) {
        this.waittingtime = waittingtime;
    }

    public int getTokenum() {
        return this.tokenum;
    }

    public void setTokenum(int tokenum) {
        this.tokenum = tokenum;
    }

    public Date getWaittingtimestart() {
        return this.waittingtimestart;
    }

    public void setWaittingtimestart(Date waittingtimestart) {
        this.waittingtimestart = waittingtimestart;
    }

    public String getAppid() {
        return this.appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadimgurl() {
        return this.headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIpaddr() {
        return this.ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public String getOsname() {
        return this.osname;
    }

    public void setOsname(String osname) {
        this.osname = osname;
    }

    public String getBrowser() {
        return this.browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    @Transient
    public String getTopic() {
        return "/" + this.orgi + "/" + this.agentno;
    }

    @Transient
    public long getOrdertime() {
        return this.ordertime;
    }

    public void setOrdertime(long ordertime) {
        this.ordertime = ordertime;
    }

    public Date getServicetime() {
        return this.servicetime;
    }

    public void setServicetime(Date servicetime) {
        this.servicetime = servicetime;
    }

    public String getRegion() {
        return this.region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAgentservice() {
        return this.agentservice;
    }

    public void setAgentservice(String agentservice) {
        this.agentservice = agentservice;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getResion() {
        return resion;
    }

    public void setResion(String resion) {
        this.resion = resion;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTraceid() {
        return traceid;
    }

    public void setTraceid(String traceid) {
        this.traceid = traceid;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Transient
    public String getSkillname() {
        return skillname;
    }

    public void setSkillname(String skillname) {
        this.skillname = skillname;
    }

    public String getOpttype() {
        return opttype;
    }

    public void setOpttype(String opttype) {
        this.opttype = opttype;
    }

    public boolean isChatbotops() {
        return chatbotops;
    }

    public void setChatbotops(boolean chatbotops) {
        this.chatbotops = chatbotops;
    }

    public int getChatbotlogicerror() {
        return chatbotlogicerror;
    }

    public void setChatbotlogicerror(int chatbotlogicerror) {
        this.chatbotlogicerror = chatbotlogicerror;
    }

    public int getChatbotround() {
        return chatbotround;
    }

    public void setChatbotround(int chatbotround) {
        this.chatbotround = chatbotround;
    }

    @Override
    public int compareTo(AgentUser o) {
        int ret = 0;
        if (this.getLogindate() == null) {
            ret = -1;
        } else if (o.getLogindate() == null) {
            ret = 1;
        } else {
            if (this.getLogindate().after(o.getLogindate())) {
                ret = 1;
            } else {
                ret = -1;
            }
        }
        return ret;
    }

    public String getAgentname() {
        return agentname;
    }

    public void setAgentname(String agentname) {
        this.agentname = agentname;
    }
}
