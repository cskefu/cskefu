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

package com.chatopera.cc.aspect;

import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.cache.RedisCommand;
import com.chatopera.cc.cache.RedisKey;
import com.chatopera.cc.model.AgentUser;
import com.chatopera.cc.proxy.AgentAuditProxy;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Aspect
@Component
public class AgentUserAspect {

    private final static Logger logger = LoggerFactory.getLogger(AgentUserAspect.class);

    @Autowired
    private Cache cache;

    @Autowired
    private RedisCommand redisCommand;

    @Autowired
    private AgentAuditProxy agentAuditProxy;

    @After("execution(* com.chatopera.cc.persistence.repository.AgentUserRepository.save(..))")
    public void save(final JoinPoint joinPoint) {
        final AgentUser agentUser = (AgentUser) joinPoint.getArgs()[0];
        logger.info(
                "[save] agentUser id {}, agentno {}, userId {}, status {}", agentUser.getId(), agentUser.getAgentno(),
                agentUser.getUserid(), agentUser.getStatus());

        if (StringUtils.isBlank(agentUser.getId())
                || StringUtils.isBlank(agentUser.getUserid())) {
            return;
        }

        // 更新坐席监控信息
        agentAuditProxy.updateAgentUserAudits(agentUser);

        // 同步缓存
        cache.putAgentUserByOrgi(agentUser, agentUser.getOrgi());
    }

    @After("execution(* com.chatopera.cc.persistence.repository.AgentUserRepository.delete(..))")
    public void delete(final JoinPoint joinPoint) {
        final AgentUser agentUser = (AgentUser) joinPoint.getArgs()[0];
        logger.info(
                "[delete] agentUser id {}, agentno {}, userId {}", agentUser.getId(), agentUser.getAgentno(),
                agentUser.getUserid());
        cache.deleteAgentUserAuditByOrgiAndId(agentUser.getOrgi(), agentUser.getId());
        cache.deleteAgentUserByUserIdAndOrgi(agentUser, agentUser.getOrgi());
    }

    /**
     * 更新内存中的坐席与其服务的访客的集合
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("@annotation(AgentUserAspect.LinkAgentUser)")
    public Object LinkAgentUser(ProceedingJoinPoint joinPoint) throws Throwable {
        final AgentUser updated = (AgentUser) joinPoint.getArgs()[0];
        final String orgi = (String) joinPoint.getArgs()[1];
        Object proceed = joinPoint.proceed(); // after things are done.
        logger.info(
                "[linkAgentUser] agentUser: status {}, userId {}, agentno {}, orgi {}", updated.getStatus(),
                updated.getUserid(), updated.getAgentno(), orgi);
        if (StringUtils.equals(updated.getStatus(), MainContext.AgentUserStatusEnum.END.toString())) {
            // 从集合中删除
            redisCommand.removeSetVal(
                    RedisKey.getInServAgentUsersByAgentnoAndOrgi(updated.getAgentno(), orgi), updated.getUserid());
        } else if (StringUtils.equals(updated.getStatus(), MainContext.AgentUserStatusEnum.INSERVICE.toString())) {
            redisCommand.insertSetVal(
                    RedisKey.getInServAgentUsersByAgentnoAndOrgi(updated.getAgentno(), orgi), updated.getUserid());
        } else if (StringUtils.equals(updated.getStatus(), MainContext.AgentUserStatusEnum.INQUENE.toString())) {
            logger.info("[linkAgentUser] ignored inque agent user, haven't resolve one agent yet.");
        } else {
            logger.warn("[linkAgentUser] unexpected condition.");
        }
        return proceed;
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface LinkAgentUser {
    }
}
