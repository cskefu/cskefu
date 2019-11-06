/*
 * Copyright (C) 2019 Chatopera Inc, All rights reserved.
 * <https://www.chatopera.com>
 * This software and related documentation are provided under a license agreement containing
 * restrictions on use and disclosure and are protected by intellectual property laws.
 * Except as expressly permitted in your license agreement or allowed by law, you may not use,
 * copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform,
 * publish, or display any part, in any form, or by any means. Reverse engineering, disassembly,
 * or decompilation of this software, unless required by law for interoperability, is prohibited.
 */

package com.chatopera.cc.proxy;

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.model.Organ;
import com.chatopera.cc.model.OrganUser;
import com.chatopera.cc.model.User;
import com.chatopera.cc.persistence.repository.OrganRepository;
import com.chatopera.cc.persistence.repository.OrganUserRepository;
import com.chatopera.cc.persistence.repository.UserRepository;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 用户/坐席 常用方法
 */
@Component
public class UserProxy {
    private final static Logger logger = LoggerFactory.getLogger(UserProxy.class);

    @Autowired
    private OrganUserRepository organUserRes;

    @Autowired
    private OrganRepository organRes;

    @Autowired
    private UserRepository userRes;

    /**
     * 创建新用户
     * 支持多租户
     *
     * @param user
     * @param orgi
     * @param orgid
     * @param orgiByTenantshare
     * @return
     */
    public String createNewUser(final User user, final String orgi, final String orgid, final String orgiByTenantshare) {
        String msg = "";
        msg = validUser(user);
        if (StringUtils.isNotBlank(msg) && !msg.equals("new_user_success")) {
            return msg;
        } else {
            // 此时 msg 是 new_user_success
            user.setSuperadmin(false); // 不支持创建第二个超级管理员

            if (StringUtils.isNotBlank(user.getPassword())) {
                user.setPassword(MainUtils.md5(user.getPassword()));
            }

            user.setOrgi(orgiByTenantshare);
            if (StringUtils.isNotBlank(orgid)) {
                user.setOrgid(orgid);
            } else {
                user.setOrgid(MainContext.SYSTEM_ORGI);
            }
            userRes.save(user);
            OnlineUserProxy.clean(orgi);
        }
        return msg;
    }


    public User findOne(final String id) {
        return userRes.findOne(id);
    }

    public List<String> findUserIdsInOrgan(final String organ) {
        List<OrganUser> x = organUserRes.findByOrgan(organ);

        if (x.size() == 0) {
            return null;
        }

        List<String> z = new ArrayList<>();
        for (final OrganUser y : x) {
            z.add(y.getUserid());
        }
        return z;
    }

    public List<String> findUserIdsInOrgans(final List<String> organs) {

        List<OrganUser> x = organUserRes.findByOrganIn(organs);

        if (x.size() == 0) return null;

        Set<String> y = new HashSet<>();

        for (final OrganUser z : x) {
            y.add(z.getUserid());
        }

        return new ArrayList<>(y);

    }


    /**
     * 通过坐席ID查找其技能组Map
     *
     * @param agentno
     * @return
     */
    public HashMap<String, String> getSkillsMapByAgentno(final String agentno) {

        final User user = userRes.findOne(agentno);
        if (user == null) return new HashMap<>();

        attachOrgansPropertiesForUser(user);
        return user.getSkills();
    }

    /**
     * 获得一个用户的直属组织机构
     *
     * @param userid
     * @return
     */
    public List<String> findOrgansByUserid(final String userid) {
        List<OrganUser> x = organUserRes.findByUserid(userid);

        if (x.size() == 0) return null;

        List<String> y = new ArrayList<>();

        for (final OrganUser z : x) {
            y.add(z.getOrgan());
        }

        return y;
    }


    public Page<User> findByOrganInAndAgentAndDatastatus(
            final List<String> organs,
            boolean agent,
            boolean datastatus,
            Pageable pageRequest) {
        List<String> users = findUserIdsInOrgans(organs);

        if (users == null) return null;

        return userRes.findByAgentAndDatastatusAndIdIn(agent, datastatus, users, pageRequest);

    }

    public List<User> findByOrganInAndAgentAndDatastatus(
            final List<String> organs,
            boolean agent,
            boolean datastatus) {
        List<String> users = findUserIdsInOrgans(organs);

        if (users == null) return null;

        return userRes.findByAgentAndDatastatusAndIdIn(agent, datastatus, users);
    }

    public List<User> findByOrganInAndDatastatus(
            final List<String> organs,
            boolean datastatus) {
        List<String> users = findUserIdsInOrgans(organs);

        if (users == null) return null;

        return userRes.findByDatastatusAndIdIn(datastatus, users);
    }

    public Page<User> findByOrganInAndDatastatusAndUsernameLike(
            final List<String> organs,
            final boolean datastatus,
            final String username,
            Pageable pageRequest) {
        List<String> users = findUserIdsInOrgans(organs);
        if (users == null) return null;
        return userRes.findByDatastatusAndUsernameLikeAndIdIn(datastatus, username, users, pageRequest);
    }

    public List<User> findByOrganAndOrgiAndDatastatus(final String organ, final String orgi, final boolean datastatus) {
        List<String> users = findUserIdsInOrgan(organ);

        if (users == null) return null;

        return userRes.findByOrgiAndDatastatusAndIdIn(orgi, datastatus, users);

    }

