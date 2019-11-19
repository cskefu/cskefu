/*
 * Copyright (C) 2019 Chatopera Inc, <https://www.chatopera.com>
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

import com.chatopera.cc.acd.agent.ACDAgentMw1;
import com.chatopera.cc.acd.visitor.*;
import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.cache.RedisCommand;
import com.chatopera.cc.cache.RedisKey;
import com.chatopera.cc.exception.CSKefuException;
import com.chatopera.cc.model.*;
import com.chatopera.cc.peer.PeerSyncIM;
import com.chatopera.cc.persistence.repository.*;
import com.chatopera.cc.proxy.AgentUserProxy;
import com.chatopera.cc.socketio.client.NettyClients;
import com.chatopera.cc.socketio.message.Message;
import com.chatopera.cc.util.HashMapUtils;
import com.chatopera.cc.util.IP;
import com.chatopera.cc.util.SerializeUtil;
import com.chatopera.compose4j.Composer;
import com.chatopera.compose4j.exception.Compose4jRuntimeException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Automatic Call Distribution Main Entry
 */
@SuppressWarnings("deprecation")
@Component
public class ACDServiceRouter {
    private final static Logger logger = LoggerFactory.getLogger(ACDServiceRouter.class);

    // Redis缓存: 缓存的底层实现接口
    @Autowired
    private RedisCommand redisCommand;

    // 缓存管理：高级缓存实现接口
    @Autowired
    private Cache cache;

    // 在线访客与坐席关联表
    @Autowired
    private AgentUserRepository agentUserRes;

    // 在线访客
    @Autowired
    private OnlineUserRepository onlineUserRes;

    // 坐席服务记录
    @Autowired
    private AgentServiceRepository agentServiceRes;


    @Autowired
    private AgentUserProxy agentUserProxy;

    // 坐席服务任务
    @Autowired
    private AgentUserTaskRepository agentUserTaskRes;

    // 机器人坐席
    @Autowired
    private ACDChatbotService acdChatbotService;

    // 坐席状态
    @Autowired
    private AgentStatusRepository agentStatusRes;

    // 消息工厂
    @Autowired
    private ACDMessageHelper acdMessageHelper;

    // 坐席服务
    @Autowired
    private ACDAgentService acdAgentService;

    // 消息分发
    @Autowired
    private PeerSyncIM peerSyncIM;

    @Autowired
    private ACDPolicyService acdPolicyService;

    @Autowired
    private ACDQueueService acdQueueService;

    @Autowired
    private ACDWorkMonitor acdWorkMonitor;

    /**
     * 为坐席安排访客
     */
    private Composer<ACDComposeContext> agentPipeline;

    @Autowired
    private ACDAgentMw1 acdAgentMw1;

    /**
     * 为访客安排坐席
     */
    private Composer<ACDComposeContext> visitorPipeline;

    @Autowired
    private ACDVisBodyParserMw acdVisBodyParserMw;

    @Autowired
    private ACDVisBindingMw acdVisBindingMw;

    @Autowired
    private ACDVisSessionCfgMw acdVisSessionCfgMw;

    @Autowired
    private ACDVisServiceMw acdVisServiceMw;

    @Autowired
    private ACDVisAllocatorMw acdVisAllocatorMw;

    @PostConstruct
    private void setup() {
        logger.info("[setup] setup ACD Algorithm Service ...");

        setUpAgentPipeline();
        setUpVisitorPipeline();
    }

    /**
     * 建立坐席处理管道
     */
    private void setUpAgentPipeline() {
        agentPipeline = new Composer<>();
        agentPipeline.use(acdAgentMw1);
    }


    /**
     * 建立访客处理管道
     */
    private void setUpVisitorPipeline() {
        visitorPipeline = new Composer<>();

        /**
         * 1) 设置基本信息
         */
        visitorPipeline.use(acdVisBodyParserMw);

        /**
         * 1) 绑定技能组或坐席(包括邀请时的坐席)
         */
        visitorPipeline.use(acdVisBindingMw);

        /**
         * 1) 坐席配置:工作时间段，有无就绪在线坐席
         *
         */
        visitorPipeline.use(acdVisSessionCfgMw);

        /**
         * 1）选择坐席，确定AgentService
         */
        visitorPipeline.use(acdVisServiceMw);

        /**
         * 1）根据策略筛选坐席
         */
        visitorPipeline.use(acdVisAllocatorMw);
    }


