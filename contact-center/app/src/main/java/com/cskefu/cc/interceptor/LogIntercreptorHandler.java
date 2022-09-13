/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
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
package com.cskefu.cc.interceptor;

import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.model.RequestLog;
import com.cskefu.cc.model.User;
import com.cskefu.cc.persistence.repository.RequestLogRepository;
import com.cskefu.cc.util.Menu;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Enumeration;

/**
 * 系统访问记录
 *
 * @author admin
 */
public class LogIntercreptorHandler implements org.springframework.web.servlet.HandlerInterceptor {

    private final static Logger logger = LoggerFactory.getLogger(LogIntercreptorHandler.class);

    private static RequestLogRepository requestLogRes;

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object arg2, ModelAndView arg3) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) arg2;
        Object hander = handlerMethod.getBean();
        RequestMapping obj = handlerMethod.getMethod().getAnnotation(RequestMapping.class);
        if (StringUtils.isNotBlank(request.getRequestURI()) &&
                !(request.getRequestURI().startsWith("/message/ping") || request.getRequestURI().startsWith("/res/css") || request.getRequestURI().startsWith("/error") || request.getRequestURI().startsWith("/im/"))) {
            RequestLog log = new RequestLog();
            log.setEndtime(new Date());

            if (obj != null) {
                log.setName(obj.name());
            }
            log.setMethodname(handlerMethod.toString());
            log.setIp(request.getRemoteAddr());
            if (hander != null) {
                log.setClassname(hander.getClass().toString());
                if (hander instanceof Handler && ((Handler) hander).getStarttime() != 0) {
                    log.setQuerytime(System.currentTimeMillis() - ((Handler) hander).getStarttime());
                }
            }
            log.setUrl(request.getRequestURI());

            log.setHostname(request.getRemoteHost());
            log.setEndtime(new Date());
            log.setType(MainContext.LogType.REQUEST.toString());
            User user = (User) request.getSession(true).getAttribute(Constants.USER_SESSION_NAME);
            if (user != null) {
                log.setUserid(user.getId());
                log.setUsername(user.getUsername());
                log.setUsermail(user.getEmail());
                log.setOrgi(user.getOrgi());
            }
            StringBuffer str = new StringBuffer();
            Enumeration<String> names = request.getParameterNames();
            while (names.hasMoreElements()) {
                String paraName = names.nextElement();
                if (paraName.indexOf("password") >= 0) {
                    str.append(paraName).append("=").append(MainUtils.encryption(request.getParameter(paraName))).append(",");
                } else {
                    str.append(paraName).append("=").append(request.getParameter(paraName)).append(",");
                }
            }

            Menu menu = handlerMethod.getMethod().getAnnotation(Menu.class);
            if (menu != null) {
                log.setFuntype(menu.type());
                log.setFundesc(menu.subtype());
                log.setName(menu.name());
            }

            log.setParameters(str.toString());
            getRequestLogRes().save(log);
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object arg2) {
        HandlerMethod handlerMethod = (HandlerMethod) arg2;
        Object hander = handlerMethod.getBean();
        if (hander instanceof Handler) {
            ((Handler) hander).setStarttime(System.currentTimeMillis());
        }
        return true;
    }

    private static RequestLogRepository getRequestLogRes() {
        if (requestLogRes == null) {
            requestLogRes = MainContext.getContext().getBean(RequestLogRepository.class);
        }
        return requestLogRes;
    }
}
