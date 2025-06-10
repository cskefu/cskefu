/* 
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2019-2022 Chatopera Inc, <https://www.chatopera.com>, 
 * Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.cskefu.cc.activemq;

import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.socketio.client.NettyClients;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class AgentSessionSubscription {
    private final static Logger logger = LoggerFactory.getLogger(AgentSessionSubscription.class);

    /**
     * 接收坐席会话监控消息
     *
     * @param msg
     */
    @JmsListener(destination = "${cskefu.activemq.destination.prefix}" + Constants.MQ_TOPIC_WEB_SESSION_SSO + "${cskefu.activemq.destination.suffix}", containerFactory = "jmsListenerContainerTopic")
    public void onMessage(final String msg) {
        logger.info("[onMessage] payload {}", msg);
        try {
            final JsonObject json = new JsonParser().parse(msg).getAsJsonObject();
            // 把登出消息通知给浏览器
            NettyClients.getInstance().publishLeaveEventMessage(
                    json.get("agentno").getAsString(),
                    json.get("expired").getAsString());
        } catch (Exception e) {
            logger.warn("[onMessage] error", e);
        }
    }


}
