package com.cskefu.cc.plugins.messenger;

import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.model.AgentUser;
import com.cskefu.cc.model.OnlineUser;
import com.cskefu.cc.peer.PeerContext;
import com.cskefu.cc.persistence.repository.OnlineUserRepository;
import com.cskefu.cc.socketio.message.ChatMessage;
import com.chatopera.compose4j.Functional;
import com.chatopera.compose4j.Middleware;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Messenger 消息处理
 */
@Component
public class MessengerChannelMessager implements Middleware<PeerContext> {
    private final static Logger logger = LoggerFactory.getLogger(MessengerChannelMessager.class);

    @Autowired
    private OnlineUserRepository onlineUserRes;

    @Autowired
    private MessengerMessageProxy messengerMessageProxy;

    @Override
    public void apply(final PeerContext ctx, final Functional next) {
        if ((!ctx.isSent()) && ctx.getChannel() == MainContext.ChannelType.MESSENGER) {
            AgentUser agentUser = ctx.getMessage().getAgentUser();

            final OnlineUser onlineUser = onlineUserRes.findOneByUseridAndOrgi(
                    agentUser.getUserid(), agentUser.getOrgi());

            if (onlineUser != null) {
                handle(ctx, onlineUser);
            } else {
                logger.info(
                        "[apply] can not online user or its contactsid with agentUserId {}, userid {} and orgi {}",
                        agentUser.getId(), agentUser.getUserid(), agentUser.getOrgi());
            }
        }
        next.apply();
    }

    /**
     * 处理消息体
     *
     * @param ctx
     * @param onlineUser
     */
    private void handle(final PeerContext ctx, final OnlineUser onlineUser) {
        if (ctx.getMessage().getChannelMessage() instanceof ChatMessage) {
            final ChatMessage chatMessage = (ChatMessage) ctx.getMessage().getChannelMessage();
            logger.info(
                    "[apply] chat message type {}, content {}", chatMessage.getMsgtype(),
                    chatMessage.getMessage());
            if (StringUtils.equals(chatMessage.getMsgtype(), MainContext.MediaType.TEXT.toString())) {
                Document document = Jsoup.parse(chatMessage.getMessage());
                Elements pngs = document.select("img[src]");
                if (pngs.size() > 0) {
                    for (Element element : pngs) {
                        String imgUrl = element.attr("src");
                        if (StringUtils.isNotBlank(imgUrl)) {
                            messengerMessageProxy.sendImage(onlineUser.getAppid(), onlineUser.getUserid(), imgUrl);
                        }
                    }
                } else {
                    messengerMessageProxy.send(onlineUser.getAppid(), onlineUser.getUserid(), document.text());
                }
            } else if (StringUtils.equals(chatMessage.getMsgtype(), MainContext.MediaType.IMAGE.toString())) {
                messengerMessageProxy.sendImage(onlineUser.getAppid(), onlineUser.getUserid(), chatMessage.getMessage());
            }
            logger.info("[apply] message is sent.");
            ctx.setSent(true);
        } else {
            if (StringUtils.isNotBlank(ctx.getMessage().getMessage())) {
                messengerMessageProxy.send(onlineUser.getAppid(), onlineUser.getUserid(), ctx.getMessage().getMessage());
            }
        }
    }
}
