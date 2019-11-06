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
package com.chatopera.cc.acd;

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.cache.RedisCommand;
import com.chatopera.cc.cache.RedisKey;
import com.chatopera.cc.exception.CSKefuException;
import com.chatopera.cc.model.*;
import com.chatopera.cc.peer.PeerSyncIM;
import com.chatopera.cc.persistence.es.ContactsRepository;
import com.chatopera.cc.persistence.repository.*;
import com.chatopera.cc.proxy.AgentAuditProxy;
import com.chatopera.cc.socketio.client.NettyClients;
import com.chatopera.cc.socketio.message.Message;
import com.chatopera.cc.util.SerializeUtil;
import com.chatopera.cc.util.WebIMReport;
import com.corundumstudio.socketio.SocketIONamespace;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Automatic Call Distribution
 */
@SuppressWarnings("deprecation")
public class AutomaticServiceDist {
    private final static Logger logger = LoggerFactory.getLogger(AutomaticServiceDist.class);

    // Redis缓存: 缓存的底层实现接口
    private static RedisCommand redisCommand;

    // 缓存管理：高级缓存实现接口
    private static Cache cache;

    // 在线访客与坐席关联表
    private static AgentUserRepository agentUserRes;

    // 在线访客
    private static OnlineUserRepository onlineUserRes;

    // 坐席服务记录
    private static AgentServiceRepository agentServiceRes;

    // 坐席服务任务
    private static AgentUserTaskRepository agentUserTaskRes;

    // 坐席状态报告
    private static AgentReportRepository agentReportRes;

    // 坐席状态
    private static AgentStatusRepository agentStatusRes;

    // 坐席配置
    private static SessionConfigRepository sessionConfigRes;

    //用户
    private static UserRepository UserRes;

    // 联系人
    private static ContactsRepository contactsRes;

    // 联系人会话关联表
    private static AgentUserContactsRepository agentUserContactsRes;

    // 会话监控
    private static AgentAuditProxy agentAuditProxy;


    // 消息分发
    private static PeerSyncIM peerSyncIM;

    /**
     * 载入坐席 ACD策略配置
     *
     * @param orgi
     * @return
     */
    public static SessionConfig initSessionConfig(final String orgi) {
        SessionConfig sessionConfig;
        if ((sessionConfig = getCache().findOneSessionConfigByOrgi(orgi)) == null) {
            sessionConfig = getSessionConfigRes().findByOrgi(orgi);
            if (sessionConfig == null) {
                sessionConfig = new SessionConfig();
            } else {
                getCache().putSessionConfigByOrgi(sessionConfig, orgi);
            }
        }
        return sessionConfig;
    }

    /**
     * 载入坐席 ACD策略配置
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<SessionConfig> initSessionConfigList() {
        List<SessionConfig> sessionConfigList;
        if ((sessionConfigList = getCache().findOneSessionConfigListByOrgi(MainContext.SYSTEM_ORGI)) == null) {
            SessionConfigRepository sessionConfigRes = MainContext.getContext().getBean(SessionConfigRepository.class);
            sessionConfigList = sessionConfigRes.findAll();
            if (sessionConfigList != null && sessionConfigList.size() > 0) {
                getCache().putSessionConfigListByOrgi(sessionConfigList, MainContext.SYSTEM_ORGI);
            }
        }
        return sessionConfigList;
    }

    /**
     * 获得 当前服务状态
     *
     * @param orgi
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static AgentReport getAgentReport(String orgi) {
        return AutomaticServiceDist.getAgentReport(null, orgi);
    }

    /**
     * 获得一个技能组的坐席状态
     *
     * @param organ
     * @param orgi
     * @return
     */
    public static AgentReport getAgentReport(String organ, String orgi) {
        /**
         * 统计当前在线的坐席数量
         */
        AgentReport report = new AgentReport();

        Map<String, AgentStatus> readys = getCache().getAgentStatusReadyByOrig(orgi);
        int readyNum = 0;
        int busyNum = 0;

        for (Map.Entry<String, AgentStatus> entry : readys.entrySet()) {
            if (organ == null) {
                readyNum++;
                if (entry.getValue().isBusy()) {
                    busyNum++;
                }
                continue;
            }

            if (entry.getValue().getSkills() != null &&
                    entry.getValue().getSkills().containsKey(organ)) {
                readyNum++;
                if (entry.getValue().isBusy()) {
                    busyNum++;
                }

            }
        }
        report.setAgents(readyNum);
        report.setBusy(busyNum);
        report.setOrgi(orgi);

        /**
         * 统计当前服务中的用户数量
         */
        // 服务中
        report.setUsers(getCache().getInservAgentUsersSizeByOrgi(orgi));
        // 等待中
        report.setInquene(getCache().getInqueAgentUsersSizeByOrgi(orgi));

        // DEBUG
//        logger.info(
//                "[getAgentReport] orgi {}, organ {}, agents {}, busy {}, users {}, inqueue {}", orgi, organ,
//                report.getAgents(), report.getBusy(), report.getUsers(), report.getInquene()
//                   );
        return report;
    }

    @SuppressWarnings("unchecked")
    public static int getQueueIndex(String agent, String orgi, String skill) {
        int queneUsers = 0;
        Map<String, AgentUser> map = getCache().getAgentUsersInQueByOrgi(orgi);

        for (Map.Entry<String, AgentUser> entry : map.entrySet()) {
            if (StringUtils.isNotBlank(skill)) {
                if (StringUtils.equals(entry.getValue().getSkill(), skill)) {
                    queneUsers++;
                }
                continue;
            } else {
                if (StringUtils.isNotBlank(agent)) {
                    if (StringUtils.equals(entry.getValue().getAgentno(), agent)) {
                        queneUsers++;
                    }
                    continue;
                } else {
                    queneUsers++;
                }
            }
        }
        return queneUsers;
    }

