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

package com.chatopera.cc.util;

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
