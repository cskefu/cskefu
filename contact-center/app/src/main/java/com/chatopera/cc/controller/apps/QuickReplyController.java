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
package com.chatopera.cc.controller.apps;

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.MetadataTable;
import com.chatopera.cc.model.QuickReply;
import com.chatopera.cc.persistence.es.QuickReplyRepository;
import com.chatopera.cc.persistence.repository.MetadataRepository;
import com.chatopera.cc.persistence.repository.QuickTypeRepository;
import com.chatopera.cc.persistence.repository.ReporterRepository;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.dsdata.DSData;
import com.chatopera.cc.util.dsdata.DSDataEvent;
import com.chatopera.cc.util.dsdata.ExcelImportProecess;
import com.chatopera.cc.util.dsdata.export.ExcelExporterProcess;
import com.chatopera.cc.util.dsdata.process.QuickReplyProcess;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
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
@RequestMapping("/setting/quickreply")
@RequiredArgsConstructor
public class QuickReplyController extends Handler {

    @NonNull
    private final QuickReplyRepository quickReplyRes;

    @NonNull
    private final QuickTypeRepository quickTypeRes;

    @NonNull
    private final MetadataRepository metadataRes;

    @NonNull
    private final ReporterRepository reporterRes;

    @Value("${web.upload-path}")
    private String path;

    @RequestMapping("/index")
    @Menu(type = "setting", subtype = "quickreply", admin = true)
    public ModelAndView index(ModelMap map, HttpServletRequest request, @Valid String typeid) {
        List<com.chatopera.cc.model.QuickType> quickTypeList = quickTypeRes.findByOrgiAndQuicktype(super.getOrgi(request), MainContext.QuickType.PUB.toString());
        if (!StringUtils.isBlank(typeid)) {
            map.put("quickType", quickTypeRes.findByIdAndOrgi(typeid, super.getOrgi(request)));
            map.put("quickReplyList", quickReplyRes.getByOrgiAndCate(super.getOrgi(request), typeid, null, PageRequest.of(super.getP(request), super.getPs(request))));
        } else {
            map.put("quickReplyList", quickReplyRes.getByOrgiAndType(super.getOrgi(request), MainContext.QuickType.PUB.toString(), null, PageRequest.of(super.getP(request), super.getPs(request))));
        }
        map.put("pubQuickTypeList", quickTypeList);
        return request(super.createAppsTempletResponse("/apps/setting/quickreply/index"));
    }

    @RequestMapping("/replylist")
    @Menu(type = "setting", subtype = "quickreply", admin = true)
    public ModelAndView list(ModelMap map, HttpServletRequest request, @Valid String typeid) {
        if (!StringUtils.isBlank(typeid) && !typeid.equals("0")) {
            map.put("quickReplyList", quickReplyRes.getByOrgiAndCate(super.getOrgi(request), typeid, null, PageRequest.of(super.getP(request), super.getPs(request))));
        } else {
            map.put("quickReplyList", quickReplyRes.getByOrgiAndType(super.getOrgi(request), MainContext.QuickType.PUB.toString(), null, PageRequest.of(super.getP(request), super.getPs(request))));
        }
        map.put("quickType", quickTypeRes.findByIdAndOrgi(typeid, super.getOrgi(request)));
        return request(super.createRequestPageTempletResponse("/apps/setting/quickreply/replylist"));
    }

    @RequestMapping("/add")
    @Menu(type = "setting", subtype = "quickreplyadd", admin = true)
    public ModelAndView quickreplyadd(ModelMap map, HttpServletRequest request, @Valid String parentid) {
        if (!StringUtils.isBlank(parentid)) {
            map.addAttribute("quickType", quickTypeRes.findByIdAndOrgi(parentid, super.getOrgi(request)));
        }
        map.addAttribute("quickTypeList", quickTypeRes.findByOrgiAndQuicktype(super.getOrgi(request), MainContext.QuickType.PUB.toString()));
        return request(super.createRequestPageTempletResponse("/apps/setting/quickreply/add"));
    }