    /**
     * 为坐席批量分配用户
     *
     * @param agentno
     * @param orgi
     */
    @SuppressWarnings("unchecked")
    public static void allotAgent(String agentno, String orgi) {
        // 获得目标坐席的状态
        AgentStatus agentStatus = SerializeUtil.deserialize(
                getRedisCommand().getHashKV(RedisKey.getAgentStatusReadyHashKey(orgi), agentno));

        if (agentStatus == null) {
            logger.warn("[allotAgent] can not find AgentStatus for agentno {}", agentno);
            return;
        }

        // 获得所有待服务访客的列表
        Map<String, AgentUser> pendingAgentUsers = getCache().getAgentUsersInQueByOrgi(orgi);

        for (Map.Entry<String, AgentUser> entry : pendingAgentUsers.entrySet()) {
            AgentUser agentUser = entry.getValue();
            boolean process = false;

            if ((StringUtils.equals(agentUser.getAgentno(), agentno))) {
                // 待服务的访客指定了该坐席
                process = true;
            } else {
                if (agentStatus != null &&
                        agentStatus.getSkills() != null &&
                        agentStatus.getSkills().size() > 0) {
                    // 目标坐席有状态，并且坐席属于某技能组
                    if ((StringUtils.isBlank(agentUser.getAgentno()) &&
                            StringUtils.isBlank(agentUser.getSkill()))) {
                        // 待服务的访客还没有指定坐席，并且也没有绑定技能组
                        process = true;
                    } else {
                        if (StringUtils.isBlank(agentUser.getAgentno()) &&
                                agentStatus.getSkills().containsKey(agentUser.getSkill())) {
                            // 待服务的访客还没有指定坐席，并且指定的技能组和该坐席的技能组一致
                            process = true;
                        }
                    }
                } else {
                    // 目标坐席没有状态，或该目标坐席有状态但是没有属于任何一个技能组
                    if (StringUtils.isBlank(agentUser.getAgentno()) &&
                            StringUtils.isBlank(agentUser.getSkill())) {
                        // 待服务访客没有指定坐席，并且没有指定技能组
                        process = true;
                    }
                }
            }

            if (!process) {
                continue;
            }

            SessionConfig sessionConfig = AutomaticServiceDist.initSessionConfig(orgi);
            long maxusers = sessionConfig == null ? Constants.AGENT_STATUS_MAX_USER : sessionConfig.getMaxuser();
            if (agentStatus.getUsers() < maxusers) {   //坐席未达到最大咨询访客数量
                // 从排队队列移除
                getCache().deleteAgentUserInqueByAgentUserIdAndOrgi(agentUser.getUserid(), orgi);

                // 下面开始处理其加入到服务中的队列
                try {
                    AgentService agentService = processAgentService(agentStatus, agentUser, orgi, sessionConfig);

                    // 处理完成得到 agentService
                    Message outMessage = new Message();
                    outMessage.setMessage(AutomaticServiceDist.getSuccessMessage(
                            agentService,
                            agentUser.getChannel(),
                            orgi));
                    outMessage.setMessageType(MainContext.MediaType.TEXT.toString());
                    outMessage.setCalltype(MainContext.CallType.IN.toString());
                    outMessage.setCreatetime(MainUtils.dateFormate.format(new Date()));

                    if (StringUtils.isNotBlank(agentUser.getUserid())) {
                        outMessage.setAgentUser(agentUser);
                        outMessage.setChannelMessage(agentUser);

                        // 向访客推送消息
                        getPeerSyncIM().send(
                                MainContext.ReceiverType.VISITOR,
                                MainContext.ChannelType.toValue(agentUser.getChannel()), agentUser.getAppid(),
                                MainContext.MessageType.STATUS, agentUser.getUserid(), outMessage, true
                                            );

                        // 向坐席推送消息
                        getPeerSyncIM().send(MainContext.ReceiverType.AGENT, MainContext.ChannelType.WEBIM,
                                             agentUser.getAppid(),
                                             MainContext.MessageType.NEW, agentUser.getAgentno(), outMessage, true);
                    }
                } catch (Exception ex) {
                    logger.warn("[allotAgent] fail to process service", ex);
                }
            } else {
                logger.info("[allotAgent] agentno {} reach the max users limit", agentno);
                break;
            }
        }
        broadcastAgentsStatus(orgi, "agent", "success", agentno);
    }

