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


import com.cskefu.cc.model.UserHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
public interface UserEventRepository extends JpaRepository<UserHistory, String> {

    @Query("select count(distinct ip) as ipnums, count(id) as pvnums from UserHistory where createtime > ?1 and createtime < ?2")
    List<Object> findByCreatetimeRange(Date start, Date end);

    @Query("select count(distinct ip) as ipnums, count(id) as pvnums from UserHistory where createtime > ?1 and createtime < ?2 and appid in (?3)")
    List<Object> findByCreatetimeRangeAndInAppIds(Date start, Date end, Collection<String> appids);

    Page<UserHistory> findBySessionid(String sessionid, Pageable page);
}