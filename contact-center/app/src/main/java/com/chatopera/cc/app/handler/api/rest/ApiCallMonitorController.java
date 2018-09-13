/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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
import com.chatopera.cc.aggregation.MathHelper;
import com.chatopera.cc.exception.CallOutRecordException;
import com.chatopera.cc.app.model.*;
import com.chatopera.cc.app.persistence.repository.*;
import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.handler.api.request.RestUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 通话记录
 */
@RestController
@RequestMapping("/api/callout/monitor")
@Api(value = "语音渠道坐席监控", description = "监控语音渠道使用情况，支持监听等操作。")
public class ApiCallMonitorController extends Handler {
    private static final Logger logger = LoggerFactory.getLogger(ApiCallMonitorController.class);
    private HashOperations<String, String, String> redisHashOps;

    /**
     * 使用StringRedisTemplate而不是RedisTemplate解决序列化问题
     * https://stackoverflow.com/questions/13215024/weird-redis-key-with-spring-data-jedis
     */
    @Autowired
    private StringRedisTemplate redis;

    @Autowired
    private SNSAccountRepository snsAccountRes;

    @Autowired
    private OrganRepository organRes;

    @Autowired
    private UserRepository userRes;

    @Autowired
    private AgentStatusRepository agentStatusRes;

    @Autowired
    private StatusEventRepository statusEventRes;

    @PostConstruct
    private void init() {
        redisHashOps = redis.opsForHash();
    }


    private AgentStatus getAgentStatus(final String agentId, final String orgi) {
        List<AgentStatus> x = agentStatusRes.findByAgentnoAndOrgi(agentId, orgi);
        if (x.size() > 0)
            return x.get(0);
        return null;
    }

    /**
     * 获取正在通话的数据
     *
     * @param agentId
     * @param sip
     * @param status
     * @return
     */
    private JsonObject getStatusEvent(final String agentId, final String sip, final String status) {
        JsonObject j = new JsonObject();
        StatusEvent s = statusEventRes.findByAgentAndSiptrunkAndStatus(agentId, sip, status);
        if (s != null) {
            j.addProperty("callid", s.getCallid());
            j.addProperty("called", s.getCalled());
            j.addProperty("direction", s.getDirection());
            // 格式化开始时间
            j.addProperty("createtime", Constants.DISPLAY_DATE_FORMATTER.format(s.getCreatetime()));
            // 增加持续时间
            j.addProperty("duration", MathHelper.formatSecondsBetweenTwoDates(s.getCreatetime(), null));
        }
        return j;
    }

    /**
     * @param organ
     * @return
     */
    private String getOrganName(final String organ) {
        if (StringUtils.isBlank(organ))
            return "未设置";

        Organ o = organRes.findOne(organ);
        if (o == null) {
            return "";
        } else {
            return o.getName();
        }
    }

    /**
     * 查询通话记录
     *
     * @return
     */
    private JsonObject status(final JsonObject j) {
        JsonObject resp = new JsonObject();
        try {
            if (j.has("channel")) {
                final String channel = j.get("channel").getAsString();
                SNSAccount snsAccount = snsAccountRes.findBySnsid(channel);
                if (snsAccount == null) {
                    resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_5);
                    resp.addProperty(RestUtils.RESP_KEY_ERROR, "不存在该语音渠道。");
                    return resp;
                }

                // 检查organ
                String organ = null;
                if (j.has("organ")) {
                    organ = j.get("organ").getAsString();
                    if (StringUtils.isBlank(organ)) {
                        resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_6);
                        resp.addProperty(RestUtils.RESP_KEY_MSG, "部门参数是空字符串，不合法。");
                        return resp;
                    }
                    if (organRes.findOne(organ) == null) {
                        resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_6);
                        resp.addProperty(RestUtils.RESP_KEY_MSG, "不存在该部门。");
                        return resp;
                    }
                }

