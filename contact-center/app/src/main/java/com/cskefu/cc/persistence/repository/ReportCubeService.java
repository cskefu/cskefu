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
package com.cskefu.cc.persistence.repository;

import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.model.*;
import com.cskefu.cc.util.bi.ReportData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("reportCubeService")
public class ReportCubeService {

    @Autowired
    private TemplateRepository templateRes;

    @Value("${web.upload-path}")
    private String path;

    @Autowired
    private DataSourceService dataSource;

    @Autowired
    private ReportFilterRepository reportFilterRes;

    @Autowired
    private MetadataRepository metadataRes;

    /**
     * @param report
     * @return
     * @throws Exception
     */
    public ReportData getReportData(ReportModel model, Cube cube, HttpServletRequest request, boolean parseParam, HashMap<String, String> semap) throws Exception {

        processFilter(model, cube, request);

        Template modeltp = templateRes.findByIdAndOrgi(model.getTempletid(), model.getOrgi());
        boolean isTable = modeltp != null && "数据表".equals(modeltp.getName());

        cube.setSql(createCubeSQL(model, cube, request, true, semap));

        Template tp = null;

        List<SysDic> tpDicList = Dict.getInstance().getDic(Constants.CSKEFU_SYSTEM_DIC);
        for (SysDic sysDic : tpDicList) {
            if (sysDic.getCode().equals("reportquery")) {
                List<Template> tpList = templateRes.findByTemplettypeAndOrgi(sysDic.getId(), model.getOrgi());
                for (Template tpl : tpList) {
                    if ("reportquery".equals(tpl.getCode())) {
                        tp = tpl;
                        break;
                    }
                }
            }
        }
        Map<String, Object> tplValuesMap = new HashMap<>();
        tplValuesMap.put("reportModel", model);
        tplValuesMap.put("cube", cube);
        tplValuesMap.put("name", model.getName());
        tplValuesMap.put("istable", isTable);
        CubeService cubeService = new CubeService(tp.getTemplettext(), path, dataSource, tplValuesMap, true);
        String mdx = this.genMdx(model, cube, isTable, request);
        ReportData reportData = cubeService.execute(mdx, model.getMeasures());
        if ("true".equals(model.getIsloadfulldata())) {
            int p = Integer
                    .parseInt(request.getParameter("p") != null
                            && request.getParameter("p").matches(
                            "[\\d]{1,}") ? request
                            .getParameter("p") : "1");
            int ps = Integer
                    .parseInt(request.getParameter("ps") != null
                            && request.getParameter("ps").matches(
                            "[\\d]{1,}") ? request
                            .getParameter("ps") : "0");
            if (p < 1) {
                p = 1;
            }
            if (ps < 1) {
                ps = model.getPagesize() > 0 ? model.getPagesize() : 20;
            }
            reportData.setPage(p);
            reportData.setPageSize(ps);
        }

        return reportData;
    }


