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

package com.chatopera.cc.acd;

import com.chatopera.cc.acd.visitor.ACDVisAllocatorMw;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.model.*;
import com.chatopera.cc.proxy.AgentUserProxy;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ACDAgentService {
    private final static Logger logger = LoggerFactory.getLogger(ACDAgentService.class);

    @Autowired
    private ACDVisAllocatorMw acdAgentAllocatorMw;

    @Autowired
    private AgentUserProxy agentUserProxy;

    @Autowired
    private ACDPolicyService acdPolicyService;

    @Autowired
    private ACDQueueService acdQueueService;

    /**
     * 为访客分配坐席
     *
     * @param agentUser
     */
    @SuppressWarnings("unchecked")
    public AgentService allotAgent(
            final AgentUser agentUser,
            final String orgi) {
        /**
         * 查询条件，当前在线的 坐席，并且 未达到最大 服务人数的坐席
         */
        final SessionConfig sessionConfig = acdPolicyService.initSessionConfig(orgi);
        List<AgentStatus> agentStatusList = acdAgentAllocatorMw.filterOutAvailableAgentStatus(
                agentUser, orgi, sessionConfig);

        /**
         * 处理ACD 的 技能组请求和 坐席请求
         */
        AgentStatus agentStatus = null;
        AgentService agentService = null;    //放入缓存的对象
        if (agentStatusList.size() > 0) {
            agentStatus = agentStatusList.get(0);
            if (agentStatus.getUsers() >= sessionConfig.getMaxuser()) {
                agentStatus = null;
                /**
                 * 判断当前有多少人排队中 ， 分三种情况：1、请求技能组的，2、请求坐席的，3，默认请求的
                 *
                 */
            }
        }

        try {
            agentService = acdAgentAllocatorMw.processAgentService(agentStatus, agentUser, orgi, false);
            // 处理结果：进入排队队列
            if (StringUtils.equals(MainContext.AgentUserStatusEnum.INQUENE.toString(), agentService.getStatus())) {
                agentService.setQueneindex(
                        acdQueueService.getQueueIndex(agentUser.getAgentno(), orgi, agentUser.getSkill()));
            }
        } catch (Exception ex) {
            logger.warn("[allotAgent] exception: ", ex);
        }
        agentUserProxy.broadcastAgentsStatus(
                orgi, "user", agentService != null && agentService.getStatus().equals(
                        MainContext.AgentUserStatusEnum.INSERVICE.toString()) ? "inservice" : "inquene",
                agentUser.getId()
                                            );
        return agentService;
    }


}
