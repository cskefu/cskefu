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
package com.chatopera.cc;

import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.plugins.PluginRegistry;
import com.chatopera.cc.config.AppCtxRefreshEventListener;
import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.util.SystemEnvHelper;
import com.chatopera.cc.util.mobile.MobileNumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.MultipartConfigElement;
import java.io.IOException;

@SpringBootApplication
@EnableJpaRepositories("com.chatopera.cc.persistence.repository")
@EnableElasticsearchRepositories("com.chatopera.cc.persistence.es")
@EnableAsync
@EnableJms
@EnableTransactionManagement
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

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
    private final static boolean isCalloutModule = SystemEnvHelper.parseModuleFlag("cskefu.module.callout");

    // CRM模块
    private final static boolean isContactsModule = SystemEnvHelper.parseModuleFlag("cskefu.module.contacts");

    // 聊天机器人模块
    private final static boolean isChatbotModule = SystemEnvHelper.parseModuleFlag("cskefu.module.chatbot");

    // 访客聊天监控模块
    private final static boolean isCcaModule = SystemEnvHelper.parseModuleFlag("cskefu.module.cca");

    // 企业聊天模块
    private final static boolean isEntImModule = SystemEnvHelper.parseModuleFlag("cskefu.module.entim");

    // 渠道:Skype渠道
    private final static boolean isSkypeModule = SystemEnvHelper.isClassExistByFullName(
            PluginRegistry.PLUGIN_ENTRY_SKYPE);

    static {
        // 外呼模块
        if (isCalloutModule) {
            MainContext.enableModule(Constants.CSKEFU_MODULE_CALLOUT);
        }
        // CRM模块
        if (isContactsModule) {
            MainContext.enableModule(Constants.CSKEFU_MODULE_CONTACTS);
        }
        // 聊天机器人模块
        if (isChatbotModule) {
            MainContext.enableModule(Constants.CSKEFU_MODULE_CHATBOT);
        }

        // skype模块
        if (isSkypeModule) {
            MainContext.enableModule(Constants.CSKEFU_MODULE_SKYPE);
        }

        // 会话监控模块 Customer Chats Audit
        if (isCcaModule) {
            MainContext.enableModule(Constants.CSKEFU_MODULE_CCA);

        }
        // 企业聊天模块
        if (isEntImModule) {
            MainContext.enableModule(Constants.CSKEFU_MODULE_ENTIM);
        }
    }

    /**
     * Init local resources
     */
    protected static void init() {
        try {
            MobileNumberUtils.init();
        } catch (IOException e) {
            logger.error("Application Startup Error", e);
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

        /************************
         *  该APP中加载多个配置文件
         *  http://roufid.com/load-multiple-configuration-files-different-directories-spring-boot/
         ************************/
        SpringApplication app = new SpringApplicationBuilder(Application.class)
                .properties("spring.config.name:application,git")
                .build();

        app.setBannerMode(Banner.Mode.CONSOLE);
        app.setAddCommandLineProperties(false);
        app.addListeners(new AppCtxRefreshEventListener());
        MainContext.setApplicationContext(app.run(args));
    }
}
