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

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;


public interface ReportData extends Serializable{
	public Level getRow() ;
	public Level getCol() ;
	public void setRow(Level level) ;
	public List<List<ValueData>> getData() ;
	public String getViewData() ;
	public void setPageSize(int pageSize);
	public int getPageSize() ;
	public int getPage();
	public void setPage(int page) ;
	public void setViewData(String viewData) ;
	public void exchangeColRow() ;	//行列转换
	public void merge(ReportData data) ;
	public Date getDate() ;
	public void setDate(Date createtime) ;
	public void setException(Exception ex) ;
	public Exception getException ();
	public RequestData getRequestData();
	public void setRequestData(RequestData data);
	
	public QueryText getQueryText() ;
	public void setQueryText(QueryText queryText) ;
	
	public void setTotal(long total) ;
	public long getTotal() ;
	
	public Map<String , Object> getOptions() ;
	
	public void setOptions(Map<String , Object> options);
	
	public void setQueryTime(long queryTime) ;
	
	public long getQueryTime() ;
}
