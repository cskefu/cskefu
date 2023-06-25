/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2018- Jun. 2023 Chatopera Inc, <https://www.chatopera.com>,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (C) 2017 优客服-多渠道客服系统,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.cskefu.cc.persistence.repository;

import com.cskefu.cc.model.PassportWebIMUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PassportWebIMUserRepository extends JpaRepository<PassportWebIMUser, String> {

    @Query(nativeQuery = true, value = "SELECT * FROM cs_passport_webim_user WHERE userid = ?1 ORDER BY createtime DESC LIMIT 1")
    PassportWebIMUser findOneByUserid(final String userid);

    Page<PassportWebIMUser> findByUserid(String userid, Pageable page);

    Page<PassportWebIMUser> findByStatusAndCreatetimeLessThan(String status, Date createtime, Pageable paramPageable);

    Page<PassportWebIMUser> findByStatus(String paramString1, Pageable paramPageable);

    @Query("select invitestatus , count(id) as users from PassportWebIMUser where status = ?1 group by invitestatus")
    List<Object> findByStatus(String status);

    Page<PassportWebIMUser> findByStatusAndAppidIn(String paramString1, Collection<String> appids, Pageable paramPageable);

    @Query("select invitestatus , count(id) as users from PassportWebIMUser where status = ?1 and appid in (?2) group by invitestatus")
    List<Object> findByStatusAndInAppIds(String status, Collection<String> appids);

    @Query("select result, count(id) as records from InviteRecord where agentno = ?1 and createtime > ?2 and createtime < ?3 group by result")
    List<Object> findByAgentnoAndCreatetimeRange(String agentno, Date start, Date end);

    @Query("select result, count(id) as records from InviteRecord where agentno = ?1 group by result")
    List<Object> findByUserid(String userid);

    @Query("select count(id) from AgentService where status = ?1 and agentno = ?2 and createtime > ?3 and createtime < ?4")
    Long countByAgentForAgentUser(String status, String agentno, Date start, Date end);

    @Query("select avg(sessiontimes) from AgentService where status = ?1 and agentno = ?2 and createtime > ?3 and createtime < ?4")
    Long countByAgentForAvagTime(String status, String agentno, Date start, Date end);

    @Query("select avg(sessiontimes) from AgentService where status = ?1 and userid = ?2")
    Long countByUserForAvagTime(String status, String userid);

    @Query("select createdate as dt, count(distinct ip) as ips ,  count(id) as records from UserHistory where model = ?1 and createtime > ?2 and createtime < ?3 group by createdate order by dt asc")
    List<Object> findByCreatetimeRange(String model, Date start, Date end);

    @Query("select createdate as dt, count(id) as users from AgentService where createtime > ?1 and createtime < ?2 group by createdate order by dt asc")
    List<Object> findByCreatetimeRangeForAgent(Date start, Date end);

    @Query("select osname, count(id) as users from AgentService where createtime > ?1 and createtime < ?2 and channeltype = ?3 group by osname")
    List<Object> findByCreatetimeRangeForClient(Date start, Date end, String channel);

    @Query("select browser, count(id) as users from AgentService where createtime > ?1 and createtime < ?2 and channeltype = ?3 group by browser")
    List<Object> findByCreatetimeRangeForBrowser(Date start, Date end, String channel);

    @Query("select agentno, count(id) as users from AgentService where skill = ?1 and userid = ?2 group by agentno")
    List<Object> findBySkillForDistinctAgent(String skill, String userid);

    @Query(nativeQuery = true, value = "SELECT * FROM cs_passport_webim_user WHERE contactsid = ?1 AND channel = ?2 LIMIT 1")
    Optional<PassportWebIMUser> findOneByContactidAndChannel(final String contactId, final String channel);
}
