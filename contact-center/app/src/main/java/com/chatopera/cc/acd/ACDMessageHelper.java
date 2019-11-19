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
import com.chatopera.cc.model.SessionConfig;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ACDMessageHelper {
    private final static Logger logger = LoggerFactory.getLogger(ACDMessageHelper.class);

    @Autowired
    private ACDPolicyService acdPolicyService;

    /**
     * 通知消息内容：分配到坐席
     *
     * @param agentService
     * @param channel
     * @param orgi
     * @return
     */
    public String getSuccessMessage(AgentService agentService, String channel, String orgi) {
        String queneTip = "<span id='agentno'>" + agentService.getAgentusername() + "</span>";
        if (!MainContext.ChannelType.WEBIM.toString().equals(channel)) {
            queneTip = agentService.getAgentusername();
        }
        SessionConfig sessionConfig = acdPolicyService.initSessionConfig(orgi);
        String successMsg = "坐席分配成功，" + queneTip + "为您服务。";
        if (StringUtils.isNotBlank(sessionConfig.getSuccessmsg())) {
            successMsg = sessionConfig.getSuccessmsg().replaceAll("\\{agent\\}", queneTip);
        }
        return successMsg;
    }

    /**
     * 通知消息内容：和坐席断开
     *
     * @param channel
     * @param orgi
     * @return
     */
    public String getServiceFinishMessage(String channel, String orgi) {
        SessionConfig sessionConfig = acdPolicyService.initSessionConfig(orgi);
        String queneTip = "坐席已断开和您的对话";
        if (StringUtils.isNotBlank(sessionConfig.getFinessmsg())) {
            queneTip = sessionConfig.getFinessmsg();
        }
        return queneTip;
    }


    /**
     * 通知消息内容：和坐席断开，刷新页面
     *
     * @param channel
     * @param orgi
     * @return
     */
    public String getServiceOffMessage(String channel, String orgi) {
        SessionConfig sessionConfig = acdPolicyService.initSessionConfig(orgi);
        String queneTip = "坐席已断开和您的对话，刷新页面为您分配新的坐席";
        if (StringUtils.isNotBlank(sessionConfig.getFinessmsg())) {
            queneTip = sessionConfig.getFinessmsg();
        }
        return queneTip;
    }

    public String getNoAgentMessage(int queneIndex, String channel, String orgi) {
        if (queneIndex < 0) {
            queneIndex = 0;
        }
        String queneTip = "<span id='queneindex'>" + queneIndex + "</span>";
        if (!MainContext.ChannelType.WEBIM.toString().equals(channel)) {
            queneTip = String.valueOf(queneIndex);
        }
        SessionConfig sessionConfig = acdPolicyService.initSessionConfig(orgi);
        String noAgentTipMsg = "坐席全忙，已进入等待队列，您也可以在其他时间再来咨询。";
        if (StringUtils.isNotBlank(sessionConfig.getNoagentmsg())) {
            noAgentTipMsg = sessionConfig.getNoagentmsg().replaceAll("\\{num\\}", queneTip);
        }
        return noAgentTipMsg;
    }

    public String getQueneMessage(int queneIndex, String channel, String orgi) {

        String queneTip = "<span id='queneindex'>" + queneIndex + "</span>";
        if (!MainContext.ChannelType.WEBIM.toString().equals(channel)) {
            queneTip = String.valueOf(queneIndex);
        }
        SessionConfig sessionConfig = acdPolicyService.initSessionConfig(orgi);
        String agentBusyTipMsg = "正在排队，请稍候,在您之前，还有  " + queneTip + " 位等待用户。";
        if (StringUtils.isNotBlank(sessionConfig.getAgentbusymsg())) {
            agentBusyTipMsg = sessionConfig.getAgentbusymsg().replaceAll("\\{num\\}", queneTip);
        }
        return agentBusyTipMsg;
    }

}
