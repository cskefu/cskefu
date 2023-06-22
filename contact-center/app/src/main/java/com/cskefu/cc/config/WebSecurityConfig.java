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

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.addFilterAfter(tokenInfoTokenFilterSecurityInterceptor(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/**").permitAll())
                .csrf().disable().headers().frameOptions().sameOrigin()
//                .addFilterAfter(csrfHeaderFilter(), BasicAuthenticationFilter.class)        // TODO lecjy
                .and().addFilterAfter(apiTokenFilterSecurityInterceptor(), BasicAuthenticationFilter.class)
                .build();
    }

    @Bean
    public Filter tokenInfoTokenFilterSecurityInterceptor() throws Exception {
        RequestMatcher autconfig = new AntPathRequestMatcher("/autoconfig/**");
        RequestMatcher configprops = new AntPathRequestMatcher("/configprops/**");
        RequestMatcher beans = new AntPathRequestMatcher("/beans/**");
        RequestMatcher dump = new AntPathRequestMatcher("/dump/**");
        RequestMatcher env = new AntPathRequestMatcher("/env/**");
        RequestMatcher info = new AntPathRequestMatcher("/info/**");
        RequestMatcher mappings = new AntPathRequestMatcher("/mappings/**");
        RequestMatcher trace = new AntPathRequestMatcher("/trace/**");
        RequestMatcher druid = new AntPathRequestMatcher("/druid/**");

        /**
         * Bypass actuator api
         */
//        RequestMatcher health = new AntPathRequestMatcher("/health/**");
//        RequestMatcher metrics = new AntPathRequestMatcher("/metrics/**");
//        return new DelegateRequestMatchingFilter(autconfig , configprops , beans , dump , env , health , info , mappings , metrics , trace, druid);
        return new DelegateRequestMatchingFilter(autconfig, configprops, beans, dump, env, mappings, trace, druid);
    }

    @Bean
    public Filter apiTokenFilterSecurityInterceptor() throws Exception {
        return new ApiRequestMatchingFilter(new AntPathRequestMatcher("/api/**"));
    }

    private Filter csrfHeaderFilter() {
        return new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {

                CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                if (csrf != null) {
                    Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
                    String token = csrf.getToken();
                    if (cookie == null || token != null
                            && !token.equals(cookie.getValue())) {

                        // Token is being added to the XSRF-TOKEN cookie.
                        cookie = new Cookie("XSRF-TOKEN", token);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                }
                filterChain.doFilter(request, response);
            }
        };
    }
}
