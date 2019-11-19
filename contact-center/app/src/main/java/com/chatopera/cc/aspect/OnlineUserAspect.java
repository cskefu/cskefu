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
import com.chatopera.cc.model.OnlineUser;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OnlineUserAspect {
    private final static Logger logger = LoggerFactory.getLogger(OnlineUserAspect.class);

    @Autowired
    private Cache cache;

    /**
     * 因为会定期从缓存序列化到数据库
     *
     * @param joinPoint
     */
    @Before("execution(* com.chatopera.cc.persistence.repository.OnlineUserRepository.save(..))")
    public void save(final JoinPoint joinPoint) {
        final OnlineUser onlineUser = (OnlineUser) joinPoint.getArgs()[0];
//        logger.info(
//                "[save] put onlineUser id {}, status {}, invite status {}", onlineUser.getId(), onlineUser.getStatus(),
//                onlineUser.getInvitestatus());
        if (StringUtils.isNotBlank(onlineUser.getStatus())) {
            switch (MainContext.OnlineUserStatusEnum.toValue(onlineUser.getStatus())) {
                case OFFLINE:
                    cache.deleteOnlineUserByIdAndOrgi(onlineUser.getId(), onlineUser.getOrgi());
                    break;
                default:
                    cache.putOnlineUserByOrgi(onlineUser, onlineUser.getOrgi());
            }
        }
    }

}
