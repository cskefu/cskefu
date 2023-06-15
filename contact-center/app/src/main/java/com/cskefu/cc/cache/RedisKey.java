/*
 * Copyright (C) 2019-2022 Chatopera Inc, <https://www.chatopera.com>
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
package com.cskefu.cc.cache;

import com.cskefu.cc.basic.MainContext;

public class RedisKey {

    public static final String CACHE_SESSIONS = "sso";

    /*********************
     *
     * 以下为Redis的常用KEY管理
     *
     *********************/

    // AGENT STATUS 相关

    /**
     * 获得坐席列表指定字符串状态的KEY
     *
     * @return
     */
    public static String getAgentStatusHashKeyByStatusStr(final String status) {
        StringBuffer sb = new StringBuffer();
        sb.append("agent:status:");
        sb.append(status);
        return sb.toString();
    }

    /**
     * 就绪的客服列表KEY
     *
     * @return
     */
    public static String getAgentStatusReadyHashKey() {
        return getAgentStatusHashKeyByStatusStr(MainContext.AgentStatusEnum.READY.toString());
    }

    /**
     * 未就绪的坐席
     *
     * @return
     */
    public static String getAgentStatusNotReadyHashKey() {
        return getAgentStatusHashKeyByStatusStr(MainContext.AgentStatusEnum.NOTREADY.toString());
    }


    // AGENT USER 相关

    /**
     * 获得坐席访客关联列表指定字符串状态的KEY
     *
     * @param status
     * @return
     */
    public static String getAgentUserHashKeyByStatusStr(final String status) {
        StringBuffer sb = new StringBuffer();
        sb.append("agent:user:");
        sb.append(status);
        return sb.toString();
    }

    /**
     * 排队中的访客KEY
     *
     * @return
     */
    public static String getAgentUserInQueHashKey() {
        return getAgentUserHashKeyByStatusStr(MainContext.AgentUserStatusEnum.INQUENE.toString());
    }

    /**
     * 服务中的访客
     *
     * @return
     */
    public static String getAgentUserInServHashKey() {
        return getAgentUserHashKeyByStatusStr(MainContext.AgentUserStatusEnum.INSERVICE.toString());
    }

    /**
     * 结束服务的访客
     *
     * @return
     */
    public static String getAgentUserEndHashKey() {
        return getAgentUserHashKeyByStatusStr(MainContext.AgentUserStatusEnum.END.toString());
    }

    /**
     * 获得一个坐席的服务中的访客列表KEY
     */
    public static String getInServAgentUsersByAgentno(final String agentno) {
        StringBuffer sb = new StringBuffer();
        sb.append("agent:");
        sb.append(agentno);
        sb.append(":inserv");
        return sb.toString();
    }


    // Customer Chats Audit

    /**
     * 存储AgentUser监控信息的存储Hash的KEY
     *
     * @return
     */
    public static String getCustomerChatsAuditKey() {
        StringBuffer sb = new StringBuffer();
        sb.append("audit:customerchats");
        return sb.toString();
    }


    //  ONLINE USER 相关

    /**
     * 获得在线访客列表
     *
     * @return
     */
    public static String getOnlineUserHashKey() {
        StringBuffer sb = new StringBuffer();
        sb.append("visitor:online");
        return sb.toString();
    }

    // LOGIN USER 相关

    /**
     * 已经登录的系统用户的API Auth Token
     * 包括管理员，坐席等，访客不在该列
     * 在该列表中的用户代表在线的系统用户，通过浏览器或API访问了系统
     *
     * @return
     */
    public static String getApiTokenBearerKeyWithValue(final String token) {
        StringBuffer sb = new StringBuffer();
        sb.append("api:token:bearer:");
        sb.append(token);
        return sb.toString();
    }

    /**
     * CallCenter Agent 相关
     *
     * @return
     */
    public static String getCallCenterAgentHashKey() {
        StringBuffer sb = new StringBuffer();
        sb.append("callcenter:agent");
        return sb.toString();
    }

    /**
     * Job 相关
     *
     * @return
     */
    public static String getJobHashKey() {
        StringBuffer sb = new StringBuffer();
        sb.append("job");
        return sb.toString();
    }

    /**
     * System 相关
     *
     * @return
     */
    public static String getSystemHashKey() {
        StringBuffer sb = new StringBuffer();
        sb.append("system");
        return sb.toString();
    }

    /**
     * 系统词典
     *
     * @return
     */
    public static String getSysDicHashKey() {
        StringBuffer sb = new StringBuffer();
        sb.append("sysdic");
        return sb.toString();
    }


    /**
     * 坐席会话配置相关
     *
     * @return
     */
    public static String getSessionConfigList() {
        StringBuffer sb = new StringBuffer();
        sb.append("session:config:list");
        return sb.toString();
    }

    public static String getSessionConfig(String organid) {
        StringBuffer sb = new StringBuffer();
        sb.append(organid);
        sb.append(":session:config");
        return sb.toString();
    }

    /**
     * SocketIO连接相关
     */
    public static String getWebIMAgentSocketIOByAgentno(final String agentno) {
        StringBuffer sb = new StringBuffer();
        sb.append("agent:socketio:");
        sb.append(agentno);
        return sb.toString();
    }

    /**
     * CousultInvite 相关
     */
    public static String getConsultInvites() {
        StringBuffer sb = new StringBuffer();
        sb.append("consultinvite");
        return sb.toString();
    }

    /**
     * 和访客黑名单相关
     */
    public static String getBlackEntityKey() {
        StringBuffer sb = new StringBuffer();
        sb.append("visitor:blacklist");
        return sb.toString();
    }

    /**
     * 系统登录用户的会话Session信息
     */
    public static String getUserSessionKey() {
        StringBuffer sb = new StringBuffer();
        sb.append("user:session");
        return sb.toString();
    }

}
