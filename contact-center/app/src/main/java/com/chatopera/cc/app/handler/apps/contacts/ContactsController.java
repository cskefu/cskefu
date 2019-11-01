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

package com.chatopera.cc.app.handler.apps.contacts;

import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.PinYinTools;
import com.chatopera.cc.app.basic.MainUtils;
import com.chatopera.cc.exception.CSKefuException;
import com.chatopera.cc.concurrent.dsdata.DSData;
import com.chatopera.cc.concurrent.dsdata.DSDataEvent;
import com.chatopera.cc.concurrent.dsdata.ExcelImportProecess;
import com.chatopera.cc.concurrent.dsdata.export.ExcelExporterProcess;
import com.chatopera.cc.concurrent.dsdata.process.ContactsProcess;
import com.chatopera.cc.app.model.Contacts;
import com.chatopera.cc.app.model.MetadataTable;
import com.chatopera.cc.app.model.PropertiesEvent;
import com.chatopera.cc.app.persistence.es.ContactsRepository;
import com.chatopera.cc.app.persistence.repository.MetadataRepository;
import com.chatopera.cc.app.persistence.repository.PropertiesEventRepository;
import com.chatopera.cc.app.persistence.repository.ReporterRepository;
import com.chatopera.cc.util.PropertiesEventUtils;
import com.chatopera.cc.app.handler.Handler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

