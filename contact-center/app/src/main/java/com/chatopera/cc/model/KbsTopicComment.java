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

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;

@Data
@Document(indexName = "kbs_topiccomment", type = "kbs_topiccomment")
public class KbsTopicComment implements UKAgg {

    private static final long serialVersionUID = -4911955236794918875L;
    private String id = MainUtils.getUUID();
    private String username;
    private String creater;

    private Date createtime = new Date();

    @Field(index = false, type = FieldType.Text)
    private String dataid;

    private String content;    //评论内容

    private Date updatetime = new Date();

    private boolean optimal;    //是否最佳答案

    private int up;            //点赞数量
    private int comments;        //回复数量

    private boolean admin;

    private String cate;

    private String optype;
    private String ipcode;
    private String country;
    private String province;
    private String city;
    private String isp;
    private String region;

    private int rowcount;
    private String key;

    private Topic topic;

    private User user;

    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    public String getId() {
        return id;
    }

    @Transient
    public Topic getTopic() {
        return topic;
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
