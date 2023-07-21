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
package com.cskefu.cc;

import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.config.AppCtxRefreshEventListener;
import com.cskefu.cc.util.SystemEnvHelper;
import com.cskefu.cc.util.mobile.MobileNumberUtils;
import jakarta.servlet.MultipartConfigElement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.JmsPoolConnectionFactoryFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.unit.DataSize;

import java.io.IOException;

@SpringBootApplication
@EnableJpaRepositories("com.cskefu.cc.persistence.repository")
@EnableTransactionManagement
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Value("${web.upload-path}")
    private String uploaddir;

    @Value("${spring.servlet.multipart.max-file-size}")
    private Long multipartMaxUpload;

    @Value("${spring.servlet.multipart.max-request-size}")
    private Long multipartMaxRequest;

    /**
     * 加载模块
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
            // Tune druid params, https://github.com/cskefu/cskefu/issues/835
            System.setProperty("druid.mysql.usePingMethod", "false");

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

  // TODO lecjy
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(multipartMaxUpload)); //KB,MB
        factory.setMaxRequestSize(DataSize.ofMegabytes(multipartMaxRequest));
        factory.setLocation(uploaddir);
        return factory.createMultipartConfig();
    }

    // TODO lecjy
    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer() {
        return factory -> {
            // 定义404错误页
            HttpStatus notFound = HttpStatus.NOT_FOUND;
            // 定义404错误页
            ErrorPage errorPage = new ErrorPage(notFound, "/error.html");
            // 追加错误页，替换springboot默认的错误页
            factory.addErrorPages(errorPage);
        };
    }

    public static void main(String[] args) {
        try {
            Application.serve(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
