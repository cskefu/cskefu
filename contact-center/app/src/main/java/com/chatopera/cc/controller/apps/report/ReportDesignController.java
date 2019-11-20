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

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.*;
import com.chatopera.cc.persistence.repository.*;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.bi.ReportData;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@Controller
@RequestMapping("/apps/report/design")
public class ReportDesignController extends Handler {

	@Value("${web.upload-path}")
	private String path;

	@Value("${uk.im.server.port}")
	private Integer port;

	@Autowired
	private TemplateRepository templateRes;

	@Autowired
	private ReportRepository reportRes;

	@Autowired
	private ReportModelRepository reportModelRes;

	@Autowired
	private PublishedCubeRepository publishedCubeRepository;

	@Autowired
	private ColumnPropertiesRepository columnPropertiesRepository;


	@Autowired
	private ReportFilterRepository reportFilterRepository;

	@Autowired
	private ReportCubeService reportCubeService;
	
	@Autowired
	private TablePropertiesRepository tablePropertiesRes;
	
	@Autowired
	private PublishedReportRepository publishedReportRes;
	
	@Autowired
	private SysDicRepository sysDicRes;
	@Autowired
	private MetadataRepository metadataRes;

	@RequestMapping("/index")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView index(ModelMap map, HttpServletRequest request, @Valid String q, @Valid String id) throws Exception {
		List<SysDic> tpDicList = Dict.getInstance().getDic(Constants.CSKEFU_SYSTEM_DIC);
		for (SysDic sysDic : tpDicList) {
			if (sysDic.getCode().equals("layout")) {
				map.addAttribute("layoutList",
						templateRes.findByTemplettypeAndOrgi(sysDic.getId(), super.getOrgi(request)));
			} else if (sysDic.getCode().equals("report")) {
				map.addAttribute("reportList",
						templateRes.findByTemplettypeAndOrgi(sysDic.getId(), super.getOrgi(request)));
			} else if (sysDic.getCode().equals("filter")) {
				map.addAttribute("filterList",
						templateRes.findByTemplettypeAndOrgi(sysDic.getId(), super.getOrgi(request)));
			}
		}

		if (!StringUtils.isBlank(id)) {
			map.addAttribute("report", reportRes.findByIdAndOrgi(id, super.getOrgi(request)));
			map.addAttribute("reportModels", reportModelRes.findByOrgiAndReportid(super.getOrgi(request), id));
			
			List<ReportFilter> listFilters = reportFilterRepository.findByReportidAndFiltertypeAndOrgi(id, "report", super.getOrgi(request));
			if(!listFilters.isEmpty()) {
				Map<String,ReportFilter> filterMap = new HashMap<String,ReportFilter>();
				for(ReportFilter rf:listFilters) {
					filterMap.put(rf.getId(), rf);
				}
				for(ReportFilter rf:listFilters) {
					if(!StringUtils.isBlank(rf.getCascadeid())) {
						rf.setChildFilter(filterMap.get(rf.getCascadeid()));
					}
				}
			}
			map.addAttribute("reportFilters", reportCubeService.fillReportFilterData(listFilters, request));
		}
		return request(super.createRequestPageTempletResponse("/apps/business/report/design/index"));
	}

	/**
	 * 请求 报表的模板组件， 请求的时候，生成个报表组件，报表组件 需要存放在列的对应关系中
	 * 
	 * @param map
	 * @param request
	 * @param template
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/rtpl")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView rtpl(ModelMap map, HttpServletRequest request, @Valid String tplname, @Valid String template,
			@Valid String colindex, @Valid String id, @Valid String parentid, @Valid String mid) throws Exception {
		Template tp = templateRes.findByIdAndOrgi(template, super.getOrgi(request));
		map.addAttribute("eltemplet", tp);
		if (!StringUtils.isBlank(parentid)) {
			ReportModel model = new ReportModel();
			model.setOrgi(super.getOrgi(request));
			model.setCreatetime(new Date());
			model.setReportid(id);
			model.setParentid(parentid);
			model.setName(tplname);
			if (!StringUtils.isBlank(colindex) && colindex.matches("[\\d]{1,}")) {
				model.setColindex(Integer.parseInt(colindex));
			} else {
				model.setColindex(1);
			}
			ChartProperties chartProperties = new ChartProperties();
			chartProperties.setChartype(tp.getCharttype());
			Base64 base64 = new Base64();
			model.setChartcontent(base64.encodeToString(MainUtils.toBytes(chartProperties))) ;
			model.setTempletid(template);
			model.setMid(mid);

			reportModelRes.save(model);
			map.addAttribute("element", model);
		}
		return request(super.createRequestPageTempletResponse("/apps/business/report/design/element"));
	}

	/**
	 * 请求 报表的模板组件， 请求的时候，生成个报表组件，报表组件 需要存放在列的对应关系中
	 * 
	 * @param map
	 * @param request
	 * @param template
	 * @param id
	 * @return
	 */
	@RequestMapping("/element")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView element(ModelMap map, HttpServletRequest request, @Valid String colindex, @Valid String id,
			@Valid String parentid, @Valid String mid) {

		if (!StringUtils.isBlank(id) && !StringUtils.isBlank(parentid)) {
			ReportModel model = reportModelRes.findByIdAndOrgi(id, super.getOrgi(request));
			if (model != null) {
				model.setParentid(parentid);
				if (!StringUtils.isBlank(colindex) && colindex.matches("[\\d]{1,}")) {
					model.setColindex(Integer.parseInt(colindex));
				} else {
					model.setColindex(1);
				}
				reportModelRes.save(model);
			}
		}
		return request(super.createRequestPageTempletResponse("/public/success"));
	}

