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

package com.chatopera.cc.acd.middleware.visitor;

import com.chatopera.cc.acd.ACDQueueService;
import com.chatopera.cc.acd.basic.ACDComposeContext;
import com.chatopera.cc.acd.basic.ACDMessageHelper;
import com.chatopera.cc.basic.MainContext;
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
                                    ctx.getOrganid(),
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
            }
        } else {
            // 该AgentUser为新建
            // 过滤坐席，获得 Agent Service
            next.apply();
        }
    }
}
