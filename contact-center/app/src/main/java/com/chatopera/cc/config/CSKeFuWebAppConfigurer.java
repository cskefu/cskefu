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
package com.chatopera.cc.config;

import com.chatopera.cc.interceptor.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CSKeFuWebAppConfigurer
        extends WebMvcConfigurerAdapter {


    /**
     * https://www.baeldung.com/spring-cors
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // enables CORS requests from any origin to any endpoint in the application.
        registry.addMapping("/**").allowedOrigins("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 多个拦截器组成一个拦截器链
        // addPathPatterns 用于添加拦截规则
        // excludePathPatterns 用户排除拦截
        registry.addInterceptor(new UserExperiencePlanInterceptorHandler()).addPathPatterns("/**").excludePathPatterns("/im/**", "/res/image*", "/res/file*", "/cs/**", "/messenger/webhook/*");
        registry.addInterceptor(new UserInterceptorHandler()).addPathPatterns("/**").excludePathPatterns("/login.html", "/im/**", "/res/image*", "/res/file*", "/cs/**", "/messenger/webhook/*");
        registry.addInterceptor(new CrossInterceptorHandler()).addPathPatterns("/**");
        registry.addInterceptor(new LogIntercreptorHandler()).addPathPatterns("/**");
        registry.addInterceptor(new ViewsInterceptorHandler()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}