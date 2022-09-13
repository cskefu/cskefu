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
package com.cskefu.cc.config;

import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.auth.AuthToken;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ApiRequestMatchingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(ApiRequestMatchingFilter.class);

    private RequestMatcher[] ignoredRequests;
    private static AuthToken authToken;


    public ApiRequestMatchingFilter(RequestMatcher... matcher) {
        this.ignoredRequests = matcher;
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String method = request.getMethod();

        if (!StringUtils.isBlank(method) && method.equalsIgnoreCase("options")) {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "x-requested-with,accept,authorization,content-type");
            response.setHeader("X-Frame-Options", "SAMEORIGIN");
            response.setStatus(HttpStatus.ACCEPTED.value());
        } else {
            boolean matchAnyRoles = false;
            for (RequestMatcher anyRequest : ignoredRequests) {
                if (anyRequest.matches(request)) {
                    matchAnyRoles = true;
                }
            }
            if (matchAnyRoles) {
                String authorization = request.getHeader("authorization");
                if (StringUtils.isBlank(authorization)) {
                    authorization = request.getParameter("authorization");
                }
                if (StringUtils.isNotBlank(authorization) &&
                        getAuthToken().existUserByAuth(authorization)) {
                    chain.doFilter(req, resp);
                } else {
                    response.sendRedirect("/tokens/error");
                }
            } else {
                chain.doFilter(req, resp);
            }
        }
    }

    @Override
    public void destroy() {

    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {

    }

    private static AuthToken getAuthToken() {
        if (authToken == null) {
            authToken = MainContext.getContext().getBean(AuthToken.class);
        }
        return authToken;
    }

}