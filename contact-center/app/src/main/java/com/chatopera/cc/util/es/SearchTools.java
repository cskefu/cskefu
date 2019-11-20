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
package com.chatopera.cc.util.es;

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.model.FormFilter;
import com.chatopera.cc.model.FormFilterItem;
import com.chatopera.cc.model.MetadataTable;
import com.chatopera.cc.persistence.impl.ESDataExchangeImpl;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

public class SearchTools {
	
	public static PageImpl<UKDataBean> search(String orgi , FormFilter formFilter , List<FormFilterItem> itemList , MetadataTable metadataTable , boolean loadRef , int p, int ps){
		BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
		queryBuilder.must(termQuery("orgi", orgi)) ;
		
		BoolQueryBuilder orBuilder = new BoolQueryBuilder();
		int orNums = 0 ;
		for(FormFilterItem formFilterItem : itemList) {
			QueryBuilder tempQueryBuilder = null ;
			if(formFilterItem.getField().equals("q")) {
				tempQueryBuilder = new QueryStringQueryBuilder(formFilterItem.getValue()).defaultOperator(Operator.AND) ;
			}else {
				switch(formFilterItem.getCond()) {
					case "01" : 
						tempQueryBuilder = rangeQuery(formFilterItem.getField()).from(formFilterItem.getValue()).includeLower(false) ;
						break ;
					case "02" : 
						tempQueryBuilder = rangeQuery(formFilterItem.getField()).from(formFilterItem.getValue()).includeLower(true) ;
						break ;
					case "03" : 
						tempQueryBuilder = rangeQuery(formFilterItem.getField()).to(formFilterItem.getValue()).includeUpper(false) ;
						break ;
					case "04" : 
						tempQueryBuilder = rangeQuery(formFilterItem.getField()).to(formFilterItem.getValue()).includeUpper(true) ;
						break ;
					case "05" : 
						tempQueryBuilder = termQuery(formFilterItem.getField() , formFilterItem.getValue()) ;
						break ;
					case "06" : 
						tempQueryBuilder = termQuery(formFilterItem.getField() , formFilterItem.getValue()) ;
						break ;
					case "07" : 
						tempQueryBuilder = new QueryStringQueryBuilder(formFilterItem.getValue()).field(formFilterItem.getField()).defaultOperator(Operator.AND) ;
						break ;
					default :
						break ;
				}
			}
			if("AND".equalsIgnoreCase(formFilterItem.getComp())) {
				if("06".equals(formFilterItem.getCond())) {
					queryBuilder.mustNot(tempQueryBuilder) ;
				}else {
					queryBuilder.must(tempQueryBuilder) ;
				}
			}else {
				orNums ++ ;
				if("06".equals(formFilterItem.getCond())) {
					orBuilder.mustNot(tempQueryBuilder) ;
				}else {
					orBuilder.should(tempQueryBuilder) ;
				}
			}
		}
		if(orNums > 0) {
			queryBuilder.must(orBuilder) ;
		}
		
		return search(queryBuilder, metadataTable, loadRef, p, ps);
	}
	
	public static PageImpl<UKDataBean> dissearch(String orgi , FormFilter formFilter , List<FormFilterItem> itemList , MetadataTable metadataTable , int p, int ps){
		BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
		queryBuilder.must(termQuery("orgi", orgi)) ;
		queryBuilder.must(termQuery("status", MainContext.NamesDisStatusType.NOT.toString())) ;
		queryBuilder.must(termQuery("validresult", "valid")) ;
		
		BoolQueryBuilder orBuilder = new BoolQueryBuilder();
		int orNums = 0 ;
		for(FormFilterItem formFilterItem : itemList) {
			QueryBuilder tempQueryBuilder = null ;
			if(formFilterItem.getField().equals("q")) {
				tempQueryBuilder = new QueryStringQueryBuilder(formFilterItem.getValue()).defaultOperator(Operator.AND) ;
			}else {
				switch(formFilterItem.getCond()) {
					case "01" : 
						tempQueryBuilder = rangeQuery(formFilterItem.getField()).from(formFilterItem.getValue()).includeLower(false) ;
						break ;
					case "02" : 
						tempQueryBuilder = rangeQuery(formFilterItem.getField()).from(formFilterItem.getValue()).includeLower(true) ;
						break ;
					case "03" : 
						tempQueryBuilder = rangeQuery(formFilterItem.getField()).to(formFilterItem.getValue()).includeUpper(false) ;
						break ;
					case "04" : 
						tempQueryBuilder = rangeQuery(formFilterItem.getField()).to(formFilterItem.getValue()).includeUpper(true) ;
						break ;
					case "05" : 
						tempQueryBuilder = termQuery(formFilterItem.getField() , formFilterItem.getValue()) ;
						break ;
					case "06" : 
						tempQueryBuilder = termQuery(formFilterItem.getField() , formFilterItem.getValue()) ;
						break ;
					case "07" : 
						tempQueryBuilder = new QueryStringQueryBuilder(formFilterItem.getValue()).field(formFilterItem.getField()).defaultOperator(Operator.AND) ;
						break ;
					default :
						break ;
				}
			}
			if("AND".equalsIgnoreCase(formFilterItem.getComp())) {
				if("06".equals(formFilterItem.getCond())) {
					queryBuilder.mustNot(tempQueryBuilder) ;
				}else {
					queryBuilder.must(tempQueryBuilder) ;
				}
			}else {
				orNums ++ ;
				if("06".equals(formFilterItem.getCond())) {
					orBuilder.mustNot(tempQueryBuilder) ;
				}else {
					orBuilder.should(tempQueryBuilder) ;
				}
			}
		}
		if(orNums > 0) {
			queryBuilder.must(orBuilder) ;
		}
		return search(queryBuilder, metadataTable, false, p, ps);
	}
	
