package com.cskefu.cc.model;

import jakarta.persistence.*;

import java.util.Date;

/**
 * 存储元数据
 */
@Entity
@Table(name = "cs_metakv")
@org.hibernate.annotations.Proxy(lazy = false)
public class MetaKv implements java.io.Serializable {

    @Id
    private String metakey;

    private String metavalue;

    private String datatype;

    private String comment;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatetime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createtime;

    public String getMetakey() {
        return metakey;
    }

    public void setMetakey(String metakey) {
        this.metakey = metakey;
    }

    public String getMetavalue() {
        return metavalue;
    }

    public void setMetavalue(String metavalue) {
        this.metavalue = metavalue;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
}
