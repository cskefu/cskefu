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

import com.chatopera.cc.acd.ACDPolicyService;
import com.chatopera.cc.acd.ACDWorkMonitor;
import com.chatopera.cc.acd.basic.ACDComposeContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.model.AgentReport;
import com.chatopera.cc.model.SessionConfig;
import com.chatopera.compose4j.Functional;
import com.chatopera.compose4j.Middleware;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class ACDVisSessionCfgMw implements Middleware<ACDComposeContext> {
    private final static Logger logger = LoggerFactory.getLogger(ACDVisSessionCfgMw.class);

    @Autowired
    private ACDPolicyService acdPolicyService;

    @Autowired
    private ACDWorkMonitor acdWorkMonitor;

    @Override
    public void apply(final ACDComposeContext ctx, final Functional next) {
        SessionConfig sessionConfig = acdPolicyService.initSessionConfig(ctx.getOrganid(),
                ctx.getOrgi());

        ctx.setSessionConfig(sessionConfig);

        // 查询就绪的坐席，如果指定技能组则按照技能组查询
        AgentReport report;
        if (StringUtils.isNotBlank(ctx.getOrganid())) {
            report = acdWorkMonitor.getAgentReport(ctx.getOrganid(), ctx.getOrgi());
        } else {
            report = acdWorkMonitor.getAgentReport(ctx.getOrgi());
        }

        ctx.setAgentReport(report);

        // 不在工作时间段
        if (sessionConfig.isHourcheck() && !MainUtils.isInWorkingHours(sessionConfig.getWorkinghours())) {
            logger.info("[apply] not in working hours");
            ctx.setMessage(sessionConfig.getNotinwhmsg());
        } else if (report.getAgents() == 0) {
            // 没有就绪的坐席
            logger.info("[apply] find no agents, redirect to leave a message.");
            ctx.setNoagent(true);
        } else {
            logger.info("[apply] find agents size {}, allocate agent in next.", report.getAgents());
            // 具备工作中的就绪坐席，进入筛选坐席
            next.apply();
        }
    }
}
