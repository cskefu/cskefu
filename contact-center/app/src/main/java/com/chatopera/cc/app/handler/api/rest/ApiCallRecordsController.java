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
package com.chatopera.cc.app.handler.api.rest;

import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.util.Constants;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.aggregation.CallOutHangupAggsResult;
import com.chatopera.cc.aggregation.CallOutHangupAuditResult;
import com.chatopera.cc.aggregation.MathHelper;
import com.chatopera.cc.exception.CallOutRecordException;
import com.chatopera.cc.app.persistence.repository.OrganRepository;
import com.chatopera.cc.app.persistence.repository.SNSAccountRepository;
import com.chatopera.cc.app.persistence.repository.StatusEventRepository;
import com.chatopera.cc.app.persistence.repository.UserRepository;
import com.chatopera.cc.app.persistence.storage.MinioService;
import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.handler.api.request.RestUtils;
import com.chatopera.cc.app.model.Organ;
import com.chatopera.cc.app.model.SNSAccount;
import com.chatopera.cc.app.model.StatusEvent;
import com.chatopera.cc.app.model.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.minio.errors.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.xmlpull.v1.XmlPullParserException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 通话记录
 */
@RestController
@RequestMapping("/api/callout/records")
@Api(value = "通话记录", description = "按条件查询通话记录")
public class ApiCallRecordsController extends Handler {
    private static final Logger logger = LoggerFactory.getLogger(ApiCallRecordsController.class);

    @Autowired
    StatusEventRepository statusEventRes;

    @Autowired
    MinioService minioService;

    @Autowired
    SNSAccountRepository snsAccountRes;

    @Autowired
    OrganRepository organRes;

    @Autowired
    UserRepository userRes;

    /**
     * 处理日期格式
     *
     * @return
     */
    private Date enddate(final JsonObject j) throws ParseException {
        if (j.has("enddate")) {
            Date end = Constants.QUERY_DATE_FORMATTER.parse(j.get("enddate").getAsString());
            return end;
        }
        return null;
    }

    /**
     * 处理日期格式
     *
     * @return
     */
    private Date fromdate(final JsonObject j) throws ParseException {
        if (j.has("fromdate")) {
            Date from = Constants.QUERY_DATE_FORMATTER.parse(j.get("fromdate").getAsString());
            return from;
        }
        return null;
    }

