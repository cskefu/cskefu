/*
 * Copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
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
import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.controller.api.request.RestUtils;
import com.chatopera.cc.model.Chatbot;
import com.chatopera.cc.persistence.repository.AgentUserRepository;
import com.chatopera.cc.persistence.repository.ChatbotRepository;
import com.chatopera.cc.socketio.message.ChatMessage;
import com.chatopera.cc.util.SerializeUtil;
import com.chatopera.cc.util.SystemEnvHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;

/**
 * 发送消息给聊天机器人并处理返回结果
 */
@Component
public class ChatbotEventSubscription {
    private final static Logger logger = LoggerFactory.getLogger(ChatbotEventSubscription.class);

    @Autowired
    private Cache cache;

    @Autowired
    private AgentUserRepository agentUserRes;

    @Autowired
    private ChatbotRepository chatbotRes;

    // 机器人服务提供地址
    private final static String botServiecProvider = SystemEnvHelper.getenv(
            ChatbotConstants.BOT_PROVIDER, ChatbotConstants.DEFAULT_BOT_PROVIDER);

    // FAQ最佳回复阀值
    private final static double faqBestReplyThreshold = Double.parseDouble(SystemEnvHelper.getenv(
            ChatbotConstants.THRESHOLD_FAQ_BEST_REPLY, "0.8"));
    // FAQ建议回复阀值
    private final static double faqSuggReplyThreshold = Double.parseDouble(SystemEnvHelper.getenv(
            ChatbotConstants.THRESHOLD_FAQ_SUGG_REPLY, "0.6"));

    @Autowired
    private ChatbotProxy chatbotProxy;

    /**
     * 接收发送消息给聊天机器人的请求
     *
     * @param payload
     */
    @JmsListener(destination = Constants.INSTANT_MESSAGING_MQ_QUEUE_CHATBOT, containerFactory = "jmsListenerContainerQueue")
    public void onMessage(final String payload) {
        ChatMessage message = SerializeUtil.deserialize(payload);
        try {
            chat(message);
        } catch (MalformedURLException e) {
            logger.error("[onMessage] error", e);
        } catch (ChatbotException e) {
            logger.error("[onMessage] error", e);
        }
    }


    private void chat(final ChatMessage request) throws MalformedURLException, ChatbotException, JSONException {
        Chatbot c = chatbotRes
                .findOne(request.getAiid());

        logger.info(
                "[chat] chat request baseUrl {}, chatbot {}, fromUserId {}, textMessage {}", botServiecProvider,
                c.getName(),
                request.getUserid(), request.getMessage());
        // Get response from Conversational Engine.
        com.chatopera.bot.sdk.Chatbot bot = new com.chatopera.bot.sdk.Chatbot(
                c.getClientId(), c.getSecret(), botServiecProvider);
        JSONObject result = bot.conversation(
                request.getUserid(), request.getMessage(), faqBestReplyThreshold, faqSuggReplyThreshold);

        // parse response
        if (result != null) {
            logger.info("[chat] chat response {}", result.toString());
            if (result.getInt(RestUtils.RESP_KEY_RC) == 0) {
                // reply
                JSONObject data = result.getJSONObject("data");
                if (data.has("logic_is_fallback")) {
                    ChatMessage resp = new ChatMessage();
                    resp.setCalltype(MainContext.CallType.OUT.toString());
                    resp.setAppid(resp.getAppid());
                    resp.setOrgi(request.getOrgi());
                    resp.setAiid(request.getAiid());
                    resp.setMessage(data.getString("string"));

                    if (data.getBoolean("logic_is_fallback")) {
                        // 兜底回复，检查FAQ
                        JSONArray faqReplies = data.getJSONArray("faq");
                        JSONArray suggs = new JSONArray();
                        for (int i = 0; i < faqReplies.length(); i++) {
                            JSONObject sugg = new JSONObject();
                            JSONObject faqReply = faqReplies.getJSONObject(i);
                            sugg.put("label", Integer.toString(i + 1) + ". " + faqReply.getString("post"));
                            sugg.put("text", faqReply.getString("post"));
                            sugg.put("type", "qlist");
                            suggs.put(sugg);
                        }
                        if (suggs.length() > 0) {
                            // TODO set help message on View Page
                            resp.setMessage("为您找到如下信息：");
                            resp.setExpmsg(suggs.toString());
                        }
                    } else if (data.has("params")) {
                        resp.setExpmsg(data.get("params").toString());
                    }

                    resp.setTouser(request.getUserid());
                    resp.setAgentserviceid(request.getAgentserviceid());
                    resp.setMsgtype(request.getMsgtype());
                    resp.setUserid(request.getUserid());
                    resp.setType(request.getType());
                    resp.setChannel(request.getChannel());

                    resp.setContextid(request.getContextid());
                    resp.setSessionid(request.getSessionid());
                    resp.setUsession(request.getUsession());
                    resp.setUsername(c.getName());
                    resp.setUpdatetime(System.currentTimeMillis());

                    // 更新聊天机器人累计值
                    updateAgentUserWithRespData(request.getUserid(), request.getOrgi(), data);
                    // 保存并发送
                    chatbotProxy.saveAndPublish(resp);
                }
            } else {
                logger.warn("[chat] can not get expected response {}", result.toString());
            }
        }
    }

    /**
     * 根据聊天机器人返回数据更新agentUser
     *
     * @param userid
     * @param data
     */
    private void updateAgentUserWithRespData(final String userid, final String orgi, final JSONObject data) throws JSONException {
        cache.findOneAgentUserByUserIdAndOrgi(userid, orgi).ifPresent(p -> {
            p.setChatbotround(p.getChatbotround() + 1);
            if (data.has("logic_is_unexpected") && data.getBoolean("logic_is_unexpected")) {
                p.setChatbotlogicerror(p.getChatbotlogicerror() + 1);
            }
            agentUserRes.save(p);
        });

    }

}