    /**
     * 生成结算数据SQL
     *
     * @param model
     * @param cube
     * @param request
     * @param queryLog
     * @return
     * @throws Exception
     */
    public String createCubeSQL(ReportModel model, Cube cube,
                                HttpServletRequest request, boolean useStaticFilter, HashMap<String, String> semap) throws Exception {
        StringBuffer strb = new StringBuffer();
        strb.append("select ");
        //要查询的表名以及左连接
        StringBuffer tables = new StringBuffer();
        StringBuffer mainTable = new StringBuffer(" from ");
        String mainTableStr = null;
        Map<String, String> tableMap = new HashMap<>();
        int index = 1;
        Map<String, TableProperties> tableppyMap = new HashMap<>();
        Map<String, MetadataTable> tableObjMap = new HashMap<>();
        CubeMetadata mainMetaData = null;
        for (CubeMetadata cm : cube.getMetadata()) {
            tableMap.put(cm.getTb().getId(), "a" + index);
            if ("0".equals(cm.getMtype())) {
                mainMetaData = cm;
                mainTable.append(cm.getTb().getTablename()).append(" as ").append(tableMap.get(cm.getTb().getId())).append(" ");
                mainTableStr = cm.getTb().getId();
            }
            for (TableProperties tableproperty : cm.getTb().getTableproperty()) {
                tableppyMap.put(tableproperty.getId(), tableproperty);
            }
            tableObjMap.put(cm.getTb().getId(), cm.getTb());
            index++;
        }
        //要查询的列名
        Map<String, String> exist = new HashMap<String, String>();
        StringBuffer columns = new StringBuffer();
        for (Dimension d : cube.getDimension()) {
            for (CubeLevel cl : d.getCubeLevel()) {
                if (!exist.containsKey(cl.getColumname()) && !mainMetaData.getTb().getTablename().equals(cl.getTablename())) {
                    if (columns.length() > 0) {
                        columns.append(",");
                    }
                    columns.append(tableMap.get(cl.getTableproperty().getDbtableid())).append(".").append(cl.getTableproperty().getFieldname()).append(" ");
                    exist.put(cl.getColumname(), cl.getColumname());
                }
            }
        }

        //要查询的表名以及左连接
        for (CubeMetadata cm : cube.getMetadata()) {
            if (!"0".equals(cm.getMtype())) {
                for (Dimension d : cube.getDimension()) {
                    if (!StringUtils.isBlank(d.getFkfield()) && !StringUtils.isBlank(d.getFktable()) && !StringUtils.isBlank(d.getFktableid()) && cm.getTb().getId().equals(d.getFktable())) {
                        tables.append(" left join ").append(cm.getTb().getTablename()).append(" as ").append(tableMap.get(cm.getTb().getId())).append(" on ");
                        StringBuffer onsql = new StringBuffer();
                        onsql.append(tableMap.get(mainTableStr)).append(".").append(tableppyMap.get(d.getFkfield()).getFieldname());
                        onsql.append("=").append(tableMap.get(d.getFktable())).append(".").append(tableppyMap.get(d.getFktableid()).getFieldname());
                        onsql.append(" ");
                        tables.append(onsql);
                    }

                }
            } else {

            }
        }
        if (mainMetaData != null) {
            strb.append(" ").append(tableMap.get(mainMetaData.getTb().getId())).append(".* ");
            if (columns.length() > 0) {
                strb.append(",").append(columns.toString());
            }
            strb.append(" ").append(mainTable).append(tables);
        } else {
            strb.append(" * ").append(mainTable).append(tables);
        }
        //过滤关联的表名以及左连接
        StringBuffer filtertables = new StringBuffer();
        StringBuffer wherecon = new StringBuffer();
        List<ReportFilter> reportFilter = model.getFilters();
        Map<String, String> tablefilterMap = new HashMap<>();
        if (!reportFilter.isEmpty()) {
            //左链接 过滤器关联的表
            index = 1;
            for (ReportFilter filter : reportFilter) {
                tablefilterMap.put(filter.getFktableid() + filter.getId(), "b" + index);
                index++;
            }
            for (ReportFilter filter : reportFilter) {
                filtertables.append(" left join ").append(tableObjMap.get(filter.getFktableid()).getTablename()).append(" as ").append(tablefilterMap.get(filter.getFktableid() + filter.getId())).append(" on ");
                StringBuffer onsql = new StringBuffer();
                onsql.append(tableMap.get(mainTableStr)).append(".").append(tableppyMap.get(filter.getFieldid()).getFieldname());
                onsql.append("=").append(tablefilterMap.get(filter.getFktableid() + filter.getId())).append(".").append(tableppyMap.get(filter.getFkfieldid()).getFieldname());
                onsql.append(" ");
                filtertables.append(onsql);
                if ("range".equals(filter.getValuefiltertype())) {//范围   range code_start 和 code_end
                    if ((!StringUtils.isBlank(filter.getCode()))) {
                        String startValue = getStartValue(filter, request);
                        String endValue = getEndValue(filter, request);
                        String dataname = tablefilterMap.get(filter.getFktableid() + filter.getId()) + "." + tableppyMap.get(filter.getFilterfieldid()).getFieldname();

                        if (!StringUtils.isBlank(startValue)) {
                            wherecon.append(wherecon.length() > 0 ? " and " : "").append(dataname).append(" >= '").append(startValue).append("' ");
                        }
                        if (!StringUtils.isBlank(endValue)) {
                            wherecon.append(wherecon.length() > 0 ? " and " : "").append(dataname).append(" <= '").append(endValue).append("' ");
                        }
                    }
                } else {//compare
                    String value = getDefaultValue(filter, request);
                    String dataname = tablefilterMap.get(filter.getFktableid() + filter.getId()) + "." + tableppyMap.get(filter.getFilterfieldid()).getFieldname();
                    if (!StringUtils.isBlank(value)) {
                        if (!wherecon.toString().contains(dataname)) {
                            if (!StringUtils.isBlank(value)) {
                                if (wherecon.length() > 0) {
                                    wherecon.append(" and ");
                                }
                                if ("EQUAL".equals(filter.getComparetype().toUpperCase())) {
                                    if (value.indexOf(",") > -1) {
                                        wherecon.append(dataname.toUpperCase()).append(" in ('").append(value.replaceAll(",", "','")).append("') ");
                                    } else {
                                        wherecon.append(dataname.toUpperCase()).append(" = '").append(value).append("' ");
                                    }
                                } else if ("NOT".equals(filter.getComparetype().toUpperCase())) {
                                    if (value.indexOf(",") > -1) {
                                        wherecon.append(dataname.toUpperCase()).append(" not in ('").append(value.replaceAll(",", "','")).append("') ");
                                    } else {
                                        wherecon.append(dataname.toUpperCase()).append(" != '").append(value).append("' ");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (filtertables.length() > 0) {
            strb.append(filtertables);
        }
        if (wherecon.length() > 0) {
            strb.append(" where ").append(wherecon);
        }
        return strb.length() > 0 ? strb.toString() : null;
    }

    public boolean checkSemap(String fieldename, CubeMetadata mainMetaData) {
        List<TableProperties> mainlist = mainMetaData.getTb().getTableproperty();
        for (TableProperties tableProperties : mainlist) {
            if (tableProperties.getFieldname().equals(fieldename)) {
                return true;
            }
        }
        return false;
    }

    public String genMdx(ReportModel model, Cube cube, boolean isTable, HttpServletRequest request) {
        StringBuffer rowstrb = new StringBuffer(), colstrb = new StringBuffer();
        StringBuffer dimstrb = new StringBuffer();
        StringBuffer coldimstrb = new StringBuffer();
        //维度 只需要最后一个
        if (!model.getProperties().isEmpty()) {
            ColumnProperties cp = model.getProperties().get(model.getProperties().size() - 1);
            all:
            for (Dimension d : cube.getDimension()) {
                for (CubeLevel cl : d.getCubeLevel()) {
                    if (cp.getDataid().equals(cl.getId())) {
                        dimstrb.append(" [").append(model.getName()).append("].[").append(cl.getName()).append("].members ");
                        break all;
                    }
                }
            }
        }
        if ("true".equals(model.getIsloadfulldata())) {
            int p = Integer
                    .parseInt(request.getParameter("p") != null
                            && request.getParameter("p").matches(
                            "[\\d]{1,}") ? request
                            .getParameter("p") : "1");
            int ps = Integer
                    .parseInt(request.getParameter("ps") != null
                            && request.getParameter("ps").matches(
                            "[\\d]{1,}") ? request
                            .getParameter("ps") : "0");
            if (p < 1) {
                p = 1;
            }
            if (ps < 1) {
                ps = model.getPagesize() > 0 ? model.getPagesize() : 20;
            }
            int startindex = (p - 1) * ps;
            dimstrb.insert(0, "subset( ").append(",").append(startindex).append(" , ").append(ps).append(" ) ");
        }
        rowstrb.append(dimstrb).append(" ON ROWS");
        //指标
        StringBuffer measure = new StringBuffer();
        for (ColumnProperties cp : model.getMeasures()) {
            for (CubeMeasure cm : cube.getMeasure()) {
                if (cp.getDataid().equals(cm.getId())) {
                    measure.append(" [Measures].[").append(cm.getName()).append("] ,");
                }
            }
        }
        if (measure.length() > 1) {
            measure.deleteCharAt(measure.length() - 1);
        }
        measure.insert(0, " {");
        measure.append(" }");

        //列维度
        if (isTable) {
            //维度 只需要最后一个
            if (!model.getColproperties().isEmpty()) {
                ColumnProperties cp = model.getColproperties().get(model.getColproperties().size() - 1);
                all:
                for (Dimension d : cube.getDimension()) {
                    for (CubeLevel cl : d.getCubeLevel()) {
                        if (cp.getDataid().equals(cl.getId())) {
                            coldimstrb.append(" [").append(model.getName()).append("_col].[").append(cl.getName()).append("].members ");
                            break all;
                        }
                    }
                }
            }
            if (coldimstrb.length() > 0) {
                colstrb.append("CrossJoin(").append(coldimstrb).append(" , ").append(measure).append(" ) ").append(" ON COLUMNS");
            } else {
                colstrb.append(measure).append(" ON COLUMNS");
            }
        } else {
            colstrb.append(measure).append(" ON COLUMNS");
        }

        StringBuffer strb = new StringBuffer();
        strb.append("SELECT ").append(colstrb);
        if (dimstrb.length() > 0) {
            strb.append(" , ").append(rowstrb).append(" FROM [").append(cube.getName()).append("]");
        } else {
            strb.append(" FROM [").append(cube.getName()).append("]");
        }
        //行列交换
        if (model.isExchangerw()) {
            if (strb.indexOf(" ON COLUMNS") < 0 && strb.indexOf(" ON ROWS") >= 0) {
                strb.replace(strb.indexOf(" ON ROWS"), strb.indexOf(" ON ROWS") + " ON ROWS".length(), " ON COLUMNS");
            }
            if (strb.indexOf(" ON COLUMNS") >= 0 && strb.indexOf(" ON ROWS") >= 0) {
                int colindex = strb.indexOf(" ON COLUMNS");
                int rowindex = strb.indexOf(" ON ROWS");
                strb.replace(rowindex, rowindex + " ON ROWS".length(), " ON COLUMNS");
                strb.replace(colindex, colindex + " ON COLUMNS".length(), " ON ROWS");
            }
        }
        return strb.toString();
    }
	
	
	/*public void fillFilterValue(ReportModel model,ReportFilter filter,HttpServletRequest request ) {
		
	}*/

    public void processReportFilter(Cube cube, ReportFilter filter, HttpServletRequest request) throws Exception {
        if (filter != null) {
            createCubeFilter(filter, cube, request);
        }
    }

    public void processFilter(ReportModel model, Cube cube, HttpServletRequest request) throws Exception {
        if (model != null && !model.getFilters().isEmpty()) {
            for (ReportFilter filter : model.getFilters()) {
                if ("range".equals(filter.getValuefiltertype())) {
                    filter.setRequeststartvalue(request.getParameter(filter.getCode() + "_start"));
                    filter.setRequestendvalue(request.getParameter(filter.getCode() + "_end"));

                    filter.setRequeststartvalue(getStartValue(filter, request));
                    filter.setRequestendvalue(getEndValue(filter, request));

                } else {
                    filter.setRequestvalue(request.getParameter(filter.getCode()));

                    filter.setRequestvalue(getDefaultValue(filter, request));
                }
            }
            for (ReportFilter filter : model.getFilters()) {
                processReportFilter(cube, filter, request);
            }
        }
    }

    public ReportFilter processFilter(ReportModel model, ReportFilter curFilter, Cube cube, HttpServletRequest request) throws Exception {
        if (model != null && !model.getFilters().isEmpty()) {
            for (ReportFilter filter : model.getFilters()) {
                if ("range".equals(filter.getValuefiltertype())) {
                    filter.setRequeststartvalue(request.getParameter(filter.getCode() + "_start"));
                    filter.setRequestendvalue(request.getParameter(filter.getCode() + "_end"));
                } else {
                    filter.setRequestvalue(request.getParameter(filter.getCode()));
                }
            }
            return createCubeFilter(curFilter, cube, request);
        }
        return null;
    }

    /**
     * 获取默认的 参数
     *
     * @param filter
     * @return
     * @throws TemplateException
     * @throws IOException
     */
    public static String getDefaultValue(ReportFilter filter, HttpServletRequest request) {
        String value = filter.getDefaultvalue();
        if (value != null && value.matches("[ ]{0,}[TtMmYy]{1,}[ ]{0,}[+-]{0,1}([\\d]{0,})")) {//处理动态参数的问题 ， Y表示 年 ， 如 Y+1 ， M表示 月 ， 如：M+1 ， T表示 日 ， 如 T+1 ， 例如，Y-1 = 2013 ， M-1 = 8
            value = MainUtils.processParam(filter.getFormatstr(), value);
        }
        value = StringUtils.isBlank(filter.getRequestvalue()) ? value : filter.getRequestvalue();
        return value;
    }

    /**
     * 获取默认的 参数
     *
     * @param filter
     * @return
     */
    public static String getStartValue(ReportFilter filter, HttpServletRequest request) {
        String startValue = filter.getStartvalue();
        if (startValue != null && startValue.matches("[ ]{0,}[TtMmYy]{1,}[ ]{0,}[+-]{0,1}([\\d]{0,})")) {//处理动态参数的问题 ， Y表示 年 ， 如 Y+1 ， M表示 月 ， 如：M+1 ， T表示 日 ， 如 T+1 ， 例如，Y-1 = 2013 ， M-1 = 8
            startValue = MainUtils.processParam(filter.getFormatstr(), startValue);
        }
        return StringUtils.isBlank(filter.getRequeststartvalue()) ? startValue : filter.getRequeststartvalue();
    }

    /**
     * 获取默认的 参数
     *
     * @param filter
     * @return
     */
    public static String getEndValue(ReportFilter filter, HttpServletRequest request) {
        String endValue = filter.getEndvalue();
        if (endValue != null && endValue.matches("[ ]{0,}[TtMmYy]{1,}[ ]{0,}[+-]{0,1}([\\d]{0,})")) {//处理动态参数的问题 ， Y表示 年 ， 如 Y+1 ， M表示 月 ， 如：M+1 ， T表示 日 ， 如 T+1 ， 例如，Y-1 = 2013 ， M-1 = 8
            endValue = MainUtils.processParam(filter.getFormatstr(), endValue);
        }
        return StringUtils.isBlank(filter.getRequestendvalue()) ? endValue : filter.getRequestendvalue();
    }

    /**
     * 生成过滤器自动获取数据SQL
     *
     * @param model
     * @param cube
     * @param request
     * @param queryLog
     * @return
     * @throws Exception
     */
    public ReportFilter createCubeFilter(ReportFilter filter, Cube cube, HttpServletRequest request) throws Exception {
        if (MainContext.FilterConValueType.AUTO.toString().equals(filter.getConvalue()) && MainContext.FilterModelType.SIGSEL.toString().equals(filter.getModeltype())) {
            Map<String, MetadataTable> tableMap = new HashMap<>();
            Map<String, String> tableIndexMap = new HashMap<>();
            Map<String, TableProperties> tableppyMap = new HashMap<>();
            int index = 1;
            if (cube != null) {
                for (CubeMetadata cm : cube.getMetadata()) {
                    tableMap.put(cm.getTb().getId(), cm.getTb());
                    tableIndexMap.put(cm.getTb().getId(), "a" + index);
                    for (TableProperties tableproperty : cm.getTb().getTableproperty()) {
                        tableppyMap.put(tableproperty.getId(), tableproperty);
                    }
                    index++;
                }
            } else {
                for (MetadataTable cm : metadataRes.findByOrgi(filter.getOrgi())) {
                    tableMap.put(cm.getId(), cm);
                    tableIndexMap.put(cm.getId(), "a" + index);
                    for (TableProperties tableproperty : cm.getTableproperty()) {
                        tableppyMap.put(tableproperty.getId(), tableproperty);
                    }
                    index++;
                }
            }
            MetadataTable table = tableMap.get(filter.getFktableid());
            TableProperties keyfield = tableppyMap.get(filter.getKeyfield());
            TableProperties valuefield = tableppyMap.get(filter.getValuefield());
            if (table != null && keyfield != null && valuefield != null && StringUtils.isNotBlank(filter.getFktableid()) && StringUtils.isNotBlank(filter.getKeyfield()) && StringUtils.isNotBlank(filter.getValuefield())) {
                StringBuffer strb = new StringBuffer();
                strb.append("select ");
                //要查询的表名以及左连接
                StringBuffer mainTable = new StringBuffer(" from ");

                mainTable.append(table.getTablename()).append(" as ").append(tableIndexMap.get(filter.getFktableid()));
                //要查询的列名
                StringBuffer columns = new StringBuffer();
                columns.append(tableIndexMap.get(filter.getFktableid())).append(".").append(keyfield.getFieldname()).append(" KEYVAL,");
                columns.append(tableIndexMap.get(filter.getFktableid())).append(".").append(valuefield.getFieldname()).append(" VAL");

                StringBuffer wherecon = new StringBuffer(" 1=1 ");
                wherecon.append(" and " + tableIndexMap.get(filter.getFktableid()) + ".orgi = ").append("'").append(filter.getOrgi()).append("' ");

                StringBuffer leftjoin = new StringBuffer();
                //查询所有级联上级
                List<ReportFilter> rfList = reportFilterRes.findByCascadeidAndOrgi(filter.getId(), filter.getOrgi());
                //字典项
                //if(filter.isIsdic() && (rfList==null || rfList.isEmpty())) {
                if (filter.isIsdic()) {
                    leftjoin.append(" left join uk_sysdic as b1 on b1.id = ").append(tableIndexMap.get(filter.getFktableid())).append(".id ");
                    wherecon.append(" and b1.dicid = (select id from uk_sysdic where code = ").append("'").append(filter.getDiccode()).append("') ");
                    if (rfList == null || rfList.isEmpty()) {
                        wherecon.append(" and b1.parentid = (select id from uk_sysdic where code = ").append("'").append(filter.getDiccode()).append("') ");
                    }
                }
                if (rfList != null && !rfList.isEmpty()) {
                    for (ReportFilter f : rfList) {
                        if ("range".equals(f.getValuefiltertype())) {
                            f.setRequeststartvalue(request.getParameter(f.getCode() + "_start"));
                            f.setRequestendvalue(request.getParameter(f.getCode() + "_end"));
                        } else {
                            f.setRequestvalue(request.getParameter(f.getCode()));
                        }
                    }
                    Map<String, String> tablefilterMap = new HashMap<>();
                    for (ReportFilter f : rfList) {
                        tablefilterMap.put(f.getFktableid() + f.getId(), "c" + index);
                        index++;
                    }
                    for (ReportFilter f : rfList) {
                        leftjoin.append(" left join ").append((tableMap.get(f.getFktableid()).getTablename())).append(" as ").append(tablefilterMap.get(f.getFktableid() + f.getId())).append(" on ").append(tablefilterMap.get(f.getFktableid() + f.getId())).append(".").append((tableppyMap.get(f.getKeyfield()).getFieldname())).append(" = ").append(tableIndexMap.get(filter.getFktableid())).append(".").append(f.getTableproperty().getFieldname());
                        wherecon.append(" and ").append(tablefilterMap.get(f.getFktableid() + f.getId())).append(".").append((tableppyMap.get(f.getKeyfield()).getFieldname())).append(" = '").append(getDefaultValue(f, request)).append("' ");
                    }
                }
                strb.append(columns).append(mainTable).append(leftjoin);
                if (wherecon.length() > 0) {
                    strb.append(" where ").append(wherecon);
                }
                StringBuffer rowstrb = new StringBuffer();
                rowstrb.append(" {[KEYVAL].[KEYVAL].members,[KEYVAL].[VAL].members} ");
                rowstrb.append(" ON COLUMNS");
				/*StringBuffer colstrb = new StringBuffer();
				colstrb.append(" [VAL].[VAL].members ");
				colstrb.append(" ON COLUMNS");*/

                StringBuffer strMdx = new StringBuffer();
                strMdx.append("SELECT ")/*.append(colstrb).append(" , ")*/.append(rowstrb).append(" FROM [filterquery]");
                Template tp = null;
                List<SysDic> tpDicList = Dict.getInstance().getDic(Constants.CSKEFU_SYSTEM_DIC);
                for (SysDic sysDic : tpDicList) {
                    if (sysDic.getCode().equals("reportquery")) {
                        List<Template> tpList = templateRes.findByTemplettypeAndOrgi(sysDic.getId(), filter.getOrgi());
                        for (Template tpl : tpList) {
                            if ("filterquery".equals(tpl.getCode())) {
                                tp = tpl;
                                break;
                            }
                        }
                    }
                }
                Map<String, Object> tplValuesMap = new HashMap<>();
                tplValuesMap.put("sql", strb.toString());
                CubeService cubeService = new CubeService(tp.getTemplettext(), path, dataSource, tplValuesMap, true);
                ReportData reportData = cubeService.execute(strMdx.toString());
                filter.setReportData(reportData);
                return filter;
            }
        }
        return filter;
    }

    /**
     * 填充报表过滤器的值以及下拉值
     *
     * @param report
     * @return
     * @throws Exception
     */
    public List<ReportFilter> fillReportFilterData(List<ReportFilter> reportFilterList, HttpServletRequest request) throws Exception {
        ReportModel model = new ReportModel();
        model.setFilters(reportFilterList);
        processFilter(model, null, request);
        return model.getFilters();
    }
}