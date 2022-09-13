/*
 * Copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
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

import com.cskefu.cc.model.FbOTN;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface FbOTNRepository extends JpaRepository<FbOTN, String> {
    List<FbOTN> findByPageId(final String pageId);

    @Query(nativeQuery = true, value = "select o.* from cs_fb_otn o left join cs_fb_messenger m on o.page_id = m.page_id where m.organ in (?1)")
    List<FbOTN> findByOrgans(final Collection<String> organIds);

    Page<FbOTN> findByPageIdIn(final Collection<String> organIds, Pageable page);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update cs_fb_otn set sub_num = sub_num +1 where id = ?1")
    void incOneSubNumById(final String id);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update cs_fb_otn set melink_num = melink_num +1 where id = ?1")
    void incOneMelinkNumById(final String id);
}
