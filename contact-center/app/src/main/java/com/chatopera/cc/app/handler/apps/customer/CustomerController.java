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

package com.chatopera.cc.app.handler.apps.customer;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.chatopera.cc.app.MainContext;
import com.chatopera.cc.app.MainUtils;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.PinYinTools;
import com.chatopera.cc.exception.CSKefuException;
import com.chatopera.cc.util.task.DSData;
import com.chatopera.cc.util.task.ExcelImportProecess;
import com.chatopera.cc.util.task.export.ExcelExporterProcess;
import com.chatopera.cc.util.task.process.EntCustomerProcess;
import com.chatopera.cc.app.service.es.ContactsRepository;
import com.chatopera.cc.app.service.es.EntCustomerRepository;
import com.chatopera.cc.app.service.repository.PropertiesEventRepository;
import com.chatopera.cc.util.PropertiesEventUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.chatopera.cc.util.task.DSDataEvent;
import com.chatopera.cc.app.service.repository.MetadataRepository;
import com.chatopera.cc.app.service.repository.ReporterRepository;
import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.model.CustomerGroupForm;
import com.chatopera.cc.app.model.EntCustomer;
import com.chatopera.cc.app.model.MetadataTable;
import com.chatopera.cc.app.model.PropertiesEvent;

@Controller
@RequestMapping("/apps/customer")
public class CustomerController extends Handler{
	
	@Autowired
	private EntCustomerRepository entCustomerRes;
	
	@Autowired
	private ContactsRepository contactsRes ;
	
	@Autowired
	private ReporterRepository reporterRes ;
	
	@Autowired
	private MetadataRepository metadataRes ;
	
	@Autowired
	private PropertiesEventRepository propertiesEventRes ;
	
	@Value("${web.upload-path}")
    private String path;
	
    @RequestMapping("/index")
    @Menu(type = "customer" , subtype = "index")
    public ModelAndView index(ModelMap map , HttpServletRequest request , @Valid String q , @Valid String ekind) throws CSKefuException {
    	BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

    	if(!super.esOrganFilter(request, boolQueryBuilder)){
            return request(super.createAppsTempletResponse("/apps/business/customer/index"));
        }

    	if(!StringUtils.isBlank(q)){
        	map.put("q", q) ;
        }
    	if(!StringUtils.isBlank(ekind)){
    		boolQueryBuilder.must(termQuery("ekind" , ekind)) ;
        	map.put("ekind", ekind) ;
        }
    	map.addAttribute("entCustomerList", entCustomerRes.findByCreaterAndSharesAndOrgi(super.getUser(request).getId(), super.getUser(request).getId(),super.getOrgi(request), null , null , false, boolQueryBuilder , q , new PageRequest(super.getP(request) , super.getPs(request)))) ;
    	
    	return request(super.createAppsTempletResponse("/apps/business/customer/index"));
    }
    
    @RequestMapping("/today")
    @Menu(type = "customer" , subtype = "today")
    public ModelAndView today(ModelMap map , HttpServletRequest request , @Valid String q , @Valid String ekind) throws CSKefuException {
    	BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if(!super.esOrganFilter(request, boolQueryBuilder)){
            return request(super.createAppsTempletResponse("/apps/business/customer/index"));
        }

    	if(!StringUtils.isBlank(q)){
        	map.put("q", q) ;
        }

    	if(!StringUtils.isBlank(ekind)){
    		boolQueryBuilder.must(termQuery("ekind" , ekind)) ;
        	map.put("ekind", ekind) ;
        }
    	map.addAttribute("entCustomerList", entCustomerRes.findByCreaterAndSharesAndOrgi(super.getUser(request).getId(), super.getUser(request).getId(),super.getOrgi(request), MainUtils.getStartTime() , null , false, boolQueryBuilder ,q , new PageRequest(super.getP(request) , super.getPs(request)))) ;
    	
    	return request(super.createAppsTempletResponse("/apps/business/customer/index"));
    }
    
