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

import com.cskefu.cc.model.CousultInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ConsultInviteRepository extends JpaRepository<CousultInvite, String> {
    @Query(nativeQuery = true, value = "SELECT * from uk_consult_invite WHERE snsaccountid = ?1 LIMIT 1")
    CousultInvite findBySnsaccountid(@Param("snsaccountid") String Snsaccountid);

    @Query(nativeQuery = true, value = "select ci.`snsaccountid` from  `uk_consult_invite` ci  where ci.`consult_skill_fixed_id` in (?1)")
    List<String> findSNSIdBySkill(Collection<String> skills);

    List<CousultInvite> findAll();
}


