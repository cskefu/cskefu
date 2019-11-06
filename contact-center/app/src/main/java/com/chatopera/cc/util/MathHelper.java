/*
 * Copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
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

import java.util.Date;

public class MathHelper {

    public static final String FLOAT_PERCENTAGE_FORMATTER = "%.2f%%";
    public static final String FLOAT_PERCENTAGE_INVALID = "NaN%";
    public static final String FLOAT_PERCENTAGE_ZERO = "0.0%";

    /**
     * 计算两个int类型的数字的百分比字符串
     *
     * @param molecule
     * @param denominator
     * @return
     */
    public static String float_percentage_formatter(final int molecule, final int denominator) {
        String r = String.format(FLOAT_PERCENTAGE_FORMATTER, 100 * ((float) molecule / denominator));
        if (FLOAT_PERCENTAGE_INVALID.equals(r))
            r = FLOAT_PERCENTAGE_ZERO;
        return r;
    }


    public static String formatSecondsBetweenTwoDates(Date pre, Date d){
        if(d == null)
            d = new Date();
        return MathHelper.formatSeconds(((d.getTime() - pre.getTime()) / 1000));
    }

    public static String formatSeconds(Long timeInLong){
        Long l = new Long(timeInLong);
        return MathHelper.formatSeconds(l.intValue());
    }

    public static String formatSeconds(int timeInSeconds)
    {
        int hours = timeInSeconds / 3600;
        int secondsLeft = timeInSeconds - hours * 3600;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - minutes * 60;

        StringBuffer sb = new StringBuffer();
        if (hours < 10)
            sb.append(0);
        sb.append(hours);
        sb.append(":");

        if (minutes < 10)
            sb.append(0);
        sb.append(minutes);
        sb.append(":");

        if (seconds < 10)
            sb.append(0);
        sb.append(seconds);

        return sb.toString();
    }

}
