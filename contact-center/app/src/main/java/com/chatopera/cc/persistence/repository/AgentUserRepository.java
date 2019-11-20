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
package com.chatopera.cc.persistence.repository;

import com.chatopera.cc.model.AgentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AgentUserRepository extends JpaRepository<AgentUser, String> {
    AgentUser findByIdAndOrgi(String paramString, String orgi);

    @Query(value = "SELECT * FROM uk_agentuser WHERE userid = ?1 AND orgi = ?2 ORDER BY createtime DESC LIMIT 1", nativeQuery = true)
    Optional<AgentUser> findOneByUseridAndOrgi(String userid, String orgi);

    @Query(value = "SELECT * FROM uk_agentuser WHERE userid = ?1 LIMIT 1", nativeQuery = true)
    AgentUser findOneByUserid(final String userid);

    List<AgentUser> findByUseridAndOrgi(String userid, String orgi);

    List<AgentUser> findByUseridAndStatus(String userid, String status);

    List<AgentUser> findByAgentnoAndOrgi(String agentno, String orgi, Sort sort);

    List<AgentUser> findByAgentnoAndOrgi(String agentno, String orgi);

    Page<AgentUser> findByOrgiAndStatus(String orgi, String status, Pageable page);

    List<AgentUser> findByOrgiAndStatus(final String orgi, final String status, final Sort sort);

    List<AgentUser> findByOrgiAndStatusAndAgentnoIsNot(final String orgi, final String status, final String agentno, final Sort sort);

    List<AgentUser> findByOrgiAndStatusAndSkillAndAgentno(final String orgi, final String status, final String skill, final String agentno, Sort defaultSort);

    List<AgentUser> findByAgentnoAndStatusAndOrgi(String agentno, String status, String orgi);

    List<AgentUser> findByOrgiAndStatusAndSkillAndAgentnoIsNot(final String orgi, final String status, final String skill, final String agentno, final Sort sort);

    List<AgentUser> findByOrgiAndStatusAndAgentno(final String orgi, final String status, final String agentno, final Sort defaultSort);

    @Query(value = "SELECT a FROM AgentUser a WHERE a.userid in(:userids)")
    List<AgentUser> findAllByUserids(@Param("userids") List<String> userids);

    int countByAgentnoAndStatusAndOrgi(String agentno, String status, String orgi);

    AgentUser findOneByAgentnoAndStatusAndOrgi(String id, String status, String orgi);

    @Query(value = "SELECT * FROM uk_agentuser AS u " +
            "LEFT JOIN uk_agentuser_contacts AS c " +
            "ON u.userid = c.userid WHERE c.id = ?1 AND NOT u.status = ?2 AND c.orgi = ?3 LIMIT 1", nativeQuery = true)
    AgentUser findOneByContactIdAndStatusNotAndOrgi(final String contactid, final String status, final String orgi);

    @Query(value = "SELECT * FROM uk_agentuser AS u " +
            "LEFT JOIN uk_agentuser_contacts AS c " +
            "ON u.userid = c.userid WHERE c.contactsid = ?1 " +
            "AND c.channel = ?3 AND NOT u.status = ?2 AND c.orgi = ?4 " +
            "ORDER BY u.createtime DESC LIMIT 1", nativeQuery = true)
    Optional<AgentUser> findOneByContactIdAndStatusNotAndChannelAndOrgi(final String contactid, final String status, final String channel, final String orgi);

    @Query(value = "SELECT * FROM uk_agentuser AS u " +
            "LEFT JOIN uk_agentuser_contacts AS c " +
            "ON u.userid = c.userid WHERE c.contactsid = ?1 " +
            "AND c.channel = ?2 AND c.orgi = ?3 " +
            "ORDER BY u.createtime DESC LIMIT 1", nativeQuery = true)
    Optional<AgentUser> findOneByContactIdAndChannelAndOrgi(final String contactid, final String channel, final String orgi);


}
