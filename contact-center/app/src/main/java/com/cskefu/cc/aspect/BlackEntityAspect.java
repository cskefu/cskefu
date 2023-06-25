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

import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.model.BlackEntity;
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

    @After("execution(* com.cskefu.cc.persistence.repository.BlackListRepository.save(..))")
    public void save(final JoinPoint joinPoint) {
        final BlackEntity blackEntity = (BlackEntity) joinPoint.getArgs()[0];
        logger.info("[save] blackEntity userId {}", blackEntity.getUserid());
        cache.putBlackEntity(blackEntity);
    }

    @After("execution(* com.cskefu.cc.persistence.repository.BlackListRepository.delete(..))")
    public void delete(final JoinPoint joinPoint) {
        final BlackEntity blackEntity = (BlackEntity) joinPoint.getArgs()[0];
        logger.info("[delete] blackEntity userId {}", blackEntity.getUserid());
        cache.deleteBlackEntityByUserId(blackEntity.getUserid());
    }
}
