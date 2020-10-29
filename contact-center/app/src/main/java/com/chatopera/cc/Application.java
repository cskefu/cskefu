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
package com.chatopera.cc;

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.plugins.PluginRegistry;
import com.chatopera.cc.config.AppCtxRefreshEventListener;
import com.chatopera.cc.util.SystemEnvHelper;
import com.chatopera.cc.util.mobile.MobileNumberUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
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
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.MultipartConfigElement;
import java.io.IOException;

@SpringBootApplication
@EnableJpaRepositories("com.chatopera.cc.persistence.repository")
@EnableElasticsearchRepositories("com.chatopera.cc.persistence.es")
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
    static {
        // CRM模块
        if (StringUtils.equalsIgnoreCase(SystemEnvHelper.parseFromApplicationProps("cskefu.modules.contacts"), "true")) {
            MainContext.enableModule(Constants.CSKEFU_MODULE_CONTACTS);
        }

        // 会话监控模块 Customer Chats Audit
        if (StringUtils.equalsIgnoreCase(SystemEnvHelper.parseFromApplicationProps("cskefu.modules.cca"), "true")) {
            MainContext.enableModule(Constants.CSKEFU_MODULE_CCA);
        }

        // 企业聊天模块
        if (StringUtils.equalsIgnoreCase(SystemEnvHelper.parseFromApplicationProps("cskefu.modules.entim"), "true")) {
            MainContext.enableModule(Constants.CSKEFU_MODULE_ENTIM);
        }

        // 数据报表
        if (StringUtils.equalsIgnoreCase(SystemEnvHelper.parseFromApplicationProps("cskefu.modules.report"), "true")) {
            MainContext.enableModule(Constants.CSKEFU_MODULE_REPORT);
        }


    }

    /**
     * Init local resources
     */
    protected static void serve(final String[] args) {
        try {
            MobileNumberUtils.init();
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
        Application.serve(args);
    }
}
