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
package com.chatopera.cc.controller;

import com.chatopera.cc.acd.ACDWorkMonitor;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.model.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.TimeZone;

@Controller
public class ApplicationController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    @Autowired
    private ACDWorkMonitor acdWorkMonitor;

    @Value("${git.build.version}")
    private String appVersionNumber;

    @Value("${git.commit.id.abbrev}")
    private String appVersionAbbrev;

    @Value("${application.build.datestr}")
    private String appBuildDate;

    @Value("${application.customer.entity}")
    private String appCustomerEntity;

    @Autowired
    private Cache cache;

    @RequestMapping("/")
    public ModelAndView admin(HttpServletRequest request) {
//        logger.info("[admin] path {} queryString {}", request.getPathInfo(),request.getQueryString());
        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/index"));
        User logined = super.getUser(request);
        TimeZone timezone = TimeZone.getDefault();

        view.addObject("agentStatusReport", acdWorkMonitor.getAgentReport(logined.getOrgi()));
        view.addObject("tenant", super.getTenant(request));
        view.addObject("istenantshare", super.isEnabletneant());
        view.addObject("timeDifference", timezone.getRawOffset());


        // 增加版本信息
        view.addObject("appBuildDate", appBuildDate);
        view.addObject("appVersionAbbrev", appVersionAbbrev);
        view.addObject("appVersionNumber", appVersionNumber);
        view.addObject("appCustomerEntity", appCustomerEntity);

        if (super.isEnabletneant()) {
            // 多租户启用 非管理员 一定要选择租户才能进入界面
            if (!logined.isAdmin() && StringUtils.isNotBlank(
                    logined.getOrgid()) && super.isTenantconsole() && MainContext.SYSTEM_ORGI.equals(
                    logined.getOrgi())) {
                view = request(super.createRequestPageTempletResponse("redirect:/apps/tenant/index"));
            }
            if (StringUtils.isBlank(logined.getOrgid())) {
                view = request(super.createRequestPageTempletResponse("redirect:/apps/organization/add.html"));
            }
        }
        view.addObject("agentStatus", cache.findOneAgentStatusByAgentnoAndOrig(logined.getId(), logined.getOrgi()));
        return view;
    }

    @RequestMapping("/lazyAgentStatus")
    public ModelAndView lazyAgentStatus(HttpServletRequest request) {
        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/index"));
        return view;
    }

}