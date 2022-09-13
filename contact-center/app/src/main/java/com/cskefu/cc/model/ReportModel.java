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


import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.util.bi.ReportData;
import org.apache.commons.codec.binary.Base64;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "uk_reportmodel")
@org.hibernate.annotations.Proxy(lazy = false)
public class ReportModel implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id = MainUtils.getUUID();
	private String posx ;
	private String posy ;
	private String poswidth;
	private String posheight;
	private String name ;
	private String code ;
	private String reportid ;
	private String modeltype ;
	private int sortindex ;
	private String stylestr;
	private String labeltext ;
	private String cssclassname ;
	private String mposleft;	//指标位置
	private String mpostop ;	//指标位置
	private String title ;
	private boolean exchangerw = false ;	//行列转换
	private String publishedcubeid ;
	private String rowdimension ;	//逗号分隔
	private String coldimension ;	//逗号分隔
	private String measure ;		//逗号分隔
	private String dstype ;		//数据源类型cube or table
	private String dbtype;      //数据库类型：sqlserver oracle mysql hive ....
	private String orgi ;
	private String objectid ;
	private Date createtime ;
	private String filterstr;
	private String sortstr ;
	private String viewtype = "view";	//视图类型, 图表,组件, 仪表盘
	private String chartemplet ;		//图表显示数据XML文件的模板,存放显示模板的id
	private String chartype ;			//图表类型
	private String chartdatatype	;	//图表数据类型
	private String chart3d = "false";			//显示为3D图表
	private String xtitle ;				//修改用处，对图表，用于控制是否显示X轴标题 ， 对于表格，用于控制需要隐藏的参数
	private String ytitle ;				//修改用处。对图表，用于控制是否显示Y轴标题，对于表格，用于控制当前当前选中的参数的名称
	private String charttitle = "false";
	private String displayborder ;			//显示边框
	private String bordercolor = "#FFFFFF";			//边框颜色
	private String displaydesc ;	//显示颜色说明区域
	private String formdisplay ;	//表单内显示模式 ，行内（Form-Inline）：换行（form-horizontal）
	private String labelstyle ;		//表单样式
	private String formname ;		//表单名称 name
	private String defaultvalue ;	//默认值
	private String querytext ;		//查询语法
	private String tempquey ;		//保存当次查询语句
	private boolean displaytitle = false ;	//显示表头
	private boolean clearzero = false ;	//除零
	
	private String titlestr ;
	private String width = "100";
	private String height = "100";
	private String widthunit = "";	//宽度单位  % OR px
	private String heightunit = ""; 	//高度单位 % OR px
	private String defheight = "200px";   //默认内部元素的高度，默认为 200px
	private String defwidth = "98" ;
	
	private String neckwidth ;
	private String neckheight ;
	private String extparam ;
	private String marginright ;
	
	private String colorstr ; 
	
	
	private String start ;
	
	private String end ;
	private String rowformatstr ;
	private String colformatstr ;
	private String publishtype ;	
	
	private String editview ="view";			//编辑视图
	private boolean expandbtm = true;	//展开底部工具栏
	private boolean expandrgt = true;	//展开右侧工具栏
	private String curtab 	;	 //当前默认展开的 工具栏  ： 数据 ： 组件
	
	private String hiddencolstr;//隐藏的列

	private String eventstr ;	//事件
	private String dsmodel;	//数据源对象
	
	private String html;//自定义报表文本
	
	private String mid ;		
	private String parentid ;
	private String templetid;
	private int colspan ;
	
	private int colindex ;	//列位置
	
	private String sqldialect;
	private int pagesize = 20;
	private String isloadfulldata = "false";
	private boolean isexport;
	
	private boolean selectdata = false;
	
	
	private String exporttitle ;
	
	private int colsize = 100;//报表显示列大小
	
	private String sorttype;//排序类型
	private String sortname;//排序列名称；
	
	private List<ColumnProperties> properties = new ArrayList<ColumnProperties>();
	private List<ColumnProperties> colproperties = new ArrayList<ColumnProperties>();
	
	private List<ColumnProperties> measures = new ArrayList<ColumnProperties>();
	private List<ReportFilter> filters = new ArrayList<ReportFilter>() ;
	private List<DrillDown> drilldown = new ArrayList<DrillDown>();
	
	private ReportData reportData ;
	
	private ChartProperties chartProperties ;
	
	private String chartcontent ;
	/**
	 * @return the id
	 */
	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "assigned")	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPosx() {
		return posx;
	}
	public void setPosx(String posx) {
		this.posx = posx;
	}
	public String getPosy() {
		return posy;
	}
	public void setPosy(String posy) {
		this.posy = posy;
	}
	public String getPoswidth() {
		return poswidth;
	}
	public void setPoswidth(String poswidth) {
		this.poswidth = poswidth;
	}
	public String getPosheight() {
		return posheight;
	}
	public void setPosheight(String posheight) {
		this.posheight = posheight;
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
	public String getReportid() {
		return reportid;
	}
	public void setReportid(String reportid) {
		this.reportid = reportid;
	}
	public String getModeltype() {
		return modeltype;
	}
	public void setModeltype(String modeltype) {
		this.modeltype = modeltype;
	}
	public int getSortindex() {
		return sortindex;
	}
	public void setSortindex(int sortindex) {
		this.sortindex = sortindex;
	}
	public String getStylestr() {
		return stylestr;
	}
	public void setStylestr(String stylestr) {
		this.stylestr = stylestr;
	}
	public String getLabeltext() {
		return labeltext;
	}
	public void setLabeltext(String labeltext) {
		this.labeltext = labeltext;
	}
	public String getCssclassname() {
		return cssclassname;
	}
	public void setCssclassname(String cssclassname) {
		this.cssclassname = cssclassname;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean isExchangerw() {
		return exchangerw;
	}
	public void setExchangerw(boolean exchangerw) {
		this.exchangerw = exchangerw;
	}
	public String getPublishedcubeid() {
		return publishedcubeid;
	}
	public void setPublishedcubeid(String publishedcubeid) {
		this.publishedcubeid = publishedcubeid;
	}
	public String getRowdimension() {
		return rowdimension;
	}
	public void setRowdimension(String rowdimension) {
		this.rowdimension = rowdimension;
	}
	public String getColdimension() {
		return coldimension;
	}
	public void setColdimension(String coldimension) {
		this.coldimension = coldimension;
	}
	public String getMeasure() {
		return measure;
	}
	public void setMeasure(String measure) {
		this.measure = measure;
	}
	public String getDstype() {
		return dstype;
	}
	public void setDstype(String dstype) {
		this.dstype = dstype;
	}
	public String getDbtype() {
		return dbtype;
	}
	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	public String getObjectid() {
		return objectid;
	}
	public void setObjectid(String objectid) {
		this.objectid = objectid;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getFilterstr() {
		return filterstr;
	}
	public void setFilterstr(String filterstr) {
		this.filterstr = filterstr;
	}
	public String getSortstr() {
		return sortstr;
	}
	public void setSortstr(String sortstr) {
		this.sortstr = sortstr;
	}
	public String getViewtype() {
		return viewtype;
	}
	public void setViewtype(String viewtype) {
		this.viewtype = viewtype;
	}
	public String getChartemplet() {
		return chartemplet;
	}
	public void setChartemplet(String chartemplet) {
		this.chartemplet = chartemplet;
	}
	public String getChartype() {
		return chartype;
	}
	public void setChartype(String chartype) {
		this.chartype = chartype;
	}
	public String getChartdatatype() {
		return chartdatatype;
	}
	public void setChartdatatype(String chartdatatype) {
		this.chartdatatype = chartdatatype;
	}
	public String getChart3d() {
		return chart3d;
	}
	public void setChart3d(String chart3d) {
		this.chart3d = chart3d;
	}
	public String getXtitle() {
		return xtitle;
	}
	public void setXtitle(String xtitle) {
		this.xtitle = xtitle;
	}
	public String getYtitle() {
		return ytitle;
	}
	public void setYtitle(String ytitle) {
		this.ytitle = ytitle;
	}
	public String getCharttitle() {
		return charttitle;
	}
	public void setCharttitle(String charttitle) {
		this.charttitle = charttitle;
	}
	public String getDisplayborder() {
		return displayborder;
	}
	public void setDisplayborder(String displayborder) {
		this.displayborder = displayborder;
	}
	public String getBordercolor() {
		return bordercolor;
	}
	public void setBordercolor(String bordercolor) {
		this.bordercolor = bordercolor;
	}
	public String getDisplaydesc() {
		return displaydesc;
	}
	public void setDisplaydesc(String displaydesc) {
		this.displaydesc = displaydesc;
	}
	public String getFormdisplay() {
		return formdisplay;
	}
	public void setFormdisplay(String formdisplay) {
		this.formdisplay = formdisplay;
	}
	public String getLabelstyle() {
		return labelstyle;
	}
	public void setLabelstyle(String labelstyle) {
		this.labelstyle = labelstyle;
	}
	public String getFormname() {
		return formname;
	}
	public void setFormname(String formname) {
		this.formname = formname;
	}
	public String getDefaultvalue() {
		return defaultvalue;
	}
	public void setDefaultvalue(String defaultvalue) {
		this.defaultvalue = defaultvalue;
	}
	public String getQuerytext() {
		return querytext;
	}
	public void setQuerytext(String querytext) {
		this.querytext = querytext;
	}
	public String getTempquey() {
		return tempquey;
	}
	public void setTempquey(String tempquey) {
		this.tempquey = tempquey;
	}
	public boolean isDisplaytitle() {
		return displaytitle;
	}
	public void setDisplaytitle(boolean displaytitle) {
		this.displaytitle = displaytitle;
	}
	public boolean isClearzero() {
		return clearzero;
	}
	public void setClearzero(boolean clearzero) {
		this.clearzero = clearzero;
	}
	public String getTitlestr() {
		return titlestr;
	}
	public void setTitlestr(String titlestr) {
		this.titlestr = titlestr;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getWidthunit() {
		return widthunit;
	}
	public void setWidthunit(String widthunit) {
		this.widthunit = widthunit;
	}
	public String getHeightunit() {
		return heightunit;
	}
	public void setHeightunit(String heightunit) {
		this.heightunit = heightunit;
	}
	public String getDefheight() {
		return defheight;
	}
	public void setDefheight(String defheight) {
		this.defheight = defheight;
	}
	public String getDefwidth() {
		return defwidth;
	}
	public void setDefwidth(String defwidth) {
		this.defwidth = defwidth;
	}
	public String getNeckwidth() {
		return neckwidth;
	}
	public void setNeckwidth(String neckwidth) {
		this.neckwidth = neckwidth;
	}
	public String getNeckheight() {
		return neckheight;
	}
	public void setNeckheight(String neckheight) {
		this.neckheight = neckheight;
	}
	public String getExtparam() {
		return extparam;
	}
	public void setExtparam(String extparam) {
		this.extparam = extparam;
	}
	public String getMarginright() {
		return marginright;
	}
	public void setMarginright(String marginright) {
		this.marginright = marginright;
	}
	public String getColorstr() {
		return colorstr;
	}
	public void setColorstr(String colorstr) {
		this.colorstr = colorstr;
	}
	@Column(name="sstart")
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	@Column(name="send")
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public String getRowformatstr() {
		return rowformatstr;
	}
	public void setRowformatstr(String rowformatstr) {
		this.rowformatstr = rowformatstr;
	}
	public String getColformatstr() {
		return colformatstr;
	}
	public void setColformatstr(String colformatstr) {
		this.colformatstr = colformatstr;
	}
	public String getPublishtype() {
		return publishtype;
	}
	public void setPublishtype(String publishtype) {
		this.publishtype = publishtype;
	}
	public String getEditview() {
		return editview;
	}
	public void setEditview(String editview) {
		this.editview = editview;
	}
	public boolean isExpandbtm() {
		return expandbtm;
	}
	public void setExpandbtm(boolean expandbtm) {
		this.expandbtm = expandbtm;
	}
	public boolean isExpandrgt() {
		return expandrgt;
	}
	public void setExpandrgt(boolean expandrgt) {
		this.expandrgt = expandrgt;
	}
	public String getCurtab() {
		return curtab;
	}
	public void setCurtab(String curtab) {
		this.curtab = curtab;
	}
	public String getHiddencolstr() {
		return hiddencolstr;
	}
	public void setHiddencolstr(String hiddencolstr) {
		this.hiddencolstr = hiddencolstr;
	}
	public String getEventstr() {
		return eventstr;
	}
	public void setEventstr(String eventstr) {
		this.eventstr = eventstr;
	}
	public String getDsmodel() {
		return dsmodel;
	}
	public void setDsmodel(String dsmodel) {
		this.dsmodel = dsmodel;
	}
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
	public String getSqldialect() {
		return sqldialect;
	}
	public void setSqldialect(String sqldialect) {
		this.sqldialect = sqldialect;
	}
	
	public String getIsloadfulldata() {
		return isloadfulldata;
	}
	public void setIsloadfulldata(String isloadfulldata) {
		this.isloadfulldata = isloadfulldata;
	}
	public boolean isIsexport() {
		return isexport;
	}
	public void setIsexport(boolean isexport) {
		this.isexport = isexport;
	}
	public boolean isSelectdata() {
		return selectdata;
	}
	public void setSelectdata(boolean selectdata) {
		this.selectdata = selectdata;
	}
	public String getExporttitle() {
		return exporttitle;
	}
	public void setExporttitle(String exporttitle) {
		this.exporttitle = exporttitle;
	}
	public int getPagesize() {
		return pagesize;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	public int getColsize() {
		return colsize;
	}
	public void setColsize(int colsize) {
		this.colsize = colsize;
	}
	public String getSorttype() {
		return sorttype;
	}
	public void setSorttype(String sorttype) {
		this.sorttype = sorttype;
	}
	public String getSortname() {
		return sortname;
	}
	public void setSortname(String sortname) {
		this.sortname = sortname;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	public String getTempletid() {
		return templetid;
	}
	public void setTempletid(String templetid) {
		this.templetid = templetid;
	}
	public int getColspan() {
		return colspan;
	}
	public void setColspan(int colspan) {
		this.colspan = colspan;
	}
	public int getColindex() {
		return colindex;
	}
	public void setColindex(int colindex) {
		this.colindex = colindex;
	}
	public Template templet() {
		return MainUtils.getTemplate(this.templetid) ;
	}
	@Transient
	public List<ColumnProperties> getProperties() {
		return properties;
	}
	public void setProperties(List<ColumnProperties> properties) {
		this.properties = properties;
	}
	@Transient
	public List<ColumnProperties> getColproperties() {
		return colproperties;
	}
	public void setColproperties(List<ColumnProperties> colproperties) {
		this.colproperties = colproperties;
	}
	@Transient
	public List<ColumnProperties> getMeasures() {
		return measures;
	}
	public void setMeasures(List<ColumnProperties> measures) {
		this.measures = measures;
	}
	@Transient
	public List<ReportFilter> getFilters() {
		return filters;
	}
	
	public void setFilters(List<ReportFilter> filters) {
		this.filters = filters;
	}
	@Transient
	public List<DrillDown> getDrilldown() {
		return drilldown;
	}
	public void setDrilldown(List<DrillDown> drilldown) {
		this.drilldown = drilldown;
	}
	@Transient
	public ReportData getReportData() {
		return reportData;
	}
	public void setReportData(ReportData reportData) {
		this.reportData = reportData;
	}
	
	@Transient
	public ChartProperties getChartProperties() {
		Base64 base64 = new Base64();
		try {
			return chartProperties!=null ? chartProperties : (chartProperties = (this.chartcontent==null?null:(ChartProperties) MainUtils.toObject(base64.decode(this.chartcontent))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return chartProperties;
	}
	@Transient
	public String getChartPropertiesJson() {
		return MainUtils.toJson(getChartProperties());
	}
	public String getChartcontent() {
		return chartcontent;
	}
	public void setChartcontent(String chartcontent) {
		this.chartcontent = chartcontent;
	}
	
}