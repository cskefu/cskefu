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

import javax.persistence.*;
import java.util.*;


/**
 * @author Hai Liang Wang User.java 2019-10-10
 */
@Entity
@Table(name = "cs_user")
@org.hibernate.annotations.Proxy(lazy = false)
public class User implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String sessionid;

    private String username;
    private String password;
    private String email;
    private String uname;
    private String firstname;
    private String midname;
    private String lastname;
    private String language;
    private String jobtitle;
    private String gender;
    private String mobile;
    private String birthday;
    private String nickname;
    private String secureconf = "5";
    private boolean admin;                 // 是否是管理员
    private boolean superadmin = false;    // 是否是系统管理员
    private String orgi;
    private String creater;
    private Date createtime = new Date();
    private Date passupdatetime = new Date();
    private Date updatetime = new Date();
    private String memo;
    private boolean agent;         // 是否开通坐席功能
    // 呼叫中心相关
    private boolean callcenter;    // 是否启用呼叫中心 坐席功能
    private String pbxhostId;      // 语音平台 ID
    private String extensionId;    // 分机号ID

    @Transient
    private PbxHost pbxHost;       // 语音平台

    @Transient
    private Extension extension;   // 分机

    private String city;           // 城市
    private String province;       // 省份
    private boolean login;         // 是否登录

    private String status;         //
    private boolean datastatus;    // 数据状态，是否已删除

    private int maxuser;           // 排队队列最大数值 ， 开启坐席功能后启用
    private String ordertype;      // 坐席的工作队列排序方式


    private Date lastlogintime = new Date();    //最后登录时间

    private AgentStatus agentStatus;

    private int fans;               // 粉丝
    private int follows;            // 关注
    private int integral;           // 积分

    /**
     * 权限相关
     */
    // 直接隶属的组织机构列表，存储机构ID
    private HashMap<String, Organ> organs;

    // 用户所属的技能组们
    private HashMap<String, String> skills;

    // 我的组织机构标识及附属机构们，存储机构ID
    private HashSet<String> affiliates = new HashSet<>();

    // Note: 此处并没有给附属部门于直属部门间的关系建立一个对象缓存，如果需要这个信息，通过API接口进行查询

    // 角色列表
    private List<Role> roleList = new ArrayList<Role>();

    // 角色的权限
    private Map<String, Object> roleAuthMap = new HashMap<String, Object>();

    public User() {
    }

    public User(String id) {
        this.id = id;
    }


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

    @Transient
    public String getSessionid() {
        return sessionid;
    }


    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
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


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getUname() {
        return uname;
    }


    public void setUname(String uname) {
        this.uname = uname;
    }


    public String getFirstname() {
        return firstname;
    }


    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }


    public String getMidname() {
        return midname;
    }


    public void setMidname(String midname) {
        this.midname = midname;
    }


    public String getLastname() {
        return lastname;
    }


    public void setLastname(String lastname) {
        this.lastname = lastname;
    }


    public String getLanguage() {
        return language;
    }


    public void setLanguage(String language) {
        this.language = language;
    }


    public String getJobtitle() {
        return jobtitle;
    }


    public void setJobtitle(String jobtitle) {
        this.jobtitle = jobtitle;
    }


    public String getGender() {
        return gender;
    }


    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getBirthday() {
        return birthday;
    }


    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }


    public String getNickname() {
        return nickname;
    }


    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public String getSecureconf() {
        return secureconf;
    }


    public void setSecureconf(String secureconf) {
        this.secureconf = secureconf;
    }


    public String getOrgi() {
        return orgi;
    }


    public void setOrgi(String orgi) {
        this.orgi = orgi;
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


    public Date getPassupdatetime() {
        return passupdatetime;
    }


    public void setPassupdatetime(Date passupdatetime) {
        this.passupdatetime = passupdatetime;
    }


    public Date getUpdatetime() {
        return updatetime;
    }


    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }


    public String getMemo() {
        return memo;
    }


    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isAgent() {
        return agent;
    }

    public void setAgent(boolean agent) {
        this.agent = agent;
    }

    @Transient
    public HashMap<String, String> getSkills() {
        return skills;
    }

    public void setSkills(HashMap<String, String> skills) {
        this.skills = skills;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Transient
    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public Date getLastlogintime() {
        return lastlogintime;
    }

    public void setLastlogintime(Date lastlogintime) {
        this.lastlogintime = lastlogintime;
    }

    @Transient
    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isDatastatus() {
        return datastatus;
    }

    public void setDatastatus(boolean datastatus) {
        this.datastatus = datastatus;
    }

    @Transient
    public AgentStatus getAgentStatus() {
        return agentStatus;
    }

    public void setAgentStatus(AgentStatus agentStatus) {
        this.agentStatus = agentStatus;
    }

    public boolean isCallcenter() {
        return callcenter;
    }

    public void setCallcenter(boolean callcenter) {
        this.callcenter = callcenter;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isSuperadmin() {
        return superadmin;
    }

    public void setSuperadmin(boolean superadmin) {
        this.superadmin = superadmin;
    }

    @Transient
    public Map<String, Object> getRoleAuthMap() {
        return roleAuthMap;
    }

    public void setRoleAuthMap(Map<String, Object> roleAuthMap) {
        this.roleAuthMap = roleAuthMap;
    }

    public int getMaxuser() {
        return maxuser;
    }

    public void setMaxuser(int maxuser) {
        this.maxuser = maxuser;
    }

    public String getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(String ordertype) {
        this.ordertype = ordertype;
    }

    // 某机构是不是包括我
    public boolean inAffiliates(final String organ) {
        return affiliates.contains(organ);
    }

    @Transient
    public HashSet<String> getAffiliates() {
        return affiliates;
    }

    public void setAffiliates(HashSet<String> affiliates) {
        this.affiliates = affiliates;
    }

    @Transient
    public Map<String, Organ> getOrgans() {
        return organs;
    }

    public void setOrgans(final HashMap<String, Organ> organs) {
        this.organs = organs;
    }

    @Transient
    public String getPbxhostId() {
        return pbxhostId;
    }

    public void setPbxhostId(String pbxhostId) {
        this.pbxhostId = pbxhostId;
    }

    @Transient
    public String getExtensionId() {
        return extensionId;
    }

    public void setExtensionId(String extensionId) {
        this.extensionId = extensionId;
    }

    @Transient
    public PbxHost getPbxHost() {
        return pbxHost;
    }

    public void setPbxHost(PbxHost pbxHost) {
        this.pbxHost = pbxHost;
    }

    @Transient
    public Extension getExtension() {
        return extension;
    }

    public void setExtension(Extension extension) {
        this.extension = extension;
    }
}
