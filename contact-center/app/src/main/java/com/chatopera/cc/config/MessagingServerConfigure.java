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

import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.exception.InstantMessagingExceptionListener;
import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

@org.springframework.context.annotation.Configuration
public class MessagingServerConfigure {
    @Value("${uk.im.server.host}")
    private String host;

    @Value("${uk.im.server.port}")
    private Integer port;

    @Value("${cs.im.server.ssl.port}")
    private Integer sslPort;

    @Value("${web.upload-path}")
    private String path;

    @Value("${uk.im.server.threads}")
    private String threads;

    private SocketIOServer server;

    @Bean(name = "webimport")
    public Integer getWebIMPort() {
        if (sslPort != null) {
            return sslPort;
        } else {
            return port;
        }
    }

    @Bean
    public SocketIOServer socketIOServer() throws NoSuchAlgorithmException, IOException {
        Configuration config = new Configuration();
//		config.setHostname("localhost");
        config.setPort(port);

//		config.getSocketConfig().setReuseAddress(true);
//		config.setSocketConfig(new SocketConfig());
//		config.setOrigin("*");
        config.setExceptionListener(new InstantMessagingExceptionListener());

        File sslFile = new File(path, "ssl/https.properties");
        if (sslFile.exists()) {
            Properties sslProperties = new Properties();
            FileInputStream in = new FileInputStream(sslFile);
            sslProperties.load(in);
            in.close();
            if (StringUtils.isNotBlank(sslProperties.getProperty("key-store")) && StringUtils.isNotBlank(
                    sslProperties.getProperty("key-store-password"))) {
                config.setKeyStorePassword(MainUtils.decryption(sslProperties.getProperty("key-store-password")));
                InputStream stream = new FileInputStream(
                        new File(path, "ssl/" + sslProperties.getProperty("key-store")));
                config.setKeyStore(stream);
            }
        }


//	    config.setSSLProtocol("https");
        int workThreads = StringUtils.isNotBlank(threads) && threads.matches("[\\d]{1,6}") ? Integer.parseInt(
                threads) : 100;
        config.setWorkerThreads(workThreads);
//		config.setStoreFactory(new HazelcastStoreFactory());
        config.setAuthorizationListener(new AuthorizationListener() {
            public boolean isAuthorized(HandshakeData data) {
                return true;
            }
        });
        config.getSocketConfig().setReuseAddress(true);
        config.getSocketConfig().setSoLinger(0);
        config.getSocketConfig().setTcpNoDelay(true);
        config.getSocketConfig().setTcpKeepAlive(true);

        return server = new SocketIOServer(config);
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
        return new SpringAnnotationScanner(socketServer);
    }

    @PreDestroy
    public void destory() {
        server.stop();
    }
}  