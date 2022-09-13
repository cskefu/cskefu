/*
 * Copyright (C) 2019-2022 Chatopera Inc, <https://www.chatopera.com>
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

package com.cskefu.cc.acd;

import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.model.*;
import com.cskefu.cc.persistence.repository.*;
import com.cskefu.cc.proxy.OrganProxy;
import com.cskefu.cc.util.HashMapUtils;
import com.cskefu.cc.util.WebIMReport;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 坐席自动分配策略集
 */
@Component
public class ACDPolicyService {
    private final static Logger logger = LoggerFactory.getLogger(ACDPolicyService.class);

    @Autowired
    private Cache cache;

    @Autowired
    private UserRepository userRes;

    @Autowired
    private SessionConfigRepository sessionConfigRes;

    @Autowired
    private OnlineUserRepository onlineUserRes;

    @Autowired
    private AgentUserRepository agentUserRes;

    @Autowired
    private SNSAccountRepository snsAccountRes;

    @Autowired
    private OrganProxy organProxy;

    /**
     * 载入坐席 ACD策略配置
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<SessionConfig> initSessionConfigList() {
        List<SessionConfig> sessionConfigList;
        if ((sessionConfigList = cache.findOneSessionConfigListByOrgi(Constants.SYSTEM_ORGI)) == null) {
            sessionConfigList = sessionConfigRes.findAll();
            if (sessionConfigList != null && sessionConfigList.size() > 0) {
                cache.putSessionConfigListByOrgi(sessionConfigList, Constants.SYSTEM_ORGI);
            }
        }
        return sessionConfigList;
    }

    /**
     * 载入坐席 ACD策略配置
     *
     * @param orgi
     * @return
     */
    public SessionConfig initSessionConfig(String organid, final String orgi) {
        SessionConfig sessionConfig;
        if ((sessionConfig = cache.findOneSessionConfigByOrgi(organid, orgi)) == null) {
            sessionConfig = sessionConfigRes.findByOrgiAndSkill(orgi, organid);
            if (sessionConfig == null) {
                sessionConfig = new SessionConfig();
            } else {
                cache.putSessionConfigByOrgi(sessionConfig, organid, orgi);
            }
        }

        return sessionConfig;
    }

    /**
     * 确定AgentStatus：空闲坐席优先
     *
     * @param agentStatuses
     * @return
     */
    public AgentStatus decideAgentStatusWithIdleAgent(final List<AgentStatus> agentStatuses) {
        for (final AgentStatus o : agentStatuses) {
            if (o.getUsers() == 0) {
                logger.info("[decideAgentStatusWithIdleAgent] choose agentno {} by idle status.", o.getAgentno());
                return o;
            }
        }
        return null;
    }

