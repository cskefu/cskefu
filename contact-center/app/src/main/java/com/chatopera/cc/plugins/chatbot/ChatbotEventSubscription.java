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
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.model.Chatbot;
import com.chatopera.cc.persistence.repository.AgentUserRepository;
import com.chatopera.cc.persistence.repository.ChatMessageRepository;
import com.chatopera.cc.persistence.repository.ChatbotRepository;
import com.chatopera.cc.socketio.message.ChatMessage;
import com.chatopera.cc.util.SerializeUtil;
import com.chatopera.cc.util.SystemEnvHelper;
import org.apache.commons.lang.StringUtils;
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

    @Autowired
    private ChatMessageRepository chatMessageRes;

    // 机器人服务提供地址
    private final static String botServiceProvider = SystemEnvHelper.getenv(
            ChatbotConstants.BOT_PROVIDER, ChatbotConstants.DEFAULT_BOT_PROVIDER);

    // FAQ最佳回复阀值
    private final static double faqBestReplyThreshold = Double.parseDouble(SystemEnvHelper.getenv(
            ChatbotConstants.THRESHOLD_FAQ_BEST_REPLY, "0.8"));
    // FAQ建议回复阀值
    private final static double faqSuggReplyThreshold = Double.parseDouble(SystemEnvHelper.getenv(
            ChatbotConstants.THRESHOLD_FAQ_SUGG_REPLY, "0.6"));

    @Autowired
    private ChatbotProxy chatbotProxy;

    @Autowired
    private ChatbotComposer chatbotComposer;

    /**
     * 接收发送消息给聊天机器人的请求
     *
     * @param payload
     */
    @JmsListener(destination = Constants.INSTANT_MESSAGING_MQ_QUEUE_CHATBOT, containerFactory = "jmsListenerContainerQueue")
    public void onMessage(final String payload) {
        ChatMessage message = SerializeUtil.deserialize(payload);
        try {
            try {
				chat(message);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
                "[chat] chat request baseUrl {}, chatbot {}, fromUserId {}, textMessage {}", botServiceProvider,
                c.getName(),
                request.getUserid(), request.getMessage());
        // Get response from Conversational Engine.
        com.chatopera.bot.sdk.Chatbot bot = new com.chatopera.bot.sdk.Chatbot(
                c.getClientId(), c.getSecret(), botServiceProvider);


        JSONObject body = new JSONObject();
        body.put("fromUserId", request.getUserid());
        body.put("textMessage", request.getMessage());
        body.put("faqBestReplyThreshold", faqBestReplyThreshold);
        body.put("faqSuggReplyThreshold", faqSuggReplyThreshold);
        Response result = bot.command("POST", "/conversation/query", body);

        // parse response
        if (result != null) {
            logger.info("[chat] chat response {}", result.toString());
            if (result.getRc() == 0) {
                // reply
                JSONObject data = (JSONObject) result.getData();
                if (data.has("logic_is_fallback")) {
                    ChatMessage resp = creatChatMessage(request, c);
                    resp.setMessage(data.getString("string"));
                    ChatMessage respHelp = new ChatMessage();
                    JSONArray respParams = new JSONArray();
                    if (!StringUtils.equals(MainContext.ChannelType.WEBIM.toString(), c.getChannel())) {
                        /**
                         * 非 WEBIM 情况，比如 Facebook Messenger，使用下面的方法
                         */
                        // 如果在更多渠道下，此处可能仅适应于 Messenger，那么宜将检测条件调整为 ChannelType.MESSENGER
                        if (data.getBoolean("logic_is_fallback")) {
                            if (!StringUtils.equals(Constants.CHATBOT_HUMAN_FIRST, c.getWorkmode())) {
                                JSONArray faqReplies = data.getJSONArray("faq");
                                JSONObject message = new JSONObject();
                                JSONObject attachment = new JSONObject();
                                attachment.put("type", "template");
                                JSONObject payload = new JSONObject();
                                payload.put("template_type", "button");
                                JSONArray buttons = new JSONArray();
                                if (faqReplies.length() > 0) {
                                    int cycles = faqReplies.length() > 3 ? 3 : faqReplies.length();
                                    payload.put("text", "${suggestQuestion}");
                                    for (int i = 0; i < cycles; i++) {
                                        JSONObject button = new JSONObject();
                                        JSONObject faqReply = faqReplies.getJSONObject(i);
                                        button.put("type", "postback");
                                        button.put("title", faqReply.getString("post"));
                                        button.put("payload", "FAQ_LIST");
                                        buttons.put(button);
                                    }
                                } else {
                                    if (StringUtils.equals(c.getWorkmode(), Constants.CHATBOT_CHATBOT_ONLY)) {
                                        return;
                                    }

                                    payload.put("text", data.getString("string"));
                                    JSONObject button = new JSONObject();
                                    button.put("type", "postback");
                                    button.put("title", "${transferManualService}");
                                    button.put("payload", "TRANSFER_LABOR");
                                    buttons.put(button);
                                }
                                payload.put("buttons", buttons);
                                attachment.put("payload", payload);
                                message.put("attachment", attachment);
                                resp.setExpmsg(message.toString());
                            }
                        } else if (StringUtils.equals(Constants.PROVIDER_FEEDBACK, data.getJSONObject("service").get("provider").toString())) {
                            respHelp = null;
                            // 反馈回复内容
                            String sentiment = data.getJSONObject("service").get("sentiment").toString();
                            if (StringUtils.equals(Constants.PROVIDER_FEEDBACK_EVAL_POSITIVE, sentiment)) {
                                // 积极评价
                                resp.setMessage(ChatbotConstants.PROVIDER_FEEDBACK_EVAL_POSITIVE_REPLY_PLACEHOLDER);
                            } else if (StringUtils.equals(Constants.PROVIDER_FEEDBACK_EVAL_NEGATIVE, sentiment)) {
                                // 消极评价
                                resp.setMessage(ChatbotConstants.PROVIDER_FEEDBACK_EVAL_NEGATIVE_REPLY_PLACEHOLDER);
                            } else {
                                // no response
                                resp.setMessage("${leaveMeAlone}");
                            }
                        } else if (StringUtils.equals(Constants.PROVIDER_FAQ, data.getJSONObject("service").get("provider").toString())) {
                            if (data.has("params")) {
                                resp.setMessage(data.getJSONArray("params").getJSONObject(0).getString("content"));
                            }

                            respHelp = creatChatMessage(request, c);
                            JSONObject message = new JSONObject();
                            JSONObject attachment = new JSONObject();
                            attachment.put("type", "template");
                            JSONObject payload = new JSONObject();
                            payload.put("template_type", "button");
                            payload.put("text", "${evaluationAsk}");
                            JSONArray buttons = new JSONArray();
                            JSONObject buttonYes = new JSONObject();
                            buttonYes.put("type", "postback");
                            buttonYes.put("title", "${evaluationYes}");
                            buttonYes.put("payload", "__chatbot_help_positive");
                            buttons.put(buttonYes);
                            JSONObject buttonNo = new JSONObject();
                            buttonNo.put("type", "postback");
                            buttonNo.put("title", "${evaluationNo}");
                            buttonNo.put("payload", "__chatbot_help_negative");
                            buttons.put(buttonNo);
                            payload.put("buttons", buttons);
                            attachment.put("payload", payload);
                            message.put("attachment", attachment);

                            respHelp.setExpmsg(message.toString());
                        } else if (data.has("params")) {
                            Object obj = data.get("params");
                            if (obj instanceof JSONObject) {
                                resp.setExpmsg(obj.toString());
                            } else {
                                JSONArray params = data.getJSONArray("params");
                                for (int i = 0; i < params.length(); i++) {
                                    respParams.put(params.get(i));
                                }
                            }
                        }
                    } else {
                        /**
                         * 当前渠道为 WEBIM
                         */
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
                                // set the maximum suggest list as only seven items
                                if(i == 6){
                                    break;
                                }
                            }
                            if (suggs.length() > 0) {
                                // TODO set help message on View Page
                                resp.setMessage("为您找到如下信息：");
                                resp.setExpmsg(suggs.toString());
                            }
                        } else if (data.has("params")) {
                            resp.setExpmsg(data.get("params").toString());
                        }
                    }

                    // 更新聊天机器人累计值
                    updateAgentUserWithRespData(request.getUserid(), request.getOrgi(), data);

                    // 保存并发送
                    if (MainContext.ChannelType.WEBIM.toString().equals(resp.getChannel())) {
                        // WEBIM 渠道
                        if (StringUtils.equals(resp.getMessage(), "{CLEAR} 混合类型消息") || StringUtils.equals(resp.getMessage(), "{CLEAR} 图文消息")) {
                            // 处理多答案
                            JSONArray params = data.getJSONArray("params");
                            for (int i = 0; i < params.length(); i++) {
                                JSONObject item = params.getJSONObject(i);
                                ChatMessage itemResp = (ChatMessage) resp.clone();
                                itemResp.setId(MainUtils.getUUID());
                                itemResp.setExpmsg(null);

                                // 处理不同答案类型
                                // 数据格式：https://github.com/chatopera/cskefu/issues/468
                                if (item.getString("type").equals("plain")) {
                                    itemResp.setMessage(item.getString("content"));
                                    chatbotProxy.saveAndPublish(itemResp);
                                } else if (item.getString("type").equals("card")) {
                                    if (item.has("thumbnail")) {
                                        String thumbnailUrl = botServiceProvider + item.getString("thumbnail");
                                        item.put("thumbnail", thumbnailUrl);
                                        JSONArray expmsg = new JSONArray();
                                        expmsg.put(item);
                                        itemResp.setExpmsg(expmsg.toString());
//                                        itemResp.setMsgtype("image");
                                        itemResp.setMessage(thumbnailUrl);
                                        chatbotProxy.saveAndPublish(itemResp);
                                    }
                                }
                            }
                        } else {
                            chatbotProxy.saveAndPublish(resp);
                        }
                    } else {
                        // 其他渠道
                        chatMessageRes.save(resp);
                        if (respParams.length() > 0) {
                            for (int i = 0; i < respParams.length(); i++) {
                                ChatMessage respParam = creatChatMessage(request, c);
                                respParam.setExpmsg(respParams.get(i).toString());
                                chatMessageRes.save(respParam);
                                chatbotComposer.handle(respParam);
                            }
                        } else {
                            chatbotComposer.handle(resp);
                        }
                        if (respHelp != null) {
                            chatbotComposer.handle(respHelp);
                        }
                    }
                }
            }
        } else {
            logger.warn("[chat] can not get expected response rc {}, error {}", result.getRc(), result.getError());
        }
    }


    /**
     * 创建chatMessage并设置固定值
     *
     * @param request
     * @param c
     * @return
     */
    private ChatMessage creatChatMessage(ChatMessage request, Chatbot c) {
        ChatMessage resp = new ChatMessage();
        resp.setCalltype(MainContext.CallType.OUT.toString());
        resp.setAppid(request.getAppid());
        resp.setOrgi(request.getOrgi());
        resp.setAiid(request.getAiid());
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

        return resp;

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
            try {
				if (data.has("logic_is_unexpected") && data.getBoolean("logic_is_unexpected")) {
				    p.setChatbotlogicerror(p.getChatbotlogicerror() + 1);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            agentUserRes.save(p);
        });

    }

}
