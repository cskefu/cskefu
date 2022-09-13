/*
 * Copyright (C) 2019-2022 Chatopera Inc, <https://www.chatopera.com>
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

package com.cskefu.cc.acd.middleware.visitor;

import com.cskefu.cc.acd.ACDAgentService;
import com.cskefu.cc.acd.ACDPolicyService;
import com.cskefu.cc.acd.basic.ACDComposeContext;
import com.cskefu.cc.model.AgentService;
import com.cskefu.cc.model.AgentStatus;
import com.chatopera.compose4j.Functional;
import com.chatopera.compose4j.Middleware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ACDVisAllocatorMw implements Middleware<ACDComposeContext> {
    private final static Logger logger = LoggerFactory.getLogger(ACDVisAllocatorMw.class);

    @Autowired
    private ACDAgentService acdAgentService;

    @Autowired
    private ACDPolicyService acdPolicyService;

    @Override
    public void apply(final ACDComposeContext ctx, final Functional next) {

        /**
         * 查询条件，当前在线的 坐席，并且 未达到最大 服务人数的坐席
         */
        final List<AgentStatus> agentStatuses = acdPolicyService.filterOutAvailableAgentStatus(
                ctx.getAgentUser(), ctx.getOrgi(), ctx.getSessionConfig());

        /**
         * 处理ACD 的 技能组请求和 坐席请求
         */
        AgentStatus agentStatus = acdPolicyService.filterOutAgentStatusWithPolicies(
                ctx.getSessionConfig(), agentStatuses, ctx.getOrgi(), ctx.getOnlineUserId(), ctx.isInvite());

        AgentService agentService = null;
        try {
            agentService = acdAgentService.resolveAgentService(
                    agentStatus, ctx.getAgentUser(), ctx.getOrgi(), false);
        } catch (Exception ex) {
            logger.warn("[allotAgent] exception: ", ex);
        }

        ctx.setAgentService(agentService);
    }

}
