/*
 * Copyright (C) 2019 Chatopera Inc, All rights reserved.
 * <https://www.chatopera.com>
 * This software and related documentation are provided under a license agreement containing
 * restrictions on use and disclosure and are protected by intellectual property laws.
 * Except as expressly permitted in your license agreement or allowed by law, you may not use,
 * copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform,
 * publish, or display any part, in any form, or by any means. Reverse engineering, disassembly,
 * or decompilation of this software, unless required by law for interoperability, is prohibited.
 */


package com.chatopera.cc.model.keys;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * 联合主键
 * https://blog.csdn.net/u013628152/article/details/43566961
 * IdClass: https://www.objectdb.com/java/jpa/entity/id#Entity_Identification, https://stackoverflow.com/questions/19813372/using-id-for-multiple-fields-in-same-class
 */
public class OrganUserId implements Serializable {
    String userid;
    String organ;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof OrganUserId) {
            OrganUserId id = (OrganUserId) o;
            if (StringUtils.equals(userid, id.getUserid()) && StringUtils.equals(organ, id.getOrgan())) {
                return true;
            }
        }
        return false;
    }

    /**
     * hashCode作用详解
     * https://www.cnblogs.com/Qian123/p/5703507.html
     * @return
     */
    @Override
    public int hashCode() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.organ);
        sb.append(this.userid);
        return sb.hashCode();
    }

}
