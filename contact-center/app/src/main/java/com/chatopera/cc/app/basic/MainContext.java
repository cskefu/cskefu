/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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

package com.chatopera.cc.app.basic;

import com.chatopera.cc.util.Constants;
import com.chatopera.cc.util.DateConverter;
import com.chatopera.cc.app.basic.resource.ActivityResource;
import com.chatopera.cc.app.basic.resource.BatchResource;
import com.chatopera.cc.app.model.Log;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MainContext {

    public static final String USER_SESSION_NAME = "user";
    public static final String GUEST_USER = "guest";
    public static final String IM_USER_SESSION_NAME = "im_user";
    public static final String UKEFU_SYSTEM_DIC = "com.dic.system.template";
    public static final String UKEFU_SYSTEM_AUTH_DIC = "com.dic.auth.resource";
    public static final String UKEFU_SYSTEM_AREA_DIC = "com.dic.address.area";
    public static final String UKEFU_SYSTEM_ADPOS_DIC = "com.dic.adv.type";
    public static final String UKEFU_SYSTEM_COMMENT_DIC = "com.dic.app.comment";
    public static final String UKEFU_SYSTEM_COMMENT_ITEM_DIC = "com.dic.app.comment.item";

    public static final String UKEFU_SYSTEM_DIS_AI = "ownerai";
    public static final String UKEFU_SYSTEM_DIS_AGENT = "owneruser";
    public static final String UKEFU_SYSTEM_ASSUSER = "assuser";
    public static final String UKEFU_SYSTEM_DIS_ORGAN = "ownerdept";
    public static final String UKEFU_SYSTEM_DIS_TIME = "distime";

    public static final String UKEFU_SYSTEM_COOKIES_FLAG = "uk_flagid";
    public static final String UKEFU_SYSTEM_NO_AI_CONFIG = "NOTEXIST";

    public static final String UKEFU_SYSTEM_NO_DAT = "NOTEXIST";

    public static final String SYSTEM_INDEX = "uckefu";
    public static final String UKEFU_SYSTEM_SECFIELD = "ukefu_sec_field";


    public static final String UKEFU_SYSTEM_CALLCENTER = "callcenter";
    public static final String UKEFU_SYSTEM_WORKORDEREMAIL = "workordermail";
    public static final String UKEFU_SYSTEM_SMSEMAIL = "callcenter";
    public static final String UKEFU_SYSTEM_AI_INPUT = "inputparam";
    public static final String UKEFU_SYSTEM_AI_OUTPUT = "outputparam";

    public static final String UKEFU_SYSTEM_INFOACQ = "infoacq";        //数据采集模式
    public static final String GUEST_USER_ID_CODE = "R3GUESTUSEKEY";
    public static final String WORKORDERS_CLOSED_STATUS = "uckefu_workorders_closed";
    public static final String SERVICE_QUENE_NULL_STR = "service_quene_null";
    public static final String DEFAULT_TYPE = "default";        //默认分类代码
    public static final String START = "start";                    //流程默认的开始节点
    public static final String CACHE_SKILL = "cache_skill_";                    //技能组的缓存
    public static final String CACHE_AGENT = "cache_agent_";                    //坐席列表的缓存

    public static final String CUBE_TITLE_MEASURE = "指标";

    public static final String UKEFU_SYSTEM_AREA = "uckefu_system_area";

    public static final String UKEFU_SYSTEM_ADV = "uckefu_system_adv";        //系统广告位

    public static final int MAX_IMAGE_WIDTH = 460;

    private static boolean imServerRunning = false;            //IM服务状态

    public static final int AGENT_STATUS_MAX_USER = 10;        //每个坐席 最大接待的 咨询数量

    public static final String SYSTEM_CACHE_SESSION_CONFIG = "session_config";

    public static final String SYSTEM_CACHE_SESSION_CONFIG_LIST = "session_config_list";

    public static final String SYSTEM_CACHE_AI_CONFIG = "ai_config";

    public static final String SYSTEM_CACHE_CALLOUT_CONFIG = "callout_config";

    public static String SYSTEM_ORGI = "cskefu";

    public static final String USER_CURRENT_ORGI_SESSION = "current_orgi";
    public static Map<String, Boolean> model = new HashMap<String, Boolean>();

    public static Map<String, Class<?>> uKeFuResourceMap = new HashMap<String, Class<?>>();

    private static int WebIMPort = 8081;

    private static ApplicationContext applicationContext;

    private static ElasticsearchTemplate templet;

    public static BlockingQueue<Log> tempLogQueue = new LinkedBlockingQueue<Log>();

    static {
        ConvertUtils.register(new DateConverter(), java.util.Date.class);
        model.put("report", true);

        uKeFuResourceMap.put(TaskType.ACTIVE.toString(), ActivityResource.class);

        uKeFuResourceMap.put(TaskType.BATCH.toString(), BatchResource.class);
    }

    public enum AskSectionType {
        DEFAULT;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum ActivityExecType {
        DEFAULT, RECOVERY;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum AgentWorkType {
        MEIDIACHAT,
        CALLCENTER;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum SystemMessageType {
        EMAIL, SMS;

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

    public enum ProcessType {
        WORKORDER;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum QuickTypeEnum {
        PUB,
        PRI;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum NamesProcessStatus {
        DIS,
        PREVIEW,
        CALLING,
        CALLED,
        CALLFAILD;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum FormFilterTypeEnum {
        BATCH,
        BUSINESS;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum NameStatusTypeEnum {
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

    public enum TagTypeEnum {
        QUALITY;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum StatusTypeEnum {
        INBOUND,
        OUTBOUND;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }


    public enum LogTypeEnum {
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

    public enum SalesNamesStatus {
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

    public enum QualityStatus {
        NO,        //未开启质检
        DIS,        //已分配
        NODIS;        //未分配

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum CallCenterCallTypeEnum {
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

        private CallCenterCallTypeEnum(final String name, final int index) {
            this.name = name;
            this.index = index;
        }

        public String toLetters() {
            return super.toString().toLowerCase();
        }

        public String toString() {
            return this.name;
        }
    }

    // 服务处理类型
    public enum OptTypeEnum {
        CHATBOT("机器人客服", 1),
        HUMAN("人工客服", 2);

        private final String name;
        private final int index;

        private OptTypeEnum(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public String toLetters() {
            return super.toString().toLowerCase();
        }

        public String toString() {
            return this.name;
        }
    }

    // 外呼计划状态
    public enum CallOutDialplanStatusEnum {
        RUNNING("执行中", 1),
        STOPPED("已停止", 2),
        STARTING("开始执行", 3),
        STOPPING("停止中", 4),
        INITIALIZATION("初始化", 5);

        private final String name;
        private final int index;

        private CallOutDialplanStatusEnum(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public String toLetters() {
            return super.toString().toLowerCase();
        }

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

        private CallWireEventType(final String name, final int index) {
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

        private CallServiceStatus(final String name, final int index) {
            this.name = name;
            this.index = index;
        }


        public String toLetters() {
            return super.toString().toLowerCase();
        }

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

    public enum ExtentionType {
        LINE,
        IVR,
        BUSINESS,
        SKILL,
        CONFERENCE,
        QUENE;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum DTMFTypeEnum {
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

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum ReportType {
        REPORT;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }


    public enum TaskType {
        BATCH,
        ACTIVE;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum WorkOrdersEventType {
        ACCEPTUSER,        //审批人变更
        OTHER;            //其他变更

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum BpmType {
        WORKORDERS;

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

    public enum WeiXinEventTypeEnum {
        SUB,
        UNSUB;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum ChannelTypeEnum {
        WEIXIN,
        WEIBO,
        WEBIM,
        PHONE,
        EMAIL, AI;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum EventTypeEnum {
        SUB,
        UNSUB,
        MENU;

        public String toString() {
            return super.toString().toLowerCase();
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

        private AgentStatusEnum(final String name, final int index) {
            this.name = name;
            this.index = index;
        }

        public String zh() {
            return this.name;
        }

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum WorkStatusEnum {
        IDLE,
        WAITTING,
        CALLOUT,
        PREVIEW,
        OUTBOUNDCALL;

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

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum MessageTypeEnum {
        NEW,
        MESSAGE,
        END,
        TRANS, STATUS, AGENTSTATUS, SERVICE, WRITING;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum CallCenterResultStatusEnum {
        OK;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum MediaTypeEnum {
        TEXT,
        EVENT,
        IMAGE,
        VIDIO,
        VOICE, LOCATION, FILE, COOPERATION, ACTION;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum CallTypeEnum {
        IN("呼入", 1),
        OUT("呼出", 2),
        SYSTEM("系统", 3),
        INVITE("邀请", 4);

        private final String name;
        private final int index;

        private CallTypeEnum(final String name, final int index) {
            this.name = name;
            this.index = index;
        }

        public String toLetters() {
            return super.toString().toLowerCase();
        }

        public String toString() {
            return this.name;
        }

    }

    public enum OnlineUserOperatorStatus {
        ONLINE,
        OFFLINE,
        REONLINE,
        CHAT,
        RECHAT,
        BYE,
        SEARCH,
        ACCESS;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum OnlineUserTypeStatus {
        USER,
        WEBIM,
        WEIXIN,
        APP,
        TELECOM,
        OTHER,
        WEIBO;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum AgentUserStatusEnum {
        INSERVICE,
        INQUENE, END;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
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

    public static int getWebIMPort() {
        return WebIMPort;
    }

    public static void setWebIMPort(int webIMPort) {
        WebIMPort = webIMPort;
    }

    /**
     * 系统级的加密密码 ， 从CA获取
     *
     * @return
     */
    public static String getSystemSecrityPassword() {
        return "UCKeFu";
    }

    public static void setIMServerStatus(boolean running) {
        imServerRunning = running;
    }

    public static boolean getIMServerStatus() {
        return imServerRunning;
    }

    public enum FilterConType {
        DIMENSION,
        MEASURE;

        public String toString() {
            return super.toString().toLowerCase();
        }
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

    public enum CallType {
        AI,
        AGENT;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum ChatbotType {
        SMARTAI,
        BUSINESSAI;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }


    public enum ChatbotBussType {
        SALE,
        QUESURVEY;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }


    public enum CallOutType {
        AGENT,
        AI;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    /**
     * @param resource
     * @return
     */
    public static Class<?> getResource(String resource) {
        return uKeFuResourceMap.get(resource);
    }

    /**
     * 是否开启外呼模块
     * @return
     */
    public static boolean isEnableCalloutModule() {
        return model.containsKey(Constants.CSKEFU_MODULE_CALLOUT) && (model.get(Constants.CSKEFU_MODULE_CALLOUT).equals(true));
    }

}
