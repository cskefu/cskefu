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
package com.chatopera.cc.app.im.util;

import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.app.im.client.NettyClients;
import com.chatopera.cc.app.im.message.ChatMessage;
import com.chatopera.cc.app.model.AgentUser;
import com.chatopera.cc.app.model.AgentUserTask;
import com.chatopera.cc.app.model.MessageOutContent;
import com.chatopera.cc.app.cache.CacheHelper;
import com.chatopera.cc.app.persistence.repository.AgentUserTaskRepository;
import com.chatopera.cc.app.persistence.repository.ChatMessageRepository;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class HumanUtils {
    private final static Logger logger = LoggerFactory.getLogger(HumanUtils.class);

    /**
     * 发送文本消息
     *
     * @param data
     * @param userid
     */
    public static void createTextMessage(ChatMessage data, String userid) {
        createMessage(data, MainContext.MediaTypeEnum.TEXT.toString(), userid);
    }

    /**
     * 发送各种消息的底层方法
     *
     * @param data
     * @param msgtype
     * @param userid
     */
    protected static void createMessage(ChatMessage data, String msgtype, String userid) {
        AgentUser agentUser = (AgentUser) CacheHelper.getAgentUserCacheBean().getCacheObject(userid, MainContext.SYSTEM_ORGI);
        MessageOutContent outMessage = new MessageOutContent();

        outMessage.setMessage(data.getMessage());
        outMessage.setFilename(data.getFilename());
        outMessage.setFilesize(data.getFilesize());

        outMessage.setMessageType(msgtype);
        outMessage.setCalltype(MainContext.CallTypeEnum.IN.toString());
        outMessage.setAgentUser(agentUser);
        outMessage.setSnsAccount(null);

        MessageOutContent statusMessage = null;
        if (agentUser == null) {
            statusMessage = new MessageOutContent();
            statusMessage.setMessage(data.getMessage());
            statusMessage.setMessageType(MainContext.MessageTypeEnum.STATUS.toString());
            statusMessage.setCalltype(MainContext.CallTypeEnum.OUT.toString());
            statusMessage.setMessage("当前坐席全忙，请稍候");
        } else {
            data.setUserid(agentUser.getUserid());
            data.setUsername(agentUser.getUsername());
            data.setTouser(agentUser.getAgentno());
            data.setAgentuser(agentUser.getId());
            data.setAgentserviceid(agentUser.getAgentserviceid());
            data.setAppid(agentUser.getAppid());
            data.setOrgi(agentUser.getOrgi());
            data.setMsgtype(msgtype);
            data.setUsername(agentUser.getUsername());
            data.setUsession(agentUser.getUserid());                //agentUser作为 session id
            data.setContextid(agentUser.getContextid());
            data.setCalltype(MainContext.CallTypeEnum.IN.toString());
            if (!StringUtils.isBlank(agentUser.getAgentno())) {
                data.setTouser(agentUser.getAgentno());
            }
            data.setChannel(agentUser.getChannel());

            outMessage.setContextid(agentUser.getContextid());
            outMessage.setFromUser(data.getUserid());
            outMessage.setToUser(data.getTouser());
            outMessage.setChannelMessage(data);
            outMessage.setNickName(agentUser.getUsername());
            outMessage.setCreatetime(data.getCreatetime());

            if (data.getType() != null && data.getType().equals(MainContext.MessageTypeEnum.MESSAGE.toString())) {
                AgentUserTaskRepository agentUserTaskRes = MainContext.getContext().getBean(AgentUserTaskRepository.class);
                AgentUserTask agentUserTask = agentUserTaskRes.getOne(agentUser.getId());
                if (agentUserTask != null) {
                    if (agentUserTask.getLastgetmessage() != null && agentUserTask.getLastmessage() != null) {
                        data.setLastagentmsgtime(agentUserTask.getLastgetmessage());
                        data.setLastmsgtime(agentUserTask.getLastmessage());
                        data.setAgentreplyinterval((int) ((System.currentTimeMillis() - agentUserTask.getLastgetmessage().getTime()) / 1000));    //坐席上次回复消息的间隔
                        data.setAgentreplytime((int) ((System.currentTimeMillis() - agentUserTask.getLastmessage().getTime()) / 1000));        //坐席回复消息花费时间
                    }
                    agentUserTask.setUserasks(agentUserTask.getUserasks() + 1);    //总咨询记录数量
                    agentUserTask.setAgentreplytime(agentUserTask.getAgentreplytime() + data.getAgentreplyinterval());    //总时长
                    if (agentUserTask.getUserasks() > 0) {
                        agentUserTask.setAvgreplytime(agentUserTask.getAgentreplytime() / agentUserTask.getUserasks());
                    }

                    agentUserTask.setLastmessage(new Date());
                    agentUserTask.setWarnings("0");
                    agentUserTask.setWarningtime(null);

                    /**
                     * 去掉坐席超时回复消息提醒
                     */
                    agentUserTask.setReptime(null);
                    agentUserTask.setReptimes("0");

                    agentUserTask.setLastmsg(data.getMessage().length() > 100 ? data.getMessage().substring(0, 100) : data.getMessage());
                    agentUserTask.setTokenum(agentUserTask.getTokenum() + 1);
                    data.setTokenum(agentUserTask.getTokenum());
                    agentUserTaskRes.save(agentUserTask);
                }
            }

            /**
             * 保存消息
             */
            if (MainContext.MessageTypeEnum.MESSAGE.toString().equals(data.getType())) {
                MainContext.getContext().getBean(ChatMessageRepository.class).save(data);
            }
        }
        if (StringUtils.isNotBlank(data.getUserid()) && MainContext.MessageTypeEnum.MESSAGE.toString().equals(data.getType())) {
            NettyClients.getInstance().publishIMEventMessage(data.getUserid(), MainContext.MessageTypeEnum.MESSAGE.toString(), outMessage);
            if (statusMessage != null) {
                NettyClients.getInstance().publishIMEventMessage(data.getUserid(), MainContext.MessageTypeEnum.STATUS.toString(), statusMessage);
            }
        }
        if (agentUser != null && StringUtils.isNotBlank(agentUser.getAgentno())) {
            // TODO 将消息发送给 坐席
            NettyClients.getInstance().publishAgentEventMessage(agentUser.getAgentno(), MainContext.MessageTypeEnum.MESSAGE.toString(), data);
        }
    }



//    /**
//     * 发送消息
//     *
//     * @param data
//     * @param msgtype
//     */
//    private static void sendMessage(ChatMessage data, String msgtype) {
//        MessageOutContent outMessage = new MessageOutContent();
//
//        outMessage.setMessage(data.getMessage());
//        outMessage.setFilename(data.getFilename());
//        outMessage.setFilesize(data.getFilesize());
//
//
//        outMessage.setMessageType(msgtype);
//        outMessage.setCalltype(MainContext.CallTypeEnum.IN.toString());
//        outMessage.setSnsAccount(null);
//
//        outMessage.setContextid(data.getContextid());
//        outMessage.setFromUser(data.getUserid());
//        outMessage.setToUser(data.getTouser());
//        outMessage.setChannelMessage(data);
//        outMessage.setNickName(data.getUsername());
//        outMessage.setCreatetime(data.getCreatetime());
//
//        if (!StringUtils.isBlank(data.getUserid()) && MainContext.MessageTypeEnum.MESSAGE.toString().equals(data.getType())) {
//            NettyClients.getInstance().publishIMEventMessage(data.getUserid(), MainContext.MessageTypeEnum.MESSAGE.toString(), outMessage);
//        }
//    }

//    /**
//     *
//     * @param message
//     * @param length
//     * @param name
//     * @param channel
//     * @param msgtype
//     * @param userid
//     * @param username
//     * @param appid
//     * @param orgi
//     * @param attachid
//     * @param aiid
//     * @return
//     */
//    public static ChatMessage createRichMediaMessageWithChannel(String message, int length, String name, String channel, String msgtype, String userid, String username, String appid, String orgi, String attachid, String aiid) {
//        ChatMessage data = new ChatMessage();
//        if (!StringUtils.isBlank(userid)) {
//            data.setUserid(userid);
//            data.setUsername(username);
//            data.setTouser(userid);
//            data.setAppid(appid);
//            data.setOrgi(orgi);
//            data.setChannel(channel);
//            data.setMessage(message);
//
//            data.setAiid(aiid);
//
//            data.setFilesize(length);
//            data.setFilename(name);
//            data.setAttachmentid(attachid);
//
//            data.setMsgtype(msgtype);
//
//            data.setType(MainContext.MessageTypeEnum.MESSAGE.toString());
//            createChatbotMessage(data, appid, channel, MainContext.CallTypeEnum.IN.toString(), MainContext.ChatbotItemType.USERINPUT.toString(), msgtype, data.getUserid());
//        }
//        return data;
//    }

}
