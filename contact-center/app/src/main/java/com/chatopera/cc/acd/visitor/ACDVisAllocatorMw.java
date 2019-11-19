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

package com.chatopera.cc.acd.visitor;

import com.chatopera.cc.acd.ACDComposeContext;
import com.chatopera.cc.acd.ACDPolicyService;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.model.*;
import com.chatopera.cc.persistence.repository.AgentServiceRepository;
import com.chatopera.cc.persistence.repository.AgentUserRepository;
import com.chatopera.cc.persistence.repository.OnlineUserRepository;
import com.chatopera.cc.persistence.repository.UserRepository;
import com.chatopera.cc.proxy.AgentUserProxy;
import com.chatopera.cc.util.HashMapUtils;
import com.chatopera.cc.util.WebIMReport;
import com.chatopera.compose4j.Functional;
import com.chatopera.compose4j.Middleware;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class ACDVisAllocatorMw implements Middleware<ACDComposeContext> {
    private final static Logger logger = LoggerFactory.getLogger(ACDVisAllocatorMw.class);

    @Autowired
    private Cache cache;

    @Autowired
    private AgentServiceRepository agentServiceRes;

    @Autowired
    private OnlineUserRepository onlineUserRes;

    @Autowired
    private UserRepository userRes;

    @Autowired
    private AgentUserRepository agentUserRes;

    @Autowired
    private AgentUserProxy agentUserProxy;

    @Autowired
    private ACDPolicyService acdPolicyService;

    @Override
    public void apply(final ACDComposeContext ctx, final Functional next) {

        /**
         * 查询条件，当前在线的 坐席，并且 未达到最大 服务人数的坐席
         */
        final List<AgentStatus> agentStatuses = filterOutAvailableAgentStatus(
                ctx.getAgentUser(), ctx.getOrgi(), ctx.getSessionConfig());

        /**
         * 处理ACD 的 技能组请求和 坐席请求
         */
        AgentStatus agentStatus = filterOutAgentStatusWithPolicies(
                ctx.getSessionConfig(), agentStatuses, ctx.getOrgi(), ctx.getOnlineUserId(), ctx.isInvite());
        AgentService agentService = null;

        try {
            agentService = processAgentService(
                    agentStatus, ctx.getAgentUser(), ctx.getOrgi(), false);
        } catch (Exception ex) {
            logger.warn("[allotAgent] exception: ", ex);
        }

        ctx.setAgentService(agentService);
    }

    /**
     * 根据坐席配置的策略输出符合要求的AgentStatus，确定最终的坐席
     *
     * @param sessionConfig
     * @param agentStatuses
     * @return
     */
    private AgentStatus filterOutAgentStatusWithPolicies(
            final SessionConfig sessionConfig,
            final List<AgentStatus> agentStatuses,
            final String orgi,
            final String onlineUserId,
            final boolean isInvite) {
        AgentStatus agentStatus = null;

        // 过滤后没有就绪的满足条件的坐席
        if (agentStatuses.size() == 0) {
            return agentStatus;
        }

        // 邀请功能
        if (isInvite) {
            logger.info("[filterOutAgentStatusWithPolicies] is invited onlineUser.");
            if (agentStatuses.size() == 1) {
                agentStatus = agentStatuses.get(0);
                // Note: 如何该邀请人离线了，恰巧只有一个其它就绪坐席，也会进入这种条件。
                logger.info(
                        "[filterOutAgentStatusWithPolicies] resolve agent as the invitee {}.",
                        agentStatus.getAgentno());
            }
            // 邀请功能，但是agentStatuses大小不是1，则进入后续决策
        }

        // 启用历史坐席优先
        if ((agentStatus == null) && sessionConfig.isLastagent()) {
            logger.info("[filterOutAgentStatusWithPolicies] check agent against chat history.");
            // 启用了历史坐席优先 ， 查找 历史服务坐席
            List<com.chatopera.cc.util.WebIMReport> webIMaggs = MainUtils.getWebIMDataAgg(
                    onlineUserRes.findByOrgiForDistinctAgent(orgi, onlineUserId));
            for (WebIMReport report : webIMaggs) {
                for (final AgentStatus o : agentStatuses) {
                    if (StringUtils.equals(
                            o.getAgentno(), report.getData()) && o.getUsers() < sessionConfig.getMaxuser()) {
                        agentStatus = o;
                        logger.info(
                                "[filterOutAgentStatusWithPolicies] choose agentno {} by chat history.",
                                agentStatus.getAgentno());
                        break;
                    }
                }

                if (agentStatus != null) {
                    break;
                }
            }
        }

        // 新客服接入人工坐席分配策略
        if (agentStatus == null) {
            // 设置默认为空闲坐席优先
            if (StringUtils.isBlank(sessionConfig.getDistribution())) {
                sessionConfig.setDistribution("0");
            }

            switch (sessionConfig.getDistribution()) {
                case "0":
                    // 空闲坐席优先
                    agentStatus = acdPolicyService.decideAgentStatusWithIdleAgent(agentStatuses);
                    if (agentStatus == null) {
                        // 如果没有空闲坐席，则按照平均分配
                        agentStatus = acdPolicyService.decideAgentStatusInAverage(agentStatuses);
                    }
                    break;
                case "1":
                    // 坐席平均分配
                    agentStatus = acdPolicyService.decideAgentStatusInAverage(agentStatuses);
                    break;
                default:
                    logger.warn(
                            "[filterOutAgentStatusWithPolicies] unexpected Distribution Strategy 【{}】",
                            sessionConfig.getDistribution());
            }
        }

        if (agentStatus != null) {
            logger.info(
                    "[filterOutAgentStatusWithPolicies] final agentStatus {}, agentno {}", agentStatus.getId(),
                    agentStatus.getAgentno());
        } else {
            logger.info("[filterOutAgentStatusWithPolicies] oops, no agent satisfy rules.");
        }

        return agentStatus;
    }

    /**
     * 过滤就绪坐席
     * 优先级: 1. 指定坐席;2. 指定技能组; 3. 租户所有的坐席
     *
     * @param agentUser
     * @param orgi
     * @return
     */
    public List<AgentStatus> filterOutAvailableAgentStatus(
            final AgentUser agentUser,
            final String orgi,
            final SessionConfig sessionConfig) {
        logger.info(
                "[filterOutAvailableAgentStatus] pre-conditions: agentUser.agentno {}, orgi {}, skill {}, onlineUser {}",
                agentUser.getAgentno(), orgi, agentUser.getSkill(), agentUser.getUserid()
                   );
        List<AgentStatus> agentStatuses = new ArrayList<>();
        Map<String, AgentStatus> map = cache.findAllReadyAgentStatusByOrgi(orgi);

        // DEBUG
        if (map.size() > 0) {
            StringBuffer sb = new StringBuffer();
            sb.append("[filterOutAvailableAgentStatus] ready agents online: \n");
            for (final Map.Entry<String, AgentStatus> f : map.entrySet()) {
                sb.append(
                        String.format("   name %s, agentno %s, service %d/%d, status %s, busy %s, skills %s \n",
                                      f.getValue().getUsername(),
                                      f.getValue().getAgentno(), f.getValue().getUsers(), f.getValue().getMaxusers(),
                                      f.getValue().getStatus(), f.getValue().isBusy(),
                                      HashMapUtils.concatKeys(f.getValue().getSkills(), "|")));
            }
            logger.info(sb.toString());
        } else {
            logger.info("[filterOutAvailableAgentStatus] None ready agent found.");
        }

        if (agentUser != null && StringUtils.isNotBlank(agentUser.getAgentno())) {
            // 指定坐席
            for (final Map.Entry<String, AgentStatus> entry : map.entrySet()) {
                // 被指定的坐席，不检查是否忙，是否达到最大接待数量
                if (StringUtils.equals(
                        entry.getValue().getAgentno(), agentUser.getAgentno())) {
                    agentStatuses.add(entry.getValue());
                    logger.info(
                            "[filterOutAvailableAgentStatus] <Agent> find ready agent {}, name {}, status {}, service {}/{}",
                            entry.getValue().getAgentno(), entry.getValue().getUsername(), entry.getValue().getStatus(),
                            entry.getValue().getUsers(),
                            entry.getValue().getMaxusers());
                    break;
                }
            }
        }

        // 此处size是1或0
        if (agentStatuses.size() == 1) {
            logger.info("[filterOutAvailableAgentStatus] agent status list size: {}", agentStatuses.size());
            // 得到指定的坐席
            return agentStatuses;
        }

        // Note 如果指定了坐席，但是该坐席却不是就绪的，那么就根据技能组或其它条件查找

        /**
         * 指定坐席未查询到就绪的
         */
        if (StringUtils.isNotBlank(agentUser.getSkill())) {
            // 指定技能组
            for (final Map.Entry<String, AgentStatus> entry : map.entrySet()) {
                if ((!entry.getValue().isBusy()) &&
                        (entry.getValue().getUsers() < sessionConfig.getMaxuser()) &&
                        (entry.getValue().getSkills() != null &&
                                entry.getValue().getSkills().containsKey(agentUser.getSkill()))) {
                    logger.info(
                            "[filterOutAvailableAgentStatus] <Skill#{}> find ready agent {}, name {}, status {}, service {}/{}, skills {}",
                            agentUser.getSkill(),
                            entry.getValue().getAgentno(), entry.getValue().getUsername(), entry.getValue().getStatus(),
                            entry.getValue().getUsers(),
                            entry.getValue().getMaxusers(),
                            HashMapUtils.concatKeys(entry.getValue().getSkills(), "|"));
                    agentStatuses.add(entry.getValue());
                } else {
                    logger.info(
                            "[filterOutAvailableAgentStatus] <Skill#{}> skip ready agent {}, name {}, status {}, service {}/{}, skills {}",
                            agentUser.getSkill(),
                            entry.getValue().getAgentno(), entry.getValue().getUsername(), entry.getValue().getStatus(),
                            entry.getValue().getUsers(),
                            entry.getValue().getMaxusers(),
                            HashMapUtils.concatKeys(entry.getValue().getSkills(), "|"));
                }
            }
            // 如果绑定了技能组，立即返回该技能组的人
            // 这时候，如果该技能组没有人，也不按照其它条件查找
            logger.info("[filterOutAvailableAgentStatus] agent status list size: {}", agentStatuses.size());
            return agentStatuses;
        } else {
            /**
             * 在指定的坐席和技能组中未查到坐席
             * 接下来进行无差别查询
             */
            // 对于该租户的所有客服
            for (final Map.Entry<String, AgentStatus> entry : map.entrySet()) {
                if ((!entry.getValue().isBusy()) && (entry.getValue().getUsers() < sessionConfig.getMaxuser())) {
                    agentStatuses.add(entry.getValue());
                    logger.info(
                            "[filterOutAvailableAgentStatus] <Redundance> find ready agent {}, agentname {}, status {}, service {}/{}, skills {}",
                            entry.getValue().getAgentno(), entry.getValue().getUsername(), entry.getValue().getStatus(),
                            entry.getValue().getUsers(),
                            entry.getValue().getMaxusers(),
                            HashMapUtils.concatKeys(entry.getValue().getSkills(), "|"));
                } else {
                    logger.info(
                            "[filterOutAvailableAgentStatus] <Redundance> skip ready agent {}, name {}, status {}, service {}/{}, skills {}",
                            entry.getValue().getAgentno(), entry.getValue().getUsername(), entry.getValue().getStatus(),
                            entry.getValue().getUsers(),
                            entry.getValue().getMaxusers(),
                            HashMapUtils.concatKeys(entry.getValue().getSkills(), "|"));
                }
            }
        }

        logger.info("[filterOutAvailableAgentStatus] agent status list size: {}", agentStatuses.size());
        return agentStatuses;
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
    public AgentService processAgentService(
            AgentStatus agentStatus,
            final AgentUser agentUser,
            final String orgi,
            final boolean finished) {
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