    /**
     * 访客服务结束
     *
     * @param agentUser
     * @param orgi
     * @throws Exception
     */
    public static void serviceFinish(final AgentUser agentUser, final String orgi) {
        if (agentUser != null) {
            // 获得坐席状态
            AgentStatus agentStatus = null;
            if (StringUtils.equals(MainContext.AgentUserStatusEnum.INSERVICE.toString(), agentUser.getStatus()) &&
                    agentUser.getAgentno() != null) {
                agentStatus = getCache().findOneAgentStatusByAgentnoAndOrig(agentUser.getAgentno(), orgi);
            }

            // 设置新AgentUser的状态
            agentUser.setStatus(MainContext.AgentUserStatusEnum.END.toString());
            if (agentUser.getServicetime() != null) {
                agentUser.setSessiontimes(System.currentTimeMillis() - agentUser.getServicetime().getTime());
            }

            // 从缓存中删除agentUser缓存
            getAgentUserRes().save(agentUser);

            final SessionConfig sessionConfig = AutomaticServiceDist.initSessionConfig(orgi);

            // 坐席服务
            AgentService service = null;
            if (StringUtils.isNotBlank(agentUser.getAgentserviceid())) {
                service = getAgentServiceRes().findByIdAndOrgi(agentUser.getAgentserviceid(), agentUser.getOrgi());
            } else if (agentStatus != null) {
                // 该访客没有和坐席对话，因此没有 AgentService
                // 当做留言处理，创建一个新的 AgentService
                service = processAgentService(agentStatus, agentUser, orgi, true, sessionConfig);
            }

            if (service != null) {
                service.setStatus(MainContext.AgentUserStatusEnum.END.toString());
                service.setEndtime(new Date());
                if (service.getServicetime() != null) {
                    service.setSessiontimes(System.currentTimeMillis() - service.getServicetime().getTime());
                }

                final List<AgentUserTask> agentUserTaskList = getAgentUserTaskRes().findByIdAndOrgi(
                        agentUser.getId(), agentUser.getOrgi());
                if (agentUserTaskList.size() > 0) {
                    final AgentUserTask agentUserTask = agentUserTaskList.get(0);
                    service.setAgentreplyinterval(agentUserTask.getAgentreplyinterval());
                    service.setAgentreplytime(agentUserTask.getAgentreplytime());
                    service.setAvgreplyinterval(agentUserTask.getAvgreplyinterval());
                    service.setAvgreplytime(agentUserTask.getAvgreplytime());

                    service.setUserasks(agentUserTask.getUserasks());
                    service.setAgentreplys(agentUserTask.getAgentreplys());

                    // 开启了质检，并且是有效对话
                    if (sessionConfig.isQuality()) {
                        // 未分配质检任务
                        service.setQualitystatus(MainContext.QualityStatusEnum.NODIS.toString());
                    }
                }

                /**
                 * 启用了质检任务，开启质检
                 */
                if ((!sessionConfig.isQuality()) || service.getUserasks() == 0) {
                    // 未开启质检 或无效对话无需质检
                    service.setQualitystatus(MainContext.QualityStatusEnum.NO.toString());
                }
                getAgentServiceRes().save(service);
            }

            /**
             * 发送到访客端的通知
             */
            switch (MainContext.ChannelType.toValue(agentUser.getChannel())) {
                case WEBIM:
                    // WebIM 发送对话结束事件
                    // 向访客发送消息
                    Message outMessage = new Message();
                    outMessage.setAgentStatus(agentStatus);
                    outMessage.setMessage(AutomaticServiceDist.getServiceFinishMessage(agentUser.getChannel(), orgi));
                    outMessage.setMessageType(MainContext.AgentUserStatusEnum.END.toString());
                    outMessage.setCalltype(MainContext.CallType.IN.toString());
                    outMessage.setCreatetime(MainUtils.dateFormate.format(new Date()));
                    outMessage.setAgentUser(agentUser);

                    // 向访客发送消息
                    getPeerSyncIM().send(
                            MainContext.ReceiverType.VISITOR,
                            MainContext.ChannelType.toValue(agentUser.getChannel()), agentUser.getAppid(),
                            MainContext.MessageType.STATUS, agentUser.getUserid(), outMessage, true
                                        );

                    if (agentStatus != null) {
                        // 坐席在线，通知结束会话
                        outMessage.setChannelMessage(agentUser);
                        outMessage.setAgentUser(agentUser);
                        getPeerSyncIM().send(MainContext.ReceiverType.AGENT, MainContext.ChannelType.WEBIM,
                                             agentUser.getAppid(),
                                             MainContext.MessageType.END, agentUser.getAgentno(), outMessage, true);
                    }
                    break;
                case PHONE:
                    // 语音渠道，强制发送
                    logger.info("[serviceFinish] send notify to callout channel agentno {}", agentUser.getAgentno());
                    NettyClients.getInstance().sendCalloutEventMessage(
                            agentUser.getAgentno(), MainContext.MessageType.END.toString(), agentUser);
                    break;
                default:
                    logger.info(
                            "[serviceFinish] ignore notify agent service end for channel {}, agent user id {}",
                            agentUser.getChannel(), agentUser.getId());
            }

            // 更新访客的状态为可以接收邀请
            final OnlineUser onlineUser = getOnlineUserRes().findOneByUseridAndOrgi(
                    agentUser.getUserid(), agentUser.getOrgi());
            if (onlineUser != null) {
                onlineUser.setInvitestatus(MainContext.OnlineUserInviteStatus.DEFAULT.toString());
                getOnlineUserRes().save(onlineUser);
                logger.info(
                        "[online] onlineUser id {}, status {}, invite status {}", onlineUser.getId(),
                        onlineUser.getStatus(), onlineUser.getInvitestatus());
            }

            // 当前访客服务已经结束，为坐席寻找新访客
            if (agentStatus != null) {
                long maxusers = sessionConfig != null ? sessionConfig.getMaxuser() : Constants.AGENT_STATUS_MAX_USER;
                if ((agentStatus.getUsers() - 1) < maxusers) {
                    allotAgent(agentStatus.getAgentno(), orgi);
                }
            }
            broadcastAgentsStatus(orgi, "end", "success", agentUser != null ? agentUser.getId() : null);
        } else {
            logger.info("[serviceFinish] orgi {}, invalid agent user, should not be null", orgi);
        }
    }

    /**
     * 更新坐席当前服务中的用户状态
     * #TODO 需要分布式锁
     *
     * @param agentStatus
     * @param orgi
     */
    public synchronized static void updateAgentStatus(AgentStatus agentStatus, String orgi) {
        int users = getCache().getInservAgentUsersSizeByAgentnoAndOrgi(agentStatus.getAgentno(), orgi);
        agentStatus.setUsers(users);
        agentStatus.setUpdatetime(new Date());
        getCache().putAgentStatusByOrgi(agentStatus, orgi);
    }