	public static PageImpl<UKDataBean> recoversearch(String orgi , String cmd ,String id, MetadataTable metadataTable , int p, int ps){
		BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
		queryBuilder.must(termQuery("orgi", orgi)) ;
		queryBuilder.mustNot(termQuery("status", MainContext.NamesDisStatusType.NOT.toString())) ;
		queryBuilder.must(termQuery("validresult", "valid")) ;
		
		switch(cmd) {
			case "actid" : queryBuilder.must(termQuery("actid", id)) ; break ;
			case "batid" : queryBuilder.must(termQuery("batid", id)) ; break ;
			case "taskid" : queryBuilder.must(termQuery("taskid", id)) ; break ;
			case "filterid" : queryBuilder.must(termQuery("filterid", id)) ; break ;
			case "agent" : queryBuilder.must(termQuery(Constants.CSKEFU_SYSTEM_DIS_AGENT, id)) ; break ;
			case "skill" : queryBuilder.must(termQuery(Constants.CSKEFU_SYSTEM_DIS_ORGAN, id)) ; break ;
			case "taskskill" : queryBuilder.must(termQuery("taskid", id)).must(termQuery("status", MainContext.NamesDisStatusType.DISAGENT.toString())) ; break ;
			case "filterskill" : queryBuilder.must(termQuery("filterid", id)).must(termQuery("status", MainContext.NamesDisStatusType.DISAGENT.toString())) ; break ;
			default : queryBuilder.must(termQuery("actid", "NOT_EXIST_KEY")) ;  //必须传入一个进来;
		}
		
		return search(queryBuilder, metadataTable, false, p, ps);
	}
	/**
	 * 
	 * @param orgi
	 * @param agent
	 * @param p
	 * @param ps
	 * @return
	 */
	public static PageImpl<UKDataBean> agentsearch(String orgi ,boolean excludeCalled ,  String agent , int p, int ps){
		BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
		queryBuilder.must(termQuery("orgi", orgi)) ;
		if(excludeCalled){
			queryBuilder.must(termQuery("callstatus", MainContext.NameStatusType.NOTCALL.toString())) ;
		}
		queryBuilder.must(termQuery("validresult", "valid")) ;
		queryBuilder.must(termQuery(Constants.CSKEFU_SYSTEM_DIS_AGENT, agent)) ;
		queryBuilder.must(termQuery("status", MainContext.NamesDisStatusType.DISAGENT.toString())) ;
		
		return search(queryBuilder, p, ps);
	}
	
	/**
	 * 
	 * @param orgi
	 * @param agent
	 * @param p
	 * @param ps
	 * @return
	 */
	public static PageImpl<UKDataBean> agentapsearch(String orgi , String agent , int p, int ps){
		BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
		queryBuilder.must(termQuery("orgi", orgi)) ;
		queryBuilder.must(termQuery("validresult", "valid")) ;
		queryBuilder.must(termQuery(Constants.CSKEFU_SYSTEM_DIS_AGENT, agent)) ;
		queryBuilder.must(termQuery("apstatus", true)) ;		//预约状态
		
		queryBuilder.must(rangeQuery("aptime").to(System.currentTimeMillis())) ;		//预约状态
		
		return search(queryBuilder, p, ps);
	}
	
