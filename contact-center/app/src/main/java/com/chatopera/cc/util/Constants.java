/*
 * Copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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

import com.chatopera.cc.app.basic.MainContext;
import org.apache.commons.lang.StringUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Constants {

    public final static String MINIO_BUCKET = "chatopera";

    public final static String IM_MESSAGE_TYPE_MESSAGE = "message";
    public final static String CHATBOT_EVENT_TYPE_CHAT = "chat";


    /**
     * Modules
     */
    public final static String CSKEFU_MODULE_CALLOUT = "sales";
    public final static String CSKEFU_MODULE_CHATBOT = "chatbot";
    public final static String CSKEFU_MODULE_CONTACTS = "contacts";

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
    public final static String INSTANT_MESSTRING_WEBIM_AGENT_PATTERN = "im:webim:agent:%s:events";
    public final static String INSTANT_MESSAGING_WEBIM_AGENT_CHANNEL = String.format(INSTANT_MESSTRING_WEBIM_AGENT_PATTERN, "*");
    public final static String INSTANT_MESSTRING_WEBIM_ONLINE_USER_PATTERN = "im:webim:onlineuser:%s:events";
    public final static String INSTANT_MESSAGING_WEBIM_ONLINE_USER_CHANNEL = String.format(INSTANT_MESSTRING_WEBIM_ONLINE_USER_PATTERN, "*");

    /**
     * Attachment File Type
     */
    public final static String ATTACHMENT_TYPE_IMAGE = "image";
    public final static String ATTACHMENT_TYPE_FILE = "file";

    /**
     * FreeSwitch Communication
     */
    public final static String FS_SIP_STATUS = "pbx:%s:sips"; // 查询SIP状态
    public final static String FS_CHANNEL_CC_TO_FS = "pbx:%s:execute"; // 发送外呼执行信号
    public final static String FS_DIALPLAN_STATUS = "pbx:%s:status"; // 外呼执行状态存储
    public final static String FS_DIALPLAN_TARGET = "pbx:%s:targets:%s";   // 外呼计划电话列表
    public final static String FS_CHANNEL_FS_TO_CC = "pbx:*:events";    // freeswitch 通知消息
    public final static String FS_BRIDGE_CONNECT = "callOutConnect";
    public final static String FS_LEG_ANSWER = "answer";
    public final static String FS_LEG_HANGUP = "hangup";
    public final static String FS_LEG_INCALL_ZH = "通话";
    public final static String FS_CALL_TYPE_CALLOUT = "callout";
    public final static Set<String> CALL_DIRECTION_TYPES = new HashSet<>(Arrays.asList(MainContext.CallTypeEnum.OUT.toString(), MainContext.CallTypeEnum.IN.toString()));
    public final static Set<String> CALL_SERVICE_STAUTS = new HashSet<>(Arrays.asList(MainContext.CallServiceStatus.INQUENE.toString(),
            MainContext.CallServiceStatus.RING.toString(),
            MainContext.CallServiceStatus.INCALL.toString(),
            MainContext.CallServiceStatus.BRIDGE.toString(),
            MainContext.CallServiceStatus.HOLD.toString(),
            MainContext.CallServiceStatus.HANGUP.toString(),
            MainContext.CallServiceStatus.OFFLINE.toString()));

}
