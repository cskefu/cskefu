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
package com.chatopera.cc.handler.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.handler.Handler;
import com.chatopera.cc.handler.api.request.RestUtils;
import com.chatopera.cc.model.OrganUser;
import com.chatopera.cc.model.User;
import com.chatopera.cc.persistence.repository.OrganUserRepository;
import com.chatopera.cc.persistence.repository.UserRepository;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.RestResult;
import com.chatopera.cc.util.RestResultType;
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
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/user")
public class ApiUserController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(ApiUserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganUserRepository organUserRepository;

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
            userList = userRepository.findByIdAndOrgi(
                    id, super.getOrgi(request), new PageRequest(super.getP(request), super.getPs(request)));
        } else {
            if (!StringUtils.isBlank(username)) {
                userList = userRepository.findByDatastatusAndOrgiAndUsernameLike(
                        false, super.getOrgi(request), username, new PageRequest(
                                super.getP(request),
                                super.getPs(request)));
            } else {
                userList = userRepository.findByDatastatusAndOrgi(
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
                userRepository.save(user);
            } else if (!StringUtils.isBlank(user.getId())) {
                User old = userRepository.findByIdAndOrgi(user.getId(), super.getOrgi(request));
                user.setPassword(old.getPassword());
                userRepository.save(user);
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
            user = userRepository.findByIdAndOrgi(id, super.getOrgi(request));
            if (!user.isSuperadmin()) {    //系统管理员， 不允许 使用 接口删除
                userRepository.delete(user);
            } else {
                result.setStatus(RestResultType.USER_DELETE);
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(path = "/findByOrgan", method = RequestMethod.POST)
    @Menu(type = "apps", subtype = "user", access = true)
    public ResponseEntity<RestResult> findByOrgan(HttpServletRequest request, @Valid String organ) {
        List<OrganUser> organUsers = organUserRepository.findByOrgan(organ);
        List<String> userids = organUsers.stream().map(p -> p.getUserid()).collect(Collectors.toList());
        List<User> users = userRepository.findAll(userids);
        JSONArray json = new JSONArray();
        users.stream().forEach(u -> {
            JSONObject obj = new JSONObject();
            obj.put("id", u.getId());
            obj.put("uname", u.getUname());
            json.add(obj);
        });

        return new ResponseEntity<>(new RestResult(RestResultType.OK, json), HttpStatus.OK);
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
        JsonObject resp = new JsonObject();

        // 从payload中创建User


//        if (payload) {
//            return resp;
//        }

        return resp;
    }

}
