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
package com.chatopera.cc.controller.api;

import com.chatopera.cc.acd.ACDAgentDispatcher;
import com.chatopera.cc.acd.ACDAgentService;
import com.chatopera.cc.acd.basic.ACDComposeContext;
import com.chatopera.cc.acd.basic.ACDMessageHelper;
import com.chatopera.cc.basic.MainContext.*;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.controller.api.request.RestUtils;
import com.chatopera.cc.exception.CSKefuException;
import com.chatopera.cc.model.*;
import com.chatopera.cc.peer.PeerSyncIM;
import com.chatopera.cc.persistence.repository.AgentServiceRepository;
import com.chatopera.cc.persistence.repository.AgentUserRepository;
import com.chatopera.cc.persistence.repository.UserRepository;
import com.chatopera.cc.proxy.AgentUserProxy;
import com.chatopera.cc.socketio.message.Message;
import com.chatopera.cc.util.Menu;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * ACD服务 获取当前对话中的访客
 */
@RestController
@RequestMapping("/api/agentuser")
public class ApiAgentUserController extends Handler {

    private final static Logger logger = LoggerFactory.getLogger(ApiAgentUserController.class);

    @Autowired
    private ACDMessageHelper acdMessageHelper;

    @Autowired
    private AgentUserProxy agentUserProxy;

    @Autowired
    private ACDAgentService acdAgentService;

    @Autowired
    private Cache cache;

    @Autowired
    private PeerSyncIM peerSyncIM;

    @Autowired
    private AgentUserRepository agentUserRes;

    @Autowired
    private UserRepository userRes;

    @Autowired
    private AgentServiceRepository agentServiceRes;

    @Autowired
    private ACDAgentDispatcher acdAgentDispatcher;