	/**
	 * 请求 布局的模板组件 ， 请求的时候，生成一个布局记录，布局记录分两个部分，一个是行，一个是列 ，一次请求，创建一条行记录（ROW）和多个列记录（COL）
	 * 行记录和列记录都存放到 ES中
	 * 
	 * @param map
	 * @param request
	 * @param template
	 * @param id
	 * @return
	 */
	@RequestMapping("/ltpl")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView ltpl(ModelMap map, HttpServletRequest request, @Valid String template, @Valid String id,
			@Valid String mid, @Valid String colspan) {
		map.addAttribute("templet", templateRes.findByIdAndOrgi(template, super.getOrgi(request)));
		ReportModel model = new ReportModel();
		model.setOrgi(super.getOrgi(request));
		model.setCreatetime(new Date());
		model.setReportid(id);
		model.setParentid(id);

		if (!StringUtils.isBlank(colspan) && colspan.matches("[\\d]{1,}")) {
			model.setColspan(Integer.parseInt(colspan));
		} else {
			model.setColspan(4);
		}
		model.setTempletid(template);
		model.setMid(mid);

		reportModelRes.save(model);
		map.addAttribute("model", model);

		return request(super.createRequestPageTempletResponse("/apps/business/report/design/layout"));
	}
	/**
	 * 请求 过滤器的模板组件， 请求的时候，生成个过滤器组件
	 * 
	 * @param map
	 * @param request
	 * @param template
	 * @param id
	 * @return
	 */
	@RequestMapping("/ftpl")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView ftpl(ModelMap map, HttpServletRequest request, @Valid String tplname, @Valid String template,
			@Valid String colindex, @Valid String id, @Valid String parentid, @Valid String mid) {
		Template t =  templateRes.findByIdAndOrgi(template, super.getOrgi(request));
		map.addAttribute("eltemplet",t);
		if (!StringUtils.isBlank(parentid)) {
			ReportFilter filter = new ReportFilter();
			filter.setCode(MainUtils.genID());
			filter.setReportid(id);
			filter.setOrgi(super.getOrgi(request));
			filter.setCreatetime(new Date());
			filter.setName(t.getName());
			filter.setDataname(t.getName());
			filter.setTitle(t.getName());
			filter.setFiltertype("report");
	    	filter.setFuntype("filter");
			filter.setFiltertemplet(template);
			filter.setModelid(mid);
			filter.setModeltype(t.getCode());
			
			filter.setConvalue(MainContext.FilterConValueType.INPUT.toString());
			filter.setValuefiltertype(MainContext.FilterValuefilterType.COMPARE.toString());
			filter.setComparetype(MainContext.FilterCompType.EQUAL.toString());
			filter.setFormatstr("yyyy-MM-dd");
			reportFilterRepository.save(filter);
			map.addAttribute("filter", filter);
		}
		return request(super.createRequestPageTempletResponse("/apps/business/report/design/filter"));
	}
	/**
	 * 删除模板组件
	 * 
	 * @param map
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping("/modeldelete")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView modeldelete(ModelMap map, HttpServletRequest request, @Valid String id) {
		ReportModel model = reportModelRes.findByIdAndOrgi(id, super.getOrgi(request));
		if (model != null) {
			List<ReportModel> childsList = reportModelRes.findByParentidAndOrgi(model.getId(), super.getOrgi(request));
			if (!childsList.isEmpty()) {
				reportModelRes.delete(childsList);
			}
			reportModelRes.delete(model);
		}
		return request(super.createRequestPageTempletResponse("/public/success"));
	}

	/**
	 * 组件设计
	 * 
	 * @param map
	 * @param request
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/modeldesign")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView modeldesign(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String tabid , HashMap<String,String> semap)
			throws Exception {
		List<SysDic> tpDicList = Dict.getInstance().getDic(Constants.CSKEFU_SYSTEM_DIC);
		for (SysDic sysDic : tpDicList) {
			 if (sysDic.getCode().equals("report")) {
				map.addAttribute("reportList",
						templateRes.findByTemplettypeAndOrgi(sysDic.getId(), super.getOrgi(request)));
			 }
		}
		
		ReportModel model = this.getModel(id, super.getOrgi(request));
		map.addAttribute("reportModel", model);
		map.addAttribute("element", model);
		if (model != null && !StringUtils.isBlank(model.getPublishedcubeid())) {
			PublishedCube cube = publishedCubeRepository.findOne(model.getPublishedcubeid());
			map.addAttribute("cube", cube);
			if (canGetReportData(model, cube.getCube())) {
				ReportData reportData = null ;
				try {
					reportData = reportCubeService.getReportData(model, cube.getCube(), request, true,semap) ;
					map.addAttribute("reportData",reportData);
				}catch(Exception ex) {
					map.addAttribute("msg",(ExceptionUtils.getMessage(ex).replaceAll("\r\n","") + ExceptionUtils.getRootCauseMessage(ex)).replaceAll("\"", "'"));
				}
			}
			map.addAttribute("eltemplet", templateRes.findByIdAndOrgi(model.getTempletid(), super.getOrgi(request)));
		}
		map.addAttribute("tabid", tabid);
		return request(super.createRequestPageTempletResponse("/apps/business/report/design/modeldesign"));
	}
	private boolean canGetReportData(ReportModel model,Cube cube) {
		return !model.getProperties().isEmpty() || !model.getColproperties().isEmpty() || !model.getMeasures().isEmpty();
	}
	private ReportModel getModel(String id,String orgi) {
		ReportModel model = reportModelRes.findByIdAndOrgi(id, orgi);
		if (model != null) {
			model.setProperties(
					columnPropertiesRepository.findByModelidAndCurOrderBySortindexAsc(model.getId(), "field"));
			model.setColproperties(
					columnPropertiesRepository.findByModelidAndCurOrderBySortindexAsc(model.getId(), "cfield"));
			model.setMeasures(
					columnPropertiesRepository.findByModelidAndCurOrderBySortindexAsc(model.getId(), "measure"));
			List<ReportFilter> listFilters = reportFilterRepository.findByModelidOrderBySortindexAsc(model.getId());
			if(!listFilters.isEmpty()) {
				for(ReportFilter rf:listFilters) {
					if(!StringUtils.isBlank(rf.getCascadeid())) {
						rf.setChildFilter(reportFilterRepository.findByIdAndOrgi(rf.getCascadeid(), orgi));
					}
				}
			}
			model.setFilters(listFilters);
		}
		return model;
	}

	/**
	 * 选择模型
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/dataset")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView dataset(ModelMap map, HttpServletRequest request, @Valid String mid, @Valid String cubeid)
			throws Exception {
		if (!StringUtils.isBlank(cubeid)) {
			PublishedCube cube = publishedCubeRepository.findOne(cubeid);
			map.put("cube", cube);
		}
		ReportModel model = this.getModel(mid, super.getOrgi(request));
		if (!StringUtils.isBlank(cubeid)) {
			model.setPublishedcubeid(cubeid);
			if(model!=null) {
				columnPropertiesRepository.delete(model.getProperties());
				columnPropertiesRepository.delete(model.getColproperties());
				columnPropertiesRepository.delete(model.getMeasures());
				reportFilterRepository.delete(model.getFilters());
			}
		}
		reportModelRes.save(model);
		map.put("reportModel", model);
		return request(super.createRequestPageTempletResponse(
				"redirect:/apps/report/design/modeldesign.html?id=" + model.getId()));
	}

	@RequestMapping("/adddata")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView dimensionadd(ModelMap map, HttpServletRequest request, @Valid String cubeid, @Valid String t,@Valid String dtype,
			@Valid String mid, @Valid String dim, @Valid String tabid) {
		ModelAndView view = request(
				super.createRequestPageTempletResponse("/apps/business/report/design/modeldesign/add"));
		if (!StringUtils.isBlank(cubeid)) {
			PublishedCube cube = publishedCubeRepository.findOne(cubeid);
			map.addAttribute("cube", cube);
		}
		map.addAttribute("t", t);
		ReportModel model = reportModelRes.findByIdAndOrgi(mid, super.getOrgi(request));
		if (!StringUtils.isBlank(cubeid)) {
			model.setPublishedcubeid(cubeid);
        }
		map.addAttribute("reportModel", model);
		map.addAttribute("dim", dim);
		map.addAttribute("tabid", tabid);
		map.addAttribute("dtype", dtype);
		return view;
	}
	/**
	 * 添加过滤器
	 * @param map
	 * @param request
	 * @return
	 */
	@RequestMapping("/filteradd")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView filteradd(ModelMap map, HttpServletRequest request, @Valid String cubeid,@Valid String dtype,
			@Valid String mid) {
		ModelAndView view = request(
				super.createRequestPageTempletResponse("/apps/business/report/design/modeldesign/filteradd"));
		if (!StringUtils.isBlank(cubeid)) {
			PublishedCube cube = publishedCubeRepository.findOne(cubeid);
			map.addAttribute("cube", cube);
			List<MetadataTable> metadataTable = new ArrayList<>();
			for(CubeMetadata cm:cube.getCube().getMetadata()) {
				if("0".equals(cm.getMtype())) {
					map.addAttribute("table",cm.getTb());
					map.addAttribute("fieldList",cm.getTb().getTableproperty());
				}
				metadataTable.add(cm.getTb());
			}
			map.addAttribute("fktableList",metadataTable);
		}
		map.addAttribute("sysdicList", sysDicRes.findByParentid("0")) ;
		map.addAttribute("mid", mid);
		map.addAttribute("dtype", dtype);
		return view;
	}
	/**
	 * 保存过滤器
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@RequestMapping("/filtersave")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView filterupfiltersavedate(ModelMap map, HttpServletRequest request, @Valid ReportFilter f,@Valid String tbppy) {
		String modelId = "";
		if (f != null) {
			if(StringUtils.isBlank(f.getCode())) {
				f.setCode(MainUtils.genID());
			}
			f.setOrgi(super.getOrgi(request));
			f.setCreatetime(new Date());
			f.setName(f.getTitle());
			f.setDataname(f.getTitle());
			if(MainContext.FilterConValueType.AUTO.toString().equals(f.getConvalue()) && MainContext.FilterModelType.SIGSEL.toString().equals(f.getModeltype())) {
				f.setCascadeid(f.getCascadeid());
				f.setTableproperty(null);
				if(!StringUtils.isBlank(tbppy)) {
					TableProperties t = new TableProperties();
					t.setId(tbppy);
					f.setTableproperty(t);
				}
			}else {
				f.setCascadeid(null);
				f.setTableproperty(null);
			}
			modelId = f.getModelid();
			reportFilterRepository.save(f);
		}
		return request(super.createRequestPageTempletResponse(
				"redirect:/apps/report/design/modeldesign.html?id=" + modelId + "&tabid=filter"));
	}
	@RequestMapping("/gettableid")
    @Menu(type = "report" , subtype = "reportdesign")
    public ModelAndView gettableid(ModelMap map , HttpServletRequest request , @Valid String tableid) {
    	if(!StringUtils.isBlank(tableid)){
    		map.put("fktableidList", tablePropertiesRes.findByDbtableid(tableid));
    	}
    	return request(super.createRequestPageTempletResponse("/apps/business/report/design/modeldesign/fktableid"));
    }
	@RequestMapping("/values")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView values(ModelMap map, HttpServletRequest request, @Valid String mid, @Valid String dsid,
			@Valid String t, @Valid String d,@Valid String dtype, @Valid String m, @Valid String f, @Valid String tabid) throws Exception {
		ReportModel model = this.getModel(mid, super.getOrgi(request));
		if (!StringUtils.isBlank(dsid)) {
			model.setPublishedcubeid(dsid);
		}
		PublishedCube cube = publishedCubeRepository.findOne(dsid);

		if (!StringUtils.isBlank(d)) {
			boolean inlist = false;
			ColumnProperties currCp = null;
			if (cube != null && cube.getCube() != null && cube.getCube().getDimension().size() > 0) {
				for (ColumnProperties level : model.getProperties()) {
					if (level.getDataid().equals(d)) {
						inlist = true;
						currCp = level;
						break;
					}
				}
				for (ColumnProperties level : model.getColproperties()) {
					if (level.getDataid().equals(d)) {
						inlist = true;
						currCp = level;
						break;
					}
				}
				if (!inlist) {
					ColumnProperties col = new ColumnProperties();
					if(StringUtils.isBlank(dtype)) {
						col.setCur("field"); // 数据结构字段
					}else{
						col.setCur(dtype);
					}
					col.setId(MainUtils.genID());
					CubeLevel cubeLevel = null;
					for (Dimension dim : cube.getCube().getDimension()) {
						for (CubeLevel level : dim.getCubeLevel()) {
							if (level.getId().equals(d)) {
								cubeLevel = level;
								break;
							}
						}
					}
					if (cubeLevel != null) {
						col.setDataid(d);
						col.setDataname(cubeLevel.getName());
						col.setColname(cubeLevel.getColumname());
						col.setTitle(cubeLevel.getName());
					}
					col.setSortindex(("cfield".equals(dtype))?model.getColproperties().size()+1:model.getProperties().size() + 1);
					col.setOrgi(super.getOrgi(request));
					col.setModelid(model.getId());
					columnPropertiesRepository.save(col);
				}else {
					if(!StringUtils.isBlank(dtype)) {
						currCp.setCur(dtype);
						currCp.setSortindex(("cfield".equals(dtype))?model.getColproperties().size()+1:model.getProperties().size() + 1);
						columnPropertiesRepository.save(currCp);
					}
				}
			}
		}
		if (!StringUtils.isBlank(m)) {
			boolean inlist = false;
			if (cube != null && cube.getCube() != null && cube.getCube().getMeasure().size() > 0) {
				for (ColumnProperties measure : model.getMeasures()) {
					if (measure.getDataid().equals(m)) {
						inlist = true;
						break;
					}
				}
				if (!inlist) {
					ColumnProperties col = new ColumnProperties();
					col.setCur("measure"); // 数据结构字段
					col.setId(MainUtils.genID());
					CubeMeasure cubeMeasure = null;
					for (CubeMeasure measure : cube.getCube().getMeasure()) {
						if (measure.getId().equals(m)) {
							cubeMeasure = measure;
							break;
						}
					}
					if (cubeMeasure != null) {
						col.setDataid(m);
						col.setDataname(cubeMeasure.getName());
						col.setColname(cubeMeasure.getColumname());
						col.setTitle(cubeMeasure.getName());
					}
					col.setSortindex(model.getMeasures().size() + 1);
					col.setOrgi(super.getOrgi(request));
					col.setModelid(model.getId());
					model.getMeasures().add(col);
					columnPropertiesRepository.save(col);
				}
			}
		}
		if (!StringUtils.isBlank(f)) {
			boolean inlist = false;
			if (cube != null && cube.getCube() != null && cube.getCube().getDimension().size() > 0) {
				for (ReportFilter filter : model.getFilters()) {
					if (filter.getDataid().equals(f)) {
						inlist = true;
						break;
					}
				}
				if (!inlist) {
					ReportFilter filter = new ReportFilter();
					filter.setId(MainUtils.genID());
					CubeLevel cubeLevel = null;
					if (cube != null && cube.getCube() != null && cube.getCube().getDimension().size() > 0) {
						for (Dimension dim : cube.getCube().getDimension()) {
							for (CubeLevel level : dim.getCubeLevel()) {
								if (level.getId().equals(f)) {
									cubeLevel = level;
									break;
								}
							}
						}
						if (cubeLevel != null) {
							filter.setCubeid(cube.getId());
							filter.setDataid(cubeLevel.getId());
							filter.setDimid(cubeLevel.getDimid());
							filter.setLevel(cubeLevel);
							filter.setDataname(cubeLevel.getName());
							filter.setTitle(cubeLevel.getName());
							filter.setModelid(mid);
							
							filter.setModeltype(MainContext.FilterModelType.TEXT.toString());
							filter.setConvalue(MainContext.FilterConValueType.INPUT.toString());
							filter.setValuefiltertype(MainContext.FilterValuefilterType.COMPARE.toString());
							filter.setComparetype(MainContext.FilterCompType.EQUAL.toString());
							
							if ("select".equalsIgnoreCase(filter.getModeltype())) {
								filter.setConvalue(MainContext.FilterConValueType.AUTO.toString());
							}
						}
						filter.setReportid(model.getReportid());
						filter.setSortindex(model.getFilters().size() + 1);
						filter.setOrgi(super.getOrgi(request));
						model.getFilters().add(filter);
						reportFilterRepository.save(filter);
                    }
				}
			}
		}
		return request(super.createRequestPageTempletResponse(
				"redirect:/apps/report/design/modeldesign.html?id=" + model.getId() + "&tabid=" + tabid));
	}

	/**
	 * 异步 请求 报表的模板组件
	 * 
	 * @param map
	 * @param request
	 * @param template
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getelement")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView getelement(ModelMap map, HttpServletRequest request, @Valid String id,@Valid String publishedid, HashMap<String,String> semap) throws Exception {
		if (!StringUtils.isBlank(id)) {
			ReportModel model = this.getModel(id, super.getOrgi(request),publishedid);
			if(model!=null) {
				map.addAttribute("eltemplet", MainUtils.getTemplate(model.getTempletid()));
			}
			map.addAttribute("element", model);
			map.addAttribute("reportModel", model);
			
			if (model != null && !StringUtils.isBlank(model.getPublishedcubeid())) {
				List<PublishedCube> cubeList = publishedCubeRepository.findByIdAndOrgi(model.getPublishedcubeid() , super.getOrgi(request));
				if(cubeList.size() > 0) {
					PublishedCube cube = cubeList.get(0) ;
					map.addAttribute("cube", cube);
					if (canGetReportData(model, cube.getCube())) {
						ReportData reportData = null ;
						try {
							reportData = reportCubeService.getReportData(model, cube.getCube(), request, true, semap) ;
							map.addAttribute("reportData",reportData);
						}catch(Exception ex) {
							map.addAttribute("msg",ex.getMessage());
						}
					}
				}
			}
		}
		return request(super.createRequestPageTempletResponse("/apps/business/report/design/elementajax"));
	}
	private ReportModel getModel(String id,String orgi,String publishedid) {
		if(!StringUtils.isBlank(publishedid)) {
			PublishedReport publishedReport = publishedReportRes.findById(publishedid);
			if(publishedReport!=null) {
				if(publishedReport.getReport()!=null && !publishedReport.getReport().getReportModels().isEmpty()) {
					for(ReportModel rm :publishedReport.getReport().getReportModels()) {
						if(rm.getId().equals(id)) {
							return rm;
						}
					}
				}
				return this.getModel(id, orgi);
			}else{
				return this.getModel(id, orgi);
			}
		}else {
			return this.getModel(id, orgi);
		}
		
	}

	/**
	 * 编辑报表过滤器
	 * 
	 * @param map
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/rfilteredit")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView rfilteredit(ModelMap map, HttpServletRequest request, @Valid String fid) throws Exception {
		map.addAttribute("sysdicList", sysDicRes.findByParentid("0")) ;
		if (!StringUtils.isBlank(fid)) {
			ReportFilter rf = reportFilterRepository.findByIdAndOrgi(fid, super.getOrgi(request));
			if(rf!=null) {
				map.addAttribute("fktableList", metadataRes.findByOrgi(super.getOrgi(request)));
				map.put("fktableidList", tablePropertiesRes.findByDbtableid(rf.getFktableid()));
				if(!StringUtils.isBlank(rf.getCascadeid())) {
					ReportFilter rfcas = reportFilterRepository.findByIdAndOrgi(rf.getCascadeid(), super.getOrgi(request));
					if(rfcas!=null) {
						map.put("fktableiddivList", tablePropertiesRes.findByDbtableid(rfcas.getFktableid()));
					}
				}
			}
			map.addAttribute("reportFilter", rf);
			map.addAttribute("reportFilters", reportCubeService.fillReportFilterData(reportFilterRepository.findByReportidAndFiltertypeAndOrgi(rf.getReportid(), "report", super.getOrgi(request)),request));
		}
		return request(super.createRequestPageTempletResponse("/apps/business/report/design/filteredit"));
	}
	/**
	 * 编辑过滤器
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@RequestMapping("/rfilterupdate")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView rfilterupdate(ModelMap map, HttpServletRequest request, @Valid ReportFilter f,@Valid String tbppy) {
		String reportId = "";
		if (!StringUtils.isBlank(f.getId())) {
			ReportFilter rf = reportFilterRepository.findByIdAndOrgi(f.getId(), super.getOrgi(request));
			if (rf != null) {
				reportId = rf.getReportid();
				rf.setTitle(f.getTitle());
				rf.setCode(f.getCode());
				rf.setModeltype(f.getModeltype());
				rf.setConvalue(f.getConvalue());
				rf.setValuefiltertype(f.getValuefiltertype());
				rf.setComparetype(f.getComparetype());
				rf.setDefaultvalue(f.getDefaultvalue());
				rf.setStartvalue(f.getStartvalue());
				rf.setEndvalue(f.getEndvalue());
				rf.setFormatstr(f.getFormatstr());
				rf.setMustvalue(f.getMustvalue());
				
				rf.setTableid(f.getTableid());
				rf.setFieldid(f.getFieldid());
				rf.setFktableid(f.getFktableid());
				rf.setFkfieldid(f.getFkfieldid());
				rf.setFilterfieldid(f.getFilterfieldid());
				
				if(MainContext.FilterConValueType.AUTO.toString().equals(f.getConvalue()) && MainContext.FilterModelType.SIGSEL.toString().equals(f.getModeltype())) {
					rf.setCascadeid(f.getCascadeid());
					rf.setTableproperty(null);
					rf.setIsdic(f.isIsdic());
					rf.setDiccode(f.getDiccode());
					rf.setKeyfield(f.getKeyfield());
					rf.setValuefield(f.getValuefield());
					if(!StringUtils.isBlank(tbppy)) {
						TableProperties t = new TableProperties();
						t.setId(tbppy);
						rf.setTableproperty(t);
					}
				}else {
					rf.setCascadeid(null);
					rf.setTableproperty(null);
					rf.setIsdic(false);
					rf.setDiccode("");
					rf.setKeyfield("");
					rf.setValuefield("");
				}
				reportFilterRepository.save(rf);
			}
		}
		return request(super.createRequestPageTempletResponse(
				"redirect:/apps/report/design/index.html?id=" + reportId));
	}
	/**
	 * 编辑模型过滤器
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@RequestMapping("/filteredit")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView filteredit(ModelMap map, HttpServletRequest request, @Valid String fid) {
		map.addAttribute("sysdicList", sysDicRes.findByParentid("0")) ;
		if (!StringUtils.isBlank(fid)) {
			ReportFilter rf = reportFilterRepository.findByIdAndOrgi(fid, super.getOrgi(request));
			if(rf!=null) {
				if (!StringUtils.isBlank(rf.getCubeid())) {
					PublishedCube cube = publishedCubeRepository.findOne(rf.getCubeid());
					map.addAttribute("cube", cube);
					List<MetadataTable> metadataTable = new ArrayList<>();
					for(CubeMetadata cm:cube.getCube().getMetadata()) {
						if("0".equals(cm.getMtype())) {
							map.addAttribute("table",cm.getTb());
							map.addAttribute("fieldList",cm.getTb().getTableproperty());
						}
						metadataTable.add(cm.getTb());
					}
					if(!StringUtils.isBlank(rf.getCascadeid())) {
						ReportFilter rfcas = reportFilterRepository.findByIdAndOrgi(rf.getCascadeid(), super.getOrgi(request));
						if(rfcas!=null) {
							map.put("fktableiddivList", tablePropertiesRes.findByDbtableid(rfcas.getFktableid()));
						}
					}
					map.addAttribute("fktableList",metadataTable);
					map.put("fktableidList", tablePropertiesRes.findByDbtableid(rf.getFktableid()));
				}
				ReportModel model = this.getModel(rf.getModelid(), super.getOrgi(request));
				map.addAttribute("reportModel", model);
			}
			map.addAttribute("reportFilter", rf);
		}
		return request(super.createRequestPageTempletResponse("/apps/business/report/design/modeldesign/filteredit"));
	}
	/**
	 * 编辑过滤器
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@RequestMapping("/filterupdate")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView filterupdate(ModelMap map, HttpServletRequest request, @Valid ReportFilter f,@Valid String tbppy) {
		String modelId = "";
		if (!StringUtils.isBlank(f.getId())) {
			ReportFilter rf = reportFilterRepository.findByIdAndOrgi(f.getId(), super.getOrgi(request));
			if (rf != null) {
				modelId = rf.getModelid();
				rf.setTitle(f.getTitle());
				rf.setCode(f.getCode());
				rf.setModeltype(f.getModeltype());
				rf.setConvalue(f.getConvalue());
				rf.setValuefiltertype(f.getValuefiltertype());
				rf.setComparetype(f.getComparetype());
				rf.setDefaultvalue(f.getDefaultvalue());
				rf.setStartvalue(f.getStartvalue());
				rf.setEndvalue(f.getEndvalue());
				rf.setFormatstr(f.getFormatstr());
				rf.setMustvalue(f.getMustvalue());
				
				rf.setTableid(f.getTableid());
				rf.setFieldid(f.getFieldid());
				rf.setFktableid(f.getFktableid());
				rf.setFkfieldid(f.getFkfieldid());
				rf.setFilterfieldid(f.getFilterfieldid());
				
				if(MainContext.FilterConValueType.AUTO.toString().equals(f.getConvalue()) && MainContext.FilterModelType.SIGSEL.toString().equals(f.getModeltype())) {
					rf.setCascadeid(f.getCascadeid());
					rf.setTableproperty(null);
					rf.setIsdic(f.isIsdic());
					rf.setDiccode(f.getDiccode());
					rf.setKeyfield(f.getKeyfield());
					rf.setValuefield(f.getValuefield());
					if(!StringUtils.isBlank(tbppy)) {
						TableProperties t = new TableProperties();
						t.setId(tbppy);
						rf.setTableproperty(t);
					}
				}else {
					rf.setCascadeid(null);
					rf.setTableproperty(null);
					rf.setIsdic(false);
					rf.setDiccode("");
					rf.setKeyfield("");
					rf.setValuefield("");
				}
				reportFilterRepository.save(rf);
			}
		}
		return request(super.createRequestPageTempletResponse(
				"redirect:/apps/report/design/modeldesign.html?id=" + modelId + "&tabid=filter"));
	}
	 @RequestMapping("/fktableid")
	    @Menu(type = "report" , subtype = "reportdesign" , admin= true)
	    public ModelAndView fktableid(ModelMap map , HttpServletRequest request , @Valid String fid,@Valid String fkId) {
		 if (!StringUtils.isBlank(fid)) {
				ReportFilter rf = reportFilterRepository.findByIdAndOrgi(fid, super.getOrgi(request));
				if(rf!=null) {
					if(!StringUtils.isBlank(fkId)) {
						ReportFilter rfcas = reportFilterRepository.findByIdAndOrgi(fkId, super.getOrgi(request));
						if(rfcas!=null) {
							map.put("fktableiddivList", tablePropertiesRes.findByDbtableid(rfcas.getFktableid()));
						}
					}
				}
				map.addAttribute("reportFilter", rf);
			}
	    	return request(super.createRequestPageTempletResponse("/apps/business/report/design/modeldesign/fktableiddiv"));
	    }
	/**
	 * 编辑过滤器
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@RequestMapping("/filterdel")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView filterdel(ModelMap map, HttpServletRequest request, @Valid String id) {
		String modelId = "";
		if (!StringUtils.isBlank(id)) {
			ReportFilter rf = reportFilterRepository.findByIdAndOrgi(id, super.getOrgi(request));
			if (rf != null) {
				modelId = rf.getModelid();
				reportFilterRepository.delete(rf);
			}
		}
		return request(super.createRequestPageTempletResponse(
				"redirect:/apps/report/design/modeldesign.html?id=" + modelId + "&tabid=filter"));
	}
	/**
	 * 编辑过滤器
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@RequestMapping("/rfilterdel")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView rfilterdel(ModelMap map, HttpServletRequest request, @Valid String id) {
		String reportId = "";
		if (!StringUtils.isBlank(id)) {
			ReportFilter rf = reportFilterRepository.findByIdAndOrgi(id, super.getOrgi(request));
			if (rf != null) {
				reportId = rf.getReportid();
				reportFilterRepository.delete(rf);
			}
		}
		return request(super.createRequestPageTempletResponse(
				"redirect:/apps/report/design/index.html?id=" + reportId));
	}
	/**
	 * 排序
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@RequestMapping("/sort")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView sort(ModelMap map, HttpServletRequest request,@Valid String modelId,@Valid String type, @Valid String[] sort, @Valid String[] colsort, @Valid String[] rowsort) {
		String tabid = "data";
		if (!StringUtils.isBlank(type)) {
			if("dim".equals(type) || "measure".equals(type)) {
				if(sort!=null && sort.length > 0 ) {
					int index = 1;
					for(String id:sort) {
						ColumnProperties col = columnPropertiesRepository.findByIdAndOrgi(id,super.getOrgi(request));
						if(col!=null) {
							col.setSortindex(index);
							columnPropertiesRepository.save(col);
							index++;
						}
					}
				}
				if(colsort!=null && colsort.length > 0 ) {
					int index = 1;
					for(String id:colsort) {
						ColumnProperties col = columnPropertiesRepository.findByIdAndOrgi(id,super.getOrgi(request));
						if(col!=null) {
							col.setSortindex(index);
							col.setCur("cfield");
							columnPropertiesRepository.save(col);
							index++;
						}
					}
				}
				if(rowsort!=null && rowsort.length > 0 ) {
					int index = 1;
					for(String id:rowsort) {
						ColumnProperties col = columnPropertiesRepository.findByIdAndOrgi(id,super.getOrgi(request));
						if(col!=null) {
							col.setSortindex(index);
							col.setCur("field");
							columnPropertiesRepository.save(col);
							index++;
						}
					}
				}
			}else {
				tabid = "filter";
				if(sort!=null && sort.length > 0 ) {
					int index = 1;
					for(String id:sort) {
						ReportFilter rf = reportFilterRepository.findByIdAndOrgi(id,super.getOrgi(request));
						if(rf!=null) {
							rf.setSortindex(index);
							reportFilterRepository.save(rf);
							index++;
						}
					}
				}
			}
		}
		return request(super.createRequestPageTempletResponse(
				"redirect:/apps/report/design/modeldesign.html?id=" + modelId + "&tabid="+tabid));
	}
	
	/**
	 * 移除维度或指标
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@RequestMapping("/columndel")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView columndel(ModelMap map, HttpServletRequest request, @Valid String id) {
		String modelId = "";
		if (!StringUtils.isBlank(id)) {
			ColumnProperties col = columnPropertiesRepository.findByIdAndOrgi(id, super.getOrgi(request));
			if (col != null) {
				modelId = col.getModelid();
				columnPropertiesRepository.delete(col);
			}
		}
		return request(super.createRequestPageTempletResponse(
				"redirect:/apps/report/design/modeldesign.html?id=" + modelId + "&tabid=data"));
	}
	
	/**
	 * 修改指标
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@RequestMapping("/columnedit")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView columnedit(ModelMap map, HttpServletRequest request, @Valid String id) {
		if (!StringUtils.isBlank(id)) {
			ColumnProperties col = columnPropertiesRepository.findByIdAndOrgi(id, super.getOrgi(request));
			if (col != null) {
				map.put("col", col) ;
			}
		}
		return request(super.createRequestPageTempletResponse("/apps/business/report/design/modeldesign/measureedit"));
	}
	
	/**
	 * 保存指标
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@RequestMapping("/columnupdate")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView columnupdte(ModelMap map, HttpServletRequest request, @Valid String id,@Valid String title, @Valid String mid) {
		if (!StringUtils.isBlank(id) && !StringUtils.isBlank(title)) {
			ColumnProperties col = columnPropertiesRepository.findByIdAndOrgi(id, super.getOrgi(request));
			if (col != null) {
				col.setTitle(title);
				columnPropertiesRepository.save(col) ;
			}
		}
		return request(super.createRequestPageTempletResponse(
				"redirect:/apps/report/design/modeldesign.html?id=" + mid + "&tabid=data"));
	}

	@RequestMapping("/changetpl")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView changetpl(ModelMap map, HttpServletRequest request, @Valid String mid, @Valid String tplid) throws Exception {
		ReportModel model = this.getModel(mid, super.getOrgi(request));
		if (!StringUtils.isBlank(tplid)) {
			model.setTempletid(tplid);
			Template tp = templateRes.findByIdAndOrgi(tplid, super.getOrgi(request));
			ChartProperties oldChartppy = model.getChartProperties();
			oldChartppy.setChartype(tp.getCharttype());
			Base64 base64 = new Base64();
			model.setChartcontent(base64.encodeToString(MainUtils.toBytes(oldChartppy))) ;
			reportModelRes.save(model);
		}
		return request(super.createRequestPageTempletResponse(
				"redirect:/apps/report/design/modeldesign.html?id=" + model.getId() + "&tabid=data"));
	}
	
	@RequestMapping("/changechartppy")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView changechartppy(ModelMap map,HttpServletRequest request, @Valid ReportModel reportModel, @Valid ChartProperties chartProperties,  HashMap<String,String> semap) throws Exception {
		ReportModel model = this.getModel(reportModel.getId(), super.getOrgi(request));
		if (null!=model) {
			model.setExchangerw(reportModel.isExchangerw());
			model.setIsloadfulldata("true".equals(reportModel.getIsloadfulldata())?"true":"false");
			model.setPagesize(reportModel.getPagesize());
			ChartProperties oldChartppy = model.getChartProperties();
			oldChartppy = oldChartppy==null? new ChartProperties():oldChartppy;
			oldChartppy.setLegen(chartProperties.isLegen());
			oldChartppy.setLegenalign(chartProperties.getLegenalign());
			oldChartppy.setDataview(chartProperties.isDataview());
			oldChartppy.setFormat(StringUtils.isBlank(chartProperties.getFormat())?"val":chartProperties.getFormat());
			Base64 base64 = new Base64();
			model.setChartcontent(base64.encodeToString(MainUtils.toBytes(oldChartppy))) ;
			reportModelRes.save(model);
		}
		map.addAttribute("eltemplet", templateRes.findByIdAndOrgi(model.getTempletid(), super.getOrgi(request)));
		map.addAttribute("element", model);
		map.addAttribute("reportModel", model);
		if (model != null && !StringUtils.isBlank(model.getPublishedcubeid())) {
			PublishedCube cube = publishedCubeRepository.findOne(model.getPublishedcubeid());
			map.addAttribute("cube", cube);
			if (!model.getMeasures().isEmpty()) {
				map.addAttribute("reportData",reportCubeService.getReportData(model, cube.getCube(), request, true, semap));
			}
		}
		return request(super.createRequestPageTempletResponse("/apps/business/report/design/elementajax"));
	}
	
	/**
	 * 组件设计
	 * 
	 * @param map
	 * @param request
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/filtervalchange")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView filtervalchange(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String fid,@Valid String publishedid)
			throws Exception {
		if(StringUtils.isBlank(publishedid)) {
			ReportFilter filter = reportFilterRepository.findByIdAndOrgi(fid, super.getOrgi(request));
			if(filter!=null) {
				if("report".equals(filter.getFiltertype())) {
					ReportModel model = new ReportModel();
					List<ReportFilter> reportFilterList = reportFilterRepository.findByReportidAndFiltertypeAndOrgi(filter.getReportid(), "report", super.getOrgi(request));
					model.setFilters(reportFilterList);
					map.addAttribute("filter", reportCubeService.processFilter(model, filter,null, request));
				}else {
					ReportModel model = this.getModel(id, super.getOrgi(request));
					if (model!=null && !StringUtils.isBlank(fid) && !StringUtils.isBlank(model.getPublishedcubeid())) {
						PublishedCube cube = publishedCubeRepository.findOne(model.getPublishedcubeid());
						map.addAttribute("filter", reportCubeService.processFilter(model, filter, cube.getCube(), request));
					}
				}
			}
		}else {
			PublishedReport publishedReport = publishedReportRes.findById(publishedid);
			if(publishedReport!=null) {
				map.addAttribute("publishedReport", publishedReport);
				ReportFilter filter =  null;
				for(ReportFilter f : publishedReport.getReport().getReportFilters()) {
					if(!StringUtils.isBlank(fid) && f.getId().equals(fid)) {
						filter = f;
						break;
					}
				}
				ReportModel model = null;
				for(ReportModel rm:publishedReport.getReport().getReportModels()) {
					if(id.equals(rm.getId())) {
						model = rm;
					}
					for(ReportFilter f:rm.getFilters()) {
						if(!StringUtils.isBlank(fid) && f.getId().equals(fid)) {
							filter = f;
							break;
						}
					}
				}
				if(filter!=null) {
					if("report".equals(filter.getFiltertype())) {
						ReportModel modelr = new ReportModel();
						List<ReportFilter> reportFilterList = publishedReport.getReport().getReportFilters();
						modelr.setFilters(reportFilterList);
						map.addAttribute("filter", reportCubeService.processFilter(modelr, filter,null, request));
					}else {
						if (model!=null && !StringUtils.isBlank(fid) && !StringUtils.isBlank(model.getPublishedcubeid())) {
							PublishedCube cube = publishedCubeRepository.findOne(model.getPublishedcubeid());
							map.addAttribute("filter", reportCubeService.processFilter(model, filter, cube.getCube(), request));
						}
					}
				}
			}
		}
		
		return request(super.createRequestPageTempletResponse("/apps/business/report/design/modeldesign/filter"));
	}
	@RequestMapping("/editmodelname")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView editmodelname(ModelMap map,HttpServletRequest request, @Valid String id, @Valid String name) {
		map.addAttribute("id", id);
		map.addAttribute("name", name);
		return request(super.createRequestPageTempletResponse(
				"/apps/business/report/design/modeldesign/editmodelname"));
	}
	@RequestMapping("/updatemodelname")
	@Menu(type = "report", subtype = "reportdesign")
	public ModelAndView updatemodelname(ModelMap map,HttpServletRequest request,@Valid String name, @Valid String id) {
		ReportModel model = this.getModel(id, super.getOrgi(request));
		if(!StringUtils.isBlank(name)) {
			model.setName(name);
			reportModelRes.save(model);
		}
		return request(super.createRequestPageTempletResponse(
				"redirect:/apps/report/design/modeldesign.html?id=" + model.getId() + "&tabid=data"));
	}
	/**
	 * 报表发布页面加载
	 * @param request
	 * @param cubeid
	 * @return
	 * @throws Exception
	 */
    @RequestMapping("/reportpublish")
    @Menu(type = "report" , subtype = "reportdesign" )
    public ModelAndView reportpublish(ModelMap map ,HttpServletRequest request , @Valid String reportid) throws Exception{  
		map.put("reportid", reportid);
		return request(super.createRequestPageTempletResponse("/apps/business/report/reportpublish"));
    }
	
