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

package com.cskefu.cc.socketio.message;

import com.chatopera.compose4j.AbstractContext;
import com.cskefu.cc.model.*;

import java.io.Serializable;
import java.util.List;

/**
 * 发送消息的高级封装
 */
public class Message extends AbstractContext {

    public String id;
    /**
     * 发送方向：IN 访客给坐席，OUT 坐席给访客
     * NOTE callType应尽早设置
     */
    private String calltype;
    private String messageType;            // 消息类型 [必填]
    private Channel channel;

    private Serializable channelMessage;

    // 渠道信息
    private String channelType;                 // 渠道类型
    private String appid;                   // 渠道应用ID

    private String attachmentid;
    private boolean noagent;              // 是否有坐席，用于为新访客分配坐席的一个flag

    private AgentUser agentUser;          // 访客坐席会话
    private AgentStatus agentStatus;      // 坐席状态
    private AgentService agentService;    // 访客会话服务
    private String agentserviceid;        // 此值倾向于发送给前端，后端接口直接使用agentService传对象
    private PassportWebIMUser passportWebIMUser;        // 访客
    private User agent;                   // 坐席
    private User supervisor;              // 会话监控人员

    // 访客关联的联系人信息
    private Contacts contact;             // 访客关联的联系人
    private AgentUserContacts agentUserContacts; // 会话联系关联信息

    // 会话ID
    private String session;               // 会话周期
    private String contextid;             // 上下文ID
    private String createtime;            // 创建时间
    private String sign;                  // 消息签名

    // 消息属性
    private String message;              // 文本
    private String filename;             // 文件名
    private int filesize;                // 文件大小

    // boolean 处理结果
    private boolean isResolved;          // 该请求是否被正常处理

    private List<OtherMessageItem> suggest;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Channel getSnsAccount() {
        return channel;
    }

    public void setSnsAccount(Channel channel) {
        this.channel = channel;
    }

    public AgentUser getAgentUser() {
        return agentUser;
    }

    public void setAgentUser(AgentUser agentUser) {
        this.agentUser = agentUser;
    }

    public Serializable getChannelMessage() {
        return channelMessage;
    }

    public void setChannelMessage(Serializable channelMessage) {
        this.channelMessage = channelMessage;
    }

    public String getContextid() {
        return contextid;
    }

    public void setContextid(String contextid) {
        this.contextid = contextid;
    }

    public String getCalltype() {
        return calltype;
    }

    public void setCalltype(String calltype) {
        this.calltype = calltype;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getFilesize() {
        return filesize;
    }

    public void setFilesize(int filesize) {
        this.filesize = filesize;
    }

    public String getAttachmentid() {
        return attachmentid;
    }

    public void setAttachmentid(String attachmentid) {
        this.attachmentid = attachmentid;
    }

    public boolean isNoagent() {
        return noagent;
    }

    public void setNoagent(boolean noagent) {
        this.noagent = noagent;
    }

    public List<OtherMessageItem> getSuggest() {
        return suggest;
    }

    public void setSuggest(List<OtherMessageItem> suggest) {
        this.suggest = suggest;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public AgentService getAgentService() {
        return agentService;
    }

    public void setAgentService(AgentService agentService) {
        this.agentService = agentService;
    }

    public PassportWebIMUser getOnlineUser() {
        return passportWebIMUser;
    }

    public void setOnlineUser(PassportWebIMUser passportWebIMUser) {
        this.passportWebIMUser = passportWebIMUser;
    }

    public User getAgent() {
        return agent;
    }

    public void setAgent(User agent) {
        this.agent = agent;
    }

    public User getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(User supervisor) {
        this.supervisor = supervisor;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Contacts getContact() {
        return contact;
    }

    public void setContact(Contacts contact) {
        this.contact = contact;
    }

    public AgentStatus getAgentStatus() {
        return agentStatus;
    }

    public void setAgentStatus(AgentStatus agentStatus) {
        this.agentStatus = agentStatus;
    }

    public AgentUserContacts getAgentUserContacts() {
        return agentUserContacts;
    }

    public void setAgentUserContacts(AgentUserContacts agentUserContacts) {
        this.agentUserContacts = agentUserContacts;
    }

    public String getAgentserviceid() {
        return agentserviceid;
    }

    public void setAgentserviceid(String agentserviceid) {
        this.agentserviceid = agentserviceid;
    }

    public boolean isResolved() {
        return isResolved;
    }

    public void setResolved(boolean resolved) {
        isResolved = resolved;
    }
}
