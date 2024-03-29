/* 
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2019-2022 Chatopera Inc, <https://www.chatopera.com>, 
 * Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package com.cskefu.cc.acd.middleware.visitor;

import com.cskefu.cc.acd.ACDPolicyService;
import com.cskefu.cc.acd.ACDWorkMonitor;
import com.cskefu.cc.acd.basic.ACDComposeContext;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.model.AgentReport;
import com.cskefu.cc.model.SessionConfig;
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
        SessionConfig sessionConfig = acdPolicyService.initSessionConfig(ctx.getOrganid());

        ctx.setSessionConfig(sessionConfig);

        // 查询就绪的坐席，如果指定技能组则按照技能组查询
        AgentReport report;
        if (StringUtils.isNotBlank(ctx.getOrganid())) {
            report = acdWorkMonitor.getAgentReport(ctx.getOrganid());
        } else {
            report = acdWorkMonitor.getAgentReport();
        }

        ctx.setAgentReport(report);

        // 不在工作时间段
        if (sessionConfig.isHourcheck() && !MainUtils.isInWorkingHours(sessionConfig.getWorkinghours())) {
            logger.info("[apply] not in working hours");
            ctx.setMessage(sessionConfig.getNotinwhmsg());
        } else if (report.getAgents() == 0) {
            // 没有就绪的坐席
            if (ctx.getChannelType().equals(MainContext.ChannelType.MESSENGER.toString())) {
                next.apply();
            } else {
                logger.info("[apply] find no agents, redirect to leave a message.");
                ctx.setNoagent(true);
            }
        } else {
            logger.info("[apply] find agents size {}, allocate agent in next.", report.getAgents());
            // 具备工作中的就绪坐席，进入筛选坐席
            next.apply();
        }
    }
}
