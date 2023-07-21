/* 
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2019-Jun. 2023 Chatopera Inc, <https://www.chatopera.com>, 
 * Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.cskefu.cc.socketio.client;

import com.cskefu.cc.basic.MainUtils;
import com.corundumstudio.socketio.SocketIOClient;
import com.google.common.collect.ArrayListMultimap;

import java.util.List;

/**
 * 呼叫中心登录坐席
 *
 * @author iceworld
 */
public class NettyCalloutClient implements NettyClient {

    private final ArrayListMultimap<String, SocketIOClient> _map = ArrayListMultimap.create();

    public List<SocketIOClient> getClients(String key) {
        return _map.get(key);
    }

    public void putClient(String key, SocketIOClient client) {
        _map.put(key, client);
    }

    public int removeClient(String key, String id) {
        List<SocketIOClient> keyClients = this.getClients(key);
        for (SocketIOClient client : keyClients) {
            if (MainUtils.getContextID(client.getSessionId().toString()).equals(id)) {
                keyClients.remove(client);
                break;
            }
        }
        if (keyClients.size() == 0) {
            _map.removeAll(key);
        }
        return keyClients.size();
    }
}
