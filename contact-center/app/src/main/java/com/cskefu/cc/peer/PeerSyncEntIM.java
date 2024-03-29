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
package com.cskefu.cc.peer;

import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.model.User;
import com.cskefu.cc.persistence.repository.ChatMessageRepository;
import com.cskefu.cc.persistence.repository.RecentUserRepository;
import com.cskefu.cc.socketio.client.NettyClients;
import com.cskefu.cc.socketio.message.ChatMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PeerSyncEntIM {
    private final static Logger logger = LoggerFactory.getLogger(
            PeerSyncEntIM.class);

    @Autowired
    ChatMessageRepository chatMessageRes;

    @Autowired
    RecentUserRepository recentUserRes;

    public void send(
            final String user,
            final String group,
            final MainContext.MessageType msgType,
            final ChatMessage data
    ) {
        logger.info(
                "[send] userid {}, group {}, msgType {}, outMessage {}", user, group, msgType, data);

        if (data.getType() == null) {
            data.setType("message");
        }

        data.setUserid(user);
//		data.setUsername(name);
        data.setId(MainUtils.getUUID());
        data.setUsession(user);
        data.setCalltype(MainContext.CallType.OUT.toString());

        if (!StringUtils.isBlank(group)) {    //如果是群聊
            data.setContextid(group);
            data.setChatype("group");
            data.setModel("entim");
            chatMessageRes.save(data);
            NettyClients.getInstance().sendEntIMGroupEventMessage(data.getUserid(), group, msgType.toString(), data);
        } else {    //单聊
            data.setContextid(data.getTouser());
            chatMessageRes.save(data);
            ChatMessage outMessage = new ChatMessage();
            BeanUtils.copyProperties(data, outMessage);
            NettyClients.getInstance().sendEntIMEventMessage(data.getUserid(), msgType.toString(), outMessage);    //同时将消息发送给自己
            data.setCalltype(MainContext.CallType.IN.toString());
            data.setContextid(user);
            data.setUserid(data.getTouser());
            data.setId(MainUtils.getUUID());
            chatMessageRes.save(data);    //每条消息存放两条，一个是我的对话记录 ， 另一条是对方的对话历史， 情况当前聊天记录的时候，只清理自己的
            NettyClients.getInstance().sendEntIMEventMessage(data.getTouser(), msgType.toString(), data);    //发送消息给目标用户

            recentUserRes.findByCreaterAndUser(data.getTouser(), new User(user)).ifPresent(u -> {
                u.setNewmsg(u.getNewmsg() + 1);
                if (data.getMessage() != null && data.getMessage().length() > 50) {
                    u.setLastmsg(data.getMessage().substring(0, 50));
                } else {
                    u.setLastmsg(data.getMessage());
                }
                recentUserRes.save(u);
            });

        }
    }
}
