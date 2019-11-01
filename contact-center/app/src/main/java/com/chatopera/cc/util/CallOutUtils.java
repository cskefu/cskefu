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
package com.chatopera.cc.util;

import java.util.Date;
import java.util.List;

import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.util.es.UKDataBean;
import com.chatopera.cc.app.cache.CacheHelper;
import com.chatopera.cc.app.persistence.repository.UKefuCallOutTaskRepository;
import com.chatopera.cc.app.model.UKefuCallOutConfig;
import com.chatopera.cc.app.model.JobDetail;
import com.chatopera.cc.app.model.TableProperties;
import org.apache.commons.lang.StringUtils;

import com.chatopera.cc.app.im.client.NettyClients;
import com.chatopera.cc.util.freeswitch.model.CallCenterAgent;
import com.chatopera.cc.app.persistence.repository.UKefuCallOutConfigRepository;
import com.chatopera.cc.app.persistence.repository.UKefuCallOutNamesRepository;
import com.chatopera.cc.app.persistence.repository.JobDetailRepository;
import com.chatopera.cc.app.persistence.repository.MetadataRepository;
import com.chatopera.cc.app.model.UKefuCallOutNames;
import com.chatopera.cc.app.model.UKefuCallOutTask;
import com.chatopera.cc.app.model.MetadataTable;

public class CallOutUtils {
	/**
	 * AI配置
	 * @param orgi
	 * @return
	 */
	public static UKefuCallOutConfig initCallOutConfig(String dataid, String orgi){
		UKefuCallOutConfig ukefuCallOutConfig = (UKefuCallOutConfig) CacheHelper.getSystemCacheBean().getCacheObject(MainContext.SYSTEM_CACHE_CALLOUT_CONFIG+"_"+dataid, orgi);
		if(MainContext.getContext() != null && ukefuCallOutConfig == null){
			UKefuCallOutConfigRepository ukefuCallOutConfigRepository = MainContext.getContext().getBean(UKefuCallOutConfigRepository.class) ;
			List<UKefuCallOutConfig> ukefuCallOutConfigList = ukefuCallOutConfigRepository.findByDataidAndOrgi(dataid,orgi) ;
			if(ukefuCallOutConfigList.size() == 0){
				ukefuCallOutConfig = new UKefuCallOutConfig() ;
			}else{
				ukefuCallOutConfig = ukefuCallOutConfigList.get(0) ;
				CacheHelper.getSystemCacheBean().put(MainContext.SYSTEM_CACHE_CALLOUT_CONFIG+"_"+ ukefuCallOutConfig.getDataid(), ukefuCallOutConfig, orgi) ;
			}
		}
		return ukefuCallOutConfig;
	}
	
	/**
	 * AI配置
	 * @param orgi
	 * @return
	 */
	public static UKefuCallOutConfig initCallOutConfig(String orgi){
		UKefuCallOutConfig ukefuCallOutConfig = (UKefuCallOutConfig) CacheHelper.getSystemCacheBean().getCacheObject(MainContext.SYSTEM_CACHE_CALLOUT_CONFIG+"_"+orgi, orgi);
		if(MainContext.getContext() != null && ukefuCallOutConfig == null){
			UKefuCallOutConfigRepository ukefuCallOutConfigRepository = MainContext.getContext().getBean(UKefuCallOutConfigRepository.class) ;
			List<UKefuCallOutConfig> ukefuCallOutConfigList = ukefuCallOutConfigRepository.findByOrgi(orgi) ;
			if(ukefuCallOutConfigList.size() == 0){
				ukefuCallOutConfig = new UKefuCallOutConfig() ;
			}else{
				ukefuCallOutConfig = ukefuCallOutConfigList.get(0) ;
				CacheHelper.getSystemCacheBean().put(MainContext.SYSTEM_CACHE_CALLOUT_CONFIG+"_"+orgi, ukefuCallOutConfig, orgi) ;
			}
		}
		return ukefuCallOutConfig;
	}
	
