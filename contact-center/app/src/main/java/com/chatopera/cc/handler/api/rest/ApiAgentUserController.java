/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.handler.api.rest;

import com.chatopera.cc.acd.AutomaticServiceDist;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.handler.Handler;
import com.chatopera.cc.handler.api.request.RestUtils;
import com.chatopera.cc.model.AgentUser;
import com.chatopera.cc.util.Menu;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * ACD服务 获取当前对话中的访客
 */
@RestController
@RequestMapping("/api/agentuser")
public class ApiAgentUserController extends Handler {

    private final static Logger logger = LoggerFactory.getLogger(ApiAgentUserController.class);

    @Autowired
    private Cache cache;

    /**
     * 获取当前对话中的访客
     * 坐席相关 RestAPI
     *
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @Menu(type = "apps", subtype = "agentuser", access = true)
    public ResponseEntity<String> operations(HttpServletRequest request, @RequestBody final String body, @Valid String q) {
        logger.info("[operations] body {}, q {}", body, q);
        final JsonObject j = StringUtils.isBlank(body) ? (new JsonObject()) : (new JsonParser()).parse(body).getAsJsonObject();
        JsonObject json = new JsonObject();
        HttpHeaders headers = RestUtils.header();

        if (!j.has("ops")) {
            json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_1);
            json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的请求参数。");
        } else {
            switch (StringUtils.lowerCase(j.get("ops").getAsString())) {
                case "inserv":
                    json = inserv(request, j);
                    break;
                case "withdraw":
                    json = withdraw(request, j);
                    break;
                default:
                    json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
                    json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的操作。");
            }
        }

        return new ResponseEntity<String>(json.toString(), headers, HttpStatus.OK);
    }

    /**
     * 撤退一个坐席
     * 将当前坐席服务中的访客分配给其他就绪的坐席
     *
     * @param request
     * @param j
     * @return
     */
    private JsonObject withdraw(final HttpServletRequest request, final JsonObject j) {
        JsonObject resp = new JsonObject();
        AutomaticServiceDist.withdrawAgent(super.getOrgi(request), super.getUser(request).getId());
        resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
        return resp;
    }


    /**
     * 获得当前访客服务中的访客信息
     * 获取当前正在对话的访客信息，包含多种渠道来源的访客
     *
     * @param request
     * @param j
     * @return
     */
    private JsonObject inserv(final HttpServletRequest request, final JsonObject j) {
        JsonObject resp = new JsonObject();
        JsonArray data = new JsonArray();

        List<AgentUser> lis = cache.findInservAgentUsersByAgentnoAndOrgi(super.getUser(request).getId(), super.getOrgi(request));
        for (final AgentUser au : lis) {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", au.getId());
            obj.addProperty("userid", au.getUserid());
            obj.addProperty("status", au.getStatus());
            obj.addProperty("agentno", au.getAgentno());
            obj.addProperty("channel", au.getChannel());
            obj.addProperty("nickname", au.getNickname());
            data.add(obj);
        }
        resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
        resp.add("data", data);

        return resp;
    }
}