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

import com.cskefu.cc.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface AccountRepository extends JpaRepository<Account, String> {


    /**
     * 根据条件返回客户，符合一下条件之一：
     * 1. 联系人的 organ 字段在传入的 organs 列表中，并且联系人的 shares 字段值是 all
     *
     * @param organs
     * @param pageRequest
     * @return
     */
    @Query(value = "select a from Account a where a.organ IN :organs AND a.shares = 'all' AND a.datastatus = false")
    Page<Account> findByOrganInAndSharesAllAndDatastatusFalse(@Param("organs") Collection<String> organs, Pageable pageRequest);


    Page<Account> findByCreaterAndSharesAndDatastatus(String id, String id1, boolean b, Pageable pageRequest);
}