    /**
     * 向所有client通知坐席状态变化
     *
     * @param orgi
     * @param worktype
     * @param workresult
     * @param dataid
     */
    public static void broadcastAgentsStatus(final String orgi, final String worktype, final String workresult, final String dataid) {
        /**
         * 坐席状态改变，通知监测服务
         */
        AgentReport agentReport = AutomaticServiceDist.getAgentReport(orgi);
        agentReport.setOrgi(orgi);
        agentReport.setWorktype(worktype);
        agentReport.setWorkresult(workresult);
        agentReport.setDataid(dataid);
        getAgentReportRes().save(agentReport);
        MainContext.getContext().getBean("agentNamespace", SocketIONamespace.class).getBroadcastOperations().sendEvent(
                "status", agentReport);
    }


    /**
     * @param agent    坐席
     * @param userid   用户ID
     * @param status   工作状态，也就是上一个状态
     * @param current  下一个工作状态
     * @param worktype 类型 ： 语音OR 文本
     * @param orgi
     * @param lasttime
     */
    public static void recordAgentStatus(
            String agent,
            String username,
            String extno,
            boolean admin,
            String userid,
            String status,
            String current,
            String worktype,
            String orgi,
            Date lasttime
                                        ) {
        WorkMonitorRepository workMonitorRes = MainContext.getContext().getBean(WorkMonitorRepository.class);
        WorkMonitor workMonitor = new WorkMonitor();
        if (StringUtils.isNotBlank(agent) && StringUtils.isNotBlank(status)) {
            workMonitor.setAgent(agent);
            workMonitor.setAgentno(agent);
            workMonitor.setStatus(status);
            workMonitor.setAdmin(admin);
            workMonitor.setUsername(username);
            workMonitor.setExtno(extno);
            workMonitor.setWorktype(worktype);
            if (lasttime != null) {
                workMonitor.setDuration((int) (System.currentTimeMillis() - lasttime.getTime()) / 1000);
            }
            if (status.equals(MainContext.AgentStatusEnum.BUSY.toString())) {
                workMonitor.setBusy(true);
            }
            if (status.equals(MainContext.AgentStatusEnum.READY.toString())) {
                int count = workMonitorRes.countByAgentAndDatestrAndStatusAndOrgi(
                        agent, MainUtils.simpleDateFormat.format(new Date()),
                        MainContext.AgentStatusEnum.READY.toString(), orgi
                                                                                 );
                if (count == 0) {
                    workMonitor.setFirsttime(true);
                }
            }
            if (current.equals(MainContext.AgentStatusEnum.NOTREADY.toString())) {
                List<WorkMonitor> workMonitorList = workMonitorRes.findByOrgiAndAgentAndDatestrAndFirsttime(
                        orgi, agent, MainUtils.simpleDateFormat.format(new Date()), true);
                if (workMonitorList.size() > 0) {
                    WorkMonitor firstWorkMonitor = workMonitorList.get(0);
                    if (firstWorkMonitor.getFirsttimes() == 0) {
                        firstWorkMonitor.setFirsttimes(
                                (int) (System.currentTimeMillis() - firstWorkMonitor.getCreatetime().getTime()));
                        workMonitorRes.save(firstWorkMonitor);
                    }
                }
            }
            workMonitor.setCreatetime(new Date());
            workMonitor.setDatestr(MainUtils.simpleDateFormat.format(new Date()));

            workMonitor.setName(agent);
            workMonitor.setOrgi(orgi);
            workMonitor.setUserid(userid);

            workMonitorRes.save(workMonitor);
        }
    }

    /**
     * 撤退一个坐席
     * 1）将该坐席状态置为"非就绪"
     * 2) 将该坐席的访客重新分配给其它坐席
     *
     * @param orgi
     * @param agentno
     * @return 有没有成功将所有其服务的访客都分配出去
     */
    public static boolean withdrawAgent(final String orgi, final String agentno) {
        // 先将该客服切换到非就绪状态
        final AgentStatus agentStatus = getCache().findOneAgentStatusByAgentnoAndOrig(agentno, orgi);
        if (agentStatus != null) {
            agentStatus.setBusy(false);
            agentStatus.setUpdatetime(new Date());
            agentStatus.setStatus(MainContext.AgentStatusEnum.NOTREADY.toString());
            getAgentStatusRes().save(agentStatus);
            cache.putAgentStatusByOrgi(agentStatus, orgi);
        }

        // 然后将该坐席的访客分配给其它坐席
        // 获得该租户在线的客服的多少
        // TODO 对于agentUser的技能组过滤，在下面再逐个考虑？
        // 该信息同样也包括当前用户
        List<AgentUser> agentUsers = cache.findInservAgentUsersByAgentnoAndOrgi(agentno, orgi);
        int sz = agentUsers.size();
        for (final AgentUser x : agentUsers) {
            try {
                // TODO 此处没有考虑遍历过程中，系统中坐席的服务访客的信息实际上是变化的
                // 可能会发生maxusers超过设置的情况，如果做很多检查，会带来一定一系统开销
                // 因为影响不大，放弃实时的检查
                allotAgent(x, x.getOrgi());
                // 因为重新分配该访客，将其从撤离的坐席中服务集合中删除
                // 此处类似于 Transfer
                getRedisCommand().removeSetVal(
                        RedisKey.getInServAgentUsersByAgentnoAndOrgi(agentno, orgi), x.getUserid());
                sz--;
            } catch (Exception e) {
                logger.warn("[withdrawAgent] throw error:", e);
            }
        }

        if (sz == 0) {
            logger.info("[withdrawAgent] after re-allotAgent, the agentUsers size is {} for agentno {}", sz, agentno);
        } else {
            logger.warn("[withdrawAgent] after re-allotAgent, the agentUsers size is {} for agentno {}", sz, agentno);
        }

        return sz == 0;
    }

