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

import com.cskefu.cc.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {

    User findById(String id);

    User findByEmailAndDatastatus(String email, boolean datastatus);

    User findByMobileAndDatastatus(String mobile, boolean datastatus);

    @Query(value = "SELECT u FROM User u WHERE u.callcenter = 1 " +
            "AND u.datastatus = 0 " +
            "AND (:users is null OR u.id IN :users)")
    List<User> findAllByCallcenterIsTrueAndDatastatusIsFalseAndIdIn(@Param("users") List<String> users);

    User findByUsernameAndDatastatus(String username, boolean datastatus);

    User findByUsernameAndPasswordAndDatastatus(String username, String password, boolean datastatus);

    User findByUsernameAndPassword(String username, String password);

    Page<User> findByOrgi(String orgi, Pageable paramPageable);

    // 查询所有管理员
    List<User> findByAdminAndOrgi(boolean admin, final String orgi);

    List<User> findByOrgi(String orgi);

    Page<User> findByDatastatusAndOrgi(boolean datastatus, String orgi, Pageable paramPageable);

    Page<User> findByDatastatusAndOrgiAndUsernameLike(boolean datastatus, String orgi, String username, Pageable paramPageable);

    Page<User> findByIdAndOrgi(String id, String orgi, Pageable paramPageable);

    List<User> findByOrgiAndDatastatusAndIdIn(
            String orgi,
            boolean datastatus,
            List<String> users);

    List<User> findByOrgiAndDatastatus(final String orgi, final boolean datastatus);

    List<User> findByOrgiAndAgentAndDatastatus(final String orgi, final boolean agent, final boolean status);

    long countByAgentAndDatastatusAndIdIn(
            final boolean agent,
            final boolean datastatus,
            final List<String> users);

    List<User> findAll(Specification<User> spec);

    Page<User> findByDatastatusAndOrgiAndSuperadminNot(
            boolean datastatus, String orgi, boolean superadmin,
            Pageable pageRequest);

    Page<User> findByAgentAndDatastatusAndIdIn(
            boolean agent,
            boolean datastatus,
            final List<String> users,
            Pageable pageRequest);

    List<User> findByAgentAndDatastatusAndIdIn(boolean agent, boolean datastatus, final List<String> users);

    List<User> findByDatastatusAndIdIn(boolean datastatus, List<String> users);

    Page<User> findByDatastatusAndUsernameLikeAndIdIn(
            boolean datastatus,
            final String username,
            final List<String> users, Pageable pageRequest);

    List<User> findByOrgiAndAgentAndDatastatusAndIdIsNot(final String orgi, boolean agent, boolean datastatus, final String id);

    Page<User> findByIdIn(Iterable<String> ids, Pageable pageRequest);

    List<User> findByIdIn(List<String> ids);
}
