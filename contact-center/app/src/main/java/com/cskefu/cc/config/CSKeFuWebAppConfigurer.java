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

import com.cskefu.cc.interceptor.*;
import com.cskefu.cc.util.SystemEnvHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CSKeFuWebAppConfigurer implements WebMvcConfigurer {
    private final static Logger logger = LoggerFactory.getLogger(CSKeFuWebAppConfigurer.class);
    private final static String ENABLE_LOG_REQUEST = SystemEnvHelper.parseFromApplicationProps("extras.log.request");

    /**
     * https://www.baeldung.com/spring-cors
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // enables CORS requests from any origin to any endpoint in the application.
        registry.addMapping("/**").allowedOrigins("*");
    }

    @Override
    @SuppressWarnings("deprecation")
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(Boolean.TRUE);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 多个拦截器组成一个拦截器链
        // addPathPatterns 用于添加拦截规则
        // excludePathPatterns 用户排除拦截
        registry.addInterceptor(new UserExperiencePlanInterceptorHandler()).addPathPatterns("/**").excludePathPatterns("/im/**", "/res/image*", "/res/file*", "/cs/**", "/messenger/webhook/*");
        registry.addInterceptor(new UserInterceptorHandler()).addPathPatterns("/**").excludePathPatterns("/login.html", "/im/**", "/res/image*", "/res/file*", "/cs/**", "/messenger/webhook/*");
        registry.addInterceptor(new CrossInterceptorHandler()).addPathPatterns("/**");

        if (StringUtils.equalsIgnoreCase(ENABLE_LOG_REQUEST, "on")) {
            logger.warn("Logging request into DB as in ENV: ENABLE_LOG_REQUEST=on");
            registry.addInterceptor(new RequestLogIntercreptorHandler()).addPathPatterns("/**");
        } else {
            logger.info("Disable Logging request into DB.");
        }

        registry.addInterceptor(new ViewsInterceptorHandler()).addPathPatterns("/**");
    }
}