/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd.
 * <https://www.chatopera.com>, Licensed under the Chunsong Public
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cskefu.cc.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.chatopera.store.enums.LICSTATUS;
import com.chatopera.store.exceptions.EnumValueException;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

public class PugHelper {
    public final static String NA = "N/A";


    public String formatDate(String pattern, Date value) {
        try {
            if (value == null) {
                return NA;
            }

            SimpleDateFormat format = new SimpleDateFormat(pattern);
            String result = format.format(value);

            if (StringUtils.isBlank(result)) {
                return NA;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NA;
    }

    public String padRight(Object src, String ch) {
        return padRight(src.toString(), ch);
    }

    public String padRight(String src, String ch) {
        int len = ch.length();
        int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }

        char[] charr = ch.toCharArray();
        System.arraycopy(src.toCharArray(), 0, charr, diff, src.length());
        return new String(charr);
    }

    /**
     * 在字符串中替换一些字符为 *, 起到混淆、加密、遮盖的敏感信息的目的
     *
     * @param prev
     * @return
     */
    public String messupStringWithStars(final String prev) {
        StringBuffer sb = new StringBuffer();

        if (prev.length() >= 6) {
            sb.append("***");
            int initial = prev.length() - 4;
            for (int i = initial; i < prev.length(); i++) {
                sb.append(prev.charAt(i));
            }
        } else { // < 6
            if (prev.length() <= 2 && prev.length() > 0) {
                return "***";
            } else { // 2 < length < 6
                sb.append("***");
                int initial = prev.length() - 2;
                for (int i = initial; i < prev.length(); i++) {
                    sb.append(prev.charAt(i));
                }
            }
        }

        return sb.toString();
    }

    /**
     * 将 String 转化为 JSONArray
     *
     * @param str
     * @return
     */
    public JSONArray parseStringToJsonArray(final String str) {
        return JSON.parseArray(str);
    }

    public String toJSON(Object input) {
        return JSON.toJSON(input).toString();
    }

    public ArrayList<Integer> range(Integer start, Integer end) {
        ArrayList<Integer> result = new ArrayList<>();
        for (Integer i = start; i < end; i++) {
            result.add(i);
        }
        return result;
    }

    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public <T> List<T> reverse(Collection<T> input) {
        List<T> result = new ArrayList<>(input);
        Collections.reverse(result);
        return result;
    }

    /**
     * 获得证书状态的中文
     *
     * @param status
     * @return
     */
    public String getLicstatusInChinese(final String status) {
        try {
            LICSTATUS licstatus = LICSTATUS.toValue(status);
            switch (licstatus) {
                case NOTFOUND -> {
                    return "未找到";
                }
                case EXHAUSTED -> {
                    return "配额耗尽";
                }
                case INUSE -> {
                    return "使用中";
                }
                case EXPIRED -> {
                    return "已过期";
                }
                default -> {
                    return status;
                }
            }
        } catch (EnumValueException e) {
            return "未知";
        }
    }

    /**
     * 截取字符串，首先根据分隔符分隔，然后选取前 N 个，使用连接符连接返回
     *
     * @param orignal
     * @param splitBy
     * @param firstN
     * @param joinWith
     * @return
     */
    public String splitStringAndJoinWith(final String orignal, final String splitBy, final int firstN, final String joinWith) {
        String[] splits = StringUtils.split(orignal, splitBy);
        int n = Math.min(splits.length, firstN);
        List<String> joined = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            joined.add(splits[i]);
        }

        if (joined.size() > 0) {
            return StringUtils.join(joined, joinWith);
        } else {
            return "";
        }
    }
}
