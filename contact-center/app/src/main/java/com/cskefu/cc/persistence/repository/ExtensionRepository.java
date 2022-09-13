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

import com.cskefu.cc.model.Extension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExtensionRepository extends JpaRepository<Extension, String> {

    Extension findByIdAndOrgi(String id, String orgi);

    List<Extension> findByHostidAndOrgi(String hostid, String orgi);

    List<Extension> findByExtensionAndOrgi(String extension, String orgi);

    List<Extension> findByExtension(String extension);

    List<Extension> findByHostidAndExtypeAndOrgi(String hostid, String extype, String orgi);

    List<Extension> findByHostidAndExtypeAndOrgiAndAgentnoIsNull(String hostid, String extype, String orgi);

    List<Extension> findByExtypeAndOrgi(String type, String orgi);

    @Query(value = "SELECT * FROM uk_callcenter_extention WHERE extention = ?1 AND hostid = ?2 AND orgi = ?3 LIMIT 1", nativeQuery = true)
    Optional<Extension> findOneByExtensionAndHostidAndOrgi(final String extension, final String hostid, final String orgi);

    int countByHostidAndOrgi(final String hostid, final String orgi);

    @Query(value = "SELECT * FROM uk_callcenter_extention WHERE agentno = ?1 AND orgi = ?2 LIMIT 1", nativeQuery = true)
    Optional<Extension> findByAgentnoAndOrgi(final String agentno, final String orgi);
}
