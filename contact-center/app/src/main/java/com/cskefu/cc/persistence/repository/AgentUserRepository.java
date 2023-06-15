/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2023 Chatopera Inc, <https://www.chatopera.com>
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
package com.cskefu.cc.persistence.repository;

import com.cskefu.cc.model.AgentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AgentUserRepository extends JpaRepository<AgentUser, String> {
    AgentUser findById(String paramString);

    @Query(value = "SELECT * FROM uk_agentuser WHERE userid = ?1 ORDER BY createtime DESC LIMIT 1", nativeQuery = true)
    Optional<AgentUser> findOneByUserid(String userid);

    List<AgentUser> findByUserid(String userid);

    List<AgentUser> findByUseridAndStatus(String userid, String status);

    List<AgentUser> findByAgentno(String agentno, Sort sort);

    List<AgentUser> findByAgentno(String agentno);

    Page<AgentUser> findByStatus(String status, Pageable page);

    Page<AgentUser> findByStatusAndSkillIn(String status, Set<String> agentskill, Pageable page);

    int countByStatusAndSkillIn(String status, Set<String> agentskill);

    List<AgentUser> findByStatus(final String status, final Sort sort);

    List<AgentUser> findByStatusAndAgentnoIsNot(final String status, final String agentno, final Sort sort);

    List<AgentUser> findByStatusAndSkillAndAgentno(final String status, final String skill, final String agentno, Sort defaultSort);

    List<AgentUser> findByAgentnoAndStatus(String agentno, String status);

    List<AgentUser> findByStatusAndSkillAndAgentnoIsNot(final String status, final String skill, final String agentno, final Sort sort);

    List<AgentUser> findByStatusAndSkillInAndAgentnoIsNotAndChatbotopsIsFalse(final String status, final Collection<String> skill, final String agentno, final Sort sort);

    List<AgentUser> findByStatusAndAgentno(final String status, final String agentno, final Sort defaultSort);

    @Query(value = "SELECT a FROM AgentUser a WHERE a.userid in(:userids)")
    List<AgentUser> findAllByUserids(@Param("userids") List<String> userids);

    int countByAgentnoAndStatus(String agentno, String status);

    int countByAgentnoAndStatusAndSkill(String agentno, String status, String skill);

    AgentUser findOneByAgentnoAndStatus(String id, String status);

    @Query(value = "SELECT * FROM uk_agentuser AS u " +
            "LEFT JOIN uk_agentuser_contacts AS c " +
            "ON u.userid = c.userid WHERE c.id = ?1 AND NOT u.status = ?2 LIMIT 1", nativeQuery = true)
    AgentUser findOneByContactIdAndStatusNot(final String contactid, final String status);

    @Query(value = "SELECT * FROM uk_agentuser AS u " +
            "LEFT JOIN uk_agentuser_contacts AS c " +
            "ON u.userid = c.userid WHERE c.contactsid = ?1 " +
            "AND c.channeltype = ?3 AND NOT u.status = ?2 " +
            "ORDER BY u.createtime DESC LIMIT 1", nativeQuery = true)
    Optional<AgentUser> findOneByContactIdAndStatusNotAndChanneltype(final String contactid, final String status, final String channeltype);

    @Query(value = "SELECT * FROM uk_agentuser AS u " +
            "LEFT JOIN uk_agentuser_contacts AS c " +
            "ON u.userid = c.userid WHERE c.contactsid = ?1 " +
            "AND c.channeltype = ?2 " +
            "ORDER BY u.createtime DESC LIMIT 1", nativeQuery = true)
    Optional<AgentUser> findOneByContactIdAndChanneltype(final String contactid, final String channeltype);

    @Query(value = "SELECT * FROM uk_agentuser AS u " +
            "WHERE u.userid = ?1 " +
            "AND u.channeltype = ?3 AND NOT u.status = ?2 " +
            "ORDER BY u.createtime DESC LIMIT 1", nativeQuery = true)
    Optional<AgentUser> findOneByUseridAndStatusNotAndChanneltype(final String userid, final String status, final String channeltype);

    AgentUser findOneByUseridAndStatusAndChanneltype(String userid, String status, String channeltype);
}
