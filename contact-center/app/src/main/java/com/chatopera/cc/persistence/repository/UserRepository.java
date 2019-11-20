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

import com.chatopera.cc.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    User findByIdAndOrgi(String paramString, String orgi);
    User findById(String id);
    User findByEmailAndDatastatus(String email, boolean datastatus);
    User findByMobileAndDatastatus(String mobile, boolean datastatus);

    @Query(value = "SELECT * FROM cs_user WHERE sipaccount = ?1 AND DATASTATUS = ?2 LIMIT 1", nativeQuery = true)
    Optional<User> findOneBySipaccountAndDatastatus(String sipaccount, boolean datastatus);

    @Query(value = "SELECT u FROM User u WHERE sipaccount <> '' AND datastatus = 0")
    List<User> findBySipaccountIsNotNullAndDatastatusIsFalse();

    @Query(value = "SELECT u FROM User u WHERE u.callcenter = 1 " +
            "AND u.datastatus = 0 " +
            "AND (:users is null OR u.id IN :users)")
    List<User> findAllByCallcenterIsTrueAndDatastatusIsFalseAndIdIn(@Param("users") List<String> users);

    User findByUsernameAndDatastatus(String username, boolean datastatus);
    User findByUsernameAndPasswordAndDatastatus(String username, String password, boolean datastatus);
    User findByMobileAndPasswordAndDatastatus(String mobile, String password, boolean datastatus);
    User findByUsernameAndOrgi(String paramString, String orgi);
    User findByUsernameAndPassword(String username, String password);
    Page<User> findByOrgi(String orgi, Pageable paramPageable);

//    // 查询系统管理员
//    List<User> findBySuperadminAndOrgi(boolean isSuperadmin, final String orgi);

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

    @Query(value = "SELECT s.sipaccount from User s " +
            "WHERE " +
            "s.sipaccount is not null AND " +
            "s.datastatus = :datastatus AND " +
            "s.id IN :users AND " +
            "s.orgi = :orgi")
    List<String> findSipsByDatastatusAndOrgiAndIdIn(
            @Param("datastatus") boolean datastatus,
            @Param("orgi") String orgi,
            @Param("users") List<String> users);

    List<User> findByOrgiAndDatastatus(final String orgi, final boolean datastatus);

    Page<User> findByOrgiAndAgentAndDatastatus(final String orgi, final boolean agent, boolean status, Pageable paramPageable);

    List<User> findByOrgiAndAgentAndDatastatus(final String orgi, final boolean agent, final boolean status);

    long countByAgentAndDatastatusAndIdIn(
            final boolean agent,
            final boolean datastatus,
            final List<String> users);

    List<User> findAll(Specification<User> spec);


    Page<User> findByDatastatusAndOrgiAndOrgid(
            boolean b, String orgi, String orgid,
            Pageable pageRequest);

    Page<User> findByDatastatusAndOrgiAndOrgidAndSuperadminNot(
            boolean datastatus, String orgi, String orgid, boolean superadmin,
            Pageable pageRequest);



    List<User> findByOrgiAndDatastatusAndOrgid(String orgi, boolean b, String orgid);


    Page<User> findByDatastatusAndOrgiAndOrgidAndUsernameLike(
            boolean Datastatus,
            final String orgi,
            final String orgid,
            final String username,
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

    List<User> findByOrgidAndAgentAndDatastatus(String orgid, boolean agent, boolean datastatus);


    List<User> findByOrgiAndAgentAndDatastatusAndIdIsNot(final String orgi, boolean agent, boolean datastatus, final String id);
}
