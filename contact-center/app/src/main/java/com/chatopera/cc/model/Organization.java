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

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;


/**
 * @author ricy Tenant.java 2010-3-17
 * 企业信息
 */
@Entity
@Table(name = "uk_organization")
@org.hibernate.annotations.Proxy(lazy = false)
public class Organization implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String code;
    private String orgtype;                  // 机构类型
    private String orgscale;                 // 规模
    private String orgindustry;              // 行业
    private Date createtime = new Date();    // 创建时间
    private String logo;
    private String memo;


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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOrgtype() {
        return orgtype;
    }

    public void setOrgtype(String orgtype) {
        this.orgtype = orgtype;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getOrgscale() {
        return orgscale;
    }

    public void setOrgscale(String orgscale) {
        this.orgscale = orgscale;
    }

    public String getOrgindustry() {
        return orgindustry;
    }

    public void setOrgindustry(String orgindustry) {
        this.orgindustry = orgindustry;
    }
}
