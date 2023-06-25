/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2018- Jun. 2023 Chatopera Inc, <https://www.chatopera.com>,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (C) 2017 优客服-多渠道客服系统,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.cskefu.cc.proxy;

import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.util.freeswitch.model.CallCenterAgent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
@Service("callcenterOutboundQueue")
public class CallcenterOutboundProxy {

    /**
     * 为外呼坐席分配名单
     */
    @SuppressWarnings("unchecked")
    public static List<CallCenterAgent> service() {
        List<CallCenterAgent> agentList = new ArrayList<>();
        final Map<String, CallCenterAgent> map = MainContext.getCache().findAllCallCenterAgents();
        for (Map.Entry<String, CallCenterAgent> entry : map.entrySet()) {
            if (StringUtils.equals(entry.getValue().getWorkstatus(), "callout")) {
                agentList.add(entry.getValue());
            }
        }
        return agentList;
    }

    /**
     * 为外呼坐席分配名单
     *
     * @param sip
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<CallCenterAgent> service(String sip) {
        List<CallCenterAgent> agentList = new ArrayList<>();
        final Map<String, CallCenterAgent> map = MainContext.getCache().findAllCallCenterAgents();
        for (Map.Entry<String, CallCenterAgent> entry : map.entrySet()) {
            if (StringUtils.equals(entry.getValue().getSiptrunk(), sip)) {
                agentList.add(entry.getValue());
            }
        }
        return agentList;
    }

    /**
     * 为外呼坐席分配名单
     *
     * @param extno
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<CallCenterAgent> extention(String extno) {
        List<CallCenterAgent> agentList = new ArrayList<>();
        Map<String, CallCenterAgent> map = MainContext.getCache().findAllCallCenterAgents();
        for (Map.Entry<String, CallCenterAgent> entry : map.entrySet()) {
            if (StringUtils.equals(entry.getValue().getExtno(), extno)) {
                agentList.add(entry.getValue());
            }
        }
        return agentList;
    }

}
