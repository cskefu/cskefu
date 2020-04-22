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
package com.chatopera.cc.interceptor;

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.RequestLog;
import com.chatopera.cc.model.User;
import com.chatopera.cc.persistence.repository.RequestLogRepository;
import com.chatopera.cc.util.Menu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
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
@Slf4j
@Component
@RequiredArgsConstructor
public class LogInterceptorHandler implements org.springframework.web.servlet.HandlerInterceptor {

    @NonNull
    private final RequestLogRepository requestLogRes;

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler,
                           ModelAndView modelAndView) {
        if (!(handler instanceof HandlerMethod)) {
            log.debug("Request {} invoked with handler {}", request.getServletPath(), handler.getClass());
            return;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Object actualHandler = handlerMethod.getBean();
        RequestMapping obj = handlerMethod.getMethod().getAnnotation(RequestMapping.class);
        if (StringUtils.isNotBlank(request.getRequestURI()) && !(request.getRequestURI().startsWith("/message/ping") || request.getRequestURI().startsWith("/res/css") || request.getRequestURI().startsWith("/error") || request.getRequestURI().startsWith("/im/"))) {
            RequestLog log = new RequestLog();
            log.setEndtime(new Date());

            if (obj != null) {
                log.setName(obj.name());
            }
            log.setMethodname(handlerMethod.toString());
            log.setIp(request.getRemoteAddr());
            log.setClassname(actualHandler.getClass().toString());
            if (actualHandler instanceof Handler && ((Handler) actualHandler).getStartTime() != 0) {
                log.setQuerytime(System.currentTimeMillis() - ((Handler) actualHandler).getStartTime());
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
            StringBuilder str = new StringBuilder();
            Enumeration<String> names = request.getParameterNames();
            while (names.hasMoreElements()) {
                String paraName = names.nextElement();
                if (paraName.contains("password")) {
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
            requestLogRes.save(log);
        }
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            log.debug("Request {} invoked with handler {}", request.getServletPath(), handler.getClass());
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Object actualHandler = handlerMethod.getBean();
        if (actualHandler instanceof Handler) {
            ((Handler) actualHandler).setStartTime(System.currentTimeMillis());
        }
        return true;
    }
}
