package com.cskefu.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity<T> implements Serializable {
    private static final long serialVersionUID = -6751846719593132836L;
    private T id;
    private Date createTime;
    private Date updateTime;
    private String creator;
    private String updator;
    private Boolean deleted;
    private Integer version;
}
