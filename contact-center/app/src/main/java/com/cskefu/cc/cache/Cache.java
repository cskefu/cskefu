/* 
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2019-2022 Chatopera Inc, <https://www.chatopera.com>, 
 * Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.cskefu.cc.cache;

import com.cskefu.cc.aspect.AgentUserAspect;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.exception.CSKefuCacheException;
import com.cskefu.cc.model.*;
import com.cskefu.cc.persistence.repository.AgentUserRepository;
import com.cskefu.cc.persistence.repository.PassportWebIMUserRepository;
import com.cskefu.cc.util.SerializeUtil;
import com.cskefu.cc.util.freeswitch.model.CallCenterAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;

@Component
public class Cache {

    final static private Logger logger = LoggerFactory.getLogger(Cache.class);

    @Autowired
    private PassportWebIMUserRepository onlineUserRes;

    @Autowired
    private AgentUserRepository agentUserRes;

    @Autowired
    private RedisCommand redisCommand;

    /**
     * 获得就绪的坐席列表
     *
     * @return
     */
    public Map<String, AgentStatus> getAgentStatusReady() {
        Map<String, String> agentStatuses = redisCommand.getHash(RedisKey.getAgentStatusReadyHashKey());
        return convertFromStringToAgentStatus(agentStatuses);
    }

    /**
     * 通过访客ID获得访客坐席关联关系
     *
     * @param userId
     * @return
     */
    public Optional<AgentUser> findOneAgentUserByUserId(final String userId) {
        if (redisCommand.hasHashKV(RedisKey.getAgentUserInQueHashKey(), userId)) {
            // 排队等待中
            return Optional.ofNullable((AgentUser) SerializeUtil.deserialize(
                    redisCommand.getHashKV(RedisKey.getAgentUserInQueHashKey(), userId)));
        } else if (redisCommand.hasHashKV(RedisKey.getAgentUserInServHashKey(), userId)) {
            // 服务中
            return Optional.ofNullable((AgentUser) SerializeUtil.deserialize(
                    redisCommand.getHashKV(RedisKey.getAgentUserInServHashKey(), userId)));
        } else if (redisCommand.hasHashKV(RedisKey.getAgentUserEndHashKey(), userId)) {
            // 已经结束
            return Optional.ofNullable((AgentUser) SerializeUtil.deserialize(
                    redisCommand.getHashKV(RedisKey.getAgentUserEndHashKey(), userId)));
        } else {
            // 缓存中没有找到，继续到数据库查找
            return agentUserRes.findOneByUserid(userId);
        }
    }


    /**
     * 返回排队中的客服列表
     *
     * @return
     */
    public Map<String, AgentUser> getAgentUsersInQue() {
        Map<String, String> agentUsers = redisCommand.getHash(RedisKey.getAgentUserInQueHashKey());
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
     */
    public void deleteAgentUserInservByAgentUserId(final String userid) {
        redisCommand.delHashKV(RedisKey.getAgentUserInServHashKey(), userid);
    }


    /**
     * 将访客ID从排队队列中删除
     *
     * @param userid
     */
    public void deleteAgentUserInqueByAgentUserId(final String userid) {
        redisCommand.delHashKV(RedisKey.getAgentUserInQueHashKey(), userid);
    }

    /**
     * 获得一个坐席的状态
     *
     * @param agentno 坐席ID
     * @return
     */
    public AgentStatus findOneAgentStatusByAgentno(final String agentno) {
        String status = getAgentStatusStatus(agentno);
        logger.debug("[findOneAgentStatusByAgentnoAndOrig] agentno {}, status {}", agentno, status);

        // 缓存中没有该坐席状态，该坐席目前是离线的
        if (StringUtils.equals(status, MainContext.AgentStatusEnum.OFFLINE.toString())) {
            return null;
        }

        String val = redisCommand.getHashKV(RedisKey.getAgentStatusHashKeyByStatusStr(status), agentno);
        AgentStatus result = SerializeUtil.deserialize(val);
        logger.debug("[findOneAgentStatusByAgentnoAndOrig] result: username {}", result.getUsername());
        return result;
    }

    /**
     * 更新坐席状态
     *
     * @param agentStatus
     */
    public void putAgentStatus(AgentStatus agentStatus) {
        String pre = getAgentStatusStatus(agentStatus.getAgentno()); // 坐席前状态

        if (StringUtils.equals(pre, MainContext.AgentStatusEnum.OFFLINE.toString())) {
            // 之前不存在，新建缓存
            if ((!StringUtils.equals(agentStatus.getStatus(), MainContext.AgentStatusEnum.OFFLINE.toString()))) {
                redisCommand.setHashKV(
                        RedisKey.getAgentStatusHashKeyByStatusStr(agentStatus.getStatus()),
                        agentStatus.getAgentno(), SerializeUtil.serialize(agentStatus));
            }
            return;
        } else {
            // 之前存在，与将要更新的状态一致
            if (StringUtils.equals(pre, agentStatus.getStatus())) {
                redisCommand.setHashKV(
                        RedisKey.getAgentStatusHashKeyByStatusStr(pre), agentStatus.getAgentno(),
                        SerializeUtil.serialize(agentStatus));
                return;
            } else {
                // 之前存在，而且与新状态不一致
                redisCommand.delHashKV(RedisKey.getAgentStatusHashKeyByStatusStr(pre), agentStatus.getAgentno());
                if (!StringUtils.equals(agentStatus.getStatus(), MainContext.AgentStatusEnum.OFFLINE.toString())) {
                    redisCommand.setHashKV(
                            RedisKey.getAgentStatusHashKeyByStatusStr(agentStatus.getStatus()),
                            agentStatus.getAgentno(), SerializeUtil.serialize(agentStatus));
                }
            }
        }
    }

    /**
     * 获得一个租户的就绪坐席状态
     *
     * @return
     */
    public Map<String, AgentStatus> findAllReadyAgentStatus() {
        List<String> keys = new ArrayList<>();
        keys.add(RedisKey.getAgentStatusReadyHashKey());

        Map<String, String> map = redisCommand.getAllMembersInMultiHash(keys);
        return convertFromStringToAgentStatus(map);
    }

    /**
     * 获得一个租户的所有坐席状态
     *
     * @return
     */
    public Map<String, AgentStatus> findAllAgentStatus() {
        List<String> keys = new ArrayList<>();
        // TODO 增加支持更多状态
        keys.add(RedisKey.getAgentStatusReadyHashKey());
        keys.add(RedisKey.getAgentStatusNotReadyHashKey());

        Map<String, String> map = redisCommand.getAllMembersInMultiHash(keys);
        return convertFromStringToAgentStatus(map);
    }


    /**
     * Inline方法
     */
    private static Map<String, AgentStatus> convertFromStringToAgentStatus(final Map<String, String> map) {
        Map<String, AgentStatus> result = new HashMap<>();
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
     */
    public void deleteAgentStatusByAgentno(final String agentno) {
        String status = getAgentStatusStatus(agentno);
        if (!StringUtils.equals(MainContext.AgentStatusEnum.OFFLINE.toString(), status)) {
            redisCommand.delHashKV(RedisKey.getAgentStatusHashKeyByStatusStr(status), agentno);
        }
    }

    /**
     * 获得一个坐席的状态 agentStatus.status
     * 只返回大类状态
     *
     * @param agentno
     * @return
     */
    private String getAgentStatusStatus(final String agentno) {
        // 首先判断这个坐席的状态是READY还是BUSY，再去更新
        if (redisCommand.hasHashKV(RedisKey.getAgentStatusReadyHashKey(), agentno)) {
            return MainContext.AgentStatusEnum.READY.toString();
        } else if (redisCommand.hasHashKV(RedisKey.getAgentStatusNotReadyHashKey(), agentno)) {
            return MainContext.AgentStatusEnum.NOTREADY.toString();
        } else {
            return MainContext.AgentStatusEnum.OFFLINE.toString();
        }
    }


    /**
     * 获得技能组的坐席状态
     *
     * @param skill
     * @return
     */
    public List<AgentStatus> getAgentStatusBySkill(final String skill) {
        Map<String, AgentStatus> map = findAllAgentStatus();
        List<AgentStatus> agentList = new ArrayList<>();

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
     * @return
     */
    public int getAgentStatusReadySize() {
        return Math.toIntExact(redisCommand.getHashSize(RedisKey.getAgentStatusReadyHashKey()));
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
     */
    @AgentUserAspect.LinkAgentUser
    public void putAgentUser(AgentUser agentUser) {
        if (redisCommand.hasHashKV(RedisKey.getAgentUserInServHashKey(), agentUser.getUserid())) {
            // 服务中
            if (!StringUtils.equals(
                    agentUser.getStatus(),
                    MainContext.AgentUserStatusEnum.INSERVICE.toString())) {
                // 删除旧记录
                redisCommand.delHashKV(RedisKey.getAgentUserInServHashKey(), agentUser.getUserid());
            }
        } else if (redisCommand.hasHashKV(RedisKey.getAgentUserInQueHashKey(), agentUser.getUserid())) {
            // 等待服务
            if (!StringUtils.equals(
                    agentUser.getStatus(),
                    MainContext.AgentUserStatusEnum.INQUENE.toString())) {
                // 删除旧记录
                redisCommand.delHashKV(RedisKey.getAgentUserInQueHashKey(), agentUser.getUserid());
            }
        }

        // 更新新记录，忽略状态为END的agentUser，已结束的服务不加入缓存
        if (!StringUtils.equals(agentUser.getStatus(), MainContext.AgentUserStatusEnum.END.toString())) {
            redisCommand.setHashKV(
                    RedisKey.getAgentUserHashKeyByStatusStr(agentUser.getStatus()), agentUser.getUserid(),
                    SerializeUtil.serialize(agentUser));
        }
    }


    /**
     * 获得一个客服服务中的访客列表
     *
     * @param agentno
     * @return
     */
    public List<AgentUser> findInservAgentUsersByAgentno(final String agentno) {
        logger.info("[findInservAgentUsersByAgentno] agentno {}", agentno);
        List<AgentUser> result = new ArrayList<>();
        List<String> ids = redisCommand.getSet(RedisKey.getInServAgentUsersByAgentno(agentno));
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
     * @return
     */
    public int getInservAgentUsersSizeByAgentno(final String agentno) {
        return Math.toIntExact(redisCommand.getSetSize(RedisKey.getInServAgentUsersByAgentno(agentno)));
    }

    /**
     * 获得服务中的访客的数量
     *
     * @return
     */
    public int getInservAgentUsersSize() {
        return redisCommand.getHashSize(RedisKey.getAgentUserInServHashKey());
    }

    /**
     * 获得等待中的访客的数量
     *
     * @return
     */
    public int getInqueAgentUsersSize() {
        return redisCommand.getHashSize(RedisKey.getAgentUserInQueHashKey());
    }

    /**
     * Delete agentUser
     *
     * @param agentUser
     */
    @AgentUserAspect.LinkAgentUser
    public void deleteAgentUserByUserId(final AgentUser agentUser) {
        if (redisCommand.hasHashKV(RedisKey.getAgentUserInQueHashKey(), agentUser.getUserid())) {
            // 排队等待中
            redisCommand.delHashKV(RedisKey.getAgentUserInQueHashKey(), agentUser.getUserid());
        } else if (redisCommand.hasHashKV(RedisKey.getAgentUserInServHashKey(), agentUser.getUserid())) {
            redisCommand.delHashKV(RedisKey.getAgentUserInServHashKey(), agentUser.getUserid());
        } else if (redisCommand.hasHashKV(RedisKey.getAgentUserEndHashKey(), agentUser.getUserid())) {
            redisCommand.delHashKV(RedisKey.getAgentUserEndHashKey(), agentUser.getUserid());
        } else {
            // TODO 考虑是否有其他状态保存
        }
    }

    /***************************
     * CousultInvite 相关
     ***************************/
    public void putConsultInvite(final CousultInvite cousultInvite) {
        redisCommand.setHashKV(
                RedisKey.getConsultInvites(), cousultInvite.getSnsaccountid(),
                SerializeUtil.serialize(cousultInvite));
    }

    public CousultInvite findOneConsultInviteBySnsid(final String snsid) {
        String serialized = redisCommand.getHashKV(RedisKey.getConsultInvites(), snsid);
        if (StringUtils.isBlank(serialized)) {
            return null;
        } else {
            return (CousultInvite) SerializeUtil.deserialize(serialized);
        }
    }

    public void deleteConsultInviteBySnsid(final String snsid) {
        redisCommand.delHashKV(RedisKey.getConsultInvites(), snsid);
    }


    /****************************
     *  OnlineUser相关
     ****************************/

    /**
     * 更新 onlineUser
     *
     * @param passportWebIMUser
     */
    public void putOnlineUser(final PassportWebIMUser passportWebIMUser) {
        // 此处onlineUser的id 与 onlineUser userId相同
        redisCommand.setHashKV(
                RedisKey.getOnlineUserHashKey(), passportWebIMUser.getId(), SerializeUtil.serialize(passportWebIMUser));
    }

    /**
     * 获得 onlineUser
     *
     * @param id
     * @return
     */
    public PassportWebIMUser findOneOnlineUserByUserId(final String id) {
        String serialized = redisCommand.getHashKV(RedisKey.getOnlineUserHashKey(), id);
        if (StringUtils.isBlank(serialized)) {
            // query with MySQL
            return onlineUserRes.findOneByUserid(id);
        } else {
            return convertFromStringToOnlineUser(serialized);
        }
    }

    private static PassportWebIMUser convertFromStringToOnlineUser(final String serialized) {
        PassportWebIMUser obj = SerializeUtil.deserialize(serialized);
        return obj;
    }

    /**
     * 删除 onlineUser
     *
     * @param id
     */
    public void deleteOnlineUserById(final String id) {
        redisCommand.delHashKV(RedisKey.getOnlineUserHashKey(), id);
    }

    /**
     * 根据租户ID获得在线访客的列表大小
     */
    public int getOnlineUserSize() {
        return redisCommand.getHashSize(RedisKey.getOnlineUserHashKey());
    }


    /**
     * 将在线访客从一个坐席的服务列表中删除
     *
     * @param userid
     * @param agentno
     */
    public void deleteOnlineUserIdFromAgentStatusByUseridAndAgentno(final String userid, final String agentno) {
        redisCommand.removeSetVal(RedisKey.getInServAgentUsersByAgentno(agentno), userid);
    }

    private Map<String, PassportWebIMUser> convertFromStringToOnlineUsers(final Map<String, String> map) {
        Map<String, PassportWebIMUser> result = new HashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            PassportWebIMUser x = SerializeUtil.deserialize(entry.getValue());
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
     * @param agent
     */
    public void putCallCenterAgentById(final String id, final CallCenterAgent agent) {
        redisCommand.setHashKV(RedisKey.getCallCenterAgentHashKey(), id, SerializeUtil.serialize(agent));
    }

    /**
     * 根据ID和租户ID获得CallCenterAgent
     *
     * @param id
     * @return
     */
    public CallCenterAgent findOneCallCenterAgentById(final String id) {
        String serialized = redisCommand.getHashKV(RedisKey.getCallCenterAgentHashKey(), id);
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
     */
    public void deleteCallCenterAgentById(final String id) {
        redisCommand.delHashKV(RedisKey.getCallCenterAgentHashKey(), id);
    }


    /**
     * 根据租户ID获得所有的CallCenterAgent
     *
     * @return
     */
    public Map<String, CallCenterAgent> findAllCallCenterAgents() {
        Map<String, String> map = redisCommand.getHash(RedisKey.getCallCenterAgentHashKey());
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
    public void putBlackEntity(final BlackEntity blackEntity) {
        redisCommand.setHashKV(
                RedisKey.getBlackEntityKey(), blackEntity.getUserid(), SerializeUtil.serialize(blackEntity));
    }

    // 通过指定的访客和租户查找黑名单
    public Optional<BlackEntity> findOneBlackEntityByUserId(final String userid) {
        String ser = redisCommand.getHashKV(RedisKey.getBlackEntityKey(), userid);
        if (StringUtils.isBlank(ser)) {
            return Optional.empty();
        }

        return Optional.ofNullable(SerializeUtil.deserialize(ser));
    }

    // 将一个访客从黑名单中移除
    public void deleteBlackEntityByUserId(final String userid) {
        redisCommand.delHashKV(RedisKey.getBlackEntityKey(), userid);
    }

    // 指定的访客是否在租户的黑名单中
    public boolean existBlackEntityByUserId(final String userid) {
        return redisCommand.hasHashKV(RedisKey.getBlackEntityKey(), userid);
    }

    // 根据租户ID获得所有访客的黑名单
    public Map<String, BlackEntity> findAllBlackEntity() {
        Map<String, BlackEntity> result = new HashMap<>();
        for (Map.Entry<String, String> entry : redisCommand.getHash(
                RedisKey.getBlackEntityKey()).entrySet()) {
            result.put(entry.getKey(), SerializeUtil.deserialize(entry.getValue()));
        }
        return result;
    }


    /*****************************
     * Job 相关
     *****************************/
    public void putJobById(final String jobId, final JobDetail job) {
        redisCommand.setHashKV(RedisKey.getJobHashKey(), jobId, SerializeUtil.serialize(job));
    }

    public JobDetail findOneJobById(final String jobId) {
        String serialized = redisCommand.getHashKV(RedisKey.getJobHashKey(), jobId);

        if (StringUtils.isNotBlank(serialized)) {
            return (JobDetail) SerializeUtil.deserialize(serialized);
        }
        return null;
    }

    public boolean existJobById(final String jobId) {
        return redisCommand.hasHashKV(RedisKey.getJobHashKey(), jobId);
    }

    public void deleteJobByJobId(final String jobId) {
        redisCommand.delHashKV(RedisKey.getJobHashKey(), jobId);
    }

    /**
     * 系统词典相关
     */
    // 存储根词典
    public void putSysDic(final String id, final SysDic sysDic) {
        redisCommand.setHashKV(RedisKey.getSysDicHashKey(), id, SerializeUtil.serialize(sysDic));
    }

    // 将指定租户的系统词典清空
    public void eraseSysDic() {
        redisCommand.delete(RedisKey.getSysDicHashKey());
    }

    // 存储词典子项
    public void putSysDic(final String code, final List<SysDic> sysDics) {
        redisCommand.setHashKV(RedisKey.getSysDicHashKey(), code, SerializeUtil.serialize(sysDics));
    }

    // 获得词典的子项列表
    public List<SysDic> getSysDicItemsByCode(final String code) {
        String serialized = redisCommand.getHashKV(RedisKey.getSysDicHashKey(), code);
        if (serialized != null) {
            return (List<SysDic>) SerializeUtil.deserialize(serialized);
        }
        return null;
    }

    // 获得词典子项
    public SysDic findOneSysDicByCode(final String code) {
        String serialized = redisCommand.getHashKV(RedisKey.getSysDicHashKey(), code);

        if (StringUtils.isBlank(serialized)) {
            return null;
        }

        return (SysDic) SerializeUtil.deserialize(serialized);
    }

    // 获得词典
    public SysDic findOneSysDicById(final String id) {
        String serialized = redisCommand.getHashKV(RedisKey.getSysDicHashKey(), id);

        if (StringUtils.isBlank(serialized)) {
            return null;
        }

        return (SysDic) SerializeUtil.deserialize(serialized);
    }

    // 批量存储
    public void putSysDic(List<SysDic> vals) {
        Map<String, String> map = new HashMap<>();
        for (final SysDic dic : vals) {
            map.put(dic.getId(), SerializeUtil.serialize(dic));
        }
        redisCommand.hmset(RedisKey.getSysDicHashKey(), map);
    }

    public void deleteSysDicById(final String id) {
        redisCommand.delHashKV(RedisKey.getSysDicHashKey(), id);
    }

    public boolean existSysDicById(final String id) {
        return redisCommand.hasHashKV(RedisKey.getSysDicHashKey(), id);
    }

    /**
     * System 相关
     */
    public <T extends Serializable> void putSystemById(final String id, final T obj) {
        redisCommand.setHashKV(RedisKey.getSystemHashKey(), id, SerializeUtil.serialize(obj));
    }

    public <T extends Serializable> void putSystemListById(final String id, final List<T> obj) {
        redisCommand.setHashKV(RedisKey.getSystemHashKey(), id, SerializeUtil.serialize(obj));
    }

    public <TK, TV extends Serializable> void putSystemMapById(final String id, final Map<TK, TV> obj) {
        redisCommand.setHashKV(RedisKey.getSystemHashKey(), id, SerializeUtil.serialize(obj));
    }

    public boolean existSystemById(final String id) {
        return redisCommand.hasHashKV(RedisKey.getSystemHashKey(), id);
    }

    public void deleteSystembyId(final String id) {
        redisCommand.delHashKV(RedisKey.getSystemHashKey(), id);
    }

    public <T extends Serializable> T findOneSystemById(final String id) {
        String serialized = redisCommand.getHashKV(RedisKey.getSystemHashKey(), id);
        if (StringUtils.isNotBlank(serialized)) {
            return (T) SerializeUtil.deserialize(serialized);
        }
        return null;
    }

    public <T extends Serializable> List<T> findOneSystemListById(final String id) {
        String serialized = redisCommand.getHashKV(RedisKey.getSystemHashKey(), id);
        if (StringUtils.isNotBlank(serialized)) {
            return (List<T>) SerializeUtil.deserialize(serialized);
        }
        return null;
    }

    public <TK, TV extends Serializable> Map<TK, TV> findOneSystemMapById(final String id) {
        String serialized = redisCommand.getHashKV(RedisKey.getSystemHashKey(), id);
        if (StringUtils.isNotBlank(serialized)) {
            return (Map<TK, TV>) SerializeUtil.deserialize(serialized);
        }
        return null;
    }

    // 获得系统cache的列表大小
    public int getSystemSize() {
        return redisCommand.getHashSize(RedisKey.getSystemHashKey());
    }

    /**************************
     * Session Config 相关
     **************************/

    public void putSessionConfig(final SessionConfig sessionConfig, String organid) {
        redisCommand.put(RedisKey.getSessionConfig(organid), SerializeUtil.serialize(sessionConfig));
    }

    public SessionConfig findOneSessionConfig(String organid) {
        String serialized = redisCommand.get(RedisKey.getSessionConfig(organid));
        if (StringUtils.isNotBlank(serialized)) {
            return (SessionConfig) SerializeUtil.deserialize(serialized);
        }
        return null;
    }

    public void deleteSessionConfig(String organid) {
        redisCommand.delete(RedisKey.getSessionConfig(organid));
    }

    public boolean existSessionConfig(String organid) {
        return redisCommand.exists(RedisKey.getSessionConfig(organid));
    }

    public void putSessionConfigList(final List<SessionConfig> lis) {
        redisCommand.put(RedisKey.getSessionConfigList(), SerializeUtil.serialize(lis));
    }

    public List<SessionConfig> findOneSessionConfigList() {
        String serialized = redisCommand.get(RedisKey.getSessionConfigList());
        if (StringUtils.isNotBlank(serialized)) {
            return (List<SessionConfig>) SerializeUtil.deserialize(serialized);
        }

        return null;
    }

    public void deleteSessionConfigList() {
        redisCommand.delete(RedisKey.getSessionConfigList());
    }

    public boolean existSessionConfigList() {
        return redisCommand.exists(RedisKey.getSessionConfigList());
    }

    /******************************************
     * Customer Chats Audit 相关
     ******************************************/
    public void putAgentUserAudit(final AgentUserAudit audit) throws CSKefuCacheException {
        if (StringUtils.isBlank(audit.getAgentUserId())) {
            throw new CSKefuCacheException("agentUserId is required.");
        }
        redisCommand.setHashKV(
                RedisKey.getCustomerChatsAuditKey(), audit.getAgentUserId(), SerializeUtil.serialize(audit));
    }

    public void deleteAgentUserAuditById(final String agentUserId) {
        redisCommand.delHashKV(RedisKey.getCustomerChatsAuditKey(), agentUserId);
    }

    public Optional<AgentUserAudit> findOneAgentUserAuditById(final String agentUserId) {
        logger.info("[findOneAgentUserAuditById] agentUserId {}", agentUserId);
        String serialized = redisCommand.getHashKV(RedisKey.getCustomerChatsAuditKey(), agentUserId);
        if (StringUtils.isBlank(serialized)) {
            return Optional.empty();
        }
        return Optional.ofNullable((AgentUserAudit) SerializeUtil.deserialize(serialized));
    }

    public boolean existAgentUserAuditById(final String agentUserId) {
        return redisCommand.hasHashKV(RedisKey.getCustomerChatsAuditKey(), agentUserId);
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
     */
    public void putUserSessionByAgentnoAndSessionId(final String agentno, final String sessionId) {
        redisCommand.setHashKV(RedisKey.getUserSessionKey(), agentno, sessionId);
    }

    public boolean existUserSessionByAgentno(final String agentno) {
        return redisCommand.hasHashKV(RedisKey.getUserSessionKey(), agentno);
    }

    public String findOneSessionIdByAgentno(final String agentno) {
        return redisCommand.getHashKV(RedisKey.getUserSessionKey(), agentno);
    }

    public void deleteUserSessionByAgentno(final String agentno) {
        redisCommand.delHashKV(RedisKey.getUserSessionKey(), agentno);
    }

}
