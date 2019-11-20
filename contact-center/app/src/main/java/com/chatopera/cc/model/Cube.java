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
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Entity
@Table(name = "uk_cube")
@org.hibernate.annotations.Proxy(lazy = false)
public class Cube implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id ;
	private String name ;	
	private String code ;
	private String modeltype ;	//模型类型， 虚拟 立方体：立方体
	private String dstype ;	//db , r3
	private String db;
	private Date createtime ;
	private String mposleft;	//指标位置
	private String mpostop ;	//指标位置
	private String typeid ;	
	private String orgi ;
	private String createdata ;
	private String dataid ;
	private String dataflag ;			//修改字段用途，改为 数据版本
	private int startindex ;
	private Date startdate ;
	private String creater;
	private Date updatetime;
	private String cubefile ;			//修改字段用途，改为 授权信息
	private String sql ;				//CUBE的临时SQL信息
	
	private List<CubeMetadata> metadata = new ArrayList<CubeMetadata>();
	private List<Dimension> dimension = new ArrayList<Dimension>();
	private List<CubeMeasure> measure = new ArrayList<CubeMeasure>();
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
	public String getDb() {
		return db;
	}
	public void setDb(String db) {
		this.db = db;
	}
	//	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
//	@JoinColumn(name = "cubeid")
//	@OrderBy("sortindex")
	@Transient
	public List<Dimension> getDimension() {
		return dimension;
	}
	public void setDimension(List<Dimension> dimension) {
		this.dimension = dimension;
	}
	public String getMposleft() {
		return mposleft;
	}
	public void setMposleft(String mposleft) {
		this.mposleft = mposleft;
	}
	public String getMpostop() {
		return mpostop;
	}
	public void setMpostop(String mpostop) {
		this.mpostop = mpostop;
	}
//	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
//	@JoinColumn(name = "cubeid")
//	@OrderBy("sortindex")
	@Transient
	public List<CubeMeasure> getMeasure() {
		return measure;
	}
	public void setMeasure(List<CubeMeasure> measure) {
		this.measure = measure;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
//	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
//	@JoinColumn(name = "cube")
	@Transient
	public List<CubeMetadata> getMetadata() {
		return metadata;
	}
	public void setMetadata(List<CubeMetadata> metadata) {
		this.metadata = metadata;
	}
	public String getTypeid() {
		return typeid;
	}
	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDstype() {
		return dstype;
	}
	public void setDstype(String dstype) {
		this.dstype = dstype;
	}
	public String getModeltype() {
		return modeltype;
	}
	public void setModeltype(String modeltype) {
		this.modeltype = modeltype;
	}
	/*@Transient
	public String getTable(){
		return "c_d_"+RivuTools.md5(this.getId());
	}*/
	public String getCreatedata() {
		return createdata;
	}
	public void setCreatedata(String createdata) {
		this.createdata = createdata;
	}
	public String getDataid() {
		return dataid;
	}
	public void setDataid(String dataid) {
		this.dataid = dataid;
	}
	public String getDataflag() {
		return dataflag;
	}
	public void setDataflag(String dataflag) {
		this.dataflag = dataflag;
	}
	public int getStartindex() {
		return startindex;
	}
	public void setStartindex(int startindex) {
		this.startindex = startindex;
	}
	public Date getStartdate() {
		return startdate;
	}
	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getCreater() {
		return creater;
	}
	public void setCreater(String creater) {
		this.creater = creater;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public String getCubefile() {
		return cubefile;
	}
	public void setCubefile(String cubefile) {
		this.cubefile = cubefile;
	}
	@Transient
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	@Transient
	public String getTable(){
		return "c_d_"+ MainUtils.md5(this.getId());
	}
}
