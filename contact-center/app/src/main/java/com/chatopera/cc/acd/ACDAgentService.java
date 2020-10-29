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

import com.chatopera.cc.acd.basic.ACDComposeContext;
import com.chatopera.cc.acd.basic.ACDMessageHelper;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.cache.RedisCommand;
import com.chatopera.cc.cache.RedisKey;
import com.chatopera.cc.exception.CSKefuException;
import com.chatopera.cc.model.*;
import com.chatopera.cc.peer.PeerSyncIM;
import com.chatopera.cc.persistence.repository.*;
import com.chatopera.cc.proxy.AgentStatusProxy;
import com.chatopera.cc.proxy.AgentUserProxy;
import com.chatopera.cc.socketio.client.NettyClients;
import com.chatopera.cc.socketio.message.Message;
import com.chatopera.cc.util.HashMapUtils;
import com.chatopera.cc.util.SerializeUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class ACDAgentService {
    private final static Logger logger = LoggerFactory.getLogger(ACDAgentService.class);

    @Autowired
    private RedisCommand redisCommand;

    @Autowired
    private ACDMessageHelper acdMessageHelper;

    @Autowired
    private AgentStatusProxy agentStatusProxy;

    @Autowired
    private ACDPolicyService acdPolicyService;

    @Autowired
    private PeerSyncIM peerSyncIM;

    @Autowired
    private Cache cache;

    @Autowired
    private AgentUserRepository agentUserRes;

    @Autowired
    private AgentServiceRepository agentServiceRes;

    @Autowired
    private AgentUserTaskRepository agentUserTaskRes;

    @Autowired
    private AgentStatusRepository agentStatusRes;

    @Autowired
    private OnlineUserRepository onlineUserRes;

    @Autowired
    private UserRepository userRes;

    @Autowired
    private AgentUserProxy agentUserProxy;


    /**
     * ACD结果通知
     *
     * @param ctx
     */
    public void notifyAgentUserProcessResult(final ACDComposeContext ctx) {
        if (ctx != null && StringUtils.isNotBlank(
                ctx.getMessage())) {
            logger.info("[onConnect] find available agent for onlineUser id {}", ctx.getOnlineUserId());

            /**
             * 发送消息给坐席
             * 如果没有AgentService或该AgentService没有坐席或AgentService在排队中，则不发送
             */
            if (ctx.getAgentService() != null && (!ctx.isNoagent()) && !StringUtils.equals(
                    MainContext.AgentUserStatusEnum.INQUENE.toString(),
                    ctx.getAgentService().getStatus())) {
                // 通知消息到坐席
                MainContext.getPeerSyncIM().send(MainContext.ReceiverType.AGENT,
                        MainContext.ChannelType.WEBIM,
                        ctx.getAppid(),
                        MainContext.MessageType.NEW,
                        ctx.getAgentService().getAgentno(),
                        ctx, true);
            }

            /**
             * 发送消息给访客
             */
            Message outMessage = new Message();
            outMessage.setMessage(ctx.getMessage());
            outMessage.setMessageType(MainContext.MessageType.MESSAGE.toString());
            outMessage.setCalltype(MainContext.CallType.IN.toString());
            outMessage.setCreatetime(MainUtils.dateFormate.format(new Date()));
            outMessage.setNoagent(ctx.isNoagent());
            if (ctx.getAgentService() != null) {
                outMessage.setAgentserviceid(ctx.getAgentService().getId());
            }

            MainContext.getPeerSyncIM().send(MainContext.ReceiverType.VISITOR,
                    MainContext.ChannelType.WEBIM, ctx.getAppid(),
                    MainContext.MessageType.NEW, ctx.getOnlineUserId(), outMessage, true);


        } else {
            logger.info("[onConnect] can not find available agent for user {}", ctx.getOnlineUserId());
        }
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
    public AgentService assignVisitorAsInvite(
            final String agentno,
            final AgentUser agentUser,
            final String orgi
    ) throws Exception {
        final AgentStatus agentStatus = cache.findOneAgentStatusByAgentnoAndOrig(agentno, orgi);
        return pickupAgentUserInQueue(agentUser, agentStatus);
    }

    /**
     * 为坐席批量分配用户
     *
     * @param agentno
     * @param orgi
     */
    public void assignVisitors(String agentno, String orgi) {
        logger.info("[assignVisitors] agentno {}, orgi {}", agentno, orgi);
        // 获得目标坐席的状态
        AgentStatus agentStatus = SerializeUtil.deserialize(
                redisCommand.getHashKV(RedisKey.getAgentStatusReadyHashKey(orgi), agentno));

        if (agentStatus == null) {
            logger.warn("[assignVisitors] can not find AgentStatus for agentno {}", agentno);
            return;
        }
        logger.info("[assignVisitors] agentStatus id {}, status {}, service {}/{}, skills {}, busy {}",
                agentStatus.getId(), agentStatus.getStatus(), agentStatus.getUsers(), agentStatus.getMaxusers(),
                HashMapUtils.concatKeys(agentStatus.getSkills(), "|"), agentStatus.isBusy());

        if ((!StringUtils.equals(
                MainContext.AgentStatusEnum.READY.toString(), agentStatus.getStatus())) || agentStatus.isBusy()) {
            // 该坐席处于非就绪状态，或该坐席处于置忙
            // 不分配坐席
            return;
        }

        // 获得所有待服务访客的列表
        final Map<String, AgentUser> pendingAgentUsers = cache.getAgentUsersInQueByOrgi(orgi);

        // 本次批量分配访客数目
        Map<String, Integer> assigned = new HashMap<>();
        int currentAssigned = cache.getInservAgentUsersSizeByAgentnoAndOrgi(
                agentStatus.getAgentno(), agentStatus.getOrgi());

        logger.info(
                "[assignVisitors] agentno {}, name {}, current assigned {}, batch size in queue {}",
                agentStatus.getAgentno(),
                agentStatus.getUsername(), currentAssigned, pendingAgentUsers.size());

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
                } else if (StringUtils.isBlank(agentUser.getAgentno()) &&
                        agentStatus.getSkills().containsKey(agentUser.getSkill())) {
                    // 待服务的访客还没有指定坐席，并且指定的技能组和该坐席的技能组一致
                    process = true;
                }
            } else if (StringUtils.isBlank(agentUser.getAgentno()) &&
                    StringUtils.isBlank(agentUser.getSkill())) {
                // 目标坐席没有状态，或该目标坐席有状态但是没有属于任何一个技能组
                // 待服务访客没有指定坐席，并且没有指定技能组
                process = true;
            }

            if (!process) {
                continue;
            }

            // 坐席未达到最大咨询访客数量，并且单次批量分配小于坐席就绪时分配最大访客数量(initMaxuser)
            final SessionConfig sessionConfig = acdPolicyService.initSessionConfig(agentUser.getSkill(), orgi);
            if ((ACDServiceRouter.getAcdPolicyService().getAgentUsersBySkill(agentStatus, agentUser.getSkill()) < sessionConfig.getMaxuser()) && (assigned.getOrDefault(agentUser.getSkill(), 0) < sessionConfig.getInitmaxuser())) {
                assigned.merge(agentUser.getSkill(), 1, Integer::sum);
                pickupAgentUserInQueue(agentUser, agentStatus);
            } else {
                logger.info(
                        "[assignVisitors] agentno {} reach the max users limit {}/{} or batch assign limit {}/{}",
                        agentno,
                        (currentAssigned + assigned.getOrDefault(agentUser.getSkill(), 0)),
                        sessionConfig.getMaxuser(), assigned, sessionConfig.getInitmaxuser());
                break;
            }
        }
        agentStatusProxy.broadcastAgentsStatus(orgi, "agent", "success", agentno);
    }

    /**
     * 从队列中选择访客进行会话
     *
     * @param agentUser
     * @param agentStatus
     * @return
     */
    public AgentService pickupAgentUserInQueue(final AgentUser agentUser, final AgentStatus agentStatus) {
        // 从排队队列移除
        cache.deleteAgentUserInqueByAgentUserIdAndOrgi(agentUser.getUserid(), agentUser.getOrgi());
        AgentService agentService = null;
        // 下面开始处理其加入到服务中的队列
        try {
            agentService = resolveAgentService(
                    agentStatus, agentUser, agentUser.getOrgi(), false);

            // 处理完成得到 agentService
            Message outMessage = new Message();
            outMessage.setMessage(acdMessageHelper.getSuccessMessage(
                    agentService,
                    agentUser.getChannel(),
                    agentUser.getOrgi()));
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

                // 通知更新在线数据
                agentStatusProxy.broadcastAgentsStatus(agentUser.getOrgi(), "agent", "pickup", agentStatus.getAgentno());
            }
        } catch (Exception ex) {
            logger.warn("[assignVisitors] fail to process service", ex);
        }
        return agentService;
    }

    /**
     * 访客服务结束
     *
     * @param agentUser
     * @param orgi
     * @throws Exception
     */
    public void finishAgentService(final AgentUser agentUser, final String orgi) {
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

            final SessionConfig sessionConfig = acdPolicyService.initSessionConfig(agentUser.getSkill(), orgi);

            /**
             * 坐席服务
             */
            AgentService service = null;
            if (StringUtils.isNotBlank(agentUser.getAgentserviceid())) {
                service = agentServiceRes.findByIdAndOrgi(agentUser.getAgentserviceid(), agentUser.getOrgi());
            } else if (agentStatus != null) {
                // 该访客没有和坐席对话，因此没有 AgentService
                // 当做留言处理，创建一个新的 AgentService
                service = resolveAgentService(agentStatus, agentUser, orgi, true);
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
                    outMessage.setMessage(acdMessageHelper.getServiceFinishMessage(agentUser.getChannel(), agentUser.getSkill(), orgi));
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
                    logger.info(
                            "[finishAgentService] send notify to callout channel agentno {}", agentUser.getAgentno());
                    NettyClients.getInstance().sendCalloutEventMessage(
                            agentUser.getAgentno(), MainContext.MessageType.END.toString(), agentUser);
                    break;
                default:
                    logger.info(
                            "[finishAgentService] ignore notify agent service end for channel {}, agent user id {}",
                            agentUser.getChannel(), agentUser.getId());
            }

            // 更新访客的状态为可以接收邀请
            final OnlineUser onlineUser = onlineUserRes.findOneByUseridAndOrgi(
                    agentUser.getUserid(), agentUser.getOrgi());
            if (onlineUser != null) {
                onlineUser.setInvitestatus(MainContext.OnlineUserInviteStatus.DEFAULT.toString());
                onlineUserRes.save(onlineUser);
                logger.info(
                        "[finishAgentService] onlineUser id {}, status {}, invite status {}", onlineUser.getId(),
                        onlineUser.getStatus(), onlineUser.getInvitestatus());
            }

            // 当前访客服务已经结束，为坐席寻找新访客
            if (agentStatus != null) {
                if ((ACDServiceRouter.getAcdPolicyService().getAgentUsersBySkill(agentStatus, agentUser.getSkill()) - 1) < sessionConfig.getMaxuser()) {
                    assignVisitors(agentStatus.getAgentno(), orgi);
                }
            }
            agentStatusProxy.broadcastAgentsStatus(
                    orgi, "end", "success", agentUser != null ? agentUser.getId() : null);
        } else {
            logger.info("[finishAgentService] orgi {}, invalid agent user, should not be null", orgi);
        }
    }


    /**
     * 删除AgentUser
     * 包括数据库记录及缓存信息
     *
     * @param agentUser
     * @param orgi
     * @return
     */
    public void finishAgentUser(final AgentUser agentUser, final String orgi) throws CSKefuException {
        logger.info("[finishAgentUser] userId {}, orgi {}", agentUser.getUserid(), orgi);

        if (agentUser == null || agentUser.getId() == null) {
            throw new CSKefuException("Invalid agentUser info");
        }

        if (!StringUtils.equals(MainContext.AgentUserStatusEnum.END.toString(), agentUser.getStatus())) {
            /**
             * 未结束聊天，先结束对话，然后删除记录
             */
            // 删除缓存
            finishAgentService(agentUser, orgi);
        }

        // 删除数据库里的AgentUser记录
        agentUserRes.delete(agentUser);
    }

    /**
     * 为agentUser生成对应的AgentService
     * 使用场景：
     * 1. 在AgentUser服务结束并且还没有对应的AgentService
     * 2. 在新服务开始，安排坐席
     *
     * @param agentStatus 坐席状态
     * @param agentUser   坐席访客会话
     * @param orgi        租户ID
     * @param finished    结束服务
     * @return
     */
    public AgentService resolveAgentService(
            AgentStatus agentStatus,
            final AgentUser agentUser,
            final String orgi,
            final boolean finished) {

        AgentService agentService = new AgentService();
        if (StringUtils.isNotBlank(agentUser.getAgentserviceid())) {
            AgentService existAgentService = agentServiceRes.findByIdAndOrgi(agentUser.getAgentserviceid(), orgi);
            if (existAgentService != null) {
                agentService = existAgentService;
            } else {
                agentService.setId(agentUser.getAgentserviceid());
            }
        }
        agentService.setOrgi(orgi);

        final Date now = new Date();
        // 批量复制属性
        MainUtils.copyProperties(agentUser, agentService);
        agentService.setChannel(agentUser.getChannel());
        agentService.setSessionid(agentUser.getSessionid());

        // 此处为何设置loginDate为现在
        agentUser.setLogindate(now);
        OnlineUser onlineUser = onlineUserRes.findOneByUseridAndOrgi(agentUser.getUserid(), orgi);

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

            final User agent = userRes.findOne(agentService.getAgentno());
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
            agentServiceRes.save(agentService);

            agentUser.setAgentserviceid(agentService.getId());
            agentUser.setLastgetmessage(now);
            agentUser.setLastmessage(now);
        }

        agentService.setDataid(agentUser.getId());

        /**
         * 分配成功以后， 将用户和坐席的对应关系放入到缓存
         * 将 AgentUser 放入到当前坐席的服务队列
         */
        agentUserRes.save(agentUser);

        /**
         * 更新OnlineUser对象，变更为服务中，不可邀请
         */
        if (onlineUser != null && !finished) {
            onlineUser.setInvitestatus(MainContext.OnlineUserInviteStatus.INSERV.toString());
            onlineUserRes.save(onlineUser);
        }

        // 更新坐席服务人数，坐席更新时间到缓存
        if (agentStatus != null) {
            agentUserProxy.updateAgentStatus(agentStatus, orgi);
        }
        return agentService;
    }


}
