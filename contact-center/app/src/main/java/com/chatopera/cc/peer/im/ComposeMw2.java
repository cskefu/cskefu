package com.chatopera.cc.peer.im;

import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.peer.PeerContext;
import com.chatopera.cc.socketio.client.NettyClients;
import com.chatopera.compose4j.Functional;
import com.chatopera.compose4j.Middleware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 向访客发送WebIM消息
 */
@Component
public class ComposeMw2 implements Middleware<PeerContext> {

    private final static Logger logger = LoggerFactory.getLogger(
            ComposeMw2.class);

    @Override
    public void apply(final PeerContext ctx, final Functional next) {
        logger.info(
                "[apply] receiverType {}, touser {}, msgType {}",
                ctx.getReceiverType(), ctx.getTouser(), ctx.getMsgType()
                   );

        // 区分接受人类型
        switch (ctx.getReceiverType()) {
            case AGENT:
                // 该消息发送给坐席
                NettyClients.getInstance().publishAgentEventMessage(
                        ctx.getTouser(),
                        ctx.getMsgType().toString(),
                        ctx.getMessage().getChannelMessage(),
                        ctx.isDist());
                ctx.setSent(true);
                break;
            case VISITOR:
                // 通过WebIM发送消息给访客
                if (ctx.getChannel() == MainContext.ChannelType.WEBIM) {
                    ctx.setSent(sendToVisitor(ctx));
                }
                break;
            case CHATBOT:
                // 该消息发送给聊天机器人
                break;
            default:
                logger.info(
                        "[apply] unknown receiverType {}",
                        ctx.getReceiverType().toString());
        }

        next.apply();
    }

    /**
     * 给访客发送消息
     *
     * @param ctx
     * @return
     */
    private boolean sendToVisitor(final PeerContext ctx) {
        switch (ctx.getMsgType()) {
            case STATUS:
            case NEW:
            case END:
                NettyClients.getInstance().publishIMEventMessage(
                        ctx.getTouser(),
                        MainContext.MessageType.STATUS.toString(),
                        ctx.getMessage(),
                        true);
                return true;
            case MESSAGE:
                NettyClients.getInstance().publishIMEventMessage(
                        ctx.getTouser(),
                        ctx.getMsgType().toString(),
                        ctx.getMessage().getChannelMessage(),
                        true);
                return true;
            case SATISFACTION:
                NettyClients.getInstance().publishIMEventMessage(
                        ctx.getTouser(),
                        MainContext.MessageType.SATISFACTION.toString(),
                        ctx.getMessage(),
                        true);
                return true;
        }
        return false;
    }
}