	/**
	 * AI配置
	 * @param orgi
	 * @return
	 */
	public static List<UKefuCallOutConfig> initCallOutConfig(){
		UKefuCallOutConfigRepository ukefuCallOutConfigRepository = MainContext.getContext().getBean(UKefuCallOutConfigRepository.class) ;
		return ukefuCallOutConfigRepository.findAll()  ;
	}
	
	
	public static UKefuCallOutNames processNames(UKDataBean name, CallCenterAgent agent , String orgi , int leavenames) {
		String batid = (String) name.getValues().get("batid") ;
		String taskid = (String) name.getValues().get("taskid") ;
		JobDetail batch = MainContext.getContext().getBean(JobDetailRepository.class).findByIdAndOrgi(batid, orgi) ;
		UKefuCallOutTask task = MainContext.getContext().getBean(UKefuCallOutTaskRepository.class).findByIdAndOrgi(taskid, orgi) ;
		UKefuCallOutNames callOutName = new UKefuCallOutNames() ;
		UKefuCallOutNamesRepository callOutNamesRes = MainContext.getContext().getBean(UKefuCallOutNamesRepository.class) ;
		
		List<UKefuCallOutNames> callNamesList = callOutNamesRes.findByDataidAndCreaterAndOrgi((String)name.getValues().get("id"), (String) name.getValues().get(MainContext.UKEFU_SYSTEM_DIS_AGENT), orgi) ;
		if(callNamesList.size() > 0) {
			callOutName = callNamesList.get(0) ;
		}
		if(callOutName!=null){
			callOutName.setOrgi(orgi);
			if(task!=null) {
				callOutName.setName(task.getName());	//任务名称
			}
			if(batch!=null) {
				callOutName.setBatname(batch.getName());
				callOutName.setMetaname(batch.getActid());
			}
			
			
			callOutName.setActid(task.getActid());
			callOutName.setBatid(batid);
			
			callOutName.setTaskid(taskid);
			
			callOutName.setFilterid((String) name.getValues().get("filterid"));
			callOutName.setDataid((String)name.getValues().get("id"));
			
			callOutName.setStatus(MainContext.NamesProcessStatus.DIS.toString());
			
			callOutName.setCreater((String) name.getValues().get(MainContext.UKEFU_SYSTEM_DIS_AGENT));
			callOutName.setOrgan((String) name.getValues().get(MainContext.UKEFU_SYSTEM_DIS_ORGAN));
			callOutName.setCreatetime(new Date());
			callOutName.setUpdatetime(new Date());
			Object apstatus = name.getValues().get("apstatus") ;
			if(apstatus!=null && apstatus.toString().equals("true")) {
				callOutName.setReservation(true);
			}else {
				callOutName.setReservation(false);
			}
			callOutName.setMemo((String) name.getValues().get("apmemo"));
			
			callOutName.setOwneruser((String) name.getValues().get(MainContext.UKEFU_SYSTEM_DIS_AGENT));
			callOutName.setOwnerdept((String) name.getValues().get(MainContext.UKEFU_SYSTEM_DIS_AGENT));
		}
		
		callOutName.setLeavenum(leavenames);
		
		String dial_number = null ;
		boolean disphonenum = false ;
		String distype = null;
		
		if(batch!=null && !StringUtils.isBlank(batch.getActid())) {
			MetadataTable table = MainContext.getContext().getBean(MetadataRepository.class).findByTablename(batch.getActid()) ;
			for(TableProperties tp : table.getTableproperty()) {
				if(tp.isPhonenumber()) {
					dial_number = (String) name.getValues().get(tp.getFieldname()) ; 
					disphonenum = tp.isSecfield() ;
					distype = tp.getSecdistype() ;
					break ;
				}
			}
		}
		
		if(!StringUtils.isBlank(dial_number)) {
			callOutName.setPhonenumber(dial_number);
			if(disphonenum) {
				callOutName.setDistype(distype);
			}
			if(agent!=null) {
				NettyClients.getInstance().sendCallCenterMessage(agent.getExtno(), "preview", callOutName);
			}
		}else if(agent!=null){
			agent.setWorkstatus(MainContext.WorkStatusEnum.IDLE.toString());
			NettyClients.getInstance().sendCallCenterMessage(agent.getExtno(), "error", "nophonenumber");
			
			NettyClients.getInstance().sendCallCenterMessage(agent.getExtno(), "docallout", agent);
		}
		callOutNamesRes.save(callOutName) ;
		if(agent!=null) {
			agent.setNameid(callOutName.getId());
		}
		return callOutName ;
	}
	
