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

import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.model.AgentService;
import com.chatopera.cc.model.AgentUser;
import com.chatopera.cc.persistence.repository.AgentServiceRepository;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ACDChatbotService {
    private final static Logger logger = LoggerFactory.getLogger(ACDChatbotService.class);

    @Autowired
    private AgentServiceRepository agentServiceRes;

    /**
     * 为访客分配机器人客服， ACD策略，此处 AgentStatus 是建议 的 坐席，  如果启用了  历史服务坐席 优先策略， 则会默认检查历史坐席是否空闲，如果空闲，则分配，如果不空闲，则 分配当前建议的坐席
     *
     * @param agentUser
     * @param orgi
     * @return
     * @throws Exception
     */
    public AgentService processChatbotService(final String botName, final AgentUser agentUser, final String orgi) {
        AgentService agentService = new AgentService();    //放入缓存的对象
        Date now = new Date();
        if (StringUtils.isNotBlank(agentUser.getAgentserviceid())) {
            agentService = agentServiceRes.findByIdAndOrgi(agentUser.getAgentserviceid(), orgi);
            agentService.setEndtime(now);
            if (agentService.getServicetime() != null) {
                agentService.setSessiontimes(System.currentTimeMillis() - agentService.getServicetime().getTime());
            }
            agentService.setStatus(MainContext.AgentUserStatusEnum.END.toString());
        } else {
            agentService.setServicetime(now);
            agentService.setLogindate(now);
            agentService.setOrgi(orgi);
            agentService.setOwner(agentUser.getContextid());
            agentService.setSessionid(agentUser.getSessionid());
            agentService.setRegion(agentUser.getRegion());
            agentService.setUsername(agentUser.getUsername());
            agentService.setChannel(agentUser.getChannel());
            if (botName != null) {
                agentService.setAgentusername(botName);
            }

            if (StringUtils.isNotBlank(agentUser.getContextid())) {
                agentService.setContextid(agentUser.getContextid());
            } else {
                agentService.setContextid(agentUser.getSessionid());
            }

            agentService.setUserid(agentUser.getUserid());
            agentService.setAiid(agentUser.getAgentno());
            agentService.setAiservice(true);
            agentService.setStatus(MainContext.AgentUserStatusEnum.INSERVICE.toString());

            agentService.setAppid(agentUser.getAppid());
            agentService.setLeavemsg(false);
        }

        agentServiceRes.save(agentService);
        return agentService;
    }

}
