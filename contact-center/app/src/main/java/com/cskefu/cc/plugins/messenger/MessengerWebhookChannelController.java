/*
 * Copyright (C) 2019 Chatopera Inc, <https://www.chatopera.com>
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

package com.cskefu.cc.plugins.messenger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.chatopera.bot.exception.ChatbotException;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.exception.CSKefuException;
import com.cskefu.cc.model.FbMessenger;
import com.cskefu.cc.persistence.repository.FbMessengerRepository;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;

@Controller
@RequestMapping("/messenger")
public class MessengerWebhookChannelController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(MessengerWebhookChannelController.class);

    @Autowired
    private FbMessengerRepository fbMessengerRepository;

    @Autowired
    private MessengerMessageProxy messengerMessageProxy;

    @Autowired
    private MessengerChatbot messengerChatbot;

    @RequestMapping(value = "/webhook/{pageId}", method = RequestMethod.GET)
    @ResponseBody
    public String get(@PathVariable("pageId") String pageId, @RequestParam("hub.challenge") String challenge, @RequestParam("hub.verify_token") String verify_token) {
        logger.info("[get] verify token pageid {}", pageId);
        FbMessenger fbMessenger = fbMessengerRepository.findOneByPageId(pageId);
        String result = "not allow!";

        if (fbMessenger != null && fbMessenger.getVerifyToken().equals(verify_token)) {
            result = challenge;
        }

        return result;
    }

    @RequestMapping(value = "/webhook/{pageId}", method = RequestMethod.POST)
    @ResponseBody
    public String post(@PathVariable("pageId") String pageId, @RequestBody JSONObject jsonParam) {
        logger.info("[post] pageId {}, payload {}", pageId, jsonParam.toString());
        // NOTE: 此处 PathVariable pageId 不安全，不建议在函数内使用，详见 #1190
        // https://gitlab.chatopera.com/cskefu/cskefu.io/issues/1190
        // 该值可使用 recipientId 更为准确，recipientId 就是实际的 pageId
        if (jsonParam.getString("object").equals("page")) {
            JSONArray entries = jsonParam.getJSONArray("entry");
            for (int i = 0; i < entries.size(); i++) {
                JSONObject entry = entries.getJSONObject(i);
                JSONArray messaging = entry.getJSONArray("messaging");
                for (int j = 0; j < messaging.size(); j++) {
                    try {
                        messageHandler(messaging.getJSONObject(j));
                    } catch (Exception e) {
                        logger.error("[messenger] 接收消息异常", e);
                    }
                }
            }
        }

        return "";
    }

    private void messageHandler(JSONObject messagingEvent) throws ChatbotException, CSKefuException, MalformedURLException {
        String senderId = (String) JSONPath.eval(messagingEvent, "$.sender.id");
        String recipientId = (String) JSONPath.eval(messagingEvent, "$.recipient.id");
        String referralType = (String) JSONPath.eval(messagingEvent, "$.referral.type");
        String optinType = (String) JSONPath.eval(messagingEvent, "$.optin.type");
        String postbackPayload = (String) JSONPath.eval(messagingEvent, "$.postback.payload");
        JSONObject quickRepliesPayload = (JSONObject) JSONPath.eval(messagingEvent, "$.message.quick_reply");

        if (StringUtils.equals(referralType, "OPEN_THREAD")) {
            String ref = (String) JSONPath.eval(messagingEvent, "$.referral.ref");
            messengerMessageProxy.acceptMeLink(senderId, recipientId, ref);
            return;
        } else if (StringUtils.equals(optinType, "one_time_notif_req")) {
            String otnToken = (String) JSONPath.eval(messagingEvent, "$.optin.one_time_notif_token");
            String ref = (String) JSONPath.eval(messagingEvent, "$.optin.payload");
            messengerMessageProxy.acceptOTNReq(senderId, recipientId, otnToken, ref);
            return;
        } else if (StringUtils.isNotBlank(postbackPayload)) {
            if (StringUtils.equals(postbackPayload, "TRANSFER_LABOR")) {
                messengerChatbot.switchManualCustomerService(senderId);
            } else if (StringUtils.equals(postbackPayload, "FAQ_LIST")) {
                String msg = (String) JSONPath.eval(messagingEvent, "$.postback.title");
                messengerMessageProxy.accept(senderId, recipientId, MainContext.MediaType.TEXT, msg);
            } else if (StringUtils.equals(postbackPayload, "startChatopera")) {
                messengerMessageProxy.accept(senderId, recipientId, MainContext.MediaType.TEXT, "__kickoff");
            } else {
                messengerMessageProxy.accept(senderId, recipientId, MainContext.MediaType.TEXT, postbackPayload);
            }
            return;
        } else if (quickRepliesPayload != null) {
            String msg = (String) JSONPath.eval(messagingEvent, "$.message.quick_reply.payload");
            messengerMessageProxy.accept(senderId, recipientId, MainContext.MediaType.TEXT, msg);
            return;
        }

        String msg = (String) JSONPath.eval(messagingEvent, "$.message.text");
        JSONArray attachments = (JSONArray) JSONPath.eval(messagingEvent, "$.message.attachments");

        if (StringUtils.isNotBlank(msg)) {
            messengerMessageProxy.accept(senderId, recipientId, MainContext.MediaType.TEXT, msg);
        } else {
            for (Object att : attachments) {

                String type = (String) JSONPath.eval(att, "$.type");
                String url = (String) JSONPath.eval(att, "$.payload.url");

                if (StringUtils.equals(type, "image")) {
                    messengerMessageProxy.accept(senderId, recipientId, MainContext.MediaType.IMAGE, url);
                }
            }
        }
    }
}

