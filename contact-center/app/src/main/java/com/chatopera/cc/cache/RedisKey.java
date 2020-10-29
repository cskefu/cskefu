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
package com.chatopera.cc.cache;

import com.chatopera.cc.basic.MainContext;

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
     * @param orgi 租户ID
     * @return
     */
    public static String getAgentStatusHashKeyByStatusStr(final String orgi, final String status) {
        StringBuffer sb = new StringBuffer();
        sb.append(orgi);
        sb.append(":agent:status:");
        sb.append(status);
        return sb.toString();
    }

    /**
     * 就绪的客服列表KEY
     *
     * @param orgi 租户ID
     * @return
     */
    public static String getAgentStatusReadyHashKey(final String orgi) {
        return getAgentStatusHashKeyByStatusStr(orgi, MainContext.AgentStatusEnum.READY.toString());
    }

    /**
     * 未就绪的坐席
     *
     * @param orgi
     * @return
     */
    public static String getAgentStatusNotReadyHashKey(final String orgi) {
        return getAgentStatusHashKeyByStatusStr(orgi, MainContext.AgentStatusEnum.NOTREADY.toString());
    }


    // AGENT USER 相关

    /**
     * 获得坐席访客关联列表指定字符串状态的KEY
     *
     * @param orgi
     * @param status
     * @return
     */
    public static String getAgentUserHashKeyByStatusStr(final String orgi, final String status) {
        StringBuffer sb = new StringBuffer();
        sb.append(orgi);
        sb.append(":agent:user:");
        sb.append(status);
        return sb.toString();
    }

    /**
     * 排队中的访客KEY
     *
     * @param orgi
     * @return
     */
    public static String getAgentUserInQueHashKey(final String orgi) {
        return getAgentUserHashKeyByStatusStr(orgi, MainContext.AgentUserStatusEnum.INQUENE.toString());
    }

    /**
     * 服务中的访客
     *
     * @param orgi
     * @return
     */
    public static String getAgentUserInServHashKey(final String orgi) {
        return getAgentUserHashKeyByStatusStr(orgi, MainContext.AgentUserStatusEnum.INSERVICE.toString());
    }

    /**
     * 结束服务的访客
     *
     * @param orgi
     * @return
     */
    public static String getAgentUserEndHashKey(final String orgi) {
        return getAgentUserHashKeyByStatusStr(orgi, MainContext.AgentUserStatusEnum.END.toString());
    }

    /**
     * 获得一个坐席的服务中的访客列表KEY
     */
    public static String getInServAgentUsersByAgentnoAndOrgi(final String agentno, final String orgi) {
        StringBuffer sb = new StringBuffer();
        sb.append(orgi);
        sb.append(":agent:");
        sb.append(agentno);
        sb.append(":inserv");
        return sb.toString();
    }


    // Customer Chats Audit

    /**
     * 存储AgentUser监控信息的存储Hash的KEY
     *
     * @param orgi
     * @return
     */
    public static String getCustomerChatsAuditKeyByOrgi(final String orgi) {
        StringBuffer sb = new StringBuffer();
        sb.append(orgi);
        sb.append(":audit:customerchats");
        return sb.toString();
    }


    //  ONLINE USER 相关

    /**
     * 获得在线访客列表
     *
     * @param orgi
     * @return
     */
    public static String getOnlineUserHashKey(final String orgi) {
        StringBuffer sb = new StringBuffer();
        sb.append(orgi);
        sb.append(":visitor:online");
        return sb.toString();
    }

    // LOGIN USER 相关

    /**
     * 已经登录的系统用户的API Auth Token
     * 包括管理员，坐席等，访客不在该列
     * 在该列表中的用户代表在线的系统用户，通过浏览器或API访问了系统
     *
     * @param orgi
     * @return
     */
    public static String getLoginUserKey(final String auth) {
        StringBuffer sb = new StringBuffer();
        sb.append("token:");
        sb.append(auth);
        return sb.toString();
    }

    /**
     * CallCenter Agent 相关
     *
     * @param orgi
     * @return
     */
    public static String getCallCenterAgentHashKeyByOrgi(final String orgi) {
        StringBuffer sb = new StringBuffer();
        sb.append(orgi);
        sb.append(":callcenter:agent");
        return sb.toString();
    }

    /**
     * Job 相关
     *
     * @param orgi
     * @return
     */
    public static String getJobHashKeyByOrgi(final String orgi) {
        StringBuffer sb = new StringBuffer();
        sb.append(orgi);
        sb.append(":job");
        return sb.toString();
    }

    /**
     * System 相关
     *
     * @param orgi
     * @return
     */
    public static String getSystemHashKeyByOrgi(final String orgi) {
        StringBuffer sb = new StringBuffer();
        sb.append(orgi);
        sb.append(":system");
        return sb.toString();
    }

    /**
     * 系统词典
     *
     * @param orgi
     * @return
     */
    public static String getSysDicHashKeyByOrgi(final String orgi) {
        StringBuffer sb = new StringBuffer();
        sb.append(orgi);
        sb.append(":sysdic");
        return sb.toString();
    }


    /**
     * 坐席会话配置相关
     *
     * @param orgi
     * @return
     */
    public static String getSessionConfigList(final String orgi) {
        StringBuffer sb = new StringBuffer();
        sb.append(orgi);
        sb.append(":session:config:list");
        return sb.toString();
    }

    public static String getSessionConfig(String organid, final String orgi) {
        StringBuffer sb = new StringBuffer();
        sb.append(orgi);
        sb.append(":");
        sb.append(organid);
        sb.append(":session:config");
        return sb.toString();
    }

    /**
     * SocketIO连接相关
     */
    public static String getWebIMAgentSocketIOByAgentnoAndOrgi(final String agentno, final String orgi) {
        StringBuffer sb = new StringBuffer();
        sb.append(orgi);
        sb.append(":agent:socketio:");
        sb.append(agentno);
        return sb.toString();
    }

    /**
     * CousultInvite 相关
     */
    public static String getConsultInvitesByOrgi(final String orgi) {
        StringBuffer sb = new StringBuffer();
        sb.append(orgi);
        sb.append(":consultinvite");
        return sb.toString();
    }

    /**
     * 和访客黑名单相关
     */
    public static String getBlackEntityKeyByOrgi(final String orgi) {
        StringBuffer sb = new StringBuffer();
        sb.append(orgi);
        sb.append(":visitor:blacklist");
        return sb.toString();
    }

    /**
     * 系统登录用户的会话Session信息
     */
    public static String getUserSessionKeyByOrgi(final String orgi) {
        StringBuffer sb = new StringBuffer();
        sb.append(orgi);
        sb.append(":user:session");
        return sb.toString();
    }

}
