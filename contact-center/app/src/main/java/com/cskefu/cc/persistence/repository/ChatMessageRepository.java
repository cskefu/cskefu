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

import com.cskefu.cc.socketio.message.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, String> {
    List<ChatMessage> findByUsessionAndOrgi(String usession, String orgi);

    ChatMessage findById(String id);

    Page<ChatMessage> findByUsessionAndOrgi(String usession, String orgi, Pageable page);

//    public abstract Page<ChatMessage> findByUseridAndOrgi(String usession, String orgi, Pageable page);
//

    @Query(value = "select u from ChatMessage u where u.usession = ?1 and u.message like %?2%")
    Page<ChatMessage> findByUsessionAndMessageLike(String usession, String message, Pageable page);
//
//
//    @Query(value = "select u from ChatMessage u where u.usession = ?1 and  u.createtime > ?2 order by createtime")
//
//    Page<ChatMessage> findByUsessionAndGreaterThanCreatetime(String usession,String createtime);
//

    @Query(value = "select *  from (select * from uk_chat_message where  usession = :usession and createtime <= :createtime order by createtime desc limit 0,10 )t3" +
            " UNION " +
            "select *  from (select * from uk_chat_message where  usession = :usession and  createtime > :createtime order by createtime  limit 0,9999)t4 ORDER BY createtime", nativeQuery = true)
    List<ChatMessage> findByCreatetime(@Param("usession") String usession, @Param("createtime") Date createtime);

    @Query(value = "select u from ChatMessage u where u.usession = ?1 and u.message like %?2% and u.islabel = true")
    Page<ChatMessage> findByislabel(String usession, String message, Pageable page);


    @Query(value = "select * from(select * from uk_chat_message where  usession = ?1 order by createtime desc limit ?2,20 ) c order by createtime asc", nativeQuery = true)
    List<ChatMessage> findByusession(String usession, Integer current);

//    @Query(value = "select count(*) from uk_chat_message where  usession = :usession and createtime <= :createtime order by createtime", nativeQuery = true)

    int countByUsessionAndCreatetimeGreaterThanEqual(String usession, Date createtime);

    Page<ChatMessage> findByUsessionAndCreatetimeGreaterThanEqual(String userid, String createtime, Pageable page);

    Page<ChatMessage> findByUseridAndOrgi(String userid, String orgi, Pageable page);

    List<ChatMessage> findByContextidAndOrgi(String contextid, String orgi);

    Page<ChatMessage> findByContextidAndOrgi(String contextid, String orgi, Pageable page);

    Page<ChatMessage> findByContextidAndOrgiAndCreatetimeLessThan(String contextid, String orgi, Date createtime, Pageable page);

    Page<ChatMessage> findByChatypeAndOrgi(String chatype, String orgi, Pageable page);

    Page<ChatMessage> findByAgentserviceidAndOrgi(String agentserviceid, String orgi, Pageable page);

    Page<ChatMessage> findByContextidAndUseridAndOrgi(String contextid, String userid, String orgi, Pageable page);

    Page<ChatMessage> findByContextidAndUseridAndOrgiAndCreatetimeLessThan(String contextid, String userid, String orgi, Date createtime, Pageable page);
}
