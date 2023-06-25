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

import com.cskefu.cc.model.Contacts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ContactsRepository extends JpaRepository<Contacts, String> {

    int countByDatastatusIsFalseAndPhone(String phone);

    List<Contacts> findOneByDatastatusIsFalseAndPhone(String phone);

    Contacts findOneByWluidAndWlsidAndWlcidAndDatastatus(String wluid, String wlsid, String wlcid, Boolean datastatus);

    Contacts findByskypeidAndDatastatus(String skypeid, Boolean datastatus);

    Contacts findByIdAndDatastatus(String id, Boolean datastatus);

    @Query(nativeQuery = true, value = "SELECT * FROM uk_contacts WHERE id = ?1")
    Optional<Contacts> findOneById(final String id);

    Page<Contacts> findByCreaterAndSharesAndDatastatus(String id, String shares, boolean datastatus, Pageable pageRequest);

    /**
     * 根据条件返回联系人，符合一下条件之一：
     * 1. 联系人的 organ 字段在传入的 organs 列表中，并且联系人的 shares 字段值是 all
     * @param creater
     * @param organs
     * @param pageRequest
     * @return
     */
    @Query("select c from Contacts c where c.organ IN :organs AND c.shares = 'all' AND c.datastatus = false")
    Page<Contacts> findByOrganInAndSharesAllAndDatastatusFalse(@Param("organs") Collection<String> organs, Pageable pageRequest);

    Page<Contacts> findByDatastatus(boolean b, Pageable pageRequest);
}
