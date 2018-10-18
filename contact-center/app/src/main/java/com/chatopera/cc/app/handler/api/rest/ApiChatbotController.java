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
package com.chatopera.cc.app.handler.api.rest;

import com.chatopera.cc.util.Menu;
import com.chatopera.cc.app.basic.MainUtils;
import com.chatopera.cc.exception.CallOutRecordException;
import com.chatopera.cc.app.model.*;
import com.chatopera.cc.app.persistence.repository.*;
import com.chatopera.cc.util.OnlineUserUtils;
import com.chatopera.cc.app.im.util.ChatbotUtils;
import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.handler.api.request.RestUtils;
import com.chatopera.chatbot.ChatbotAPI;
import com.chatopera.chatbot.ChatbotAPIRuntimeException;
import com.google.gson.JsonArray;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/api/chatbot")
@Api(value = "聊天机器人", description = "请求聊天机器人服务")
public class ApiChatbotController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(ApiChatbotController.class);

    @Value("${license.client.id}")
    private String clientId;

    @Autowired
    private ChatbotRepository chatbotRes;

    @Autowired
    private SNSAccountRepository snsAccountRes;

    @Autowired
    private UserRepository userRes;

    @Autowired
    private OrganRepository organRes;

    @Autowired
    private ConsultInviteRepository consultInviteRes;

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
                case "delete":
                    json = delete(j, curruser.getId(), curruser.getOrgan(), curruser.getOrgi());
                    break;
                case "fetch":
                    json = fetch(j, curruser.getId(), curruser.isSuperuser(), curruser.getMyorgans(), curruser.getOrgi(), super.getP(request), super.getPs(request));
                    break;
                case "update":
                    json = update(j);
                    break;
                case "enable":
                    json = enable(j, true);
                    break;
                case "disable":
                    json = enable(j, false);
                    break;
                case "vacant":
                    json = vacant(j, curruser.getOrgi(), curruser.isSuperuser(), curruser.getMyorgans());
                    break;
                default:
                    json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
                    json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的操作。");
            }
        }
        return new ResponseEntity<String>(json.toString(), headers, HttpStatus.OK);
    }

    /**
     * 获取空缺聊天机器人的网站渠道列表
     *
     * @param j
     * @param orgi
     * @param myorgans
     * @return
     */
    private JsonObject vacant(final JsonObject j, String orgi, boolean isSuperuser, final HashSet<String> myorgans) {
        JsonObject resp = new JsonObject();
        if ((!isSuperuser) && (myorgans == null || myorgans.size() == 0)) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "您还未属于任何【部门】，不具有访问该资源的权限。");
            return resp;
        }

        List<SNSAccount> records = snsAccountRes.findBySnstypeAndOrgiAndOrgans(ChatbotUtils.SNS_TYPE_WEBIM, orgi, myorgans != null ? new ArrayList<String>(myorgans) : null);
        JsonArray ja = new JsonArray();

        for (SNSAccount r : records) {
            if (!chatbotRes.existsBySnsAccountIdentifierAndOrgi(r.getSnsid(), orgi)) {
                JsonObject o = new JsonObject();
                o.addProperty("id", r.getId());
                o.addProperty("snsid", r.getSnsid());
                o.addProperty("snsType", r.getSnstype());
                o.addProperty("snsurl", r.getBaseURL());
                ja.add(o);
            }
        }

        resp.add("data", ja);
        resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
        return resp;
    }

    /**
     * Enable Chatbot
     *
     * @param j
     * @return
     */
    private JsonObject enable(JsonObject j, boolean isEnabled) {
        JsonObject resp = new JsonObject();
        if ((!j.has("id")) || StringUtils.isBlank(j.get("id").getAsString())) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的操作，id不能为空。");
            return resp;
        }

        final String id = j.get("id").getAsString();
        Chatbot c = chatbotRes.findOne(id);

        if (c == null) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "该聊天机器人不存在。");
            return resp;
        }

        try {
            if (c.getApi().exists(c.getChatbotID())) {
                c.setEnabled(isEnabled);
                chatbotRes.save(c);

                // 更新访客网站配置
                CousultInvite invite = OnlineUserUtils.cousult(c.getSnsAccountIdentifier(), c.getOrgi(), consultInviteRes);
                invite.setAi(isEnabled);
                consultInviteRes.save(invite);
                OnlineUserUtils.cacheCousult(invite);

                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                resp.addProperty(RestUtils.RESP_KEY_DATA, "完成。");
            } else {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_7);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "智能问答引擎不存在该聊天机器人，未能正确设置。");
            }
        } catch (ChatbotAPIRuntimeException e) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_5);
            resp.addProperty(RestUtils.RESP_KEY_DATA, "设置不成功，智能问答引擎服务异常。");
        } catch (MalformedURLException e) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_6);
            resp.addProperty(RestUtils.RESP_KEY_DATA, "设置不成功，智能问答引擎地址不合法。");
        }
        return resp;
    }

    /**
     * 更新聊天机器人
     *
     * @param j
     * @return
     */
    private JsonObject update(JsonObject j) {
        JsonObject resp = new JsonObject();
        if (!j.has("id")) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "非法参数，id不能为空。");
            return resp;
        }
        final String id = j.get("id").getAsString();

        Chatbot c = chatbotRes.findOne(id);

        if (c == null) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "该聊天机器人不存在。");
            return resp;
        }

        // 更新访客网站配置
        CousultInvite invite = OnlineUserUtils.cousult(c.getSnsAccountIdentifier(), c.getOrgi(), consultInviteRes);

        if (j.has("workmode") && ChatbotUtils.VALID_WORKMODELS.contains(j.get("workmode").getAsString())) {
            c.setWorkmode(j.get("workmode").getAsString());
            invite.setAifirst(StringUtils.equals(ChatbotUtils.CHATBOT_FIRST, c.getWorkmode()));
        }

        String description = j.has("description") ? j.get("description").getAsString() : null;
        String fallback = j.has("fallback") ? j.get("fallback").getAsString() : null;
        String welcome = j.has("welcome") ? j.get("welcome").getAsString() : null;
        String name = j.has("name") ? j.get("name").getAsString() : null;


        if (j.has("enabled")) {
            boolean enabled = j.get("enabled").getAsBoolean();
            c.setEnabled(enabled);
            invite.setAi(enabled);
        }

        if (StringUtils.isNotBlank(description) ||
                StringUtils.isNotBlank(fallback) ||
                StringUtils.isNotBlank(welcome)) {
            try {
                if (c.getApi().updateByChatbotID(c.getChatbotID(), name, description, fallback, welcome)) {
                    resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                    resp.addProperty(RestUtils.RESP_KEY_DATA, "更新成功。");
                } else {
                    resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_5);
                    resp.addProperty(RestUtils.RESP_KEY_ERROR, "更新失败。");
                    return resp;
                }
            } catch (ChatbotAPIRuntimeException e) {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_5);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "更新智能问答引擎失败。" + e.toString());
                return resp;
            } catch (MalformedURLException e) {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_6);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "更新智能问答引擎失败。" + e.toString());
                return resp;
            }
        }

        if (StringUtils.isNotBlank(description))
            c.setDescription(description);

        if (StringUtils.isNotBlank(fallback))
            c.setFallback(fallback);

        if (StringUtils.isNotBlank(welcome)) {
            c.setWelcome(welcome);
            invite.setAisuccesstip(welcome);
        }

        if (StringUtils.isNotBlank(name)) {
            c.setName(name);
            invite.setAiname(name);
        }

        c.setUpdatetime(new Date());
        chatbotRes.save(c);
        consultInviteRes.save(invite);
        OnlineUserUtils.cacheCousult(invite);

        return resp;
    }

    /**
     * 获取聊天机器人列表
     *
     * @param j
     * @param id
     * @param myorgans
     * @param orgi
     * @param p
     * @param ps
     * @return
     */
    private JsonObject fetch(JsonObject j, String id, boolean isSuperuser, HashSet<String> myorgans, String orgi, int p, int ps) {
        JsonObject resp = new JsonObject();
        if (isSuperuser) {
            myorgans = null;
        } else if (myorgans.size() == 0) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "当前登录用户未分配到任何部门并且不是管理员，无权访问机器人客服资源。");
            return resp;
        }

        Page<Chatbot> records = chatbotRes.findByOrgans(myorgans != null ? new ArrayList<String>(myorgans) : null, new PageRequest(p, ps, Sort.Direction.DESC, new String[]{"createtime"}));

        JsonArray ja = new JsonArray();
        for (Chatbot c : records) {
            JsonObject o = new JsonObject();
            o.addProperty("id", c.getId());
            o.addProperty("name", c.getName());
            o.addProperty("primaryLanguage", c.getPrimaryLanguage());
            o.addProperty("description", c.getDescription());
            o.addProperty("fallback", c.getFallback());
            o.addProperty("welcome", c.getWelcome());
            o.addProperty("workmode", c.getWorkmode());
            o.addProperty("channel", c.getChannel());
            o.addProperty("snsid", c.getSnsAccountIdentifier());
            o.addProperty("enabled", c.isEnabled());

            // SNSAccount
            SNSAccount snsAccount = snsAccountRes.findBySnsidAndOrgi(c.getSnsAccountIdentifier(), orgi);
            if(snsAccount == null){
                chatbotRes.delete(c); // 删除不存在snsAccount的机器人
                continue; // 忽略不存在snsAccount的机器人
            }

            o.addProperty("snsurl", snsAccount.getBaseURL());

            // 创建人
            User user = userRes.findById(c.getCreater());
            if (user != null) {
                o.addProperty("creater", c.getCreater());
                o.addProperty("creatername", user.getUname());
            }

            // 部门
            Organ g = organRes.findOne(c.getOrgan());
            if (g != null) {
                o.addProperty("organ", c.getOrgan());
                o.addProperty("organname", g.getName());
            }
            ja.add(o);
        }

        resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
        resp.add("data", ja);
        resp.addProperty("size", records.getSize()); // 每页条数
        resp.addProperty("number", records.getNumber()); // 当前页
        resp.addProperty("totalPage", records.getTotalPages()); // 所有页
        resp.addProperty("totalElements", records.getTotalElements()); // 所有检索结果数量

        return resp;
    }

    /**
     * 删除聊天机器人
     *
     * @param j
     * @param uid
     * @param organ
     * @param orgi
     * @return
     */
    private JsonObject delete(JsonObject j, String uid, String organ, String orgi) {
        JsonObject resp = new JsonObject();
        if ((!j.has("id")) || StringUtils.isBlank(j.get("id").getAsString())) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的参数，未传入id。");
            return resp;
        }
        final String id = j.get("id").getAsString();

        Chatbot c = chatbotRes.findOne(id);
        if (c == null) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的参数，不存在该聊天机器人。");
            return resp;
        }

        try {
            if (c.getApi().deleteByChatbotID(c.getChatbotID())) {
                // 更新访客网站配置
                CousultInvite invite = OnlineUserUtils.cousult(c.getSnsAccountIdentifier(), c.getOrgi(), consultInviteRes);
                if (invite != null) {
                    invite.setAi(false);
                    invite.setAiname(null);
                    invite.setAisuccesstip(null);
                    invite.setAifirst(false);
                    invite.setAiid(null);
                    consultInviteRes.save(invite);
                    OnlineUserUtils.cacheCousult(invite);
                }
                chatbotRes.delete(c);
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                resp.addProperty(RestUtils.RESP_KEY_DATA, "删除成功。");
                return resp;
            } else {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_6);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "未成功删除该聊天机器人。");
                return resp;
            }
        } catch (ChatbotAPIRuntimeException e) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_5);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "该聊天机器人服务请求异常。" + e.toString());
            return resp;
        } catch (MalformedURLException e) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "该聊天机器人地址错误。");
            return resp;
        }
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

        if (!(j.has("primaryLanguage") && ChatbotUtils.VALID_LANGS.contains(j.get("primaryLanguage").getAsString()))) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的参数，未传入有效【primaryLanguage】。");
            return resp;
        } else {
            primaryLanguage = j.get("primaryLanguage").getAsString();
        }

        if (!(j.has("workmode") && ChatbotUtils.VALID_WORKMODELS.contains(j.get("workmode").getAsString()))) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的参数，未传入有效【workmode】。");
            return resp;
        } else {
            workmode = j.get("workmode").getAsString();
        }

        if ((!j.has("snsid")) || StringUtils.isBlank(j.get("snsid").getAsString())) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的参数，未传入【snsid】。");
            return resp;
        } else {
            snsid = j.get("snsid").getAsString();
            // #TODO 仅支持webim
            if (!snsAccountRes.existsBySnsidAndSnstypeAndOrgi(snsid, ChatbotUtils.SNS_TYPE_WEBIM, orgi)) {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的参数，不存在【snsid】对应的网站渠道。");
                return resp;
            }

            if (chatbotRes.existsBySnsAccountIdentifierAndOrgi(snsid, orgi)) {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的参数，该渠道【snsid】已经存在聊天机器人。");
                return resp;
            }
        }

        chatbotID = ChatbotUtils.resolveChatbotIDWithSnsid(snsid, clientId);
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
                c.setId(MainUtils.getUUID());
                c.setBaseUrl(capi.getBaseUrl());
                c.setChatbotID(chatbotID);
                c.setDescription(description);
                c.setFallback(fallback);
                c.setPrimaryLanguage(primaryLanguage);
                c.setName(name);
                c.setWelcome(result.getJSONObject("data").getString("welcome"));
                c.setCreater(creater);
                c.setOrgan(organ);
                c.setOrgi(orgi);
                c.setChannel(ChatbotUtils.SNS_TYPE_WEBIM);
                c.setSnsAccountIdentifier(snsid);
                Date dt = new Date();
                c.setCreatetime(dt);
                c.setUpdatetime(dt);
                c.setWorkmode(workmode);

                // 默认不开启
                boolean enabled = false;
                c.setEnabled(enabled);

                // 更新访客网站配置
                CousultInvite invite = OnlineUserUtils.cousult(c.getSnsAccountIdentifier(), c.getOrgi(), consultInviteRes);
                invite.setAi(enabled);
                invite.setAifirst(StringUtils.equals(ChatbotUtils.CHATBOT_FIRST, workmode));
                invite.setAiid(c.getId());
                invite.setAiname(c.getName());
                invite.setAisuccesstip(c.getWelcome());
                consultInviteRes.save(invite);
                OnlineUserUtils.cacheCousult(invite);
                chatbotRes.save(c);

                JsonObject data = new JsonObject();
                data.addProperty("id", c.getId());
                resp.add(RestUtils.RESP_KEY_DATA, data);
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                return resp;
            } else {
                // 创建失败
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_6);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "创建失败，失败原因 [" + result.getString("error") + "]");
                return resp;
            }
        } catch (ChatbotAPIRuntimeException e) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_5);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "智能问答引擎服务异常，该机器人【chatbotID】已经存在或服务不能访问到，请联系 [info@chatopera.com] 获得支持。");
            return resp;
        } catch (MalformedURLException e) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的智能问答引擎服务URL。");
            return resp;
        }
    }

}
