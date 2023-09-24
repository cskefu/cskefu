package com.cskefu;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@Slf4j
@SpringBootApplication
//@EnableDiscoveryClient
public class WebsocketApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(WebsocketApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

@Component
@Slf4j
class SocketIOServerRunner implements CommandLineRunner, DisposableBean {

    @Autowired
    private SocketIOServer socketIOServer;

    @Override
    public void run(String... args) throws Exception {
        socketIOServer.start();
        log.warn("SocketIOServer启动成功");
    }

    @Override
    public void destroy() throws Exception {
        socketIOServer.stop();
        log.warn("SocketIOServer关闭成功");
    }
}
