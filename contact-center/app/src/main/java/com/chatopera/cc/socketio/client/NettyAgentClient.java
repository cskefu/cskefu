/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chatopera.cc.socketio.client;

import com.corundumstudio.socketio.SocketIOClient;
import com.google.common.collect.ArrayListMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NettyAgentClient implements NettyClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyAgentClient.class);

    private ArrayListMultimap<String, SocketIOClient> agentClientsMap = ArrayListMultimap.create();

    public List<SocketIOClient> getClients(String key) {
        return agentClientsMap.get(key);
    }

    public void putClient(String key, SocketIOClient client) {
//        logger.info("[putClient] userId {}", key);
        agentClientsMap.put(key, client);
//        // 更新缓存
//        MainContext.getCache().putWebIMAgentSocketioSessionId(key, MainContext.SYSTEM_ORGI,
//                                                              MainUtils.getContextID(client.getSessionId().toString()));
    }

    @Deprecated
    public int removeClient(String key, String id) {
        logger.warn("[removeClient] should not happen, call NettClients.removeClient instead.");
        return 0;
    }


    public void removeAll(final String key) {
        agentClientsMap.removeAll(key);
    }

}
