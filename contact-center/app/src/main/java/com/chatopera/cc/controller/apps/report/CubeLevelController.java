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
package com.chatopera.cc.controller.apps.report;

import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.CubeLevel;
import com.chatopera.cc.model.CubeMetadata;
import com.chatopera.cc.model.Dimension;
import com.chatopera.cc.model.TableProperties;
import com.chatopera.cc.persistence.repository.CubeLevelRepository;
import com.chatopera.cc.persistence.repository.CubeMetadataRepository;
import com.chatopera.cc.persistence.repository.DimensionRepository;
import com.chatopera.cc.persistence.repository.TablePropertiesRepository;
import com.chatopera.cc.util.Menu;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/apps/report/cubelevel")
public class CubeLevelController extends Handler{
	
	@Autowired
	private CubeLevelRepository cubeLevelRes;
	
	@Autowired
	private DimensionRepository dimensionRes;
	
	@Autowired
	private TablePropertiesRepository tablePropertiesRes;
	
	@Autowired
	private CubeMetadataRepository cubeMetadataRes;
	
	@RequestMapping("/add")
    @Menu(type = "report" , subtype = "cubelevel")
    public ModelAndView cubeLeveladd(ModelMap map , HttpServletRequest request , @Valid String cubeid,@Valid String dimid) {
    	map.addAttribute("cubeid", cubeid);
    	map.addAttribute("dimid", dimid);
    	//map.addAttribute("fktableList",cubeMetadataRes.findByCubeid(cubeid));
    	Dimension dim = dimensionRes.findByIdAndOrgi(dimid,super.getOrgi(request));
    	
    	if(dim!=null){
    		if(!StringUtils.isBlank(dim.getFktable())) {
    			map.put("fktableidList", tablePropertiesRes.findByDbtableid(dim.getFktable()));
    		}else {
    			List<CubeMetadata> cmList = cubeMetadataRes.findByCubeidAndMtype(cubeid,"0");
    			if(!cmList.isEmpty() && cmList.get(0)!=null) {
    				map.put("fktableidList", tablePropertiesRes.findByDbtableid(cmList.get(0).getTb().getId()));
    			}
    		}
    		
    	}
        return request(super.createRequestPageTempletResponse("/apps/business/report/cube/cubelevel/add"));
    }
    
    @RequestMapping("/save")
    @Menu(type = "report" , subtype = "cubelevel" )
    public ModelAndView cubeLevelsave(ModelMap map , HttpServletRequest request , @Valid CubeLevel cubeLevel,@Valid String tableid) {
    	if(!StringUtils.isBlank(cubeLevel.getName())){
    		cubeLevel.setOrgi(super.getOrgi(request));
    		cubeLevel.setCreater(super.getUser(request).getId());
    		cubeLevel.setCode(cubeLevel.getColumname());
    		if(!StringUtils.isBlank(tableid)) {
    			TableProperties tb = new TableProperties();
    			tb.setId(tableid);
    			TableProperties t = tablePropertiesRes.findById(tableid);
    			cubeLevel.setTablename(t.getTablename());
    			cubeLevel.setCode(t.getFieldname());
        		cubeLevel.setColumname(t.getFieldname());
    			cubeLevel.setTableproperty(tb);
    		}
			cubeLevelRes.save(cubeLevel) ;
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/apps/report/cube/detail.html?id="+cubeLevel.getCubeid()+"&dimensionId="+cubeLevel.getDimid()));
    }
    
    @RequestMapping("/delete")
    @Menu(type = "report" , subtype = "cubelevel" )
    public ModelAndView quickreplydelete(ModelMap map , HttpServletRequest request , @Valid String id) {
    	CubeLevel cubeLevel = cubeLevelRes.findOne(id) ;
    	if(cubeLevel!=null){
    		cubeLevelRes.delete(cubeLevel);
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/apps/report/cube/detail.html?id="+cubeLevel.getCubeid()+"&dimensionId="+cubeLevel.getDimid()));
    }
    @RequestMapping("/edit")
    @Menu(type = "report" , subtype = "cubelevel" , admin= true)
    public ModelAndView quickreplyedit(ModelMap map , HttpServletRequest request , @Valid String id) {
    	CubeLevel cubeLevel = cubeLevelRes.findOne(id) ; 
    	map.put("cubeLevel", cubeLevel) ;
    	Dimension dim = dimensionRes.findByIdAndOrgi(cubeLevel.getDimid(),super.getOrgi(request));
    	if(dim!=null){
    		if(!StringUtils.isBlank(dim.getFktable())) {
    			map.put("fktableidList", tablePropertiesRes.findByDbtableid(dim.getFktable()));
    			map.addAttribute("tableid", dim.getFktable());
    		}else {
    			List<CubeMetadata> cmList = cubeMetadataRes.findByCubeidAndMtype(cubeLevel.getCubeid(),"0");
    			if(!cmList.isEmpty() && cmList.get(0)!=null) {
    				map.put("fktableidList", tablePropertiesRes.findByDbtableid(cmList.get(0).getTb().getId()));
    				map.addAttribute("tableid", cmList.get(0).getId());
    			}
    		}
    		
    	}
        return request(super.createRequestPageTempletResponse("/apps/business/report/cube/cubelevel/edit"));
    }
    
    @RequestMapping("/update")
    @Menu(type = "report" , subtype = "cubelevel" , admin= true)
    public ModelAndView quickreplyupdate(ModelMap map , HttpServletRequest request , @Valid CubeLevel cubeLevel,@Valid String tableid) {
    	if(!StringUtils.isBlank(cubeLevel.getId())){
    		CubeLevel temp = cubeLevelRes.findOne(cubeLevel.getId()) ;
    		cubeLevel.setOrgi(super.getOrgi(request));
    		cubeLevel.setCreater(super.getUser(request).getId());
    		if(temp!=null){
    			cubeLevel.setCreatetime(temp.getCreatetime());
    		}
    		if(!StringUtils.isBlank(tableid)) {
    			TableProperties tb = new TableProperties();
    			tb.setId(tableid);
    			TableProperties t = tablePropertiesRes.findById(tableid);
    			cubeLevel.setTablename(t.getTablename());
    			cubeLevel.setCode(t.getFieldname());
        		cubeLevel.setColumname(t.getFieldname());
    			cubeLevel.setTableproperty(tb);
    		}
    		cubeLevelRes.save(cubeLevel) ;
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/apps/report/cube/detail.html?id="+cubeLevel.getCubeid()+"&dimensionId="+cubeLevel.getDimid()));
    }
    @RequestMapping("/fktableid")
    @Menu(type = "report" , subtype = "cubelevel" , admin= true)
    public ModelAndView fktableid(ModelMap map , HttpServletRequest request , @Valid String tableid) {
    	if(!StringUtils.isBlank(tableid)){
    		map.put("fktableidList", tablePropertiesRes.findByDbtableid(tableid));
    	}
    	return request(super.createRequestPageTempletResponse("/apps/business/report/cube/cubelevel/fktableiddiv"));
    }
}