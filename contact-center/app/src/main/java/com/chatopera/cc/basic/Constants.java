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
package com.chatopera.cc.basic;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 常量
 */
public class Constants {

    /**
     * 系统配置
     */
    public static final String SYSTEM_ORGI = "cskefu";
    public static final String USER_SESSION_NAME = "user";
    public static final String ORGAN_SESSION_NAME = "organ";
    public static final String GUEST_USER = "guest";
    public static final String IM_USER_SESSION_NAME = "im_user";
    public static final String CSKEFU_SYSTEM_DIC = "com.dic.system.template";
    public static final String CSKEFU_SYSTEM_AUTH_DIC = "com.dic.auth.resource";
    public static final String CSKEFU_SYSTEM_AREA_DIC = "com.dic.address.area";
    public static final String CSKEFU_SYSTEM_ADPOS_DIC = "com.dic.adv.type";
    public static final String CSKEFU_SYSTEM_COMMENT_DIC = "com.dic.webim.comment";
    public static final String CSKEFU_SYSTEM_COMMENT_ITEM_DIC = "com.dic.webim.comment.item";
    public static final String CSKEFU_SYSTEM_DIS_AI = "ownerai";
    public static final String CSKEFU_SYSTEM_DIS_AGENT = "owneruser";
    public static final String CSKEFU_SYSTEM_ASSUSER = "assuser";
    public static final String CSKEFU_SYSTEM_DIS_ORGAN = "ownerdept";
    public static final String CSKEFU_SYSTEM_DIS_TIME = "distime";
    public static final String CSKEFU_SYSTEM_COOKIES_FLAG = "uk_flagid";
    public static final String CSKEFU_SYSTEM_NO_DAT = "NOTEXIST";
    public static final String CSKEFU_SYSTEM_SECFIELD = "cskefu_sec_field";

    public static final String CSKEFU_SYSTEM_CALLCENTER = "callcenter";
    public static final String CSKEFU_SYSTEM_WORKORDEREMAIL = "workordermail";
    public static final String CSKEFU_SYSTEM_SMSEMAIL = "callcenter";
    public static final String CSKEFU_SYSTEM_AI_INPUT = "inputparam";
    public static final String CSKEFU_SYSTEM_AI_OUTPUT = "outputparam";

    public static final String CSKEFU_SYSTEM_INFOACQ = "infoacq";        // 数据采集模式
    public static final String DEFAULT_TYPE = "default";                 // 默认分类代码
    public static final String CACHE_SKILL = "cache_skill_";             // 技能组的缓存
    public static final String CACHE_AGENT = "cache_agent_";             // 坐席列表的缓存

    public static final String CUBE_TITLE_MEASURE = "指标";

    public static final String CSKEFU_SYSTEM_AREA = "cskefu_system_area";

    public static final String CSKEFU_SYSTEM_ADV = "cskefu_system_adv";   // 系统广告位

    public static final String SYSTEM_CACHE_CALLOUT_CONFIG = "callout_config";

    /**
     * 分布式存储
     */
    public final static String MINIO_BUCKET = "chatopera";


    /**
     * Channels
     */
    public static final String CHANNEL_TYPE_WEBIM = "webim";
    public final static String IM_MESSAGE_TYPE_MESSAGE = "message";
    public final static String IM_MESSAGE_TYPE_WRITING = "writing";
    public final static String CHATBOT_EVENT_TYPE_CHAT = "chat";

    /**
     * Modules
     */
    public final static String CSKEFU_MODULE_CALLOUT = "callout";
    public final static String CSKEFU_MODULE_CHATBOT = "chatbot";
    public final static String CSKEFU_MODULE_CONTACTS = "contacts";
    public final static String CSKEFU_MODULE_SKYPE = "skype";
    public final static String CSKEFU_MODULE_CCA = "cca";
    public final static String CSKEFU_MODULE_ENTIM = "entim";
    public final static String CSKEFU_MODULE_WORKORDERS = "workorders";
    public final static String CSKEFU_MODULE_CALLCENTER = "callcenter";
    public final static String CSKEFU_MODULE_REPORT = "report";

    /**
     * Formatter
     */
    // Date Formatter https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
    public final static SimpleDateFormat QUERY_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
    public final static SimpleDateFormat DISPLAY_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static DecimalFormat DURATION_MINS_FORMATTER = new DecimalFormat("0.00");

