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

import com.cskefu.cc.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    User findByEmailAndDatastatus(String email, boolean datastatus);

    User findByMobileAndDatastatus(String mobile, boolean datastatus);

    @Query(nativeQuery = true, value = "SELECT u FROM User u WHERE u.callcenter = 1 " +
            "AND u.datastatus = 0 " +
            "AND (:users is null OR u.id IN :users)")
    List<User> findAllByCallcenterIsTrueAndDatastatusIsFalseAndIdIn(@Param("users") List<String> users);

    User findByUsernameAndDatastatus(String username, boolean datastatus);

    Optional<User> findByUsername(String username);

    User findByUsernameAndPasswordAndDatastatus(String username, String password, boolean datastatus);

    User findByUsernameAndPassword(String username, String password);

    // 查询所有管理员
    List<User> findByAdmin(boolean admin);

    Page<User> findByDatastatus(boolean datastatus, Pageable paramPageable);

    List<User> findByDatastatus(boolean datastatus);

    Page<User> findByDatastatusAndUsernameLike(boolean datastatus, String username, Pageable paramPageable);


    List<User> findByAgentAndDatastatus(final boolean isAgent, final boolean datastatus);

    long countByAgentAndDatastatusAndIdIn(
            final boolean agent,
            final boolean datastatus,
            final List<String> users);

    List<User> findAll(Specification<User> spec);

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

    List<User> findByAgentAndDatastatusAndIdIsNot(boolean agent, boolean datastatus, final String id);

    Page<User> findByIdIn(Collection<String> ids, Pageable pageRequest);

    List<User> findByIdIn(List<String> ids);
}
