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
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "uk_reportfilter")
@org.hibernate.annotations.Proxy(lazy = false)
public class ReportFilter implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6580407773098864725L;
	private String id ;
	private String dataid ;
	private String dataname ;
	private String title ;
	private String modelid ;
	private String reportid ;
	private String contype ;
	private Date createtime = new Date();
	private String funtype ;	//过滤功能类型： filter , rank , 
	private String filtertype ;
	private String measureid ;	
	private String dimid ;
	private boolean request = false ;
	
	private String cubeid ;
	
	private String modeltype ;			// 组件类型
	private String formatstr ; 
	private String convalue ;
	private String userdefvalue ;
	private String valuefiltertype ;
	private String name ;
	private String code ; 
	private String orgi ;
	private String content ;
	private String valuestr ;
	private String filterprefix ;		//修改用处 ， 改为 当前过滤器是否是对权限维度的 过滤，如果是，则不允许普通用修改
	private String filtersuffix ;
	private String valuecompare ;
	private String defaultvalue ;
	private String noformatvalue;    //去掉格式化的值例如defaultvalue[time].[nain].[2014],这儿只存2014
	private String startvalue ;		//范围过滤的  开始默认值
	private String endvalue ;		//范围查询的 结束默认值
	private String comparetype ;
	private String requestvalue ;		//保存最后一次从页面上点击查询后获取的数据
	private boolean child = false ;				//是否是 下级查询的过滤条件
	
	private String requeststartvalue ;			//范围查询的  开始值
	private String requestendvalue ;			//范围查询的结束值
	private String defaultvaluerule ;
	
	
	private CubeLevel level ;
	
	private ReportData reportData ;	//过滤器的 默认加载的数据， 缓存的数据，可以重新加载
	//private QueryText query ;
	private String queryText ;
	
	private String mustvalue="";//是否必须输入mustvalue必须输入，否则为空
	
	
	private String cascadeid;//用于过滤器级联操作，下一级id
	
	private TableProperties tableproperty;//查询的字段
	
	private ReportFilter childFilter;//级联下一级
	
	@SuppressWarnings("unused")
	private ReportFilter parentFilter;//级联中上一级
	
	private String parentValue;
	
	
	private String groupids; //指标分组id，多个分组id用“,”分隔开
	
	private String filtertemplet;
	
	//private ReportData data ;
	
	//private String cubename;
	
	private int sortindex;
	
	private String tableid;//主表
	private String fieldid;//主表映射字段
	private String fktableid;//过滤关联表
	private String fkfieldid;//过滤映射字段
	private String filterfieldid;//过滤字段
	private boolean isdic = false;//启用字典
	private String diccode;//字典类型
	private String keyfield;//key字段
	private String valuefield;//value字段
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
	public String getDataid() {
		return dataid;
	}
	public void setDataid(String dataid) {
		this.dataid = dataid;
	}
	public String getModelid() {
		return modelid;
	}
	public void setModelid(String modelid) {
		this.modelid = modelid;
	}
	public String getReportid() {
		return reportid;
	}
	public void setReportid(String reportid) {
		this.reportid = reportid;
	}
	public String getContype() {
		return contype;
	}
	public void setContype(String contype) {
		this.contype = contype;
	}
	public String getFiltertype() {
		return filtertype;
	}
	public void setFiltertype(String filtertype) {
		this.filtertype = filtertype;
	}
	
	public String getFormatstr() {
		return this.formatstr;
	}
	public void setFormatstr(String formatstr) {
		this.formatstr = formatstr;
	}
	public String getConvalue() {
		return convalue;
	}
	public void setConvalue(String convalue) {
		this.convalue = convalue;
	}
	public String getUserdefvalue() {
		return userdefvalue;
	}
	public void setUserdefvalue(String userdefvalue) {
		this.userdefvalue = userdefvalue;
	}
	public String getValuefiltertype() {
		return valuefiltertype;
	}
	public void setValuefiltertype(String valuefiltertype) {
		this.valuefiltertype = valuefiltertype;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code!=null ? code : id ;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getValuestr() {
		return valuestr;
	}
	public void setValuestr(String valuestr) {
		this.valuestr = valuestr;
	}
	public String getFilterprefix() {
		return filterprefix;
	}
	public void setFilterprefix(String filterprefix) {
		this.filterprefix = filterprefix;
	}
	public String getFiltersuffix() {
		return filtersuffix;
	}
	public void setFiltersuffix(String filtersuffix) {
		this.filtersuffix = filtersuffix;
	}
	public String getModeltype() {
		return modeltype;
	}
	public void setModeltype(String modeltype) {
		this.modeltype = modeltype;
	}
	public String getDataname() {
		return dataname;
	}
	public void setDataname(String dataname) {
		this.dataname = dataname;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getFuntype() {
		return funtype;
	}
	public void setFuntype(String funtype) {
		this.funtype = funtype;
	}
	public String getMeasureid() {
		return measureid;
	}
	public void setMeasureid(String measureid) {
		this.measureid = measureid;
	}
	public String getValuecompare() {
		return valuecompare;
	}
	public void setValuecompare(String valuecompare) {
		this.valuecompare = valuecompare;
	}
	public String getDefaultvalue() {
		return defaultvalue!=null ? defaultvalue:"";
	}
	public void setDefaultvalue(String defaultvalue) {
		this.defaultvalue = defaultvalue;
	}
	public String getComparetype() {
		return comparetype!=null ? comparetype : "";
	}
	public void setComparetype(String comparetype) {
		this.comparetype = comparetype;
	}
	@Transient
	public boolean isRequest() {
		return request;
	}
	public void setRequest(boolean request) {
		this.request = request;
	}
	@Transient
	public ReportData getReportData() {
		return reportData;
	}
	public void setReportData(ReportData reportData) {
		this.reportData = reportData;
	}
	@Transient
	public CubeLevel getLevel() {
		return level;
	}
	public void setLevel(CubeLevel level) {
		this.level = level;
	}
	public String getDimid() {
		return dimid;
	}
	public void setDimid(String dimid) {
		this.dimid = dimid;
	}
	@Transient
	public String getRequestvalue() {
		return requestvalue;
	}
	public void setRequestvalue(String requestvalue) {
		this.requestvalue = requestvalue;
	}
	@Transient
	public boolean isChild() {
		return child;
	}
	public void setChild(boolean child) {
		this.child = child;
	}
	/*@Transient
	public String getCurvalue(){
		return RivuTools.getDefaultValue(this , RivuDataContext.ReportCompareEnum.COMPARE.toString() , null) ;
	}
	@Transient
	public String getCurstartvalue(){
		return RivuTools.getStartValue(this, null);
	}
	@Transient
	public String getCurendvalue(){
		return RivuTools.getEndValue(this, null) ;
	}*/
	@Transient
	public String getRequeststartvalue() {
		return requeststartvalue!=null&&requeststartvalue.length()>0?requeststartvalue:null;
	}
	public void setRequeststartvalue(String requeststartvalue) {
	
		this.requeststartvalue = requeststartvalue;
		
	}
	@Transient
	public String getRequestendvalue() {
		
		return requestendvalue!=null&&requestendvalue.length()>0?requestendvalue:null;
	}
	public void setRequestendvalue(String requestendvalue) {
		
			this.requestendvalue = requestendvalue;
		
		
	}
	/*@Transient
	public QueryText getQuery() {
		return query;
	}
	public void setQuery(QueryText query) {
		this.query = query;
	}*/
	@Transient
	public String getQueryText() {
		return queryText;
	}
	public void setQueryText(String queryText) {
		this.queryText = queryText;
	}
	public String getStartvalue() {
		return startvalue;
	}
	public void setStartvalue(String startvalue) {
		this.startvalue = startvalue;
	}
	public String getEndvalue() {
		return endvalue;
	}
	public void setEndvalue(String endvalue) {
		this.endvalue = endvalue;
	}
	public String getMustvalue() {
		return mustvalue;
	}
	public void setMustvalue(String mustvalue) {
		this.mustvalue = mustvalue;
	}
	public String getCascadeid() {
		return cascadeid;
	}
	public void setCascadeid(String cascadeid) {
		this.cascadeid = cascadeid;
	}
	
	/*@Transient
	@JSONField(serialize = false)
	public ReportFilter getParentFilter() {
		return parentFilter;
	}*/
	public void setParentFilter(ReportFilter parentFilter) {
		this.parentFilter = parentFilter;
	}
	public String getGroupids() {
		return groupids;
	}
	public void setGroupids(String groupids) {
		this.groupids = groupids;
	}
	public String getNoformatvalue() {
		return noformatvalue;
	}
	public void setNoformatvalue(String noformatvalue) {
		this.noformatvalue = noformatvalue;
	}
	@Transient
	public String getParentValue() {
		return parentValue;
	}
	public void setParentValue(String parentValue) {
		this.parentValue = parentValue;
	}
	@Transient
	public Template templet(){
		return MainUtils.getTemplate(this.filtertemplet) ;
	}
	public String getFiltertemplet() {
		return filtertemplet;
	}
	public void setFiltertemplet(String filtertemplet) {
		this.filtertemplet = filtertemplet;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCubeid() {
		return cubeid;
	}
	public void setCubeid(String cubeid) {
		this.cubeid = cubeid;
	}
	public String getDefaultvaluerule() {
		return defaultvaluerule;
	}
	public void setDefaultvaluerule(String defaultvaluerule) {
		this.defaultvaluerule = defaultvaluerule;
	}
	/*public String getCubename() {
		return cubename;
	}
	public void setCubename(String cubename) {
		this.cubename = cubename;
	}*/
	public int getSortindex() {
		return sortindex;
	}
	public void setSortindex(int sortindex) {
		this.sortindex = sortindex;
	}
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="tableproperty")
	@NotFound(action=NotFoundAction.IGNORE)
	public TableProperties getTableproperty() {
		return tableproperty;
	}
	public void setTableproperty(TableProperties tableproperty) {
		this.tableproperty = tableproperty;
	}
	@Transient
	public ReportFilter getChildFilter() {
		return childFilter;
	}
	public void setChildFilter(ReportFilter childFilter) {
		this.childFilter = childFilter;
	}
	public String getTableid() {
		return tableid;
	}
	public void setTableid(String tableid) {
		this.tableid = tableid;
	}
	public String getFieldid() {
		return fieldid;
	}
	public void setFieldid(String fieldid) {
		this.fieldid = fieldid;
	}
	public String getFktableid() {
		return fktableid;
	}
	public void setFktableid(String fktableid) {
		this.fktableid = fktableid;
	}
	public String getFkfieldid() {
		return fkfieldid;
	}
	public void setFkfieldid(String fkfieldid) {
		this.fkfieldid = fkfieldid;
	}
	public String getFilterfieldid() {
		return filterfieldid;
	}
	public void setFilterfieldid(String filterfieldid) {
		this.filterfieldid = filterfieldid;
	}
	public boolean isIsdic() {
		return isdic;
	}
	public void setIsdic(boolean isdic) {
		this.isdic = isdic;
	}
	public String getDiccode() {
		return diccode;
	}
	public void setDiccode(String diccode) {
		this.diccode = diccode;
	}
	public String getKeyfield() {
		return keyfield;
	}
	public void setKeyfield(String keyfield) {
		this.keyfield = keyfield;
	}
	public String getValuefield() {
		return valuefield;
	}
	public void setValuefield(String valuefield) {
		this.valuefield = valuefield;
	}
	
	
}
