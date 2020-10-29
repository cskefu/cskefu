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
package com.chatopera.cc.cache;

import com.chatopera.cc.aspect.AgentUserAspect;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.exception.CSKefuCacheException;
import com.chatopera.cc.model.*;
import com.chatopera.cc.persistence.repository.AgentUserRepository;
import com.chatopera.cc.persistence.repository.OnlineUserRepository;
import com.chatopera.cc.util.SerializeUtil;
import com.chatopera.cc.util.freeswitch.model.CallCenterAgent;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

@Component
public class Cache {

    final static private Logger logger = LoggerFactory.getLogger(Cache.class);

    @Autowired
    private OnlineUserRepository onlineUserRes;

    @Autowired
    private AgentUserRepository agentUserRes;

    @Autowired
    private RedisCommand redisCommand;

    /**
     * 获得就绪的坐席列表
     *
     * @param orgi 租户
     * @return
     */
    public Map<String, AgentStatus> getAgentStatusReadyByOrig(final String orgi) {
        Map<String, String> agentStatuses = redisCommand.getHash(RedisKey.getAgentStatusReadyHashKey(orgi));
        return convertFromStringToAgentStatus(agentStatuses);
    }

    /**
     * 通过访客ID和ORGI获得访客坐席关联关系
     *
     * @param userId
     * @param orgi
     * @return
     */
    public Optional<AgentUser> findOneAgentUserByUserIdAndOrgi(final String userId, final String orgi) {
        if (redisCommand.hasHashKV(RedisKey.getAgentUserInQueHashKey(orgi), userId)) {
            // 排队等待中
            return Optional.ofNullable((AgentUser) SerializeUtil.deserialize(
                    redisCommand.getHashKV(RedisKey.getAgentUserInQueHashKey(orgi), userId)));
        } else if (redisCommand.hasHashKV(RedisKey.getAgentUserInServHashKey(orgi), userId)) {
            // 服务中
            return Optional.ofNullable((AgentUser) SerializeUtil.deserialize(
                    redisCommand.getHashKV(RedisKey.getAgentUserInServHashKey(orgi), userId)));
        } else if (redisCommand.hasHashKV(RedisKey.getAgentUserEndHashKey(orgi), userId)) {
            // 已经结束
            return Optional.ofNullable((AgentUser) SerializeUtil.deserialize(
                    redisCommand.getHashKV(RedisKey.getAgentUserEndHashKey(orgi), userId)));
        } else {
            // 缓存中没有找到，继续到数据库查找
            return agentUserRes.findOneByUseridAndOrgi(userId, orgi);
        }
    }


    /**
     * 返回排队中的客服列表
     *
     * @param orgi
     * @return
     */
    public Map<String, AgentUser> getAgentUsersInQueByOrgi(final String orgi) {
        Map<String, String> agentUsers = redisCommand.getHash(RedisKey.getAgentUserInQueHashKey(orgi));
        Map<String, AgentUser> map = new HashMap<>();
        for (final Map.Entry<String, String> entry : agentUsers.entrySet()) {
            final AgentUser obj = SerializeUtil.deserialize(entry.getValue());
            map.put(obj.getId(), obj);
        }
        return map;
    }

    /**
     * 将访客ID从服务中队列中删除
     * TODO 将访客对应的客服的服务列表变更
     *
     * @param userid
     * @param orgi
     */
    public void deleteAgentUserInservByAgentUserIdAndOrgi(final String userid, final String orgi) {
        redisCommand.delHashKV(RedisKey.getAgentUserInServHashKey(orgi), userid);
    }


    /**
     * 将访客ID从排队队列中删除
     *
     * @param userid
     * @param orgi
     */
    public void deleteAgentUserInqueByAgentUserIdAndOrgi(final String userid, final String orgi) {
        redisCommand.delHashKV(RedisKey.getAgentUserInQueHashKey(orgi), userid);
    }

