/*
 * Copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.webim.web.handler.api.rest;

import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.UKTools;
import com.chatopera.cc.util.exception.CallOutRecordException;
import com.chatopera.cc.webim.service.repository.ChatbotRepository;
import com.chatopera.cc.webim.service.repository.SNSAccountRepository;
import com.chatopera.cc.webim.web.handler.Handler;
import com.chatopera.cc.webim.web.handler.api.request.RestUtils;
import com.chatopera.cc.webim.web.model.Chatbot;
import com.chatopera.cc.webim.web.model.User;
import com.chatopera.chatbot.ChatbotAPI;
import com.chatopera.chatbot.ChatbotAPIRuntimeException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

@RestController
@RequestMapping("/api/chatbot")
@Api(value = "聊天机器人", description = "请求聊天机器人服务")
public class ApiChatbotController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(ApiChatbotController.class);
    private final HashSet<String> VALID_LANGS = new HashSet<String>(Arrays.asList(new String[]{"zh_CN", "en_US"}));
    private final HashSet<String> VALID_WORKMODELS = new HashSet<String>(Arrays.asList(new String[]{"客服机器人优先", "人工客服优先"}));
    private final String SNS_TYPE_WEBIM = "webim";

    @Value("${license.client.id}")
    private String clientId;

    @Autowired
    private ChatbotRepository chatbotRes;

    @Autowired
    private SNSAccountRepository snsAccountRes;

    @RequestMapping(method = RequestMethod.POST)
    @Menu(type = "apps", subtype = "chatbot", access = true)
    @ApiOperation("聊天机器人")
    public ResponseEntity<String> operations(HttpServletRequest request, @RequestBody final String body) throws CallOutRecordException {
        final JsonObject j = (new JsonParser()).parse(body).getAsJsonObject();
        logger.info("[chatbot] operations payload {}", j.toString());
        JsonObject json = new JsonObject();
        HttpHeaders headers = RestUtils.header();
        User curruser = super.getUser(request);

        if (!j.has("ops")) {
            json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_1);
            json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的请求参数。");
        } else {
            switch (StringUtils.lowerCase(j.get("ops").getAsString())) {
                case "create":
                    json = create(j, curruser.getId(), curruser.getOrgan(), curruser.getOrgi());
                    break;
                default:
                    json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
                    json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的操作。");
            }
        }
        return new ResponseEntity<String>(json.toString(), headers, HttpStatus.OK);
    }

    /**
     * 使用snsid得到ChatbotID
     *
     * @param snsid
     * @return
     */
    private String resolveChatbotIDWithSnsid(String snsid) {
        return clientId + "_" + snsid;
    }


    /**
     * 使用chatbotID得到snsid
     *
     * @param chatbotID
     * @return
     */
    private String resolveSnsidWithChatbotID(String chatbotID) {
        return StringUtils.remove(chatbotID, clientId + "_");
    }

    /**
     * 创建聊天机器人
     *
     * @param j
     * @param creater
     * @param organ
     * @param orgi
     * @return
     */
    private JsonObject create(JsonObject j, String creater, String organ, String orgi) {
        JsonObject resp = new JsonObject();
        String baseUrl = null;
        String chatbotID = null;
        String name = null;
        String description = null;
        String fallback = null;
        String welcome = null;
        String primaryLanguage = null;
        String snsid = null;
        String workmode = null;

        // 验证数据: 必须字段
        if ((!j.has("baseUrl")) || StringUtils.isBlank(j.get("baseUrl").getAsString())) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的参数，未传入【baseUrl】。");
            return resp;
        } else {
            baseUrl = j.get("baseUrl").getAsString();
        }

        if ((!j.has("name")) || StringUtils.isBlank(j.get("name").getAsString())) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的参数，未传入【name】。");
            return resp;
        } else {
            name = j.get("name").getAsString();
        }

        if (!(j.has("primaryLanguage") && VALID_LANGS.contains(j.get("primaryLanguage").getAsString()))) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的参数，未传入有效【primaryLanguage】。");
            return resp;
        } else {
            primaryLanguage = j.get("primaryLanguage").getAsString();
        }

        if (!(j.has("workmode") && VALID_WORKMODELS.contains(j.get("workmode").getAsString()))) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的参数，未传入有效【workmode】。");
            return resp;
        } else {
            workmode = j.get("workmode").getAsString();
        }

        if ((!j.has("snsid")) || StringUtils.isBlank(j.get("snsid").getAsString())) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的参数，未传入有效【snsid】。");
            return resp;
        } else {
            snsid = j.get("snsid").getAsString();
            // #TODO 仅支持webim
            if (!snsAccountRes.existsBySnsidAndSnstypeAndOrgi(snsid, SNS_TYPE_WEBIM, orgi)) {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的参数，未传入有效【snsid】。");
                return resp;
            }

            if (chatbotRes.existsBySnsAccountIdentifierAndOrgi(snsid, orgi)) {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的参数，该渠道【snsid】已经存在聊天机器人。");
                return resp;
            }
        }

        chatbotID = resolveChatbotIDWithSnsid(snsid);
        if (chatbotRes.existsByChatbotIDAndOrgi(chatbotID, orgi)) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的参数，数据库中存在该聊天机器人。");
            return resp;
        }

        if ((!j.has("fallback")) || StringUtils.isBlank(j.get("fallback").getAsString())) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的参数，未传入【fallback】。");
            return resp;
        } else {
            fallback = j.get("fallback").getAsString();
        }

        // 可选字段
        if (j.has("description"))
            description = j.get("description").getAsString();

        if (j.has("welcome"))
            welcome = j.get("welcome").getAsString();

        try {
            ChatbotAPI capi = new ChatbotAPI(baseUrl);
            JSONObject result = capi.createBot(chatbotID,
                    name,
                    primaryLanguage,
                    fallback,
                    description,
                    welcome);

            if (result.getInt("rc") == 0) {
                // 创建成功
                Chatbot c = new Chatbot();
                c.setId(UKTools.getUUID());
                c.setBaseUrl(capi.getBaseUrl());
                c.setChatbotID(chatbotID);
                c.setDescription(description);
                c.setFallback(fallback);
                c.setPrimaryLanguage(primaryLanguage);
                c.setWelcome(welcome);
                c.setName(name);

                // 默认不开启
                c.setEnabled(false);
                c.setCreater(creater);
                c.setOrgan(organ);
                c.setOrgi(orgi);
                c.setChannel(SNS_TYPE_WEBIM);
                c.setSnsAccountIdentifier(snsid);
                Date dt = new Date();
                c.setCreatetime(dt);
                c.setUpdatetime(dt);
                c.setWorkmode(workmode);

                chatbotRes.save(c);

                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                resp.addProperty(RestUtils.RESP_KEY_DATA, "创建成功。");
                return resp;
            } else {
                // 创建失败
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_6);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "创建失败，失败原因 [" + result.getString("error") + "]");
                return resp;
            }
        } catch (ChatbotAPIRuntimeException e) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_5);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "智能问答引擎服务异常。" + e.toString());
            return resp;
        } catch (MalformedURLException e) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的智能问答引擎服务URL。");
            return resp;
        }
    }

}
