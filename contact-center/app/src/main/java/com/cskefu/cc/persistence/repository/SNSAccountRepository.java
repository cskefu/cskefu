/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
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

import com.cskefu.cc.model.SNSAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SNSAccountRepository
        extends JpaRepository<SNSAccount, String> {
    SNSAccount findByIdAndOrgi(String paramString, String orgi);

    boolean existsBySnsidAndSnstypeAndOrgi(String snsid, String snsType, String orgi);

    @Query(value = "SELECT * FROM `uk_snsaccount` WHERE snsid = ?1 LIMIT 1", nativeQuery = true)
    Optional<SNSAccount> findBySnsid(String snsid);

    SNSAccount findBySnsidAndOrgi(String snsid, String orgi);

    List<SNSAccount> findByBaseURLAndOrgi(String baseurl, String orgi);

    int countBySnsidAndOrgi(String snsid, String orgi);

    int countBySnstypeAndOrgi(final String snsType, final String orgi);

    List<SNSAccount> findBySnstypeAndOrgi(String snsType, String orgi);

    Page<SNSAccount> findBySnstypeAndOrgi(String paramString, String orgi, Pageable page);

    @Query(value = "select s.* from `uk_snsaccount` s WHERE s.snstype = ?1 AND s.orgi = ?2 and s.`organ` in (?3) ORDER BY ?#{#pageable}",
            countQuery = "select count(1) from `uk_snsaccount` s WHERE s.snstype = ?1 AND s.orgi = ?2 and s.`organ` in (?3)", nativeQuery = true)
    Page<SNSAccount> findBySnstypeAndOrgiAndOrgan(String paramString, String orgi, Collection<String> organ, Pageable page);

    @Query(value = "SELECT * FROM uk_snsaccount WHERE snstype = ?1 AND snsid = ?2 AND orgi = ?3 LIMIT 1", nativeQuery = true)
    Optional<SNSAccount> findOneBySnsTypeAndSnsIdAndOrgi(final String snsType, final String snsId, final String orgi);

    @Query(value = "SELECT * FROM uk_snsaccount WHERE snstype = ?1 AND orgi = ?2 LIMIT 1", nativeQuery = true)
    SNSAccount findOneBySnstypeAndOrgi(final String snsType, final String orgi);


}
