/*
 * Copyright (C) 2019 Chatopera Inc, <https://www.chatopera.com>
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

package com.chatopera.cc.acd.basic;

import com.chatopera.cc.model.*;
import com.chatopera.cc.socketio.message.Message;
import com.chatopera.cc.util.IP;

public class ACDComposeContext extends Message {

    // 技能组及渠道
    private String orgi;
    private String organid;
    private Organ organ;
    private String appid;
    private String channel;
    private SNSAccount snsAccount;
    private String sessionid;

    // 策略
    private SessionConfig sessionConfig;
    // 坐席报告
    private AgentReport agentReport;

    // 机器人客服
    private String aiid;
    private boolean isAi;

    // 是否是邀请
    private boolean isInvite;

    private User agent;
    private String agentno;
    private String agentUserId;

    private String agentServiceId;

    private AgentUser agentUser;

    private AgentService agentService;

    // 访客
    private String onlineUserId;
    private OnlineUser onlineUser;
    private String onlineUserNickname;
    private String onlineUserHeadimgUrl;

    // 其它信息
    private IP ipdata;
    private String initiator;
    private String title;
    private String url;
    private String browser;
    private String osname;
    private String traceid;
    private String ownerid;
    private String ip;

    public String getOrganid() {
        return organid;
    }

    public void setOrganid(String organid) {
        this.organid = organid;
    }

    public Organ getOrgan() {
        return organ;
    }

    public void setOrgan(Organ organ) {
        this.organ = organ;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public SNSAccount getSnsAccount() {
        return snsAccount;
    }

    public void setSnsAccount(SNSAccount snsAccount) {
        this.snsAccount = snsAccount;
    }

    public SessionConfig getSessionConfig() {
        return sessionConfig;
    }

    public void setSessionConfig(SessionConfig sessionConfig) {
        this.sessionConfig = sessionConfig;
    }

    public String getAiid() {
        return aiid;
    }

    public void setAiid(String aiid) {
        this.aiid = aiid;
    }

    public boolean isAi() {
        return isAi;
    }

    public void setAi(boolean ai) {
        isAi = ai;
    }

    public boolean isInvite() {
        return isInvite;
    }

    public void setInvite(boolean invite) {
        isInvite = invite;
    }

    public User getAgent() {
        return agent;
    }

    public void setAgent(User agent) {
        this.agent = agent;
    }

    public String getAgentno() {
        return agentno;
    }

    public void setAgentno(String agentno) {
        this.agentno = agentno;
    }

    public String getAgentUserId() {
        return agentUserId;
    }

    public void setAgentUserId(String agentUserId) {
        this.agentUserId = agentUserId;
    }

    public String getOnlineUserId() {
        return onlineUserId;
    }

    public void setOnlineUserId(String onlineUserId) {
        this.onlineUserId = onlineUserId;
    }

    public String getAgentServiceId() {
        return agentServiceId;
    }

    public void setAgentServiceId(String agentServiceId) {
        this.agentServiceId = agentServiceId;
    }

    public AgentUser getAgentUser() {
        return agentUser;
    }

    public void setAgentUser(AgentUser agentUser) {
        this.agentUser = agentUser;
    }

    public OnlineUser getOnlineUser() {
        return onlineUser;
    }

    public void setOnlineUser(OnlineUser onlineUser) {
        this.onlineUser = onlineUser;
    }

    public AgentService getAgentService() {
        return agentService;
    }

    public void setAgentService(AgentService agentService) {
        this.agentService = agentService;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getOnlineUserNickname() {
        return onlineUserNickname;
    }

    public void setOnlineUserNickname(String onlineUserNickname) {
        this.onlineUserNickname = onlineUserNickname;
    }

    public IP getIpdata() {
        return ipdata;
    }

    public void setIpdata(IP ipdata) {
        this.ipdata = ipdata;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
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

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getOsname() {
        return osname;
    }

    public void setOsname(String osname) {
        this.osname = osname;
    }

    public String getTraceid() {
        return traceid;
    }

    public void setTraceid(String traceid) {
        this.traceid = traceid;
    }

    public String getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(String ownerid) {
        this.ownerid = ownerid;
    }

    public String getOrgi() {
        return orgi;
    }

    public void setOrgi(String orgi) {
        this.orgi = orgi;
    }

    public String getOnlineUserHeadimgUrl() {
        return onlineUserHeadimgUrl;
    }

    public void setOnlineUserHeadimgUrl(String onlineUserHeadimgUrl) {
        this.onlineUserHeadimgUrl = onlineUserHeadimgUrl;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public AgentReport getAgentReport() {
        return agentReport;
    }

    public void setAgentReport(AgentReport agentReport) {
        this.agentReport = agentReport;
    }
}