    /**
     * 确定AgentStatus：坐席平均分配
     *
     * @param agentStatuses
     * @return
     */
    public AgentStatus decideAgentStatusInAverage(final List<AgentStatus> agentStatuses) {
        // 查找最少人数的AgentStatus
        AgentStatus x = agentStatuses.stream().min(Comparator.comparingInt(AgentStatus::getUsers)).get();

        if (x != null) {
            logger.info("[decideAgentStatusWithIdleAgent] choose agentno {} in average.", x.getAgentno());
        }

        return x;
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
            User user = userRes.findById(agentUser.getUserid());
            if (user != null && !user.isSuperadmin()) {
                // 用户不为空，并且不是超级管理员
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
        }

        // 此处size是1或0
        if (agentStatuses.size() == 1) {
            logger.info("[filterOutAvailableAgentStatus] agent status list size: {}", agentStatuses.size());
            // 得到指定的坐席
            return filterOutAgentStatusBySkipSuperAdmin(agentStatuses);
        }

        // Note 如果指定了坐席，但是该坐席却不是就绪的，那么就根据技能组或其它条件查找

        /**
         * 指定坐席未查询到就绪的
         */
        if (StringUtils.isNotBlank(agentUser.getSkill())) {
            // 指定技能组
            for (final Map.Entry<String, AgentStatus> entry : map.entrySet()) {
                if ((!entry.getValue().isBusy()) &&
                        (getAgentUsersBySkill(entry.getValue(), agentUser.getSkill()) < sessionConfig.getMaxuser()) &&
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
            return filterOutAgentStatusBySkipSuperAdmin(agentStatuses);
        } else {
            /**
             * 在指定的坐席和技能组中未查到坐席
             * 接下来进行无差别查询
             *
             * TODO 指定技能组无用户，停止分配
             */

            SNSAccount snsAccount = snsAccountRes.findBySnsidAndOrgi(agentUser.getAppid(), orgi);
            Map<String, Organ> allOrgan = organProxy.findAllOrganByParentIdAndOrgi(snsAccount.getOrgan(), orgi);
//            allOrgan.keySet().retainAll

            // 对于该租户的所有客服
            for (final Map.Entry<String, AgentStatus> entry : map.entrySet()) {
                Set<String> agentSkills = entry.getValue().getSkills().keySet();
                agentSkills.retainAll(allOrgan.keySet());

                if ((!entry.getValue().isBusy()) && (entry.getValue().getUsers() < sessionConfig.getMaxuser()) && agentSkills.size() > 0) {
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
        return filterOutAgentStatusBySkipSuperAdmin(agentStatuses);
    }

    /**
     * 过滤超级管理员
     *
     * @param agentStatuses
     * @return
     */
    private List<AgentStatus> filterOutAgentStatusBySkipSuperAdmin(final List<AgentStatus> agentStatuses) {
        List<AgentStatus> result = new ArrayList<>();
        List<String> uids = new ArrayList<>();
        HashMap<String, User> userMap = new HashMap<>();

        for (final AgentStatus as : agentStatuses) {
            if (StringUtils.isNotBlank(as.getUserid()))
                uids.add(as.getUserid());
        }

        List<User> users = userRes.findByIdIn(uids);

        for (final User u : users) {
            userMap.put(u.getId(), u);
        }

        for (final AgentStatus as : agentStatuses) {
            if (userMap.containsKey(as.getUserid())) {
                if (!userMap.get(as.getUserid()).isSuperadmin())
                    result.add(as);
            }
        }

        logger.info("[filterOutAgentStatusBySkipSuperAdmin] agent status list size: {}", agentStatuses.size());
        return result;
    }


    /**
     * 根据坐席配置的策略输出符合要求的AgentStatus，确定最终的坐席
     *
     * @param sessionConfig
     * @param agentStatuses
     * @return
     */
    public AgentStatus filterOutAgentStatusWithPolicies(
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
            List<WebIMReport> webIMaggs = MainUtils.getWebIMDataAgg(
                    onlineUserRes.findBySkillAndOrgiForDistinctAgent(sessionConfig.getSkill(), orgi, onlineUserId));
            for (WebIMReport report : webIMaggs) {
                for (final AgentStatus o : agentStatuses) {
                    if (StringUtils.equals(
                            o.getAgentno(), report.getData()) && getAgentUsersBySkill(o, sessionConfig.getSkill()) < sessionConfig.getMaxuser()) {
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
                    agentStatus = decideAgentStatusWithIdleAgent(agentStatuses);
                    if (agentStatus == null) {
                        // 如果没有空闲坐席，则按照平均分配
                        agentStatus = decideAgentStatusInAverage(agentStatuses);
                    }
                    break;
                case "1":
                    // 坐席平均分配
                    agentStatus = decideAgentStatusInAverage(agentStatuses);
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

    public int getAgentUsersBySkill(AgentStatus agentStatus, String skill) {
        return agentUserRes.countByAgentnoAndStatusAndOrgiAndSkill(agentStatus.getAgentno(), MainContext.AgentUserStatusEnum.INSERVICE.toString(), agentStatus.getOrgi(), skill);
    }

}
