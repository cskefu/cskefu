/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2023 Chatopera Inc, <https://www.chatopera.com>
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
package com.cskefu.cc.config;

import com.cskefu.cc.proxy.UserProxy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    final private static Logger logger = LoggerFactory.getLogger(ApplicationStartupListener.class);

    @Value("${extras.auth.super-admin.pass}")
    private String superAdminPass;

    @Autowired
    private UserProxy userProxy;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        if (StringUtils.isNotBlank(superAdminPass)) {
            logger.warn("Reset Superadmin Password by ENV variable EXTRAS_AUTH_SUPER_ADMIN_PASS=********");
            if (!userProxy.resetAccountPasswordByUsername("admin", superAdminPass)) {
                logger.error("Reset Superadmin Password failure. Check 1) admin user do exist in DB with username admin.");
            }
        }
        return;
    }
}