	/**
	 * 
	 * @param orgi
	 * @param agent
	 * @param p
	 * @param ps
	 * @return
	 */
	public static PageImpl<UKDataBean> aisearch(String orgi , int p, int ps){
		BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
		queryBuilder.must(termQuery("orgi", orgi)) ;
		queryBuilder.must(termQuery("callstatus", MainContext.NameStatusType.NOTCALL.toString())) ;
		
		queryBuilder.must(termQuery("validresult", "valid")) ;
		queryBuilder.must(termQuery("status", MainContext.NamesDisStatusType.DISAI.toString())) ;
		
		return search(queryBuilder, p, ps);
	}
	

	
	/**
	 * 
	 * @param orgi
	 * @param agent
	 * @param p
	 * @param ps
	 * @return
	 */
	public static PageImpl<UKDataBean> namesearch(String orgi , String phonenum){
		BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
		queryBuilder.must(termQuery("orgi", orgi)) ;
		queryBuilder.must(termQuery("validresult", "valid")) ;
		queryBuilder.must(termQuery("status", MainContext.NamesDisStatusType.DISAGENT.toString())) ;
		StringBuffer strb = new StringBuffer();
		if(!StringUtils.isBlank(phonenum)) {
			strb.append(phonenum) ;
			if(phonenum.startsWith("0")) {
				strb.append(" ").append(phonenum.substring(1)) ;
			}
		}else {
			strb.append(Constants.CSKEFU_SYSTEM_NO_DAT) ;
		}
		queryBuilder.must(new QueryStringQueryBuilder(strb.toString()).defaultOperator(Operator.OR) );
		return search(queryBuilder,0, 1);
	}
	
	/**
	 * 
	 * @param orgi
	 * @param agent
	 * @param p
	 * @param ps
	 * @return
	 */
	public static PageImpl<UKDataBean> search(BoolQueryBuilder queryBuilder, int p, int ps){
		return search(queryBuilder, null, true, p, ps);
	}
	/**
	 * 
	 * @param queryBuilder
	 * @param metadataTable
	 * @param loadRef
	 * @param p
	 * @param ps
	 * @return
	 */
	private static PageImpl<UKDataBean> search(BoolQueryBuilder queryBuilder , MetadataTable metadataTable , boolean loadRef , int p, int ps){
		ESDataExchangeImpl esDataExchange = MainContext.getContext().getBean(ESDataExchangeImpl.class);
		return esDataExchange.findPageResult(queryBuilder, Constants.SYSTEM_INDEX, metadataTable, new PageRequest(p, ps , Sort.Direction.ASC, "createtime") , loadRef) ;
	}
	
	/**
	 * 
	 * @param queryBuilder
	 * @param metadataTable
	 * @param loadRef
	 * @param p
	 * @param ps
	 * @return
	 */
	public static PageImpl<UKDataBean> aggregation(BoolQueryBuilder queryBuilder , String aggField, boolean loadRef , int p, int ps){
		ESDataExchangeImpl esDataExchange = MainContext.getContext().getBean(ESDataExchangeImpl.class);
		return esDataExchange.findAllPageAggResult(queryBuilder , aggField ,  new PageRequest(p, ps , Sort.Direction.ASC, "createtime") , loadRef , null) ;
	}
	
	/**
	 * 
	 * @param queryBuilder
	 * @param metadataTable
	 * @param loadRef
	 * @param p
	 * @param ps
	 * @return
	 */
	public static UKDataBean get(UKDataBean dataBean){
		ESDataExchangeImpl esDataExchange = MainContext.getContext().getBean(ESDataExchangeImpl.class);
		return esDataExchange.getIObjectByPK(dataBean, dataBean.getId());
	}
	
	/**
	 * 
	 * @param queryBuilder
	 * @param metadataTable
	 * @param loadRef
	 * @param p
	 * @param ps
	 * @return
	 */
	public static UKDataBean get(String type, String id){
		ESDataExchangeImpl esDataExchange = MainContext.getContext().getBean(ESDataExchangeImpl.class);
		return esDataExchange.getIObjectByPK(type, id);
	}
	
	/**
	 * 
	 * @param queryBuilder
	 * @param metadataTable
	 * @param loadRef
	 * @param p
	 * @param ps
	 * @return
	 */
	public static void save(UKDataBean dataBean){
		ESDataExchangeImpl esDataExchange = MainContext.getContext().getBean(ESDataExchangeImpl.class);
		try {
			esDataExchange.saveIObject(dataBean);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
