package com.cskefu.handler;

import com.cskefu.annotations.WebSocketMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@WebSocketMapping("/ws/message")
public class MessageHandler implements WebSocketHandler {

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        HandshakeInfo handshakeInfo = session.getHandshakeInfo();
        return session.send(session.receive().map(msg -> session.textMessage("服务端：-> " + msg.getPayloadAsText())));
    }
}
