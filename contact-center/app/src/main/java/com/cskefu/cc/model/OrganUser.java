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

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.util.Date;

/**
 * @Author Hai Liang Wang
 * 2019-10-10
 * 部门与用户关联表
 * 支持一个用户存在于多个部门中
 * IdClass: https://www.objectdb.com/java/jpa/entity/id#Entity_Identification, https://stackoverflow.com/questions/19813372/using-id-for-multiple-fields-in-same-class
 */
@Entity
@Table(name = "cs_organ_user")
@org.hibernate.annotations.Proxy(lazy = false)
@IdClass(OrganUserId.class)
public class OrganUser implements java.io.Serializable {

    @Id
    private String userid;     // 用户标识
    @Id
    private String organ;      // 部门标识
    private Date createtime;   // 创建时间
    private String creator;    // 创建人
    private Date updatetime;   // 更新时间

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

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

}