	public static UKefuCallOutNames processNames(UKDataBean name, String orgi , int leavenames , UKefuCallOutNamesRepository callOutNamesRes) {
		String batid = (String) name.getValues().get("batid") ;
		String taskid = (String) name.getValues().get("taskid") ;
		JobDetail batch = MainContext.getContext().getBean(JobDetailRepository.class).findByIdAndOrgi(batid, orgi) ;
		UKefuCallOutTask task = MainContext.getContext().getBean(UKefuCallOutTaskRepository.class).findByIdAndOrgi(taskid, orgi) ;
		UKefuCallOutNames callOutName = new UKefuCallOutNames() ;
		
		List<UKefuCallOutNames> callNamesList = callOutNamesRes.findByDataidAndCreaterAndOrgi((String)name.getValues().get("id"), (String) name.getValues().get(MainContext.UKEFU_SYSTEM_DIS_AGENT), orgi) ;
		if(callNamesList.size() > 0) {
			callOutName = callNamesList.get(0) ;
		}
		if(callOutName!=null){
			callOutName.setOrgi(orgi);
			if(task!=null) {
				callOutName.setName(task.getName());	//任务名称
			}
			if(batch!=null) {
				callOutName.setBatname(batch.getName());
			}
			
			
			callOutName.setActid(task.getActid());
			callOutName.setBatid(batid);
			
			callOutName.setTaskid(taskid);
			
			callOutName.setMetaname(batch.getActid());
			
			callOutName.setFilterid((String) name.getValues().get("filterid"));
			callOutName.setDataid((String)name.getValues().get("id"));
			
			callOutName.setStatus(MainContext.NamesProcessStatus.DIS.toString());
			
			callOutName.setCreater((String) name.getValues().get(MainContext.UKEFU_SYSTEM_DIS_AGENT));
			callOutName.setOrgan((String) name.getValues().get(MainContext.UKEFU_SYSTEM_DIS_ORGAN));
			callOutName.setCreatetime(new Date());
			callOutName.setUpdatetime(new Date());
			String apstatus = (String) name.getValues().get("apstatus") ;
			if(!StringUtils.isBlank(apstatus) && apstatus.equals("true")) {
				callOutName.setReservation(true);
			}else {
				callOutName.setReservation(false);
			}
			callOutName.setMemo((String) name.getValues().get("apmemo"));
			
			callOutName.setOwneruser((String) name.getValues().get(MainContext.UKEFU_SYSTEM_DIS_AGENT));
			callOutName.setOwnerdept((String) name.getValues().get(MainContext.UKEFU_SYSTEM_DIS_AGENT));
		}
		
		callOutName.setLeavenum(leavenames);
		
		String dial_number = null ;
		boolean disphonenum = false ;
		String distype = null;
		
		if(batch!=null && !StringUtils.isBlank(batch.getActid())) {
			MetadataTable table = MainContext.getContext().getBean(MetadataRepository.class).findByTablename(batch.getActid()) ;
			for(TableProperties tp : table.getTableproperty()) {
				if(tp.isPhonenumber()) {
					dial_number = (String) name.getValues().get(tp.getFieldname()) ; 
					disphonenum = tp.isSecfield() ;
					distype = tp.getSecdistype() ;
					break ;
				}
			}
		}
		
		if(!StringUtils.isBlank(dial_number)) {
			callOutName.setPhonenumber(dial_number);
			if(disphonenum) {
				callOutName.setDistype(distype);
			}
			
		}
		callOutNamesRes.save(callOutName) ;
		return callOutName ;
	}
}
