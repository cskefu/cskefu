/* 
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2019-2022 Chatopera Inc, All rights reserved. 
 * <https://www.chatopera.com>
 */
package com.cskefu.cc.activemq;

import com.cskefu.cc.acd.ACDAgentDispatcher;
import com.cskefu.cc.acd.ACDWorkMonitor;
import com.cskefu.cc.acd.basic.ACDComposeContext;
import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.model.AgentStatus;
import com.cskefu.cc.persistence.repository.AgentStatusRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 处理SocketIO的离线事件
 */
@Component
public class SocketioConnEventSubscription {

    private final static Logger logger = LoggerFactory.getLogger(SocketioConnEventSubscription.class);

    @Autowired
    private ACDAgentDispatcher acdAgentDispatcher;

    @Autowired
    private ACDWorkMonitor acdWorkMonitor;

    @Autowired
    private AgentStatusRepository agentStatusRes;

    @Autowired
    private Cache cache;

    @Value("${application.node.id}")
    private String appNodeId;

    @PostConstruct
    public void setup() {
        logger.info("ActiveMQ Subscription is setup successfully.");
    }

    @JmsListener(destination = "${cskefu.activemq.destination.prefix}" + Constants.WEBIM_SOCKETIO_AGENT_DISCONNECT + "${cskefu.activemq.destination.suffix}", containerFactory = "jmsListenerContainerQueue")
    public void onMessage(final String payload) {
        logger.info("[onMessage] payload {}", payload);

        try {
            JsonParser parser = new JsonParser();
            JsonObject j = parser.parse(payload).getAsJsonObject();
            if (j.has("userId") && j.has("isAdmin")) {
                final AgentStatus agentStatus = cache.findOneAgentStatusByAgentno(
                        j.get("userId").getAsString());
                if (agentStatus != null && (!agentStatus.isConnected())) {
                    /**
                     * 处理该坐席为离线
                     */
                    // 重分配坐席
                    ACDComposeContext ctx = new ACDComposeContext();
                    ctx.setAgentno(agentStatus.getAgentno());
                    acdAgentDispatcher.dequeue(ctx);
                    if (ctx.isResolved()) {
                        logger.info("[onMessage] re-allotAgent for user's visitors successfully.");
                    } else {
                        logger.info("[onMessage] re-allotAgent, error happens.");
                    }

                    // 更新数据库
                    agentStatus.setBusy(false);
                    agentStatus.setStatus(MainContext.AgentStatusEnum.OFFLINE.toString());
                    agentStatus.setUpdatetime(new Date());

                    // 设置该坐席状态为离线
                    cache.deleteAgentStatusByAgentno(agentStatus.getAgentno());
                    agentStatusRes.save(agentStatus);

                    // 记录坐席工作日志
                    acdWorkMonitor.recordAgentStatus(agentStatus.getAgentno(),
                            agentStatus.getUsername(),
                            agentStatus.getAgentno(),
                            j.get("isAdmin").getAsBoolean(),
                            agentStatus.getAgentno(),
                            agentStatus.getStatus(),
                            MainContext.AgentStatusEnum.OFFLINE.toString(),
                            MainContext.AgentWorkType.MEIDIACHAT.toString(),
                            null);
                } else if (agentStatus == null) {
                    // 该坐席已经完成离线设置
                    logger.info("[onMessage] agent is already offline, skip any further operations");
                } else {
                    // 该坐席目前在线，忽略该延迟事件
                    logger.info("[onMessage] agent is online now, ignore this message.");
                }
            }
        } catch (Exception e) {
            logger.error("onMessage", e);
        }
    }
}
