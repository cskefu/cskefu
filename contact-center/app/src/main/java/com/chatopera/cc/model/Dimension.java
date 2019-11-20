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
import java.util.List;
@Entity
@Table(name = "uk_dimension")
@org.hibernate.annotations.Proxy(lazy = false)
public class Dimension implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private String id ;
	private String name ;	//维度名称	
	private String code ;
	private String type ;	//类型：TimeDimension
	private String modeltype ;
	private String cubeid;
	private String orgi ;
	private String allmembername ;
	private String postop ;			//改变用处， 修改为 是否权限控制字段
	private Dimension dim ;
	private String posleft ;
	private int sortindex ;
	private String parameters;
	private String attribue;
	private String fkfield ;
	private String fktable ;
	private String fktableid ;
	private List<CubeLevel> cubeLevel ;
	private Date createtime = new Date();
	private String creater;
	/**
	 * @return the id
	 */
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCubeid() {
		return cubeid;
	}
	public void setCubeid(String cubeid) {
		this.cubeid = cubeid;
	}
	public int getSortindex() {
		return sortindex;
	}
	public void setSortindex(int sortindex) {
		this.sortindex = sortindex;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	public String getAttribue() {
		return attribue;
	}
	public void setAttribue(String attribue) {
		this.attribue = attribue;
	}
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "dimid",insertable=false,updatable=false)
	@OrderBy("sortindex")
	public List<CubeLevel> getCubeLevel() {
		return cubeLevel;
	}
	public void setCubeLevel(List<CubeLevel> cubeLevel) {
		this.cubeLevel = cubeLevel;
	}
	public String getPostop() {
		return postop;
	}
	public void setPostop(String postop) {
		this.postop = postop;
	}
	public String getPosleft() {
		return posleft;
	}
	public void setPosleft(String posleft) {
		this.posleft = posleft;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getModeltype() {
		return modeltype;
	}
	public void setModeltype(String modeltype) {
		this.modeltype = modeltype;
	}
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="dim")
	public Dimension getDim() {
		return dim;
	}
	public void setDim(Dimension dim) {
		this.dim = dim;
	}
	public String getAllmembername() {
		return allmembername;
	}
	public void setAllmembername(String allmembername) {
		this.allmembername = allmembername;
	}
	public String getFkfield() {
		return fkfield;
	}
	public void setFkfield(String fkfield) {
		this.fkfield = fkfield;
	}
	public String getFktable() {
		return fktable;
	}
	public void setFktable(String fktable) {
		this.fktable = fktable;
	}
	public String getFktableid() {
		return fktableid;
	}
	public void setFktableid(String fktableid) {
		this.fktableid = fktableid;
	}
	public String getCreater() {
		return creater;
	}
	public void setCreater(String creater) {
		this.creater = creater;
	}
}
