/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.basic;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.chatopera.cc.model.*;
import com.chatopera.cc.persistence.repository.*;
import com.chatopera.cc.util.WebIMReport;
import com.chatopera.cc.util.*;
import com.chatopera.cc.util.asr.AsrResult;
import com.chatopera.cc.util.mail.MailSender;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.googlecode.aviator.AviatorEvaluator;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import io.netty.handler.codec.http.HttpHeaders;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.jasypt.util.text.BasicTextEncryptor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainUtils {
    private final static Logger logger = LoggerFactory.getLogger(MainUtils.class);

    private static MD5 md5 = new MD5();

    public static SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static SimpleDateFormat timeRangeDateFormat = new SimpleDateFormat("HH:mm");

    /**
     * 当前时间+已过随机生成的 长整形数字
     *
     * @return
     */
    public static String genID() {
        return Base62.encode(getUUID()).toLowerCase();
    }

    public static String genIDByKey(String key) {
        return Base62.encode(key).toLowerCase();
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getContextID(String session) {
        return session.replaceAll("-", "");
    }

    public static String md5(String str) {
        return md5.getMD5ofStr(md5.getMD5ofStr(str));
    }

    public static String md5(byte[] bytes) {
        return md5.getMD5ofByte(bytes);
    }

    public static void copyProperties(Object source, Object target, String... ignoreProperties)
            throws BeansException {

        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {
                PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null && !targetPd.getName().equalsIgnoreCase("id")) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null &&
                            ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source);
                            if (value != null) {  //只拷贝不为null的属性 by zhao
                                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                    writeMethod.setAccessible(true);
                                }
                                writeMethod.invoke(target, value);
                            }
                        } catch (Throwable ex) {
                            throw new FatalBeanException(
                                    "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }

    public static long ipToLong(String ipAddress) {
        long result = 0;
        String[] ipAddressInArray = ipAddress.split("\\.");
        if (ipAddressInArray != null && ipAddressInArray.length == 4) {
            for (int i = 3; i >= 0; i--) {
                long ip = Long.parseLong(ipAddressInArray[3 - i]);

                // left shifting 24,16,8,0 and bitwise OR

                // 1. 192 << 24
                // 1. 168 << 16
                // 1. 1 << 8
                // 1. 2 << 0
                result |= ip << (i * 8);

            }
        }
        return result;
    }

    public static String longToIp2(long ip) {

        return ((ip >> 24) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "."
                + ((ip >> 8) & 0xFF) + "." + (ip & 0xFF);
    }

    /***
     * ID编码 ， 发送对话的时候使用
     * @param id
     * @param nid
     * @return
     */
    public static String genNewID(String id, String nid) {
        StringBuffer strb = new StringBuffer();
        if (id != null && nid != null) {
            int length = Math.max(id.length(), nid.length());
            for (int i = 0; i < length; i++) {
                if (nid.length() > i && id.length() > i) {
                    int cur = (id.charAt(i) + nid.charAt(i)) / 2;
                    strb.append((char) cur);
                } else if (nid.length() > i) {
                    strb.append(nid.charAt(i));
                } else {
                    strb.append(id.charAt(i));
                }
            }
        }
        return strb.toString();
    }

    /**
     * @param request
     * @return
     */
    public static Map<String, Object> getRequestParam(HttpServletRequest request) {
        Map<String, Object> values = new HashMap<String, Object>();
        Enumeration<String> enums = request.getParameterNames();
        while (enums.hasMoreElements()) {
            String param = enums.nextElement();
            values.put(param, request.getParameter(param));
        }
        return values;
    }

    /**
     * @param request
     * @return
     */
    public static String getParameter(HttpServletRequest request) {
        Enumeration<String> names = request.getParameterNames();
        StringBuffer strb = new StringBuffer();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (name.indexOf("password") < 0) {    //不记录 任何包含 password 的参数内容
                if (strb.length() > 0) {
                    strb.append(",");
                }
                strb.append(name).append("=").append(request.getParameter(name));
            }
        }
        return strb.toString();

    }

    /**
     * 获取一天的开始时间
     *
     * @return
     */
    public static Date getStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    /**
     * 获取一天的开始时间
     *
     * @return
     */
    public static Date getWeekStartTime() {
        Calendar weekStart = Calendar.getInstance();
        weekStart.set(
                weekStart.get(Calendar.YEAR), weekStart.get(Calendar.MONDAY), weekStart.get(Calendar.DAY_OF_MONTH), 0,
                0, 0);
        weekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return weekStart.getTime();
    }

    /**
     * 获取一天的开始时间
     *
     * @return
     */
    public static Date getLast30Day() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.DAY_OF_MONTH, -30);
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    /**
     * 获取一天的开始时间
     *
     * @return
     */
    public static Date getLastDay(int days) {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.DAY_OF_MONTH, -days);
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    /**
     * 获取一天的结束时间
     *
     * @return
     */
    public static Date getEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    /**
     * 获取一天的结束时间
     *
     * @return
     */
    public static Date getLastTime(int secs) {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.add(Calendar.SECOND, secs * -1);
        return todayEnd.getTime();
    }

    public static void noCacheResponse(HttpServletResponse response) {
        response.setDateHeader("Expires", 0);
        response.setHeader("Buffer", "True");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Expires", "0");
        response.setHeader("ETag", String.valueOf(System.currentTimeMillis()));
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Date", String.valueOf(new Date()));
        response.setHeader("Last-Modified", String.valueOf(new Date()));
    }

    public static BrowserClient parseClient(HttpServletRequest request) {
        BrowserClient client = new BrowserClient();
        String browserDetails = request.getHeader("User-Agent");
        String userAgent = browserDetails;
        String user = userAgent.toLowerCase();
        String os = "";
        String browser = "", version = "";


        //=================OS=======================
        if (userAgent.toLowerCase().indexOf("windows") >= 0) {
            os = "windows";
        } else if (userAgent.toLowerCase().indexOf("mac") >= 0) {
            os = "mac";
        } else if (userAgent.toLowerCase().indexOf("x11") >= 0) {
            os = "unix";
        } else if (userAgent.toLowerCase().indexOf("android") >= 0) {
            os = "android";
        } else if (userAgent.toLowerCase().indexOf("iphone") >= 0) {
            os = "iphone";
        } else {
            os = "UnKnown";
        }
        //===============Browser===========================
        if (user.contains("qqbrowser")) {
            browser = "QQBrowser";
        } else if (user.contains("msie") || user.indexOf("rv:11") > -1) {
            if (user.indexOf("rv:11") >= 0) {
                browser = "IE11";
            } else {
                String substring = userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0];
                browser = substring.split(" ")[0].replace("MSIE", "IE") + substring.split(" ")[1];
            }
        } else if (user.contains("trident")) {
            browser = "IE 11";
        } else if (user.contains("edge")) {
            browser = "Edge";
        } else if (user.contains("safari") && user.contains("version")) {
            browser = (userAgent.substring(userAgent.indexOf("Safari")).split(" ")[0]).split("/")[0];
            version = (userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
        } else if (user.contains("opr") || user.contains("opera")) {
            if (user.contains("opera")) {
                browser = (userAgent.substring(userAgent.indexOf("Opera")).split(" ")[0]).split(
                        "/")[0] + "-" + (userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
            } else if (user.contains("opr")) {
                browser = ((userAgent.substring(userAgent.indexOf("OPR")).split(" ")[0]).replace("/", "-")).replace(
                        "OPR", "Opera");
            }
        } else if (user.contains("chrome")) {
            browser = "Chrome";
        } else if ((user.indexOf("mozilla/7.0") > -1) || (user.indexOf("netscape6") != -1) || (user.indexOf(
                "mozilla/4.7") != -1) || (user.indexOf("mozilla/4.78") != -1) || (user.indexOf(
                "mozilla/4.08") != -1) || (user.indexOf("mozilla/3") != -1)) {
            //browser=(userAgent.substring(userAgent.indexOf("MSIE")).split(" ")[0]).replace("/", "-");
            browser = "Netscape-?";

        } else if ((user.indexOf("mozilla") > -1)) {
            //browser=(userAgent.substring(userAgent.indexOf("MSIE")).split(" ")[0]).replace("/", "-");
            if (browserDetails.indexOf(" ") > 0) {
                browser = browserDetails.substring(0, browserDetails.indexOf(" "));
            } else {
                browser = "Mozilla";
            }

        } else if (user.contains("firefox")) {
            browser = (userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
        } else if (user.contains("rv")) {
            browser = "ie";
        } else {
            browser = "UnKnown";
        }
        client.setUseragent(browserDetails);
        client.setOs(os);
        client.setBrowser(browser);
        client.setVersion(version);

        return client;
    }

    /**
     * 活动JPA统计结果
     *
     * @param values
     * @return
     */
    public static WebIMReport getWebIMReport(List<Object> values) {
        WebIMReport report = new WebIMReport();
        if (values != null && values.size() > 0) {
            Object[] value = (Object[]) values.get(0);
            if (value.length >= 2) {
                report.setIpnums((long) value[0]);
                report.setPvnums((long) value[1]);
            }
        }
        return report;
    }

    /**
     * 活动JPA统计结果
     *
     * @param values
     * @return
     */
    public static WebIMReport getWebIMInviteStatus(List<Object> values) {
        WebIMReport report = new WebIMReport();
        if (values != null && values.size() > 0) {

            for (int i = 0; i < values.size(); i++) {
                Object[] value = (Object[]) values.get(i);
                if (value.length >= 2) {
                    String invitestatus = (String) value[0];
                    if (MainContext.OnlineUserInviteStatus.DEFAULT.toString().equals(
                            invitestatus) || invitestatus == null) {
                        report.setUsers((long) value[1]);
                    } else if (MainContext.OnlineUserInviteStatus.INVITE.toString().equals(invitestatus)) {
                        report.setInviteusers((long) value[1]);
                    } else if (MainContext.OnlineUserInviteStatus.REFUSE.toString().equals(invitestatus)) {
                        report.setRefuseusers((long) value[1]);
                    }
                }
            }
        }
        return report;
    }

    /**
     * 活动JPA统计结果
     *
     * @param values
     * @return
     */
    public static List<WebIMReport> getWebIMInviteAgg(List<Object> values) {
        List<WebIMReport> webIMReportList = new ArrayList<WebIMReport>();
        if (values != null && values.size() > 0) {
            for (int i = 0; i < values.size(); i++) {
                Object[] value = (Object[]) values.get(i);
                WebIMReport report = new WebIMReport();
                if (value.length == 3) {
                    report.setData((String) value[0]);
                    report.setIpnums((long) value[1]);
                    report.setPvnums((long) value[2]);
                }
                webIMReportList.add(report);
            }
        }
        return webIMReportList;
    }

    /**
     * 活动JPA统计结果
     *
     * @param values
     * @return
     */
    public static List<WebIMReport> getWebIMDataAgg(List<Object> values) {
        List<WebIMReport> webIMReportList = new ArrayList<WebIMReport>();
        if (values != null && values.size() > 0) {
            for (int i = 0; i < values.size(); i++) {
                Object[] value = (Object[]) values.get(i);
                WebIMReport report = new WebIMReport();
                if (value.length == 2) {
                    if (value[0] == null || value[0].toString().equalsIgnoreCase("null") || StringUtils.isBlank(value[0].toString())) {
                        report.setData("其他");
                    } else {
                        report.setData((String) value[0]);
                    }
                    report.setUsers((long) value[1]);
                }
                webIMReportList.add(report);
            }
        }
        return webIMReportList;
    }

    /**
     * 活动JPA统计结果
     *
     * @param values
     * @return
     */
    public static WebIMReport getWebIMInviteResult(List<Object> values) {
        WebIMReport report = new WebIMReport();
        if (values != null && values.size() > 0) {

            for (int i = 0; i < values.size(); i++) {
                Object[] value = (Object[]) values.get(i);
                if (value.length >= 2) {
                    String invitestatus = (String) value[0];
                    if (MainContext.OnlineUserInviteStatus.DEFAULT.toString().equals(
                            invitestatus) || invitestatus == null) {
                        report.setUsers((long) value[1]);
                    } else if (MainContext.OnlineUserInviteStatus.ACCEPT.toString().equals(invitestatus)) {
                        report.setInviteusers((long) value[1]);
                    } else if (MainContext.OnlineUserInviteStatus.REFUSE.toString().equals(invitestatus)) {
                        report.setRefuseusers((long) value[1]);
                    }
                }
            }
        }
        return report;
    }

    /**
     * 活动JPA统计结果
     *
     * @param values
     * @return
     */
    public static WeiXinReport getWeiXinReportResult(List<Object> values) {
        WeiXinReport report = new WeiXinReport();
        if (values != null && values.size() > 0) {
            for (int i = 0; i < values.size(); i++) {
                Object[] value = (Object[]) values.get(i);
                if (value.length >= 2) {
                    String event = (String) value[0];
                    if (MainContext.WeiXinEventType.SUB.toString().equals(event)) {
                        report.setSubs((long) value[1]);
                    } else if (MainContext.WeiXinEventType.UNSUB.toString().equals(event)) {
                        report.setUnsubs((long) value[1]);
                    }
                }
            }
        }
        return report;
    }

    public static Map<String, Object> transBean2Map(Object obj) {

        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性  
                if (!key.equals("class")) {
                    // 得到property对应的getter方法 

                    Method readMethod = property.getReadMethod();

                    if (readMethod != null) {
                        Object value = readMethod.invoke(obj);
                        if (value instanceof Date) {
                            value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) value);
                        }
                        map.put(key, value);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("transBean2Map Error " + e);
        }

        return map;

    }

    public static void populate(Object bean, Map<Object, Object> properties) throws IllegalAccessException, InvocationTargetException {
        ConvertUtils.register(new Converter() {
            @SuppressWarnings("rawtypes")
            @Override
            public Object convert(Class arg0, Object arg1) {
                if (arg1 == null) {
                    return null;
                }
                if (arg1 instanceof Date) {
                    return arg1;
                } else if (!(arg1 instanceof String)) {
                    throw new ConversionException("只支持字符串转换 !");
                }
                String str = (String) arg1;
                if (str.trim().equals("")) {
                    return null;
                }

                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                try {
                    return sd.parse(str);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

        }, java.util.Date.class);
        if (properties == null || bean == null) {
            return;
        }
        try {
            BeanUtilsBean.getInstance().populate(bean, properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] toBytes(Object object) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream objectOutput = new ObjectOutputStream(out);
        objectOutput.writeObject(object);
        return out.toByteArray();
    }

    public static Object toObject(byte[] data) throws Exception {
        ByteArrayInputStream input = new ByteArrayInputStream(data);
        ObjectInputStream objectInput = new ObjectInputStream(input);
        return objectInput.readObject();
    }

    /**
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String encryption(final String str) throws NoSuchAlgorithmException {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(MainContext.getSystemSecrityPassword());
        return textEncryptor.encrypt(str);
    }

    /**
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String decryption(final String str) throws NoSuchAlgorithmException {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(MainContext.getSystemSecrityPassword());
        return textEncryptor.decrypt(str);
    }

    public static String getTopic(final String snsid, final String msgtype, final String eventype, final String eventkey, final String msg) {
        StringBuffer strb = new StringBuffer();
        strb.append(snsid);
        strb.append(".").append(msgtype);
        if (msgtype.equals("text")) {
            strb.append(".").append(msg);
        } else if (msgtype.equals("exchange")) {
            strb.append(".").append(eventype.toLowerCase());
            if (StringUtils.isNotBlank(eventkey)) {
                strb.append(".").append(eventkey);
            }
        } else {
            strb.append(".").append(msgtype);
        }
        return strb.toString();
    }

    public static String getTopic(String snsid, String msgtype, String eventype) {
        StringBuffer strb = new StringBuffer();
        strb.append(snsid);
        strb.append(".").append(msgtype);
        if (msgtype.equals("text")) {
            strb.append(".").append(msgtype);
        } else if (msgtype.equals("exchange")) {
            strb.append(".").append(eventype.toLowerCase());
        } else {
            strb.append(".").append(msgtype);
        }
        return strb.toString();
    }

    /**
     * 处理 对话消息中的图片
     *
     * @param message
     * @return
     */
    public static String filterChatMessage(String message) {
        Document document = Jsoup.parse(message);
        Elements pngs = document.select("img[src]");
        for (Element element : pngs) {
            String imgUrl = element.attr("src");
            if (imgUrl.indexOf("/res/image") >= 0) {
                element.attr("class", "ukefu-media-image");
            }
        }
        return document.html();
    }

    /**
     * 检查当前时间是否是在 时间范围内 ，时间范围的格式为 ： 08:30~11:30,13:30~17:30
     *
     * @param timeRanges
     * @return
     */
    public static boolean isInWorkingHours(String timeRanges) {
        boolean workintTime = true;
        String timeStr = timeRangeDateFormat.format(new Date());
        if (StringUtils.isNotBlank(timeRanges)) {        //设置了 工作时间段
            workintTime = false;                    //将 检查结果设置为 False ， 如果当前时间是在 时间范围内，则 置为 True
            String[] timeRange = timeRanges.split(",");
            for (String tr : timeRange) {
                String[] timeGroup = tr.split("~");
                if (timeGroup.length == 2) {
                    if (timeGroup[0].compareTo(timeGroup[1]) >= 0) {
                        if (timeStr.compareTo(timeGroup[0]) >= 0 || timeStr.compareTo(timeGroup[1]) <= 0) {
                            workintTime = true;
                        }
                    } else {
                        if (timeStr.compareTo(timeGroup[0]) >= 0 && timeStr.compareTo(timeGroup[1]) <= 0) {
                            workintTime = true;
                        }
                    }
                }
            }
        }
        return workintTime;
    }

    public static File processImage(final File destFile, final File imageFile) throws IOException {
        if (imageFile != null && imageFile.exists()) {
            Thumbnails.of(imageFile).width(460).keepAspectRatio(true).toFile(destFile);
        }
        return destFile;
    }

    public static File scaleImage(final File destFile, final File imageFile, float quality) throws IOException {
        if (imageFile != null && imageFile.exists()) {
            Thumbnails.of(imageFile).scale(1f).outputQuality(quality).toFile(destFile);
        }
        return destFile;
    }

    public static String processEmoti(String message) {
        Pattern pattern = Pattern.compile("\\[([\\d]*?)\\]");
        SystemConfig systemConfig = MainContext.getCache().findOneSystemByIdAndOrgi(
                "systemConfig", MainContext.SYSTEM_ORGI);

        Matcher matcher = pattern.matcher(message);
        StringBuffer strb = new StringBuffer();
        while (matcher.find()) {
            if (systemConfig != null && StringUtils.isNotBlank(systemConfig.getIconstr())) {
                matcher.appendReplacement(
                        strb,
                        "<img src='" + systemConfig.getIconstr() + "/im/js/kindeditor/plugins/emoticons/images/" + matcher.group(
                                1) + ".png'>");
            } else {
                matcher.appendReplacement(
                        strb, "<img src='/im/js/kindeditor/plugins/emoticons/images/" + matcher.group(1) + ".png'>");
            }
        }
        matcher.appendTail(strb);
        if (strb.length() == 0) {
            strb.append(message);
        }
        return strb.toString().replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "[表情]");
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getIpAddr(HttpHeaders headers, String remoteAddr) {
        String ip = headers.get("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.get("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.get("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = remoteAddr;
        }
        return ip;
    }

    public static boolean secConfirm(SecretRepository secRes, String orgi, String confirm) {
        /**
         * 先调用 IMServer
         */
        boolean execute = false;
        List<Secret> secretConfig = secRes.findByOrgi(orgi);
        if (StringUtils.isNotBlank(confirm)) {
            if (secretConfig != null && secretConfig.size() > 0) {
                Secret secret = secretConfig.get(0);
                if (MainUtils.md5(confirm).equals(secret.getPassword())) {
                    execute = true;
                }
            }
        } else if (secretConfig.size() == 0) {
            execute = true;
        }
        return execute;
    }

    /**
     * 获取系统配置
     *
     * @return
     */
    public static SystemConfig getSystemConfig() {
        SystemConfig systemConfig = MainContext.getCache().findOneSystemByIdAndOrgi(
                "systemConfig", MainContext.SYSTEM_ORGI);
        if (systemConfig == null) {
            SystemConfigRepository systemConfigRes = MainContext.getContext().getBean(SystemConfigRepository.class);
            systemConfig = systemConfigRes.findByOrgi(MainContext.SYSTEM_ORGI);
        }
        return systemConfig;
    }

    /**
     * 初始化呼叫中心功能里需要隐藏号码的字段
     *
     * @param tpRes
     */
    public static void initSystemSecField(TablePropertiesRepository tpRes) {
        if (tpRes != null) {
            List<TableProperties> tpList = tpRes.findBySecfield(true);
            MainContext.getCache().putSystemListByIdAndOrgi(
                    Constants.CSKEFU_SYSTEM_SECFIELD, MainContext.SYSTEM_ORGI, tpList);
        }
    }

    /**
     * 获取系统地区配置
     *
     * @return
     */
    public static void initSystemArea() {
        MainContext.getCache().deleteSystembyIdAndOrgi(Constants.CSKEFU_SYSTEM_AREA, MainContext.SYSTEM_ORGI);
        AreaTypeRepository areaTypeRes = MainContext.getContext().getBean(AreaTypeRepository.class);
        MainContext.getCache().putSystemListByIdAndOrgi(
                Constants.CSKEFU_SYSTEM_AREA, MainContext.SYSTEM_ORGI, areaTypeRes.findAll());
    }

    /**
     * 缓存 广告位
     *
     * @return
     */
    public static void initAdv(String orgi) {
        MainContext.getCache().deleteSystembyIdAndOrgi(Constants.CSKEFU_SYSTEM_ADV + "_" + orgi, orgi);
        AdTypeRepository adRes = MainContext.getContext().getBean(AdTypeRepository.class);
        MainContext.getCache().putSystemListByIdAndOrgi(
                Constants.CSKEFU_SYSTEM_ADV + "_" + orgi, orgi, adRes.findByOrgi(orgi));
    }

    public static Template getTemplate(String id) {
        Template templet = null;
        if ((templet = MainContext.getCache().findOneSystemByIdAndOrgi(id, MainContext.SYSTEM_ORGI)) == null) {
            TemplateRepository templateRes = MainContext.getContext().getBean(TemplateRepository.class);
            templet = templateRes.findByIdAndOrgi(id, MainContext.SYSTEM_ORGI);
            MainContext.getCache().putSystemByIdAndOrgi(id, MainContext.SYSTEM_ORGI, templet);
        }
        return templet;
    }

    /**
     * 按照权重获取广告
     *
     * @param adpos
     * @return
     */
    @SuppressWarnings("unchecked")
    public static AdType getPointAdv(String adpos, String orgi) {
        List<AdType> adTypeList = new ArrayList<AdType>();
        List<AdType> cacheAdTypeList = MainContext.getCache().findOneSystemListByIdAndOrgi(
                Constants.CSKEFU_SYSTEM_ADV + "_" + orgi, orgi);
        if (cacheAdTypeList == null) {
            AdTypeRepository adRes = MainContext.getContext().getBean(AdTypeRepository.class);
            cacheAdTypeList = adRes.findByOrgi(orgi);
            MainContext.getCache().putSystemListByIdAndOrgi(
                    Constants.CSKEFU_SYSTEM_ADV + "_" + orgi, orgi, cacheAdTypeList);
        }
        List<SysDic> sysDicList = Dict.getInstance().getDic(Constants.CSKEFU_SYSTEM_ADPOS_DIC);
        SysDic sysDic = null;
        if (sysDicList != null) {
            for (SysDic dic : sysDicList) {
                if (dic.getCode().equals(adpos)) {
                    sysDic = dic;
                    break;
                }
            }
        }
        if (adTypeList != null && sysDic != null) {
            for (AdType adType : cacheAdTypeList) {
                if (adType.getAdpos().equals(sysDic.getId())) {
                    adTypeList.add(adType);
                }
            }
        }
        return weitht(adTypeList);
    }

    private static Random random = new Random();

    /**
     * 按照权重，获取广告内容
     *
     * @param adList
     * @return
     */
    private static AdType weitht(List<AdType> adList) {
        AdType adType = null;
        int weight = 0;
        if (adList != null && adList.size() > 0) {
            for (AdType ad : adList) {
                weight += ad.getWeight();
            }
            int n = random.nextInt(weight), m = 0;
            for (AdType ad : adList) {
                if (m <= n && n < m + ad.getWeight()) {
                    adType = ad;
                    break;
                }
                m += ad.getWeight();
            }
        }
        return adType;
    }

    /**
     * 16进制字符串转换为字符串
     *
     * @return
     */
    public static String string2HexString(String strPart) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < strPart.length(); i++) {
            int ch = (int) strPart.charAt(i);
            String strHex = Integer.toHexString(ch);
            hexString.append(strHex);
        }
        return hexString.toString();
    }

    /**
     * @throws IOException
     * @throws TemplateException
     */
    @SuppressWarnings("deprecation")
    public static String getTemplet(String templet, Map<String, Object> values) throws IOException, TemplateException {
        StringWriter writer = new StringWriter();
        Configuration cfg = null;
        freemarker.template.Template template = null;
        String retValue = templet;
        if (templet != null && templet.length() > 0 && templet.indexOf("$") >= 0) {
            cfg = new Configuration();
            TempletLoader loader = new TempletLoader(templet);
            cfg.setTemplateLoader(loader);
            cfg.setDefaultEncoding("UTF-8");
            template = cfg.getTemplate("");
            template.process(values, writer);
            retValue = writer.toString();
        }
        return retValue;
    }

    /**
     * 发送邮件
     *
     * @param email
     * @param cc
     * @param subject
     * @param content
     * @throws Exception
     */
    public static void sendMail(String email, String cc, String subject, String content, List<String> filenames) throws Exception {
        SystemConfig config = MainUtils.getSystemConfig();
        if (config != null && config.isEnablemail() && config.getEmailid() != null) {
            SystemMessage systemMessage = MainContext.getContext().getBean(
                    SystemMessageRepository.class).findByIdAndOrgi(config.getEmailid(), config.getOrgi());
            MailSender sender = new MailSender(
                    systemMessage.getSmtpserver(), systemMessage.getMailfrom(), systemMessage.getSmtpuser(),
                    decryption(systemMessage.getSmtppassword()), systemMessage.getSeclev(), systemMessage.getSslport());
            if (email != null) {
                sender.send(email, cc, subject, content, filenames);
            }
        }
    }

    public static String encode(Object obj) {
        Base64 base64 = new Base64();
        try {
            return base64.encodeToString(MainUtils.toBytes(obj));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String processContentEncode(String str) throws Exception {
        return Base64.encodeBase64String(str.getBytes(StandardCharsets.UTF_8)).replaceAll("\\+", "-");
    }

    public static String processContentDecode(String str) throws Exception {
        return new String(Base64.decodeBase64(str.replaceAll("-", "\\+").getBytes()), StandardCharsets.UTF_8);
    }

    /**
     * @param defaultFormatValue
     * @param text
     * @return
     */
    public static String processParam(String defaultFormatValue, String text) {
        String formatValue = "yyyy-MM-dd";
        if (text.matches("[ ]{0,}([Yy]{1,})[ ]{0,}[+-]{0,1}([\\d]{0,})")) {
            formatValue = "yyyy";
        } else if (text.matches("[ ]{0,}([Mm]{1,})[ ]{0,}[+-]{0,1}([\\d]{0,})")) {
            formatValue = "yyyy-MM";
        }

        return getDays(
                text, defaultFormatValue != null && defaultFormatValue.length() > 0 ? defaultFormatValue : formatValue);
    }

    /***
     * 计算T+1
     * @param text
     * @param format
     * @return
     */
    public static String getDays(String text, String format) {
        String retDateFormat = text;
        Pattern pattern = Pattern.compile("[ ]{0,}([TtMmYy]{1,})[ ]{0,}[+-]{0,1}([\\d]{0,})");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find() && matcher.groupCount() >= 1) {
            try {
                if (matcher.group(1).equalsIgnoreCase("T")) {
                    retDateFormat = formatDateValue(format, getDaysParam(text));
                } else if (matcher.group(1).equalsIgnoreCase("M")) {
                    retDateFormat = formatMonthValue(format, getDaysParam(text));
                } else if (matcher.group(1).equalsIgnoreCase("Y")) {
                    retDateFormat = String.valueOf((int) Double.parseDouble(String.valueOf(getDaysParam(text))));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return retDateFormat;
    }

    /***
     * 计算T+1
     * @param text
     * @return
     */
    public static Object getDaysParam(String text) {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("T", processDays());
        context.put("t", processDays());
        context.put("M", processMonth());
        context.put("m", processMonth());
        context.put("Y", processYear());
        context.put("y", processYear());

        return AviatorEvaluator.execute(text, context);
    }

    /**
     * @param value
     * @return
     * @throws ParseException
     * @throws Exception
     */
    public static String formatDateValue(String format, Object value) throws ParseException {
        if (value != null && value.toString().matches("[\\d.]{5,}")) {
            value = new SimpleDateFormat(format).format(
                    new Date((long) (Double.parseDouble(value.toString()) * 24 * 60 * 60 * 1000)));
        }
        return value != null ? value.toString() : "0";
    }

    /**
     * @param value
     * @return
     * @throws ParseException
     * @throws Exception
     */
    public static String formatMonthValue(String formatValue, Object value) throws ParseException {
        if (value != null && value.toString().matches("[\\d.]{3,}")) {
            int months = (int) Double.parseDouble(String.valueOf(value));
            int year = 0;
            int month = 0;
            if (months % 12 == 0) {
                year = months / 12 - 1;
                month = 12;
            } else {
                year = months / 12;
                month = months % 12;
            }
            if (month < 10) {
                value = year + "0" + month;
            } else {
                value = String.valueOf(year) + month;
            }
            value = new SimpleDateFormat(formatValue).format(
                    new SimpleDateFormat("yyyyMM").parse(String.valueOf(value)));
        }
        return value != null ? value.toString() : "0";
    }

    /**
     * @return
     */
    public static double processDays() {
        return System.currentTimeMillis() * 1.0f / (1000 * 60 * 60 * 24);
    }

    /**
     * @return
     */
    public static double processMonth() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.YEAR) * 12 + calendar.get(Calendar.MONTH) + 1;
        return month;
    }

    /**
     * @return
     */
    public static double processYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    private static final ObjectMapper JSON = new ObjectMapper();

    static {
        JSON.setSerializationInclusion(Include.NON_NULL);
        JSON.configure(SerializationFeature.INDENT_OUTPUT, Boolean.TRUE);
    }

    public static String toJson(Object obj) {
        try {
            return JSON.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param message
     */
    public static AsrResult parseAsrResult(String id, String message, int speakms) {
        AsrResult asrResult = null;
        Pattern pattern = Pattern.compile("([\\d]{1,})[\\.]{1}([\\s\\S]*);");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find() && matcher.groupCount() == 2) {
            asrResult = new AsrResult(id, matcher.group(2), matcher.group(1));
            if (asrResult.getMessage().endsWith("。")) {
                asrResult.setMessage(asrResult.getMessage().substring(0, asrResult.getMessage().length() - 1));
            }
        }
        if (speakms > 0 && asrResult != null) {
            asrResult.setSpeakms(speakms);
        }
        return asrResult;
    }

    /**
     * 发送短信
     *
     * @param phone
     * @param id
     * @param tpId
     * @param tplValuesMap
     * @return
     * @throws Exception
     */
    public static boolean sendSms(String phone, String id, String tpId, Map<String, Object> tplValuesMap) throws Exception {
        SystemConfig config = MainUtils.getSystemConfig();
        if (config != null) {
            SystemMessage systemMessage = MainContext.getContext().getBean(
                    SystemMessageRepository.class).findByIdAndOrgi(id, config.getOrgi());
            if (systemMessage == null) {
                return false;
            }
            Template tp = MainUtils.getTemplate(tpId);
            if (tp == null) {
                return false;
            }
            String params = MainUtils.getTemplet(tp.getTemplettext(), tplValuesMap);

            SysDic sysDic = Dict.getInstance().getDicItem(systemMessage.getSmstype());
            //阿里大于
            if (sysDic != null && "dysms".equals(sysDic.getCode())) {
                //设置超时时间-可自行调整
                System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
                System.setProperty("sun.net.client.defaultReadTimeout", "10000");
                //初始化ascClient需要的几个参数
                final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
                final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
                //替换成你的AK
                final String accessKeyId = systemMessage.getAppkey();//你的accessKeyId,参考本文档步骤2
                final String accessKeySecret = systemMessage.getAppsec();//你的accessKeySecret，参考本文档步骤2
                //初始化ascClient,暂时不支持多region（请勿修改）
                IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
                        accessKeySecret);
                DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
                IAcsClient acsClient = new DefaultAcsClient(profile);
                //组装请求对象
                SendSmsRequest request = new SendSmsRequest();
                //使用post提交
                request.setMethod(MethodType.POST);
                //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
                request.setPhoneNumbers(phone);
                //必填:短信签名-可在短信控制台中找到
                request.setSignName(systemMessage.getSign());
                //必填:短信模板-可在短信控制台中找到
                request.setTemplateCode(systemMessage.getTpcode());
                //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
                //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
                request.setTemplateParam(params);
                //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
                //request.setSmsUpExtendCode("90997");
                //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
                request.setOutId("yourOutId");
                //请求失败这里会抛ClientException异常
                SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
                if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
                    return true;
                } else if (StringUtils.isNotBlank(sendSmsResponse.getMessage())) {
                    try {
                        throw new Exception("短信发送失败，原因：" + sendSmsResponse.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    /**
     * @param userid
     * @param client
     * @param session
     * @param orgi
     * @param ipaddr
     * @param hostname
     * @return
     */
    public static WorkSession createWorkSession(String userid, String client, String session, String orgi, String ipaddr, String hostname, String admin, boolean first) {
        WorkSession workSession = new WorkSession();
        workSession.setCreatetime(new Date());
        workSession.setBegintime(new Date());
        workSession.setAgent(userid);
        workSession.setAgentno(userid);
        workSession.setAgentno(userid);
        if (StringUtils.isNotBlank(admin) && admin.equalsIgnoreCase("true")) {
            workSession.setAdmin(true);
        }

        workSession.setFirsttime(first);

        workSession.setIpaddr(ipaddr);
        workSession.setHostname(hostname);
        workSession.setUserid(userid);
        workSession.setClientid(client);
        workSession.setSessionid(session);
        workSession.setOrgi(orgi);

        workSession.setDatestr(MainUtils.simpleDateFormat.format(new Date()));

        return workSession;
    }

    /**
     * @param plan
     * @return
     */
    public static String convertCrond(JobTask plan) {
        StringBuffer strb = new StringBuffer();
        if ("day".equals(plan.getRunCycle())) {
            strb.append(plan.getRunBeginSecond()).append(" ").append(plan.getRunBeginMinute()).append(
                    plan.getIsRepeat() && plan.getRepeatSpace() != null && plan.getRepeatSpace() < 60 ? "/" + plan.getRepeatSpace() : "").append(
                    " ").append(plan.getRunBeginHour()).append(
                    plan.getIsRepeat() && plan.getRepeatSpace() != null && plan.getRepeatSpace() > 60 ? "/" + plan.getRepeatSpace() / 60 : (plan.getRepeatJustTime() != null && plan.getRepeatJustTime() > 0 ? "-" + (plan.getRunBeginHour() + plan.getRepeatJustTime()) : "")).append(
                    " ").append("*").append(
                    plan.getRunSpace() != null && plan.getRunSpace() > 0 ? "/" + plan.getRunSpace() : "").append(
                    " ").append(" * ?");
        }
        if ("week".equals(plan.getRunCycle())) {
            strb.append(plan.getRunBeginSecond()).append(" ").append(plan.getRunBeginMinute()).append(
                    plan.getIsRepeat() && plan.getRepeatSpace() != null && plan.getRepeatSpace() < 60 ? "/" + plan.getRepeatSpace() : "").append(
                    " ").append(plan.getRunBeginHour()).append(
                    plan.getIsRepeat() && plan.getRepeatSpace() != null && plan.getRepeatSpace() > 60 ? "/" + plan.getRepeatSpace() / 60 : (plan.getRepeatJustTime() != null && plan.getRepeatJustTime() > 0 ? "-" + (plan.getRunBeginHour() + plan.getRepeatJustTime()) : "")).append(
                    " ").append(plan.getRunDates() == null || plan.getRunDates().length == 0 ? "*" : "?").append(
                    " * ").append(plan.getRunDates() == null || plan.getRunDates().length == 0 ? "?" : StringUtils.join(
                    plan.getRunDates(), ",")).append(
                    plan.getRunSpace() != null && plan.getRunSpace() > 0 ? "/" + plan.getRunSpace() : "");
        }
        if ("month".equals(plan.getRunCycle())) {
            strb.append(plan.getRunBeginSecond()).append(" ").append(plan.getRunBeginMinute()).append(
                    plan.getIsRepeat() && plan.getRepeatSpace() != null && plan.getRepeatSpace() < 60 ? "/" + plan.getRepeatSpace() : "").append(
                    " ").append(plan.getRunBeginHour()).append(
                    plan.getIsRepeat() && plan.getRepeatSpace() != null && plan.getRepeatSpace() > 60 ? "/" + plan.getRepeatSpace() / 60 : (plan.getRepeatJustTime() != null && plan.getRepeatJustTime() > 0 ? "-" + (plan.getRunBeginHour() + plan.getRepeatJustTime()) : "")).append(
                    " ").append(plan.getRunBeginDate()).append(" ").append(
                    plan.getRunDates() == null || plan.getRunDates().length == 0 ? "*" : StringUtils.join(
                            plan.getRunDates(), ",")).append(" ").append(" ?");
        }
        return strb.toString();
    }

    public static Date updateTaskNextFireTime(JobDetail jobDetail) throws Exception {
        Date nextFireDate = new Date();
        Date date = new Date();
        if (jobDetail != null && jobDetail.getCronexp() != null && jobDetail.getCronexp().length() > 0) {
            try {
                nextFireDate = (CronTools.getFinalFireTime(
                        jobDetail.getCronexp(),
                        jobDetail.getNextfiretime() != null ? jobDetail.getNextfiretime() : date));
            } catch (ParseException e) {
                nextFireDate = new Date(
                        System.currentTimeMillis() + 1000 * 60 * 60 * 24);    //一旦任务的 Cron表达式错误，将下次执行时间自动设置为一天后，避免出现任务永远无法终止的情况
                e.printStackTrace();
            }
        }
        return nextFireDate;
    }

    /**
     * @param dialNum
     * @param distype
     * @return
     */
    public static String processSecField(String dialNum, String distype) {
        StringBuilder strb = new StringBuilder(dialNum);
        if (distype.equals("01")) {
            if (strb.length() > 4) {
                strb.replace(strb.length() / 2 - 2, strb.length() / 2 + 2, "****");
            } else {
                strb.replace(0, strb.length(), "****");
            }
        } else if (distype.equals("02")) {
            if (strb.length() > 4) {
                strb.replace(strb.length() - 4, strb.length(), "****");
            } else {
                strb.replace(0, strb.length(), "****");
            }
        } else if (distype.equals("03")) {
            if (strb.length() > 4) {
                strb.replace(0, 4, "****");
            } else {
                strb.replace(0, strb.length(), "****");
            }
        } else if (distype.equals("04")) {
            int length = strb.length();
            strb.setLength(0);
            for (int i = 0; i < length; i++) {
                strb.append("*");
            }
        }
        return strb.toString();
    }

    public static void putMapEntry(
            Map<String, String[]> map, String name,
            String value) {
        String[] newValues = null;
        String[] oldValues = (String[]) (String[]) map.get(name);
        if (oldValues == null) {
            newValues = new String[1];
            newValues[0] = value;
        } else {
            newValues = new String[oldValues.length + 1];
            System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
            newValues[oldValues.length] = value;
        }
        map.put(name, newValues);
    }

    public static byte convertHexDigit(byte b) {
        if ((b >= 48) && (b <= 57)) {
            return (byte) (b - 48);
        }
        if ((b >= 97) && (b <= 102)) {
            return (byte) (b - 97 + 10);
        }
        if ((b >= 65) && (b <= 70)) {
            return (byte) (b - 65 + 10);
        }
        return 0;
    }

}
