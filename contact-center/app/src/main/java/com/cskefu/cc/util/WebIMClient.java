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

package com.cskefu.cc.util;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class WebIMClient {
    private String userid;
    private String client;
    private SseEmitter sse;
    private String traceid; // 跟踪用户的ID

    public WebIMClient(String userid, String client, SseEmitter sse) {
        this(userid, client, sse, null);
    }

    public WebIMClient(final String userid, final String client, final SseEmitter sse, final String traceid) {
        this.userid = userid;
        this.sse = sse;
        this.client = client;
        this.traceid = traceid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public SseEmitter getSse() {
        return sse;
    }

    public void setSse(SseEmitter sse) {
        this.sse = sse;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getTraceid() {
        return traceid;
    }

    public void setTraceid(String traceid) {
        this.traceid = traceid;
    }
}
