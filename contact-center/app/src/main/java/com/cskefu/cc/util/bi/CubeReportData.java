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
package com.cskefu.cc.util.bi;

import com.cskefu.cc.util.bi.model.Level;
import com.cskefu.cc.util.bi.model.RequestData;
import com.cskefu.cc.util.bi.model.ValueData;
import org.apache.lucene.queryparser.flexible.core.nodes.PathQueryNode.QueryText;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class CubeReportData implements ReportData, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3987533346101055742L;
	private Exception exception ;
	private Level row ;
	private Level col ;
	private int pageSize ;
	private int page ;
	private long numFound = 0 ;
	private RequestData reqdata;
	private QueryText queryText ;
	private Map<String , Object> options ;
	private long queryTime ;
	private long total ;
	
	private Date createTime;

	private List<List<ValueData>> data ;
	private String viewData ;
	
	public void setRow(Level row) {
		this.row = row;
	}

	public void setCol(Level col) {
		this.col = col;
	}

	public void setData(List<List<ValueData>> data) {
		this.data = data;
	}

	@Override
	public Level getRow() {
		return row;
	}

	@Override
	public Level getCol() {
		return col;
	}

	@Override
	public List<List<ValueData>> getData() {
		return data;
	}

	public String getViewData() {
		return viewData;
	}

	public void setViewData(String viewData) {
		this.viewData = viewData;
	}
	public ReportData clone(){
		try {
			return (ReportData) super.clone() ;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}

	public long getNumFound() {
		return numFound;
	}

	public void setNumFound(long l) {
		this.numFound = l;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public void exchangeColRow() {
		Level temp = this.col ; 
		this.col = this.row ;
		this.row = temp ;
		int colspan = this.col.getColspan() ;
		this.col.setColspan(this.col.getRowspan()) ;
		this.col.setRowspan(colspan) ;
		
		colspan = this.row.getColspan() ;
		this.row.setColspan(this.row.getRowspan()) ;
		this.row.setRowspan(colspan) ;
		
 		List<List<ValueData>> tempList = new ArrayList<List<ValueData>>();
		for(int i=0 ; i<data.size() ; i++){
			for(int j=0 ; j<data.get(i).size() ; j++){
				if(tempList.size()<=j){
					tempList.add(new ArrayList<ValueData>()) ;
				}
				if(tempList.get(j)!=null){
					tempList.get(j).add(data.get(i).get(j)) ;
				}
			}
		}
		this.data.clear();
		this.data = tempList ;
	}

	@Override
	public void merge(ReportData data) {
		this.col.getChilderen().addAll(data.getCol().getChilderen()) ;
		if(this.row!=null && this.row.getTitle()!=null && data.getRow()!=null && data.getRow().getTitle()!=null){
			List<?> titles = this.row.getTitle().get(this.row.getTitle().size()-1) ;
			List<?> otitles = data.getRow().getTitle().get(data.getRow().getTitle().size()-1) ;
			for(int i=0 ; i<titles.size() ; i++){
				for(int j=0 ; j<otitles.size() ; j++){
					if(titles.get(i).toString().equals(otitles.get(j).toString())){
						this.data.get(i).addAll(data.getData().get(j)) ;
						break ;
					}
				}
			}
		}
		this.col.init();
	}

	@Override
	public Date getDate() {
		// TODO Auto-generated method stub
		return createTime;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	@Override
	public void setRequestData(RequestData data) {
		reqdata = data;
	}

	@Override
	public RequestData getRequestData() {
		return reqdata;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public QueryText getQueryText() {
		return queryText;
	}

	public void setQueryText(QueryText queryText) {
		this.queryText = queryText;
	}

	public Map<String, Object> getOptions() {
		return options;
	}

	public void setOptions(Map<String, Object> options) {
		this.options = options;
	}

	public long getQueryTime() {
		return queryTime;
	}

	public void setQueryTime(long queryTime) {
		this.queryTime = queryTime;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	@Override
	public void setDate(Date createtime) {
		// TODO Auto-generated method stub
		this.createTime = createtime;
	}
}
