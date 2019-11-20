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
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.controller.api.request.RestUtils;
import com.chatopera.cc.exception.CSKefuException;
import com.chatopera.cc.model.AgentUser;
import com.chatopera.cc.model.Contacts;
import com.chatopera.cc.model.User;
import com.chatopera.cc.persistence.es.ContactsRepository;
import com.chatopera.cc.proxy.AgentUserProxy;
import com.chatopera.cc.proxy.ContactsProxy;
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
import java.util.Optional;

/**
 * 联系人服务
 * 联系人管理功能
 */
@RestController
@RequestMapping("/api/contacts")
public class ApiContactsController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(ApiContactsController.class);

    @Autowired
    private ContactsRepository contactsRepository;

    @Autowired
    private ContactsRepository contactsRes;

    @Autowired
    private ContactsProxy contactsProxy;

    @Autowired
    private AgentUserProxy agentUserProxy;

    /**
     * 返回用户列表，支持分页，分页参数为 p=1&ps=50，默认分页尺寸为 20条每页
     *
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @Menu(type = "apps", subtype = "contacts", access = true)
    public ResponseEntity<RestResult> list(HttpServletRequest request, @Valid String creater, @Valid String q) {
        Page<Contacts> contactsList = null;
        if (!StringUtils.isBlank(creater)) {
            User user = super.getUser(request);
            contactsList = contactsRepository.findByCreaterAndSharesAndOrgi(user.getId(), user.getId(),
                                                                            super.getOrgi(request), false, q,
                                                                            new PageRequest(
                                                                                    super.getP(request),
                                                                                    super.getPs(request)));
        } else {
            contactsList = contactsRepository.findByOrgi(super.getOrgi(request), false, q,
                                                         new PageRequest(super.getP(request), super.getPs(request)));
        }
        return new ResponseEntity<>(new RestResult(RestResultType.OK, contactsList), HttpStatus.OK);
    }

    /**
     * 新增或修改用户用户 ，在修改用户信息的时候，如果用户 密码未改变，请设置为 NULL
     *
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    @Menu(type = "apps", subtype = "contacts", access = true)
    public ResponseEntity<RestResult> put(HttpServletRequest request, @Valid Contacts contacts) {
        if (contacts != null && !StringUtils.isBlank(contacts.getName())) {

            contacts.setOrgi(super.getOrgi(request));
            contacts.setCreater(super.getUser(request).getId());
            contacts.setUsername(super.getUser(request).getUsername());
            contacts.setCreatetime(new Date());
            contacts.setUpdatetime(new Date());

            contactsRepository.save(contacts);
        }
        return new ResponseEntity<>(new RestResult(RestResultType.OK), HttpStatus.OK);
    }

    /**
     * 删除用户，只提供 按照用户ID删除 ， 并且，不能删除系统管理员
     * 删除联系人，联系人删除是逻辑删除，将 datastatus字段标记为 true，即已删除
     *
     * @param request
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE)
    @Menu(type = "apps", subtype = "contacts", access = true)
    public ResponseEntity<RestResult> delete(HttpServletRequest request, @Valid String id) {
        RestResult result = new RestResult(RestResultType.OK);
        if (!StringUtils.isBlank(id)) {
            Contacts contacts = contactsRepository.findOne(id);
            if (contacts != null) {    //系统管理员， 不允许 使用 接口删除
                contacts.setDatastatus(true);
                contactsRepository.save(contacts);
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 联系人页面，客户点击页面时，判断是否有能触达的通道
     *
     * @param request
     * @param body
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @Menu(type = "apps", subtype = "contacts", access = true)
    public ResponseEntity<String> operations(
            final HttpServletRequest request,
            @RequestBody final String body) {
        final JsonObject j = (new JsonParser()).parse(body).getAsJsonObject();
        logger.info("[chatbot] operations payload {}", j.toString());
        JsonObject json = new JsonObject();
        HttpHeaders headers = RestUtils.header();
        final User logined = super.getUser(request);
        final String orgi = logined.getOrgi();

        if (!j.has("ops")) {
            json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_1);
            json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的请求参数。");
        } else {
            switch (StringUtils.lowerCase(j.get("ops").getAsString())) {
                case "approach":
                    // 查找立即触达的渠道
                    json = approach(j, logined);
                    break;
                case "proactive":
                    // 与联系开始聊天
                    json = proactive(j, logined);
                    break;
                default:
                    json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
                    json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的操作。");
            }
        }

        return new ResponseEntity<String>(json.toString(), headers, HttpStatus.OK);
    }

    /**
     * 主动与联系人聊天
     *
     * @param payload
     * @param logined
     * @return
     */
    private JsonObject proactive(final JsonObject payload, User logined) {
        JsonObject resp = new JsonObject();

        final String channels = payload.has("channels") ? payload.get("channels").getAsString() : null;
        final String contactid = payload.has("contactid") ? payload.get("contactid").getAsString() : null;

        if (StringUtils.isBlank(channels) || StringUtils.isBlank(contactid)) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "Invalid params.");
            return resp;
        }

        try {
            AgentUser agentUser = agentUserProxy.figureAgentUserBeforeChatWithContactInfo(channels, contactid, logined);
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
            JsonObject data = new JsonObject();
            data.addProperty("agentuserid", agentUser.getId());
            resp.add(RestUtils.RESP_KEY_DATA, data);
        } catch (CSKefuException e) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "Can not create agent user.");
            return resp;
        }

        return resp;
    }

    /**
     * 根据联系人信息查找立即触达的渠道
     *
     * @param payload
     * @param logined
     * @return
     */
    private JsonObject approach(final JsonObject payload, final User logined) {
        JsonObject resp = new JsonObject();

        if (!payload.has("contactsid")) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_1);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "Invalid params.");
            return resp;
        }

        final String contactsid = payload.get("contactsid").getAsString();
        Optional<Contacts> contactOpt = contactsRes.findOneById(contactsid).filter(
                p -> !p.isDatastatus());

        if (contactOpt.isPresent()) {
            List<MainContext.ChannelType> channles;
            try {
                channles = contactsProxy.liveApproachChannelsByContactid(
                        logined, contactsid, contactsProxy.isSkypeSetup(logined.getOrgi()));
                if (channles.size() > 0) {
                    resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                    JsonArray data = new JsonArray();
                    for (final MainContext.ChannelType e : channles) {
                        data.add(e.toString());
                    }
                    resp.add(RestUtils.RESP_KEY_DATA, data);
                } else {
                    resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
                    resp.addProperty(RestUtils.RESP_KEY_ERROR, "No available channel to approach contact.");
                }
            } catch (CSKefuException e) {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "Contact not found.");
            }
        } else {
            // can not find contact, may is deleted.
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "Not found contact.");
        }

        return resp;
    }
}