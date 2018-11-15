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
package com.chatopera.cc.concurrent.chatbot;

import com.chatopera.bot.exception.ChatbotException;
import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.app.cache.CacheHelper;
import com.chatopera.cc.app.handler.api.request.RestUtils;
import com.chatopera.cc.app.im.message.ChatMessage;
import com.chatopera.cc.app.im.util.ChatbotUtils;
import com.chatopera.cc.app.model.AgentUser;
import com.chatopera.cc.app.model.Chatbot;
import com.chatopera.cc.app.persistence.repository.AgentUserRepository;
import com.chatopera.cc.app.persistence.repository.ChatbotRepository;
import com.chatopera.cc.concurrent.user.UserDataEvent;
import com.chatopera.cc.util.Constants;
import com.lmax.disruptor.EventHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

@SuppressWarnings("rawtypes")
public class ChatbotEventHandler implements EventHandler<UserDataEvent> {
    private final static Logger logger = LoggerFactory.getLogger(ChatbotEventHandler.class);

    private ChatbotRepository chatbotRes;
    private AgentUserRepository agentUserRes;
    private String botServiceUrl;

    /**
     * 根据聊天机器人返回数据更新agentUser
     *
     * @param userid
     * @param data
     */
    private void updateAgentUserWithRespData(final String userid, final String orgi, final JSONObject data) {
        AgentUser agentUser = (AgentUser) CacheHelper.getAgentUserCacheBean().getCacheObject(userid, orgi);
        agentUser.setChatbotround(agentUser.getChatbotround() + 1);
        if (data.has("logic_is_unexpected") && data.getBoolean("logic_is_unexpected")) {
            agentUser.setChatbotlogicerror(agentUser.getChatbotlogicerror() + 1);
        }
        getAgentUserRes().save(agentUser);
        CacheHelper.getAgentUserCacheBean().put(userid, agentUser, orgi);
    }

    private void chat(final ChatbotEvent payload) throws MalformedURLException, ChatbotException {
        ChatMessage request = (ChatMessage) payload.getData();
        Chatbot c = getChatbotRes()
                .findOne(request.getAiid());

        logger.info("[chatbot disruptor] chat request baseUrl {}, chatbot {}, fromUserId {}, textMessage {}", getChatbotServiceUrl(), c.getName(), request.getUserid(), request.getMessage());
        // Get response from Conversational Engine.
        com.chatopera.bot.sdk.Chatbot bot = new com.chatopera.bot.sdk.Chatbot(c.getClientId(), c.getSecret(), getChatbotServiceUrl());
        JSONObject result = bot.conversation(request.getUserid(), request.getMessage());

        // parse response
        logger.info("[chatbot disruptor] chat response {}", result.toString());
        if (result.getInt(RestUtils.RESP_KEY_RC) == 0) {
            // reply
            JSONObject data = result.getJSONObject("data");
            ChatMessage resp = new ChatMessage();
            resp.setCalltype(MainContext.CallTypeEnum.OUT.toString());
            resp.setAppid(resp.getAppid());
            resp.setOrgi(request.getOrgi());
            resp.setAiid(request.getAiid());
            resp.setMessage(data.getString("string"));
            resp.setTouser(request.getUserid());
            resp.setTousername(request.getUsername());
            resp.setAgentserviceid(request.getAgentserviceid());
            resp.setMsgtype(request.getMsgtype());
            resp.setUserid(request.getUserid());
            resp.setType(request.getType());
            resp.setChannel(request.getChannel());
            if (data.has("params")) {
                resp.setExpmsg(data.get("params").toString());
            }
            resp.setContextid(request.getContextid());
            resp.setSessionid(request.getSessionid());
            resp.setUsession(request.getUsession());
            resp.setUsername(c.getName());
            resp.setUpdatetime(System.currentTimeMillis());

            updateAgentUserWithRespData(request.getUserid(), request.getOrgi(), data); // 更新聊天机器人累计值
            ChatbotUtils.saveAndPublish(resp); // 保存并发送
        } else {
            // TODO handle exceptions
        }
    }

    @Override
    public void onEvent(UserDataEvent event, long arg1, boolean arg2)
            throws Exception {
        ChatbotEvent payload = (ChatbotEvent) event.getEvent();
        switch (payload.getEventype()) {
            case Constants
                    .CHATBOT_EVENT_TYPE_CHAT:
                chat(payload);
                break;
            default:
                logger.warn("[chatbot disruptor] onEvent unknown.");
        }
    }

    /**
     * Lazy load agentUser repo
     *
     * @return
     */
    private AgentUserRepository getAgentUserRes() {
        if (agentUserRes == null)
            agentUserRes = MainContext.getContext().getBean(AgentUserRepository.class);
        return agentUserRes;
    }


    /**
     * Lazy load chatbot repo
     *
     * @return
     */
    private ChatbotRepository getChatbotRes() {
        if (chatbotRes == null)
            chatbotRes = MainContext.getContext().getBean(ChatbotRepository.class);
        return chatbotRes;
    }


    private String getChatbotServiceUrl() {
        if (botServiceUrl == null) {
            botServiceUrl = MainContext.getContext().getEnvironment().getProperty("chatopera.bot.url");
        }
        return botServiceUrl;
    }

}
