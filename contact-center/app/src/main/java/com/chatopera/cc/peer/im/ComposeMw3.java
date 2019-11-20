package com.chatopera.cc.peer.im;

import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.peer.PeerContext;
import com.chatopera.cc.peer.PeerUtils;
import com.chatopera.cc.proxy.AgentAuditProxy;
import com.chatopera.cc.socketio.message.ChatMessage;
import com.chatopera.compose4j.Functional;
import com.chatopera.compose4j.Middleware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 发送后的工作
 */
@Component
public class ComposeMw3 implements Middleware<PeerContext> {

    private final static Logger logger = LoggerFactory.getLogger(
            ComposeMw3.class);

    @Autowired
    private AgentAuditProxy agentAuditProxy;

    @Override
    public void apply(final PeerContext ctx, final Functional next) {
        logger.info(
                "[apply] receiverType {}, touser {}, msgType {}",
                ctx.getReceiverType(), ctx.getTouser(), ctx.getMsgType()
                   );

        // 处理会话监控
        if (ctx.isSent()) {
            switch (ctx.getReceiverType()) {
                // 发送给坐席的消息成功后，同时也发送给会话监控，同时确保也路由到了会话监控
                case AGENT:
                    sendAgentAuditMessage(ctx);
                    break;
                default:
                    logger.info(
                            "[apply] other ReceiverType {}",
                            ctx.getReceiverType()
                               );
            }
        }

        next.apply();
    }

    /**
     * 发送消息给会话监控
     *
     * @param ctx
     * @return
     */
    private void sendAgentAuditMessage(final PeerContext ctx) {
        boolean send = true;
        if (ctx.getMessage().getChannelMessage() instanceof ChatMessage) {
            final ChatMessage msg = (ChatMessage) ctx.getMessage().getChannelMessage();
            if (PeerUtils.isMessageInWritting(msg)) {
                send = false;
            }
        }

        if (send) {
            switch (ctx.getMsgType()) {
                case TRANSOUT:
                    // 忽略坐席转出事件
                    break;
                default:
                    agentAuditProxy.publishMessage(
                            ctx.getMessage().getAgentUser(),
                            ctx.getMessage().getChannelMessage(),
                            MainContext.MessageType.toValue(
                                    ("audit_" + ctx.getMsgType().toString()))
                                                  );
            }
        }
    }
}