                // 从Redis中取数据
                Map<String, String> pbxSipStatusMap = redisHashOps.entries(String.format(Constants.FS_SIP_STATUS, channel));
                if (pbxSipStatusMap.keySet().size() == 0) {
                    resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_6);
                    resp.addProperty(RestUtils.RESP_KEY_ERROR, "软交换系统不存在该语音渠道的数据。");
                    return resp;
                }

                // 获取到数据，返回
                JsonArray ja = new JsonArray();

                // 客服列表
                List<User> agents = userRes.findAllByCallcenterIsTrueAndDatastatusIsFalseAndOrgan(organ);

                for (User g : agents) {
                    JsonObject x = new JsonObject();
                    x.addProperty("name", g.getUname());
                    x.addProperty("organ", getOrganName(g.getOrgan()));

                    AgentStatus as = getAgentStatus(g.getId(), MainContext.SYSTEM_ORGI);
                    if (as == null) {
                        // 离线客服
                        x.addProperty("web", MainContext.AgentStatusEnum.OFFLINE.zh());
                    } else {
                        x.addProperty("web", as.isBusy() ? MainContext.AgentStatusEnum.BUSY.zh() : MainContext.AgentStatusEnum.IDLE.zh());
                    }

                    String sipaccount = g.getSipaccount();
                    if (StringUtils.isNotBlank(sipaccount)) {
                        String sipstatus = pbxSipStatusMap.get(sipaccount);
                        if (StringUtils.isNotBlank(sipstatus)) {
                            x.addProperty("sip", sipaccount);
                            x.addProperty("status", sipstatus);
                            if (Constants.FS_LEG_INCALL_ZH.equals(sipstatus))
                                x.add("current", getStatusEvent(g.getId(), sipaccount, MainContext.CallServiceStatus.INCALL.toString()));
                        } else {
                            x.addProperty("sip", sipaccount);
                            x.addProperty("status", MainContext.CallServiceStatus.OFFLINE.toString());
                        }
                    } else {
                        x.addProperty("sip", "未设置");
                        x.addProperty("status", "无");
                    }
                    ja.add(x);
                }

                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                resp.add(RestUtils.RESP_KEY_DATA, ja);
            } else {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "缺少请求参数 [语音渠道标识]。");
            }
        } catch (Exception e) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "检索数据返回异常。");
            logger.error("[callout monitor] 检索数据返回异常 {}", j.toString(), e);
        }
        return resp;
    }

    /**
     * 获取SIP账号状态
     *
     * @param channel
     * @param sipaccount
     * @return
     */
    private String getSipStatus(String channel, String sipaccount) {
        logger.info("[callout monitor] getSipStatus hash {}, key {}", String.format(Constants.FS_SIP_STATUS, channel), sipaccount);
        String s = redisHashOps.get(String.format(Constants.FS_SIP_STATUS, channel), sipaccount);
        if (s == null)
            return "SIP账号未注册到语音网关或离线中。";
        if (s.equals("空闲"))
            return null;
        return String.format("SIP账号的状态为【%s】，该状态不能完成监听。", s);
    }

    /**
     * 监听
     *
     * @param request
     * @param j
     * @return
     */
    private JsonObject dropin(HttpServletRequest request, final JsonObject j) {
        logger.info("[callout] dropin data {}", j.toString());
        JsonObject resp = new JsonObject();
        String callid = null;
        String channel = null;
        // 分析参数
        if (!j.has("callid")) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "通话ID参数不存在。");
            return resp;
        } else {
            callid = j.get("callid").getAsString();
        }

        if (!j.has("channel")) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "语音渠道参数不存在。");
            return resp;
        } else {
            channel = j.get("channel").getAsString();
            if (snsAccountRes.findBySnsid(channel) == null) {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_6);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "语音渠道不存在。");
                return resp;
            }
        }

        User current = super.getUser(request);
        if (current == null) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "无法解析登录账号。");
            return resp;
        }

        logger.info("[callout monitor] current isCallcenter {} , getSipaccount {}", current.isCallcenter(), current.getSipaccount());
        if (current.isCallcenter() && StringUtils.isNotBlank(current.getSipaccount())) {
            String status = getSipStatus(channel, current.getSipaccount());
            if (status != null) {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_7);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, status);
                return resp;
            }

            ChannelTopic ct = new ChannelTopic(String.format(Constants.FS_CHANNEL_CC_TO_FS, channel));
            JsonObject payload = new JsonObject();
            payload.addProperty("ops", "monitor");
            payload.addProperty("uuid", callid);
            payload.addProperty("sip", current.getSipaccount());
            payload.addProperty("channel", channel);
            redis.convertAndSend(ct.getTopic(), payload.toString());
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
            resp.addProperty(RestUtils.RESP_KEY_DATA, "监听任务已经下发，如果该线路还没有被监听，将您接入。");
        } else {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_5);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "未绑定SIP账号，无法监听。");
            return resp;
        }

        return resp;
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
    public ResponseEntity<String> execute(HttpServletRequest request, @RequestBody final String body) throws CallOutRecordException {
        logger.info("[callout monitor] raw payload {}", body);
        final JsonObject j = (new JsonParser()).parse(body).getAsJsonObject();
        JsonObject json = new JsonObject();
        HttpHeaders headers = RestUtils.header();

        if (!j.has("ops")) {
            json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_1);
            json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的请求参数。");
        } else {
            switch (StringUtils.lowerCase(j.get("ops").getAsString())) {
                case "status": // 根据语音渠道获取状态
                    json = status(j);
                    break;
                case "dropin":
                    json = dropin(request, j);
                    break;
                default:
                    json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
                    json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的操作。");
            }
        }
        return new ResponseEntity<String>(json.toString(), headers, HttpStatus.OK);
    }


}
