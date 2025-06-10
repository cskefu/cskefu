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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.persistence.repository.BlackListRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * 访客黑名单
 */
@Component
public class BlackListEventSubscription {
    private final static Logger logger = LoggerFactory.getLogger(BlackListEventSubscription.class);

    @Autowired
    private Cache cache;

    @Autowired
    private BlackListRepository blackListRes;

    /**
     * 拉黑访客到达拉黑时间后，从黑名单中移除
     *
     * @param payload
     */
    @JmsListener(destination = "${cskefu.activemq.destination.prefix}" + Constants.WEBIM_SOCKETIO_ONLINE_USER_BLACKLIST + "${cskefu.activemq.destination.suffix}", containerFactory = "jmsListenerContainerQueue")
    public void onMessage(final String payload) {
        logger.info("[onMessage] payload {}", payload);

        try {
            final JSONObject json = JSON.parseObject(payload);
            final String userId = json.getString("userId");

            if (StringUtils.isNotBlank(userId)) {
                cache.findOneBlackEntityByUserId(userId).ifPresent(blackListRes::delete);
            } else {
                logger.warn("[onMessage] error: invalid payload");
            }
        } catch (Exception e) {
            logger.error("[onMessage] error", e);
        }
    }
}
