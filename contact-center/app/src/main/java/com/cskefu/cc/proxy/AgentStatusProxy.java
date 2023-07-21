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

package com.cskefu.cc.proxy;

import com.cskefu.cc.acd.ACDWorkMonitor;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.model.AgentReport;
import com.cskefu.cc.persistence.repository.AgentReportRepository;
import com.corundumstudio.socketio.SocketIONamespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AgentStatusProxy {
    private final static Logger logger = LoggerFactory.getLogger(AgentStatusProxy.class);

    @Autowired
    private AgentReportRepository agentReportRes;

    @Autowired
    private ACDWorkMonitor acdWorkMonitor;

    private SocketIONamespace agentNamespace;

    /**
     * 向所有坐席client通知坐席状态变化
     *
     * @param worktype
     * @param workresult
     * @param dataid
     */
    public void broadcastAgentsStatus(final String worktype, final String workresult, final String dataid) {
        /**
         * 坐席状态改变，通知监测服务
         */
        AgentReport agentReport = acdWorkMonitor.getAgentReport();
        agentReport.setWorktype(worktype);
        agentReport.setWorkresult(workresult);
        agentReport.setDataid(dataid);
        agentReportRes.save(agentReport);
        getAgentNamespace().getBroadcastOperations().sendEvent(
                "status", agentReport);
    }

    private SocketIONamespace getAgentNamespace() {
        if (agentNamespace == null) {
            agentNamespace = MainContext.getContext().getBean("agentNamespace", SocketIONamespace.class);
        }
        return agentNamespace;
    }


}