    /**
     * 获得一个坐席的状态
     *
     * @param agentno 坐席ID
     * @param orgi    租户ID
     * @return
     */
    public AgentStatus findOneAgentStatusByAgentnoAndOrig(final String agentno, final String orgi) {
        String status = getAgentStatusStatus(agentno, orgi);
        logger.debug("[findOneAgentStatusByAgentnoAndOrig] agentno {}, status {}", agentno, status);

        // 缓存中没有该坐席状态，该坐席目前是离线的
        if (StringUtils.equals(status, MainContext.AgentStatusEnum.OFFLINE.toString())) {
            return null;
        }

        String val = redisCommand.getHashKV(RedisKey.getAgentStatusHashKeyByStatusStr(orgi, status), agentno);
        AgentStatus result = SerializeUtil.deserialize(val);
        logger.debug("[findOneAgentStatusByAgentnoAndOrig] result: username {}", result.getUsername());
        return result;
    }

    /**
     * 更新坐席状态
     *
     * @param agentStatus
     * @param orgi
     */
    public void putAgentStatusByOrgi(AgentStatus agentStatus, String orgi) {
        String pre = getAgentStatusStatus(agentStatus.getAgentno(), orgi); // 坐席前状态

        if (StringUtils.equals(pre, MainContext.AgentStatusEnum.OFFLINE.toString())) {
            // 之前不存在，新建缓存
            if ((!StringUtils.equals(agentStatus.getStatus(), MainContext.AgentStatusEnum.OFFLINE.toString()))) {
                redisCommand.setHashKV(
                        RedisKey.getAgentStatusHashKeyByStatusStr(orgi, agentStatus.getStatus()),
                        agentStatus.getAgentno(), SerializeUtil.serialize(agentStatus));
            }
            return;
        } else {
            // 之前存在，与将要更新的状态一致
            if (StringUtils.equals(pre, agentStatus.getStatus())) {
                redisCommand.setHashKV(
                        RedisKey.getAgentStatusHashKeyByStatusStr(orgi, pre), agentStatus.getAgentno(),
                        SerializeUtil.serialize(agentStatus));
                return;
            } else {
                // 之前存在，而且与新状态不一致
                redisCommand.delHashKV(RedisKey.getAgentStatusHashKeyByStatusStr(orgi, pre), agentStatus.getAgentno());
                if (!StringUtils.equals(agentStatus.getStatus(), MainContext.AgentStatusEnum.OFFLINE.toString())) {
                    redisCommand.setHashKV(
                            RedisKey.getAgentStatusHashKeyByStatusStr(orgi, agentStatus.getStatus()),
                            agentStatus.getAgentno(), SerializeUtil.serialize(agentStatus));
                }
            }
        }
    }

    /**
     * 获得一个租户的就绪坐席状态
     *
     * @param orgi
     * @return
     */
    public Map<String, AgentStatus> findAllReadyAgentStatusByOrgi(final String orgi) {
        List<String> keys = new ArrayList<>();
        keys.add(RedisKey.getAgentStatusReadyHashKey(orgi));

        Map<String, String> map = redisCommand.getAllMembersInMultiHash(keys);
        return convertFromStringToAgentStatus(map);
    }

    /**
     * 获得一个租户的所有坐席状态
     *
     * @param orgi
     * @return
     */
    public Map<String, AgentStatus> findAllAgentStatusByOrgi(final String orgi) {
        List<String> keys = new ArrayList<>();
        // TODO 增加支持更多状态
        keys.add(RedisKey.getAgentStatusReadyHashKey(orgi));
        keys.add(RedisKey.getAgentStatusNotReadyHashKey(orgi));

        Map<String, String> map = redisCommand.getAllMembersInMultiHash(keys);
        return convertFromStringToAgentStatus(map);
    }


