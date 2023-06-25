/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2018- Jun. 2023 Chatopera Inc, <https://www.chatopera.com>,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (C) 2017 优客服-多渠道客服系统,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.cskefu.cc.socketio.client;

import com.cskefu.cc.basic.MainUtils;
import com.corundumstudio.socketio.SocketIOClient;
import com.google.common.collect.ArrayListMultimap;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NettyIMClient implements NettyClient {

    private final ArrayListMultimap<String, SocketIOClient> imClientsMap = ArrayListMultimap.create();

    public int size() {
        return imClientsMap.size();
    }

    public List<SocketIOClient> getClients(String key) {
        return imClientsMap.get(key);
    }

    public void putClient(String key, SocketIOClient client) {
        imClientsMap.put(key, client);
    }

    @Override
    public int removeClient(String key, String id) {
        List<SocketIOClient> keyClients = this.getClients(key);
        for (SocketIOClient client : keyClients) {
            if (MainUtils.getContextID(client.getSessionId().toString()).equals(id)) {
                keyClients.remove(client);
                break;
            }
        }
        if (keyClients.size() == 0) {
            imClientsMap.removeAll(key);
        }
        return keyClients.size();
    }

    public Boolean checkClient(String key, String id) {
        List<SocketIOClient> keyClients = this.getClients(key);
        for (SocketIOClient client : keyClients) {
            String sessionId = client.getHandshakeData().getSingleUrlParam("session");
            if (sessionId != null && sessionId.equals(id)) {
                return true;
            }
        }

        return false;
    }
}
