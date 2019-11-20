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
package com.chatopera.cc.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
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
    public EmbeddedServletContainerFactory createEmbeddedServletContainerFactory() throws IOException, NoSuchAlgorithmException {
        TomcatEmbeddedServletContainerFactory tomcatFactory = new TomcatEmbeddedServletContainerFactory();
        tomcatFactory.addConnectorCustomizers(new CSKeFuTomcatConnectorCustomizer(maxthread, maxconnections));
        return tomcatFactory;
    }

    class CSKeFuTomcatConnectorCustomizer implements TomcatConnectorCustomizer {
        private Integer maxthread;
        private Integer maxconnection;

        CSKeFuTomcatConnectorCustomizer(Integer maxthread, Integer maxconnection) {
            this.maxthread = maxthread;
            this.maxconnection = maxconnection;
        }

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

