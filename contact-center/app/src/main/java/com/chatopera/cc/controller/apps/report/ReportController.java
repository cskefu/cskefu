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
import com.chatopera.cc.util.dsdata.DSData;
import com.chatopera.cc.util.dsdata.DSDataEvent;
import com.chatopera.cc.util.dsdata.ExcelImportProecess;
import com.chatopera.cc.util.dsdata.export.ExcelExporterProcess;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/apps/report")
public class ReportController extends Handler {

    @NonNull
    private final DataDicRepository dataDicRes;
    @NonNull
    private final ReportRepository reportRes;
    @NonNull
    private final PublishedReportRepository publishedReportRes;
    @NonNull
    private final MetadataRepository metadataRes;
    @NonNull
    private final ReportCubeService reportCubeService;
    @Value("${web.upload-path}")
    private String path;

    @RequestMapping("/index")
    @Menu(type = "setting", subtype = "report", admin = true)
    public ModelAndView index(ModelMap map, HttpServletRequest request, @Valid String dicid) {
        if (!StringUtils.isBlank(dicid) && !"0".equals(dicid)) {
            map.put("dataDic", dataDicRes.findByIdAndOrgi(dicid, super.getOrgi(request)));
            map.put("reportList", reportRes.findByOrgiAndDicid(super.getOrgi(request), dicid, PageRequest.of(super.getP(request), super.getPs(request))));
        } else {
            map.put("reportList", reportRes.findByOrgi(super.getOrgi(request), PageRequest.of(super.getP(request), super.getPs(request))));
        }
        map.put("dataDicList", dataDicRes.findByOrgi(super.getOrgi(request)));
        return request(super.createAppsTempletResponse("/apps/business/report/index"));
    }

    @RequestMapping("/add")
    @Menu(type = "setting", subtype = "reportadd", admin = true)
    public ModelAndView quickreplyadd(ModelMap map, HttpServletRequest request, @Valid String dicid) {
        if (!StringUtils.isBlank(dicid)) {
            map.addAttribute("dataDic", dataDicRes.findByIdAndOrgi(dicid, super.getOrgi(request)));
        }
        map.addAttribute("dataDicList", dataDicRes.findByOrgi(super.getOrgi(request)));
        return request(super.createRequestPageTempletResponse("/apps/business/report/add"));
    }

    @RequestMapping("/save")
    @Menu(type = "setting", subtype = "report", admin = true)
    public ModelAndView quickreplysave(HttpServletRequest request, @Valid Report report) {
        ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/apps/report/index.html?dicid=" + report.getDicid()));
        if (!StringUtils.isBlank(report.getName())) {
            int count = reportRes.countByOrgiAndName(super.getOrgi(request), report.getName());
            if (count == 0) {
                report.setOrgi(super.getOrgi(request));
                report.setCreater(super.getUser(request).getId());
                report.setReporttype(MainContext.ReportType.REPORT.toString());
                report.setCode(MainUtils.genID());
                reportRes.save(report);
            } else {
                view = request(super.createRequestPageTempletResponse("redirect:/apps/report/index.html?msg=rt_name_exist&dicid=" + report.getDicid()));
            }
        }
        return view;
    }

    @RequestMapping("/delete")
    @Menu(type = "setting", subtype = "report", admin = true)
    public ModelAndView quickreplydelete(@Valid String id) {
        Report report = reportRes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Report %s not found", id)));
        reportRes.delete(report);
        return request(super.createRequestPageTempletResponse("redirect:/apps/report/index.html?dicid=" + report.getDicid()));
    }

    @RequestMapping("/edit")
    @Menu(type = "setting", subtype = "report", admin = true)
    public ModelAndView quickreplyedit(ModelMap map, HttpServletRequest request, @Valid String id) {
        Report report = reportRes.findById(id).orElse(null);
        map.put("report", report);
        if (report != null) {
            map.put("dataDic", dataDicRes.findByIdAndOrgi(report.getDicid(), super.getOrgi(request)));
        }
        map.addAttribute("dataDicList", dataDicRes.findByOrgi(super.getOrgi(request)));
        return request(super.createRequestPageTempletResponse("/apps/business/report/edit"));
    }

