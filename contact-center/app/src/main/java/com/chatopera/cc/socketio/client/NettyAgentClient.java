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

import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
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
        // 更新缓存
        MainContext.getCache().putWebIMAgentSocketioSessionId(key, MainContext.SYSTEM_ORGI,
                MainUtils.getContextID(client.getSessionId().toString()));
    }

    public int removeClient(String key, String id) {
        List<SocketIOClient> keyClients = this.getClients(key);
        logger.debug("[removeClient] userId {}, sessionId {}, client size {}", key, id, keyClients.size());

        for (SocketIOClient client : keyClients) {
            if (MainUtils.getContextID(client.getSessionId().toString()).equals(id)) {
                keyClients.remove(client);
                // 更新缓存
                MainContext.getCache().deleteWebIMAgentSocketioSessionId(key, MainContext.SYSTEM_ORGI, id);
                logger.info("[removeClient] socketClient userid {} sessionId {} is removed.", key, id);

                break;
            }
        }
        if (keyClients.size() == 0) {
            logger.debug("[removeClient] 0 clients for userId {} after remove, remove all keys from NettyClientMap", key);
            agentClientsMap.removeAll(key);

//  以下代码打印剩余的SocketIO的连接的信息
//        } else {
//            StringBuffer sb = new StringBuffer();
//            for (SocketIOClient client : keyClients) {
//                sb.append(MainUtils.getContextID(client.getSessionId().toString()));
//                sb.append(", ");
//            }
//
//            logger.debug("[removeClient] still get userId {} remaining clients: {}", key, sb.toString());
        }
        return keyClients.size();
    }

}
