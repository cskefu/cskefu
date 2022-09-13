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
package com.cskefu.cc.acd;

import com.cskefu.cc.basic.MainContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Automatic Call Distribution Main Entry
 * ACD服务路由得到子服务
 */
public class ACDServiceRouter {
    private final static Logger logger = LoggerFactory.getLogger(ACDServiceRouter.class);

    private static ACDChatbotService acdChatbotService;

    // 坐席服务
    private static ACDAgentService acdAgentService;

    private static ACDPolicyService acdPolicyService;

    private static ACDWorkMonitor acdWorkMonitor;


    public static ACDPolicyService getAcdPolicyService() {
        if (acdPolicyService == null) {
            acdPolicyService = MainContext.getContext().getBean(ACDPolicyService.class);
        }

        return acdPolicyService;
    }

    public static ACDAgentService getAcdAgentService() {
        if (acdAgentService == null) {
            acdAgentService = MainContext.getContext().getBean(ACDAgentService.class);
        }

        return acdAgentService;
    }


    public static ACDChatbotService getAcdChatbotService() {
        if (acdChatbotService == null) {
            acdChatbotService = MainContext.getContext().getBean(ACDChatbotService.class);
        }
        return acdChatbotService;
    }

    public static ACDWorkMonitor getAcdWorkMonitor() {
        if (acdWorkMonitor == null) {
            acdWorkMonitor = MainContext.getContext().getBean(ACDWorkMonitor.class);
        }
        return acdWorkMonitor;

    }
}
