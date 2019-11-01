/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2019 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.socketio.message;

import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.proxy.OnlineUserProxy;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "uk_chat_message")
@Document(indexName = "cskefu", type = "chat_message")
@org.hibernate.annotations.Proxy(lazy = false)
public class ChatMessage implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 3520656734252136303L;

    private String id = MainUtils.getUUID();
    private String appid;
    private String userid;
    private String username;

    private String aiid;
    private String touser;

    private boolean cooperation;

    private String msgtype;
    private String creater;
    private String usession;
    private String agentserviceid;

    private String sessionid;    //增加记录 AI 的 Client Session ID

    private String topicid;    //命中的 知识库 ID
    private String topicatid;    //命中的知识库分类ID
    private boolean topic;        //是否命中知识库
    private boolean aichat;    //是否和AI提问

    private String message;
    private String expmsg;        // 语音消息时，显示为ASR的识别结果，文字消息时，显示为附带的动态业务字段

    private String orgi;
    private String channel;
    private String model;            //消息所属模块， WebIM/EntIM
    private String chatype;        //对话类型，是私聊还是群聊 或者是智能机器人对话
    private Date lastagentmsgtime;    //前一条的坐席发送消息时间
    private Date lastmsgtime;        //前一条的访客发送消息时间
    private int agentreplytime;    //坐席回复消息时长		单位：秒
    private int agentreplyinterval;//坐席回复消息时间间隔 ， 单位：秒

    private String batid;        //变更用处，修改为是否有协作保存的图片

    private String headimgurl;        //用户头像 ，临时用

    private String filename;        //文件名
    private int filesize;            //文件尺寸
    private String attachmentid;    //附件ID

    private boolean datastatus;    //数据状态，是否已撤回消息

    private String mediaid;
    private String locx;    //location x
    private String locy;    //location y

    private long updatetime = System.currentTimeMillis();

    private int duration;    //音频时长

    private String scale;        //地图级别
    private String suggestmsg;    //推荐消息

    private int tokenum;    //当前未读消息数量
    private String agentuser;

    /**
     * 在发送消息 intervented = true 时
     * 代表该消息时会话监控人员发送的
     * supervisorname: 坐席监控人员名字
     * creater: 坐席监控人员的ID
     */
    private boolean intervented;     // 是否是会话监控发出的干预消息
    // 坐席监控人员的名字
    private String supervisorname;

    private boolean islabel;   //是否添加标记

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getUserid() {
        return userid;
    }

    public String getUserid(String userid) {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsession() {
        return usession;
    }

    public void setUsession(String usession) {
        this.usession = usession;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOrgi() {
        return orgi;
    }

    public void setOrgi(String orgi) {
        this.orgi = orgi;
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    private String type;        // 类型有两种 ， 一种 message ， 一种 writing
    private String contextid;
    private String calltype;

    //    @Field(type = FieldType.Date, format = DateFormat.custom,pattern = "yyyy-MM-dd")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd",timezone="GMT+8")
    private Date createtime = new Date();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "assigned")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getChatype() {
        return chatype;
    }

    public void setChatype(String chatype) {
        this.chatype = chatype;
    }

    public String getAgentserviceid() {
        return agentserviceid;
    }

    public void setAgentserviceid(String agentserviceid) {
        this.agentserviceid = agentserviceid;
    }

    @Transient
    public int getTokenum() {
        return tokenum;
    }

    public void setTokenum(int tokenum) {
        this.tokenum = tokenum;
    }

    public boolean isIslabel() {
        return islabel;
    }

    public void setIslabel(boolean islabel) {
        this.islabel = islabel;
    }

    @Transient
    public String getAgentuser() {
        return agentuser;
    }

    public void setAgentuser(String agentuser) {
        this.agentuser = agentuser;
    }

    @Transient
    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getMediaid() {
        return mediaid;
    }

    public void setMediaid(String mediaid) {
        this.mediaid = mediaid;
    }

    public String getLocx() {
        return locx;
    }

    public void setLocx(String locx) {
        this.locx = locx;
    }

    public String getLocy() {
        return locy;
    }

    public void setLocy(String locy) {
        this.locy = locy;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public long getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(long updatetime) {
        this.updatetime = updatetime;
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

    public Date getLastagentmsgtime() {
        return lastagentmsgtime;
    }

    public void setLastagentmsgtime(Date lastagentmsgtime) {
        this.lastagentmsgtime = lastagentmsgtime;
    }

    public int getAgentreplytime() {
        return agentreplytime;
    }

    public void setAgentreplytime(int agentreplytime) {
        this.agentreplytime = agentreplytime;
    }

    public Date getLastmsgtime() {
        return lastmsgtime;
    }

    public void setLastmsgtime(Date lastmsgtime) {
        this.lastmsgtime = lastmsgtime;
    }

    public int getAgentreplyinterval() {
        return agentreplyinterval;
    }

    public void setAgentreplyinterval(int agentreplyinterval) {
        this.agentreplyinterval = agentreplyinterval;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getBatid() {
        return batid;
    }

    public void setBatid(String batid) {
        this.batid = batid;
    }

    public boolean isCooperation() {
        return cooperation;
    }

    public void setCooperation(boolean cooperation) {
        this.cooperation = cooperation;
    }

    public boolean isDatastatus() {
        return datastatus;
    }

    public void setDatastatus(boolean datastatus) {
        this.datastatus = datastatus;
    }

    public String getTopicid() {
        return topicid;
    }

    public void setTopicid(String topicid) {
        this.topicid = topicid;
    }

    public String getTopicatid() {
        return topicatid;
    }

    public void setTopicatid(String topicatid) {
        this.topicatid = topicatid;
    }

    public boolean isTopic() {
        return topic;
    }

    public void setTopic(boolean topic) {
        this.topic = topic;
    }

    public boolean isAichat() {
        return aichat;
    }

    public void setAichat(boolean aichat) {
        this.aichat = aichat;
    }

    public String getAiid() {
        return aiid;
    }

    public void setAiid(String aiid) {
        this.aiid = aiid;
    }

    public String getExpmsg() {
        return expmsg;
    }

    public void setExpmsg(String expmsg) {
        this.expmsg = expmsg;
    }

    public String getSuggestmsg() {
        return suggestmsg;
    }

    public void setSuggestmsg(String suggestmsg) {
        this.suggestmsg = suggestmsg;
    }

    public String getSupervisorname() {
        return supervisorname;
    }

    public void setSupervisorname(String supervisorname) {
        this.supervisorname = supervisorname;
    }

    @Transient
    public List<OtherMessageItem> getSuggest() {
        List<OtherMessageItem> otherMessageItemList = null;
        if (StringUtils.isNotBlank(this.getSuggestmsg())) {
            try {
                otherMessageItemList = OnlineUserProxy.objectMapper.readValue(this.getSuggestmsg(), OnlineUserProxy.getCollectionType(ArrayList.class, OtherMessageItem.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return otherMessageItemList;
    }

    public boolean isIntervented() {
        return intervented;
    }

    public void setIntervented(boolean intervented) {
        this.intervented = intervented;
    }
}
