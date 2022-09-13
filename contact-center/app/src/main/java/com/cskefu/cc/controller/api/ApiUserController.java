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
package com.cskefu.cc.controller.api;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.controller.api.request.RestUtils;
import com.cskefu.cc.model.AgentStatus;
import com.cskefu.cc.model.Organ;
import com.cskefu.cc.model.OrganUser;
import com.cskefu.cc.model.Role;
import com.cskefu.cc.model.User;
import com.cskefu.cc.model.UserRole;
import com.cskefu.cc.persistence.repository.OrganRepository;
import com.cskefu.cc.persistence.repository.OrganUserRepository;
import com.cskefu.cc.persistence.repository.RoleRepository;
import com.cskefu.cc.persistence.repository.UserRepository;
import com.cskefu.cc.persistence.repository.UserRoleRepository;
import com.cskefu.cc.proxy.UserProxy;
import com.cskefu.cc.util.Menu;
import com.cskefu.cc.util.RestResult;
import com.cskefu.cc.util.RestResultType;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class ApiUserController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(ApiUserController.class);

    @Autowired
    private Cache cache;

    @Autowired
    private UserProxy userProxy;

    @Autowired
    private UserRepository userRes;

    @Autowired
    private OrganUserRepository organUserRes;

    @Autowired
    private OrganRepository organRes;

    @Autowired
    private RoleRepository roleRes;

    @Autowired
    private UserRoleRepository userRoleRes;

    /**
     * 返回用户列表，支持分页，分页参数为 p=1&ps=50，默认分页尺寸为 20条每页
     *
     * @param request
     * @param username 搜索用户名，精确搜索
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @Menu(type = "apps", subtype = "user", access = true)
    public ResponseEntity<RestResult> list(HttpServletRequest request, @Valid String id, @Valid String username) {
        Page<User> userList = null;
        if (StringUtils.isNotBlank(id)) {
            userList = userRes.findByIdAndOrgi(
                    id, super.getOrgi(request), new PageRequest(super.getP(request), super.getPs(request)));
        } else {
            if (StringUtils.isNotBlank(username)) {
                userList = userRes.findByDatastatusAndOrgiAndUsernameLike(
                        false, super.getOrgi(request), username, new PageRequest(
                                super.getP(request),
                                super.getPs(request)));
            } else {
                userList = userRes.findByDatastatusAndOrgi(
                        false, super.getOrgi(request), new PageRequest(super.getP(request), super.getPs(request)));
            }
        }
        return new ResponseEntity<>(new RestResult(RestResultType.OK, userList), HttpStatus.OK);
    }

    /**
     * 用户管理
     *
     * @param request
     * @param body
     * @param q
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @Menu(type = "apps", subtype = "user", access = true)
    public ResponseEntity<String> operations(HttpServletRequest request, @RequestBody final String body,
            @Valid String q) {
        logger.info("[operations] body {}, q {}", body, q);
        final JsonObject j = StringUtils.isBlank(body) ? (new JsonObject())
                : (new JsonParser()).parse(
                        body).getAsJsonObject();
        JsonObject json = new JsonObject();
        HttpHeaders headers = RestUtils.header();

        if (!j.has("ops")) {
            json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_1);
            json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的请求参数。");
        } else {
            switch (StringUtils.lowerCase(j.get("ops").getAsString())) {
                case "create":
                    json = create(request, j);
                    break;
                case "update":
                    json = update(request, j);
                    break;
                case "findbyorgan":
                    json = findByOrgan(j);
                    break;
                case "delete":
                    json = delete(request, j);
                    break;
                default:
                    json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
                    json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的操作。");
            }
        }

        return new ResponseEntity<String>(json.toString(), headers, HttpStatus.OK);
    }

    /**
     * 创建新用户
     *
     * @param request
     * @param payload
     * @return
     */
    private JsonObject create(final HttpServletRequest request, final JsonObject payload) {
        logger.info("[create] payload {}", payload.toString());
        String parent = payload.get("parent").getAsString();
        Organ parentOrgan = super.getOrgan(request);
        if (StringUtils.isNotEmpty(parent)) {
            parentOrgan = organRes.getOne(parent);
        }

        String roleId = payload.get("role").getAsString();

        // 创建新用户时，阻止传入ID
        payload.remove("id");
        // 从payload中创建User
        User user = userProxy.parseUserFromJson(payload);
        JsonObject resp = userProxy.createNewUser(user, parentOrgan);

        if (StringUtils.isNotEmpty(roleId)) {
            Role role = roleRes.getOne(roleId);
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(role);
            userRole.setOrgi(Constants.SYSTEM_ORGI);
            userRole.setCreater(super.getUser(request).getId());
            userRole.setOrgan(parentOrgan.getId());
            userRoleRes.save(userRole);
        }

        logger.info("[create] response {}", resp.toString());
        return resp;
    }

    /**
     * 更新用户信息
     *
     * @param request
     * @param payload
     * @return
     */
    private JsonObject update(final HttpServletRequest request, final JsonObject payload) {
        logger.info("[update] payload {}", payload.toString());
        JsonObject resp = new JsonObject();
        final User updated = userProxy.parseUserFromJson(payload);
        if (StringUtils.isBlank(updated.getId())) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的参数。");
            return resp;
        }

        final User previous = userRes.findById(updated.getId());
        if (previous != null) {
            String msg = userProxy.validUserUpdate(updated, previous);
            if (StringUtils.equals(msg, "edit_user_success")) {

                // 由坐席切换成非坐席 判断是否坐席 以及 是否有对话
                if (!updated.isAgent()) {
                    AgentStatus agentStatus = cache.findOneAgentStatusByAgentnoAndOrig(
                            previous.getId(), previous.getOrgi());
                    if (agentStatus != null && agentStatus.getUsers() > 0) {
                        resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                        resp.addProperty(RestUtils.RESP_KEY_DATA, "t1");
                        return resp;
                    }

                    // TODO 检查该用户是否在其它技能组
                    // https://gitlab.chatopera.com/chatopera/cosinee/issues/751
                    // 如果在其它技能组，禁止修改，返回提示："该用户在其它技能组中，不支持取消坐席。取消坐席设置前需要不包括在任何技能组中。"
                }

                // 通过验证，可以更新数据库
                previous.setUname(updated.getUname());
                previous.setUsername(updated.getUsername());
                previous.setEmail(updated.getEmail());
                previous.setMobile(updated.getMobile());
                previous.setAgent(updated.isAgent());

                if (MainContext.hasModule(Constants.CSKEFU_MODULE_CALLCENTER)) {
                    previous.setCallcenter(updated.isCallcenter());
                    if (updated.isCallcenter()) {
                        previous.setExtensionId(updated.getExtensionId());
                        previous.setPbxhostId(updated.getPbxhostId());
                    }
                }

                if (StringUtils.isNotBlank(updated.getPassword())) {
                    previous.setPassword(MainUtils.md5(updated.getPassword()));
                }

                final Date now = new Date();
                if (previous.getCreatetime() == null) {
                    previous.setCreatetime(now);
                }
                previous.setUpdatetime(now);
                previous.setAdmin(updated.isAdmin());
                previous.setSuperadmin(false);
                userRes.save(previous);
            }

            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
            resp.addProperty(RestUtils.RESP_KEY_DATA, msg);

        } else {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "Previous user not exist.");
        }

        return resp;
    }

    /**
     * 根据组织查找用户
     *
     * @param payload
     * @return
     */
    private JsonObject findByOrgan(final JsonObject payload) {
        final JsonObject resp = new JsonObject();
        if (payload.has("organ")) {
            List<OrganUser> organUsers = organUserRes.findByOrgan(payload.get("organ").getAsString());
            List<String> userids = organUsers.stream().map(p -> p.getUserid()).collect(Collectors.toList());
            List<User> users = userRes.findAll(userids);

            JsonArray data = new JsonArray();
            users.stream().forEach(u -> {
                JsonObject obj = new JsonObject();
                obj.addProperty("id", u.getId());
                obj.addProperty("uname", u.getUname());
                data.add(obj);
            });

            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
            resp.add(RestUtils.RESP_KEY_DATA, data);
        } else {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "Invalid params.");
        }

        return resp;
    }

    /**
     * 删除账号
     *
     * @param request
     * @param payload
     * @return
     */
    private JsonObject delete(final HttpServletRequest request, final JsonObject payload) {
        logger.info("[create] payload {}", payload.toString());
        JsonObject resp = new JsonObject();
        if (payload.has("id")) {
            String id = payload.get("id").getAsString();
            if (StringUtils.isNotBlank(id)) {
                User user = userRes.findById(id);
                if (user == null) {
                    // 用户不存在
                    resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                    resp.addProperty(RestUtils.RESP_KEY_MSG, "done");
                    return resp;
                }

                // 系统管理员， 不允许 使用 接口删除
                if (!user.isSuperadmin()) {
                    // 删除用户的时候，同时删除用户对应的权限
                    List<UserRole> userRoles = userRoleRes.findByOrgiAndUser(user.getOrgi(), user);
                    userRoleRes.delete(userRoles);
                    // 删除用户对应的组织机构关系
                    List<OrganUser> organUsers = organUserRes.findByUserid(id);
                    organUserRes.delete(organUsers);
                    // 删除用户
                    userRes.delete(user);
                    resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                    resp.addProperty(RestUtils.RESP_KEY_MSG, "done");
                } else {
                    resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
                    resp.addProperty(RestUtils.RESP_KEY_ERROR, "USER_DELETE");
                }
            }
        } else {
            // 参数不合法
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "INVALID_PARAMS");
        }

        return resp;
    }
}
