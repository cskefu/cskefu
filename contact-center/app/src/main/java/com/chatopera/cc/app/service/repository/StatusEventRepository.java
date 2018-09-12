/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.app.service.repository;

import com.chatopera.cc.app.model.StatusEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface StatusEventRepository extends JpaRepository<StatusEvent, String> {

    public StatusEvent findById(String id);

    public StatusEvent findByIdOrBridgeid(String id, String bridgeid);

    public Page<StatusEvent> findByAni(String ani, Pageable page);

    public Page<StatusEvent> findByNameid(String nameid, Pageable page);

    public Page<StatusEvent> findByDataid(String dataid, Pageable page);

    public Page<StatusEvent> findByOrgi(String orgi, Pageable page);

    public Page<StatusEvent> findByServicestatusAndOrgi(String servicestatus, String orgi, Pageable page);

    public Page<StatusEvent> findByMisscallAndOrgi(boolean misscall, String orgi, Pageable page);

    public Page<StatusEvent> findByRecordAndOrgi(boolean record, String orgi, Pageable page);

    public Page<StatusEvent> findByCalledAndOrgi(String voicemail, String orgi, Pageable page);

    public Page<StatusEvent> findAll(Specification<StatusEvent> spec, Pageable pageable);  //分页按条件查询


    /**
     * 坐席报表
     * @param channel
     * @param fromdate
     * @param enddate
     * @param organ
     * @param orgi
     * @return
     */
    @Query(value =
            "select " +
                    "  agent, " +
                    "  direction, " +
                    "  count(IF(DIALPLAN is not null, 1, null)) dialplan, " +
                    "  count(*) total, " +
                    "  sum(duration) seconds, " +
                    "  count(IF(DURATION = 0, 1, null)) fails, " +
                    "  count(IF(DURATION >= 60, 1, null)) gt60," +
                    "  max(duration) maxduration, " +
                    "  avg(duration) avgduration, " +
                    "  agentname " +
                    "from uk_callcenter_event " +
                    "where " +
                    "  status = '已挂机' " +
                    "  and datestr >= ?2" +
                    "  and datestr < ?3" +
                    "  and voicechannel = ?1" +
                    "  and (?4 is null or organid = ?4) " +
                    "  and orgi = ?5" +
                    "  and agent is not null " +
                    "group by" +
                    "  agent," +
                    "  direction", nativeQuery = true)
    public List<Object[]>
    queryCalloutHangupAuditGroupByAgentAndDirection(String channel,
                                                                          String fromdate,
                                                                          String enddate,
                                                                          String organ,
                                                                          String orgi);

    /**
     * 外呼计划通话记录接通记录查询
     *
     * @param fromdate
     * @param enddate
     * @param organid
     * @param agentid
     * @param called
     * @param page
     * @return
     */
    @Query(value = "select s from StatusEvent s where (:fromdate is null or s.createtime >= :fromdate) " +
            "and (:enddate is null or s.createtime < :enddate) " +
            "and (:organid is null or s.organid = :organid) " +
            "and (:agent is null or s.agent = :agent) " +
            "and (:called is null or s.called = :called) " +
            "and (:dialplan is null or s.dialplan = :dialplan) " +
            "and s.direction = :direction " +
            "and s.status = :status " +
            "and s.duration > 0 ")
    public Page<StatusEvent> queryCalloutDialplanSuccRecords(@Param("fromdate") Date fromdate,
                                                 @Param("enddate") Date enddate,
                                                 @Param("organid") String organid,
                                                 @Param("agent") String agentid,
                                                 @Param("called") String called,
                                                 @Param("direction") String direction,
                                                 @Param("status") String status,
                                                 @Param("dialplan") String dialplan,
                                                 Pageable page);

    @Query(value = "select s " +
            "from StatusEvent s " +
            "where " +
            "  s.agent = :agent and " +
            "  s.siptrunk = :siptrunk and " +
            "  s.status = :status " +
            "order by s.createtime DESC")
    public StatusEvent findByAgentAndSiptrunkAndStatus(@Param("agent") String agent, @Param("siptrunk") String siptrunk, @Param("status") String status);


    /**
     * 外呼日报
     * @param datestr
     * @param channel
     * @param direction
     * @return
     */
    @Query(value = "select dialplan, " +
            "datestr, " +
            "count(*) as total, " +
            "count(case duration when 0 then 1 else null end) fails, " +
            "sum(duration) as seconds " +
            "from uk_callcenter_event " +
            "where " +
            "DIRECTION = ?3 " +
            "and status = '已挂机' " +
            "and datestr = ?1 " +
            "and voicechannel = ?2 " +
            "group by dialplan", nativeQuery = true)
    public List<Object[]> queryCallOutHangupAggsGroupByDialplanByDatestrAndChannelAndDirection(String datestr,
                                                                                               String channel,
                                                                                               String direction);

    public int countByAgent(String agent);

    public int countByAniOrCalled(String ani, String called);

    public int countByAni(String ani);

    public int countByCalled(String called);

}