    /**
     * 根据是否开启呼叫中心模块检测账号
     *
     * @param user
     * @return
     */
    public boolean validUserCallcenterParams(final User user) {
        if (user.isCallcenter() && MainContext.hasModule(Constants.CSKEFU_MODULE_CALLOUT)) {
            return (!userRes.findOneBySipaccountAndDatastatus(
                    user.getSipaccount(), false).isPresent()) || user.getSipaccount() == "";
        }
        return true;
    }

    /**
     * 从Json中创建User
     *
     * @param json
     * @return
     */
    private User exetractUserFromJson(final JsonObject json) {
        User tempUser = new User();

        if (json.has("username")) {
            String val = json.get("username").getAsString();
            if (StringUtils.isNotBlank(val)) {
                tempUser.setUsername(val);
            }
        }

        if (json.has("uname")) {
            String val = json.get("uname").getAsString();
            if (StringUtils.isNotBlank(val)) {
                tempUser.setUname(val);
            }
        }

        if (json.has("email")) {
            String val = json.get("email").getAsString();
            if (StringUtils.isNotBlank(val)) {
                tempUser.setEmail(val);
            }
        }

        if (json.has("mobile")) {
            String val = json.get("mobile").getAsString();
            if (StringUtils.isNotBlank(val)) {
                tempUser.setMobile(val);
            }
        }

        // 密码


        return tempUser;
    }

    /**
     * 验证用户数据合法性
     *
     * @param user
     * @return
     */
    public String validUser(final User user) {
        String msg = "";
        User tempUser = userRes.findByUsernameAndDatastatus(user.getUsername(), false);
        if (tempUser != null) {
            msg = "username_exist";
            return msg;
        }

        if (StringUtils.isNotBlank(user.getEmail())) {
            tempUser = userRes.findByEmailAndDatastatus(user.getEmail(), false);
            if (tempUser != null) {
                msg = "email_exist";
                return msg;
            }
        }

        if (StringUtils.isNotBlank(user.getMobile())) {
            tempUser = userRes.findByMobileAndDatastatus(user.getMobile(), false);
            if (tempUser != null) {
                msg = "mobile_exist";
                return msg;
            }
        }

        if (!validUserCallcenterParams(user)) {
            msg = "sip_account_exist";
            return msg;
        }

        if (tempUser == null && validUserCallcenterParams(user)) {
            msg = "new_user_success";
            return msg;
        }

        return msg;
    }


    public List<User> findAllByCallcenterIsTrueAndDatastatusIsFalseAndOrgan(final String organ) {

        final List<String> users = findUserIdsInOrgan(organ);

        if (users == null) return null;

        return userRes.findAllByCallcenterIsTrueAndDatastatusIsFalseAndIdIn(users);

    }

    /**
     * 或取Sips列表
     *
     * @param organ
     * @param datastatus
     * @param orgi
     * @return
     */
    public List<String> findSipsByOrganAndDatastatusAndOrgi(final String organ, final boolean datastatus, final String orgi) {
        List<String> users = findUserIdsInOrgan(organ);

        if (users == null) return null;

        return userRes.findSipsByDatastatusAndOrgiAndIdIn(datastatus, orgi, users);
    }


    /**
     * 通过租户ID，是否为坐席，是否有效和组织机构查询坐席数
     *
     * @param orgi
     * @param agent
     * @param datastatus
     * @param organ
     * @return
     */
    public long countByOrgiAndAgentAndDatastatusAndOrgan(
            final String orgi,
            final boolean agent,
            final boolean datastatus,
            final String organ) {

        final List<String> users = findUserIdsInOrgan(organ);

        if (users == null) return 0;

        return userRes.countByAgentAndDatastatusAndIdIn(agent, datastatus, users);

    }

    /**
     * 获得一个部门及其子部门并添加到User的myorgans中
     *
     * @param user
     */
    public void processAffiliates(final User user, final Map<String, String> skills, final Organ organ) {
        if (organ == null) {
            return;
        }

        if (user.inAffiliates(organ.getId())) {
            return;
        }

        user.getAffiliates().add(organ.getId());

        if (organ.isSkill()) skills.put(organ.getId(), organ.getName());

        // 获得子部门
        List<Organ> y = organRes.findByOrgiAndParent(user.getOrgi(), organ.getId());

        for (Organ x : y) {
            try {
                // 递归调用
                processAffiliates(user, skills, x);
            } catch (Exception e) {
                logger.error("processAffiliates", e);
            }
        }
    }

    /**
     * 获取用户部门以及下级部门
     *
     * @param user
     */
    public void attachOrgansPropertiesForUser(final User user) {
        List<OrganUser> organs = organUserRes.findByUserid(user.getId());
        user.setOrgans(new HashMap<>());
        final HashMap<String, String> skills = new HashMap<>();

        for (final OrganUser organ : organs) {
            // 添加直属部门到organs
            final Organ o = organRes.findOne(organ.getOrgan());
            user.getOrgans().put(organ.getOrgan(), o);

            // 添加部门及附属部门
            processAffiliates(user, skills, o);
        }

        user.setSkills(skills);
    }
}