    /**
     * 获取当前对话中的访客
     * 坐席相关 RestAPI
     *
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @Menu(type = "apps", subtype = "agentuser", access = true)
    public ResponseEntity<String> operations(HttpServletRequest request, @RequestBody final String body, @Valid String q) {
        logger.info("[operations] body {}, q {}", body, q);
        final JsonObject j = StringUtils.isBlank(body) ? (new JsonObject()) : (new JsonParser()).parse(
                body).getAsJsonObject();
        JsonObject json = new JsonObject();
        HttpHeaders headers = RestUtils.header();

        if (!j.has("ops")) {
            json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_1);
            json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的请求参数。");
        } else {
            switch (StringUtils.lowerCase(j.get("ops").getAsString())) {
                case "inserv":
                    json = inserv(request, j);
                    break;
                case "withdraw":
                    json = withdraw(request, j);
                    break;
                case "end":
                    json = end(request, j);
                    break;
                case "transout":
                    json = transout(request, j);
                    break;
                default:
                    json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
                    json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的操作。");
            }
        }

        return new ResponseEntity<String>(json.toString(), headers, HttpStatus.OK);
    }

    /**
     * 执行坐席转接
     * 将会话转接给别人
     *
     * @param request
     * @param payload
     * @return
     */
    private JsonObject transout(final HttpServletRequest request, final JsonObject payload) {
        logger.info("[transout] payload ", payload.toString());
        final String orgi = super.getOrgi(request);
        final User logined = super.getUser(request);
        JsonObject resp = new JsonObject();

        /**
         * 必填参数
         */
        // 目标坐席
        final String transAgentId = payload.get("agentno").getAsString();
        // 当前会话的ID
        final String agentUserId = payload.get("agentUserId").getAsString();
        // 坐席服务ID
        final String agentServiceId = payload.get("agentServiceId").getAsString();

        if (StringUtils.isNotBlank(agentUserId) &&
                StringUtils.isNotBlank(transAgentId) &&
                StringUtils.isNotBlank(agentServiceId)) {
            final User targetAgent = userRes.findOne(transAgentId);
            final AgentService agentService = agentServiceRes.findByIdAndOrgi(agentServiceId, orgi);

            /**
             * 更新AgentUser
             */
            final AgentUser agentUser = agentUserProxy.findOne(agentUserId).orElseGet(null);
            if (agentUser != null) {
                final AgentUserAudit agentAudits = cache.findOneAgentUserAuditByOrgiAndId(orgi, agentUserId).orElseGet(
                        null);

                // 当前服务于访客的坐席
                final String currentAgentno = agentUser.getAgentno();
                // 当前访客的ID
                final String userId = agentUser.getUserid();

                logger.info(
                        "[transout] agentuserid {} \n target agent id {}, \n current agent id {}, onlineuserid {}",
                        agentUserId, transAgentId, currentAgentno, userId);


                // 检查权限
                if ((!logined.isAdmin()) && (!StringUtils.equals(
                        agentUser.getAgentno(),
                        logined.getId())) && (!isTransPermissionAllowed(
                        agentAudits, logined))) {
                    // 1. 不是超级用户；2. 也是不是会话的所有者; 3. 也不是坐席监控人员
                    logger.info("[end] Permission not fulfill.");
                    resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
                    resp.addProperty(RestUtils.RESP_KEY_ERROR, "Permission denied.");
                    return resp;
                }

                agentUser.setAgentno(transAgentId);
                agentUser.setAgentname(targetAgent.getUname());
                agentUserRes.save(agentUser);

                /**
                 * 坐席状态
                 */
                // 转接目标坐席
                final AgentStatus transAgentStatus = cache.findOneAgentStatusByAgentnoAndOrig(transAgentId, orgi);

                // 转接源坐席
                final AgentStatus currentAgentStatus = cache.findOneAgentStatusByAgentnoAndOrig(currentAgentno, orgi);

                if (StringUtils.equals(
                        AgentUserStatusEnum.INSERVICE.toString(),
                        agentUser.getStatus())) { //转接 ， 发送消息给 目标坐席
                    // 更新当前坐席的服务访客列表
                    if (currentAgentStatus != null) {
                        cache.deleteOnlineUserIdFromAgentStatusByUseridAndAgentnoAndOrgi(userId, currentAgentno, orgi);
                        agentUserProxy.updateAgentStatus(currentAgentStatus, orgi);
                    }

                    if (transAgentStatus != null) {
                        agentService.setAgentno(transAgentId);
                        agentService.setAgentusername(transAgentStatus.getUsername());
                    }

                    // 转接坐席提示消息
                    Message outMessage = new Message();
                    outMessage.setMessage(
                            acdMessageHelper.getSuccessMessage(agentService, agentUser.getChannel(), orgi));
                    outMessage.setMessageType(MediaType.TEXT.toString());
                    outMessage.setCalltype(CallType.IN.toString());
                    outMessage.setCreatetime(MainUtils.dateFormate.format(new Date()));
                    outMessage.setAgentUser(agentUser);
                    outMessage.setAgentService(agentService);

                    if (StringUtils.isNotBlank(agentUser.getUserid())) {
                        peerSyncIM.send(
                                ReceiverType.VISITOR,
                                ChannelType.toValue(agentUser.getChannel()),
                                agentUser.getAppid(),
                                MessageType.STATUS,
                                agentUser.getUserid(),
                                outMessage,
                                true);
                    }

                    // 通知转接消息给新坐席
                    outMessage.setChannelMessage(agentUser);
                    outMessage.setAgentUser(agentUser);
                    peerSyncIM.send(
                            ReceiverType.AGENT, ChannelType.WEBIM,
                            agentUser.getAppid(), MessageType.NEW, agentService.getAgentno(),
                            outMessage, true);

                    // 通知消息给前坐席
                    if (!StringUtils.equals(logined.getId(), currentAgentno)) {
                        // 如果当前坐席不是登录用户，因为登录用户会从RestAPI返回转接的结果
                        // 该登录用户可能是坐席监控或当前坐席，那么，如果是坐席监控，就有必要
                        // 通知前坐席这个事件
                        peerSyncIM.send(ReceiverType.AGENT, ChannelType.WEBIM, agentUser.getAppid(),
                                        MessageType.TRANSOUT,
                                        currentAgentno, outMessage, true);
                    }
                }

                if (agentService != null) {
                    agentService.setAgentno(transAgentId);
                    if (payload.has("memo") && StringUtils.isNotBlank(payload.get("memo").getAsString())) {
                        agentService.setTransmemo(payload.get("memo").getAsString());
                    }
                    agentService.setTrans(true);
                    agentService.setTranstime(new Date());
                    agentServiceRes.save(agentService);
                }

                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                resp.addProperty(RestUtils.RESP_KEY_DATA, "success");
            } else {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "Can not find agent user.");
            }
        } else {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_5);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "Invalid params.");
        }

        return resp;
    }

    /**
     * 结束对话
     * 如果当前对话属于登录用户或登录用户为超级用户，则可以结束这个对话
     *
     * @param request
     * @param payload
     * @return
     */
    private JsonObject end(final HttpServletRequest request, final JsonObject payload) {
        logger.info("[end] payload {}", payload.toString());
        final String orgi = super.getOrgi(request);
        final User logined = super.getUser(request);
        JsonObject resp = new JsonObject();

        final AgentUser agentUser = agentUserRes.findByIdAndOrgi(payload.get("id").getAsString(), orgi);
        if (agentUser != null) {
            if ((StringUtils.equals(
                    logined.getId(), agentUser.getAgentno()) || logined.isAdmin())) {
                // 删除访客-坐席关联关系，包括缓存
                try {
                    acdAgentService.finishAgentUser(agentUser, orgi);
                } catch (CSKefuException e) {
                    // 未能删除成功
                    logger.error("[end]", e);
                }
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                resp.addProperty(RestUtils.RESP_KEY_DATA, "success");
            } else {
                logger.info("[end] Permission not fulfill.");
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "Permission denied.");
            }
        } else {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "Agent User not found.");
        }

        return resp;
    }

    /**
     * 撤退一个坐席
     * 将当前坐席服务中的访客分配给其他就绪的坐席
     *
     * @param request
     * @param j
     * @return
     */
    private JsonObject withdraw(final HttpServletRequest request, final JsonObject j) {
        JsonObject resp = new JsonObject();
        ACDComposeContext ctx = new ACDComposeContext();
        ctx.setAgentno(super.getUser(request).getId());
        ctx.setOrgi(super.getOrgi(request));
        acdAgentDispatcher.dequeue(ctx);
        resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
        return resp;
    }


    /**
     * 获得当前访客服务中的访客信息
     * 获取当前正在对话的访客信息，包含多种渠道来源的访客
     *
     * @param request
     * @param j
     * @return
     */
    private JsonObject inserv(final HttpServletRequest request, final JsonObject j) {
        JsonObject resp = new JsonObject();
        JsonArray data = new JsonArray();

        List<AgentUser> lis = cache.findInservAgentUsersByAgentnoAndOrgi(
                super.getUser(request).getId(), super.getOrgi(request));
        for (final AgentUser au : lis) {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", au.getId());
            obj.addProperty("userid", au.getUserid());
            obj.addProperty("status", au.getStatus());
            obj.addProperty("agentno", au.getAgentno());
            obj.addProperty("channel", au.getChannel());
            obj.addProperty("nickname", au.getNickname());
            data.add(obj);
        }
        resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
        resp.add("data", data);

        return resp;
    }

    /**
     * 检查是否具备该会话的坐席监控权限
     *
     * @param agentUserAudit
     * @param user
     * @return
     */
    private boolean isTransPermissionAllowed(final AgentUserAudit agentUserAudit, final User user) {
        if (agentUserAudit != null && agentUserAudit.getSubscribers().containsKey(user.getId())) {
            return true;
        }
        return false;
    }
}
