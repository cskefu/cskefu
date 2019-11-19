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

package com.chatopera.cc.acd;

import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.model.SessionConfig;
import com.chatopera.cc.persistence.repository.SessionConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 坐席自动分配策略集
 */
@Component
public class ACDPolicyService {
    private final static Logger logger = LoggerFactory.getLogger(ACDPolicyService.class);

    @Autowired
    private Cache cache;

    @Autowired
    private SessionConfigRepository sessionConfigRes;

    /**
     * 载入坐席 ACD策略配置
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<SessionConfig> initSessionConfigList() {
        List<SessionConfig> sessionConfigList;
        if ((sessionConfigList = cache.findOneSessionConfigListByOrgi(MainContext.SYSTEM_ORGI)) == null) {
            sessionConfigList = sessionConfigRes.findAll();
            if (sessionConfigList != null && sessionConfigList.size() > 0) {
                cache.putSessionConfigListByOrgi(sessionConfigList, MainContext.SYSTEM_ORGI);
            }
        }
        return sessionConfigList;
    }

    /**
     * 载入坐席 ACD策略配置
     *
     * @param orgi
     * @return
     */
    public SessionConfig initSessionConfig(final String orgi) {
        SessionConfig sessionConfig;
        if ((sessionConfig = cache.findOneSessionConfigByOrgi(orgi)) == null) {
            sessionConfig = sessionConfigRes.findByOrgi(orgi);
            if (sessionConfig == null) {
                sessionConfig = new SessionConfig();
            } else {
                cache.putSessionConfigByOrgi(sessionConfig, orgi);
            }
        }
        return sessionConfig;
    }
}
