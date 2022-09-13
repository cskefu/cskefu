/*
 * Copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
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
package com.cskefu.cc.schedule;

import com.cskefu.cc.acd.ACDAgentService;
import com.cskefu.cc.acd.ACDPolicyService;
import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.model.*;
import com.cskefu.cc.peer.PeerSyncIM;
import com.cskefu.cc.persistence.repository.AgentUserTaskRepository;
import com.cskefu.cc.persistence.repository.JobDetailRepository;
import com.cskefu.cc.persistence.repository.OnlineUserRepository;
import com.cskefu.cc.proxy.OnlineUserProxy;
import com.cskefu.cc.socketio.message.ChatMessage;
import com.cskefu.cc.socketio.message.Message;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
@EnableScheduling
public class WebIMTask {

    private final static Logger logger = LoggerFactory.getLogger(WebIMTask.class);

    @Autowired
    private ACDPolicyService acdPolicyService;

    @Autowired
    private ACDAgentService acdAgentService;

    @Autowired
    private AgentUserTaskRepository agentUserTaskRes;

    @Autowired
    private OnlineUserRepository onlineUserRes;

    @Autowired
    private JobDetailRepository jobDetailRes;

    @Autowired
    private TaskExecutor webimTaskExecutor;

    @Autowired
    private PeerSyncIM peerSyncIM;

    @Autowired
    private Cache cache;

    @Scheduled(fixedDelay = 5000, initialDelay = 20000) // 处理超时消息，每5秒执行一次
    public void task() {
        final List<SessionConfig> sessionConfigList = acdPolicyService.initSessionConfigList();
        if (sessionConfigList != null && sessionConfigList.size() > 0 && MainContext.getContext() != null) {
            for (final SessionConfig sessionConfig : sessionConfigList) {
                if (sessionConfig.isSessiontimeout()) {        //设置了启用 超时提醒
                    final List<AgentUserTask> agentUserTask = agentUserTaskRes.findByLastmessageLessThanAndStatusAndOrgi(
                            MainUtils.getLastTime(sessionConfig.getTimeout()),
                            MainContext.AgentUserStatusEnum.INSERVICE.toString(), sessionConfig.getOrgi());
                    for (final AgentUserTask task : agentUserTask) {        // 超时未回复
                        cache.findOneAgentUserByUserIdAndOrgi(
                                task.getUserid(), Constants.SYSTEM_ORGI).ifPresent(p -> {
                            if (StringUtils.isNotBlank(p.getAgentno())) {
                                AgentStatus agentStatus = cache.findOneAgentStatusByAgentnoAndOrig(
                                        p.getAgentno(), task.getOrgi());
                                task.setAgenttimeouttimes(task.getAgenttimeouttimes() + 1);
                                if (agentStatus != null && (task.getWarnings() == null || task.getWarnings().equals(
                                        "0"))) {
                                    task.setWarnings("1");
                                    task.setWarningtime(new Date());

                                    // 发送提示消息
                                    processMessage(
                                            sessionConfig, sessionConfig.getTimeoutmsg(), agentStatus.getUsername(),
                                            p, agentStatus, task);
                                    agentUserTaskRes.save(task);
                                } else if (sessionConfig.isResessiontimeout() && agentStatus != null && task.getWarningtime() != null && MainUtils.getLastTime(
                                        sessionConfig.getRetimeout()).after(task.getWarningtime())) {    //再次超时未回复
                                    /**
                                     * 设置了再次超时,断开
                                     */
                                    processMessage(
                                            sessionConfig, sessionConfig.getRetimeoutmsg(),
                                            sessionConfig.getServicename(),
                                            p, agentStatus, task);
                                    try {
                                        acdAgentService.finishAgentService(p, task.getOrgi());
                                    } catch (Exception e) {
                                        logger.warn("[task] exception: ", e);
                                    }
                                }
                            }
                        });
                    }
                } else if (sessionConfig.isResessiontimeout()) {    //未启用超时提醒，只设置了超时断开
                    List<AgentUserTask> agentUserTask = agentUserTaskRes.findByLastmessageLessThanAndStatusAndOrgi(
                            MainUtils.getLastTime(sessionConfig.getRetimeout()),
                            MainContext.AgentUserStatusEnum.INSERVICE.toString(), sessionConfig.getOrgi());
                    for (final AgentUserTask task : agentUserTask) {        // 超时未回复
                        cache.findOneAgentUserByUserIdAndOrgi(
                                task.getUserid(), Constants.SYSTEM_ORGI).ifPresent(p -> {
                            AgentStatus agentStatus = cache.findOneAgentStatusByAgentnoAndOrig(
                                    p.getAgentno(), task.getOrgi());
                            if (agentStatus != null && task.getWarningtime() != null && MainUtils.getLastTime(
                                    sessionConfig.getRetimeout()).after(task.getWarningtime())) {    //再次超时未回复
                                /**
                                 * 设置了再次超时,断开
                                 */
                                processMessage(
                                        sessionConfig, sessionConfig.getRetimeoutmsg(), agentStatus.getUsername(),
                                        p, agentStatus, task);
                                try {
                                    acdAgentService.finishAgentService(p, task.getOrgi());
                                } catch (Exception e) {
                                    logger.warn("[task] exception: ", e);
                                }
                            }
                        });
                    }
                }
                if (sessionConfig.isQuene()) {    // 启用排队超时功能，超时断开
                    List<AgentUserTask> agentUserTask = agentUserTaskRes.findByLogindateLessThanAndStatusAndOrgi(
                            MainUtils.getLastTime(sessionConfig.getQuenetimeout()),
                            MainContext.AgentUserStatusEnum.INQUENE.toString(), sessionConfig.getOrgi());
                    for (final AgentUserTask task : agentUserTask) {        // 超时未回复
                        cache.findOneAgentUserByUserIdAndOrgi(
                                task.getUserid(), Constants.SYSTEM_ORGI).ifPresent(p -> {
                            /**
                             * 设置了超时,断开
                             */
                            processMessage(
                                    sessionConfig, sessionConfig.getQuenetimeoutmsg(), sessionConfig.getServicename(),
                                    p, null, task);
                            try {
                                acdAgentService.finishAgentService(p, task.getOrgi());
                            } catch (Exception e) {
                                logger.warn("[task] exception: ", e);
                            }
                        });
                    }
                }
            }
        }
    }

    @Scheduled(fixedDelay = 5000, initialDelay = 20000) // 每5秒执行一次
    public void agent() {
        List<SessionConfig> sessionConfigList = acdPolicyService.initSessionConfigList();
        if (sessionConfigList != null && sessionConfigList.size() > 0) {
            for (final SessionConfig sessionConfig : sessionConfigList) {
                // ? 为什么还要重新取一次？
//                sessionConfig = automaticServiceDist.initSessionConfig(sessionConfig.getOrgi());
                if (sessionConfig != null && MainContext.getContext() != null && sessionConfig.isAgentreplaytimeout()) {
                    List<AgentUserTask> agentUserTask = agentUserTaskRes.findByLastgetmessageLessThanAndStatusAndOrgi(
                            MainUtils.getLastTime(sessionConfig.getAgenttimeout()),
                            MainContext.AgentUserStatusEnum.INSERVICE.toString(), sessionConfig.getOrgi());
                    for (final AgentUserTask task : agentUserTask) {        // 超时未回复
                        cache.findOneAgentUserByUserIdAndOrgi(
                                task.getUserid(), Constants.SYSTEM_ORGI).ifPresent(p -> {
                            AgentStatus agentStatus = cache.findOneAgentStatusByAgentnoAndOrig(
                                    p.getAgentno(), task.getOrgi());
                            if (agentStatus != null && (task.getReptimes() == null || task.getReptimes().equals("0"))) {
                                task.setReptimes("1");
                                task.setReptime(new Date());

                                //发送提示消息
                                processMessage(
                                        sessionConfig, sessionConfig.getAgenttimeoutmsg(),
                                        sessionConfig.getServicename(), p, agentStatus, task);
                                agentUserTaskRes.save(task);
                            }
                        });
                    }
                }
            }
        }
    }

    /**
     * 将一分钟之前的不活跃的OnlineUser设置为离线
     * 每分钟执行一次，将不活跃的访客设置为离线
     */
    @Scheduled(fixedDelay = 60000, initialDelay = 20000)
    public void onlineuser() {
        final Page<OnlineUser> pages = onlineUserRes.findByStatusAndCreatetimeLessThan(
                MainContext.OnlineUserStatusEnum.ONLINE.toString(),
                MainUtils.getLastTime(60), new PageRequest(0, 1000));
        if (pages.getContent().size() > 0) {
            for (final OnlineUser onlineUser : pages.getContent()) {
                try {
                    logger.info(
                            "[save] put onlineUser id {}, status {}, invite status {}", onlineUser.getId(),
                            onlineUser.getStatus(),
                            onlineUser.getInvitestatus());
                    OnlineUserProxy.offline(onlineUser);
                } catch (Exception e) {
                    logger.warn("[onlineuser] error", e);
                }
            }
        }
    }

    /**
     * appid : appid ,
     * userid:userid,
     * sign:session,
     * touser:touser,
     * session: session ,
     * orgi:orgi,
     * username:agentstatus,
     * nickname:agentstatus,
     * message : message
     *
     * @param sessionConfig
     * @param agentUser
     * @param task
     */

    private void processMessage(
            SessionConfig sessionConfig, String message, String servicename, AgentUser
            agentUser, AgentStatus agentStatus, AgentUserTask task) {

        Message outMessage = new Message();
        if (StringUtils.isNotBlank(message)) {
            outMessage.setMessage(message);
            outMessage.setMessageType(MainContext.MediaType.TEXT.toString());
            outMessage.setCalltype(MainContext.CallType.OUT.toString());
            outMessage.setAgentUser(agentUser);
            outMessage.setSnsAccount(null);

            ChatMessage chatMessage = new ChatMessage();
            if (agentUser != null) {
                chatMessage.setAppid(agentUser.getAppid());

                chatMessage.setUserid(agentUser.getUserid());
                chatMessage.setUsession(agentUser.getUserid());
                chatMessage.setTouser(agentUser.getUserid());
                chatMessage.setOrgi(agentUser.getOrgi());
                chatMessage.setUsername(agentUser.getUsername());
                chatMessage.setMessage(message);

                chatMessage.setId(MainUtils.getUUID());
                chatMessage.setContextid(agentUser.getContextid());

                chatMessage.setAgentserviceid(agentUser.getAgentserviceid());

                chatMessage.setCalltype(MainContext.CallType.OUT.toString());
                if (StringUtils.isNotBlank(agentUser.getAgentno())) {
                    chatMessage.setTouser(agentUser.getUserid());
                }
                chatMessage.setChannel(agentUser.getChannel());
                chatMessage.setUsession(agentUser.getUserid());

                outMessage.setContextid(agentUser.getContextid());

                outMessage.setChannelMessage(chatMessage);
                if (StringUtils.isNotBlank(agentUser.getAgentname())) {
                    // OUT类型，设置发消息人名字
                    chatMessage.setUsername(agentUser.getAgentname());
                } else {
                    chatMessage.setUsername(servicename);
                }
                outMessage.setCreatetime(Constants.DISPLAY_DATE_FORMATTER.format(chatMessage.getCreatetime()));

                /**
                 * 同时发送消息给双方
                 */
                // 通知坐席
                if (agentUser != null && StringUtils.isNotBlank(agentUser.getAgentno())) {
                    peerSyncIM.send(MainContext.ReceiverType.AGENT, MainContext.ChannelType.WEBIM,
                                    agentUser.getAppid(),
                                    MainContext.MessageType.MESSAGE, agentUser.getAgentno(), outMessage, true);
                }

                // 通知访客
                if (StringUtils.isNotBlank(chatMessage.getTouser())) {
                    peerSyncIM.send(MainContext.ReceiverType.VISITOR,
                                    MainContext.ChannelType.toValue(agentUser.getChannel()),
                                    agentUser.getAppid(),
                                    MainContext.MessageType.MESSAGE,
                                    agentUser.getUserid(),
                                    outMessage, true);
                }
            }
        }
    }

    /**
     * 每三秒 , 加载 标记为执行中的任务何 即将执行的 计划任务
     * TODO 需要重构，将这个作业执行引擎拆解，比如引入
     * https://airflow.apache.org/
     * 暂时设计为10分钟执行一次，测试有什么影响，需要重新看代码
     */
    @Scheduled(fixedDelay = 600000) //
    public void jobDetail() {
        List<JobDetail> allJob = new ArrayList<JobDetail>();
        Page<JobDetail> readyTaskList = jobDetailRes.findByTaskstatus(
                MainContext.TaskStatusType.READ.getType(), new PageRequest(0, 100));
        allJob.addAll(readyTaskList.getContent());
        Page<JobDetail> planTaskList = jobDetailRes.findByPlantaskAndTaskstatusAndNextfiretimeLessThan(
                true, MainContext.TaskStatusType.NORMAL.getType(), new Date(), new PageRequest(0, 100));
        allJob.addAll(planTaskList.getContent());
        if (allJob.size() > 0) {
            for (JobDetail jobDetail : allJob) {
                if (!cache.existJobByIdAndOrgi(jobDetail.getId(), jobDetail.getOrgi())) {
                    jobDetail.setTaskstatus(MainContext.TaskStatusType.QUEUE.getType());
                    jobDetailRes.save(jobDetail);
                    cache.putJobByIdAndOrgi(jobDetail.getId(), jobDetail.getOrgi(), jobDetail);
                    /**
                     * 加入到作业执行引擎
                     */
                    webimTaskExecutor.execute(new Task(jobDetail, jobDetailRes));
                }
            }
        }
    }


}
