/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.proxy;

import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.util.freeswitch.model.CallCenterAgent;
import org.apache.commons.lang.StringUtils;
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
        List<CallCenterAgent> agentList = new ArrayList<CallCenterAgent>();
        final Map<String, CallCenterAgent> map = MainContext.getCache().findAllCallCenterAgentsByOrgi(MainContext.SYSTEM_ORGI);
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
        List<CallCenterAgent> agentList = new ArrayList<CallCenterAgent>();
        final Map<String, CallCenterAgent> map = MainContext.getCache().findAllCallCenterAgentsByOrgi(MainContext.SYSTEM_ORGI);
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
        List<CallCenterAgent> agentList = new ArrayList<CallCenterAgent>();
        Map<String, CallCenterAgent> map = MainContext.getCache().findAllCallCenterAgentsByOrgi(MainContext.SYSTEM_ORGI);
        for (Map.Entry<String, CallCenterAgent> entry : map.entrySet()) {
            if (StringUtils.equals(entry.getValue().getExtno(), extno)) {
                agentList.add(entry.getValue());
            }
        }
        return agentList;
    }

}
