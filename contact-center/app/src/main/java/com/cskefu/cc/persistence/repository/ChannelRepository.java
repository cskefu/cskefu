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

import com.cskefu.cc.model.Channel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, String> {

    boolean existsBySnsidAndType(String snsid, String type);

    @Query(nativeQuery = true, value = "SELECT * FROM `cs_channel` WHERE snsid = ?1 LIMIT 1")
    Optional<Channel> findBySnsid(String snsid);

    List<Channel> findByBaseURL(String baseurl);

    int countBySnsid(String snsid);

    int countByType(final String type);

    List<Channel> findByType(String type);

    Page<Channel> findByType(String paramString, Pageable page);

    @Query(nativeQuery = true, value = "select s.* from `cs_channel` s WHERE s.type = ?1 and s.`organ` in (?2) ORDER BY ?#{#pageable}",
            countQuery = "select count(1) from `cs_channel` s WHERE s.type = ?1 and s.`organ` in (?2)")
    Page<Channel> findByTypeAndOrgan(String type, Collection<String> organ, Pageable page);

    @Query(nativeQuery = true, value = "SELECT * FROM cs_channel WHERE type = ?1 AND snsid = ?2 LIMIT 1")
    Optional<Channel> findOneBySnsTypeAndSnsId(final String type, final String snsId);

    @Query(nativeQuery = true, value = "SELECT * FROM cs_channel WHERE type = ?1 LIMIT 1")
    Channel findOneByType(final String type);


}
