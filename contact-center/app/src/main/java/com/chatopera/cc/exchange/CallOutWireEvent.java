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
package com.chatopera.cc.exchange;

import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.app.basic.MainContext.CallTypeEnum;
import com.chatopera.cc.util.Constants;
import com.chatopera.cc.exception.CallOutRuntimeException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class CallOutWireEvent {
    private static final Logger logger = LoggerFactory.getLogger(CallOutWireEvent.class);
    // 必须字段
    private String direction; // 呼叫方向
    private String status;    // channel状态
    private String dialplan; // 是否是呼叫计划
    private Date createtime; // 发起时间
    private int eventType; // CallWireEventType
    private String channel; // 语音渠道标识


    // 可选字段
    private String uuid;    // 软交换系统通话唯一标识，callid
    private String from;    // 主叫
    private String to;      // 被叫
    private String record;  // 录音文件

    private CallOutWireEvent() {

    }

//    public CallOutWireEvent(final String direction,
//                            final String status,
//                            final boolean isDialplan) throws CallOutRuntimeException {
//        this.setDirection(direction);
//        this.setStatus(status);
//        this.isDialplan = isDialplan;
//    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) throws CallOutRuntimeException {
        if (!Constants.CALL_DIRECTION_TYPES.contains(direction))
            throw new CallOutRuntimeException("不合法的呼叫方向 " + direction);
        this.direction = direction;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) throws CallOutRuntimeException {
        if (!Constants.CALL_SERVICE_STAUTS.contains(status))
            throw new CallOutRuntimeException("不合法的状态 " + status);
        this.status = status;
    }

    public static CallOutWireEvent parse(final JsonObject j) throws CallOutRuntimeException {
        CallOutWireEvent c = new CallOutWireEvent();

        /**
         * 必须字段
         */
        // 语音渠道标识
        if (j.has("channel")){
            c.setChannel(j.get("channel").getAsString());
        } else {
            throw new CallOutRuntimeException("未知的语音渠道标识。 " + j.toString());
        }


        // 呼叫方向
        if (j.has("type")) {
            switch (j.get("type").getAsString()) {
                case "callout": // 呼出
                    c.setDirection(CallTypeEnum.OUT.toString());
                    break;
                case "callin":  // 呼入
                    c.setDirection(CallTypeEnum.IN.toString());
                    break;
            }
        }

        if (c.getDirection() == null) {
            throw new CallOutRuntimeException("位置的呼叫方向。 " + j.toString());
        }

        // 创建时间
        if (j.has("createtime")) {
            c.setCreatetime(new Date(j.get("createtime").getAsLong()));
        } else {
            throw new CallOutRuntimeException("未知的创建时间。");
        }

        // 识别线路状态
        if (j.has("ops")) {
            String ops = j.get("ops").getAsString();
            switch (ops) {
                case "hangup":
                    c.setStatus(MainContext.CallServiceStatus.HANGUP.toString());
                    break;
                case "answer":
                    c.setStatus(MainContext.CallServiceStatus.INCALL.toString());
                    break;
                default:
                    // 其他类型的问题
                    break;
            }
        }

        if (c.getStatus() == null) {
            throw new CallOutRuntimeException("未知的线路状态类型。 " + j.toString());
        }

        // 是否是呼叫计划
        if (j.has("dialplan") && StringUtils.isNotBlank(j.get("dialplan").getAsString())) {
            c.setDialplan(j.get("dialplan").getAsString());
        }

        /**
         * 条件字段
         */
        if (j.has("uuid")) {
            c.setUuid(j.get("uuid").getAsString());
        }

        if (j.has("from")) {
            c.setFrom(j.get("from").getAsString());
        }

        if (j.has("record")) {
            c.setRecord(j.get("record").getAsString());
        }

        if (j.has("to")) {
            c.setTo(j.get("to").getAsString());
        }

        /**
         * 判断事件类型
         */
        if (CallTypeEnum.IN.toString().equals(c.getDirection())) { // 呼入
            // 暂不处理
            if (MainContext.CallServiceStatus.INCALL.toString().equals(c.getStatus())) {
                // 呼入应答
                c.setEventType(MainContext.CallWireEventType.CALLIN_CONN.getIndex());
            } else if (MainContext.CallServiceStatus.HANGUP.toString().equals(c.getStatus())) {
                if (c.getTo() != null) {
                    // 呼入挂断
                    c.setEventType(MainContext.CallWireEventType.CALLIN_DIST.getIndex());
                } else {
                    // 呼入失败
                    c.setEventType(MainContext.CallWireEventType.CALLIN_FAIL.getIndex());
                }
            }

        } else { // 呼出
            if (c.isDialplan()) { // 自动外呼
                if (MainContext.CallServiceStatus.INCALL.toString().equals(c.getStatus())) {
                    // 自动外呼应答
                    c.setEventType(MainContext.CallWireEventType.DIALPLAN_CONN.getIndex());
                } else if (MainContext.CallServiceStatus.HANGUP.toString().equals(c.getStatus())) {
                    if (c.getFrom() == null) {
                        // 自动外呼失败
                        c.setEventType(MainContext.CallWireEventType.DIALPLAN_FAIL.getIndex());
                    } else {
                        // 自动外呼挂断
                        c.setEventType(MainContext.CallWireEventType.DIALPLAN_DISC.getIndex());
                    }
                }
            } else { // 手动外呼
                if (MainContext.CallServiceStatus.INCALL.toString().equals(c.getStatus())) {
                    // 手动外呼应答
                    c.setEventType(MainContext.CallWireEventType.MANUDIAL_CONN.getIndex());
                } else if (MainContext.CallServiceStatus.HANGUP.toString().equals(c.getStatus())) {
                    if (c.getFrom() == null) {
                        // 手动外呼失败
                        c.setEventType(MainContext.CallWireEventType.MANUDIAL_FAIL.getIndex());
                    } else {
                        c.setEventType(MainContext.CallWireEventType.MANUDIAL_DISC.getIndex());
                    }
                }
            }
        }

        if(c.getEventType() == 0)
            throw new CallOutRuntimeException("无法确定事件类型。" + j.toString());

        return c;
    }

    public static CallOutWireEvent parse(final String j) throws CallOutRuntimeException {
        JsonParser jp = new JsonParser();
        JsonObject js = jp.parse(j).getAsJsonObject();
        return parse(js);
    }

    public JsonObject toJson() {
        JsonObject j = new JsonObject();
        j.addProperty("direction", this.direction);
        j.addProperty("status", this.status);
        j.addProperty("isDialplan", this.dialplan);
        return j;
    }

    public boolean isDialplan(){
        return  StringUtils.isNotBlank(this.dialplan);
    }

    public String getDialplan() {
        return dialplan;
    }

    public void setDialplan(String dialplan) {
        this.dialplan = dialplan;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
