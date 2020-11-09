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
import com.chatopera.cc.controller.api.request.RestUtils;
import com.chatopera.cc.model.*;
import com.chatopera.cc.persistence.repository.*;
import com.google.gson.JsonObject;
import oracle.jdbc.driver.Const;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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

    @Autowired
    private RoleAuthRepository roleAuthRes;

    @Autowired
    private PbxHostRepository pbxHostRes;

    @Autowired
    private ExtensionRepository extensionRes;

    public JsonObject createNewUser(final User user) {
        return this.createNewUser(user, null);
    }

    /**
     * 创建新用户
     * 支持多租户
     *
     * @param user
     * @param organ
     * @return
     */
    public JsonObject createNewUser(final User user, Organ organ) {
        JsonObject result = new JsonObject();
        String msg = validUser(user);
        if (StringUtils.equalsIgnoreCase(msg, "new_user_success")) {
            // 此时 msg 是 new_user_success
            user.setSuperadmin(false); // 不支持创建第二个系统管理员
            user.setOrgi(Constants.SYSTEM_ORGI);


            if (StringUtils.isNotBlank(user.getPassword())) {
                user.setPassword(MainUtils.md5(user.getPassword()));
            }
            userRes.save(user);

            if(organ!=null) {
                OrganUser ou = new OrganUser();
                ou.setUserid(user.getId());
                ou.setOrgan(organ.getId());
                organUserRes.save(ou);
            }

        }
        // 新账号未通过验证，返回创建失败信息msg
        result.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
        result.addProperty(RestUtils.RESP_KEY_DATA, msg);
        return result;
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

    public List<String> findUserIdsInOrgans(final Collection<String> organs) {

        List<OrganUser> x = organUserRes.findByOrganIn(organs);

        if (x.size() == 0) return null;

        Set<String> y = new HashSet<>();

        for (final OrganUser z : x) {
            y.add(z.getUserid());
        }

        return new ArrayList<>(y);

    }

    /**
     * 通过技能组查找技能组下坐席所有信息
     *
     * @param organs
     * @return
     */
    public List<User> findUserInOrgans(final Collection<String> organs) {
        List<OrganUser> x = organUserRes.findByOrganIn(organs);
        if (x.size() == 0) return null;
        Set<String> y = new HashSet<>();
        for (final OrganUser z : x) {
            y.add(z.getUserid());
        }
        return userRes.findAll(y);
    }

    public Page<User> findUserInOrgans(final Collection<String> organs,
                                       Pageable pageRequest) {
        List<OrganUser> x = organUserRes.findByOrganIn(organs);
        if (x.size() == 0) return null;
        Set<String> y = new HashSet<>();
        for (final OrganUser z : x) {
            y.add(z.getUserid());
        }
        return userRes.findByIdIn(y, pageRequest);
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
            final Collection<String> organs,
            boolean agent,
            boolean datastatus) {
        List<String> users = findUserIdsInOrgans(organs);

        if (users == null) return null;

        return userRes.findByAgentAndDatastatusAndIdIn(agent, datastatus, users);
    }

    public List<User> findByOrganInAndDatastatus(
            final Collection<String> organs,
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
     * 检查用户更新是否合理
     *
     * @param user
     * @param oldUser
     * @return
     */
    public String validUserUpdate(final User user, final User oldUser) {
        String msg = "edit_user_success";
        User tempUser = userRes.findByUsernameAndDatastatus(user.getUsername(), false);

        if (!StringUtils.equals(user.getUsername(), oldUser.getUsername()) && tempUser != null && (!StringUtils.equals(
                oldUser.getId(), tempUser.getId()))) {
            // 用户名发生变更，并且数据库里有其它用户占用该用户名
            msg = "username_exist";
            return msg;
        }

        if (StringUtils.isNotBlank(user.getEmail())) {
            tempUser = userRes.findByEmailAndDatastatus(user.getEmail(), false);
            if (!StringUtils.equals(user.getEmail(), oldUser.getEmail()) && tempUser != null && (!StringUtils.equals(
                    oldUser.getId(), tempUser.getId()))) {
                msg = "email_exist";
                return msg;
            }
        }

        if (StringUtils.isNotBlank(user.getMobile())) {
            tempUser = userRes.findByMobileAndDatastatus(user.getMobile(), false);
            if (!StringUtils.equals(user.getMobile(), oldUser.getMobile()) && tempUser != null && (!StringUtils.equals(
                    oldUser.getId(), tempUser.getId()))) {
                msg = "mobile_exist";
                return msg;
            }
        }

        return msg;
    }

    /**
     * 从Json中创建User
     *
     * @param payload
     * @return
     */
    public User parseUserFromJson(final JsonObject payload) {
        User user = new User();

        // 手机号
        if (payload.has("id")) {
            String val = payload.get("id").getAsString();
            if (StringUtils.isNotBlank(val)) {
                user.setId(val);
            }
        }

        // 用户名，用于登录
        if (payload.has("username")) {
            String val = payload.get("username").getAsString();
            if (StringUtils.isNotBlank(val)) {
                user.setUsername(val);
            }
        }

        // 姓名
        if (payload.has("uname")) {
            String val = payload.get("uname").getAsString();
            if (StringUtils.isNotBlank(val)) {
                user.setUname(val);
            }
        }

        // 邮件
        if (payload.has("email")) {
            String val = payload.get("email").getAsString();
            if (StringUtils.isNotBlank(val)) {
                user.setEmail(val);
            }
        }

        // 手机号
        if (payload.has("mobile")) {
            String val = payload.get("mobile").getAsString();
            if (StringUtils.isNotBlank(val)) {
                user.setMobile(val);
            }
        }

        // 密码
        if (payload.has("password")) {
            String val = payload.get("password").getAsString();
            if (StringUtils.isNotBlank(val)) {
                user.setPassword(val);
            }
        }

        // 是否是坐席
        if (payload.has("agent")) {
            String val = payload.get("agent").getAsString();
            if (StringUtils.isNotBlank(val) && StringUtils.equals("1", val)) {
                user.setAgent(true);
            } else {
                user.setAgent(false);
            }
        } else {
            user.setAgent(false);
        }

        // 是否是管理员
        if (payload.has("admin")) {
            String val = payload.get("admin").getAsString();
            if (StringUtils.isNotBlank(val) && StringUtils.equals("1", val)) {
                // 管理员默认就是坐席
                user.setAdmin(true);
                user.setAgent(true);
            } else {
                user.setAdmin(false);
            }
        } else {
            user.setAdmin(false);
        }

        // 是否是呼叫中心
        if (payload.has("callcenter")) {
            if (StringUtils.equals(payload.get("callcenter").getAsString(), "1")) {
                user.setCallcenter(true);
                // 当为呼叫中心坐席时，同时提取pbxhostid和extensionid
                if (payload.has("pbxhostid")) {
                    user.setPbxhostId(payload.get("pbxhostid").getAsString());
                }

                if (payload.has("extensionid")) {
                    user.setExtensionId(payload.get("extensionid").getAsString());
                }
            } else {
                user.setCallcenter(false);
            }
        } else {
            user.setCallcenter(false);
        }

        // 不允许创建系统管理员
        user.setSuperadmin(false);

        return user;
    }

    /**
     * 验证用户数据合法性
     *
     * @param user
     * @return
     */
    public String validUser(final User user) {
        String msg = "new_user_success";
        User exist = userRes.findByUsernameAndDatastatus(user.getUsername(), false);
        if (exist != null) {
            msg = "username_exist";
            return msg;
        }

        if (StringUtils.isNotBlank(user.getEmail())) {
            exist = userRes.findByEmailAndDatastatus(user.getEmail(), false);
            if (exist != null) {
                msg = "email_exist";
                return msg;
            }
        }

        if (StringUtils.isNotBlank(user.getMobile())) {
            exist = userRes.findByMobileAndDatastatus(user.getMobile(), false);
            if (exist != null) {
                msg = "mobile_exist";
                return msg;
            }
        }

        // 检查作为呼叫中心坐席的信息
        if (MainContext.hasModule(Constants.CSKEFU_MODULE_CALLCENTER) && user.isCallcenter()) {
            final PbxHost pbxHost = pbxHostRes.findOne(user.getPbxhostId());
            if (pbxHost != null) {
                Extension extension = extensionRes.findOne(user.getExtensionId());
                if (extension != null) {
                    if (StringUtils.isNotBlank(extension.getAgentno())) {
                        // 呼叫中心该分机已经绑定
                        msg = "extension_binded";
                    }
                } else {
                    // 该分机不存在
                    msg = "extension_not_exist";
                }
            } else {
                // 呼叫中心的语音平台不存在
                msg = "pbxhost_not_exist";
            }
        }

        return msg;
    }


    public List<User> findAllByCallcenterIsTrueAndDatastatusIsFalseAndOrgan(final String organ) {

        final List<String> users = findUserIdsInOrgan(organ);

        if (users == null) return null;

        return userRes.findAllByCallcenterIsTrueAndDatastatusIsFalseAndIdIn(users);
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
     * 增加用户的角色信息
     *
     * @param user
     */
    public void attachRolesMap(final User user) {
        // 获取用户的角色权限，进行授权
        List<RoleAuth> roleAuthList = roleAuthRes.findAll(new Specification<RoleAuth>() {
            @Override
            public Predicate toPredicate(
                    Root<RoleAuth> root, CriteriaQuery<?> query,
                    CriteriaBuilder cb) {
                List<Predicate> criteria = new ArrayList<Predicate>();
                if (user.getRoleList() != null && user.getRoleList().size() > 0) {
                    for (Role role : user.getRoleList()) {
                        criteria.add(cb.equal(root.get("roleid").as(String.class), role.getId()));
                    }
                }
                Predicate[] p = new Predicate[criteria.size()];
                cb.and(cb.equal(root.get("orgi").as(String.class), user.getOrgi()));
                return cb.or(criteria.toArray(p));
            }
        });

        // clear previous auth map values, ensure the changes are token effect in real time.
        user.getRoleAuthMap().clear();
        if (roleAuthList != null) {
            for (RoleAuth roleAuth : roleAuthList) {
                user.getRoleAuthMap().put(roleAuth.getDicvalue(), true);
            }
        }
    }

    /**
     * 获得一个部门及其子部门并添加到User的myorgans中
     *
     * @param user
     */
    public void processAffiliates(final User user, final Organ organ) {
        if (organ == null) {
            return;
        }

        if (user.inAffiliates(organ.getId())) {
            return;
        }

        user.getAffiliates().add(organ.getId());

        // 获得子部门
        List<Organ> y = organRes.findByOrgiAndParent(user.getOrgi(), organ.getId());

        for (Organ x : y) {
            try {
                // 递归调用
                processAffiliates(user, x);
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
        user.setAffiliates(new HashSet<>());

        final HashMap<String, String> skills = new HashMap<>();

        for (final OrganUser organ : organs) {
            // 添加直属部门到organs
            final Organ o = organRes.findOne(organ.getOrgan());
            user.getOrgans().put(organ.getOrgan(), o);
            if (o.isSkill()) {
                skills.put(o.getId(), o.getName());
            }

            // 添加部门及附属部门
            processAffiliates(user, o);
        }

        user.setSkills(skills);
    }
}