    /**
     * 为用户分配坐席
     *
     * @param agentUser
     */
    @SuppressWarnings("unchecked")
    public static AgentService allotAgent(
            final AgentUser agentUser,
            final String orgi) {
        /**
         * 查询条件，当前在线的 坐席，并且 未达到最大 服务人数的坐席
         */

        List<AgentStatus> agentStatusList = filterOutAvailableAgentStatus(agentUser, orgi);

        /**
         * 处理ACD 的 技能组请求和 坐席请求
         */
        AgentStatus agentStatus = null;
        AgentService agentService = null;    //放入缓存的对象
        SessionConfig sessionConfig = initSessionConfig(orgi);
        if (agentStatusList.size() > 0) {
            agentStatus = agentStatusList.get(0);
            if (agentStatus.getUsers() >= sessionConfig.getMaxuser()) {
                agentStatus = null;
                /**
                 * 判断当前有多少人排队中 ， 分三种情况：1、请求技能组的，2、请求坐席的，3，默认请求的
                 *
                 */
            }
        }

        try {
            agentService = processAgentService(agentStatus, agentUser, orgi, sessionConfig);
            // 处理结果：进入排队队列
            if (StringUtils.equals(MainContext.AgentUserStatusEnum.INQUENE.toString(), agentService.getStatus())) {
                agentService.setQueneindex(getQueueIndex(agentUser.getAgentno(), orgi, agentUser.getSkill()));
            }
        } catch (Exception ex) {
            logger.warn("[allotAgent] exception: ", ex);
        }
        broadcastAgentsStatus(
                orgi, "user", agentService != null && agentService.getStatus().equals(
                        MainContext.AgentUserStatusEnum.INSERVICE.toString()) ? "inservice" : "inquene",
                agentUser.getId()
                             );
        return agentService;
    }

    /**
     * 过滤在线客服
     * 优先级: 1. 指定坐席;2. 指定技能组; 3. 租户所有的坐席
     *
     * @param agentUser
     * @param orgi
     * @return
     */
    private static List<AgentStatus> filterOutAvailableAgentStatus(
            final AgentUser agentUser,
            final String orgi
                                                                  ) {
        logger.info(
                "[filterOutAvailableAgentStatus] agentUser {}, orgi {}, skill {}, onlineUser {}",
                agentUser.getAgentno(), orgi, agentUser.getSkill(), agentUser.getUserid()
                   );
        List<AgentStatus> agentStatuses = new ArrayList<>();
        Map<String, AgentStatus> map = getCache().findAllReadyAgentStatusByOrgi(orgi);

        if (agentUser != null && StringUtils.isNotBlank(agentUser.getAgentno())) {
            // 指定坐席
            for (Map.Entry<String, AgentStatus> entry : map.entrySet()) {
                if ((!entry.getValue().isBusy()) && (StringUtils.equals(
                        entry.getValue().getAgentno(), agentUser.getAgentno()))) {
                    agentStatuses.add(entry.getValue());
                }
            }
        }

        /**
         * 指定坐席未查询到就绪的
         */
        if (agentStatuses.size() == 0) {
            if (StringUtils.isNotBlank(agentUser.getSkill())) {
                // 指定技能组
                for (Map.Entry<String, AgentStatus> entry : map.entrySet()) {
                    if ((!entry.getValue().isBusy()) &&
                            (entry.getValue().getSkills() != null &&
                                    entry.getValue().getSkills().containsKey(agentUser.getSkill()))) {
                        agentStatuses.add(entry.getValue());
                    }
                }
            }
        }

        /**
         * 在指定的坐席和技能组中未查到坐席
         * 接下来进行无差别查询
         */
        if (agentStatuses.size() == 0) {
            // 对于该租户的所有客服
            for (Map.Entry<String, AgentStatus> entry : map.entrySet()) {
                if (!entry.getValue().isBusy()) {
                    agentStatuses.add(entry.getValue());
                }
            }
        }

        logger.info("[filterOutAvailableAgentStatus] agent status list size: {}", agentStatuses.size());
        return agentStatuses;
    }

    /**
     * 邀请访客进入当前对话，如果当前操作的 坐席是已就绪状态，则直接加入到当前坐席的 对话列表中，如果未登录，则分配给其他坐席
     *
     * @param agentno
     * @param agentUser
     * @param orgi
     * @return
     * @throws Exception
     */
    public static AgentService allotAgentForInvite(
            final String agentno,
            final AgentUser agentUser,
            final String orgi
                                                  ) throws Exception {
        AgentStatus agentStatus = getCache().findOneAgentStatusByAgentnoAndOrig(agentno, orgi);
        AgentService agentService;
        if (agentStatus != null) {
            SessionConfig sessionConfig = initSessionConfig(orgi);
            agentService = processAgentService(agentStatus, agentUser, orgi, sessionConfig);
            broadcastAgentsStatus(orgi, "invite", "success", agentno);

            /**
             * 通知坐席新的访客邀请成功
             */
            Message outMessage = new Message();
            outMessage.setAgentUser(agentUser);
            outMessage.setChannelMessage(agentUser);
            getPeerSyncIM().send(MainContext.ReceiverType.AGENT, MainContext.ChannelType.WEBIM,
                                 agentUser.getAppid(),
                                 MainContext.MessageType.NEW, agentUser.getAgentno(), outMessage, true);
        } else {
            agentService = allotAgent(agentUser, orgi);
        }
        return agentService;
    }

