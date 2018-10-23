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
package com.chatopera.cc.app.im.router;

import com.chatopera.cc.app.algorithm.AutomaticServiceDist;
import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.app.basic.MainUtils;
import com.chatopera.cc.app.im.client.NettyClients;
import com.chatopera.cc.app.model.AgentService;
import com.chatopera.cc.app.model.MessageDataBean;
import com.chatopera.cc.app.model.MessageOutContent;

public class MessageRouter extends Router {

    @Override
    public MessageDataBean handler(MessageDataBean inMessage) {
        MessageOutContent outMessage = new MessageOutContent();
        try {
            outMessage.setOrgi(inMessage.getOrgi());
            outMessage.setFromUser(inMessage.getToUser());
            outMessage.setToUser(inMessage.getFromUser());
            outMessage.setId(MainUtils.genID());
            outMessage.setMessageType(inMessage.getMessageType());
            outMessage.setUser(inMessage.getUser());
            outMessage.setAgentUser(inMessage.getAgentUser());
            /**
             * 首先交由 IMR处理 MESSAGE指令 ， 如果当前用户是在 坐席对话列表中， 则直接推送给坐席，如果不在，则执行 IMR
             */
            if (outMessage.getAgentUser() != null && outMessage.getAgentUser().getStatus() != null) {
                if (outMessage.getAgentUser().getStatus().equals(MainContext.AgentUserStatusEnum.INQUENE.toString())) {
                    int queneIndex = AutomaticServiceDist.getQueneIndex(inMessage.getAgentUser().getAgent(), inMessage.getOrgi(), inMessage.getAgentUser().getSkill());
                    if (MainContext.AgentUserStatusEnum.INQUENE.toString().equals(outMessage.getAgentUser().getStatus())) {
                        outMessage.setMessage(AutomaticServiceDist.getQueneMessage(queneIndex, outMessage.getAgentUser().getChannel(), inMessage.getOrgi()));
                    }
                } else if (outMessage.getAgentUser().getStatus().equals(MainContext.AgentUserStatusEnum.INSERVICE.toString())) {

                }
            } else if (MainContext.MessageTypeEnum.NEW.toString().equals(inMessage.getMessageType())) {
                /**
                 * 找到空闲坐席，如果未找到坐席， 则将该用户放入到 排队队列
                 *
                 */
                AgentService agentService = AutomaticServiceDist.allotAgent(inMessage.getAgentUser(), inMessage.getOrgi());
                if (agentService != null && MainContext.AgentUserStatusEnum.INSERVICE.toString().equals(agentService.getStatus())) {
                    outMessage.setMessage(AutomaticServiceDist.getSuccessMessage(agentService, inMessage.getAgentUser().getChannel(), inMessage.getOrgi()));
                    // TODO #111 publish to redis
                    NettyClients.getInstance().publishAgentEventMessage(agentService.getAgentno(), MainContext.MessageTypeEnum.NEW.toString(), inMessage.getAgentUser());
                } else {
                    if (agentService.getQueneindex() > 0) {    //当前有坐席
                        outMessage.setMessage(AutomaticServiceDist.getQueneMessage(agentService.getQueneindex(), inMessage.getAgentUser().getChannel(), inMessage.getOrgi()));
                    } else {
                        outMessage.setMessage(AutomaticServiceDist.getNoAgentMessage(agentService.getQueneindex(), inMessage.getAgentUser().getChannel(), inMessage.getOrgi()));
                    }
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return outMessage;
    }

}