    @RequestMapping("/save")
    @Menu(type = "setting", subtype = "quickreply", admin = true)
    public ModelAndView quickreplysave(HttpServletRequest request, @Valid QuickReply quickReply) {
        if (!StringUtils.isBlank(quickReply.getTitle()) && !StringUtils.isBlank(quickReply.getContent())) {
            quickReply.setOrgi(super.getOrgi(request));
            quickReply.setCreater(super.getUser(request).getId());
            quickReply.setType(MainContext.QuickType.PUB.toString());
            quickReplyRes.save(quickReply);
        }
        return request(super.createRequestPageTempletResponse("redirect:/setting/quickreply/index.html?typeid=" + quickReply.getCate()));
    }

    @RequestMapping("/delete")
    @Menu(type = "setting", subtype = "quickreply", admin = true)
    public ModelAndView quickreplydelete(@Valid String id) {
        QuickReply quickReply = quickReplyRes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Quick reply %s not found", id)));
        quickReplyRes.delete(quickReply);
        return request(super.createRequestPageTempletResponse("redirect:/setting/quickreply/index.html?typeid=" + quickReply.getCate()));
    }

    @RequestMapping("/edit")
    @Menu(type = "setting", subtype = "quickreply", admin = true)
    public ModelAndView quickreplyedit(ModelMap map, HttpServletRequest request, @Valid String id) {
        QuickReply quickReply = quickReplyRes.findById(id).orElse(null);
        map.put("quickReply", quickReply);
        if (quickReply != null) {
            map.put("quickType", quickTypeRes.findByIdAndOrgi(quickReply.getCate(), super.getOrgi(request)));
        }
        map.addAttribute("quickTypeList", quickTypeRes.findByOrgiAndQuicktype(super.getOrgi(request), MainContext.QuickType.PUB.toString()));
        return request(super.createRequestPageTempletResponse("/apps/setting/quickreply/edit"));
    }

    @RequestMapping("/update")
    @Menu(type = "setting", subtype = "quickreply", admin = true)
    public ModelAndView quickreplyupdate(HttpServletRequest request, @Valid QuickReply quickReply) {
        String id = quickReply.getId();
        if (!StringUtils.isBlank(id)) {
            QuickReply temp = quickReplyRes.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Quick reply %s not found", id)));
            quickReply.setOrgi(super.getOrgi(request));
            quickReply.setCreater(super.getUser(request).getId());
            if (temp != null) {
                quickReply.setCreatetime(temp.getCreatetime());
            }
            quickReply.setType(MainContext.QuickType.PUB.toString());
            quickReplyRes.save(quickReply);
        }
        return request(super.createRequestPageTempletResponse("redirect:/setting/quickreply/index.html?typeid=" + quickReply.getCate()));
    }

    @RequestMapping({"/addtype"})
    @Menu(type = "apps", subtype = "kbs")
    public ModelAndView addtype(ModelMap map, HttpServletRequest request, @Valid String typeid) {
        map.addAttribute("quickTypeList", quickTypeRes.findByOrgiAndQuicktype(super.getOrgi(request), MainContext.QuickType.PUB.toString()));
        if (!StringUtils.isBlank(typeid)) {
            map.addAttribute("quickType", quickTypeRes.findByIdAndOrgi(typeid, super.getOrgi(request)));
        }
        return request(super.createRequestPageTempletResponse("/apps/setting/quickreply/addtype"));
    }

    @RequestMapping("/type/save")
    @Menu(type = "apps", subtype = "kbs")
    public ModelAndView typesave(HttpServletRequest request, @Valid com.chatopera.cc.model.QuickType quickType) {
        com.chatopera.cc.model.QuickType qr = quickTypeRes.findByOrgiAndName(super.getOrgi(request), quickType.getName());
        if (qr == null) {
            quickType.setOrgi(super.getOrgi(request));
            quickType.setCreater(super.getUser(request).getId());
            quickType.setCreatetime(new Date());
            quickType.setQuicktype(MainContext.QuickType.PUB.toString());
            quickTypeRes.save(quickType);
        } else {
            return request(super.createRequestPageTempletResponse("redirect:/setting/quickreply/index.html?msg=qr_type_exist"));
        }
        return request(super.createRequestPageTempletResponse("redirect:/setting/quickreply/index.html"));
    }

