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
package com.chatopera.cc.app.im.util;

import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.app.cache.CacheHelper;
import com.chatopera.cc.app.im.client.NettyClients;
import com.chatopera.cc.app.im.message.ChatMessage;
import com.chatopera.cc.app.model.AgentUser;
import com.chatopera.cc.app.model.Chatbot;
import com.chatopera.cc.app.model.MessageOutContent;
import com.chatopera.cc.app.persistence.repository.ChatMessageRepository;
import com.chatopera.cc.app.persistence.repository.ChatbotRepository;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;

public class ChatbotUtils {
    private final static Logger logger = LoggerFactory.getLogger(ChatbotUtils.class);
    public static final HashSet<String> VALID_LANGS = new HashSet<String>(Arrays.asList(new String[]{"zh_CN", "en_US"}));
    public static final String CHATBOT_FIRST = "机器人客服优先";
    public static final String HUMAN_FIRST = "人工客服优先";
    public static final HashSet<String> VALID_WORKMODELS = new HashSet<String>(Arrays.asList(new String[]{CHATBOT_FIRST, HUMAN_FIRST}));

    public static final String SNS_TYPE_WEBIM = "webim";

    private static ChatbotRepository chatbotRes;

    private static ChatMessageRepository chatMessageRes;

    /**
     * 使用chatbotID得到snsid
     *
     * @param chatbotID
     * @return
     */
    public static String resolveSnsidWithChatbotID(String chatbotID, String clientId) {
        return StringUtils.remove(chatbotID, clientId.toLowerCase() + "_");
    }

    /**
     * @param data
     * @param direction
     * @param chatype
     * @return
     */
    private static MessageOutContent createMessage(ChatMessage data, String direction, String chatype) {
        AgentUser agentUser = (AgentUser) CacheHelper.getAgentUserCacheBean().getCacheObject(data.getUserid(), data.getOrgi());
        if (agentUser == null)
            return null;

        // 设置发送消息体
        MessageOutContent outMessage = new MessageOutContent();
        outMessage.setMessage(data.getMessage());
        outMessage.setMessageType(data.getMsgtype());
        outMessage.setCalltype(direction);
        outMessage.setAgentUser(null);
        outMessage.setSnsAccount(null);
        if (StringUtils.isNotBlank(data.getSuggestmsg())) {
            outMessage.setSuggest(data.getSuggest());
        }

        outMessage.setContextid(data.getContextid());
        outMessage.setFromUser(data.getUserid());
        outMessage.setToUser(data.getTouser());
        outMessage.setChannelMessage(data);
        outMessage.setNickName(data.getUsername());
        outMessage.setCreatetime(data.getCreatetime());

        /**
         * 保存消息
         */
        getChatMessageRes().save(data);

        //将消息发送给 访客
        NettyClients.getInstance().sendChatbotEventMessage(data.getUserid(), MainContext.MessageTypeEnum.MESSAGE.toString(), data);

        return outMessage;
    }


    /**
     * 发送聊天机器人消息
     *
     * @param data
     * @param appid
     * @param channel
     * @param direction
     * @param chatype
     * @param msgtype
     * @param userid
     * @return
     */
    protected static MessageOutContent createMessage(final ChatMessage data,
                                                     final String appid,
                                                     final String channel,
                                                     final String direction,
                                                     final String chatype,
                                                     final String msgtype,
                                                     final String userid,
                                                     final String orgi) {
        Chatbot c = getChatbotRes().findBySnsAccountIdentifierAndOrgi(appid, orgi);
        if (c == null) // ignore event if chatbot not exist.
            return null;

        data.setAiid(c.getId());
        data.setOrgi(orgi);
        data.setUserid(userid);
        data.setAgentserviceid(data.getContextid());
        data.setChatype(chatype);
        data.setChannel(channel);
        data.setMsgtype(msgtype);
        data.setUsession(data.getUserid());                //agentUser作为 session id
        data.setCalltype(direction);
        data.setUpdatetime(System.currentTimeMillis());
        return createMessage(data, direction, chatype);
    }

    /**
     * 发送文字消息
     *
     * @param data
     * @param direction
     * @param chatype
     * @return
     */
    public static MessageOutContent createTextMessage(ChatMessage data, String direction, String chatype) {
        data.setMsgtype(MainContext.MediaTypeEnum.TEXT.toString());
        return createMessage(data, direction, chatype);
    }

    private static ChatbotRepository getChatbotRes() {
        if (chatbotRes == null)
            chatbotRes = MainContext.getContext().getBean(ChatbotRepository.class);
        return chatbotRes;
    }

    private static ChatMessageRepository getChatMessageRes() {
        if (chatMessageRes == null)
            chatMessageRes = MainContext.getContext().getBean(ChatMessageRepository.class);
        return chatMessageRes;
    }

    /**
     * 保存到数据库，发送到ChatMessage
     * @param resp
     */
    public static void saveAndPublish(ChatMessage resp) {
        NettyClients.getInstance().sendChatbotEventMessage(resp.getUserid(), MainContext.MessageTypeEnum.MESSAGE.toString(), resp);
        getChatMessageRes().save(resp);
    }
}