    /**
     * 为访客 分配坐席， ACD策略，此处 AgentStatus 是建议 的 坐席，  如果启用了  历史服务坐席 优先策略， 则会默认检查历史坐席是否空闲，如果空闲，则分配，如果不空闲，则 分配当前建议的坐席
     *
     * @param agentStatus
     * @param agentUser
     * @param orgi
     * @return
     * @throws Exception
     */
    private static AgentService processAgentService(AgentStatus agentStatus, final AgentUser agentUser, final String orgi, final SessionConfig sessionConfig) throws Exception {
        return processAgentService(agentStatus, agentUser, orgi, false, sessionConfig);
    }

    /**
     * 为访客分配机器人客服， ACD策略，此处 AgentStatus 是建议 的 坐席，  如果启用了  历史服务坐席 优先策略， 则会默认检查历史坐席是否空闲，如果空闲，则分配，如果不空闲，则 分配当前建议的坐席
     *
     * @param agentUser
     * @param orgi
     * @return
     * @throws Exception
     */
    public static AgentService processChatbotService(final String botName, final AgentUser agentUser, final String orgi) {
        AgentService agentService = new AgentService();    //放入缓存的对象
        Date now = new Date();
        if (StringUtils.isNotBlank(agentUser.getAgentserviceid())) {
            agentService = getAgentServiceRes().findByIdAndOrgi(agentUser.getAgentserviceid(), orgi);
            agentService.setEndtime(now);
            if (agentService.getServicetime() != null) {
                agentService.setSessiontimes(System.currentTimeMillis() - agentService.getServicetime().getTime());
            }
            agentService.setStatus(MainContext.AgentUserStatusEnum.END.toString());
        } else {
            agentService.setServicetime(now);
            agentService.setLogindate(now);
            agentService.setOrgi(orgi);
            agentService.setOwner(agentUser.getContextid());
            agentService.setSessionid(agentUser.getSessionid());
            agentService.setRegion(agentUser.getRegion());
            agentService.setUsername(agentUser.getUsername());
            agentService.setChannel(agentUser.getChannel());
            if (botName != null) {
                agentService.setAgentusername(botName);
            }

            if (StringUtils.isNotBlank(agentUser.getContextid())) {
                agentService.setContextid(agentUser.getContextid());
            } else {
                agentService.setContextid(agentUser.getSessionid());
            }

            agentService.setUserid(agentUser.getUserid());
            agentService.setAiid(agentUser.getAgentno());
            agentService.setAiservice(true);
            agentService.setStatus(MainContext.AgentUserStatusEnum.INSERVICE.toString());

            agentService.setAppid(agentUser.getAppid());
            agentService.setLeavemsg(false);
        }

        agentServiceRes.save(agentService);
        return agentService;
    }

