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

import com.cskefu.cc.model.LeaveMsg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface LeaveMsgRepository extends JpaRepository<LeaveMsg, String> {
    Page<LeaveMsg> findAll(Pageable page);

    @Query(nativeQuery = true, value = "SELECT o.name as skillName,m.* FROM uk_leavemsg m LEFT JOIN uk_organ o ON m.skill=o.id WHERE m.skill IN (?1) ORDER BY ?#{#pageable}",
            countQuery = "SELECT count(*) FROM uk_leavemsg WHERE skill IN (?1) ORDER BY ?#{#pageable}")
    Page<LeaveMsg> findBySkill(Set<String> skill, Pageable page);

    List<LeaveMsg> findByMobile(String mobile);

    List<LeaveMsg> findByUserid(String userid);
}

