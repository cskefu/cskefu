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

package com.chatopera.cc.acd.visitor;

import com.chatopera.cc.acd.ACDComposeContext;
import com.chatopera.cc.acd.ACDMessageHelper;
import com.chatopera.cc.acd.ACDQueueService;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.proxy.AgentUserProxy;
import com.chatopera.compose4j.Functional;
import com.chatopera.compose4j.Middleware;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 寻找或为绑定服务访客的坐席，建立双方通话
 */
@Component
public class ACDVisServiceMw implements Middleware<ACDComposeContext> {
    private final static Logger logger = LoggerFactory.getLogger(ACDVisServiceMw.class);

    @Autowired
    private ACDQueueService acdQueueService;

    @Autowired
    private ACDMessageHelper acdMessageHelper;

    @Autowired
    private AgentUserProxy agentUserProxy;

    @Override
    public void apply(final ACDComposeContext ctx, final Functional next) {
        ctx.setMessageType(MainContext.MessageType.STATUS.toString());
        /**
         * 首先交由 IMR处理 MESSAGE指令 ， 如果当前用户是在 坐席对话列表中， 则直接推送给坐席，如果不在，则执行 IMR
         */
        if (StringUtils.isNotBlank(ctx.getAgentUser().getStatus())) {
            // 该AgentUser已经在数据库中
            switch (MainContext.AgentUserStatusEnum.toValue(ctx.getAgentUser().getStatus())) {
                case INQUENE:
                    logger.info("[apply] agent user is in queue");
                    int queueIndex = acdQueueService.getQueueIndex(
                            ctx.getAgentUser().getAgentno(), ctx.getOrgi(),
                            ctx.getOrganid());
                    ctx.setMessage(
                            acdMessageHelper.getQueneMessage(
                                    queueIndex,
                                    ctx.getChannel(),
                                    ctx.getOrgi()));
                    break;
                case INSERVICE:
                    // 该访客与坐席正在服务中，忽略新的连接
                    logger.info(
                            "[apply] agent user {} is in service, userid {}, agentno {}", ctx.getAgentUser().getId(),
                            ctx.getAgentUser().getUserid(), ctx.getAgentUser().getAgentno());
                    break;
                case END:
                    logger.info("[apply] agent user is null or END");
                    // 过滤坐席，获得 Agent Service
                    next.apply();
                    if (ctx.getAgentService() != null) {
                        // 没有得到agent service
                        postResolveAgentService(ctx);
                    }
            }
        } else {
            // 该AgentUser为新建
            // 过滤坐席，获得 Agent Service
            next.apply();
            if (ctx.getAgentService() != null) {
                // 没有得到agent service
                postResolveAgentService(ctx);
            }
        }
    }

    /**
     * 根据AgentService，按照逻辑继续执行
     *
     * @param ctx
     */
    private void postResolveAgentService(final ACDComposeContext ctx) {
        /**
         * 找到空闲坐席，如果未找到坐席，则将该用户放入到 排队队列
         */
        switch (MainContext.AgentUserStatusEnum.toValue(ctx.getAgentService().getStatus())) {
            case INSERVICE:
                ctx.setMessage(
                        acdMessageHelper.getSuccessMessage(
                                ctx.getAgentService(),
                                ctx.getChannel(),
                                ctx.getOrgi()));

                // TODO 判断 INSERVICE 时，agentService 对应的  agentUser
                logger.info(
                        "[apply] agent service: agentno {}, \n agentuser id {} \n user {} \n channel {} \n status {} \n queue index {}",
                        ctx.getAgentService().getAgentno(), ctx.getAgentService().getAgentuserid(),
                        ctx.getAgentService().getUserid(),
                        ctx.getAgentService().getChannel(),
                        ctx.getAgentService().getStatus(),
                        ctx.getAgentService().getQueneindex());

                if (StringUtils.isNotBlank(ctx.getAgentService().getAgentuserid())) {
                    agentUserProxy.findOne(ctx.getAgentService().getAgentuserid()).ifPresent(p -> {
                        ctx.setAgentUser(p);
                    });
                }

                // TODO 如果是 INSERVICE 那么  agentService.getAgentuserid 就一定不能为空？
//                            // TODO 此处需要考虑 agentService.getAgentuserid 为空的情况
//                            // 那么什么情况下，agentService.getAgentuserid为空？
//                            if (StringUtils.isNotBlank(agentService.getAgentuserid())) {
//                                logger.info("[handle] set Agent User with agentUser Id {}", agentService.getAgentuserid());
//                                getAgentUserProxy().findOne(agentService.getAgentuserid()).ifPresent(p -> {
//                                    outMessage.setChannelMessage(p);
//                                });
//                            } else {
//                                logger.info("[handle] agent user id is null.");
//                            }
                break;
            case INQUENE:
                if (ctx.getAgentService().getQueneindex() > 0) {
                    // 当前有坐席，要排队
                    ctx.setMessage(acdMessageHelper.getQueneMessage(
                            ctx.getAgentService().getQueneindex(),
                            ctx.getAgentUser().getChannel(),
                            ctx.getOrgi()));
                } else {
                    // TODO 什么是否返回 noAgentMessage, 是否在是 INQUENE 时 getQueneindex == 0
                    // 当前没有坐席，要留言
                    ctx.setMessage(acdMessageHelper.getNoAgentMessage(
                            ctx.getAgentService().getQueneindex(),
                            ctx.getChannel(),
                            ctx.getOrgi()));
                }
                break;
            case END:
                logger.info("[handler] should not happen for new onlineUser service request.");
            default:
        }
        ctx.setChannelMessage(ctx.getAgentUser());
    }


}
