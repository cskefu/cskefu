/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.webim.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.chatopera.cc.webim.service.cache.CacheHelper;
import com.chatopera.cc.webim.service.quene.AgentCallOutFilter;
import org.springframework.stereotype.Service;

import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.aggregation.Aggregations;
import com.hazelcast.mapreduce.aggregation.Supplier;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.SqlPredicate;
import com.chatopera.cc.util.freeswitch.model.CallCenterAgent;
import com.chatopera.cc.webim.service.quene.AiCallOutFilter;
import com.chatopera.cc.webim.web.model.UKefuCallOutNames;

@SuppressWarnings("deprecation")
@Service("calloutquene")
public class CallOutQuene {
	/**
	 * 为外呼坐席分配名单
	 * @param agentStatus
	 */
	@SuppressWarnings("unchecked")
	public static List<CallCenterAgent> service(){
		List<CallCenterAgent> agentList = new ArrayList<CallCenterAgent>();
		if(CacheHelper.getCallCenterAgentCacheBean()!=null && CacheHelper.getCallCenterAgentCacheBean().getCache()!=null) {
			PagingPredicate<String, CallCenterAgent> pagingPredicate = new PagingPredicate<String, CallCenterAgent>(  new SqlPredicate( "workstatus = 'callout'") , 10 ) ;
			agentList.addAll(((IMap<String , CallCenterAgent>) CacheHelper.getCallCenterAgentCacheBean().getCache()).values(pagingPredicate)) ;
		}
		return agentList ;
	}
	
	/**
	 * 为外呼坐席分配名单
	 * @param agentStatus
	 */
	@SuppressWarnings("unchecked")
	public static List<CallCenterAgent> service(String sip){
		List<CallCenterAgent> agentList = new ArrayList<CallCenterAgent>();
		if(CacheHelper.getCallCenterAgentCacheBean()!=null && CacheHelper.getCallCenterAgentCacheBean().getCache()!=null) {
			PagingPredicate<String, CallCenterAgent> pagingPredicate = new PagingPredicate<String, CallCenterAgent>(  new SqlPredicate( "siptrunk = '"+sip+"'") , 10 ) ;
			agentList.addAll(((IMap<String , CallCenterAgent>) CacheHelper.getCallCenterAgentCacheBean().getCache()).values(pagingPredicate)) ;
		}
		return agentList ;
	}
	
	/**
	 * 为外呼坐席分配名单
	 * @param agentStatus
	 */
	@SuppressWarnings("unchecked")
	public static List<CallCenterAgent> extention(String extno){
		List<CallCenterAgent> agentList = new ArrayList<CallCenterAgent>();
		if(CacheHelper.getCallCenterAgentCacheBean()!=null && CacheHelper.getCallCenterAgentCacheBean().getCache()!=null) {
			PagingPredicate<String, CallCenterAgent> pagingPredicate = new PagingPredicate<String, CallCenterAgent>(  new SqlPredicate( "extno = '"+extno+"'") , 10 ) ;
			agentList.addAll(((IMap<String , CallCenterAgent>) CacheHelper.getCallCenterAgentCacheBean().getCache()).values(pagingPredicate)) ;
		}
		return agentList ;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static int countAiCallOut(String orgi) {
		/**
		 * 统计当前在线的坐席数量
		 */
		IMap callOutMap = (IMap<String, Object>) CacheHelper.getCallOutCacheBean().getCache() ;
		AiCallOutFilter filter = new AiCallOutFilter(orgi) ;
		Long names = (Long) callOutMap.aggregate(Supplier.fromKeyPredicate(filter), Aggregations.count()) ;
		return names!=null ? names.intValue() : 0 ;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static int countAgentCallOut(String orgi) {
		/**
		 * 统计当前在线的坐席数量
		 */
		IMap callOutMap = (IMap<String, Object>) CacheHelper.getCallOutCacheBean().getCache() ;
		AgentCallOutFilter filter = new AgentCallOutFilter(orgi) ;
		Long names = (Long) callOutMap.aggregate(Supplier.fromKeyPredicate(filter), Aggregations.count()) ;
		return names!=null ? names.intValue() : 0 ;
	}
	
	
	/**
	 * 外呼监控，包含机器人和人工两个部分
	 * @param agentStatus
	 */
	@SuppressWarnings("unchecked")
	public static List<UKefuCallOutNames> callOutNames(String calltype , int p , int ps){
		List<UKefuCallOutNames> ukefuCallOutNamesList = new ArrayList<UKefuCallOutNames>();
		if(CacheHelper.getCallOutCacheBean()!=null && CacheHelper.getCallOutCacheBean().getCache()!=null) {
			PagingPredicate<String, UKefuCallOutNames> pagingPredicate = new PagingPredicate<String, UKefuCallOutNames>(  new SqlPredicate( "calltype = '"+calltype+"'") , 10 ) ;
			pagingPredicate.setPage(p);
			ukefuCallOutNamesList.addAll(((IMap<String , UKefuCallOutNames>) CacheHelper.getCallOutCacheBean().getCache()).values(pagingPredicate)) ;
		}
		return ukefuCallOutNamesList;
	}
}
