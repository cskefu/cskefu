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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;

public class CallOutHangupAggsResult {
    private final static Logger logger = LoggerFactory.getLogger(CallOutHangupAggsResult.class);
    private String dialplan;
    private String datestr;
    private int total;
    private int fails;
    private int duration;

    private CallOutHangupAggsResult() {

    }

    public CallOutHangupAggsResult(final String dialplan, // 呼叫计划
                                   final String datestr,  // 目标日期
                                   final int total,       // 总呼叫
                                   final int fails,       // 失败通话
                                   final int duration) {   // 总通话事件，秒
        this.dialplan = dialplan;
        this.datestr = datestr;
        this.total = total;
        this.fails = fails;
        this.duration = duration;
    }

    public static CallOutHangupAggsResult cast(Object[] x) throws CallOutRecordException {
        CallOutHangupAggsResult y = new CallOutHangupAggsResult();
        try {
            y.setDialplan((String) x[0]);
            y.setDatestr((String) x[1]);
            y.setTotal(((BigInteger) x[2]).intValue());
            y.setFails(((BigInteger) x[3]).intValue());
            y.setDuration(((BigDecimal) x[4]).intValue());
        } catch (Exception e) {
            logger.error("[callout agg] cast error", e);
            throw new CallOutRecordException("[Ljava.lang.Object; cannot be cast to " + CallOutHangupAggsResult.class.getSimpleName());
        }
        return y;
    }

    public String getDialplan() {
        return dialplan;
    }

    public void setDialplan(String dialplan) {
        this.dialplan = dialplan;
    }

    public String getDatestr() {
        return datestr;
    }

    public void setDatestr(String datestr) {
        this.datestr = datestr;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getFails() {
        return fails;
    }

    public void setFails(int fails) {
        this.fails = fails;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
