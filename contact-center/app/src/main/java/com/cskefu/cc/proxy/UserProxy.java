/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd.
 * <https://www.chatopera.com>, Licensed under the Chunsong Public
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2019-2022 Chatopera Inc, All rights reserved.
 * <https://www.chatopera.com>
 */

package com.cskefu.cc.proxy;

import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.exception.BillingQuotaException;
import com.cskefu.cc.model.*;
import com.cskefu.cc.persistence.repository.*;
import com.cskefu.cc.util.restapi.RestUtils;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Predicate;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private UserRoleRepository userRoleRes;

    @Autowired
    private OrganProxy organProxy;

    public void createNewUser(final User user) {
        this.createNewUser(user, null);
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

        if (StringUtils.equalsIgnoreCase(msg, Constants.NEW_USER_SUCCESS)) {
            // 此时 msg 是 new_user_success
            user.setSuperadmin(false); // 不支持创建第二个系统管理员
            try {
                if (StringUtils.isNotBlank(user.getPassword())) {
                    user.setPassword(MainUtils.md5(user.getPassword()));
                }
                userRes.save(user);

                if (organ != null) {
                    OrganUser ou = new OrganUser();
                    ou.setUserid(user.getId());
                    ou.setOrgan(organ.getId());
                    organUserRes.save(ou);
                }
            } catch (Exception e) {
                if (e instanceof UndeclaredThrowableException) {
                    logger.error("[createNewUser] BillingQuotaException", e);
                    if (StringUtils.startsWith(e.getCause().getMessage(), BillingQuotaException.SUFFIX)) {
                        msg = e.getCause().getMessage();
                    }
                } else {
                    logger.error("[createNewUser] err", e);
                }
            }
        }

        // 新账号未通过验证，返回创建失败信息msg
        result.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
        result.addProperty(RestUtils.RESP_KEY_DATA, msg);
        return result;
    }

    public User findOne(final String id) {
        return userRes.findById(id).orElse(null);
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

        if (x.size() == 0)
            return null;

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
        if (x.size() == 0)
            return null;
        Set<String> y = new HashSet<>();
        for (final OrganUser z : x) {
            y.add(z.getUserid());
        }
        return userRes.findAllById(y);
    }

    public Page<User> findUserInOrgans(final Collection<String> organs,
                                       Pageable pageRequest) {
        List<OrganUser> x = organUserRes.findByOrganIn(organs);
        if (x.size() == 0)
            return null;
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

        final User user = userRes.findById(agentno).orElse(null);
        if (user == null)
            return new HashMap<>();

        attachOrgansPropertiesForUser(user);
        return user.getSkills();
    }

    /**
     * 根据用户 username 重置用户的密码
     *
     * @param uname
     * @param password
     * @return boolean
     */
    public boolean resetAccountPasswordByUsername(final String uname, final String password) {
        User user = userRes.findByUsernameAndDatastatus(uname, false);
        if (user != null) {
            user.setPassword(MainUtils.md5(password));
            userRes.save(user);
        } else {
            return false;
        }
        return true;
    }

    /**
     * 获得一个用户的直属组织机构
     *
     * @param userid
     * @return
     */
    public List<String> findOrgansByUserid(final String userid) {
        List<OrganUser> x = organUserRes.findByUserid(userid);

        if (x.size() == 0)
            return null;

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

        if (users == null)
            return null;

        return userRes.findByAgentAndDatastatusAndIdIn(agent, datastatus, users, pageRequest);

    }

    public List<User> findByOrganInAndAgentAndDatastatus(
            final Collection<String> organs,
            boolean agent,
            boolean datastatus) {
        List<String> users = findUserIdsInOrgans(organs);

        if (users == null)
            return null;

        return userRes.findByAgentAndDatastatusAndIdIn(agent, datastatus, users);
    }

    public List<User> findByOrganInAndDatastatus(
            final Collection<String> organs,
            boolean datastatus) {
        List<String> users = findUserIdsInOrgans(organs);

        if (users == null)
            return null;

        return userRes.findByDatastatusAndIdIn(datastatus, users);
    }

    public Page<User> findByOrganInAndDatastatusAndUsernameLike(
            final List<String> organs,
            final boolean datastatus,
            final String username,
            Pageable pageRequest) {
        List<String> users = findUserIdsInOrgans(organs);
        if (users == null)
            return null;
        return userRes.findByDatastatusAndUsernameLikeAndIdIn(datastatus, username, users, pageRequest);
    }

    public List<User> findByOrganAndDatastatus(final String organ, final boolean datastatus) {
        List<String> users = findUserIdsInOrgan(organ);

        if (users == null)
            return null;

        return userRes.findByDatastatusAndIdIn(datastatus, users);

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
            final PbxHost pbxHost = pbxHostRes.findById(user.getPbxhostId()).orElse(null);
            if (pbxHost != null) {
                Extension extension = extensionRes.findById(user.getExtensionId()).orElse(null);
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

        if (users == null)
            return null;

        return userRes.findAllByCallcenterIsTrueAndDatastatusIsFalseAndIdIn(users);
    }

    /**
     * 通过租户ID，是否为坐席，是否有效和组织机构查询坐席数
     *
     * @param agent
     * @param datastatus
     * @param organ
     * @return
     */
    public long countByAgentAndDatastatusAndOrgan(
            final boolean agent,
            final boolean datastatus,
            final String organ) {

        final List<String> users = findUserIdsInOrgan(organ);

        if (users == null)
            return 0;

        return userRes.countByAgentAndDatastatusAndIdIn(agent, datastatus, users);

    }

    public void attachRolesMap(final User user, Organ organ) {

        // 获取用户的角色权限，进行授权
        List<RoleAuth> roleAuthList = roleAuthRes.findAll((root, query, cb) -> {
            List<Predicate> criteria = new ArrayList<>();

            String organId = organ != null ? organ.getId() : null;

            List<UserRole> userRoleList = userRoleRes.findByOrganAndUser(organId, user);
            List<Role> roles = userRoleList.stream().map(ur -> ur.getRole()).collect(Collectors.toList());
            if (roles.size() > 0) {
                for (Role role : roles) {
                    criteria.add(cb.equal(root.get("roleid").as(String.class), role.getId()));
                }
            }
            Predicate[] p = new Predicate[criteria.size()];
            return cb.or(criteria.toArray(p));
        });

        // clear previous auth map values, ensure the changes are token effect in real
        // time.
        user.getRoleAuthMap().clear();
        if (roleAuthList != null) {
            for (RoleAuth roleAuth : roleAuthList) {
                user.getRoleAuthMap().put(roleAuth.getDicvalue(), true);
            }
        }
    }

    /**
     * 增加用户的角色信息
     *
     * @param user
     */
    // public void attachRolesMap(final User user) {
    // // 获取用户的角色权限，进行授权
    // List<RoleAuth> roleAuthList = roleAuthRes.findAll(new
    // Specification<RoleAuth>() {
    // @Override
    // public Predicate toPredicate(
    // Root<RoleAuth> root, CriteriaQuery<?> query,
    // CriteriaBuilder cb) {
    // List<Predicate> criteria = new ArrayList<Predicate>();
    // if (user.getRoleList() != null && user.getRoleList().size() > 0) {
    // for (Role role : user.getRoleList()) {
    // criteria.add(cb.equal(root.get("roleid").as(String.class), role.getId()));
    // }
    // }
    // Predicate[] p = new Predicate[criteria.size()];
    // return cb.or(criteria.toArray(p));
    // }
    // });

    // // clear previous auth map values, ensure the changes are token effect in
    // real
    // // time.
    // user.getRoleAuthMap().clear();
    // if (roleAuthList != null) {
    // for (RoleAuth roleAuth : roleAuthList) {
    // user.getRoleAuthMap().put(roleAuth.getDicvalue(), true);
    // }
    // }
    // }

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
        List<Organ> y = organRes.findByParent(organ.getId());

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
            final Organ o = organRes.findById(organ.getOrgan()).orElse(null);
            user.getOrgans().put(organ.getOrgan(), o);
            if (o.isSkill()) {
                skills.put(o.getId(), o.getName());
            }

            // 添加部门及附属部门
            processAffiliates(user, o);
        }

        user.setSkills(skills);
    }

    /**
     * 为本用户设置当前所在组织机构和其附属组织机构的信息
     *
     * @param user
     * @param currentOrgan
     */
    public void attachCurrentOrgansPropertiesForUser(final User user, final Organ currentOrgan) {
        if (currentOrgan != null) {
            user.setCurrOrganAffiliates(new HashSet<>());
            user.getCurrOrganAffiliates().add(currentOrgan.getId());
            Map<String, Organ> subs = organProxy.findAllOrganByParent(currentOrgan);

            for (Map.Entry<String, Organ> entry : subs.entrySet()) {
                user.getCurrOrganAffiliates().add(entry.getKey());
            }

        } else {
            user.setCurrOrganAffiliates(new HashSet<>());
        }
    }

}
