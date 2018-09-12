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
import com.chatopera.cc.exception.CallOutRuntimeException;
import com.chatopera.cc.app.persistence.repository.CallOutDialplanRepository;
import com.chatopera.cc.app.persistence.repository.UserRepository;
import com.chatopera.cc.app.schedule.CallOutPlanTask;
import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.handler.api.request.RestUtils;
import com.chatopera.cc.app.model.CallOutDialplan;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/callout/dialplan")
@Api(value = "外呼系统服务", description = "管理外呼计划的执行过程")
public class ApiDailplanRunController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(ApiDailplanRunController.class);
    private HashOperations<String, String, String> redisHashOps;

    @Autowired
    private UserRepository userRes;

    @Autowired
    private CallOutDialplanRepository callOutDialplanRes;

    @Autowired
    private CallOutPlanTask callOutPlanTask;

    @Autowired
    private StringRedisTemplate redis;

    @PostConstruct
    private void init() {
        redisHashOps = redis.opsForHash();
    }


    /**
     * 通过部门ID查询Sips账号列表
     *
     * @param organ
     * @param orgi
     * @return
     */
    private JsonArray getSipsByOrgan(final String organ, final String orgi) {
        logger.info("[callout executor] getSipsByOrgan {}", organ);
        JsonArray j = new JsonArray();
        List<String> sips = userRes.findSipsByOrganAndDatastatusAndOrgi(organ, false, orgi);
        for (String sip : sips) {
            if (StringUtils.isNotBlank(sip))
                j.add(StringUtils.trim(sip));
        }

        logger.info("[callout executor] sips {}", j.toString());
        return j;
    }

    /**
     * 执行呼叫计划
     *
     * @param dp
     * @return
     */
    protected JsonObject execute(final CallOutDialplan dp) {
        JsonObject resp = new JsonObject();

        if (dp.isIsarchive()) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_5);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, String.format("当前呼叫计划已删除，该情况下无法启动。", dp.getStatus()));
        } else if (dp.getStatus().equals(MainContext.CallOutDialplanStatusEnum.STOPPED.toString())) {

            // 查询该技能组的SIP号码列表
            final JsonArray sips = getSipsByOrgan(dp.getOrgan().getId(), MainContext.SYSTEM_ORGI);

            if (sips.size() == 0) {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_6);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "当前呼叫计划的技能组不存在绑定SIP话机的客服人员。");
                return resp;
            }

            // 并发数，获得该部门的在线客服数 X 坐席外呼并发比
            final long countagent = userRes.countByOrgiAndAgentAndDatastatusAndOrgan(MainContext.SYSTEM_ORGI, true, false, dp.getOrgan().getId());
            final int concurrency = (int) Math.ceil(countagent * dp.getConcurrenceratio());
            logger.info("[callout executor] 并发数 {}", concurrency);
            if (concurrency >= 1) {
                dp.setExecuted(dp.getExecuted() + 1);
                // 非暂停中
                dp.setStatus(MainContext.CallOutDialplanStatusEnum.RUNNING.toString());
                dp.setCurconcurrence(concurrency);

                // 查看该计划是否为暂停中的计划
                String existed = redisHashOps.get(String.format(Constants.FS_DIALPLAN_STATUS, dp.getVoicechannel().getBaseURL()), dp.getId());
                if (existed != null) {
                    logger.info("[callout api] 【】 Redis中的前状态 {}", dp.getName(), existed);
                    JsonParser parser = new JsonParser();
                    JsonObject pre = parser.parse(existed).getAsJsonObject();
                    // 非RUNNING的状态
                    if (pre.has("status") && !(pre.get("status").getAsString().equals(MainContext.CallOutDialplanStatusEnum.RUNNING.toString()))) {
                        logger.info("[callout api] 从暂停状态恢复到启动 {} {}", dp.getName(), dp.getId());

                        // 设置持久化状态
                        pre.addProperty("concurrency", concurrency);
                        pre.addProperty("status", MainContext.CallOutDialplanStatusEnum.RUNNING.toString());
                        pre.addProperty("updatetime", (new Date()).toString());
                        pre.add("sips", sips);
                        redisHashOps.put(String.format(Constants.FS_DIALPLAN_STATUS, dp.getVoicechannel().getBaseURL()), dp.getId(), pre.toString());

                        // 发送信号
                        JsonObject payload = new JsonObject();
                        payload.addProperty("dialplan", dp.getId());
                        payload.addProperty("concurrency", concurrency);
                        payload.addProperty("ops", "start");
                        payload.addProperty("channel", dp.getVoicechannel().getBaseURL());
                        payload.add("sips", sips);
                        callOutPlanTask.publish(String.format(Constants.FS_CHANNEL_CC_TO_FS, dp.getVoicechannel().getBaseURL()), payload.toString());
                    } else {
                        logger.error("[callout api] Redis 存储的前状态不支持重新启动，或者该任务正在执行。 {}", pre.toString());
                    }
                } else {
                    logger.info("[callout api] 【{}】 Redis中不存在前状态，作为新任务开始。", dp.getName());
                    try {
                        callOutPlanTask.run(dp, sips);
                    } catch (CallOutRuntimeException e) {
                        logger.error("[callout api] 呼叫计划无法启动", e);
                    }
                }
                callOutDialplanRes.save(dp);
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                resp.addProperty(RestUtils.RESP_KEY_MSG, "开始执行");
            } else {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, String.format("当前该部门[%s]无坐席在线，该情况下无法启动。", dp.getOrgan().getName()));
            }
        } else {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, String.format("当前呼叫计划状态为 [%s]，该状态下无法启动。", dp.getStatus()));
        }
        return resp;
    }

    /**
     * 暂停执行中的呼叫计划
     *
     * @param dp
     * @return
     */
    private JsonObject pause(final CallOutDialplan dp) {
        JsonObject resp = new JsonObject();
        if (dp.getStatus().equals(MainContext.CallOutDialplanStatusEnum.RUNNING.toString())) {
            JsonObject payload = new JsonObject();
            payload.addProperty("dialplan", dp.getId());
            payload.addProperty("ops", "pause");
            payload.addProperty("channel", dp.getVoicechannel().getBaseURL());
            callOutPlanTask.publish(String.format(Constants.FS_CHANNEL_CC_TO_FS, dp.getVoicechannel().getBaseURL()), payload.toString());

            Date dt = new Date();
            JsonObject payload2 = new JsonObject();
            payload2.addProperty("concurrency", dp.getCurconcurrence());
            payload2.addProperty("status", MainContext.CallOutDialplanStatusEnum.STOPPED.toString());
            payload2.addProperty("channel", dp.getVoicechannel().getBaseURL());
            payload2.addProperty("updatetime", dt.toString());
            callOutPlanTask.setHashKeyValue(String.format(Constants.FS_DIALPLAN_STATUS, dp.getVoicechannel().getBaseURL()), dp.getId(), payload2.toString());

            dp.setUpdatetime(dt);
            dp.setStatus(MainContext.CallOutDialplanStatusEnum.STOPPED.toString());
            callOutDialplanRes.save(dp);
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
            resp.addProperty(RestUtils.RESP_KEY_MSG, "该呼叫计划被暂停。");
        } else {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "无法暂停非【执行中】的呼叫计划。");
        }
        return resp;
    }

    /**
     * 删除呼叫计划
     *
     * @param dp
     * @return
     */
    private JsonObject delete(final CallOutDialplan dp) {
        JsonObject resp = new JsonObject();
        if (!dp.isIsarchive()) {
            // 发送撤销信号
            JsonObject payload = new JsonObject();
            payload.addProperty("dialplan", dp.getId());
            payload.addProperty("ops", "cancel");
            payload.addProperty("channel", dp.getVoicechannel().getBaseURL());
            callOutPlanTask.publish(String.format(Constants.FS_CHANNEL_CC_TO_FS, dp.getVoicechannel().getBaseURL()), payload.toString());

            // 删除状态成员
            callOutPlanTask.delHashKey(String.format(Constants.FS_DIALPLAN_STATUS, dp.getVoicechannel().getBaseURL()), dp.getId());

            // 更新数据库
            dp.setStatus(MainContext.CallOutDialplanStatusEnum.STOPPED.toString());
            dp.setIsarchive(true);
            dp.setUpdatetime(new Date());
            callOutDialplanRes.save(dp);

            // 删除呼叫列表
            callOutPlanTask.delKey(String.format(Constants.FS_DIALPLAN_TARGET, dp.getVoicechannel().getBaseURL(), dp.getId()));

            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
            resp.addProperty(RestUtils.RESP_KEY_MSG, "该呼叫计划删除成功。");

        } else {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
            resp.addProperty(RestUtils.RESP_KEY_MSG, "该呼叫计划已经删除。");
        }
        return resp;
    }


    /**
     * 执行呼叫计划
     *
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @Menu(type = "apps", subtype = "callout", access = true)
    @ApiOperation("外呼计划操作")
    public ResponseEntity<String> execute(HttpServletRequest request, @RequestBody final String body) throws CallOutRuntimeException {
        final JsonObject j = (new JsonParser()).parse(body).getAsJsonObject();
        if (!(j.has("ops") && j.has("dialplanId")))
            throw new CallOutRuntimeException("Invalid body");
        final String ops = StringUtils.trim(j.get("ops").getAsString()).toLowerCase();
        final String dialplanId = StringUtils.trim(j.get("dialplanId").getAsString());
        JsonObject json = new JsonObject();
        HttpHeaders headers = RestUtils.header();

        if (callOutDialplanRes.existsById(dialplanId)) {
            CallOutDialplan dp = callOutDialplanRes.findOne(dialplanId);
            switch (ops) {
                case "execute":
                    json = execute(dp);
                    break;
                case "pause":
                    json = pause(dp);
                    break;
                case "delete":
                    json = delete(dp);
                    break;
                default:
                    json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_1);
                    json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的操作。");
            }
        } else {
            json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
            json.addProperty(RestUtils.RESP_KEY_ERROR, "该呼叫计划不存在。");
        }

        return new ResponseEntity<String>(json.toString(), headers, HttpStatus.OK);
    }
}
