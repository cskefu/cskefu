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

import com.chatopera.compose4j.Functional;
import com.chatopera.compose4j.Middleware;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.model.AgentUserTask;
import com.cskefu.cc.peer.PeerContext;
import com.cskefu.cc.peer.PeerUtils;
import com.cskefu.cc.persistence.repository.AgentUserTaskRepository;
import com.cskefu.cc.persistence.repository.ChatMessageRepository;
import com.cskefu.cc.socketio.message.ChatMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 做发送前的准备工作
 */
@Component
public class ComposeMw1 implements Middleware<PeerContext> {

    private final static Logger logger = LoggerFactory.getLogger(ComposeMw1.class);

    @Autowired
    private ChatMessageRepository chatMessageRes;

    @Autowired
    private AgentUserTaskRepository agentUserTaskRes;

    @Override
    public void apply(final PeerContext ctx, final Functional next) {
        logger.info(
                "[apply] receiverType {}, touser {}, msgType {}", ctx.getReceiverType(), ctx.getTouser(),
                ctx.getMsgType());

        // TODO first fix nickName

        switch (ctx.getReceiverType()) {
            case AGENT:
                // 发送给坐席的消息
                if (ctx.getMsgType() == MainContext.MessageType.MESSAGE) {
                    // 坐席服务数据记录
                    prcessAgentUserTask(ctx);
                }
                next.apply();
                break;
            case VISITOR:
                // 接收者是有效的
                if (StringUtils.isNotBlank(ctx.getTouser())) {
                    next.apply();
                }
                break;
            case CHATBOT:
                break;
            default:
                logger.info("[apply] unknown receiverType {}", ctx.getReceiverType());
        }

        if (ctx.isSent()) {
            /**
             * 保存消息到数据库
             */
            // 因为发送给"坐席"的消息同时包括了"发送给坐席"和"发送给访客"的内容，
            // 所以，只监控坐席的Inbound和Outbound数据就是所有的对话数据了
            switch (ctx.getReceiverType()) {
                case AGENT:
                    // 只保存ChatMessage消息，不保存NEW, END 等状态
                    if (ctx.getMessage().getChannelMessage() instanceof ChatMessage) {
                        final ChatMessage msg = (ChatMessage) ctx.getMessage().getChannelMessage();
                        // 忽略书写中的消息
                        if (!PeerUtils.isMessageInWritting(msg)) {
                            // 消息已经发送，保存到数据库
                            chatMessageRes.save(msg);
                            logger.info("[apply] chat message saved.");
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 管理坐席对话计数
     *
     * @param ctx
     */
    private void prcessAgentUserTask(final PeerContext ctx) {
        AgentUserTask agentUserTask = agentUserTaskRes.getReferenceById(ctx.getMessage().getAgentUser().getId());

        if (agentUserTask != null) {
            final ChatMessage received = (ChatMessage) ctx.getMessage().getChannelMessage();
            if (agentUserTask.getLastgetmessage() != null && agentUserTask.getLastmessage() != null) {
                received.setLastagentmsgtime(agentUserTask.getLastgetmessage());
                received.setLastmsgtime(agentUserTask.getLastmessage());
                received.setAgentreplyinterval(
                        (int) ((System.currentTimeMillis() - agentUserTask.getLastgetmessage().getTime()) / 1000));    //坐席上次回复消息的间隔
                received.setAgentreplytime(
                        (int) ((System.currentTimeMillis() - agentUserTask.getLastmessage().getTime()) / 1000));        //坐席回复消息花费时间
            }

            agentUserTask.setAgentreplys(agentUserTask.getAgentreplys() + 1);    // 总咨询记录数量
            agentUserTask.setAgentreplyinterval(
                    agentUserTask.getAgentreplyinterval() + received.getAgentreplyinterval());    //总时长
            if (agentUserTask.getAgentreplys() > 0) {
                agentUserTask.setAvgreplyinterval(
                        agentUserTask.getAgentreplyinterval() / agentUserTask.getAgentreplys());
            }

            agentUserTask.setLastmsg(
                    received.getMessage().length() > 100 ? received.getMessage().substring(
                            0,
                            100) : received.getMessage());

            if (StringUtils.equals(received.getType(), MainContext.MessageType.MESSAGE.toString())) {
                agentUserTask.setTokenum(agentUserTask.getTokenum() + 1);
            }
            received.setTokenum(agentUserTask.getTokenum());

            agentUserTaskRes.save(agentUserTask);
        }
    }
}
