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

import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.model.User;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.security.web.util.matcher.RequestMatcher;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DelegateRequestMatchingFilter implements Filter {
    private final RequestMatcher[] ignoredRequests;

    public DelegateRequestMatchingFilter(RequestMatcher... matcher) {
        this.ignoredRequests = matcher;
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        boolean matchAnyRoles = false;
        for (RequestMatcher anyRequest : ignoredRequests) {
            if (anyRequest.matches(request)) {
                matchAnyRoles = true;
            }
        }
        User user = (User) request.getSession().getAttribute(Constants.USER_SESSION_NAME);
        if (matchAnyRoles) {
            if (user != null && (user.isAdmin())) {
                chain.doFilter(req, resp);
            } else {
                // 重定向到 无权限执行操作的页面
                HttpServletResponse response = (HttpServletResponse) resp;
                response.sendRedirect("/?msg=security");
            }
        } else {
            try {
                chain.doFilter(req, resp);
            } catch (ClientAbortException ex) {
                //Tomcat异常，不做处理
            }
        }
    }

    @Override
    public void destroy() {

    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {

    }
}