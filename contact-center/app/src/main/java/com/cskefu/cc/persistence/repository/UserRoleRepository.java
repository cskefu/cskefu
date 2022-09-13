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

import com.cskefu.cc.model.Role;
import com.cskefu.cc.model.User;
import com.cskefu.cc.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, String> {
	List<UserRole> findByOrganAndRole(String organ, Role role);

	Page<UserRole> findByOrganAndRole(String organ, Role role,
			Pageable paramPageable);

	Page<UserRole> findByOrganInAndRole(Collection<String> organ, Role role,
			Pageable paramPageable);

	Page<UserRole> findByOrgiAndRole(String orgi, Role role, Pageable paramPageable);

	List<UserRole> findByOrgiAndRole(String orgi, Role role);

	List<UserRole> findByOrgiAndUser(String orgi, User user);

	List<UserRole> findByOrganAndUser(String organ, User user);

	@Query(value = "SELECT u.user_id FROM uk_userrole u WHERE u.orgi = ?1 AND u.role_id = ?2", nativeQuery = true)
	List<String> findByOrgiAndRoleId(final String orgi, final String roleid);

}
