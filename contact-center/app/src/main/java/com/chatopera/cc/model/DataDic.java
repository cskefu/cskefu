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


@Entity
@Table(name = "uk_datadic")
@org.hibernate.annotations.Proxy(lazy = false)
public class DataDic implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2593399616448881368L;
	private String id ;
	private String name ;
	private String code ;
	private String title ;					
	private String dstype ; 		//dashboard类型
	private String dstemplet ;		//dashboard类型对应的  展示模板
	private String parentid;
	private String type ;
	private String memo ;			//改变用处，变更为  目录序号
	private String distitle;		//分栏时 左侧 栏目的 显示标题
	private String orgi ;
	private String status ;
	private String iconclass;
	private String cssstyle;
	private String creater ;
	private String authcode ;
	private String publishedtype ;
	private Date createtime = new Date();
	private Date updatetime ;
	private String description ;
	private String tabtype ;
	private String dictype ;
	private boolean spsearch = true;	//是否支持搜索
	private boolean defaultmenu ;
	private String projectid ;
	private int sortindex = 1; 		//排序位置
	private String dataid ;			//dstype = singel 的时候，用于定义获取数据的 ID
	private String dicicon ;			//菜单图标
	private String curicon ;			//菜单选中时候 的 图标
	private String bgcolor ;			//在列表的时候显示的背景颜色，用于反显图标用的
	private String curbgcolor;			//在列表的时候显示的背景颜色，用于反显图标用的
	private String menupos ;			//菜单显示位置，左侧显示 ： 顶部显示
	
	private boolean navmenu ;			//顶部导航菜单按钮
	private boolean quickmenu ;			//左侧快捷菜单按钮


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
	
	public String getCssstyle() {
		return cssstyle;
	}
	public void setCssstyle(String cssstyle) {
		this.cssstyle = cssstyle;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public String getCreater() {
		return creater;
	}
	public void setCreater(String creater) {
		this.creater = creater;
	}
	public String getPublishedtype() {
		return publishedtype;
	}
	public void setPublishedtype(String publishedtype) {
		this.publishedtype = publishedtype;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTabtype() {
		return tabtype;
	}
	public void setTabtype(String tabtype) {
		this.tabtype = tabtype;
	}
	
	public String getDstype() {
		return dstype;
	}
	public void setDstype(String dstype) {
		this.dstype = dstype;
	}
	public String getDstemplet() {
		return dstemplet;
	}
	public void setDstemplet(String dstemplet) {
		this.dstemplet = dstemplet;
	}

	public int getSortindex() {
		return sortindex;
	}
	public void setSortindex(int sortindex) {
		this.sortindex = sortindex;
	}
	public String getDictype() {
		return dictype;
	}
	public void setDictype(String dictype) {
		this.dictype = dictype;
	}
	public String getIconclass() {
		return iconclass;
	}
	public void setIconclass(String iconclass) {
		this.iconclass = iconclass;
	}
	public String getAuthcode() {
		return authcode;
	}
	public void setAuthcode(String authcode) {
		this.authcode = authcode;
	}
	public boolean isDefaultmenu() {
		return defaultmenu;
	}
	public void setDefaultmenu(boolean defaultmenu) {
		this.defaultmenu = defaultmenu;
	}
	public String getDataid() {
		return dataid;
	}
	public void setDataid(String dataid) {
		this.dataid = dataid;
	}

	public String getDicicon() {
		return dicicon;
	}
	public void setDicicon(String dicicon) {
		this.dicicon = dicicon;
	}
	public String getCuricon() {
		return curicon;
	}
	public void setCuricon(String curicon) {
		this.curicon = curicon;
	}
	public String getBgcolor() {
		return bgcolor;
	}
	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}
	public String getCurbgcolor() {
		return curbgcolor;
	}
	public void setCurbgcolor(String curbgcolor) {
		this.curbgcolor = curbgcolor;
	}
	public String getMenupos() {
		return menupos;
	}
	public void setMenupos(String menupos) {
		this.menupos = menupos;
	}
	public String getDistitle() {
		return distitle;
	}
	public void setDistitle(String distitle) {
		this.distitle = distitle;
	}
	public boolean isNavmenu() {
		return navmenu;
	}
	public void setNavmenu(boolean navmenu) {
		this.navmenu = navmenu;
	}
	public boolean isQuickmenu() {
		return quickmenu;
	}
	public void setQuickmenu(boolean quickmenu) {
		this.quickmenu = quickmenu;
	}

	public boolean isSpsearch() {
		return spsearch;
	}

	public void setSpsearch(boolean spsearch) {
		this.spsearch = spsearch;
	}

	public String getProjectid() {
		return projectid;
	}

	public void setProjectid(String projectid) {
		this.projectid = projectid;
	}
}