    /**
     * Instant Messaging Events
     */
    public final static String INSTANT_MESSAGING_MQ_TOPIC_AGENT = "cskefu.webim.agent";
    // freeswitch 通知消息
    public final static String INSTANT_MESSAGING_MQ_QUEUE_PBX = "pbx.*.events";
    public final static String INSTANT_MESSAGING_MQ_TOPIC_ONLINEUSER = "cskefu.webim.onlineuser";
    public final static String WEBIM_SOCKETIO_AGENT_DISCONNECT = "cskefu.socketio.agent.disconnect";
    // 黑名单
    public final static String WEBIM_SOCKETIO_ONLINE_USER_BLACKLIST = "cskefu.im.onlineuser.blacklist";
    // 坐席socketio断开到判定为离线的时长
    public final static int WEBIM_SOCKETIO_AGENT_OFFLINE_THRESHOLD = 10;
    // 发送消息给访客: 接收来自路由的消息并判断渠道
    public final static String INSTANT_MESSAGING_MQ_TOPIC_VISITOR = "cskefu.outbound.visitor";

    // 发送给聊天机器人并处理返回结果
    public final static String INSTANT_MESSAGING_MQ_QUEUE_CHATBOT = "cskefu.outbound.chatbot";
    public static final String AUDIT_AGENT_MESSAGE = "cskefu.agent.audit";


    /**
     * 登录用户的唯一登录会话管理
     */
    // web session single sign on
    public final static String MQ_TOPIC_WEB_SESSION_SSO = "cskefu.agent.session.retired";

    /**
     * Attachment File Type
     */
    public final static String ATTACHMENT_TYPE_IMAGE = "image";
    public final static String ATTACHMENT_TYPE_FILE = "file";

    /**
     * FreeSwitch Communication
     */
    // callcenter
    public final static String ACTIVEMQ_QUEUE_SWITCH_SYNC = "cskefu.callcenter.switch.sync";

    // callout
    public final static String FS_SIP_STATUS = "pbx:%s:sips"; // 查询SIP状态
    public final static String FS_CHANNEL_CC_TO_FS = "pbx/%s/execute"; // 发送外呼执行信号
    public final static String FS_DIALPLAN_STATUS = "pbx:%s:status"; // 外呼执行状态存储
    public final static String FS_DIALPLAN_TARGET = "pbx:%s:targets:%s";   // 外呼计划电话列表
    public final static String FS_BRIDGE_CONNECT = "callOutConnect";
    public final static String FS_LEG_ANSWER = "answer";
    public final static String FS_LEG_HANGUP = "hangup";
    public final static String FS_LEG_INCALL_ZH = "通话";
    public final static String FS_CALL_TYPE_CALLOUT = "callout";
    public final static Set<String> CALL_DIRECTION_TYPES = new HashSet<>(Arrays.asList(
            MainContext.CallType.OUT.toString(), MainContext.CallType.IN.toString()));
    public final static Set<String> CALL_SERVICE_STAUTS = new HashSet<>(Arrays.asList(MainContext.CallServiceStatus.INQUENE.toString(),
            MainContext.CallServiceStatus.RING.toString(),
            MainContext.CallServiceStatus.INCALL.toString(),
            MainContext.CallServiceStatus.BRIDGE.toString(),
            MainContext.CallServiceStatus.HOLD.toString(),
            MainContext.CallServiceStatus.HANGUP.toString(),
            MainContext.CallServiceStatus.OFFLINE.toString()));

    /**
     * 缓存管理策略
     */
    public final static String cache_setup_strategy_skip = "skip";

    /**
     * Skype消息路由
     * TODO 待优化为Skype渠道，暂时使用常量
     */
    public final static String CHANNEL_SKYPE_DEST = "skype.{0}.send";
    public final static String CHANNEL_SKYPE_RECV = "skype.*.rec";
    public static final String SKYPE_PAYLOAD_KEY_CONTENT = "content";
    public static final String SKYPE_PAYLOAD_KEY_SKYPEID = "skypeId";
    public static final String SKYPE_PAYLOAD_KEY_MSGTYPE = "msgType";

    /**
     * skype接收图片类型
     */
    public final static String SKYPE_MESSAGE_TEXT = "text";
    public final static String SKYPE_MESSAGE_PIC = "pic";
    public final static String SKYPE_MESSAGE_FILE = "file";

    /**
     * 坐席邀请访客加入聊天的超时，如果访客过了这么长时间还没有接入
     * 就忽略该邀请，当前设置为 20 分钟，如果访客点击该邀请，则会随机分配坐席
     */
    public final static int WEBIM_AGENT_INVITE_TIMEOUT = 20 * 60 * 1000;


    /**
     * 聊天机器人
     */
    public static final HashSet<String> CHATBOT_VALID_LANGS = new HashSet<String>(Arrays.asList("zh_CN", "en_US"));
    public static final String CHATBOT_CHATBOT_FIRST = "机器人客服优先";
    public static final String CHATBOT_HUMAN_FIRST = "人工客服优先";
    public static final String CHATBOT_CHATBOT_ONLY = "仅机器人客服";
    public static final HashSet<String> CHATBOT_VALID_WORKMODELS = new HashSet<String>(Arrays.asList(CHATBOT_CHATBOT_FIRST, CHATBOT_HUMAN_FIRST, CHATBOT_CHATBOT_ONLY));


}