    @RequestMapping({"/edittype"})
    @Menu(type = "apps", subtype = "kbs")
    public ModelAndView edittype(ModelMap map, HttpServletRequest request, String id) {
        map.addAttribute("quickType", quickTypeRes.findByIdAndOrgi(id, super.getOrgi(request)));
        map.addAttribute("quickTypeList", quickTypeRes.findByOrgiAndQuicktype(super.getOrgi(request), MainContext.QuickType.PUB.toString()));
        return request(super.createRequestPageTempletResponse("/apps/setting/quickreply/edittype"));
    }

    @RequestMapping("/type/update")
    @Menu(type = "apps", subtype = "kbs")
    public ModelAndView typeupdate(HttpServletRequest request, @Valid com.chatopera.cc.model.QuickType quickType) {
        com.chatopera.cc.model.QuickType tempQuickType = quickTypeRes.findByIdAndOrgi(quickType.getId(), super.getOrgi(request));
        if (tempQuickType != null) {
            //判断名称是否重复
            com.chatopera.cc.model.QuickType qr = quickTypeRes.findByOrgiAndName(super.getOrgi(request), quickType.getName());
            if (qr != null && !qr.getId().equals(quickType.getId())) {
                return request(super.createRequestPageTempletResponse("redirect:/setting/quickreply/index.html?msg=qr_type_exist&typeid=" + quickType.getId()));
            }
            tempQuickType.setName(quickType.getName());
            tempQuickType.setDescription(quickType.getDescription());
            tempQuickType.setInx(quickType.getInx());
            tempQuickType.setParentid(quickType.getParentid());
            quickTypeRes.save(tempQuickType);
        }
        return request(super.createRequestPageTempletResponse("redirect:/setting/quickreply/index.html?typeid=" + quickType.getId()));
    }

    @RequestMapping({"/deletetype"})
    @Menu(type = "apps", subtype = "kbs")
    public ModelAndView deletetype(HttpServletRequest request, @Valid String id) {
        if (!StringUtils.isBlank(id)) {
            com.chatopera.cc.model.QuickType tempQuickType = quickTypeRes.findByIdAndOrgi(id, super.getOrgi(request));
            quickTypeRes.delete(tempQuickType);

            Page<QuickReply> quickReplyList = quickReplyRes.getByOrgiAndCate(super.getOrgi(request), id, null, PageRequest.of(0, 10000));

            quickReplyRes.deleteAll(quickReplyList.getContent());
        }
        return request(super.createRequestPageTempletResponse("redirect:/setting/quickreply/index.html"));
    }

    @RequestMapping("/imp")
    @Menu(type = "setting", subtype = "quickreplyimp")
    public ModelAndView imp(ModelMap map, @Valid String type) {
        map.addAttribute("type", type);
        return request(super.createRequestPageTempletResponse("/apps/setting/quickreply/imp"));
    }

    @RequestMapping("/impsave")
    @Menu(type = "setting", subtype = "quickreplyimpsave")
    public ModelAndView impsave(HttpServletRequest request, @RequestParam(value = "cusfile", required = false) MultipartFile cusfile, @Valid String type) throws IOException {
        DSDataEvent event = new DSDataEvent();
        String originalFilename = Objects.requireNonNull(cusfile.getOriginalFilename());
        String fileName = "quickreply/" + MainUtils.getUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
        File excelFile = new File(path, fileName);
        if (!excelFile.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            excelFile.getParentFile().mkdirs();
        }
        MetadataTable table = metadataRes.findByTablename("uk_quickreply");
        if (table != null) {
            FileUtils.writeByteArrayToFile(new File(path, fileName), cusfile.getBytes());
            event.setDSData(new DSData(table, excelFile, cusfile.getContentType(), super.getUser(request)));
            event.getDSData().setClazz(QuickReply.class);
            event.setOrgi(super.getOrgi(request));
            if (!StringUtils.isBlank(type)) {
                event.getValues().put("cate", type);
            } else {
                event.getValues().put("cate", Constants.DEFAULT_TYPE);
            }
            event.getValues().put("type", MainContext.QuickType.PUB.toString());
            event.getValues().put("creater", super.getUser(request).getId());
            event.getDSData().setProcess(new QuickReplyProcess(quickReplyRes));
            reporterRes.save(event.getDSData().getReport());
            new ExcelImportProecess(event).process();        //启动导入任务
        }

        return request(super.createRequestPageTempletResponse("redirect:/setting/quickreply/index.html" + (!StringUtils.isBlank(type) ? "?typeid=" + type : "")));
    }

