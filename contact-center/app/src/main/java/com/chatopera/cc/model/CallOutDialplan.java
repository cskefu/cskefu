/*
 * Copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "cs_callout_dialplan")
@org.hibernate.annotations.Proxy(lazy = false)
public class CallOutDialplan {

    private String id;
    private String name;

    private SNSAccount voicechannel;
    private Organ organ;
    private User creater;

    private boolean isrecord;
    private Date createtime= new Date(); // 创建时间
    private Date updatetime= new Date(); // 更新时间
    private String orgi;
    private boolean isarchive;
    private String status;
    private int targetnum;
    private int executed;
    private Date executefirsttime;
    private Date executelasttime;
    private int maxconcurrence;
    private float concurrenceratio;
    private int curconcurrence; // 当前并发

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


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsrecord() {
        return isrecord;
    }

    public void setIsrecord(boolean isrecord) {
        this.isrecord = isrecord;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public String getOrgi() {
        return orgi;
    }

    public void setOrgi(String orgi) {
        this.orgi = orgi;
    }

    public boolean isIsarchive() {
        return isarchive;
    }

    public void setIsarchive(boolean isarchive) {
        this.isarchive = isarchive;
    }

    public int getExecuted() {
        return executed;
    }

    public void setExecuted(int executed) {
        this.executed = executed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getExecutefirsttime() {
        return executefirsttime;
    }

    public void setExecutefirsttime(Date executefirsttime) {
        this.executefirsttime = executefirsttime;
    }

    public Date getExecutelasttime() {
        return executelasttime;
    }

    public void setExecutelasttime(Date executelasttime) {
        this.executelasttime = executelasttime;
    }

    public int getMaxconcurrence() {
        return maxconcurrence;
    }

    public void setMaxconcurrence(int maxconcurrence) {
        this.maxconcurrence = maxconcurrence;
    }

    public float getConcurrenceratio() {
        return concurrenceratio;
    }

    public void setConcurrenceratio(float concurrenceratio) {
        this.concurrenceratio = concurrenceratio;
    }

    public int getTargetnum() {
        return targetnum;
    }

    public void setTargetnum(int targetnum) {
        this.targetnum = targetnum;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="voicechannel")
    @NotFound(action= NotFoundAction.IGNORE)
    public SNSAccount getVoicechannel() {
        return voicechannel;
    }

    public void setVoicechannel(SNSAccount voicechannel) {
        this.voicechannel = voicechannel;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="organ")
    @NotFound(action= NotFoundAction.IGNORE)
    public Organ getOrgan() {
        return organ;
    }

    public void setOrgan(Organ organ) {
        this.organ = organ;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="creater")
    @NotFound(action= NotFoundAction.IGNORE)
    public User getCreater() {
        return creater;
    }

    public void setCreater(User creater) {
        this.creater = creater;
    }
    

    public int getCurconcurrence() {
        return curconcurrence;
    }

    public void setCurconcurrence(int curconcurrence) {
        this.curconcurrence = curconcurrence;
    }
}
