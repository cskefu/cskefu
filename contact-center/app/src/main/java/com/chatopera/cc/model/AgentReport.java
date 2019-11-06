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
package com.chatopera.cc.model;

import com.chatopera.cc.basic.MainUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "uk_webim_monitor")
@org.hibernate.annotations.Proxy(lazy = false)
public class AgentReport implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5931219598388385394L;

    private String id;
    private Date createtime = new Date();
    private int agents;           // 坐席数量，所有在线的并且就绪的坐席
    private int users;            // 服务中的用户
    private int inquene;          // 队列中的用户
    private int busy;             // 队列中忙的坐席，所有在线就绪并且繁忙的坐席（包括自己置忙或达到最大服务人数的坐席）
    private String orgi;

    private String worktype;
    private String workresult;
    private String dataid;

    private String datestr = MainUtils.simpleDateFormat.format(new Date());
    private String hourstr = new SimpleDateFormat("HH").format(new Date());
    private String datehourstr = new SimpleDateFormat("yyyy-MM-dd HH").format(new Date());

    public String getOrgi() {
        return orgi;
    }

    public void setOrgi(String orgi) {
        this.orgi = orgi;
    }

    private String type = "status";    //坐席状态

    public int getAgents() {
        return agents;
    }

    public void setAgents(int agents) {
        this.agents = agents;
    }

    public int getUsers() {
        return users;
    }

    public void setUsers(int users) {
        this.users = users;
    }

    public int getInquene() {
        return inquene;
    }

    public void setInquene(int inquene) {
        this.inquene = inquene;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getBusy() {
        return busy;
    }

    public void setBusy(int busy) {
        this.busy = busy;
    }

    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getDatestr() {
        return datestr;
    }

    public void setDatestr(String datestr) {
        this.datestr = datestr;
    }

    public String getHourstr() {
        return hourstr;
    }

    public void setHourstr(String hourstr) {
        this.hourstr = hourstr;
    }

    public String getDatehourstr() {
        return datehourstr;
    }

    public void setDatehourstr(String datehourstr) {
        this.datehourstr = datehourstr;
    }

    public String getWorktype() {
        return worktype;
    }

    public void setWorktype(String worktype) {
        this.worktype = worktype;
    }

    public String getWorkresult() {
        return workresult;
    }

    public void setWorkresult(String workresult) {
        this.workresult = workresult;
    }

    public String getDataid() {
        return dataid;
    }

    public void setDataid(String dataid) {
        this.dataid = dataid;
    }
}