@Controller
@RequestMapping("/apps/contacts")
public class ContactsController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(ContactsController.class);

    @Autowired
    private ContactsRepository contactsRes;

    @Autowired
    private PropertiesEventRepository propertiesEventRes;

    @Autowired
    private ReporterRepository reporterRes;

    @Autowired
    private MetadataRepository metadataRes;

    @Value("${web.upload-path}")
    private String path;

    @RequestMapping("/index")
    @Menu(type = "customer", subtype = "index")
    public ModelAndView index(ModelMap map, HttpServletRequest request, @Valid String q, @Valid String ckind) throws CSKefuException {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if(!super.esOrganFilter(request, boolQueryBuilder)){
            return request(super.createAppsTempletResponse("/apps/business/contacts/index"));
        }

        if (!StringUtils.isBlank(q)) {
            map.put("q", q);
        }

        if (!StringUtils.isBlank(ckind)) {
            boolQueryBuilder.must(termQuery("ckind", ckind));
            map.put("ckind", ckind);
        }

        map.addAttribute("contactsList", contactsRes.findByCreaterAndSharesAndOrgi(super.getUser(request).getId(), super.getUser(request).getId(), super.getOrgi(request), null, null, false, boolQueryBuilder, q, new PageRequest(super.getP(request), super.getPs(request))));

        return request(super.createAppsTempletResponse("/apps/business/contacts/index"));
    }

    @RequestMapping("/today")
    @Menu(type = "customer", subtype = "today")
    public ModelAndView today(ModelMap map, HttpServletRequest request, @Valid String q, @Valid String ckind) throws CSKefuException {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(!super.esOrganFilter(request, boolQueryBuilder)){
            return request(super.createAppsTempletResponse("/apps/business/contacts/index"));
        }

        if (!StringUtils.isBlank(q)) {
            map.put("q", q);
        }
        if (!StringUtils.isBlank(ckind)) {
            boolQueryBuilder.must(termQuery("ckind", ckind));
            map.put("ckind", ckind);
        }
        map.addAttribute("contactsList", contactsRes.findByCreaterAndSharesAndOrgi(super.getUser(request).getId(), super.getUser(request).getId(), super.getOrgi(request), MainUtils.getStartTime(), null, false, boolQueryBuilder, q, new PageRequest(super.getP(request), super.getPs(request))));

        return request(super.createAppsTempletResponse("/apps/business/contacts/index"));
    }

    @RequestMapping("/week")
    @Menu(type = "customer", subtype = "week")
    public ModelAndView week(ModelMap map, HttpServletRequest request, @Valid String q, @Valid String ckind) throws CSKefuException {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(!super.esOrganFilter(request, boolQueryBuilder)){
            return request(super.createAppsTempletResponse("/apps/business/contacts/index"));
        }

        if (!StringUtils.isBlank(q)) {
            map.put("q", q);
        }
        if (!StringUtils.isBlank(ckind)) {
            boolQueryBuilder.must(termQuery("ckind", ckind));
            map.put("ckind", ckind);
        }
        map.addAttribute("contactsList", contactsRes.findByCreaterAndSharesAndOrgi(super.getUser(request).getId(), super.getUser(request).getId(), super.getOrgi(request), MainUtils.getWeekStartTime(), null, false, boolQueryBuilder, q, new PageRequest(super.getP(request), super.getPs(request))));

        return request(super.createAppsTempletResponse("/apps/business/contacts/index"));
    }

    @RequestMapping("/creater")
    @Menu(type = "customer", subtype = "creater")
    public ModelAndView creater(ModelMap map, HttpServletRequest request, @Valid String q, @Valid String ckind) throws CSKefuException {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(!super.esOrganFilter(request, boolQueryBuilder)){
            return request(super.createAppsTempletResponse("/apps/business/contacts/index"));
        }

        boolQueryBuilder.must(termQuery("creater", super.getUser(request).getId()));

        if (!StringUtils.isBlank(ckind)) {
            boolQueryBuilder.must(termQuery("ckind", ckind));
            map.put("ckind", ckind);
        }
        if (!StringUtils.isBlank(q)) {
            map.put("q", q);
        }

        map.addAttribute("contactsList", contactsRes.findByCreaterAndSharesAndOrgi(super.getUser(request).getId(), super.getUser(request).getId(), super.getOrgi(request), null, null, false, boolQueryBuilder, q, new PageRequest(super.getP(request), super.getPs(request))));
        return request(super.createAppsTempletResponse("/apps/business/contacts/index"));
    }

    @RequestMapping("/delete")
    @Menu(type = "contacts", subtype = "contacts")
    public ModelAndView delete(HttpServletRequest request, @Valid Contacts contacts, @Valid String p) {
        if (contacts != null) {
            contacts = contactsRes.findOne(contacts.getId());
            contacts.setDatastatus(true);                            //客户和联系人都是 逻辑删除
            contactsRes.save(contacts);
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/contacts/index.html?p=" + p + "&ckind=" + contacts.getCkind()));
    }

    @RequestMapping("/add")
    @Menu(type = "contacts", subtype = "add")
    public ModelAndView add(ModelMap map, HttpServletRequest request, @Valid String ckind) {
        map.addAttribute("ckind", ckind);
        return request(super.createRequestPageTempletResponse("/apps/business/contacts/add"));
    }

    @RequestMapping("/save")
    @Menu(type = "contacts", subtype = "save")
    public ModelAndView save(HttpServletRequest request, @Valid Contacts contacts) {
        contacts.setCreater(super.getUser(request).getId());
        contacts.setOrgi(super.getOrgi(request));
        contacts.setOrgan(super.getUser(request).getOrgan());
        contacts.setPinyin(PinYinTools.getInstance().getFirstPinYin(contacts.getName()));
        if (StringUtils.isBlank(contacts.getCusbirthday())) {
            contacts.setCusbirthday(null);
        }
        contactsRes.save(contacts);
        return request(super.createRequestPageTempletResponse("redirect:/apps/contacts/index.html?ckind=" + contacts.getCkind()));
    }

    @RequestMapping("/edit")
    @Menu(type = "contacts", subtype = "contacts")
    public ModelAndView edit(ModelMap map, HttpServletRequest request, @Valid String id) {
        map.addAttribute("contacts", contactsRes.findOne(id));
        return request(super.createRequestPageTempletResponse("/apps/business/contacts/edit"));
    }

    @RequestMapping("/detail")
    @Menu(type = "customer" , subtype = "index")
    public ModelAndView detail(ModelMap map , HttpServletRequest request , @Valid String id) {
    	map.addAttribute("contacts", contactsRes.findOne(id)) ;
        return request(super.createAppsTempletResponse("/apps/business/contacts/detail"));
    }


    @RequestMapping("/update")
    @Menu(type = "contacts", subtype = "contacts")
    public ModelAndView update(HttpServletRequest request, @Valid Contacts contacts) {
        Contacts data = contactsRes.findOne(contacts.getId());
        if (data != null) {
            List<PropertiesEvent> events = PropertiesEventUtils.processPropertiesModify(request, contacts, data, "id", "orgi", "creater", "createtime", "updatetime");    //记录 数据变更 历史
            if (events.size() > 0) {
                String modifyid = MainUtils.getUUID();
                Date modifytime = new Date();
                for (PropertiesEvent event : events) {
                    event.setDataid(contacts.getId());
                    event.setCreater(super.getUser(request).getId());
                    event.setOrgi(super.getOrgi(request));
                    event.setModifyid(modifyid);
                    event.setCreatetime(modifytime);
                    propertiesEventRes.save(event);
                }
            }

            contacts.setCreater(data.getCreater());
            contacts.setCreatetime(data.getCreatetime());
            contacts.setOrgi(super.getOrgi(request));
            contacts.setOrgan(super.getUser(request).getOrgan());
            contacts.setPinyin(PinYinTools.getInstance().getFirstPinYin(contacts.getName()));
            if (StringUtils.isBlank(contacts.getCusbirthday())) {
                contacts.setCusbirthday(null);
            }
            contactsRes.save(contacts);
        }

        return request(super.createRequestPageTempletResponse("redirect:/apps/contacts/index.html?ckind=" + contacts.getCkind()));
    }

    @RequestMapping("/imp")
    @Menu(type = "contacts", subtype = "contacts")
    public ModelAndView imp(ModelMap map, HttpServletRequest request, @Valid String ckind) {
        map.addAttribute("ckind", ckind);
        return request(super.createRequestPageTempletResponse("/apps/business/contacts/imp"));
    }

    @RequestMapping("/impsave")
    @Menu(type = "contacts", subtype = "contacts")
    public ModelAndView impsave(ModelMap map, HttpServletRequest request, @RequestParam(value = "cusfile", required = false) MultipartFile cusfile, @Valid String ckind) throws IOException {
        DSDataEvent event = new DSDataEvent();
        String fileName = "contacts/" + MainUtils.getUUID() + cusfile.getOriginalFilename().substring(cusfile.getOriginalFilename().lastIndexOf("."));
        File excelFile = new File(path, fileName);
        if (!excelFile.getParentFile().exists()) {
            excelFile.getParentFile().mkdirs();
        }
        MetadataTable table = metadataRes.findByTablename("uk_contacts");
        if (table != null) {
            FileUtils.writeByteArrayToFile(new File(path, fileName), cusfile.getBytes());
            event.setDSData(new DSData(table, excelFile, cusfile.getContentType(), super.getUser(request)));
            event.getDSData().setClazz(Contacts.class);
            event.getDSData().setProcess(new ContactsProcess(contactsRes));
            event.setOrgi(super.getOrgi(request));
	    	/*if(!StringUtils.isBlank(ckind)){
	    		exchange.getValues().put("ckind", ckind) ;
	    	}*/
            event.getValues().put("creater", super.getUser(request).getId());
            reporterRes.save(event.getDSData().getReport());
            new ExcelImportProecess(event).process();        //启动导入任务
        }

        return request(super.createRequestPageTempletResponse("redirect:/apps/contacts/index.html"));
    }

    @RequestMapping("/expids")
    @Menu(type = "contacts", subtype = "contacts")
    public void expids(ModelMap map, HttpServletRequest request, HttpServletResponse response, @Valid String[] ids) throws IOException {
        if (ids != null && ids.length > 0) {
            Iterable<Contacts> contactsList = contactsRes.findAll(Arrays.asList(ids));
            MetadataTable table = metadataRes.findByTablename("uk_contacts");
            List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
            for (Contacts contacts : contactsList) {
                values.add(MainUtils.transBean2Map(contacts));
            }

            response.setHeader("content-disposition", "attachment;filename=CSKefu-Contacts-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xls");

            ExcelExporterProcess excelProcess = new ExcelExporterProcess(values, table, response.getOutputStream());
            excelProcess.process();
        }

        return;
    }

    @RequestMapping("/expall")
    @Menu(type = "contacts", subtype = "contacts")
    public void expall(ModelMap map, HttpServletRequest request, HttpServletResponse response) throws IOException, CSKefuException {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(!super.esOrganFilter(request, boolQueryBuilder)){
            return;
        }
        boolQueryBuilder.must(termQuery("datastatus", false));        //只导出 数据删除状态 为 未删除的 数据
        Iterable<Contacts> contactsList = contactsRes.findByCreaterAndSharesAndOrgi(super.getUser(request).getId(), super.getUser(request).getId(), super.getOrgi(request), null, null, false, boolQueryBuilder, null, new PageRequest(super.getP(request), super.getPs(request)));

        MetadataTable table = metadataRes.findByTablename("uk_contacts");
        List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
        for (Contacts contacts : contactsList) {
            values.add(MainUtils.transBean2Map(contacts));
        }

        response.setHeader("content-disposition", "attachment;filename=CSKefu-Contacts-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xls");

        ExcelExporterProcess excelProcess = new ExcelExporterProcess(values, table, response.getOutputStream());
        excelProcess.process();
        return;
    }

    @RequestMapping("/expsearch")
    @Menu(type = "contacts", subtype = "contacts")
    public void expall(ModelMap map, HttpServletRequest request, HttpServletResponse response, @Valid String q, @Valid String ekind) throws IOException {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (!StringUtils.isBlank(q)) {
            map.put("q", q);
        }
        if (!StringUtils.isBlank(ekind)) {
            boolQueryBuilder.must(termQuery("ekind", ekind));
            map.put("ekind", ekind);
        }

        Iterable<Contacts> contactsList = contactsRes.findByCreaterAndSharesAndOrgi(super.getUser(request).getId(), super.getUser(request).getId(), super.getOrgi(request), null, null, false, boolQueryBuilder, q, new PageRequest(super.getP(request), super.getPs(request)));
        MetadataTable table = metadataRes.findByTablename("uk_contacts");
        List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
        for (Contacts contacts : contactsList) {
            values.add(MainUtils.transBean2Map(contacts));
        }

        response.setHeader("content-disposition", "attachment;filename=CSKefu-Contacts-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xls");

        ExcelExporterProcess excelProcess = new ExcelExporterProcess(values, table, response.getOutputStream());
        excelProcess.process();

        return;
    }


    @RequestMapping("/embed/index")
    @Menu(type = "customer", subtype = "embed")
    public ModelAndView embed(ModelMap map, HttpServletRequest request, @Valid String q, @Valid String ckind) throws CSKefuException {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(!super.esOrganFilter(request, boolQueryBuilder)){
            return request(super.createAppsTempletResponse("/apps/business/contacts/embed/index"));
        }
        if (!StringUtils.isBlank(q)) {
            map.put("q", q);
        }
        if (!StringUtils.isBlank(ckind)) {
            boolQueryBuilder.must(termQuery("ckind", ckind));
            map.put("ckind", ckind);
        }
        map.addAttribute("contactsList", contactsRes.findByCreaterAndSharesAndOrgi(super.getUser(request).getId(), super.getUser(request).getId(), super.getOrgi(request), null, null, false, boolQueryBuilder, q, new PageRequest(super.getP(request), super.getPs(request))));

        return request(super.createRequestPageTempletResponse("/apps/business/contacts/embed/index"));
    }

    @RequestMapping("/embed/add")
    @Menu(type = "contacts", subtype = "embedadd")
    public ModelAndView embedadd(ModelMap map, HttpServletRequest request) {
        return request(super.createRequestPageTempletResponse("/apps/business/contacts/embed/add"));
    }

    @RequestMapping("/embed/save")
    @Menu(type = "contacts", subtype = "embedsave")
    public ModelAndView embedsave(HttpServletRequest request, @Valid Contacts contacts) {
        contacts.setCreater(super.getUser(request).getId());
        contacts.setOrgi(super.getOrgi(request));
        contacts.setOrgan(super.getUser(request).getOrgan());
        contacts.setPinyin(PinYinTools.getInstance().getFirstPinYin(contacts.getName()));
        if (StringUtils.isBlank(contacts.getCusbirthday())) {
            contacts.setCusbirthday(null);
        }
        contactsRes.save(contacts);
        return request(super.createRequestPageTempletResponse("redirect:/apps/contacts/embed/index.html"));
    }

    @RequestMapping("/embed/edit")
    @Menu(type = "contacts", subtype = "embededit")
    public ModelAndView embededit(ModelMap map, HttpServletRequest request, @Valid String id) {
        map.addAttribute("contacts", contactsRes.findOne(id));
        return request(super.createRequestPageTempletResponse("/apps/business/contacts/embed/edit"));
    }

    @RequestMapping("/embed/update")
    @Menu(type = "contacts", subtype = "embedupdate")
    public ModelAndView embedupdate(HttpServletRequest request, @Valid Contacts contacts) {
        Contacts data = contactsRes.findOne(contacts.getId());
        if (data != null) {
            List<PropertiesEvent> events = PropertiesEventUtils.processPropertiesModify(request, contacts, data, "id", "orgi", "creater", "createtime", "updatetime");    //记录 数据变更 历史
            if (events.size() > 0) {
                String modifyid = MainUtils.getUUID();
                Date modifytime = new Date();
                for (PropertiesEvent event : events) {
                    event.setDataid(contacts.getId());
                    event.setCreater(super.getUser(request).getId());
                    event.setOrgi(super.getOrgi(request));
                    event.setModifyid(modifyid);
                    event.setCreatetime(modifytime);
                    propertiesEventRes.save(event);
                }
            }

            contacts.setCreater(data.getCreater());
            contacts.setCreatetime(data.getCreatetime());
            contacts.setOrgi(super.getOrgi(request));
            contacts.setOrgan(super.getUser(request).getOrgan());
            contacts.setPinyin(PinYinTools.getInstance().getFirstPinYin(contacts.getName()));
            if (StringUtils.isBlank(contacts.getCusbirthday())) {
                contacts.setCusbirthday(null);
            }
            contactsRes.save(contacts);
        }

        return request(super.createRequestPageTempletResponse("redirect:/apps/contacts/embed/index.html"));
    }
}
