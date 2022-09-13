/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
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
package com.cskefu.cc.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "uk_columnproperties")
@org.hibernate.annotations.Proxy(lazy = false)
public class ColumnProperties implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String modelid;
	private String dataid ;
	private String dataname ; 	//如果存放的是指标，则此处为指标名称
	private String title ;
	private String colname;//列标题
	private String border;//边框
	private String width;//列宽度
	private String format;//列数据格式化
	private String decimalcount;//小数位数
	private String sepsymbol;//分隔符
	private String font;///文字大小
	private String alignment;//对齐方式
	private String fontstyle;//字体样式
	private String fontcolor;//字体颜色
	private String cur;//货币
	private String timeformat;//日期时间
	private String hyp;//超链接
	private String prefix;//前缀
	private String suffix;//后缀
	private String paramname ;//参数名
	private String orgi;
	private int sortindex;
	
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
	public String getModelid() {
		return modelid;
	}
	public void setModelid(String modelid) {
		this.modelid = modelid;
	}
	public String getDataid() {
		return dataid;
	}
	public void setDataid(String dataid) {
		this.dataid = dataid;
	}
	public String getDataname() {
		return dataname;
	}
	public void setDataname(String dataname) {
		this.dataname = dataname;
	}
	public String getColname() {
		return colname;
	}
	public void setColname(String colname) {
		this.colname = colname;
	}
	public String getBorder() {
		return border;
	}
	public void setBorder(String border) {
		this.border = border;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getdecimalcount() {
		return decimalcount;
	}
	public void setdecimalcount(String decimalcount) {
		this.decimalcount = decimalcount;
	}
	
	public String getSepsymbol() {
		return sepsymbol;
	}
	public void setSepsymbol(String sepsymbol) {
		this.sepsymbol = sepsymbol;
	}
	public String getFont() {
		return font;
	}
	public void setFont(String font) {
		this.font = font;
	}
	public String getAlignment() {
		return alignment;
	}
	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}
	public String getfontstyle() {
		return fontstyle;
	}
	public void setfontstyle(String fontstyle) {
		this.fontstyle = fontstyle;
	}
	public String getfontcolor() {
		return fontcolor;
	}
	public void setfontcolor(String fontcolor) {
		this.fontcolor = fontcolor;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public String getparamname() {
		return paramname;
	}
	public void setparamname(String paramname) {
		this.paramname = paramname;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	public String getCur() {
		return cur;
	}
	public void setCur(String cur) {
		this.cur = cur;
	}
	public String gettimeformat() {
		return timeformat;
	}
	public void settimeformat(String timeformat) {
		this.timeformat = timeformat;
	}
	public String getHyp() {
		return hyp;
	}
	public void setHyp(String hyp) {
		this.hyp = hyp;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getSortindex() {
		return sortindex;
	}
	public void setSortindex(int sortindex) {
		this.sortindex = sortindex;
	}
	
}
