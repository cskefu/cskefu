package com.cskefu.cc.plugins.messenger;

import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.model.AgentUser;
import com.cskefu.cc.model.PassportWebIMUser;
import com.cskefu.cc.peer.PeerContext;
import com.cskefu.cc.persistence.repository.PassportWebIMUserRepository;
import com.cskefu.cc.socketio.message.ChatMessage;
import com.chatopera.compose4j.Functional;
import com.chatopera.compose4j.Middleware;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Messenger 消息处理
 */
@Component
public class MessengerChannelMessager implements Middleware<PeerContext> {
    private final static Logger logger = LoggerFactory.getLogger(MessengerChannelMessager.class);

    @Autowired
    private PassportWebIMUserRepository onlineUserRes;

    @Autowired
    @Lazy
    private MessengerMessageProxy messengerMessageProxy;

    @Override
    public void apply(final PeerContext ctx, final Functional next) {
        if ((!ctx.isSent()) && ctx.getChannel() == MainContext.ChannelType.MESSENGER) {
            AgentUser agentUser = ctx.getMessage().getAgentUser();

            final PassportWebIMUser passportWebIMUser = onlineUserRes.findOneByUserid(
                    agentUser.getUserid());

            if (passportWebIMUser != null) {
                handle(ctx, passportWebIMUser);
            } else {
                logger.info(
                        "[apply] can not online user or its contactsid with agentUserId {}, userid {}",
                        agentUser.getId(), agentUser.getUserid());
            }
        }
        next.apply();
    }

    /**
     * 处理消息体
     *
     * @param ctx
     * @param passportWebIMUser
     */
    private void handle(final PeerContext ctx, final PassportWebIMUser passportWebIMUser) {
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
                            messengerMessageProxy.sendImage(passportWebIMUser.getAppid(), passportWebIMUser.getUserid(), imgUrl);
                        }
                    }
                } else {
                    messengerMessageProxy.send(passportWebIMUser.getAppid(), passportWebIMUser.getUserid(), document.text());
                }
            } else if (StringUtils.equals(chatMessage.getMsgtype(), MainContext.MediaType.IMAGE.toString())) {
                messengerMessageProxy.sendImage(passportWebIMUser.getAppid(), passportWebIMUser.getUserid(), chatMessage.getMessage());
            }
            logger.info("[apply] message is sent.");
            ctx.setSent(true);
        } else {
            if (StringUtils.isNotBlank(ctx.getMessage().getMessage())) {
                messengerMessageProxy.send(passportWebIMUser.getAppid(), passportWebIMUser.getUserid(), ctx.getMessage().getMessage());
            }
        }
    }
}