    /**
     * 为agentUser生成对应的AgentService
     * 使用场景：
     * 1. 在AgentUser服务结束并且还没有对应的AgentService
     * 2. 在新服务开始，安排坐席
     *
     * @param agentStatus   坐席状态
     * @param agentUser     坐席访客会话
     * @param orgi          租户ID
     * @param finished      结束服务
     * @param sessionConfig 坐席配置
     * @return
     */
    private static AgentService processAgentService(
            AgentStatus agentStatus,
            final AgentUser agentUser,
            final String orgi,
            final boolean finished,
            final SessionConfig sessionConfig) {
        AgentService agentService = new AgentService();
        if (StringUtils.isNotBlank(agentUser.getAgentserviceid())) {
            agentService.setId(agentUser.getAgentserviceid());
        }
        agentService.setOrgi(orgi);

        final Date now = new Date();
        // 批量复制属性
        MainUtils.copyProperties(agentUser, agentService);
        agentService.setChannel(agentUser.getChannel());
        agentService.setSessionid(agentUser.getSessionid());

        // 此处为何设置loginDate为现在
        agentUser.setLogindate(now);
        OnlineUser onlineUser = getOnlineUserRes().findOneByUseridAndOrgi(agentUser.getUserid(), orgi);

        if (finished == true) {
            // 服务结束
            agentUser.setStatus(MainContext.AgentUserStatusEnum.END.toString());
            agentService.setStatus(MainContext.AgentUserStatusEnum.END.toString());
            agentService.setSessiontype(MainContext.AgentUserStatusEnum.END.toString());
            if (agentStatus == null) {
                // 没有满足条件的坐席，留言
                agentService.setLeavemsg(true);
                agentService.setLeavemsgstatus(MainContext.LeaveMsgStatus.NOTPROCESS.toString()); //未处理的留言
            }

            if (onlineUser != null) {
                //  更新OnlineUser对象，变更为默认状态，可以接受邀请
                onlineUser.setInvitestatus(MainContext.OnlineUserInviteStatus.DEFAULT.toString());
            }
        } else if (agentStatus != null) {
            agentService.setAgent(agentStatus.getAgentno());
            agentService.setSkill(agentUser.getSkill());

            if (sessionConfig.isLastagent()) {
                // 启用了历史坐席优先 ， 查找 历史服务坐席
                List<WebIMReport> webIMaggList = MainUtils.getWebIMDataAgg(
                        onlineUserRes.findByOrgiForDistinctAgent(orgi, agentUser.getUserid()));
                if (webIMaggList.size() > 0) {
                    for (WebIMReport report : webIMaggList) {
                        if (report.getData().equals(agentStatus.getAgentno())) {
                            break;
                        } else {
                            AgentStatus hisAgentStatus = getCache().findOneAgentStatusByAgentnoAndOrig(
                                    report.getData(), orgi);
                            if (hisAgentStatus != null && hisAgentStatus.getUsers() < hisAgentStatus.getMaxusers()) {
                                // 变更为 历史服务坐席
                                agentStatus = hisAgentStatus;
                                break;
                            }
                        }

                    }
                }
            }

            agentUser.setStatus(MainContext.AgentUserStatusEnum.INSERVICE.toString());
            agentService.setStatus(MainContext.AgentUserStatusEnum.INSERVICE.toString());
            agentService.setSessiontype(MainContext.AgentUserStatusEnum.INSERVICE.toString());

            // 设置坐席名字
            agentService.setAgentno(agentStatus.getUserid());
            agentService.setAgentusername(agentStatus.getUsername());
        } else {
            // 不是服务结束，但是没有满足条件的坐席
            // 加入到排队中
            agentUser.setStatus(MainContext.AgentUserStatusEnum.INQUENE.toString());
            agentService.setStatus(MainContext.AgentUserStatusEnum.INQUENE.toString());
            agentService.setSessiontype(MainContext.AgentUserStatusEnum.INQUENE.toString());
        }

        if (finished || agentStatus != null) {
            agentService.setAgentuserid(agentUser.getId());
            agentService.setInitiator(MainContext.ChatInitiatorType.USER.toString());

            long waittingtime = 0;
            if (agentUser.getWaittingtimestart() != null) {
                waittingtime = System.currentTimeMillis() - agentUser.getWaittingtimestart().getTime();
            } else {
                if (agentUser.getCreatetime() != null) {
                    waittingtime = System.currentTimeMillis() - agentUser.getCreatetime().getTime();
                }
            }

            agentUser.setWaittingtime((int) waittingtime);
            agentUser.setServicetime(now);
            agentService.setOwner(agentUser.getOwner());
            agentService.setTimes(0);

            final User agent = getUserRes().findOne(agentService.getAgentno());
            agentUser.setAgentname(agent.getUname());
            agentUser.setAgentno(agentService.getAgentno());

            if (StringUtils.isNotBlank(agentUser.getName())) {
                agentService.setName(agentUser.getName());
            }
            if (StringUtils.isNotBlank(agentUser.getPhone())) {
                agentService.setPhone(agentUser.getPhone());
            }
            if (StringUtils.isNotBlank(agentUser.getEmail())) {
                agentService.setEmail(agentUser.getEmail());
            }
            if (StringUtils.isNotBlank(agentUser.getResion())) {
                agentService.setResion(agentUser.getResion());
            }

            if (StringUtils.isNotBlank(agentUser.getSkill())) {
                agentService.setAgentskill(agentUser.getSkill());
            }

            agentService.setServicetime(now);

            if (agentUser.getCreatetime() != null) {
                agentService.setWaittingtime((int) (System.currentTimeMillis() - agentUser.getCreatetime().getTime()));
                agentUser.setWaittingtime(agentService.getWaittingtime());
            }
            if (onlineUser != null) {
                agentService.setOsname(onlineUser.getOpersystem());
                agentService.setBrowser(onlineUser.getBrowser());
                // 记录onlineUser的id
                agentService.setDataid(onlineUser.getId());
            }

            agentService.setLogindate(agentUser.getCreatetime());
            getAgentServiceRes().save(agentService);

            agentUser.setAgentserviceid(agentService.getId());
            agentUser.setLastgetmessage(now);
            agentUser.setLastmessage(now);
        }

        agentService.setDataid(agentUser.getId());

        /**
         * 分配成功以后， 将用户和坐席的对应关系放入到缓存
         * 将 AgentUser 放入到当前坐席的服务队列
         */
        getAgentUserRes().save(agentUser);

        /**
         * 更新OnlineUser对象，变更为服务中，不可邀请
         */
        if (onlineUser != null && !finished) {
            onlineUser.setInvitestatus(MainContext.OnlineUserInviteStatus.INSERV.toString());
            onlineUserRes.save(onlineUser);
        }

        // 更新坐席服务人数，坐席更新时间到缓存
        if (agentStatus != null) {
            updateAgentStatus(agentStatus, orgi);
        }
        return agentService;
    }

    /**
     * 删除AgentUser
     * 包括数据库记录及缓存信息
     *
     * @param agentUser
     * @param orgi
     * @return
     */
    public static void deleteAgentUser(final AgentUser agentUser, final String orgi) throws CSKefuException {
        logger.info("[deleteAgentUser] userId {}, orgi {}", agentUser.getUserid(), orgi);

        if (agentUser == null || agentUser.getId() == null) {
            throw new CSKefuException("Invalid agentUser info");
        }

        if (!StringUtils.equals(MainContext.AgentUserStatusEnum.END.toString(), agentUser.getStatus())) {
            /**
             * 未结束聊天，先结束对话，然后删除记录
             */
            // 删除缓存
            serviceFinish(agentUser, orgi);
        }

        // 删除数据库里的AgentUser记录
        getAgentUserRes().delete(agentUser);
    }

    /**
     * 通知消息内容：分配到坐席
     *
     * @param agentService
     * @param channel
     * @param orgi
     * @return
     */
    public static String getSuccessMessage(AgentService agentService, String channel, String orgi) {
        String queneTip = "<span id='agentno'>" + agentService.getAgentusername() + "</span>";
        if (!MainContext.ChannelType.WEBIM.toString().equals(channel)) {
            queneTip = agentService.getAgentusername();
        }
        SessionConfig sessionConfig = initSessionConfig(orgi);
        String successMsg = "坐席分配成功，" + queneTip + "为您服务。";
        if (StringUtils.isNotBlank(sessionConfig.getSuccessmsg())) {
            successMsg = sessionConfig.getSuccessmsg().replaceAll("\\{agent\\}", queneTip);
        }
        return successMsg;
    }