    @RequestMapping("/update")
    @Menu(type = "setting", subtype = "report", admin = true)
    public ModelAndView quickreplyupdate(@Valid Report report) {
        if (!StringUtils.isBlank(report.getId())) {
            reportRes.findById(report.getId()).ifPresent(temp -> {
                temp.setName(report.getName());
                temp.setCode(report.getCode());
                temp.setDicid(report.getDicid());
                temp.setUpdatetime(new Date());
                temp.setDescription(report.getDescription());
                reportRes.save(temp);
            });
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/report/index.html?dicid=" + report.getDicid()));
    }

    @RequestMapping({"/addtype"})
    @Menu(type = "apps", subtype = "kbs")
    public ModelAndView addtype(ModelMap map, HttpServletRequest request, @Valid String dicid) {
        map.addAttribute("dataDicList", dataDicRes.findByOrgi(super.getOrgi(request)));
        if (!StringUtils.isBlank(dicid)) {
            map.addAttribute("dataDic", dataDicRes.findByIdAndOrgi(dicid, super.getOrgi(request)));
        }
        return request(super.createRequestPageTempletResponse("/apps/business/report/addtype"));
    }

    @RequestMapping("/type/save")
    @Menu(type = "apps", subtype = "report")
    public ModelAndView typesave(HttpServletRequest request, @Valid DataDic dataDic) {
        List<DataDic> dicList = dataDicRes.findByOrgiAndName(super.getOrgi(request), dataDic.getName());
        if (dicList != null && dicList.size() > 0) {
            return request(super.createRequestPageTempletResponse("redirect:/apps/report/index.html?dicid=" + dataDic.getParentid() + "&msg=qr_type_exist"));
        } else {
            dataDic.setOrgi(super.getOrgi(request));
            dataDic.setCreater(super.getUser(request).getId());
            dataDic.setCreatetime(new Date());
            dataDic.setTabtype(MainContext.QuickType.PUB.toString());
            dataDicRes.save(dataDic);
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/report/index.html?dicid=" + dataDic.getId()));
    }

    @RequestMapping({"/edittype"})
    @Menu(type = "apps", subtype = "kbs")
    public ModelAndView edittype(ModelMap map, HttpServletRequest request, String id) {
        DataDic dataDic = dataDicRes.findByIdAndOrgi(id, super.getOrgi(request));
        map.addAttribute("dataDic", dataDic);
        if (dataDic != null) {
            map.addAttribute("parentDataDic", dataDicRes.findByIdAndOrgi(dataDic.getParentid(), super.getOrgi(request)));
        }
        map.addAttribute("dataDicList", dataDicRes.findByOrgi(super.getOrgi(request)));
        return request(super.createRequestPageTempletResponse("/apps/business/report/edittype"));
    }

    @RequestMapping("/type/update")
    @Menu(type = "apps", subtype = "report")
    public ModelAndView typeupdate(HttpServletRequest request, @Valid DataDic dataDic) {
        ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/apps/report/index.html?dicid=" + dataDic.getId()));
        DataDic tempDataDic = dataDicRes.findByIdAndOrgi(dataDic.getId(), super.getOrgi(request));
        if (tempDataDic != null) {
            //判断名称是否重复
            List<DataDic> dicList = dataDicRes.findByOrgiAndNameAndIdNot(super.getOrgi(request), dataDic.getName(), dataDic.getId());
            if (dicList != null && dicList.size() > 0) {
                view = request(super.createRequestPageTempletResponse("redirect:/apps/report/index.html?msg=qr_type_exist&dicid=" + dataDic.getId()));
            } else {
                tempDataDic.setName(dataDic.getName());
                tempDataDic.setDescription(dataDic.getDescription());
                tempDataDic.setParentid(dataDic.getParentid());
                dataDicRes.save(tempDataDic);
            }
        }
        return view;
    }

    @RequestMapping({"/deletetype"})
    @Menu(type = "apps", subtype = "kbs")
    public ModelAndView deletetype(HttpServletRequest request, @Valid String id) {
        ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/apps/report/index.html?dicid=" + id));
        if (!StringUtils.isBlank(id)) {
            DataDic tempDataDic = dataDicRes.findByIdAndOrgi(id, super.getOrgi(request));
            int count = reportRes.countByOrgiAndDicid(super.getOrgi(request), id);
            if (count == 0) {
                dataDicRes.delete(tempDataDic);
                view = request(super.createRequestPageTempletResponse("redirect:/apps/report/index.html?dicid=" + tempDataDic.getParentid()));
            } else {
                view = request(super.createRequestPageTempletResponse("redirect:/apps/report/index.html?msg=report_exist&dicid=" + id));
            }
        }
        return view;
    }

    @RequestMapping("/imp")
    @Menu(type = "setting", subtype = "reportimp")
    public ModelAndView imp(ModelMap map, @Valid String type) {
        map.addAttribute("type", type);
        return request(super.createRequestPageTempletResponse("/apps/business/report/imp"));
    }

    @RequestMapping("/impsave")
    @Menu(type = "setting", subtype = "reportimpsave")
    public ModelAndView impsave(HttpServletRequest request, @RequestParam(value = "cusfile", required = false) MultipartFile cusfile, @Valid String type) throws IOException {
        DSDataEvent event = new DSDataEvent();
        String originalFilename = Objects.requireNonNull(cusfile.getOriginalFilename());
        String fileName = "quickreply/" + MainUtils.getUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
        File excelFile = new File(path, fileName);
        if (!excelFile.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            excelFile.getParentFile().mkdirs();
        }
        MetadataTable table = metadataRes.findByTablename("uk_report");
        if (table != null) {
            FileUtils.writeByteArrayToFile(new File(path, fileName), cusfile.getBytes());
            event.setDSData(new DSData(table, excelFile, cusfile.getContentType(), super.getUser(request)));
            event.getDSData().setClazz(Report.class);
            event.setOrgi(super.getOrgi(request));
            if (!StringUtils.isBlank(type)) {
                event.getValues().put("cate", type);
            } else {
                event.getValues().put("cate", Constants.DEFAULT_TYPE);
            }
            event.getValues().put("type", MainContext.QuickType.PUB.toString());
            event.getValues().put("creater", super.getUser(request).getId());
//	    	exchange.getDSData().setProcess(new QuickReplyProcess(reportRes));
//	    	reporterRes.save(exchange.getDSData().getReport()) ;
            new ExcelImportProecess(event).process();        //启动导入任务
        }

        return request(super.createRequestPageTempletResponse("redirect:/apps/report/index.html" + (!StringUtils.isBlank(type) ? "?dicid=" + type : "")));
    }

    @RequestMapping("/batdelete")
    @Menu(type = "setting", subtype = "reportbatdelete")
    public ModelAndView batdelete(@Valid String[] ids, @Valid String type) {
        if (ids != null && ids.length > 0) {
            Iterable<Report> topicList = reportRes.findAllById(Arrays.asList(ids));
            reportRes.deleteAll(topicList);
        }

        return request(super.createRequestPageTempletResponse("redirect:/apps/report/index.html" + (!StringUtils.isBlank(type) ? "?dicid=" + type : "")));
    }

    @RequestMapping("/expids")
    @Menu(type = "setting", subtype = "reportexpids")
    public void expids(HttpServletResponse response, @Valid String[] ids) throws IOException {
        if (ids != null && ids.length > 0) {
            Iterable<Report> topicList = reportRes.findAllById(Arrays.asList(ids));
            MetadataTable table = metadataRes.findByTablename("uk_report");
            List<Map<String, Object>> values = new ArrayList<>();
            for (Report topic : topicList) {
                values.add(MainUtils.transBean2Map(topic));
            }

            response.setHeader("content-disposition", "attachment;filename=UCKeFu-Report-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xls");
            if (table != null) {
                ExcelExporterProcess excelProcess = new ExcelExporterProcess(values, table, response.getOutputStream());
                excelProcess.process();
            }
        }

    }

    @RequestMapping("/expall")
    @Menu(type = "setting", subtype = "reportexpall")
    public void expall(HttpServletRequest request, HttpServletResponse response, @Valid String type) throws IOException {
        List<Report> reportList = reportRes.findByOrgiAndDicid(super.getOrgi(request), type);

        MetadataTable table = metadataRes.findByTablename("uk_report");
        List<Map<String, Object>> values = new ArrayList<>();
        for (Report report : reportList) {
            values.add(MainUtils.transBean2Map(report));
        }

        response.setHeader("content-disposition", "attachment;filename=UCKeFu-Report-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xls");

        if (table != null) {
            ExcelExporterProcess excelProcess = new ExcelExporterProcess(values, table, response.getOutputStream());
            excelProcess.process();
        }
    }

    @RequestMapping("/pbreportindex")
    @Menu(type = "setting", subtype = "pbreport", admin = true)
    public ModelAndView pbreportindex(ModelMap map, HttpServletRequest request, @Valid String dicid) {
        if (!StringUtils.isBlank(dicid) && !"0".equals(dicid)) {
            map.put("dataDic", dataDicRes.findByIdAndOrgi(dicid, super.getOrgi(request)));
            map.put("reportList", publishedReportRes.findByOrgiAndDicid(super.getOrgi(request), dicid, PageRequest.of(super.getP(request), super.getPs(request))));
        } else {
            map.put("reportList", publishedReportRes.findByOrgi(super.getOrgi(request), PageRequest.of(super.getP(request), super.getPs(request))));
        }
        map.put("dataDicList", dataDicRes.findByOrgi(super.getOrgi(request)));
        return request(super.createAppsTempletResponse("/apps/business/report/pbreportindex"));
    }

    @RequestMapping("/pbreportlist")
    @Menu(type = "setting", subtype = "pbreport", admin = true)
    public ModelAndView pbreportlist(ModelMap map, HttpServletRequest request, @Valid String dicid) {
        if (!StringUtils.isBlank(dicid) && !"0".equals(dicid)) {
            map.put("dataDic", dataDicRes.findByIdAndOrgi(dicid, super.getOrgi(request)));
            map.put("reportList", publishedReportRes.findByOrgiAndDicid(super.getOrgi(request), dicid, PageRequest.of(super.getP(request), super.getPs(request))));
        } else {
            map.put("reportList", publishedReportRes.findByOrgi(super.getOrgi(request), PageRequest.of(super.getP(request), super.getPs(request))));
        }
        map.put("dataDicList", dataDicRes.findByOrgi(super.getOrgi(request)));
        return request(super.createRequestPageTempletResponse("/apps/business/report/pbreportlist"));
    }

    @RequestMapping("/pbdelete")
    @Menu(type = "setting", subtype = "pbreport", admin = true)
    public ModelAndView pbdelete(@Valid String id) {
        PublishedReport report = publishedReportRes.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Report %s not found", id)));
        publishedReportRes.delete(report);
        return request(super.createRequestPageTempletResponse("redirect:/apps/report/pbreportindex.html?dicid=" + report.getDicid()));
    }

    /**
     * 报表
     */
    @RequestMapping("/view")
    @Menu(type = "report", subtype = "report")
    public ModelAndView view(ModelMap map, HttpServletRequest request, @Valid String id) {
        if (!StringUtils.isBlank(id)) {
            publishedReportRes.findById(id).ifPresent(publishedReport -> {

                map.addAttribute("publishedReport", publishedReport);
                map.addAttribute("report", publishedReport.getReport());
                map.addAttribute("reportModels", publishedReport.getReport().getReportModels());
                List<ReportFilter> listFilters = publishedReport.getReport().getReportFilters();
                if (!listFilters.isEmpty()) {
                    Map<String, ReportFilter> filterMap = new HashMap<>();
                    for (ReportFilter rf : listFilters) {
                        filterMap.put(rf.getId(), rf);
                    }
                    for (ReportFilter rf : listFilters) {
                        if (!StringUtils.isBlank(rf.getCascadeid())) {
                            rf.setChildFilter(filterMap.get(rf.getCascadeid()));
                        }
                    }
                }
                try {
                    List<ReportFilter> list = reportCubeService.fillReportFilterData(listFilters, request);
                    map.addAttribute("reportFilters", list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return request(super.createRequestPageTempletResponse("/apps/business/report/view"));
    }
}
