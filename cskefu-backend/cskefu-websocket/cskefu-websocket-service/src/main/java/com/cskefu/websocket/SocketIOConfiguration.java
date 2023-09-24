package com.cskefu.websocket;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Optional;

@Configuration
@Slf4j
public class SocketIOConfiguration {

    @Value("${cskefu.socketio.port:8082}")
    private Integer port;

    @Value("${cskefu.socketio.bossCount:1}")
    private int bossCount;

    @Value("${cskefu.socketio.workCount:0}")
    private int workCount;

    @Value("${cskefu.socketio.upgradeTimeout:1000}")
    private int upgradeTimeout;

    @Value("${cskefu.socketio.pingTimeout:3000}")
    private int pingTimeout;

    @Value("${cskefu.socketio.pingInterval:3000}")
    private int pingInterval;

    @Value("#{'${cskefu.socketio.namespaces:}'.split(',')}")
    private String[] namespaces;

    @Bean
    public SocketIOServer socketIOServer() {
        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setTcpNoDelay(true);
        socketConfig.setSoLinger(0);
        socketConfig.setReuseAddress(true);

        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setSocketConfig(socketConfig);
        config.setPort(port);
        config.setBossThreads(bossCount);
        int threads = Runtime.getRuntime().availableProcessors();
        if (workCount < 1) {
            config.setWorkerThreads(threads);
        } else {
            if (workCount < threads) {
                log.warn("worker threads to low");
            }
            config.setWorkerThreads(workCount);
        }
        config.setWorkerThreads(Runtime.getRuntime().availableProcessors());
        config.setAllowCustomRequests(false);
        config.setUpgradeTimeout(upgradeTimeout);
        config.setPingTimeout(pingTimeout);
        config.setPingInterval(pingInterval);

        final SocketIOServer server = new SocketIOServer(config);

        Optional.ofNullable(namespaces).ifPresent(namespace -> Arrays.stream(namespace).forEach(server::addNamespace));

        config.setAuthorizationListener(new AuthorizationListener() {
            @Override
            public boolean isAuthorized(HandshakeData data) {
                return true;
            }
        });

        server.addConnectListener(client -> {
            log.info("SessionId:  {}", client.getSessionId());
            log.info("RemoteAddress:  {}", client.getRemoteAddress());
            log.info("Transport:  {}", client.getTransport());
        });

        server.addDisconnectListener(client -> {
            log.info("断开连接：{} ", client.getSessionId());
            client.disconnect();
        });

        server.addEventListener("text", Object.class, (client, data, ackSender) -> {
            client.getHandshakeData();
            log.info("客户端：{} 发来消息 {}", client, data);
        });
        return server;
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner() {
        return new SpringAnnotationScanner(socketIOServer());
    }
}