	/**
	 * 报表发布
	 * @param request
	 * @param reportid
	 * @return
	 * @throws Exception
	 */
    @RequestMapping("/reportpublished")
    @Menu(type = "report" , subtype = "reportdesign" )
    public ModelAndView reportpublished(ModelMap map ,HttpServletRequest request , @Valid String reportid,@Valid String isRecover) throws Exception{  
    	User user = super.getUser(request);
    	if(!StringUtils.isBlank(reportid)) {
    		Report report =  reportRes.findByIdAndOrgi(reportid, super.getOrgi(request));
    		List<ReportModel> reportModels =  reportModelRes.findByOrgiAndReportid(super.getOrgi(request), reportid);
    		
    		for(ReportModel r:reportModels){
    			getModel(r.getId(), super.getOrgi(request));
    		}
    		report.setReportModels(reportModels);
    		List<ReportFilter> reportFilters = reportCubeService.fillReportFilterData(reportFilterRepository.findByReportidAndFiltertypeAndOrgi(reportid, "report", super.getOrgi(request)),request);
    		report.setReportFilters(reportFilters);
    		PublishedReport publishedReport  = new PublishedReport();
    		MainUtils.copyProperties(report, publishedReport, "");
    		publishedReport.setId(null);
        	Base64 base64 = new Base64();
        	publishedReport.setReportcontent(base64.encodeToString(MainUtils.toBytes(report))) ;
        	publishedReport.setDataid(reportid);
        	publishedReport.setCreatetime(new Date());
        	publishedReport.setCreater(user.getId());
        	List<PublishedReport> pbReportList = publishedReportRes.findByOrgiAndDataidOrderByDataversionDesc(super.getOrgi(request), reportid);
        	if(!pbReportList.isEmpty()){
        		int maxVersion = pbReportList.get(0).getDataversion() ;
        		if("yes".equals(isRecover)){
        			publishedReport.setId(pbReportList.get(0).getId()) ;
        			publishedReport.setDataversion(pbReportList.get(0).getDataversion());
        			publishedReportRes.save(publishedReport);
            	}else if("no".equals(isRecover)){
            		publishedReport.setDataversion(maxVersion+1) ;
            		publishedReportRes.save(publishedReport);
            	}else{
            		publishedReportRes.delete(pbReportList);
            		publishedReport.setDataversion(1) ;
            		publishedReportRes.save(publishedReport);
            	}
        	}else{
        		publishedReport.setDataversion(1) ;
        		publishedReportRes.save(publishedReport);
        	}
        	return request(super.createRequestPageTempletResponse("redirect:/apps/report/index.html?dicid="+publishedReport.getDicid()));
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/apps/report/index.html"));
    }
}