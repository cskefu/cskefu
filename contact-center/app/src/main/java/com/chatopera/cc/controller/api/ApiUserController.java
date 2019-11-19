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
package com.chatopera.cc.controller.api;

import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.controller.api.request.RestUtils;
import com.chatopera.cc.model.AgentStatus;
import com.chatopera.cc.model.OrganUser;
import com.chatopera.cc.model.User;
import com.chatopera.cc.persistence.repository.OrganUserRepository;
import com.chatopera.cc.persistence.repository.UserRepository;
import com.chatopera.cc.proxy.OnlineUserProxy;
import com.chatopera.cc.proxy.UserProxy;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.RestResult;
import com.chatopera.cc.util.RestResultType;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/user")
public class ApiUserController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(ApiUserController.class);

    @Autowired
    private UserRepository userRes;

    @Autowired
    private OrganUserRepository organUserRepository;

    @Autowired
    private UserProxy userProxy;

    @Autowired
    private Cache cache;

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
        if (!StringUtils.isBlank(id)) {
            userList = userRes.findByIdAndOrgi(
                    id, super.getOrgi(request), new PageRequest(super.getP(request), super.getPs(request)));
        } else {
            if (!StringUtils.isBlank(username)) {
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
     * 新增或修改用户用户 ，在修改用户信息的时候，如果用户 密码未改变，请设置为 NULL
     *
     * @param request
     * @param user
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    @Menu(type = "apps", subtype = "user", access = true)
    public ResponseEntity<RestResult> put(HttpServletRequest request, @Valid User user) {
        if (user != null && !StringUtils.isBlank(user.getUsername())) {
            if (!StringUtils.isBlank(user.getPassword())) {
                user.setPassword(MainUtils.md5(user.getPassword()));
                userRes.save(user);
            } else if (!StringUtils.isBlank(user.getId())) {
                User old = userRes.findByIdAndOrgi(user.getId(), super.getOrgi(request));
                user.setPassword(old.getPassword());
                userRes.save(user);
            }
        }
        return new ResponseEntity<>(new RestResult(RestResultType.OK), HttpStatus.OK);
    }

    /**
     * 删除用户，只提供 按照用户ID删除 ， 并且，不能删除系统管理员
     *
     * @param request
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE)
    @Menu(type = "apps", subtype = "user", access = true)
    public ResponseEntity<RestResult> delete(HttpServletRequest request, @Valid String id) {
        RestResult result = new RestResult(RestResultType.OK);
        User user = null;
        if (!StringUtils.isBlank(id)) {
            user = userRes.findByIdAndOrgi(id, super.getOrgi(request));
            if (!user.isSuperadmin()) {    //系统管理员， 不允许 使用 接口删除
                userRes.delete(user);
            } else {
                result.setStatus(RestResultType.USER_DELETE);
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
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
    public ResponseEntity<String> operations(HttpServletRequest request, @RequestBody final String body, @Valid String q) {
        logger.info("[operations] body {}, q {}", body, q);
        final JsonObject j = StringUtils.isBlank(body) ? (new JsonObject()) : (new JsonParser()).parse(
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
                default:
                    json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
                    json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的操作。");
            }
        }

        return new ResponseEntity<String>(json.toString(), headers, HttpStatus.OK);

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
            List<OrganUser> organUsers = organUserRepository.findByOrgan(payload.get("organ").getAsString());
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

        final User previous = userRes.getOne(updated.getId());
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
                previous.setSipaccount(updated.getSipaccount());
                previous.setAgent(updated.isAgent());
                previous.setOrgi(super.getOrgiByTenantshare(request));

                if (StringUtils.isNotBlank(previous.getOrgid())) {
                    previous.setOrgid(previous.getOrgid());
                } else {
                    previous.setOrgid(MainContext.SYSTEM_ORGI);
                }

                previous.setCallcenter(updated.isCallcenter());
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
                OnlineUserProxy.clean(previous.getOrgi());
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
     * 创建新用户
     *
     * @param request
     * @param payload
     * @return
     */
    private JsonObject create(final HttpServletRequest request, final JsonObject payload) {
        logger.info("[create] payload {}", payload.toString());
        final User logined = super.getUser(request);
        JsonObject resp = new JsonObject();

        // 从payload中创建User
        User newUser = null;
        // 创建新用户时，阻止传入ID
        payload.remove("id");
        newUser = userProxy.parseUserFromJson(payload);
        final String msg = userProxy.createNewUser(
                newUser, logined.getOrgi(), logined.getOrgid(), super.getOrgiByTenantshare(request));

        if (StringUtils.isNotBlank(msg)) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
            resp.addProperty(RestUtils.RESP_KEY_DATA, msg);
        } else {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "Unexpected response.");
        }

        return resp;
    }

}
