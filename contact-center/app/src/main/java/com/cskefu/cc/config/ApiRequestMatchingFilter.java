/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2018- Jun. 2023 Chatopera Inc, <https://www.chatopera.com>,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (C) 2017 优客服-多渠道客服系统,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.cskefu.cc.config;

import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.auth.BearerTokenMgr;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.RequestMatcher;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.cskefu.cc.basic.Constants.AUTH_TOKEN_TYPE_BASIC;
import static com.cskefu.cc.basic.Constants.AUTH_TOKEN_TYPE_BEARER;

public class ApiRequestMatchingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(ApiRequestMatchingFilter.class);

    private final RequestMatcher[] ignoredRequests;
    private static BearerTokenMgr bearerTokenMgr;


    public ApiRequestMatchingFilter(RequestMatcher... matcher) {
        this.ignoredRequests = matcher;
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String method = request.getMethod();

        if (StringUtils.isNotBlank(method) && method.equalsIgnoreCase("options")) {
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
                
                if (StringUtils.isNotBlank(authorization)) {
                    // set the default value for backward compatibility as bear token bare metal
                    String authorizationTrimed = authorization;
                    String authorizationTokenType = AUTH_TOKEN_TYPE_BEARER;
                    if (authorization.startsWith(String.format("%s ", AUTH_TOKEN_TYPE_BEARER))) {
                        authorizationTrimed = StringUtils.substring(authorization, 7);
                        authorizationTokenType = AUTH_TOKEN_TYPE_BEARER;
                    } else if (authorization.startsWith(String.format("%s ", AUTH_TOKEN_TYPE_BASIC))) {
                        authorizationTrimed = StringUtils.substring(authorization, 6);
                        authorizationTokenType = AUTH_TOKEN_TYPE_BASIC;
                    }

                    if (StringUtils.isNotBlank(authorizationTrimed)) {
                        switch (authorizationTokenType) {
                            case AUTH_TOKEN_TYPE_BEARER:
                                if (getBearerTokenMgr().existToken(authorizationTrimed)) {
                                    chain.doFilter(req, resp);
                                } else {
                                    response.sendRedirect("/auth/error");
                                }
                                break;
                            case AUTH_TOKEN_TYPE_BASIC:
                                // TODO
                                response.sendRedirect("/auth/error");
                                break;
                            default:
                                response.sendRedirect("/auth/error");
                        }
                    } else {
                        response.sendRedirect("/auth/error");
                    }
                } else {
                    response.sendRedirect("/auth/error");
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

    private static BearerTokenMgr getBearerTokenMgr() {
        if (bearerTokenMgr == null) {
            bearerTokenMgr = MainContext.getContext().getBean(BearerTokenMgr.class);
        }
        return bearerTokenMgr;
    }

}