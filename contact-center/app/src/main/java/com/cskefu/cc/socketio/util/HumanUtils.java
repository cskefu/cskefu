/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
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
package com.cskefu.cc.socketio.util;

import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainContext.ChannelType;
import com.cskefu.cc.basic.MainContext.MessageType;
import com.cskefu.cc.basic.MainContext.ReceiverType;
import com.cskefu.cc.model.AgentService;
import com.cskefu.cc.model.AgentUser;
import com.cskefu.cc.model.AgentUserTask;
import com.cskefu.cc.persistence.repository.AgentServiceRepository;
import com.cskefu.cc.persistence.repository.AgentUserTaskRepository;
import com.cskefu.cc.socketio.message.ChatMessage;
import com.cskefu.cc.socketio.message.Message;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class HumanUtils {
    private final static Logger logger = LoggerFactory.getLogger(HumanUtils.class);
    private static AgentServiceRepository agentServiceRes;
    private static AgentUserTaskRepository agentUserTaskRes;

    /**
     * 发送文本消息
     *
     * @param data
     * @param userid
     */
    public static void processMessage(ChatMessage data, String userid) {
        processMessage(data, MainContext.MediaType.TEXT.toString(), userid);
    }

    /**
     * 发送各种消息的底层方法
     *
     * @param chatMessage
     * @param msgtype
     * @param userid
     */
    protected static void processMessage(final ChatMessage chatMessage, final String msgtype, final String userid) {
        logger.info("[processMessage] userid {}, msgtype {}", userid, msgtype);
        AgentUser agentUser = MainContext.getCache().findOneAgentUserByUserIdAndOrgi(
                userid, Constants.SYSTEM_ORGI).orElse(null);

        Message outMessage = new Message();

        /**
         * 访客的昵称
         */
        // TODO 确定该值代表访客昵称
        String userNickName = (agentUser != null) && StringUtils.isNotBlank(
                agentUser.getNickname()) ? agentUser.getNickname() : "";

        if (agentUser != null && StringUtils.isNotBlank(agentUser.getAgentserviceid())) {
            AgentService agentService = getAgentServiceRes().findOne(
                    agentUser.getAgentserviceid());
            if (StringUtils.isNotBlank(agentService.getUsername())) {
                userNickName = agentService.getUsername();
            }
        }

        // 将访客名称修改为关联联系人的名称
        chatMessage.setUsername(userNickName);

        outMessage.setMessage(chatMessage.getMessage());
        outMessage.setFilename(chatMessage.getFilename());
        outMessage.setFilesize(chatMessage.getFilesize());

        outMessage.setMessageType(msgtype);
        outMessage.setCalltype(MainContext.CallType.IN.toString());
        outMessage.setAgentUser(agentUser);
        outMessage.setSnsAccount(null);

        Message statusMessage = null;
        if (agentUser == null) {
            statusMessage = new Message();
            statusMessage.setMessage(chatMessage.getMessage());
            statusMessage.setMessageType(MainContext.MessageType.STATUS.toString());
            statusMessage.setCalltype(MainContext.CallType.OUT.toString());
            statusMessage.setMessage("当前坐席全忙，请稍候");
        } else {
            chatMessage.setUserid(agentUser.getUserid());
            chatMessage.setTouser(agentUser.getAgentno());
            chatMessage.setAgentuser(agentUser.getId());
            chatMessage.setAgentserviceid(agentUser.getAgentserviceid());
            chatMessage.setAppid(agentUser.getAppid());
            chatMessage.setOrgi(agentUser.getOrgi());
            chatMessage.setMsgtype(msgtype);
            // agentUser作为 session id
            chatMessage.setUsession(agentUser.getUserid());
            chatMessage.setContextid(agentUser.getContextid());
            chatMessage.setCalltype(MainContext.CallType.IN.toString());
            if (StringUtils.isNotBlank(agentUser.getAgentno())) {
                chatMessage.setTouser(agentUser.getAgentno());
            }
            chatMessage.setChannel(agentUser.getChannel());
            outMessage.setContextid(agentUser.getContextid());
            outMessage.setChannel(agentUser.getChannel());
            outMessage.setAgentUser(agentUser);
            outMessage.setCreatetime(Constants.DISPLAY_DATE_FORMATTER.format(chatMessage.getCreatetime()));

            if (StringUtils.equals(chatMessage.getType(), "message")) {
                // 处理超时回复
                AgentUserTask agentUserTask = getAgentUserTaskRes().getOne(agentUser.getId());
                agentUserTask.setLastgetmessage(new Date());
                agentUserTask.setWarnings("1");
                agentUserTask.setWarningtime(null);

                agentUserTask.setReptime(null);
                agentUserTask.setReptimes("0");
                getAgentUserTaskRes().save(agentUserTask);
            }
        }

        outMessage.setChannelMessage(chatMessage);

        // 将消息返送给 访客
        if (StringUtils.isNotBlank(chatMessage.getUserid()) && MainContext.MessageType.MESSAGE.toString().equals(
                chatMessage.getType())) {
            MainContext.getPeerSyncIM().send(ReceiverType.VISITOR, ChannelType.toValue(outMessage.getChannel()),
                    outMessage.getAppid(), MessageType.MESSAGE, chatMessage.getUserid(),
                    outMessage, true);
            if (statusMessage != null) {
                MainContext.getPeerSyncIM().send(ReceiverType.VISITOR, ChannelType.toValue(outMessage.getChannel()),
                        outMessage.getAppid(), MessageType.STATUS, chatMessage.getUserid(),
                        statusMessage, true);
            }
        }

        // 将消息发送给 坐席
        if (agentUser != null && StringUtils.isNotBlank(agentUser.getAgentno())) {
            MainContext.getPeerSyncIM().send(ReceiverType.AGENT,
                    ChannelType.WEBIM,
                    agentUser.getAppid(),
                    MessageType.MESSAGE,
                    agentUser.getAgentno(),
                    outMessage, true);
        }
    }

    private static AgentServiceRepository getAgentServiceRes() {
        if (agentServiceRes == null) {
            agentServiceRes = MainContext.getContext().getBean(AgentServiceRepository.class);
        }
        return agentServiceRes;
    }

    private static AgentUserTaskRepository getAgentUserTaskRes() {
        if (agentUserTaskRes == null) {
            agentUserTaskRes = MainContext.getContext().getBean(AgentUserTaskRepository.class);
        }
        return agentUserTaskRes;
    }
}