    @RequestMapping("/week")
    @Menu(type = "customer" , subtype = "week")
    public ModelAndView week(ModelMap map , HttpServletRequest request , @Valid String q , @Valid String ekind) throws CSKefuException {
    	BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(!super.esOrganFilter(request, boolQueryBuilder)){
            return request(super.createAppsTempletResponse("/apps/business/customer/index"));
        }

    	if(!StringUtils.isBlank(q)){
        	map.put("q", q) ;
        }
    	if(!StringUtils.isBlank(ekind)){
    		boolQueryBuilder.must(termQuery("ekind" , ekind)) ;
        	map.put("ekind", ekind) ;
        }
    	map.addAttribute("entCustomerList", entCustomerRes.findByCreaterAndSharesAndOrgi(super.getUser(request).getId(), super.getUser(request).getId(),super.getOrgi(request), MainUtils.getWeekStartTime() , null , false, boolQueryBuilder ,q , new PageRequest(super.getP(request) , super.getPs(request)))) ;
    	
    	return request(super.createAppsTempletResponse("/apps/business/customer/index"));
    }
    
    @RequestMapping("/enterprise")
    @Menu(type = "customer" , subtype = "enterprise")
    public ModelAndView enterprise(ModelMap map , HttpServletRequest request , @Valid String q , @Valid String ekind) throws CSKefuException {
    	BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(!super.esOrganFilter(request, boolQueryBuilder)){
            return request(super.createAppsTempletResponse("/apps/business/customer/index"));
        }

    	boolQueryBuilder.must(termQuery("etype" , MainContext.CustomerTypeEnum.ENTERPRISE.toString())) ;
    	if(!StringUtils.isBlank(ekind)){
    		boolQueryBuilder.must(termQuery("ekind" , ekind)) ;
        	map.put("ekind", ekind) ;
        }
    	if(!StringUtils.isBlank(q)){
        	map.put("q", q) ;
        }
    	map.addAttribute("entCustomerList", entCustomerRes.findByCreaterAndSharesAndOrgi(super.getUser(request).getId(), super.getUser(request).getId(),super.getOrgi(request), null , null , false, boolQueryBuilder ,q , new PageRequest(super.getP(request) , super.getPs(request)))) ;
    	return request(super.createAppsTempletResponse("/apps/business/customer/index"));
    }
    
    @RequestMapping("/personal")
    @Menu(type = "customer" , subtype = "personal")
    public ModelAndView personal(ModelMap map , HttpServletRequest request , @Valid String q , @Valid String ekind) throws CSKefuException {
    	BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(!super.esOrganFilter(request, boolQueryBuilder)){
            return request(super.createAppsTempletResponse("/apps/business/customer/index"));
        }

    	boolQueryBuilder.must(termQuery("etype" , MainContext.CustomerTypeEnum.PERSONAL.toString())) ;
    	
    	if(!StringUtils.isBlank(ekind)){
    		boolQueryBuilder.must(termQuery("ekind" , ekind)) ;
        	map.put("ekind", ekind) ;
        }
    	
    	if(!StringUtils.isBlank(q)){
        	map.put("q", q) ;
        }
    	map.addAttribute("entCustomerList", entCustomerRes.findByCreaterAndSharesAndOrgi(super.getUser(request).getId(), super.getUser(request).getId(),super.getOrgi(request), null , null , false, boolQueryBuilder ,q , new PageRequest(super.getP(request) , super.getPs(request)))) ;
    	return request(super.createAppsTempletResponse("/apps/business/customer/index"));
    }
    
