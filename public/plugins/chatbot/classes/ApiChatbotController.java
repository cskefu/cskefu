/*
 * Copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.plugins.chatbot;

import com.chatopera.bot.exception.ChatbotException;
import com.chatopera.bot.sdk.Response;
import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.controller.api.request.RestUtils;
import com.chatopera.cc.model.*;
import com.chatopera.cc.persistence.repository.*;
import com.chatopera.cc.proxy.OnlineUserProxy;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.SystemEnvHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Date;
import java.util.List;


/**
 * 聊天机器人
 * 请求聊天机器人服务
 */
@RestController
@RequestMapping("/api/chatbot")
public class ApiChatbotController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(ApiChatbotController.class);

    @Autowired
    private ChatbotRepository chatbotRes;

    @Autowired
    private SNSAccountRepository snsAccountRes;

    @Autowired
    private UserRepository userRes;

    @Autowired
    private FbMessengerRepository fbMessengerRepository;

    @Autowired
    private ConsultInviteRepository consultInviteRes;

    private final static String botServiceProvider = SystemEnvHelper.getenv(
            ChatbotConstants.BOT_PROVIDER, ChatbotConstants.DEFAULT_BOT_PROVIDER);

    /**
     * 聊天机器人
     *
     * @param request
     * @param body
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.POST)
    @Menu(type = "apps", subtype = "chatbot", access = true)
    public ResponseEntity<String> operations(HttpServletRequest request, @RequestBody final String body) throws Exception {
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
                case "create":
                    // TODO 支持将一个用户添加到多个部门后，此处需要在Payload中传入部门ID ,即 organ
                    // 2019-10-10 Wang Hai Liang
                    json = create(j, logined.getId(), logined.getOrgi());
                    break;
                case "delete":
                    json = delete(j, logined.getId(), logined.getOrgi());
                    break;
                case "fetch":
                    json = fetch(
                            j, logined.getId(), logined.isAdmin(), orgi, super.getP(request), super.getPs(request));
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
                case "enableaisuggest":
                    json = enableAiSuggest(j, true);
                    break;
                case "disableaisuggest":
                    json = enableAiSuggest(j, false);
                    break;
                case "vacant":
                    json = vacant(j, orgi, logined.isAdmin());
                    break;
                case "faq":
                    json = faq(j, orgi);
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
     * @return
     */
    private JsonObject vacant(final JsonObject j, String orgi, boolean isSuperuser) {
        JsonObject resp = new JsonObject();
        if (!isSuperuser) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "您不具有访问该资源的权限。");
            return resp;
        }

        List<SNSAccount> records = snsAccountRes.findBySnstypeAndOrgi(j.get("snstype").getAsString(), orgi);
        JsonArray ja = new JsonArray();

        for (SNSAccount r : records) {
            if (!chatbotRes.existsBySnsAccountIdentifierAndOrgi(r.getSnsid(), orgi)) {
                JsonObject o = new JsonObject();
                o.addProperty("id", r.getId());
                o.addProperty("snsid", r.getSnsid());
                o.addProperty("snsType", r.getSnstype());
                o.addProperty("snsurl", isWebIMChannelBySnsType(j) ? r.getBaseURL() : r.getName());
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
            com.chatopera.bot.sdk.Chatbot bot = new com.chatopera.bot.sdk.Chatbot(
                    c.getClientId(), c.getSecret(), botServiceProvider);
            if (bot.exists()) {
                c.setEnabled(isEnabled);
                chatbotRes.save(c);

                if (c.getChannel().equals(Constants.CHANNEL_TYPE_WEBIM)) {
                    // 更新访客网站配置
                    CousultInvite invite = OnlineUserProxy.consult(c.getSnsAccountIdentifier(), c.getOrgi());
                    invite.setAi(isEnabled);
                    consultInviteRes.save(invite);
                    OnlineUserProxy.cacheConsult(invite);
                } else if (c.getChannel().equals(Constants.CHANNEL_TYPE_MESSENGER)) {
                    FbMessenger fbMessenger = fbMessengerRepository.findOneByPageId(c.getSnsAccountIdentifier());
                    fbMessenger.setAi(isEnabled);
                    fbMessengerRepository.save(fbMessenger);
                }


                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                resp.addProperty(RestUtils.RESP_KEY_DATA, "完成。");
            } else {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_7);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "智能问答引擎不存在该聊天机器人，未能正确设置。");
            }
        } catch (MalformedURLException e) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_6);
            resp.addProperty(RestUtils.RESP_KEY_DATA, "设置不成功，智能问答引擎地址不合法。");
        } catch (ChatbotException e) {

            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_5);
            resp.addProperty(RestUtils.RESP_KEY_DATA, "设置不成功，智能问答引擎服务异常。");
        }
        return resp;
    }

    /**
     * Enable Chatbot 智能回复
     *
     * @param j
     * @return
     */
    private JsonObject enableAiSuggest(JsonObject j, boolean isEnabled) {
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

        c.setAisuggest(isEnabled);
        chatbotRes.save(c);

        if (c.getChannel().equals(Constants.CHANNEL_TYPE_WEBIM)) {
            // 更新访客网站配置
            CousultInvite invite = OnlineUserProxy.consult(c.getSnsAccountIdentifier(), c.getOrgi());
            invite.setAisuggest(isEnabled);
            consultInviteRes.save(invite);
            OnlineUserProxy.cacheConsult(invite);
        } else if (c.getChannel().equals(Constants.CHANNEL_TYPE_MESSENGER)) {
            FbMessenger fbMessenger = fbMessengerRepository.findOneByPageId(c.getSnsAccountIdentifier());
            fbMessenger.setAisuggest(isEnabled);
            fbMessengerRepository.save(fbMessenger);
        }

        resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
        resp.addProperty(RestUtils.RESP_KEY_DATA, "完成。");

        return resp;
    }

    /**
     * 更新聊天机器人
     *
     * @param j
     * @return
     */
    private JsonObject update(JsonObject j) throws Exception {
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

        // update clientId and secret
        if (j.has("clientId")) {
            c.setClientId(j.get("clientId").getAsString());
        } else {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的参数，未传入【clientId】。");
            return resp;
        }

        if (j.has("secret")) {
            c.setSecret(j.get("secret").getAsString());
        } else {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的参数，未传入【secret】。");
            return resp;
        }

        // 更新访客网站配置
        CousultInvite invite = OnlineUserProxy.consult(c.getSnsAccountIdentifier(), c.getOrgi());

        if (j.has("workmode") && Constants.CHATBOT_VALID_WORKMODELS.contains(j.get("workmode").getAsString())) {
            c.setWorkmode(j.get("workmode").getAsString());
            if (isWebIMChannelBySnsType(j)) {
                invite.setAifirst(!StringUtils.equals(Constants.CHATBOT_HUMAN_FIRST, c.getWorkmode()));
            }
        }

        if (j.has("enabled")) {
            boolean enabled = j.get("enabled").getAsBoolean();
            c.setEnabled(enabled);
            if (isWebIMChannelBySnsType(j)) {
                invite.setAi(enabled);
            }
        }

        try {
            logger.info("[update] BOT_PROVIDER {}", botServiceProvider);
            com.chatopera.bot.sdk.Chatbot bot = new com.chatopera.bot.sdk.Chatbot(
                    c.getClientId(), c.getSecret(), botServiceProvider);

            Response result = bot.command("GET", "/");
            logger.info("[update] bot details response", result.toJSON().toString());

            if (result.getRc() == 0) {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                JsonObject data = new JsonObject();
                data.addProperty("id", c.getId());
                resp.add(RestUtils.RESP_KEY_DATA, data);
                resp.addProperty(RestUtils.RESP_KEY_MSG, "更新成功。");
                JSONObject botDetails = (JSONObject) result.getData();
                c.setDescription(botDetails.getString("description"));
                c.setFallback(botDetails.getString("fallback"));
                c.setWelcome(botDetails.getString("welcome"));
                if (isWebIMChannelBySnsType(j)) {
                    invite.setAisuccesstip(botDetails.getString("welcome"));
                    invite.setAiname(c.getName());
                }
                c.setName(botDetails.getString("name"));

            } else {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_6);
                resp.addProperty(
                        RestUtils.RESP_KEY_ERROR,
                        "Chatopera 云服务：无法访问该机器人，请确认【1】该服务器可以访问互联网，【2】该聊天机器人已经创建，【3】clientId和Secret正确设置。提示：该机器人不存在，请先创建机器人, 登录 https://bot.chatopera.com");
                return resp;
            }
        } catch (ChatbotException e) {
            logger.error("bot create error", e);
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_5);
            resp.addProperty(
                    RestUtils.RESP_KEY_ERROR,
                    "Chatopera 云服务：无法访问该机器人，请确认【1】该服务器可以访问互联网，【2】该聊天机器人已经创建，【3】clientId和Secret正确设置。");
            return resp;
        } catch (MalformedURLException e) {
            logger.error("bot request error", e);
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_7);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "更新智能问答引擎失败。" + e.toString());
            return resp;
        }

        c.setUpdatetime(new Date());
        chatbotRes.save(c);
        if (isWebIMChannelBySnsType(j)) {
            consultInviteRes.save(invite);
            OnlineUserProxy.cacheConsult(invite);
        }
        return resp;
    }

    /**
     * 获取聊天机器人列表
     *
     * @param j
     * @param id
     * @param orgi
     * @param p
     * @param ps
     * @return
     */
    private JsonObject fetch(JsonObject j, String id, boolean isSuperuser, String orgi, int p, int ps) {
        JsonObject resp = new JsonObject();
        if (!isSuperuser) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "当前登录用户不是管理员，无权访问机器人客服资源。");
            return resp;
        }

        Page<Chatbot> records = chatbotRes.findWithPagination(
                new PageRequest(p, ps, Sort.Direction.DESC, "createtime"));

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
            if (snsAccount == null) {
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
     * @param orgi
     * @return
     */
    private JsonObject delete(final JsonObject j, final String uid, final String orgi) {
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

        if (c.getChannel().equals(Constants.CHANNEL_TYPE_WEBIM)) {
            // 更新访客网站配置
            CousultInvite invite = OnlineUserProxy.consult(c.getSnsAccountIdentifier(), c.getOrgi());
            if (invite != null) {
                invite.setAi(false);
                invite.setAiname(null);
                invite.setAisuccesstip(null);
                invite.setAifirst(false);
                invite.setAiid(null);
                invite.setAisuggest(false);
                consultInviteRes.save(invite);
                OnlineUserProxy.cacheConsult(invite);
            }
        } else if (c.getChannel().equals(Constants.CHANNEL_TYPE_MESSENGER)) {
            FbMessenger fbMessenger = fbMessengerRepository.findOneByPageId(c.getSnsAccountIdentifier());
            fbMessenger.setAiid(null);
            fbMessenger.setAi(false);
            fbMessengerRepository.save(fbMessenger);
        }

        chatbotRes.delete(c);
        resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
        resp.addProperty(RestUtils.RESP_KEY_DATA, "删除成功。");
        return resp;
    }

    /**
     * 创建聊天机器人
     *
     * @param j
     * @param creater
     * @param orgi
     * @return
     */
    private JsonObject create(final JsonObject j, final String creater, final String orgi) throws Exception {
        JsonObject resp = new JsonObject();
        String snsid = null;
        String workmode = null;
        String clientId = null;
        String secret = null;

        if ((!j.has("clientId")) || StringUtils.isBlank(j.get("clientId").getAsString())) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的参数，未传入【clientId】。");
            return resp;
        } else {
            clientId = j.get("clientId").getAsString();
        }

        if ((!j.has("secret")) || StringUtils.isBlank(j.get("secret").getAsString())) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的参数，未传入【secret】。");
            return resp;
        } else {
            secret = j.get("secret").getAsString();
        }

        if (!(j.has("workmode") && Constants.CHATBOT_VALID_WORKMODELS.contains(j.get("workmode").getAsString()))) {
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
            if (!snsAccountRes.existsBySnsidAndSnstypeAndOrgi(snsid, j.get("snstype").getAsString(), orgi)) {
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

        if (chatbotRes.existsByClientIdAndOrgi(clientId, orgi)) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的参数，数据库中存在该聊天机器人。");
            return resp;
        }

        try {
            logger.info("[create] bot with url {}", botServiceProvider);
            com.chatopera.bot.sdk.Chatbot bot = new com.chatopera.bot.sdk.Chatbot(clientId, secret, botServiceProvider);
            Response result = bot.command("GET", "/");
            logger.info("[create] bot details response {}", result.toJSON().toString());

            if (result.getRc() == 0) { // 该机器人存在，clientId 和 Secret配对成功
                // 创建成功
                Chatbot c = new Chatbot();
                JSONObject botDetails = (JSONObject) result.getData();
                c.setId(MainUtils.getUUID());
                c.setClientId(clientId);
                c.setSecret(secret);
                c.setBaseUrl(botServiceProvider);
                c.setDescription(botDetails.getString("description"));
                c.setFallback(botDetails.getString("fallback"));
                c.setPrimaryLanguage(botDetails.getString("primaryLanguage"));
                c.setName(botDetails.getString("name"));
                c.setWelcome(botDetails.getString("welcome"));
                c.setCreater(creater);
                c.setOrgi(orgi);
                c.setChannel(j.get("snstype").getAsString());
                c.setSnsAccountIdentifier(snsid);
                Date dt = new Date();
                c.setCreatetime(dt);
                c.setUpdatetime(dt);
                c.setWorkmode(workmode);

                // 默认不开启
                boolean enabled = false;
                c.setEnabled(enabled);

                // 更新访客网站配置
                if (isWebIMChannelBySnsType(j)) {
                    CousultInvite invite = OnlineUserProxy.consult(c.getSnsAccountIdentifier(), c.getOrgi());
                    invite.setAi(enabled);
                    invite.setAifirst(StringUtils.equals(Constants.CHATBOT_CHATBOT_FIRST, workmode));
                    invite.setAiid(c.getId());
                    invite.setAiname(c.getName());
                    invite.setAisuccesstip(c.getWelcome());
                    consultInviteRes.save(invite);
                    OnlineUserProxy.cacheConsult(invite);
                } else if (j.get("snstype").getAsString().equals(Constants.CHANNEL_TYPE_MESSENGER)) {
                    FbMessenger fbMessenger = fbMessengerRepository.findOneByPageId(c.getSnsAccountIdentifier());
                    fbMessenger.setAi(enabled);
                    fbMessenger.setAiid(c.getId());
                    fbMessengerRepository.save(fbMessenger);
                }

                chatbotRes.save(c);

                JsonObject data = new JsonObject();
                data.addProperty("id", c.getId());
                resp.add(RestUtils.RESP_KEY_DATA, data);
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                return resp;
            } else {
                // 创建失败
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_6);
                resp.addProperty(
                        RestUtils.RESP_KEY_ERROR, "Chatopera 云服务：该机器人不存在，请先创建机器人, 登录 https://bot.chatopera.com");
                return resp;
            }
        } catch (ChatbotException e) {
            logger.error("bot create error", e);
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_5);
            resp.addProperty(
                    RestUtils.RESP_KEY_ERROR,
                    "Chatopera 云服务：无法访问该机器人，请确认【1】该服务器可以访问互联网，【2】该聊天机器人已经创建，【3】clientId和Secret正确设置。");
            return resp;
        } catch (MalformedURLException e) {
            logger.error("bot request error", e);
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "Chatopera 云服务：不合法的聊天机器人服务URL。");
            return resp;
        }
    }

    private JsonObject faq(final JsonObject j, String orgi) {
        JsonObject resp = new JsonObject();
        if ((!j.has("snsaccountid")) || StringUtils.isBlank(j.get("snsaccountid").getAsString())) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的操作，snsaccountid不能为空。");
            return resp;
        }

        final String snsaccountid = j.get("snsaccountid").getAsString();
        Chatbot c = chatbotRes.findBySnsAccountIdentifierAndOrgi(snsaccountid, orgi);

        if (c == null) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "该聊天机器人不存在。");
            return resp;
        }

        String userId = j.get("userId").getAsString();
        String textMessage = j.get("textMessage").getAsString();

        try {
            com.chatopera.bot.sdk.Chatbot bot = new com.chatopera.bot.sdk.Chatbot(
                    c.getClientId(), c.getSecret(), botServiceProvider);

            JSONObject result = bot.faq(
                    userId,
                    textMessage,
                    Double.parseDouble(SystemEnvHelper.getenv(ChatbotConstants.THRESHOLD_FAQ_BEST_REPLY, "0.8")),
                    Double.parseDouble(SystemEnvHelper.getenv(ChatbotConstants.THRESHOLD_FAQ_SUGG_REPLY, "0.6"))
            );
            if (result.getInt("rc") == 0) {
                JsonParser jsonParser = new JsonParser();
                JsonElement data = jsonParser.parse(result.getJSONArray("data").toString());

                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                resp.add(RestUtils.RESP_KEY_DATA, data);
            } else {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_5);
                resp.addProperty(RestUtils.RESP_KEY_DATA, "查询不成功，智能问答引擎服务异常。");
            }
        } catch (
                MalformedURLException e) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_6);
            resp.addProperty(RestUtils.RESP_KEY_DATA, "查询不成功，智能问答引擎地址不合法。");
        } catch (
                ChatbotException e) {

            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_5);
            resp.addProperty(RestUtils.RESP_KEY_DATA, "查询不成功，智能问答引擎服务异常。");
        } catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return resp;
    }

    /**
     * 判断一个渠道是不是网页聊天
     *
     * @param p
     * @return
     */
    private boolean isWebIMChannelBySnsType(final JsonObject p) {
        return StringUtils.equals(p.get("snstype").getAsString(), Constants.CHANNEL_TYPE_WEBIM);
    }
}
