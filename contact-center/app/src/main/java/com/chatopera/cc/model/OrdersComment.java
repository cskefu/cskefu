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
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.*;
import java.util.Date;

@Data
@Document(indexName = "orderscomment", type = "orderscomment")
@Entity
@Table(name = "uk_orderscomment")
@org.hibernate.annotations.Proxy(lazy = false)
public class OrdersComment implements UKAgg {

    private static final long serialVersionUID = -4911955236794918875L;
    private String id = MainUtils.getUUID();
    private String username;
    private String creater;
    private Date createtime = new Date();
    @Field(index = false, type = FieldType.Text)
    private String dataid;
    private String content;    //评论内容
    private Date updatetime = new Date();
    private boolean optimal;    //变更用处，流程回复
    private boolean prirep;    //变更用处， 是否私密回复
    private int up;            //点赞数量
    private int comments;        //回复数量
    private boolean admin;        //变更用处 ， 是否审批流程
    private boolean datastatus;    //数据状态，是否已删除
    private String orgi;
    private String cate;
    private String optype;
    private String approval;    //审批结果
    private String retback;    //退回位置 ， 退回到 创建人
    private String accdept;    //转办 部门
    private String accuser;    //转办人
    private String ipcode;
    private String country;
    private String province;
    private String city;
    private String isp;
    private String region;
    private int rowcount;
    private String key;
    private User user;

    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "assigned")
    public String getId() {
        return id;
    }

    @Transient
    public User getUser() {
        return user;
    }

    @Transient
    public int getRowcount() {
        return rowcount;
    }

    @Transient
    public String getKey() {
        return key;
    }
}
