package com.cskefu.handler;

import com.cskefu.annotations.WebSocketMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
@Slf4j
@WebSocketMapping("/ws/")
public class WelcomeHandler implements WebSocketHandler {

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            HandshakeInfo handshakeInfo = session.getHandshakeInfo();
            return session.send(session.receive().map(msg -> session.textMessage("cskefu-websocket-service: " + localHost.getHostAddress())));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException("出错了！");
        }
    }
}