    /**
     * 为坐席批量分配用户
     *
     * @param agentno
     * @param orgi
     */
    @SuppressWarnings("unchecked")
    public void allotVisitors(String agentno, String orgi) {
        logger.info("[allotVisitors] agentno {}, orgi {}", agentno, orgi);
        // 获得目标坐席的状态
        AgentStatus agentStatus = SerializeUtil.deserialize(
                redisCommand.getHashKV(RedisKey.getAgentStatusReadyHashKey(orgi), agentno));

        if (agentStatus == null) {
            logger.warn("[allotVisitors] can not find AgentStatus for agentno {}", agentno);
            return;
        }
        logger.info("[allotVisitors] agentStatus id {}, status {}, service {}/{}, skills {}, busy {}",
                    agentStatus.getId(), agentStatus.getStatus(), agentStatus.getUsers(), agentStatus.getMaxusers(),
                    HashMapUtils.concatKeys(agentStatus.getSkills(), "|"), agentStatus.isBusy());

        if ((!StringUtils.equals(
                MainContext.AgentStatusEnum.READY.toString(), agentStatus.getStatus())) || agentStatus.isBusy()) {
            // 该坐席处于非就绪状态，或该坐席处于置忙
            // 不分配坐席
            return;
        }

        // 获得所有待服务访客的列表
        Map<String, AgentUser> pendingAgentUsers = cache.getAgentUsersInQueByOrgi(orgi);
        final SessionConfig sessionConfig = acdPolicyService.initSessionConfig(orgi);
        // 本次批量分配访客数目
        int assigned = 0;

        for (Map.Entry<String, AgentUser> entry : pendingAgentUsers.entrySet()) {
            AgentUser agentUser = entry.getValue();
            boolean process = false;

            if ((StringUtils.equals(agentUser.getAgentno(), agentno))) {
                // 待服务的访客指定了该坐席
                process = true;
            } else if (agentStatus != null &&
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

            if (!process) {
                continue;
            }

            // 坐席未达到最大咨询访客数量，并且单次批量分配小于坐席就绪时分配最大访客数量(initMaxuser)
            if (((agentStatus.getUsers() + assigned) < sessionConfig.getMaxuser()) && (assigned < sessionConfig.getInitmaxuser())) {
                assigned++;
                // 从排队队列移除
                cache.deleteAgentUserInqueByAgentUserIdAndOrgi(agentUser.getUserid(), orgi);

                // 下面开始处理其加入到服务中的队列
                try {
                    AgentService agentService = acdVisAllocatorMw.processAgentService(
                            agentStatus, agentUser, orgi, false);

                    // 处理完成得到 agentService
                    Message outMessage = new Message();
                    outMessage.setMessage(acdMessageHelper.getSuccessMessage(
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
                        peerSyncIM.send(
                                MainContext.ReceiverType.VISITOR,
                                MainContext.ChannelType.toValue(agentUser.getChannel()), agentUser.getAppid(),
                                MainContext.MessageType.STATUS, agentUser.getUserid(), outMessage, true
                                       );

                        // 向坐席推送消息
                        peerSyncIM.send(MainContext.ReceiverType.AGENT, MainContext.ChannelType.WEBIM,
                                        agentUser.getAppid(),
                                        MainContext.MessageType.NEW, agentUser.getAgentno(), outMessage, true);
                    }
                } catch (Exception ex) {
                    logger.warn("[allotVisitors] fail to process service", ex);
                }
            } else {
                logger.info(
                        "[allotVisitors] agentno {} reach the max users limit {}/{} or batch assign limit {}/{}", agentno,
                        (agentStatus.getUsers() + assigned),
                        sessionConfig.getMaxuser(), assigned, sessionConfig.getInitmaxuser());
                break;
            }
        }
        agentUserProxy.broadcastAgentsStatus(orgi, "agent", "success", agentno);
    }

    /**
     * 访客服务结束
     *
     * @param agentUser
     * @param orgi
     * @throws Exception
     */
    public void serviceFinish(final AgentUser agentUser, final String orgi) {
        if (agentUser != null) {
            /**
             * 设置AgentUser
             */
            // 获得坐席状态
            AgentStatus agentStatus = null;
            if (StringUtils.equals(MainContext.AgentUserStatusEnum.INSERVICE.toString(), agentUser.getStatus()) &&
                    agentUser.getAgentno() != null) {
                agentStatus = cache.findOneAgentStatusByAgentnoAndOrig(agentUser.getAgentno(), orgi);
            }

            // 设置新AgentUser的状态
            agentUser.setStatus(MainContext.AgentUserStatusEnum.END.toString());
            if (agentUser.getServicetime() != null) {
                agentUser.setSessiontimes(System.currentTimeMillis() - agentUser.getServicetime().getTime());
            }

            // 从缓存中删除agentUser缓存
            agentUserRes.save(agentUser);

            final SessionConfig sessionConfig = acdPolicyService.initSessionConfig(orgi);

            /**
             * 坐席服务
             */
            AgentService service = null;
            if (StringUtils.isNotBlank(agentUser.getAgentserviceid())) {
                service = agentServiceRes.findByIdAndOrgi(agentUser.getAgentserviceid(), agentUser.getOrgi());
            } else if (agentStatus != null) {
                // 该访客没有和坐席对话，因此没有 AgentService
                // 当做留言处理，创建一个新的 AgentService
                service = acdVisAllocatorMw.processAgentService(agentStatus, agentUser, orgi, true);
            }

            if (service != null) {
                service.setStatus(MainContext.AgentUserStatusEnum.END.toString());
                service.setEndtime(new Date());
                if (service.getServicetime() != null) {
                    service.setSessiontimes(System.currentTimeMillis() - service.getServicetime().getTime());
                }

                final AgentUserTask agentUserTask = agentUserTaskRes.findOne(
                        agentUser.getId());
                if (agentUserTask != null) {
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
                agentServiceRes.save(service);
            }

            /**
             * 更新AgentStatus
             */
            if (agentStatus != null) {
                agentStatus.setUsers(
                        cache.getInservAgentUsersSizeByAgentnoAndOrgi(agentStatus.getAgentno(), agentStatus.getOrgi()));
                agentStatusRes.save(agentStatus);
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
                    outMessage.setMessage(acdMessageHelper.getServiceFinishMessage(agentUser.getChannel(), orgi));
                    outMessage.setMessageType(MainContext.AgentUserStatusEnum.END.toString());
                    outMessage.setCalltype(MainContext.CallType.IN.toString());
                    outMessage.setCreatetime(MainUtils.dateFormate.format(new Date()));
                    outMessage.setAgentUser(agentUser);

                    // 向访客发送消息
                    peerSyncIM.send(
                            MainContext.ReceiverType.VISITOR,
                            MainContext.ChannelType.toValue(agentUser.getChannel()), agentUser.getAppid(),
                            MainContext.MessageType.STATUS, agentUser.getUserid(), outMessage, true
                                   );

                    if (agentStatus != null) {
                        // 坐席在线，通知结束会话
                        outMessage.setChannelMessage(agentUser);
                        outMessage.setAgentUser(agentUser);
                        peerSyncIM.send(MainContext.ReceiverType.AGENT, MainContext.ChannelType.WEBIM,
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
            final OnlineUser onlineUser = onlineUserRes.findOneByUseridAndOrgi(
                    agentUser.getUserid(), agentUser.getOrgi());
            if (onlineUser != null) {
                onlineUser.setInvitestatus(MainContext.OnlineUserInviteStatus.DEFAULT.toString());
                onlineUserRes.save(onlineUser);
                logger.info(
                        "[online] onlineUser id {}, status {}, invite status {}", onlineUser.getId(),
                        onlineUser.getStatus(), onlineUser.getInvitestatus());
            }

            // 当前访客服务已经结束，为坐席寻找新访客
            if (agentStatus != null) {
                if ((agentStatus.getUsers() - 1) < sessionConfig.getMaxuser()) {
                    allotVisitors(agentStatus.getAgentno(), orgi);
                }
            }
            agentUserProxy.broadcastAgentsStatus(orgi, "end", "success", agentUser != null ? agentUser.getId() : null);
        } else {
            logger.info("[serviceFinish] orgi {}, invalid agent user, should not be null", orgi);
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
    public boolean withdrawAgent(final String orgi, final String agentno) {
        // 先将该客服切换到非就绪状态
        final AgentStatus agentStatus = cache.findOneAgentStatusByAgentnoAndOrig(agentno, orgi);
        if (agentStatus != null) {
            agentStatus.setBusy(false);
            agentStatus.setUpdatetime(new Date());
            agentStatus.setStatus(MainContext.AgentStatusEnum.NOTREADY.toString());
            agentStatusRes.save(agentStatus);
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
                acdAgentService.allotAgent(x, x.getOrgi());
                // 因为重新分配该访客，将其从撤离的坐席中服务集合中删除
                // 此处类似于 Transfer
                redisCommand.removeSetVal(
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
     * 邀请访客进入当前对话，如果当前操作的 坐席是已就绪状态，则直接加入到当前坐席的
     * 对话列表中，如果未登录，则分配给其他坐席
     *
     * @param agentno
     * @param agentUser
     * @param orgi
     * @return
     * @throws Exception
     */
    public AgentService allotAgentForInvite(
            final String agentno,
            final AgentUser agentUser,
            final String orgi
                                           ) throws Exception {
        AgentStatus agentStatus = cache.findOneAgentStatusByAgentnoAndOrig(agentno, orgi);
        AgentService agentService;
        if (agentStatus != null) {
            agentService = acdVisAllocatorMw.processAgentService(agentStatus, agentUser, orgi, false);
            agentUserProxy.broadcastAgentsStatus(orgi, "invite", "success", agentno);

            /**
             * 通知坐席新的访客邀请成功
             */
            Message outMessage = new Message();
            outMessage.setAgentUser(agentUser);
            outMessage.setChannelMessage(agentUser);

            logger.info("[allotAgentForInvite] agentno {}, agentuser agentno {}", agentno, agentUser.getAgentno());
            peerSyncIM.send(MainContext.ReceiverType.AGENT, MainContext.ChannelType.WEBIM,
                            agentUser.getAppid(),
                            MainContext.MessageType.NEW, agentUser.getAgentno(), outMessage, true);
        } else {
            agentService = acdAgentService.allotAgent(agentUser, orgi);
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
    public void deleteAgentUser(final AgentUser agentUser, final String orgi) throws CSKefuException {
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
        agentUserRes.delete(agentUser);
    }


    /**
     * 为新增加的访客会话分配坐席和开启访客与坐席的对话
     *
     * @param onlineUserId
     * @param nickname
     * @param orgi
     * @param session
     * @param appid
     * @param ip
     * @param osname
     * @param browser
     * @param headimg
     * @param ipdata
     * @param channel
     * @param skill
     * @param agent
     * @param title
     * @param url
     * @param traceid
     * @param ownerid
     * @param initiator
     * @return
     * @throws Exception
     */
    public Message allocateAgentService(
            final String onlineUserId,
            final String nickname,
            final String orgi,
            final String session,
            final String appid,
            final String ip,
            final String osname,
            final String browser,
            final String headimg,
            final IP ipdata,
            final String channel,
            final String skill,
            final String agent,
            final String title,
            final String url,
            final String traceid,
            final String ownerid,
            final boolean isInvite,
            final String initiator) {
        logger.info(
                "[allocateAgentService] user {}, appid {}, agent {}, skill {}, nickname {}, initiator {}, isInvite {}",
                onlineUserId,
                appid,
                agent,
                skill,
                nickname, initiator, isInvite);

        // 坐席服务请求，分配 坐席
        Message result = new Message();

        final ACDComposeContext ctx = new ACDComposeContext();
        ctx.setOnlineUserId(onlineUserId);
        ctx.setOnlineUserNickname(nickname);
        ctx.setOrganid(skill);
        ctx.setOrgi(orgi);
        ctx.setChannel(channel);
        ctx.setAgentno(agent);
        ctx.setBrowser(browser);
        ctx.setOsname(osname);
        ctx.setAppid(appid);
        ctx.setTitle(title);
        ctx.setSessionid(session);
        ctx.setUrl(url);
        ctx.setOnlineUserHeadimgUrl(headimg);
        ctx.setTraceid(traceid);
        ctx.setOwnerid(ownerid);
        ctx.setInitiator(initiator);
        ctx.setIpdata(ipdata);
        ctx.setIp(ip);
        ctx.setInvite(isInvite);

        try {
            visitorPipeline.handle(ctx);
            result = (Message) ctx;
        } catch (Compose4jRuntimeException e) {
            logger.error("[allocateAgentService] error", e);
        }

        return result;
    }


    public ACDPolicyService getAcdPolicyService() {
        return acdPolicyService;
    }

    public ACDMessageHelper getAcdMessageHelper() {
        return acdMessageHelper;
    }

    public ACDAgentService getAcdAgentService() {
        return acdAgentService;
    }

    public ACDChatbotService getAcdChatbotService() {
        return acdChatbotService;
    }

    public ACDQueueService getAcdQueueService() {
        return acdQueueService;
    }

    public ACDWorkMonitor getAcdWorkMonitor() {
        return acdWorkMonitor;
    }
}
