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

import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.model.BlackEntity;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class BlackEntityAspect {

    private final static Logger logger = LoggerFactory.getLogger(BlackEntityAspect.class);

    @Autowired
    private Cache cache;

    @After("execution(* com.chatopera.cc.persistence.repository.BlackListRepository.save(..))")
    public void save(final JoinPoint joinPoint) {
        final BlackEntity blackEntity = (BlackEntity) joinPoint.getArgs()[0];
        logger.info("[save] blackEntity userId {}, orgi {}", blackEntity.getUserid(), blackEntity.getOrgi());
        cache.putBlackEntityByOrgi(blackEntity, blackEntity.getOrgi());
    }

    @After("execution(* com.chatopera.cc.persistence.repository.BlackListRepository.delete(..))")
    public void delete(final JoinPoint joinPoint) {
        final BlackEntity blackEntity = (BlackEntity) joinPoint.getArgs()[0];
        logger.info("[delete] blackEntity userId {}, orgi {}", blackEntity.getUserid(), blackEntity.getOrgi());
        cache.deleteBlackEntityByUserIdAndOrgi(blackEntity.getUserid(), blackEntity.getOrgi());
    }
}