    /**
     * 查询通话记录
     *
     * @return
     */
    private JsonObject query(final HttpServletRequest request, final JsonObject j) {
        JsonObject resp = new JsonObject();
        Date fromdate, enddate;

        try {
            // 分析查询起止日期
            try {
                fromdate = fromdate(j);
            } catch (ParseException e) {
                fromdate = null;
            }

            try {
                enddate = enddate(j);
            } catch (ParseException e) {
                enddate = null;
            }

            if ((fromdate != null) && (enddate != null)) {
                if (fromdate.after(enddate)) {
                    resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
                    resp.addProperty(RestUtils.RESP_KEY_ERROR, "开始日期不得晚于结束日期。");
                    return resp;
                }
            }


            // 其他查询参数
            String organ = j.has("organ") ? j.get("organ").getAsString() : null;
            String agent = j.has("agent") ? j.get("agent").getAsString() : null;
            String called = j.has("called") ? j.get("called").getAsString() : null;

            Page<StatusEvent> records = statusEventRes.queryCalloutDialplanSuccRecords(fromdate,
                    DateUtils.addDays(enddate, 1),
                    organ,
                    agent,
                    called,
                    MainContext.CallTypeEnum.OUT.toString(), // 呼出
                    MainContext.CallServiceStatus.HANGUP.toString(), // 挂机
                    null, // Dialplan，null代表所有外呼
                    new PageRequest(super.getP(request), super.getPs(request), Sort.Direction.DESC, new String[]{"createtime"}));

            JsonArray ja = new JsonArray();
            for (StatusEvent record : records) {
                JsonObject jo = new JsonObject();
                jo.addProperty("id", record.getId());
                jo.addProperty("name", record.getName()); // 访客名字
                jo.addProperty("duration", record.getDuration()); // 通话时间，秒
                jo.addProperty("called", record.getCalled()); // 被叫号码
                jo.addProperty("calledcity", record.getCity());
                jo.addProperty("calledprovince", record.getProvince());
                jo.addProperty("agent", record.getAgent()); // 坐席ID
                jo.addProperty("agentname", record.getAgentname()); // 坐席名字
                jo.addProperty("calltype", record.getCalltype()); // 呼叫类型
                jo.addProperty("direction", record.getDirection()); // 呼叫方向
                jo.addProperty("starttime", Constants.DISPLAY_DATE_FORMATTER.format(record.getStarttime())); // 开始时间
                jo.addProperty("endtime", Constants.DISPLAY_DATE_FORMATTER.format(record.getEndtime())); // 结束时间
                jo.addProperty("organ", record.getOrgan()); // 部门名字
                jo.addProperty("organid", record.getOrganid()); // 部门ID
                jo.addProperty("recordingfile", record.getRecordingfile()); // 录音文件标识
                jo.addProperty("status", record.getStatus()); // 状态代码
                ja.add(jo);
            }

            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
            resp.add("data", ja);
            resp.addProperty("size", records.getSize()); // 每页条数
            resp.addProperty("number", records.getNumber()); // 当前页
            resp.addProperty("totalPage", records.getTotalPages()); // 所有页
            resp.addProperty("totalElements", records.getTotalElements()); // 所有检索结果数量
        } catch (Exception e) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "检索数据返回异常。");
            logger.error("[callout records] 检索数据返回异常 {}", j.toString(), e);
        }
        return resp;
    }

    /**
     * 获取录音文件的路径
     *
     * @param j
     * @return
     */
    private JsonObject wav(JsonObject j) {
        JsonObject resp = new JsonObject();
        if (j.has("file")) {
            final String file = j.get("file").getAsString();
            try {
                String url = minioService.presignedGetObject(Constants.MINIO_BUCKET, file);
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                JsonObject data = new JsonObject();
                data.addProperty("url", url);
                resp.add(RestUtils.RESP_KEY_DATA, data);
            } catch (InvalidBucketNameException e) {
                logger.error("[callout records] 无效的bucket {}", j.toString(), e);
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "无效的存储桶");
            } catch (NoSuchAlgorithmException e) {
                logger.error("[callout records] NoSuchAlgorithmException {}", j.toString(), e);
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "获取文件地址失败，请联系管理员。");
            } catch (InsufficientDataException e) {
                logger.error("[callout records] 数据损坏 {}", j.toString(), e);
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "该数据已损坏。");
            } catch (IOException e) {
                logger.error("[callout records] 读写异常 {}", j.toString(), e);
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "文件读写异常。");
            } catch (InvalidKeyException e) {
                logger.error("[callout records] 密钥无效。 {}", j.toString(), e);
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "获取文件密钥无效，请联系管理员。");
            } catch (NoResponseException e) {
                logger.error("[callout records] 存储服务无返回。 {}", j.toString(), e);
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "存储服务无返回。");
            } catch (XmlPullParserException e) {
                logger.error("[callout records] XML解析错误 {}", j.toString(), e);
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "XML解析错误。");
            } catch (ErrorResponseException e) {
                logger.error("[callout records] 存储服务错误返回 {}", j.toString(), e);
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "存储服务错误返回。");
            } catch (InternalException e) {
                logger.error("[callout records] 内部异常 {}", j.toString(), e);
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "内部异常。");
            } catch (InvalidExpiresRangeException e) {
                logger.error("[callout records] 过期时间设置错误 {}", j.toString(), e);
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "过期时间设置错误。");
            }
        } else {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "请求参数错误，没有文件标识。");
        }

        return resp;
    }

    /**
     * 验证聚合请求的参数
     *
     * @param j
     * @return
     */
    private String validateAggBody(JsonObject j) {
        // 检索语音渠道
        if (!j.has("channel")) {
            return "语音渠道标识参数不存在。";
        } else {
            SNSAccount snsAccount = snsAccountRes.findBySnsid(j.get("channel").getAsString());
            if (snsAccount == null) // 不存在该渠道
                return "该语音渠道不存在。";
        }

        // 检索日期
        if (!j.has("datestr")) {
            return "日期参数不存在。";
        } else {
            try {
                Constants.QUERY_DATE_FORMATTER.parse(j.get("datestr").getAsString());
            } catch (ParseException e) {
                return "日期参数不合法。";
            }
        }

        // 呼叫类型
        if (!j.has("direction")) {
            return "呼叫类型参数不存在。";
        } else if (Constants.CALL_DIRECTION_TYPES.contains(j.get("direction").getAsString())) {
            return null;
        } else {
            return "呼叫类型不合法。";
        }
    }

    /**
     * 外呼日报：返回聚合数据
     *
     * @param j
     * @return
     */
    private JsonObject agg(final JsonObject j) {
        JsonObject resp = new JsonObject();
        String valid = validateAggBody(j);

        // 参数有误
        if (valid != null) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, valid);
            return resp;
        }

        // 解析参数
        final String channel = j.get("channel").getAsString();
        final String datestr = j.get("datestr").getAsString();
        final String direction = j.get("direction").getAsString();

        List<Object[]> aggResult = statusEventRes.queryCallOutHangupAggsGroupByDialplanByDatestrAndChannelAndDirection(datestr, channel, direction);
        logger.info("[callout records] aggResult size {}", aggResult.size());

        List<CallOutHangupAggsResult> results = new ArrayList<CallOutHangupAggsResult>();

        // 数据格式转化
        for (Object[] x : aggResult) {
            CallOutHangupAggsResult result = null;
            try {
                logger.info("[callout records] 外呼日报[raw] dialplan {}, datestr {}, total {}, fails {}, totalDuration {}", x[0], x[1], x[2], x[3], x[4]);
                result = CallOutHangupAggsResult.cast(x);
                results.add(result);
            } catch (CallOutRecordException e) {
                logger.error("[callout records] 数据报表生成失败 {}", j, e);
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "数据报表生成失败，请联系管理员。");
                return resp;
            }
        }

        // 生成返回值
        int callout_auto = 0; // 自动外呼
        int callout_manu = 0; // 手动外呼
        int callout_auto_fails = 0; // 自动外呼失败
        int callout_manu_fails = 0; // 手动外呼失败
        int callout_auto_duration_seconds = 0; // 自动外呼分钟数
        int callout_manu_duration_seconds = 0; // 手动外呼分钟数

        for (CallOutHangupAggsResult z : results) {
            if (StringUtils.isNotBlank(z.getDialplan())) { // 自动外呼
//                logger.info("[callout records] 自动外呼 {}", z.getDialplan());
                callout_auto += z.getTotal();
                callout_auto_fails += z.getFails();
                callout_auto_duration_seconds += z.getDuration();
            } else { // 手动外呼
//                logger.info("[callout records] 手动外呼 {}", z.getDialplan());
                callout_manu += z.getTotal();
                callout_manu_fails += z.getFails();
                callout_manu_duration_seconds += z.getDuration();
            }
        }

        final int callout_all = callout_auto + callout_manu;
        final int callout_all_fails = callout_auto_fails + callout_manu_fails;
        final int callout_all_succ = callout_all - callout_all_fails;
        final int callout_all_duration_seconds = callout_auto_duration_seconds + callout_manu_duration_seconds;

        final int callout_auto_succ = callout_auto - callout_auto_fails;
        final int callout_manu_succ = callout_manu - callout_manu_fails;

        // 呼通率
        String callout_all_succ_per = MathHelper.float_percentage_formatter(callout_all_succ, callout_all); // 所有外呼
        String callout_auto_succ_per = MathHelper.float_percentage_formatter(callout_auto_succ, callout_auto); // 自动外呼
        String callout_manu_succ_per = MathHelper.float_percentage_formatter(callout_manu_succ, callout_manu); // 手动外呼

        JsonObject data = new JsonObject();
        JsonObject data_all = new JsonObject();
        JsonObject data_manu = new JsonObject();
        JsonObject data_auto = new JsonObject();

        data_auto.addProperty("total", callout_auto);
        data_auto.addProperty("fails", callout_auto_fails);
        data_auto.addProperty("succ", callout_auto_succ);
        data_auto.addProperty("succ_percentage", callout_auto_succ_per);
        data_auto.addProperty("duration", Constants.DURATION_MINS_FORMATTER.format((float) callout_auto_duration_seconds / 60));


        data_manu.addProperty("total", callout_manu);
        data_manu.addProperty("fails", callout_manu_fails);
        data_manu.addProperty("succ", callout_manu_succ);
        data_manu.addProperty("succ_percentage", callout_manu_succ_per);
        data_manu.addProperty("duration", Constants.DURATION_MINS_FORMATTER.format((float) callout_manu_duration_seconds / 60));


        data_all.addProperty("total", callout_all);
        data_all.addProperty("fails", callout_all_fails);
        data_all.addProperty("succ", callout_all_succ);
        data_all.addProperty("succ_percentage", callout_all_succ_per);
        data_all.addProperty("duration", Constants.DURATION_MINS_FORMATTER.format((float) callout_all_duration_seconds / 60));

        data.add("all", data_all);
        data.add("manu", data_manu);
        data.add("auto", data_auto);
        resp.add("data", data);
        resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
        return resp;
    }


    /**
     * 验证坐席报表参数
     *
     * @param j
     * @return
     */
    private String validateAuditBody(JsonObject j) {
        if (!j.has("channel")) {
            return "语音渠道参数错误。";
        } else {
            if (snsAccountRes.findBySnsid(j.get("channel").getAsString()) == null)
                return "该语音渠道不存在。";
        }

        if (j.has("organ")) {
            if (organRes.findByIdAndOrgi(j.get("organ").getAsString(), MainContext.SYSTEM_ORGI) == null)
                return "该部门不存在。";
        }

        if (!j.has("fromdate"))
            return "开始日期参数不存在。";

        if (!j.has("enddate"))
            return "结束日期参数不存在。";

        try {
            Date fromdate = Constants.QUERY_DATE_FORMATTER.parse(j.get("fromdate").getAsString());
            Date enddate = Constants.QUERY_DATE_FORMATTER.parse(j.get("enddate").getAsString());
            if (fromdate.after(enddate))
                return "开始日期不得晚于结束日期。";
        } catch (ParseException e) {
            return "日期格式错误。";
        }

        return null;
    }


    /**
     * 坐席报表
     *
     * @param j
     * @return
     */
    private JsonObject audit(final JsonObject j) {
        JsonObject resp = new JsonObject();

        // 验证数据格式
        final String valid = validateAuditBody(j);
        if (valid != null) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, valid);
            return resp;
        }

        // 解析参数
        final String channel = j.get("channel").getAsString();
        final String fromdate = j.get("fromdate").getAsString();
        String enddate = null;
        try {
            enddate = Constants.QUERY_DATE_FORMATTER.format(DateUtils.addDays(enddate(j), 1));
        } catch (ParseException e) {
        }

        final String organ = j.has("organ")? j.get("organ").getAsString() : null;

        List<Object[]> z = statusEventRes.queryCalloutHangupAuditGroupByAgentAndDirection(channel,
                fromdate,
                enddate,
                organ,
                MainContext.SYSTEM_ORGI);

        // 查询结果序列化为聚合对象
        final Map<String, CallOutHangupAuditResult> out = new HashMap<String, CallOutHangupAuditResult>();
        final Map<String, CallOutHangupAuditResult> in = new HashMap<String, CallOutHangupAuditResult>();

        for (Object[] x : z) {
            try {
                logger.info("[callout records] audit raw {} {} {} {} {} {} {} {}", x[0], x[1], x[2], x[3], x[4], x[5], x[6], x[7]);
                CallOutHangupAuditResult y = CallOutHangupAuditResult.cast(x);
                switch (y.getDirection()) {
                    case "呼出":
                        out.put(y.getAgentId(), y);
                        break;
                    case "呼入":
                        in.put(y.getAgentId(), y);
                        break;
                    default:
                        break;
                }
            } catch (CallOutRecordException e) {
                logger.error("[callout records] ", e);
            }
        }

        final SetUtils.SetView<String> x = SetUtils.union(out.keySet(), in.keySet());
        final Map<String, JsonObject> k = new HashMap<String, JsonObject>();
        final Map<String, Integer> v = new HashMap<String, Integer>();

        JsonArray data = new JsonArray();
        for (String y : x) {
            try {
                JsonObject metric = new JsonObject();
                CallOutHangupAuditResult o = out.get(y);
                CallOutHangupAuditResult i = in.get(y);
                CallOutHangupAuditResult mix = CallOutHangupAuditResult.mix(o, i);

                metric.addProperty("name", mix.getAgentName());
                metric.addProperty("id", mix.getAgentId());
                metric.addProperty("organ", getOrganByAgentId(mix.getAgentId()));
                metric.add("total", mix.toJson(false, false, false));

                if (i != null) {
                    metric.add("in", i.toJson(false, false, false));
                }

                if (o != null) {
                    metric.add("out", o.toJson(false, false, false));
                }
                k.put(mix.getAgentId(), metric);
                v.put(mix.getAgentId(), mix.getSeconds());
            } catch (CallOutRecordException e) {
                logger.error("[callout audit] error ", e);
            }
        }

        // sort
        final List<Map.Entry<String, Integer>> s = v
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());

        int r = 0;
        for (Map.Entry<String, Integer> i : s) {
            JsonObject f = k.get(i.getKey());
            f.addProperty("rank", ++r);
            data.add(f);
        }

        resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
        resp.add("data", data);
        return resp;
    }


    /**
     * 根据用户ID获取部门名称
     *
     * @param agentId
     * @return
     */
    private String getOrganByAgentId(String agentId) {
        User u = userRes.findById(agentId);
        if (u != null) {
            if (u.getOrgan() == null)
                return null;
            Organ organ = organRes.findOne(u.getOrgan());
            if (organ != null) {
                return organ.getName();
            }
        }
        return null;
    }

    /**
     * 通话记录
     *
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @Menu(type = "apps", subtype = "callout", access = true)
    @ApiOperation("通话记录查询")
    public ResponseEntity<String> operations(HttpServletRequest request, @RequestBody final String body) throws CallOutRecordException {
        final JsonObject j = (new JsonParser()).parse(body).getAsJsonObject();
        logger.info("[callout records] operations payload {}", j.toString());
        JsonObject json = new JsonObject();
        HttpHeaders headers = RestUtils.header();

        if (!j.has("ops")) {
            json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_1);
            json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的请求参数。");
        } else {
            switch (StringUtils.lowerCase(j.get("ops").getAsString())) {
                case "query": // 通话记录查询
                    json = query(request, j);
                    break;
                case "wav":   // 获取录音文件地址
                    json = wav(j);
                    break;
                case "agg":   // 外呼日报
                    json = agg(j);
                    break;
                case "audit": // 坐席报表
                    json = audit(j);
                    break;
                default:
                    json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
                    json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的操作。");
            }
        }
        return new ResponseEntity<String>(json.toString(), headers, HttpStatus.OK);
    }


}