    /**
     * Inline方法
     */
    private static Map<String, AgentStatus> convertFromStringToAgentStatus(final Map<String, String> map) {
        Map<String, AgentStatus> result = new HashMap<String, AgentStatus>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            AgentStatus obj = SerializeUtil.deserialize(entry.getValue());
            result.put(entry.getKey(), obj);
        }
        return result;
    }


    /**
     * Delete Agent Status
     *
     * @param agentno
     * @param orgi
     */
    public void deleteAgentStatusByAgentnoAndOrgi(final String agentno, final String orgi) {
        String status = getAgentStatusStatus(agentno, orgi);
        if (!StringUtils.equals(MainContext.AgentStatusEnum.OFFLINE.toString(), status)) {
            redisCommand.delHashKV(RedisKey.getAgentStatusHashKeyByStatusStr(orgi, status), agentno);
        }
    }

    /**
     * 获得一个坐席的状态 agentStatus.status
     * 只返回大类状态
     *
     * @param agentno
     * @param orgi
     * @return
     */
    private String getAgentStatusStatus(final String agentno, final String orgi) {
        // 首先判断这个坐席的状态是READY还是BUSY，再去更新
        if (redisCommand.hasHashKV(RedisKey.getAgentStatusReadyHashKey(orgi), agentno)) {
            return MainContext.AgentStatusEnum.READY.toString();
        } else if (redisCommand.hasHashKV(RedisKey.getAgentStatusNotReadyHashKey(orgi), agentno)) {
            return MainContext.AgentStatusEnum.NOTREADY.toString();
        } else {
            return MainContext.AgentStatusEnum.OFFLINE.toString();
        }
    }


    /**
     * 获得技能组的坐席状态
     *
     * @param skill
     * @param orgi
     * @return
     */
    public List<AgentStatus> getAgentStatusBySkillAndOrgi(final String skill, final String orgi) {
        Map<String, AgentStatus> map = findAllAgentStatusByOrgi(orgi);
        List<AgentStatus> agentList = new ArrayList<AgentStatus>();

        for (Map.Entry<String, AgentStatus> entry : map.entrySet()) {
            if (StringUtils.isNotBlank(skill)) {
                if (entry.getValue().getSkills() != null &&
                        entry.getValue().getSkills().containsKey(skill)) {
                    agentList.add(entry.getValue());
                    continue;
                }
            } else {
                agentList.add(entry.getValue());
            }
        }
        return agentList;
    }

    /**
     * 获得指定租户的就绪的坐席个数
     *
     * @param orgi
     * @return
     */
    public int getAgentStatusReadySizeByOrgi(final String orgi) {
        return Math.toIntExact(redisCommand.getHashSize(RedisKey.getAgentStatusReadyHashKey(orgi)));
    }


    /**************************
     * AgentUser相关
     **************************/

    /**
     * 更新坐席访客关联关系
     * TODO 更新坐席的访客列表信息，增加新的访客信息
     * 包括：从等待到服务中；从等待、服务中到删除
     * 但是此处并不包含"转接"访客给其它坐席的情况，其它坐席的关联此处会完成。
     * 但是之前那个关联坐席的信息需要删除，要另行维护
     *
     * @param agentUser 最新的agentUser的状态
     * @param orgi
     */
    @AgentUserAspect.LinkAgentUser
    public void putAgentUserByOrgi(AgentUser agentUser, String orgi) {
        if (redisCommand.hasHashKV(RedisKey.getAgentUserInServHashKey(orgi), agentUser.getUserid())) {
            // 服务中
            if (!StringUtils.equals(
                    agentUser.getStatus(),
                    MainContext.AgentUserStatusEnum.INSERVICE.toString())) {
                // 删除旧记录
                redisCommand.delHashKV(RedisKey.getAgentUserInServHashKey(orgi), agentUser.getUserid());
            }
        } else if (redisCommand.hasHashKV(RedisKey.getAgentUserInQueHashKey(orgi), agentUser.getUserid())) {
            // 等待服务
            if (!StringUtils.equals(
                    agentUser.getStatus(),
                    MainContext.AgentUserStatusEnum.INQUENE.toString())) {
                // 删除旧记录
                redisCommand.delHashKV(RedisKey.getAgentUserInQueHashKey(orgi), agentUser.getUserid());
            }
        }

        // 更新新记录，忽略状态为END的agentUser，已结束的服务不加入缓存
        if (!StringUtils.equals(agentUser.getStatus(), MainContext.AgentUserStatusEnum.END.toString())) {
            redisCommand.setHashKV(
                    RedisKey.getAgentUserHashKeyByStatusStr(orgi, agentUser.getStatus()), agentUser.getUserid(),
                    SerializeUtil.serialize(agentUser));
        }
    }


    /**
     * 获得一个客服服务中的访客列表
     *
     * @param agentno
     * @param orgi
     * @return
     */
    public List<AgentUser> findInservAgentUsersByAgentnoAndOrgi(final String agentno, final String orgi) {
        logger.info("[findInservAgentUsersByAgentnoAndOrgi] agentno {}, orgi {}", agentno, orgi);
        List<AgentUser> result = new ArrayList<>();
        List<String> ids = redisCommand.getSet(RedisKey.getInServAgentUsersByAgentnoAndOrgi(agentno, orgi));
        if (ids.size() == 0) { // no inserv agentUser
            return result;
        } else {
            result = agentUserRes.findAllByUserids(ids);
        }

        return result;
    }

    /**
     * 获得一个坐席服务中的访客数量
     *
     * @param agentno
     * @param orgi
     * @return
     */
    public int getInservAgentUsersSizeByAgentnoAndOrgi(final String agentno, final String orgi) {
        return Math.toIntExact(redisCommand.getSetSize(RedisKey.getInServAgentUsersByAgentnoAndOrgi(agentno, orgi)));
    }

    /**
     * 获得服务中的访客的数量
     *
     * @param orgi
     * @return
     */
    public int getInservAgentUsersSizeByOrgi(final String orgi) {
        return redisCommand.getHashSize(RedisKey.getAgentUserInServHashKey(orgi));
    }

    /**
     * 获得等待中的访客的数量
     *
     * @param orgi
     * @return
     */
    public int getInqueAgentUsersSizeByOrgi(final String orgi) {
        return redisCommand.getHashSize(RedisKey.getAgentUserInQueHashKey(orgi));
    }

    /**
     * Delete agentUser
     *
     * @param agentUser
     * @param orgi
     */
    @AgentUserAspect.LinkAgentUser
    public void deleteAgentUserByUserIdAndOrgi(final AgentUser agentUser, String orgi) {
        if (redisCommand.hasHashKV(RedisKey.getAgentUserInQueHashKey(orgi), agentUser.getUserid())) {
            // 排队等待中
            redisCommand.delHashKV(RedisKey.getAgentUserInQueHashKey(orgi), agentUser.getUserid());
        } else if (redisCommand.hasHashKV(RedisKey.getAgentUserInServHashKey(orgi), agentUser.getUserid())) {
            redisCommand.delHashKV(RedisKey.getAgentUserInServHashKey(orgi), agentUser.getUserid());
        } else if (redisCommand.hasHashKV(RedisKey.getAgentUserEndHashKey(orgi), agentUser.getUserid())) {
            redisCommand.delHashKV(RedisKey.getAgentUserEndHashKey(orgi), agentUser.getUserid());
        } else {
            // TODO 考虑是否有其他状态保存
        }
    }

    /***************************
     * CousultInvite 相关
     ***************************/
    public void putConsultInviteByOrgi(final String orgi, final CousultInvite cousultInvite) {
        redisCommand.setHashKV(
                RedisKey.getConsultInvitesByOrgi(orgi), cousultInvite.getSnsaccountid(),
                SerializeUtil.serialize(cousultInvite));
    }

    public CousultInvite findOneConsultInviteBySnsidAndOrgi(final String snsid, final String orgi) {
        String serialized = redisCommand.getHashKV(RedisKey.getConsultInvitesByOrgi(orgi), snsid);
        if (StringUtils.isBlank(serialized)) {
            return null;
        } else {
            return (CousultInvite) SerializeUtil.deserialize(serialized);
        }
    }

    public void deleteConsultInviteBySnsidAndOrgi(final String snsid, final String orgi) {
        redisCommand.delHashKV(RedisKey.getConsultInvitesByOrgi(orgi), snsid);
    }


    /****************************
     *  OnlineUser相关
     ****************************/

    /**
     * 更新 onlineUser
     *
     * @param onlineUser
     * @param orgi
     */
    public void putOnlineUserByOrgi(final OnlineUser onlineUser, final String orgi) {
        // 此处onlineUser的id 与 onlineUser userId相同
        redisCommand.setHashKV(
                RedisKey.getOnlineUserHashKey(orgi), onlineUser.getId(), SerializeUtil.serialize(onlineUser));
    }

    /**
     * 获得 onlineUser
     *
     * @param id
     * @param orgi
     * @return
     */
    public OnlineUser findOneOnlineUserByUserIdAndOrgi(final String id, final String orgi) {
        String serialized = redisCommand.getHashKV(RedisKey.getOnlineUserHashKey(orgi), id);
        if (StringUtils.isBlank(serialized)) {
            // query with MySQL
            return onlineUserRes.findOneByUseridAndOrgi(id, orgi);
        } else {
            return convertFromStringToOnlineUser(serialized);
        }
    }

    private static OnlineUser convertFromStringToOnlineUser(final String serialized) {
        OnlineUser obj = SerializeUtil.deserialize(serialized);
        return obj;
    }

    /**
     * 删除 onlineUser
     *
     * @param id
     * @param orgi
     */
    public void deleteOnlineUserByIdAndOrgi(final String id, final String orgi) {
        redisCommand.delHashKV(RedisKey.getOnlineUserHashKey(orgi), id);
    }

    /**
     * 根据租户ID获得在线访客的列表大小
     *
     * @param orgi
     */
    public int getOnlineUserSizeByOrgi(final String orgi) {
        return redisCommand.getHashSize(RedisKey.getOnlineUserHashKey(orgi));
    }


    /**
     * 将在线访客从一个坐席的服务列表中删除
     *
     * @param userid
     * @param agentno
     * @param orgi
     */
    public void deleteOnlineUserIdFromAgentStatusByUseridAndAgentnoAndOrgi(final String userid, final String agentno, final String orgi) {
        redisCommand.removeSetVal(RedisKey.getInServAgentUsersByAgentnoAndOrgi(agentno, orgi), userid);
    }

    private Map<String, OnlineUser> convertFromStringToOnlineUsers(final Map<String, String> map) {
        Map<String, OnlineUser> result = new HashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            OnlineUser x = SerializeUtil.deserialize(entry.getValue());
            result.put(entry.getKey(), x);
        }
        return result;
    }


    /******************************
     * Callcenter Agent 相关
     ******************************/

    /**
     * 更新CallCenterAgent
     *
     * @param id
     * @param orgi
     * @param agent
     */
    public void putCallCenterAgentByIdAndOrgi(final String id, final String orgi, final CallCenterAgent agent) {
        redisCommand.setHashKV(RedisKey.getCallCenterAgentHashKeyByOrgi(orgi), id, SerializeUtil.serialize(agent));
    }

    /**
     * 根据ID和租户ID获得CallCenterAgent
     *
     * @param id
     * @param orgi
     * @return
     */
    public CallCenterAgent findOneCallCenterAgentByIdAndOrgi(final String id, final String orgi) {
        String serialized = redisCommand.getHashKV(RedisKey.getCallCenterAgentHashKeyByOrgi(orgi), id);
        if (StringUtils.isNotBlank(serialized)) {
            return (CallCenterAgent) SerializeUtil.deserialize(serialized);
        } else {
            return null;
        }
    }

    /**
     * 删除CallCenterAgent
     *
     * @param id
     * @param orgi
     */
    public void deleteCallCenterAgentByIdAndOrgi(final String id, final String orgi) {
        redisCommand.delHashKV(RedisKey.getCallCenterAgentHashKeyByOrgi(orgi), id);
    }


    /**
     * 根据租户ID获得所有的CallCenterAgent
     *
     * @param orgi
     * @return
     */
    public Map<String, CallCenterAgent> findAllCallCenterAgentsByOrgi(final String orgi) {
        Map<String, String> map = redisCommand.getHash(RedisKey.getCallCenterAgentHashKeyByOrgi(orgi));
        Map<String, CallCenterAgent> result = new HashMap<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            result.put(entry.getKey(), SerializeUtil.deserialize(entry.getValue()));
        }

        return result;
    }

    /**
     * 访客黑名单
     */
    // 将访客放在租户的黑名单中
    public void putBlackEntityByOrgi(final BlackEntity blackEntity, final String orgi) {
        redisCommand.setHashKV(
                RedisKey.getBlackEntityKeyByOrgi(orgi), blackEntity.getUserid(), SerializeUtil.serialize(blackEntity));
    }

    // 通过指定的访客和租户查找黑名单
    public Optional<BlackEntity> findOneBlackEntityByUserIdAndOrgi(final String userid, final String orgi) {
        String ser = redisCommand.getHashKV(RedisKey.getBlackEntityKeyByOrgi(orgi), userid);
        if (StringUtils.isBlank(ser)) {
            return Optional.empty();
        }

        return Optional.ofNullable(SerializeUtil.deserialize(ser));
    }

    // 将一个访客从黑名单中移除
    public void deleteBlackEntityByUserIdAndOrgi(final String userid, final String orgi) {
        redisCommand.delHashKV(RedisKey.getBlackEntityKeyByOrgi(orgi), userid);
    }

    // 指定的访客是否在租户的黑名单中
    public boolean existBlackEntityByUserIdAndOrgi(final String userid, final String orgi) {
        return redisCommand.hasHashKV(RedisKey.getBlackEntityKeyByOrgi(orgi), userid);
    }

    // 根据租户ID获得所有访客的黑名单
    public Map<String, BlackEntity> findAllBlackEntityByOrgi(final String orgi) {
        Map<String, BlackEntity> result = new HashMap<>();
        for (Map.Entry<String, String> entry : redisCommand.getHash(
                RedisKey.getBlackEntityKeyByOrgi(orgi)).entrySet()) {
            result.put(entry.getKey(), SerializeUtil.deserialize(entry.getValue()));
        }
        return result;
    }


    /*****************************
     * Job 相关
     *****************************/
    public void putJobByIdAndOrgi(final String jobId, final String orgi, final JobDetail job) {
        redisCommand.setHashKV(RedisKey.getJobHashKeyByOrgi(orgi), jobId, SerializeUtil.serialize(job));
    }

    public JobDetail findOneJobByIdAndOrgi(final String jobId, final String orgi) {
        String serialized = redisCommand.getHashKV(RedisKey.getJobHashKeyByOrgi(orgi), jobId);

        if (StringUtils.isNotBlank(serialized)) {
            return (JobDetail) SerializeUtil.deserialize(serialized);
        }
        return null;
    }

    public boolean existJobByIdAndOrgi(final String jobId, final String orgi) {
        return redisCommand.hasHashKV(RedisKey.getJobHashKeyByOrgi(orgi), jobId);
    }

    public void deleteJobByJobIdAndOrgi(final String jobId, final String orgi) {
        redisCommand.delHashKV(RedisKey.getJobHashKeyByOrgi(orgi), jobId);
    }

    /**
     * 系统词典相关
     */
    // 存储根词典
    public void putSysDicByOrgi(final String id, final String orgi, final SysDic sysDic) {
        redisCommand.setHashKV(RedisKey.getSysDicHashKeyByOrgi(orgi), id, SerializeUtil.serialize(sysDic));
    }

    // 将指定租户的系统词典清空
    public void eraseSysDicByOrgi(final String orgi) {
        redisCommand.delete(RedisKey.getSysDicHashKeyByOrgi(orgi));
    }

    // 存储词典子项
    public void putSysDicByOrgi(final String code, final String orgi, final List<SysDic> sysDics) {
        redisCommand.setHashKV(RedisKey.getSysDicHashKeyByOrgi(orgi), code, SerializeUtil.serialize(sysDics));
    }

    // 获得词典的子项列表
    public List<SysDic> getSysDicItemsByCodeAndOrgi(final String code, final String orgi) {
        String serialized = redisCommand.getHashKV(RedisKey.getSysDicHashKeyByOrgi(orgi), code);
        if (serialized != null) {
            return (List<SysDic>) SerializeUtil.deserialize(serialized);
        }
        return null;
    }

    // 获得词典子项
    public SysDic findOneSysDicByCodeAndOrgi(final String code, final String orgi) {
        String serialized = redisCommand.getHashKV(RedisKey.getSysDicHashKeyByOrgi(orgi), code);

        if (StringUtils.isBlank(serialized)) {
            return null;
        }

        return (SysDic) SerializeUtil.deserialize(serialized);
    }

    // 获得词典
    public SysDic findOneSysDicByIdAndOrgi(final String id, final String orgi) {
        String serialized = redisCommand.getHashKV(RedisKey.getSysDicHashKeyByOrgi(orgi), id);

        if (StringUtils.isBlank(serialized)) {
            return null;
        }

        return (SysDic) SerializeUtil.deserialize(serialized);
    }

    // 批量存储
    public void putSysDicByOrgi(List<SysDic> vals, final String orgi) {
        Map<String, String> map = new HashMap<>();
        for (final SysDic dic : vals) {
            map.put(dic.getId(), SerializeUtil.serialize(dic));
        }
        redisCommand.hmset(RedisKey.getSysDicHashKeyByOrgi(orgi), map);
    }

    public void deleteSysDicByIdAndOrgi(final String id, final String orgi) {
        redisCommand.delHashKV(RedisKey.getSysDicHashKeyByOrgi(orgi), id);
    }

    public boolean existSysDicByIdAndOrgi(final String id, final String orgi) {
        return redisCommand.hasHashKV(RedisKey.getSysDicHashKeyByOrgi(orgi), id);
    }

    /**
     * System 相关
     */
    public <T extends Serializable> void putSystemByIdAndOrgi(final String id, final String orgi, final T obj) {
        redisCommand.setHashKV(RedisKey.getSystemHashKeyByOrgi(orgi), id, SerializeUtil.serialize(obj));
    }

    public <T extends Serializable> void putSystemListByIdAndOrgi(final String id, final String orgi, final List<T> obj) {
        redisCommand.setHashKV(RedisKey.getSystemHashKeyByOrgi(orgi), id, SerializeUtil.serialize(obj));
    }

    public <TK, TV extends Serializable> void putSystemMapByIdAndOrgi(final String id, final String orgi, final Map<TK, TV> obj) {
        redisCommand.setHashKV(RedisKey.getSystemHashKeyByOrgi(orgi), id, SerializeUtil.serialize(obj));
    }

    public boolean existSystemByIdAndOrgi(final String id, final String orgi) {
        return redisCommand.hasHashKV(RedisKey.getSystemHashKeyByOrgi(orgi), id);
    }

    public void deleteSystembyIdAndOrgi(final String id, final String orgi) {
        redisCommand.delHashKV(RedisKey.getSystemHashKeyByOrgi(orgi), id);
    }

    public <T extends Serializable> T findOneSystemByIdAndOrgi(final String id, final String orgi) {
        String serialized = redisCommand.getHashKV(RedisKey.getSystemHashKeyByOrgi(orgi), id);
        if (StringUtils.isNotBlank(serialized)) {
            return (T) SerializeUtil.deserialize(serialized);
        }
        return null;
    }

    public <T extends Serializable> List<T> findOneSystemListByIdAndOrgi(final String id, final String orgi) {
        String serialized = redisCommand.getHashKV(RedisKey.getSystemHashKeyByOrgi(orgi), id);
        if (StringUtils.isNotBlank(serialized)) {
            return (List<T>) SerializeUtil.deserialize(serialized);
        }
        return null;
    }

    public <TK, TV extends Serializable> Map<TK, TV> findOneSystemMapByIdAndOrgi(final String id, final String orgi) {
        String serialized = redisCommand.getHashKV(RedisKey.getSystemHashKeyByOrgi(orgi), id);
        if (StringUtils.isNotBlank(serialized)) {
            return (Map<TK, TV>) SerializeUtil.deserialize(serialized);
        }
        return null;
    }

    // 获得系统cache的列表大小
    public int getSystemSizeByOrgi(final String orgi) {
        return redisCommand.getHashSize(RedisKey.getSystemHashKeyByOrgi(orgi));
    }

    /**************************
     * Session Config 相关
     **************************/

    public void putSessionConfigByOrgi(final SessionConfig sessionConfig, String organid, final String orgi) {
        redisCommand.put(RedisKey.getSessionConfig(organid, orgi), SerializeUtil.serialize(sessionConfig));
    }

    public SessionConfig findOneSessionConfigByOrgi(String organid, final String orgi) {
        String serialized = redisCommand.get(RedisKey.getSessionConfig(organid, orgi));
        if (StringUtils.isNotBlank(serialized)) {
            return (SessionConfig) SerializeUtil.deserialize(serialized);
        }
        return null;
    }

    public void deleteSessionConfigByOrgi(String organid, final String orgi) {
        redisCommand.delete(RedisKey.getSessionConfig(organid, orgi));
    }

    public boolean existSessionConfigByOrgi(String organid, final String orgi) {
        return redisCommand.exists(RedisKey.getSessionConfig(organid, orgi));
    }

    public void putSessionConfigListByOrgi(final List<SessionConfig> lis, final String orgi) {
        redisCommand.put(RedisKey.getSessionConfigList(orgi), SerializeUtil.serialize(lis));
    }

    public List<SessionConfig> findOneSessionConfigListByOrgi(final String orgi) {
        String serialized = redisCommand.get(RedisKey.getSessionConfigList(orgi));
        if (StringUtils.isNotBlank(serialized)) {
            return (List<SessionConfig>) SerializeUtil.deserialize(serialized);
        }

        return null;
    }

    public void deleteSessionConfigListByOrgi(final String orgi) {
        redisCommand.delete(RedisKey.getSessionConfigList(orgi));
    }

    public boolean existSessionConfigListByOrgi(final String orgi) {
        return redisCommand.exists(RedisKey.getSessionConfigList(orgi));
    }

    /******************************************
     * Customer Chats Audit 相关
     ******************************************/
    public void putAgentUserAuditByOrgi(final String orgi, final AgentUserAudit audit) throws CSKefuCacheException {
        if (StringUtils.isBlank(audit.getAgentUserId())) {
            throw new CSKefuCacheException("agentUserId is required.");
        }
        redisCommand.setHashKV(
                RedisKey.getCustomerChatsAuditKeyByOrgi(orgi), audit.getAgentUserId(), SerializeUtil.serialize(audit));
    }

    public void deleteAgentUserAuditByOrgiAndId(final String orgi, final String agentUserId) {
        redisCommand.delHashKV(RedisKey.getCustomerChatsAuditKeyByOrgi(orgi), agentUserId);
    }

    public Optional<AgentUserAudit> findOneAgentUserAuditByOrgiAndId(final String orgi, final String agentUserId) {
        logger.info("[findOneAgentUserAuditByOrgiAndId] orgi {}, agentUserId {}", orgi, agentUserId);
        String serialized = redisCommand.getHashKV(RedisKey.getCustomerChatsAuditKeyByOrgi(orgi), agentUserId);
        if (StringUtils.isBlank(serialized)) {
            return Optional.empty();
        }
        return Optional.ofNullable((AgentUserAudit) SerializeUtil.deserialize(serialized));
    }

    public boolean existAgentUserAuditByOrgiAndId(final String orgi, final String agentUserId) {
        return redisCommand.hasHashKV(RedisKey.getCustomerChatsAuditKeyByOrgi(orgi), agentUserId);
    }


    /******************************************
     * User Session 相关
     ******************************************/
    /**
     * 存入user的session，存储这组信息是为了让客户的账号只能在一个浏览器内登录使用
     * 如果一个用户账号在多个浏览器使用，则登出之前的登录，只保留最后一个登录正常使用
     *
     * @param agentno
     * @param sessionId
     * @param orgi
     */
    public void putUserSessionByAgentnoAndSessionIdAndOrgi(final String agentno, final String sessionId, final String orgi) {
        redisCommand.setHashKV(RedisKey.getUserSessionKeyByOrgi(orgi), agentno, sessionId);
    }

    public boolean existUserSessionByAgentnoAndOrgi(final String agentno, final String orgi) {
        return redisCommand.hasHashKV(RedisKey.getUserSessionKeyByOrgi(orgi), agentno);
    }

    public String findOneSessionIdByAgentnoAndOrgi(final String agentno, final String orgi) {
        return redisCommand.getHashKV(RedisKey.getUserSessionKeyByOrgi(orgi), agentno);
    }

    public void deleteUserSessionByAgentnoAndOrgi(final String agentno, final String orgi) {
        redisCommand.delHashKV(RedisKey.getUserSessionKeyByOrgi(orgi), agentno);
    }

}
