/* 
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2019-2022 Chatopera Inc, All rights reserved. 
 * <https://www.chatopera.com>
 */


package com.cskefu.cc.model;

import org.apache.commons.lang3.StringUtils;

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
     *
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
