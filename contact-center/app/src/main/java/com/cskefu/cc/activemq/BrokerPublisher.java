/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd.
 * <https://www.chatopera.com>, Licensed under the Chunsong Public
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2019-2022 Chatopera Inc, All rights reserved.
 * <https://www.chatopera.com>
 */

package com.cskefu.cc.activemq;

import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.PostConstruct;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BrokerPublisher {

    final static private Logger logger = LoggerFactory.getLogger(BrokerPublisher.class);

    @Autowired
    private JmsTemplate jmsTemplate;
    @Value("${cskefu.activemq.destination.prefix}")
    private String prefix;
    @Value("${cskefu.activemq.destination.suffix}")
    private String suffix;

    @PostConstruct
    public void setup() {
        logger.info("[ActiveMQ Publisher] setup successfully.");
    }

    /**
     * 时延消息
     *
     * @param destination
     * @param payload
     * @param delay       available by delayed seconds
     */
    public void send(String destination, final String payload, final boolean isTopic, final int delay) {
        destination = prefix + destination + suffix;
        try {
            if (isTopic) {
                jmsTemplate.convertAndSend(new ActiveMQTopic(destination), payload, m -> {
                    m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 1000L * delay);
                    return m;
                });
            } else {
                // 默认为Queue
                jmsTemplate.convertAndSend(destination, payload, m -> {
                    m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 1000L * delay);
                    return m;
                });
            }
            logger.debug("[send] send succ, dest {}, payload {}", destination, payload);
        } catch (Exception e) {
            logger.warn("[send] error happens.", e);
        }
    }

    /**
     * @param destination
     * @param payload
     * @param isTopic
     */
    public void send(String destination, final String payload, boolean isTopic) {
        destination = prefix + destination + suffix;
        try {
            if (isTopic) {
                jmsTemplate.convertAndSend(new ActiveMQTopic(destination), payload);
            } else {
                // 默认为Queue
                jmsTemplate.convertAndSend(destination, payload);
            }
            logger.debug("[send] send succ, dest {}, payload {}", destination, payload);
        } catch (Exception e) {
            logger.warn("[send] error happens.", e);
        }
    }

    public void send(final String destination, final String payload) {
        send(destination, payload, false);
    }

    public void send(final String destination, final JSONObject payload) {
        send(destination, payload.toJSONString());
    }

    public void send(final String destination, final org.json.JSONObject payload) {
        send(destination, payload.toString());
    }

    public void send(final String destination, final Map<String, String> payload) {
        JSONObject obj = new JSONObject();

        for (Map.Entry<String, String> entry : payload.entrySet()) {
            obj.put(entry.getKey(), entry.getValue());
        }

        send(destination, obj.toJSONString());
    }
}