    /**
     * 通知消息内容：和坐席断开
     *
     * @param channel
     * @param orgi
     * @return
     */
    public static String getServiceFinishMessage(String channel, String orgi) {
        SessionConfig sessionConfig = initSessionConfig(orgi);
        String queneTip = "坐席已断开和您的对话";
        if (StringUtils.isNotBlank(sessionConfig.getFinessmsg())) {
            queneTip = sessionConfig.getFinessmsg();
        }
        return queneTip;
    }


    /**
     * 通知消息内容：和坐席断开，刷新页面
     *
     * @param channel
     * @param orgi
     * @return
     */
    public static String getServiceOffMessage(String channel, String orgi) {
        SessionConfig sessionConfig = initSessionConfig(orgi);
        String queneTip = "坐席已断开和您的对话，刷新页面为您分配新的坐席";
        if (StringUtils.isNotBlank(sessionConfig.getFinessmsg())) {
            queneTip = sessionConfig.getFinessmsg();
        }
        return queneTip;
    }

    public static String getNoAgentMessage(int queneIndex, String channel, String orgi) {
        if (queneIndex < 0) {
            queneIndex = 0;
        }
        String queneTip = "<span id='queneindex'>" + queneIndex + "</span>";
        if (!MainContext.ChannelType.WEBIM.toString().equals(channel)) {
            queneTip = String.valueOf(queneIndex);
        }
        SessionConfig sessionConfig = initSessionConfig(orgi);
        String noAgentTipMsg = "坐席全忙，已进入等待队列，您也可以在其他时间再来咨询。";
        if (StringUtils.isNotBlank(sessionConfig.getNoagentmsg())) {
            noAgentTipMsg = sessionConfig.getNoagentmsg().replaceAll("\\{num\\}", queneTip);
        }
        return noAgentTipMsg;
    }

    public static String getQueneMessage(int queneIndex, String channel, String orgi) {

        String queneTip = "<span id='queneindex'>" + queneIndex + "</span>";
        if (!MainContext.ChannelType.WEBIM.toString().equals(channel)) {
            queneTip = String.valueOf(queneIndex);
        }
        SessionConfig sessionConfig = initSessionConfig(orgi);
        String agentBusyTipMsg = "正在排队，请稍候,在您之前，还有  " + queneTip + " 位等待用户。";
        if (StringUtils.isNotBlank(sessionConfig.getAgentbusymsg())) {
            agentBusyTipMsg = sessionConfig.getAgentbusymsg().replaceAll("\\{num\\}", queneTip);
        }
        return agentBusyTipMsg;
    }

    private static RedisCommand getRedisCommand() {
        if (redisCommand == null) {
            redisCommand = MainContext.getContext().getBean(RedisCommand.class);
        }
        return redisCommand;
    }

    private static Cache getCache() {
        if (cache == null) {
            cache = MainContext.getContext().getBean(Cache.class);
        }
        return cache;
    }

    private static AgentUserRepository getAgentUserRes() {
        if (agentUserRes == null) {
            agentUserRes = MainContext.getContext().getBean(AgentUserRepository.class);
        }
        return agentUserRes;
    }

    private static OnlineUserRepository getOnlineUserRes() {
        if (onlineUserRes == null) {
            onlineUserRes = MainContext.getContext().getBean(OnlineUserRepository.class);
        }
        return onlineUserRes;
    }

    private static AgentServiceRepository getAgentServiceRes() {
        if (agentServiceRes == null) {
            agentServiceRes = MainContext.getContext().getBean(AgentServiceRepository.class);
        }
        return agentServiceRes;
    }

    private static AgentUserTaskRepository getAgentUserTaskRes() {
        if (agentUserTaskRes == null) {
            agentUserTaskRes = MainContext.getContext().getBean(AgentUserTaskRepository.class);
        }
        return agentUserTaskRes;
    }

    private static AgentReportRepository getAgentReportRes() {
        if (agentReportRes == null) {
            agentReportRes = MainContext.getContext().getBean(AgentReportRepository.class);
        }
        return agentReportRes;
    }

    private static AgentStatusRepository getAgentStatusRes() {
        if (agentStatusRes == null) {
            agentStatusRes = MainContext.getContext().getBean(AgentStatusRepository.class);
        }

        return agentStatusRes;
    }

    private static SessionConfigRepository getSessionConfigRes() {
        if (sessionConfigRes == null) {
            sessionConfigRes = MainContext.getContext().getBean(SessionConfigRepository.class);
        }

        return sessionConfigRes;
    }

    private static UserRepository getUserRes() {
        if (UserRes == null) {
            UserRes = MainContext.getContext().getBean(UserRepository.class);
        }

        return UserRes;
    }

    private static ContactsRepository getContactsRes() {
        if (contactsRes == null) {
            contactsRes = MainContext.getContext().getBean(ContactsRepository.class);
        }

        return contactsRes;
    }

    private static AgentUserContactsRepository getAgentUserContactsRes() {
        if (agentUserContactsRes == null) {
            agentUserContactsRes = MainContext.getContext().getBean(AgentUserContactsRepository.class);
        }

        return agentUserContactsRes;
    }

    private static AgentAuditProxy getAgentAuditProxy() {
        if (agentAuditProxy == null) {
            agentAuditProxy = MainContext.getContext().getBean(AgentAuditProxy.class);
        }
        return agentAuditProxy;
    }

    private static PeerSyncIM getPeerSyncIM() {
        if (peerSyncIM == null) {
            peerSyncIM = MainContext.getContext().getBean(PeerSyncIM.class);
        }
        return peerSyncIM;
    }


}
