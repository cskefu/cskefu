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

import com.cskefu.cc.model.Channel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelRepository
        extends JpaRepository<Channel, String> {

    Channel findById(String id);

    boolean existsBySnsidAndType(String snsid, String type);

    @Query(value = "SELECT * FROM `cs_channel` WHERE snsid = ?1 LIMIT 1", nativeQuery = true)
    Optional<Channel> findBySnsid(String snsid);

    List<Channel> findByBaseURL(String baseurl);

    int countBySnsid(String snsid);

    int countByType(final String type);

    List<Channel> findByType(String type);

    Page<Channel> findByType(String paramString, Pageable page);

    @Query(value = "select s.* from `cs_channel` s WHERE s.type = ?1 and s.`organ` in (?2) ORDER BY ?#{#pageable}",
            countQuery = "select count(1) from `cs_channel` s WHERE s.type = ?1 and s.`organ` in (?2)", nativeQuery = true)
    Page<Channel> findByTypeAndOrgan(String type, Collection<String> organ, Pageable page);

    @Query(value = "SELECT * FROM cs_channel WHERE type = ?1 AND snsid = ?2 LIMIT 1", nativeQuery = true)
    Optional<Channel> findOneBySnsTypeAndSnsId(final String type, final String snsId);

    @Query(value = "SELECT * FROM cs_channel WHERE type = ?1 LIMIT 1", nativeQuery = true)
    Channel findOneByType(final String type);


}
