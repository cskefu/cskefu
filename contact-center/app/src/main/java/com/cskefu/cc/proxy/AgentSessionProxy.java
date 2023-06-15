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
package com.cskefu.cc.proxy;

import com.cskefu.cc.activemq.BrokerPublisher;
import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.cache.Cache;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 坐席的登录会话管理
 * 存入user的session，存储这组信息是为了让客户的账号只能在一个浏览器内登录使用
 * 如果一个用户账号在多个浏览器使用，则登出之前的登录，只保留最后一个登录正常使用
 */
@Component
public class AgentSessionProxy {
    private final static Logger logger = LoggerFactory.getLogger(AgentSessionProxy.class);

    @Autowired
    private Cache cache;

    @Autowired
    private BrokerPublisher brokerPublisher;

    /**
     * 更新User的Session记录
     *
     * @param agentno
     * @param sessionId
     */
    public void updateUserSession(final String agentno, final String sessionId) {
        logger.info("[updateUserSession] agentno {}, sessionId {}", agentno, sessionId);
        if (cache.existUserSessionByAgentno(agentno)) {
            final String preSessionId = cache.findOneSessionIdByAgentno(agentno);
            if (StringUtils.equals(preSessionId, sessionId)) {
                // 现在的session和之前的是一样的，忽略更新
                logger.info(
                        "[updateUserSession] agentno {}, sessionId {} is synchronized, skip update.", agentno,
                        sessionId);
                return;
            }

            if (StringUtils.isNotBlank(preSessionId)) {
                publishAgentLeaveEvent(agentno, sessionId);
            }
        }
        cache.putUserSessionByAgentnoAndSessionId(agentno, sessionId);
    }

    /**
     * 通知浏览器登出
     *
     * @param agentno
     * @param expired 过期的SessionID
     */
    public void publishAgentLeaveEvent(final String agentno, final String expired) {
        //
        logger.info("[publishAgentLeaveEvent] notify logut browser, expired session {}", expired);
        JsonObject payload = new JsonObject();
        payload.addProperty("agentno", agentno);   // 坐席ID
        payload.addProperty("expired", expired);    // 之后的Id
        brokerPublisher.send(Constants.MQ_TOPIC_WEB_SESSION_SSO, payload.toString(), true);
    }


    /**
     * 是否是"不合法"的Session信息
     *
     * @param userid
     * @param session
     * @return
     */
    public boolean isInvalidSessionId(final String userid, final String session) {
//        logger.info("[isInvalidSessionId] userid {}, sesssion {}", userid, session);
        boolean result = true;
        if (cache.existUserSessionByAgentno(userid)) {
            final String curr = cache.findOneSessionIdByAgentno(userid);
//            logger.info("[isInvalidSessionId] current session {}", curr);
            result = !StringUtils.equals(curr, session);
        } else {
            // 不存在该用户的Session
        }
//        logger.info("[isInvalidSessionId] result {}", result);
        return result;
    }

    public void deleteUserSession(final String agentno) {
        if (cache.existUserSessionByAgentno(agentno)) {
            logger.info("[deleteUserSession] agentno {}", agentno);
            cache.deleteUserSessionByAgentno(agentno);
        }
    }
}
