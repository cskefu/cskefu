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

public class ChartProperties implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	private String chartype ;
	//private String id ;
	private boolean showtitle = false;
	private String title;
	
	private String xaxis ;	
	private String xaxis_title = "key";
	private String xaxis_label ;//x label format
	
	private String format;
	
	private String yaxis ;
	private String yaxis_title ="value";
	private String yaxis_label ;
	
	private boolean legen = false;//显示图例
	private String legenalign = "bottom";
	

	/*private boolean openPageSize = false;
	private int pageSize = 50;*/
	
	private boolean credits ;
	private boolean exporting=false;
	private boolean dataview = false;//显示数值
	private boolean theme = false;
	private String themename;
	public String getChartype() {
		return chartype;
	}
	public void setChartype(String chartype) {
		this.chartype = chartype;
	}
	/*public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}*/
	public boolean isShowtitle() {
		return showtitle;
	}
	public void setShowtitle(boolean showtitle) {
		this.showtitle = showtitle;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getXaxis() {
		return xaxis;
	}
	public void setXaxis(String xaxis) {
		this.xaxis = xaxis;
	}
	public String getXaxis_title() {
		return xaxis_title;
	}
	public void setXaxis_title(String xaxis_title) {
		this.xaxis_title = xaxis_title;
	}
	public String getXaxis_label() {
		return xaxis_label;
	}
	public void setXaxis_label(String xaxis_label) {
		this.xaxis_label = xaxis_label;
	}
	public String getYaxis() {
		return yaxis;
	}
	public void setYaxis(String yaxis) {
		this.yaxis = yaxis;
	}
	public String getYaxis_title() {
		return yaxis_title;
	}
	public void setYaxis_title(String yaxis_title) {
		this.yaxis_title = yaxis_title;
	}
	public String getYaxis_label() {
		return yaxis_label;
	}
	public void setYaxis_label(String yaxis_label) {
		this.yaxis_label = yaxis_label;
	}
	public boolean isLegen() {
		return legen;
	}
	public void setLegen(boolean legen) {
		this.legen = legen;
	}
	public String getLegenalign() {
		return legenalign;
	}
	public void setLegenalign(String legenalign) {
		this.legenalign = legenalign;
	}
	public boolean isCredits() {
		return credits;
	}
	public void setCredits(boolean credits) {
		this.credits = credits;
	}
	public boolean isExporting() {
		return exporting;
	}
	public void setExporting(boolean exporting) {
		this.exporting = exporting;
	}
	public boolean isDataview() {
		return dataview;
	}
	public void setDataview(boolean dataview) {
		this.dataview = dataview;
	}
	public boolean isTheme() {
		return theme;
	}
	public void setTheme(boolean theme) {
		this.theme = theme;
	}
	public String getThemename() {
		return themename;
	}
	public void setThemename(String themename) {
		this.themename = themename;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	
	
}
