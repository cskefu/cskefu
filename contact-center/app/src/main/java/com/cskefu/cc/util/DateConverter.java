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
package com.cskefu.cc.util;

import org.apache.commons.beanutils.converters.DateTimeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter extends DateTimeConverter {

    final public static String ZONE_ID_DEFAULT = "Asia/Shanghai";
    // format date string like `Wed Aug 30 16:30:23 CST 2023` to Date
    public static SimpleDateFormat TIMEZONE_CHINA_FORMAT_DEFAULT = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

    public DateConverter() {
    }

    public DateConverter(Object defaultValue) {
        super(defaultValue);
    }

    /* (non-Javadoc)
     * @see org.apache.commons.beanutils.converters.AbstractConverter#getDefaultType()
     */
    @SuppressWarnings("rawtypes")
    protected Class getDefaultType() {
        return Date.class;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.beanutils.converters.DateTimeConverter#convertToType(java.lang.Class, java.lang.Object)
     */
    @SuppressWarnings("rawtypes")
    @Override
    protected Object convertToType(Class arg0, Object arg1) throws Exception {
        if (arg1 == null) {
            return null;
        }
        String value = arg1.toString().trim();
        if (value.length() == 0) {
            return null;
        }
        return super.convertToType(arg0, arg1);
    }

    /**
     * Java将CST的时间字符串转换成需要的日期格式字符串
     * https://blog.csdn.net/qq_44868502/article/details/103511505
     * (new Date()).toString() 与 String to Date 的转化
     *
     * @param dstr
     * @return
     * @throws ParseException
     */
    static public Date parseCSTAsChinaTimezone(final String dstr) throws ParseException {
        return (Date) TIMEZONE_CHINA_FORMAT_DEFAULT.parse(dstr);
    }
}  