    @RequestMapping("/batdelete")
    @Menu(type = "setting", subtype = "quickreplybatdelete")
    public ModelAndView batdelete(@Valid String[] ids, @Valid String type) {
        if (ids != null && ids.length > 0) {
            Iterable<QuickReply> topicList = quickReplyRes.findAllById(Arrays.asList(ids));
            quickReplyRes.deleteAll(topicList);
        }

        return request(super.createRequestPageTempletResponse("redirect:/setting/quickreply/index.html" + (!StringUtils.isBlank(type) ? "?typeid=" + type : "")));
    }

    @RequestMapping("/expids")
    @Menu(type = "setting", subtype = "quickreplyexpids")
    public void expids(HttpServletResponse response, @Valid String[] ids) throws IOException {
        if (ids != null && ids.length > 0) {
            Iterable<QuickReply> topicList = quickReplyRes.findAllById(Arrays.asList(ids));
            MetadataTable table = metadataRes.findByTablename("uk_quickreply");
            List<Map<String, Object>> values = new ArrayList<>();
            for (QuickReply topic : topicList) {
                values.add(MainUtils.transBean2Map(topic));
            }

            response.setHeader("content-disposition", "attachment;filename=UCKeFu-QuickReply-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xls");
            if (table != null) {
                ExcelExporterProcess excelProcess = new ExcelExporterProcess(values, table, response.getOutputStream());
                excelProcess.process();
            }
        }

    }

    @RequestMapping("/expall")
    @Menu(type = "setting", subtype = "quickreplyexpall")
    public void expall(HttpServletRequest request, HttpServletResponse response, @Valid String type) throws IOException {
        Iterable<QuickReply> topicList = quickReplyRes.getQuickReplyByOrgi(super.getOrgi(request), !StringUtils.isBlank(type) ? type : null, MainContext.QuickType.PUB.toString(), null);

        MetadataTable table = metadataRes.findByTablename("uk_quickreply");
        List<Map<String, Object>> values = new ArrayList<>();
        for (QuickReply topic : topicList) {
            values.add(MainUtils.transBean2Map(topic));
        }

        response.setHeader("content-disposition", "attachment;filename=UCKeFu-QuickReply-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xls");

        if (table != null) {
            ExcelExporterProcess excelProcess = new ExcelExporterProcess(values, table, response.getOutputStream());
            excelProcess.process();
        }
    }

    @RequestMapping("/expsearch")
    @Menu(type = "setting", subtype = "quickreplyexpsearch")
    public void expall(HttpServletRequest request, HttpServletResponse response, @Valid String q, @Valid String type) throws IOException {

        Iterable<QuickReply> topicList = quickReplyRes.getQuickReplyByOrgi(super.getOrgi(request), type, MainContext.QuickType.PUB.toString(), q);

        MetadataTable table = metadataRes.findByTablename("uk_quickreply");
        List<Map<String, Object>> values = new ArrayList<>();
        for (QuickReply topic : topicList) {
            values.add(MainUtils.transBean2Map(topic));
        }

        response.setHeader("content-disposition", "attachment;filename=UCKeFu-QuickReply-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xls");

        if (table != null) {
            ExcelExporterProcess excelProcess = new ExcelExporterProcess(values, table, response.getOutputStream());
            excelProcess.process();
        }
    }
}
