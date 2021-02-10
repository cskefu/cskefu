package com.chatopera.cc.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Collections;
import java.util.List;

public class PugHelper {
    public String formatDate(String pattern, Date value) {
        if (value == null) {
            return "";
        }

        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(value);
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

    public String toJSON(Object input) {
        return JSON.toJSON(input).toString();
    }

    public ArrayList<Integer> range(Integer start, Integer end) {
        ArrayList<Integer> result = new ArrayList<Integer>();
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
}
