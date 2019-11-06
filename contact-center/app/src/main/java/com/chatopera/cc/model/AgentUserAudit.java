/*
 * Copyright (C) 2019 Chatopera Inc, <https://www.chatopera.com>
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

package com.chatopera.cc.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 对话监控 关系记录
 */
public class AgentUserAudit implements Serializable {

    private String agentUserId;                  // AgentUserId
    private HashMap<String, String> subscribers; // 订阅者 userId:权限
    private String orgi;                         // 租户ID


    public AgentUserAudit(final String orgi, final String agentUserId, final HashMap<String, String> subscribers) {
        this.orgi = orgi;
        this.subscribers = subscribers;
        this.agentUserId = agentUserId;
    }

    /**
     * 将一个坐席移除订阅者名单
     *
     * @param userId
     */
    public void removeSubscriber(final String userId) {
        if (subscribers != null) {
            subscribers.remove(userId);
        }
    }

    /**
     * 将一个坐席添加到订阅者名单
     *
     * @param userId
     * @param permission 浏览消息：R, 发送消息 S, 转接消息 T
     */
    public void addSubscriber(final String userId, final String permission) {
        if (subscribers == null) {
            subscribers = new HashMap<>();
        }
        subscribers.put(userId, permission);
    }


    /**
     * 将一个坐席添加到订阅者名单 并使用默认权限
     *
     * @param userId
     */
    public void addSubscriber(final String userId) {
        addSubscriber(userId, "RST"); // 具备RST权限
    }


    public String getAgentUserId() {
        return agentUserId;
    }

    public void setAgentUserId(String agentUserId) {
        this.agentUserId = agentUserId;
    }

    public HashMap<String, String> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(HashMap<String, String> subscribers) {
        this.subscribers = subscribers;
    }

    public String getOrgi() {
        return orgi;
    }

    public void setOrgi(String orgi) {
        this.orgi = orgi;
    }

}
