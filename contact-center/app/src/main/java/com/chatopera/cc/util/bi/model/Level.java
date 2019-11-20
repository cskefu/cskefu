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

package com.chatopera.cc.util.bi.model;

import com.chatopera.cc.basic.MainUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Level implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4752204091160546556L;
	private String name;
	private String formatName ;			//定义维度钻取列的时候使用
	private String nameValue ;
	private Level parent;
	private String rename ;
	private String code ;
	private int depth = 0;
	private Object value;
	private String leveltype; // row , col
	private int colspan = 0;
	private int rowspan = 0;
	private String dimname ;
	private List<Level> childeren;
	private List<List<Level>> title;
	private boolean total = false;		//是否是汇总行
	private boolean first ;		//汇总行位置， 首个/末个
	private int index ;
	private String level ;		//Uni Name
	private String url ;		//定义钻取使用
	private String target ;		//定义钻取使用
	private String description; //标题描述
	
	private String model	;	//区分 数据来源用处 ， 事件设计器里用到的，别处暂时未用到 ， 不序列化
	
	private List<ValueData> valueData ;
	//private List<String> firstTitle ;

	private List<FirstTitle> firstTitle;
	public void init() {
		title = new ArrayList<List<Level>>();

		title.add(new ArrayList<Level>());
		for (Level level : childeren) {
			title.get(0).add(level);
		}
		for (int i = 0; i < depth; i++) {
			List<Level> levelList = title.get(i);
			List<Level> next = new ArrayList<Level>();
			for (Level lv : levelList) {
				if (lv.getChilderen() != null) {
					for (Level tp : lv.getChilderen()) {
						next.add(tp);
					}
				}
			}
			title.add(next);
		}
		if (leveltype != null && leveltype.equals("row")) {
			for(List<Level> tl : title){
				for(int inx =0 ; inx< tl.size() ; inx++){
					Level lv = tl.get(inx) ;
					if(lv!=null){
						for(int i=1 ;lv!=null && i<lv.getRowspan() ; i++){
							tl.add(inx+1, null) ;
						}
					}
				}
			}
		}
	}
	public Level(String name ,String nameValue, String leveltype , int rowspan , int colspan , List<ValueData> valueData ,boolean total ,boolean first){
		this.name = name ;
		this.leveltype = leveltype ;
		this.rowspan = rowspan ;
		this.colspan = colspan ;
		this.valueData = valueData ;
		this.total = total ;
		this.first = first ;
		this.nameValue = nameValue ;
	}
	public Level(String name ,String nameValue, String leveltype , int rowspan , int colspan , List<ValueData> valueData ,boolean total ,boolean first , int depth){
		this.name = name ;
		this.leveltype = leveltype ;
		this.rowspan = rowspan ;
		this.colspan = colspan ;
		this.valueData = valueData ;
		this.total = total ;
		this.first = first ;
		this.nameValue = nameValue ;
		this.depth = depth ;
	}
	public Level(String name, String leveltype, Level parent, int depth) {
		this(name , leveltype , parent, depth , null) ;
	}
	
	public Level(String name, String leveltype, Level parent, int depth , int index) {
		this(name , leveltype , parent, depth , null) ;
		this.index = index ;
	}
	public Level(String name, String leveltype, Level parent, int depth , String code) {
		this.name = name;
		this.nameValue = name ;
		this.leveltype = leveltype;
		this.parent = parent;
		this.depth = depth;
		this.code = code ;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public int getColspan() {
		return colspan;
	}

	public void setColspan(int colspan) {
		this.colspan = colspan;
	}

	public int getRowspan() {
		return rowspan;
	}

	public void setRowspan(int rowspan) {
		this.rowspan = rowspan;
	}

	public List<Level> getChilderen() {
		return childeren;
	}

	public void setChilderen(List<Level> childeren) {
		this.childeren = childeren;
	}

	public String getLeveltype() {
		return leveltype;
	}

	public void setLeveltype(String leveltype) {
		this.leveltype = leveltype;
	}

	public String toString() {
		return name;
	}

	public Level getParent() {
		return parent;
	}

	public void setParent(Level parent) {
		this.parent = parent;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public List<List<Level>> getTitle() {
		return title;
	}

	public void setTitle(List<List<Level>> title) {
		this.title = title;
	}
	public List<?> getChartTitle(){
		return this.getTitle()!=null && this.getTitle().size()>1 ? this.getTitle().get(this.getTitle().size()-2) : this.getTitle().size()>0 ?  this.getTitle().get(0): null ;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

//	public List<String> getFirstTitle() {
//		return firstTitle;
//	}
//
//	public void setFirstTitle(List<String> firstTitle) {
//		this.firstTitle = firstTitle;
//	}
	
	public String getNameValue() {
		return nameValue;
	}

	public List<FirstTitle> getFirstTitle() {
		return firstTitle;
	}

	public void setFirstTitle(List<FirstTitle> firstTitle) {
		this.firstTitle = firstTitle;
	}

	public void setNameValue(String nameValue) {
		this.nameValue = nameValue;
	}

	public boolean isTotal() {
		return total;
	}

	public void setTotal(boolean total) {
		this.total = total;
	}

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}

	public String getDimname() {
		return dimname;
	}

	public void setDimname(String dimname) {
		this.dimname = dimname;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getLevel() {
		return level;
	}
	/**
	 * 获取Level的 路径， 对 立方体有效， 对 SQL或R3模型无效
	 * @return
	 */
	public String getCubeLevel(String dimname){
		Level temp = this.parent ;
		StringBuffer strb = new StringBuffer();
		while(temp!=null && !temp.getName().equals("root")){
			if(strb.length()>0){
				strb.insert(0,".");
			}
			strb.insert(0,"]").insert(0 , temp.getName()).insert(0,"[") ;
			temp = temp.parent ;
		}
		if(!StringUtils.isBlank(dimname)&&strb.length()>0){
			strb.insert(0, "].").insert(0, dimname).insert(0, "[");
		}
		
		return strb.toString();
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public List<ValueData> getValueData() {
		return valueData;
	}

	public void setValueData(List<ValueData> valueData) {
		this.valueData = valueData;
	}
	public String getRename() {
		return rename;
	}
	public void setRename(String rename) {
		this.rename = rename;
	}
	/**
	 * 页面控制CSS使用 ，在 table.html
	 * 
	 * @return
	 */
	public String getDataid(){
		return MainUtils.md5(this.name!=null ? this.name : "") ;
	}
	public String getFormatName() {
		return formatName!=null ? formatName : this.name;
	}
	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		Level parent = this.parent ;
		if(parent!=null){
			parent.setModel(model) ;
			while((parent = parent.getParent())!=null){
				parent.setModel(model) ;
			}
		}
		this.model = model;
	}
	
}
