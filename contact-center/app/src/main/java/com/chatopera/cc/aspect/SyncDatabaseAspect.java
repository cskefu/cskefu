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
package com.chatopera.cc.aspect;

import com.chatopera.cc.model.ESBean;
import com.chatopera.cc.persistence.hibernate.BaseService;
import com.chatopera.cc.util.CskefuList;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.StaleStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class SyncDatabaseAspect {
    private final static Logger logger = LoggerFactory.getLogger(SyncDatabaseAspect.class);

    @Autowired
    private BaseService<?> dbDataRes;

    /**
     * 定义拦截规则：拦截org.springframework.data.elasticsearch.repository。
     */
    @Pointcut("execution(* org.springframework.data.elasticsearch.repository.*.save(*))")
    public void syncSaveEsData() {
    }

    /**
     * 定义拦截规则：拦截org.springframework.data.elasticsearch.repository。
     */
    @Pointcut("execution(* org.springframework.data.elasticsearch.repository.*.delete(*))")
    public void syncDeleteEsData() {
    }

    @SuppressWarnings("unchecked")
    @Around("syncSaveEsData()")
    public void syncSaveEsData(ProceedingJoinPoint pjp) throws Throwable {
        pjp.proceed();
        Object[] args = pjp.getArgs();
        if (args.length == 1) {
            Object data = args[0];
            if (data != null) {
                if (data instanceof CskefuList) {
                    /** 只有一个地方用到，从ES同步数据到MySQL **/
                } else if (data instanceof List) {
                    // TODO 批量建联系人操作会执行这段代码，此处会报错，但是批量更新可以通过
                    dbDataRes.saveOrUpdateAll((List<Object>) data);
                } else {
                    try {
                        // 更新时，执行此代码，但是新建时会报错
                        dbDataRes.saveOrUpdate(data);
                    } catch (StaleStateException ex) {
                        // 报错的情况下，执行此代码
                        dbDataRes.save(data);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Around("syncDeleteEsData()")
    public void syncDeleteEsData(ProceedingJoinPoint pjp) throws Throwable {
        pjp.proceed();
        Object[] args = pjp.getArgs();
        if (args.length == 1) {
            Object data = args[0];
            if (data instanceof List) {
                dbDataRes.deleteAll((List<Object>) data);
            } else {
                if (data instanceof ESBean) {
                    dbDataRes.delete(data);
                } else {
                    dbDataRes.delete(data);
                }
            }
        }
    }
}
