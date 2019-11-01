package com.chatopera.cc.peer;

import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.model.AgentStatus;
import com.chatopera.cc.socketio.message.Message;
import com.chatopera.compose4j.AbstractContext;

import java.util.Date;

public class PeerContext extends AbstractContext {
    private AgentStatus agentStatus;
    private Message message;

    private final Date createtime = new Date();

    // 消息是否已经被发出
    private boolean sent = false;

    // 渠道
    private MainContext.ChannelType channel;

    // 渠道标识ID
    private String appid;

    // 接收者角色
    private MainContext.ReceiverType receiverType;

    // 接收消息人ID
    private String touser;

    // Distribute，在本机没有连接，是否通过ActiveMQ发布到多机
    private boolean isDist;

    // 消息类型
    private MainContext.MessageType msgType;

    public AgentStatus getAgentStatus() {
        return agentStatus;
    }

    public void setAgentStatus(AgentStatus agentStatus) {
        this.agentStatus = agentStatus;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public MainContext.ChannelType getChannel() {
        return channel;
    }

    public void setChannel(MainContext.ChannelType channel) {
        this.channel = channel;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public MainContext.ReceiverType getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(MainContext.ReceiverType receiverType) {
        this.receiverType = receiverType;
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public boolean isDist() {
        return isDist;
    }

    public void setDist(boolean dist) {
        isDist = dist;
    }

    public MainContext.MessageType getMsgType() {
        return msgType;
    }

    public void setMsgType(MainContext.MessageType msgType) {
        this.msgType = msgType;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public Date getCreatetime() {
        return createtime;
    }

}
