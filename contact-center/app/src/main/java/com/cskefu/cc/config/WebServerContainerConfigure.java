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

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class WebServerContainerConfigure {

    @Value("${server.threads.max}")
    private Integer maxthread;

    @Value("${server.connection.max}")
    private Integer maxconnections;

    @Value("${web.upload-path}")
    private String path;

    @Bean
    public TomcatServletWebServerFactory createEmbeddedServletContainerFactory() throws IOException, NoSuchAlgorithmException {
        TomcatServletWebServerFactory tomcatFactory = new TomcatServletWebServerFactory();
        tomcatFactory.addConnectorCustomizers(new CSKeFuTomcatConnectorCustomizer(maxthread, maxconnections));
        // Enable cookie value with space
        // https://stackoverflow.com/questions/38687210/error-with-cookie-value-when-adding-a-new-spring-session
        // TODO lecjy
        tomcatFactory.addContextCustomizers(context -> context.setCookieProcessor(new Rfc6265CookieProcessor()));
        return tomcatFactory;
    }

    class CSKeFuTomcatConnectorCustomizer implements TomcatConnectorCustomizer {
        private final Integer maxthread;
        private final Integer maxconnection;

        CSKeFuTomcatConnectorCustomizer(Integer maxthread, Integer maxconnection) {
            this.maxthread = maxthread;
            this.maxconnection = maxconnection;
        }

        @Override
        public void customize(Connector connector) {
            Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
            //设置最大连接数
            protocol.setMaxConnections(maxthread != null ? maxthread : 2000);
            //设置最大线程数
            protocol.setMaxThreads(maxconnection != null ? maxconnection : 2000);
            protocol.setConnectionTimeout(30000);
        }
    }
}

