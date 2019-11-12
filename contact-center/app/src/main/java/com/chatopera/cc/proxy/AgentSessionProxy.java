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
package com.chatopera.cc.proxy;

import com.chatopera.cc.activemq.BrokerPublisher;
import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.cache.Cache;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
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
     * @param orgi
     */
    public void updateUserSession(final String agentno, final String sessionId, final String orgi) {
        logger.info("[updateUserSession] agentno {}, sessionId {}, orgi {}", agentno, sessionId, orgi);
        if (cache.existUserSessionByAgentnoAndOrgi(agentno, orgi)) {
            final String preSessionId = cache.findOneSessionIdByAgentnoAndOrgi(agentno, orgi);
            if (StringUtils.equals(preSessionId, sessionId)) {
                // 现在的session和之前的是一样的，忽略更新
                logger.info(
                        "[updateUserSession] agentno {}, sessionId {} is synchronized, skip update.", agentno,
                        sessionId);
                return;
            }

            if (StringUtils.isNotBlank(preSessionId)) {
                publishAgentLeaveEvent(agentno, sessionId, orgi);
            }
        }
        cache.putUserSessionByAgentnoAndSessionIdAndOrgi(agentno, sessionId, orgi);
    }

    /**
     * 通知浏览器登出
     *
     * @param agentno
     * @param expired 过期的SessionID
     * @param orgi
     */
    public void publishAgentLeaveEvent(final String agentno, final String expired, final String orgi) {
        //
        logger.info("[publishAgentLeaveEvent] notify logut browser, expired session {}", expired);
        JsonObject payload = new JsonObject();
        payload.addProperty("agentno", agentno);   // 坐席ID
        payload.addProperty("orgi", orgi);         // 租户Id
        payload.addProperty("expired", expired);    // 之后的Id
        brokerPublisher.send(Constants.MQ_TOPIC_WEB_SESSION_SSO, payload.toString(), true);
    }


    /**
     * 是否是"不合法"的Session信息
     *
     * @param userid
     * @param session
     * @param orgi
     * @return
     */
    public boolean isInvalidSessionId(final String userid, final String session, final String orgi) {
//        logger.info("[isInvalidSessionId] userid {}, sesssion {}", userid, session);
        boolean result = true;
        if (cache.existUserSessionByAgentnoAndOrgi(userid, orgi)) {
            final String curr = cache.findOneSessionIdByAgentnoAndOrgi(userid, orgi);
//            logger.info("[isInvalidSessionId] current session {}", curr);
            result = !StringUtils.equals(curr, session);
        } else {
            // 不存在该用户的Session
        }
//        logger.info("[isInvalidSessionId] result {}", result);
        return result;
    }
}