    @RequestMapping("/creater")
    @Menu(type = "customer" , subtype = "creater")
    public ModelAndView creater(ModelMap map , HttpServletRequest request , @Valid String q , @Valid String ekind) throws CSKefuException {
    	BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(!super.esOrganFilter(request, boolQueryBuilder)){
            return request(super.createAppsTempletResponse("/apps/business/customer/index"));
        }

    	boolQueryBuilder.must(termQuery("creater" , super.getUser(request).getId())) ;
    	
    	if(!StringUtils.isBlank(ekind)){
    		boolQueryBuilder.must(termQuery("ekind" , ekind)) ;
        	map.put("ekind", ekind) ;
        }
    	if(!StringUtils.isBlank(q)){
        	map.put("q", q) ;
        }
    	
    	map.addAttribute("entCustomerList", entCustomerRes.findByCreaterAndSharesAndOrgi(super.getUser(request).getId(), super.getUser(request).getId(), super.getOrgi(request),null , null , false, boolQueryBuilder ,q , new PageRequest(super.getP(request) , super.getPs(request)))) ;
        return request(super.createAppsTempletResponse("/apps/business/customer/index"));
    }
    
    @RequestMapping("/add")
    @Menu(type = "customer" , subtype = "customer")
    public ModelAndView add(ModelMap map , HttpServletRequest request,@Valid String ekind) {
    	map.addAttribute("ekind", ekind);
        return request(super.createRequestPageTempletResponse("/apps/business/customer/add"));
    }
    
    @RequestMapping(  "/save")
    @Menu(type = "customer" , subtype = "customer")
    public ModelAndView save(HttpServletRequest request  , @Valid CustomerGroupForm customerGroupForm) {
    	customerGroupForm.getEntcustomer().setCreater(super.getUser(request).getId());
    	customerGroupForm.getEntcustomer().setOrgi(super.getOrgi(request));
    	customerGroupForm.getEntcustomer().setOrgan(super.getUser(request).getOrgan());
//    	customerGroupForm.getEntcustomer().setEtype(MainContext.CustomerTypeEnum.ENTERPRISE.toString());
    	customerGroupForm.getEntcustomer().setPinyin(PinYinTools.getInstance().getFirstPinYin(customerGroupForm.getEntcustomer().getName()));
    	entCustomerRes.save(customerGroupForm.getEntcustomer());
    	if(customerGroupForm.getContacts()!=null && !StringUtils.isBlank(customerGroupForm.getContacts().getName())){
    		customerGroupForm.getContacts().setEntcusid(customerGroupForm.getEntcustomer().getId());
    		customerGroupForm.getContacts().setCreater(super.getUser(request).getId());
    		customerGroupForm.getContacts().setOrgi(super.getOrgi(request));
    		customerGroupForm.getContacts().setOrgan(super.getUser(request).getOrgan());
    		customerGroupForm.getContacts().setPinyin(PinYinTools.getInstance().getFirstPinYin(customerGroupForm.getContacts().getName()));
    		if(StringUtils.isBlank(customerGroupForm.getContacts().getCusbirthday())) {
    			customerGroupForm.getContacts().setCusbirthday(null);
			}
    		contactsRes.save(customerGroupForm.getContacts()) ;
    	}
        return request(super.createRequestPageTempletResponse("redirect:/apps/customer/index.html?ekind="+customerGroupForm.getEntcustomer().getEkind()));
    }
    
