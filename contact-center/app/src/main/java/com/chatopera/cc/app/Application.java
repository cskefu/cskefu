/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.app;

import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.app.config.StartedEventListener;
import com.chatopera.cc.util.Constants;
import com.chatopera.cc.util.SystemEnvHelper;
import com.chatopera.cc.util.mobile.MobileNumberUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.MultipartConfigElement;
import java.io.IOException;

@SpringBootApplication
@EnableJpaRepositories("com.chatopera.cc.app.persistence.repository")
@EnableElasticsearchRepositories("com.chatopera.cc.app.persistence.es")
@EnableAsync
@EnableTransactionManagement
public class Application {

    @Value("${web.upload-path}")
    private String uploaddir;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String multipartMaxUpload;

    @Value("${spring.servlet.multipart.max-request-size}")
    private String multipartMaxRequest;

    /**
     * 记载模块
     */
    // 外呼模块
    private final static boolean isCalloutModule = SystemEnvHelper.parseModuleFlag("CSKEFU_MODULE_CALLOUT");
    // CRM模块
    private final static boolean isContactsModule = SystemEnvHelper.parseModuleFlag("CSKEFU_MODULE_CONTACTS");
    // 聊天机器人模块
    private final static boolean isChatbotModule = SystemEnvHelper.parseModuleFlag("CSKEFU_MODULE_CHATBOT");

    static {
        // 外呼模块
        if (isCalloutModule) {
            MainContext.model.put(Constants.CSKEFU_MODULE_CALLOUT, true);
        }
        // CRM模块
        if (isContactsModule) {
            MainContext.model.put(Constants.CSKEFU_MODULE_CONTACTS, true);
        }
        // 聊天机器人模块
        if (isChatbotModule) {
            MainContext.model.put(Constants.CSKEFU_MODULE_CHATBOT, true);
        }
    }

    /**
     * Init local resources
     */
    protected static void init() {
        try {
            System.out.println("init mobile number utils ...");
            MobileNumberUtils.init();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(multipartMaxUpload); //KB,MB
        factory.setMaxRequestSize(multipartMaxRequest);
        factory.setLocation(uploaddir);
        return factory.createMultipartConfig();
    }

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {

        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {
                ErrorPage error = new ErrorPage("/error.html");
                container.addErrorPages(error);
            }
        };
    }

    public static void main(String[] args) {
        Application.init();
        SpringApplication app = new SpringApplication(Application.class);
        app.setBannerMode(Banner.Mode.CONSOLE);
        app.setAddCommandLineProperties(false);
        app.addListeners(new StartedEventListener());
        MainContext.setApplicationContext(app.run(args));
    }
}

