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
package com.chatopera.cc.persistence.es;

import com.chatopera.cc.model.Contacts;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContactsRepository extends ElasticsearchRepository<Contacts, String>, ContactsEsCommonRepository {

    int countByDatastatusIsFalseAndPhoneAndOrgi(String phone, String orgi);

    List<Contacts> findOneByDatastatusIsFalseAndPhoneAndOrgi(String phone, String orgi);

    Contacts findOneByWluidAndWlsidAndWlcidAndDatastatus(String wluid, String wlsid, String wlcid, Boolean datastatus);

    List<Contacts> findByskypeidAndDatastatus(String skypeid, Boolean datastatus);

    Contacts findByskypeidAndOrgiAndDatastatus(String skypeid, String orgi, Boolean datastatus);

    @Query(value = "SELECT * FROM uk_contacts WHERE skypeid = ?1 AND datastatus = ?2 LIMIT 1", nativeQuery = true)
    Contacts findOneBySkypeidAndDatastatus(String skypeid, boolean datastatus);

    List<Contacts> findByidAndDatastatus(String id, Boolean datastatus);

    Contacts findByidAndOrgiAndDatastatus(String id, String orgi, Boolean datastatus);

    @Query(value = "select u from uk_contacts u where u.skypeid = ?1")
    List<Contacts> findByskypeid(String skypeid);

    @Query(value = "SELECT * FROM uk_contacts WHERE id = ?1", nativeQuery = true)
    Optional<Contacts> findOneById(final String id);

}
