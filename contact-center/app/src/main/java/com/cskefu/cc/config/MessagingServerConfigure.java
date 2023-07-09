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

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.cskefu.cc.exception.InstantMessagingExceptionListener;
import jakarta.annotation.PreDestroy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

@org.springframework.context.annotation.Configuration
public class MessagingServerConfigure {
    @Value("${uk.im.server.host}")
    private String host;

    @Value("${uk.im.server.port}")
    private Integer port;

    @Value("${web.upload-path}")
    private String path;

    @Value("${server.ssl.key-store}")
    private String keyStore;
//
//    @Value("${server.ssl.key-store-type}")
//    private String keyStoreType;

    @Value("${server.ssl.key-store-password}")
    private String keyStorepassword;

    @Value("${uk.im.server.threads}")
    private String threads;

    private SocketIOServer server;

    @Bean(name = "webimport")
    public Integer getWebIMPort() {
        return port;
    }

    @Bean
    public SocketIOServer socketIOServer() throws NoSuchAlgorithmException, IOException {
        Configuration config = new Configuration();
        //解决对此重启服务时，netty端口被占用问题
        com.corundumstudio.socketio.SocketConfig tmpConfig = new com.corundumstudio.socketio.SocketConfig();
        tmpConfig.setReuseAddress(true);
        config.setSocketConfig(tmpConfig);

//		config.setHostname(host);
        config.setPort(port);

//		config.getSocketConfig().setReuseAddress(true);
//		config.setSocketConfig(new SocketConfig());
//		config.setOrigin("*");
        config.setExceptionListener(new InstantMessagingExceptionListener());

//	    config.setSSLProtocol("https");
        int workThreads = StringUtils.isNotBlank(threads) && threads.matches("[\\d]{1,6}") ? Integer.parseInt(threads) : 100;
        config.setWorkerThreads(workThreads);
//		config.setStoreFactory(new HazelcastStoreFactory());
        config.setAuthorizationListener(data -> true);
        config.getSocketConfig().setReuseAddress(true);
        config.getSocketConfig().setSoLinger(0);
        config.getSocketConfig().setTcpNoDelay(true);
        config.getSocketConfig().setTcpKeepAlive(true);

        //设置https
        InputStream keyStoreStream = this.getClass().getResourceAsStream("/" + keyStore.split(":")[1]);  // 读取证书文件流
        config.setKeyStore(keyStoreStream);  // 设置证书文件
        config.setKeyStorePassword(keyStorepassword);  // 设置证书密码
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