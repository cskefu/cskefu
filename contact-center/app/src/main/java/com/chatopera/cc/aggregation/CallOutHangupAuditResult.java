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
package com.chatopera.cc.aggregation;

import com.chatopera.cc.exception.CallOutRecordException;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;

public class CallOutHangupAuditResult {
    private final static Logger logger = LoggerFactory.getLogger(CallOutHangupAuditResult.class);
    private final static String DIRECTION_ALL = "呼出和呼入";
    private String agentId;   // 坐席ID
    private String agentName;  // 坐席名字
    private String direction; // 呼叫方向 ['呼入', '呼出']
    private int dialplan;     // 自动呼叫个数
    private int total;        // 总数
    private int seconds;      // 总时长，单位：秒
    private int fails;        // 失败数
    private int gt60;         // 长于1分钟个数
    private int maxduration;  // 最长通话时间，单位：秒
    private int avgduration;  // 平均时长，单位：秒

    private CallOutHangupAuditResult() {

    }

    public CallOutHangupAuditResult(String agentId,
                                    String direction,
                                    int dialplan,
                                    int total,
                                    int seconds,
                                    int fails,
                                    int gt60,
                                    int maxduration) {
        this.agentId = agentId;
        this.direction = direction;
        this.dialplan = dialplan;
        this.total = total;
        this.seconds = seconds;
        this.fails = fails;
        this.gt60 = gt60;
        this.maxduration = maxduration;

    }

    public static CallOutHangupAuditResult cast(Object[] x) throws CallOutRecordException {
        CallOutHangupAuditResult y = new CallOutHangupAuditResult();
        try {
            y.setAgentId((String) x[0]);
            y.setDirection((String) x[1]);
            y.setDialplan(((BigInteger) x[2]).intValue());
            y.setTotal(((BigInteger) x[3]).intValue());
            y.setSeconds(((BigDecimal) x[4]).intValue());
            y.setFails(((BigInteger) x[5]).intValue());
            y.setGt60(((BigInteger) x[6]).intValue());
            y.setMaxduration((int) x[7]);
            y.setAvgduration(((BigDecimal) x[8]).intValue());
            y.setAgentName((String) x[9]);
        } catch (Exception e) {
            logger.error("[callout audit] cast error ", e);
            throw new CallOutRecordException("[Ljava.lang.Object; cannot be cast to " + CallOutHangupAuditResult.class.getSimpleName());
        }
        return y;
    }

    /**
     * 合并同一个Agent的两个不同direction的数据
     */
    public static CallOutHangupAuditResult mix(final CallOutHangupAuditResult x, final CallOutHangupAuditResult y) throws CallOutRecordException {
        if (x == null)
            return y;
        if (y == null)
            return x;
        if (x.getDirection() == y.getDirection())
            throw new CallOutRecordException("CallOutHangupAuditResult.mix 呼叫方向不能相同。");

        if (!x.getAgentId().equals(y.getAgentId()))
            throw new CallOutRecordException("CallOutHangupAuditResult.mix 坐席ID必须相同。");

        CallOutHangupAuditResult z = new CallOutHangupAuditResult();
        z.setDirection(DIRECTION_ALL);
        z.setAgentId(x.getAgentId());
        z.setAgentName(x.getAgentName());
        z.setMaxduration(x.getMaxduration() > y.getMaxduration() ? x.getMaxduration() : y.getMaxduration());
        z.setSeconds(x.getSeconds() + y.getSeconds());
        z.setGt60(x.getGt60() + y.getGt60());
        z.setTotal(x.getTotal() + y.getTotal());
        z.setFails(x.getFails() + y.getFails());
        z.setDialplan(x.getDialplan() + y.getDialplan());
        z.setAvgduration((int) ((x.getAvgduration() + y.getAvgduration()) / 2));
        return z;
    }


    public JsonObject toJson(boolean id, boolean name, boolean direction) {
        JsonObject j = new JsonObject();

        if (direction)
            j.addProperty("direction", this.getDirection());

        if (id)
            j.addProperty("agentId", this.agentId);

        if (name)
            j.addProperty("agentName", this.agentName);

        int succ = this.getTotal() - this.getFails();
        j.addProperty("total", this.getTotal());
        j.addProperty("answer", succ);
        j.addProperty("rate", MathHelper.float_percentage_formatter(succ, this.getTotal()));
        j.addProperty("dur", MathHelper.formatSeconds(this.getSeconds()));
        j.addProperty("avg", MathHelper.formatSeconds(this.avgduration));
        j.addProperty("max", MathHelper.formatSeconds(this.getMaxduration()));
        j.addProperty("gt60", this.getGt60());

        return j;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getDialplan() {
        return dialplan;
    }

    public void setDialplan(int dialplan) {
        this.dialplan = dialplan;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getFails() {
        return fails;
    }

    public void setFails(int fails) {
        this.fails = fails;
    }

    public int getGt60() {
        return gt60;
    }

    public void setGt60(int gt60) {
        this.gt60 = gt60;
    }

    public int getMaxduration() {
        return maxduration;
    }

    public void setMaxduration(int maxduration) {
        this.maxduration = maxduration;
    }

    public int getAvgduration() {
        return avgduration;
    }

    public void setAvgduration(int avgduration) {
        this.avgduration = avgduration;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }
}