    @RequestMapping("/delete")
    @Menu(type = "customer" , subtype = "customer")
    public ModelAndView delete(HttpServletRequest request ,@Valid EntCustomer entCustomer ,@Valid String p) {
    	if(entCustomer!=null){
    		entCustomer = entCustomerRes.findOne(entCustomer.getId()) ;
    		entCustomer.setDatastatus(true);							//客户和联系人都是 逻辑删除
    		entCustomerRes.save(entCustomer) ;
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/apps/customer/index.html?p="+p+"&ekind="+entCustomer.getEkind()));
    }
    
    @RequestMapping("/edit")
    @Menu(type = "customer" , subtype = "customer")
    public ModelAndView edit(ModelMap map , HttpServletRequest request , @Valid String id) {
    	map.addAttribute("entCustomer", entCustomerRes.findOne(id)) ;
        return request(super.createRequestPageTempletResponse("/apps/business/customer/edit"));
    }
    
    @RequestMapping(  "/update")
    @Menu(type = "customer" , subtype = "customer")
    public ModelAndView update(HttpServletRequest request  , @Valid CustomerGroupForm customerGroupForm) {
    	EntCustomer customer = entCustomerRes.findOne(customerGroupForm.getEntcustomer().getId()) ;
    	
    	List<PropertiesEvent> events = PropertiesEventUtils.processPropertiesModify(request, customerGroupForm.getEntcustomer() , customer , "id" , "orgi" , "creater" ,"createtime" , "updatetime") ;	//记录 数据变更 历史
    	if(events.size()>0){
    		String modifyid = MainUtils.getUUID() ;
    		Date modifytime = new Date();
    		for(PropertiesEvent event : events){
    			event.setDataid(customerGroupForm.getEntcustomer().getId());
    			event.setCreater(super.getUser(request).getId());
    			event.setOrgi(super.getOrgi(request));
    			event.setModifyid(modifyid);
    			event.setCreatetime(modifytime);
    			propertiesEventRes.save(event) ;
    		}
    	}
    	
    	customerGroupForm.getEntcustomer().setCreater(customer.getCreater());
    	customerGroupForm.getEntcustomer().setCreatetime(customer.getCreatetime());
    	customerGroupForm.getEntcustomer().setOrgi(super.getOrgi(request));
    	customerGroupForm.getEntcustomer().setOrgan(super.getUser(request).getOrgan());
    	customerGroupForm.getEntcustomer().setPinyin(PinYinTools.getInstance().getFirstPinYin(customerGroupForm.getEntcustomer().getName()));
    	entCustomerRes.save(customerGroupForm.getEntcustomer());
    	
        return request(super.createRequestPageTempletResponse("redirect:/apps/customer/index.html?ekind="+customerGroupForm.getEntcustomer().getEkind()));
    }
    
    @RequestMapping("/imp")
    @Menu(type = "customer" , subtype = "customer")
    public ModelAndView imp(ModelMap map , HttpServletRequest request,@Valid String ekind) {
    	map.addAttribute("ekind",ekind);
        return request(super.createRequestPageTempletResponse("/apps/business/customer/imp"));
    }
    
    @RequestMapping("/impsave")
    @Menu(type = "customer" , subtype = "customer")
    public ModelAndView impsave(ModelMap map , HttpServletRequest request , @RequestParam(value = "cusfile", required = false) MultipartFile cusfile,@Valid String ekind) throws IOException {
    	DSDataEvent event = new DSDataEvent();
    	String fileName = "customer/"+ MainUtils.getUUID()+cusfile.getOriginalFilename().substring(cusfile.getOriginalFilename().lastIndexOf(".")) ;
    	File excelFile = new File(path , fileName) ;
    	if(!excelFile.getParentFile().exists()){
    		excelFile.getParentFile().mkdirs() ;
    	}
    	MetadataTable table = metadataRes.findByTablename("uk_entcustomer") ;
    	if(table!=null){
	    	FileUtils.writeByteArrayToFile(new File(path , fileName), cusfile.getBytes());
	    	event.setDSData(new DSData(table,excelFile , cusfile.getContentType(), super.getUser(request)));
	    	event.getDSData().setClazz(EntCustomer.class);
	    	event.getDSData().setProcess(new EntCustomerProcess(entCustomerRes));
	    	event.setOrgi(super.getOrgi(request));
	    	/*if(!StringUtils.isBlank(ekind)){
	    		event.getValues().put("ekind", ekind) ;
	    	}*/
	    	event.getValues().put("creater", super.getUser(request).getId()) ;
	    	reporterRes.save(event.getDSData().getReport()) ;
	    	new ExcelImportProecess(event).process() ;		//启动导入任务
    	}
    	
    	return request(super.createRequestPageTempletResponse("redirect:/apps/customer/index.html"));
    }
    
    @RequestMapping("/expids")
    @Menu(type = "customer" , subtype = "customer")
    public void expids(ModelMap map , HttpServletRequest request , HttpServletResponse response , @Valid String[] ids) throws IOException {
    	if(ids!=null && ids.length > 0){
    		Iterable<EntCustomer> entCustomerList = entCustomerRes.findAll(Arrays.asList(ids)) ;
    		MetadataTable table = metadataRes.findByTablename("uk_entcustomer") ;
    		List<Map<String,Object>> values = new ArrayList<Map<String,Object>>();
    		for(EntCustomer customer : entCustomerList){
    			values.add(MainUtils.transBean2Map(customer)) ;
    		}
    		
    		response.setHeader("content-disposition", "attachment;filename=UCKeFu-EntCustomer-"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+".xls");  
    		
    		ExcelExporterProcess excelProcess = new ExcelExporterProcess( values, table, response.getOutputStream()) ;
    		excelProcess.process();
    	}
    	
        return ;
    }
    
    @RequestMapping("/expall")
    @Menu(type = "customer" , subtype = "customer")
    public void expall(ModelMap map , HttpServletRequest request , HttpServletResponse response) throws IOException, CSKefuException {
    	BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(!super.esOrganFilter(request, boolQueryBuilder)){
            // #TODO 提示没有部门
            return;
        }

    	boolQueryBuilder.must(termQuery("datastatus" , false)) ;		//只导出 数据删除状态 为 未删除的 数据
    	Iterable<EntCustomer> entCustomerList = entCustomerRes.findByCreaterAndSharesAndOrgi(super.getUser(request).getId(), super.getUser(request).getId(),super.getOrgi(request), null , null , false, boolQueryBuilder , null , new PageRequest(super.getP(request) , super.getPs(request)));
    	
    	MetadataTable table = metadataRes.findByTablename("uk_entcustomer") ;
		List<Map<String,Object>> values = new ArrayList<Map<String,Object>>();
		for(EntCustomer customer : entCustomerList){
			values.add(MainUtils.transBean2Map(customer)) ;
		}
		
		response.setHeader("content-disposition", "attachment;filename=UCKeFu-EntCustomer-"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+".xls");  
		
		ExcelExporterProcess excelProcess = new ExcelExporterProcess( values, table, response.getOutputStream()) ;
		excelProcess.process();
        return ;
    }
    
    @RequestMapping("/expsearch")
    @Menu(type = "customer" , subtype = "customer")
    public void expall(ModelMap map , HttpServletRequest request , HttpServletResponse response , @Valid String q , @Valid String ekind) throws IOException, CSKefuException {
    	BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(!super.esOrganFilter(request, boolQueryBuilder)){
            // #TODO 提示没有部门
            return;
        }

    	if(!StringUtils.isBlank(q)){
        	map.put("q", q) ;
        }
    	if(!StringUtils.isBlank(ekind)){
    		boolQueryBuilder.must(termQuery("ekind" , ekind)) ;
        	map.put("ekind", ekind) ;
        }
    	
    	Iterable<EntCustomer> entCustomerList = entCustomerRes.findByCreaterAndSharesAndOrgi(super.getUser(request).getId(), super.getUser(request).getId(),super.getOrgi(request), null , null , false, boolQueryBuilder ,q , new PageRequest(super.getP(request) , super.getPs(request)));
    	MetadataTable table = metadataRes.findByTablename("uk_entcustomer") ;
    	List<Map<String,Object>> values = new ArrayList<Map<String,Object>>();
    	for(EntCustomer customer : entCustomerList){
    		values.add(MainUtils.transBean2Map(customer)) ;
    	}

    	response.setHeader("content-disposition", "attachment;filename=UCKeFu-EntCustomer-"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+".xls");  

    	ExcelExporterProcess excelProcess = new ExcelExporterProcess( values, table, response.getOutputStream()) ;
    	excelProcess.process();
    	
        return ;
    }
}