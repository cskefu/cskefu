package com.chatopera.cc.peer;

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.socketio.message.ChatMessage;
import org.apache.commons.lang.StringUtils;

public class PeerUtils {

    /**
     * 过滤书写中的消息
     *
     * @param chatMessage
     * @return
     */
    public static boolean isMessageInWritting(final ChatMessage chatMessage) {
        return StringUtils.equals(
                chatMessage.getType(), Constants.IM_MESSAGE_TYPE_WRITING);
    }
}
