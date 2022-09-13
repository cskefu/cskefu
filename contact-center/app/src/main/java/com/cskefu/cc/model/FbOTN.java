/*
 * Copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
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

package com.cskefu.cc.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "cs_fb_otn")
@org.hibernate.annotations.Proxy(lazy = false)
public class FbOTN implements Serializable {

    private String id;
    private String name;
    private String pageId;
    private String preSubMessage;
    private String subMessage;
    private String successMessage;
    private String otnMessage;
    private String status;
    private Date createtime;
    private Date updatetime;
    private Date sendtime;
    private Integer melinkNum;
    private Integer subNum;

    private FbMessenger fbMessenger;

    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "assigned")
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

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }


    public String getPreSubMessage() {
        return preSubMessage;
    }

    public void setPreSubMessage(String preSubMessage) {
        this.preSubMessage = preSubMessage;
    }


    public String getSubMessage() {
        return subMessage;
    }

    public void setSubMessage(String subMessage) {
        this.subMessage = subMessage;
    }


    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }


    public String getOtnMessage() {
        return otnMessage;
    }

    public void setOtnMessage(String otnMessage) {
        this.otnMessage = otnMessage;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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


    public Date getSendtime() {
        return sendtime;
    }

    public void setSendtime(Date sendtime) {
        this.sendtime = sendtime;
    }

    public Integer getSubNum() {
        return subNum;
    }

    public void setSubNum(Integer subNum) {
        this.subNum = subNum;
    }

    public Integer getMelinkNum() {
        return melinkNum;
    }

    public void setMelinkNum(Integer melinkNum) {
        this.melinkNum = melinkNum;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pageId", referencedColumnName = "pageId", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    public FbMessenger getFbMessenger() {
        return fbMessenger;
    }

    public void setFbMessenger(FbMessenger fbMessenger) {
        this.fbMessenger = fbMessenger;
    }
}
