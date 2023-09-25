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

package com.cskefu.cc.aspect;

import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.cache.RedisCommand;
import com.cskefu.cc.cache.RedisKey;
import com.cskefu.cc.exception.BillingResourceException;
import com.cskefu.cc.model.AgentUser;
import com.cskefu.cc.proxy.AgentAuditProxy;
import com.cskefu.cc.proxy.LicenseProxy;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
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

    @Autowired
    private LicenseProxy licenseProxy;

    @Before("execution(* com.cskefu.cc.persistence.repository.AgentUserRepository.save(..))")
    public void beforeSave(final JoinPoint joinPoint) {
        final AgentUser agentUser = (AgentUser) joinPoint.getArgs()[0];

        if (StringUtils.isBlank(agentUser.getId())) {
            logger.info("[beforeSave] agentUser id is blank");

            if (StringUtils.isNotBlank(agentUser.getOpttype()) && StringUtils.equals(MainContext.OptType.CHATBOT.toString(), agentUser.getOpttype())) {
                // 机器人座席支持的对话，跳过计数
                agentUser.setLicenseVerifiedPass(true);
                return;
            }

            // 计数加一
            try {
                licenseProxy.increResourceUsageInMetaKv(MainContext.BillingResource.AGENGUSER, 1);
            } catch (BillingResourceException e) {
                logger.error("[beforeSave] error", e.toString());
            }
        }
    }

    @After("execution(* com.cskefu.cc.persistence.repository.AgentUserRepository.save(..))")
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
        cache.putAgentUser(agentUser);
    }

    @After("execution(* com.cskefu.cc.persistence.repository.AgentUserRepository.delete(..))")
    public void delete(final JoinPoint joinPoint) {
        final AgentUser agentUser = (AgentUser) joinPoint.getArgs()[0];
        logger.info(
                "[delete] agentUser id {}, agentno {}, userId {}", agentUser.getId(), agentUser.getAgentno(),
                agentUser.getUserid());
        cache.deleteAgentUserAuditById(agentUser.getId());
        cache.deleteAgentUserByUserId(agentUser);
    }

    /**
     * 更新内存中的坐席与其服务的访客的集合
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("@annotation(com.cskefu.cc.aspect.AgentUserAspect.LinkAgentUser)")
    public Object LinkAgentUser(ProceedingJoinPoint joinPoint) throws Throwable {
        final AgentUser updated = (AgentUser) joinPoint.getArgs()[0];
        Object proceed = joinPoint.proceed(); // after things are done.
        logger.info(
                "[linkAgentUser] agentUser: status {}, userId {}, agentno {}", updated.getStatus(),
                updated.getUserid(), updated.getAgentno());
        if (StringUtils.equals(updated.getStatus(), MainContext.AgentUserStatusEnum.END.toString())) {
            // 从集合中删除
            redisCommand.removeSetVal(
                    RedisKey.getInServAgentUsersByAgentno(updated.getAgentno()), updated.getUserid());
        } else if (StringUtils.equals(updated.getStatus(), MainContext.AgentUserStatusEnum.INSERVICE.toString())) {
            redisCommand.insertSetVal(
                    RedisKey.getInServAgentUsersByAgentno(updated.getAgentno()), updated.getUserid());
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
