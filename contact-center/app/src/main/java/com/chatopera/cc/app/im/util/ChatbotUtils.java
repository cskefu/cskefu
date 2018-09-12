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
import com.chatopera.cc.app.im.client.NettyClients;
import com.chatopera.cc.app.im.message.ChatMessage;
import com.chatopera.cc.app.model.AgentUser;
import com.chatopera.cc.app.model.MessageOutContent;
import com.chatopera.cc.app.cache.CacheHelper;
import com.chatopera.cc.app.persistence.repository.ChatMessageRepository;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.HashSet;

public class ChatbotUtils {
    public static final HashSet<String> VALID_LANGS = new HashSet<String>(Arrays.asList(new String[]{"zh_CN", "en_US"}));
    public static final String CHATBOT_FIRST = "机器人客服优先";
    public static final String HUMAN_FIRST = "人工客服优先";
    public static final HashSet<String> VALID_WORKMODELS = new HashSet<String>(Arrays.asList(new String[]{CHATBOT_FIRST, HUMAN_FIRST}));

    public static final String SNS_TYPE_WEBIM = "app";


    /**
     * 使用snsid得到ChatbotID
     *
     * @param snsid
     * @return
     */
    public static String resolveChatbotIDWithSnsid(String snsid, String clientId) {
        return (clientId + "_" + snsid).toLowerCase();
    }

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
    protected static MessageOutContent createMessage(ChatMessage data, String appid, String channel, String direction, String chatype, String msgtype, String userid) {
        MessageOutContent outMessage = new MessageOutContent();

        outMessage.setMessage(data.getMessage());
        outMessage.setMessageType(msgtype);
        outMessage.setCalltype(direction);
        outMessage.setAgentUser(null);
        outMessage.setSnsAccount(null);

        {
            data.setUserid(userid);
            data.setUsername(data.getUsername());
            data.setTouser(userid);

            data.setAgentuser(userid);


            data.setAgentserviceid(data.getContextid());
            data.setChatype(chatype);

            data.setChannel(channel);

            data.setAppid(data.getAppid());
            data.setOrgi(data.getOrgi());

            data.setMsgtype(msgtype);

            data.setUsername(data.getUsername());
            data.setUsession(data.getUserid());                //agentUser作为 session id
            data.setContextid(data.getContextid());
            data.setCalltype(direction);

            outMessage.setContextid(data.getContextid());
            outMessage.setFromUser(data.getUserid());
            outMessage.setToUser(data.getTouser());
            outMessage.setChannelMessage(data);
            outMessage.setNickName(data.getUsername());
            outMessage.setCreatetime(data.getCreatetime());

            if (!StringUtils.isBlank(data.getSuggestmsg())) {
                outMessage.setSuggest(data.getSuggest());
            }

            data.setUpdatetime(System.currentTimeMillis());

            /**
             * 保存消息
             */
            if (MainContext.MessageTypeEnum.MESSAGE.toString().equals(data.getType())) {
                MainContext.getContext().getBean(ChatMessageRepository.class).save(data);
            }
            AgentUser agentUser = (AgentUser) CacheHelper.getAgentUserCacheBean().getCacheObject(userid, MainContext.SYSTEM_ORGI);
            if (agentUser != null && !StringUtils.isBlank(agentUser.getAgentno())) {
                //将消息发送给 坐席

                if (MainContext.CallTypeEnum.OUT.toString().equals(direction)) {
                    data.setUserid(agentUser.getAgentno());
                }
                NettyClients.getInstance().sendAgentEventMessage(agentUser.getAgentno(), MainContext.MessageTypeEnum.MESSAGE.toString(), data);
            }
        }
        return outMessage;
    }

    /**
     * 发送文字消息
     * @param data
     * @param appid
     * @param channel
     * @param direction
     * @param chatype
     * @param userid
     * @return
     */
    public static MessageOutContent createTextMessage(ChatMessage data, String appid, String channel, String direction, String chatype, String userid) {
        return createMessage(data, appid, channel, direction, chatype, MainContext.MediaTypeEnum.TEXT.toString(), userid);
    }
}
