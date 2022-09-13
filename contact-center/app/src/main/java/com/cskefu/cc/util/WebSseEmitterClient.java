/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications Copyright (C) 2019-2022 Chatopera Inc, <https://www.chatopera.com>
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
package com.cskefu.cc.util;

import com.cskefu.cc.proxy.OnlineUserProxy;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WebSseEmitterClient {
    private final static Logger logger = LoggerFactory.getLogger(WebSseEmitterClient.class);
    private ConcurrentMap<String, WebIMClient> imClientsMap = new ConcurrentHashMap<>();

    public List<WebIMClient> getClients(String userid) {

        Collection<WebIMClient> values = imClientsMap.values();
        List<WebIMClient> clients = new ArrayList<WebIMClient>();
        for (WebIMClient client : values) {
            if (client.getUserid().equals(userid)) {
                clients.add(client);
            }
        }
//        logger.info("[getClients] get clients for userId {}, size {}", userid, clients.size());
        return clients;
    }

    public int size() {
        return imClientsMap.size();
    }

    public void putClient(String userid, WebIMClient client) {
//        logger.info("[putClient] userid {} client {}", userid, client.getClient());
        imClientsMap.put(client.getClient(), client);
    }

    public void removeClient(String userid, String client, boolean timeout) throws Exception {
//        logger.info("[removeClient] remove client {} for userid {}", client, userid);
        List<WebIMClient> keyClients = this.getClients(userid);
        for (int i = 0; i < keyClients.size(); i++) {
            WebIMClient webIMClient = keyClients.get(i);
            if (StringUtils.equals(webIMClient.getClient(), client)) {

                imClientsMap.remove(client);
                keyClients.remove(i);
                break;
            }
        }
        if (keyClients.size() == 0 && timeout == true) {
            OnlineUserProxy.offline(userid, userid);
//            logger.info("[removeClient] set onlineUser {} as offline.", userid);
        }
    }
}
