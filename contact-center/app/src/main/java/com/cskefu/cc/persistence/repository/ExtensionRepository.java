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

import com.cskefu.cc.model.Extension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExtensionRepository extends JpaRepository<Extension, String> {

    List<Extension> findByHostid(String hostid);

    List<Extension> findByExtension(String extension);

    List<Extension> findByHostidAndExtype(String hostid, String extype);

    List<Extension> findByHostidAndExtypeAndAgentnoIsNull(String hostid, String extype);

    List<Extension> findByExtype(String type);

    @Query(nativeQuery = true, value = "SELECT * FROM uk_callcenter_extention WHERE extention = ?1 AND hostid = ?2 LIMIT 1")
    Optional<Extension> findOneByExtensionAndHostid(final String extension, final String hostid);

    int countByHostid(final String hostid);

    @Query(nativeQuery = true, value = "SELECT * FROM uk_callcenter_extention WHERE agentno = ?1 LIMIT 1")
    Optional<Extension> findByAgentno(final String agentno);
}
