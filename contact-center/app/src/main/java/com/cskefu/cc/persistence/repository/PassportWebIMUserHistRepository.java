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

import com.cskefu.cc.model.PassportWebIMUserHist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PassportWebIMUserHistRepository extends JpaRepository<PassportWebIMUserHist, String> {

    List<PassportWebIMUserHist> findByUserid(String userid);

    List<PassportWebIMUserHist> findBySessionid(String sessionId);

    @Query(nativeQuery = true, value = "SELECT * FROM cs_passport_webim_user_his WHERE sessionid = ?1 ORDER BY createtime DESC LIMIT 1")
    Optional<PassportWebIMUserHist> findOneBySessionid(String sessionId);
}
