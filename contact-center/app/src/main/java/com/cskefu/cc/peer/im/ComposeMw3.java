/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd.
 * <https://www.chatopera.com>, Licensed under the Chunsong Public
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cskefu.cc.peer.im;

import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.peer.PeerContext;
import com.cskefu.cc.peer.PeerUtils;
import com.cskefu.cc.proxy.AgentAuditProxy;
import com.cskefu.cc.socketio.message.ChatMessage;
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
