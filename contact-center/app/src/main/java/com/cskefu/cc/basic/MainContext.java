/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
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

package com.cskefu.cc.basic;

import com.cskefu.cc.basic.resource.ActivityResource;
import com.cskefu.cc.basic.resource.BatchResource;
import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.cache.RedisCommand;
import com.cskefu.cc.peer.PeerSyncIM;
import com.cskefu.cc.util.DateConverter;
import com.cskefu.cc.util.SystemEnvHelper;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainContext {

    private final static Logger logger = LoggerFactory.getLogger(MainContext.class);

    private static boolean imServerRunning = false;  // IM服务状态

    private static Set<String> modules = new HashSet<String>();

    public static Map<String, Class<?>> csKeFuResourceMap = new HashMap<String, Class<?>>();

    private static ApplicationContext applicationContext;

    private static ElasticsearchTemplate templet;

    private static RedisCommand redisCommand;

    private static Cache cache;

    private static PeerSyncIM peerSyncIM;

    static {
        ConvertUtils.register(new DateConverter(), java.util.Date.class);
        csKeFuResourceMap.put(TaskType.ACTIVE.toString(), ActivityResource.class);
        csKeFuResourceMap.put(TaskType.BATCH.toString(), BatchResource.class);
    }

    public enum AskSectionType {
        DEFAULT;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum ActivityExecType {
        DEFAULT, RECOVERY;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum AgentWorkType {
        MEIDIACHAT,
        CALLCENTER;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum SystemMessageType {
        EMAIL, SMS;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    /**
     * 名单分配状态：已分配|未分配
     *
     * @author iceworld
     */
    public enum NamesDisStatusType {
        NOT, DISAGENT, DISORGAN, DISAI;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum QuickType {
        PUB,
        PRI;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum NamesProcessStatusEnum {
        DIS,
        PREVIEW,
        CALLING,
        CALLED,
        CALLFAILD;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum FormFilterType {
        BATCH,
        BUSINESS;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum NameStatusType {
        CALLED,    //已拨打
        NOTCALL    //未拨打
        ;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum NamesCalledEnum {
        SUCCESS,//拨打成功
        FAILD,    //拨打失败
        NOANSWER,//无人接听
        EMPNO,    //空号
        ARREARS,//欠费
        APPO,    //预约拨打
        INVALID;//无效名单

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum TagType {
        QUALITY;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum StatusType {
        INBOUND,
        OUTBOUND;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }


    public enum LogType {
        REQUEST,
        CREATE,
        READ,
        UPDATE,
        DELETE,
        OTHER,
        INFO,
        WARN,
        ERROR;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum SalesNamesStatusEnum {
        DIST,            //已分配
        NOTDIST;        //未分配

        public String toString() {
            return super.toString().toLowerCase();
        }
    }


    public enum LeaveMsgStatus {
        PROCESSED,        //已处理
        NOTPROCESS;        //未处理

        public String toString() {
            return super.toString().toLowerCase();
        }
    }


    public enum AdPosEnum {
        POINT,
        IMAGE,
        WELCOME,
        INVITE;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum QualityType {
        CHAT,
        VOICE;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum QualityStatusEnum {
        NO,        //未开启质检
        DIS,        //已分配
        NODIS;        //未分配

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum CallCenterCallType {
        INSIDELINE("内线", 1),
        ORGCALLOUT("部门外呼", 2),
        ORGCALLIN("部门呼入", 3),
        INSIDEQUENE("内线排队", 4),
        INSIDETRANS("内线转接", 5),            //已分配
        OUTSIDELINE("外线", 6),
        OUTSIDEQUENE("外线排队", 7),
        OUTSIDETRANS("外线转接", 8);        //未分配

        private final String name;
        private final int index;

        CallCenterCallType(final String name, final int index) {
            this.name = name;
            this.index = index;
        }

        public String toLetters() {
            return super.toString().toLowerCase();
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    // 服务处理类型
    public enum OptType {
        CHATBOT("机器人客服", 1),
        HUMAN("人工客服", 2);

        private final String name;
        private final int index;

        OptType(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public String toLetters() {
            return super.toString().toLowerCase();
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    // 外呼计划状态
    public enum CalloutDialplanStatusEnum {
        RUNNING("执行中", 1),
        STOPPED("已停止", 2),
        STARTING("开始执行", 3),
        STOPPING("停止中", 4),
        INITIALIZATION("初始化", 5);

        private final String name;
        private final int index;

        CalloutDialplanStatusEnum(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public String toLetters() {
            return super.toString().toLowerCase();
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum CallWireEventType {
        DIALPLAN_CONN("自动外呼接通", 1),
        DIALPLAN_DISC("自动外呼挂断", 2),
        DIALPLAN_FAIL("自动外呼失败", 3),
        MANUDIAL_CONN("手动外呼接通", 4),
        MANUDIAL_DISC("手动外呼挂断", 5),
        MANUDIAL_FAIL("手动外呼失败", 6),
        CALLIN_CONN("呼入接通", 7),
        CALLIN_DIST("呼入挂断", 8),
        CALLIN_FAIL("呼入失败", 9);

        private String name;
        private int index;

        CallWireEventType(final String name, final int index) {
            this.name = name;
            this.index = index;
        }

        public String toLetters() {
            return super.toString().toLowerCase();
        }

        public String toString() {
            return this.name;
        }

        public int getIndex() {
            return this.index;
        }
    }


    public enum CallServiceStatus {
        INQUENE("就绪", 1),
        RING("振铃", 2),            //振铃
        INCALL("应答", 3),            //应答
        BRIDGE("桥接", 4),            //桥接
        HOLD("已挂起", 5),            //已挂起
        HANGUP("已挂机", 6),           //已挂机
        OFFLINE("离线", 7);            //离线

        private final String name;
        private final int index;

        CallServiceStatus(final String name, final int index) {
            this.name = name;
            this.index = index;
        }


        public String toLetters() {
            return super.toString().toLowerCase();
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum CallChannelStatus {
        EARLY,
        DOWN;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum WxMpFileType {
        JPG,
        PNG;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum AgentInterType {
        SKILL,
        AGENT;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    /**
     * 会话发起方
     *
     * @author iceworld
     */
    public enum ChatInitiatorType {
        AGENT,
        USER;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    /**
     * 分机类型
     */
    public enum ExtensionType {
        LINE,
        IVR,
        BUSINESS,
        SKILL,
        CONFERENCE,
        QUENE;

        public String toString() {
            return super.toString().toLowerCase();
        }

        public static ExtensionType toValue(final String str) {
            for (final ExtensionType item : values()) {
                if (StringUtils.equalsIgnoreCase(item.toString(), str)) {
                    return item;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    public enum DTMFType {
        SATISF,
        PASSWORD,        //密码验证
        IDCARD,            //身份证号码
        CARDNO;            //银行卡号

        public String toString() {
            return super.toString().toLowerCase();
        }
    }


    public enum ChatbotItemType {
        USERINPUT,
        BOTREPLY;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum MultiUpdateType {
        SAVE,
        UPDATE,
        DELETE;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum ReportType {
        REPORT;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }


    public enum TaskType {
        BATCH,
        ACTIVE;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum WorkOrdersEventType {
        ACCEPTUSER,        //审批人变更
        OTHER;            //其他变更

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum BpmType {
        WORKORDERS;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum AskOperatorType {
        VIEWS,
        COMMENTS,
        UPS;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum ModelType {
        USER,
        WORKORDERS,
        KBS,
        SUMMARY,
        CCSUMMARY, WEBIM, CALLOUT;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }


    public enum AdapterType {
        TEXT,
        MEDIA,
        AGENT,
        XIAOE,
        INTER;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum MetadataTableType {
        UK_WORKORDERS;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum OnlineUserInviteStatus {
        DEFAULT,
        INVITE,
        REFUSE,
        INSERV,
        ACCEPT;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum CustomerTypeEnum {
        ENTERPRISE,
        PERSONAL;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum WeiXinEventType {
        SUB,
        UNSUB;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }


    /**
     * 会话监控消息类型
     */
    public enum InterventMessageType {
        @JsonProperty("text") TEXT,
        @JsonProperty("status") STATUS,
        @JsonProperty("image") IMAGE,
        @JsonProperty("file") FILE;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        public static InterventMessageType toValue(final String str) {
            for (final InterventMessageType item : values()) {
                if (StringUtils.equalsIgnoreCase(item.toString(), str)) {
                    return item;
                }
            }
            throw new IllegalArgumentException();
        }
    }


    /**
     * 多渠道的渠道类型
     */
    public enum ChannelType {
        WEIXIN,
        WEIBO,
        WEBIM,
        PHONE,
        SKYPE,
        MESSENGER,
        EMAIL,
        AI;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        public static ChannelType toValue(final String str) {
            for (final ChannelType item : values()) {
                if (StringUtils.equalsIgnoreCase(item.toString(), str)) {
                    return item;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    public enum AgentStatusEnum {
        READY("就绪", 1),
        NOTREADY("未就绪", 2),
        BUSY("置忙", 3),
        NOTBUSY("不忙", 4),
        IDLE("空闲", 5),
        OFFLINE("离线", 6),
        SERVICES("服务", 7);

        private String name;
        private int index;

        AgentStatusEnum(final String name, final int index) {
            this.name = name;
            this.index = index;
        }

        public String zh() {
            return this.name;
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        public static AgentStatusEnum toValue(final String str) {
            for (final AgentStatusEnum item : values()) {
                if (StringUtils.equalsIgnoreCase(item.toString(), str)) {
                    return item;
                }
            }
            throw new IllegalArgumentException();
        }

    }

    public enum WorkStatusEnum {
        IDLE,
        WAITTING,
        CALLOUT,
        PREVIEW,
        OUTBOUNDCALL,
        SERVICES;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum TaskStatusType {
        NORMAL("0"),
        READ("1"),
        QUEUE("5"),
        RUNNING("2"),
        END("3");

        private String type;

        TaskStatusType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String toString() {
            return super.toString().toLowerCase();
        }
    }


    public enum NameSpaceEnum {

        IM("/im/user"),
        AGENT("/im/agent"),
        ENTIM("/im/ent"),
        CHATBOT("/im/chatbot"),
        CALLCENTER("/callcenter/exchange"),
        CALLOUT("/callout/exchange");

        private String namespace;

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        NameSpaceEnum(String namespace) {
            this.namespace = namespace;
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }


    /**
     * 接收消息的人员类型
     */
    public enum ReceiverType {
        AGENT,   // 坐席
        VISITOR, // 访客
        CHATBOT; // 聊天机器人

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        public static ReceiverType toValue(final String str) {
            for (final ReceiverType item : values()) {
                if (StringUtils.equalsIgnoreCase(item.toString(), str)) {
                    return item;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    /**
     * 发送消息的内容类型
     */
    public enum MessageType {
        NEW,
        MESSAGE,
        END,
        TRANS,
        TRANSOUT,     // 当前会话被转接出去
        STATUS,
        AGENTSTATUS,
        SERVICE,
        WRITING,
        LEAVE,         // 浏览器端执行退出
        SATISFACTION,
        AUDIT_MESSAGE, // 会话监控消息类型
        AUDIT_NEW,
        AUDIT_END,
        AUDIT_TRANS,
        AUDIT_STATUS,
        AUDIT_AGENTSTATUS,
        AUDIT_SERVICE,
        AUDIT_WRITING;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        public static MessageType toValue(final String str) {
            for (final MessageType item : values()) {
                if (StringUtils.equalsIgnoreCase(item.toString(), str)) {
                    return item;
                }
            }
            throw new IllegalArgumentException();
        }

    }

    public enum MediaType {
        TEXT,
        EVENT,
        IMAGE,
        VIDIO,
        VOICE, LOCATION, FILE, COOPERATION, ACTION, PIC;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum CallType {
        IN("呼入", 1),
        OUT("呼出", 2),
        SYSTEM("系统", 3),
        INVITE("邀请", 4);

        private final String name;
        private final int index;

        CallType(final String name, final int index) {
            this.name = name;
            this.index = index;
        }

        public String toLetters() {
            return super.toString().toLowerCase();
        }

        public static CallType toValue(final String str) {
            for (final CallType item : values()) {
                if (StringUtils.equals(item.toString(), str)) {
                    return item;
                }
            }
            throw new IllegalArgumentException();
        }

        @Override
        public String toString() {
            return this.name;
        }

    }

    public enum OnlineUserStatusEnum {
        ONLINE,
        OFFLINE,
        REONLINE,
        CHAT,
        RECHAT,
        BYE,
        SEARCH,
        ACCESS;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }


        public static OnlineUserStatusEnum toValue(final String str) {
            for (final OnlineUserStatusEnum item : values()) {
                if (StringUtils.equals(item.toString(), str)) {
                    return item;
                }
            }
            throw new IllegalArgumentException();
        }

    }

    public enum OnlineUserType {
        USER,
        WEBIM,
        WEIXIN,
        APP,
        TELECOM,
        SKYPE,
        MESSENGER,
        OTHER,
        WEIBO;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    /**
     * 坐席访客对话状态
     */
    public enum AgentUserStatusEnum {
        INSERVICE,
        INQUENE,
        END;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        public static AgentUserStatusEnum toValue(final String str) {
            for (final AgentUserStatusEnum item : values()) {
                if (StringUtils.equalsIgnoreCase(item.toString(), str)) {
                    return item;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
        context.getBean(TerminateBean.class);
    }

    public static ApplicationContext getContext() {
        return applicationContext;
    }

    public static ElasticsearchTemplate getTemplet() {
        return templet;
    }

    public static void setTemplet(ElasticsearchTemplate templet) {
        MainContext.templet = templet;
    }

    /**
     * 系统级的加密密码 ， 从CA获取
     *
     * @return
     */
    public static String getSystemSecrityPassword() {
        return SystemEnvHelper.parseFromApplicationProps("application.security.password");
    }

    public static void setIMServerStatus(boolean running) {
        imServerRunning = running;
    }

    public static boolean getIMServerStatus() {
        return imServerRunning;
    }

    public enum FilterCompType {
        NOT,
        EQUAL;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum FilterValuefilterType {
        COMPARE,
        RANGE;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum FilterConValueType {
        INPUT,
        AUTO,
        USERDEF;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum FilterModelType {
        TEXT,
        DATE,
        SIGSEL,
        SELECT;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum FilteFunType {
        FILTER,
        RANK;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum FbMessengerStatus {
        ENABLED,
        DISABLED;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    /**
     * 呼出电话的主叫类型
     */
    public enum CallerType {
        AI,     // 机器人
        AGENT;  // 人工坐席

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    /**
     * @param resource
     * @return
     */
    public static Class<?> getResource(String resource) {
        return csKeFuResourceMap.get(resource);
    }

    /**
     * Redis底层接口
     */
    public final static RedisCommand getRedisCommand() {
        if (redisCommand == null) {
            redisCommand = getContext().getBean(RedisCommand.class);
        }
        return redisCommand;
    }

    /**
     * 缓存管理
     *
     * @return
     */
    public final static Cache getCache() {
        if (cache == null) {
            cache = getContext().getBean(Cache.class);
        }
        return cache;
    }

    public final static PeerSyncIM getPeerSyncIM() {
        if (peerSyncIM == null) {
            peerSyncIM = getContext().getBean(PeerSyncIM.class);
        }
        return peerSyncIM;
    }

    /**
     * 开启模块
     *
     * @param moduleName
     */
    public static void enableModule(final String moduleName) {
        logger.info("[module] enable module {}", moduleName);
        modules.add(StringUtils.lowerCase(moduleName));
    }

    public static boolean hasModule(final String moduleName) {
        return modules.contains(StringUtils.lowerCase(moduleName));
    }

    public static void removeModule(final String moduleName) {
        modules.remove(moduleName);
    }

    /**
     * 获得Model
     *
     * @return
     */
    public static Set<String> getModules() {
        return modules;
    }
}
