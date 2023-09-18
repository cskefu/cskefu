/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2018- Jun. 2023 Chatopera Inc, <https://www.chatopera.com>,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (C) 2017 优客服-多渠道客服系统,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.cskefu.cc.controller.apps.service;

import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.model.*;
import com.cskefu.cc.persistence.repository.ContactsRepository;
import com.cskefu.cc.persistence.repository.AgentServiceRepository;
import com.cskefu.cc.persistence.repository.MetadataRepository;
import com.cskefu.cc.persistence.repository.ServiceSummaryRepository;
import com.cskefu.cc.persistence.repository.TagRepository;
import com.cskefu.cc.proxy.OrganProxy;
import com.cskefu.cc.util.Menu;
import com.cskefu.cc.util.dsdata.export.ExcelExporterProcess;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.persistence.criteria.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/apps/agent/processed")
public class ProcessedSummaryController extends Handler {

    @Autowired
    private ServiceSummaryRepository serviceSummaryRes;

    @Autowired
    private MetadataRepository metadataRes;

    @Autowired
    private AgentServiceRepository agentServiceRes;

    @Autowired
    private TagRepository tagRes;

    @Autowired
    private ContactsRepository contactsRes;

    @Autowired
    private OrganProxy organProxy;

    /**
     * 按条件查询
     *
     * @param map
     * @param request
     * @param ani
     * @param called
     * @param begin
     * @param end
     * @param direction
     * @return
     */
    @RequestMapping(value = "/index")
    @Menu(type = "agent", subtype = "processed")
    public ModelAndView index(ModelMap map, HttpServletRequest request, @Valid final String ani, @Valid final String called, @Valid final String begin, @Valid final String end, @Valid final String direction) {
        Organ currentOrgan = super.getOrgan(request);
        Map<String, Organ> organs = organProxy.findAllOrganByParent(currentOrgan);
        Page<AgentServiceSummary> page = serviceSummaryRes.findAll((root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            Expression<String> exp = root.<String>get("skill");
            list.add(exp.in(organs.keySet()));
            list.add(cb.equal(root.get("process").as(boolean.class), true));
            list.add(cb.notEqual(root.get("channel").as(String.class), MainContext.ChannelType.PHONE.toString()));
            if (!StringUtils.isBlank(ani)) {
                list.add(cb.equal(root.get("ani").as(String.class), ani));
            }
            if (!StringUtils.isBlank(called)) {
                list.add(cb.equal(root.get("called").as(String.class), called));
            }
            try {
                if (!StringUtils.isBlank(begin) && begin.matches("[\\d]{4}-[\\d]{2}-[\\d]{2}")) {
                    list.add(cb.greaterThanOrEqualTo(root.get("createtime").as(Date.class), MainUtils.simpleDateFormat.parse(begin)));
                }
                if (!StringUtils.isBlank(end) && end.matches("[\\d]{4}-[\\d]{2}-[\\d]{2}")) {
                    list.add(cb.lessThanOrEqualTo(root.get("createtime").as(Date.class), MainUtils.dateFormate.parse(end + " 23:59:59")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Predicate[] p = new Predicate[list.size()];
            return cb.and(list.toArray(p));
        }, PageRequest.of(super.getP(request), super.getPs(request), Sort.Direction.DESC, "createtime"));
        map.addAttribute("summaryList", page);
        map.addAttribute("ani", ani);
        map.addAttribute("called", called);
        map.addAttribute("begin", begin);
        map.addAttribute("end", end);

        map.addAttribute("tags", tagRes.findByTagtype(MainContext.ModelType.SUMMARY.toString()));

        return request(super.createView("/apps/service/processed/index"));
    }


    @RequestMapping(value = "/process")
    @Menu(type = "agent", subtype = "processed")
    public ModelAndView process(ModelMap map, HttpServletRequest request, @Valid final String id) {
        AgentServiceSummary summary = serviceSummaryRes.findById(id).orElse(null);
        map.addAttribute("summary", summary);
        map.put("summaryTags", tagRes.findByTagtype(MainContext.ModelType.SUMMARY.toString()));
        if (summary != null && !StringUtils.isBlank(summary.getAgentserviceid())) {
            AgentService service = agentServiceRes.findById(summary.getAgentserviceid()).orElse(null);
            map.addAttribute("service", service);
            if (!StringUtils.isBlank(summary.getContactsid())) {
                Contacts contacts = contactsRes.findById(summary.getContactsid()).orElse(null);
                map.addAttribute("contacts", contacts);
            }
        }

        return request(super.createView("/apps/service/processed/process"));
    }

    @RequestMapping(value = "/save")
    @Menu(type = "agent", subtype = "processed")
    public ModelAndView save(ModelMap map, HttpServletRequest request, @Valid final AgentServiceSummary summary) {
        AgentServiceSummary oldSummary = serviceSummaryRes.findById(summary.getId()).orElse(null);
        if (oldSummary != null) {
            oldSummary.setProcess(true);
            oldSummary.setUpdatetime(new Date());
            oldSummary.setUpdateuser(super.getUser(request).getId());
            oldSummary.setProcessmemo(summary.getProcessmemo());
            serviceSummaryRes.save(oldSummary);
        }

        return request(super.createView("redirect:/apps/agent/processed/index.html"));
    }

    @RequestMapping("/expids")
    @Menu(type = "agent", subtype = "processed")
    public void expids(ModelMap map, HttpServletRequest request, HttpServletResponse response, @Valid String[] ids) throws IOException {
        if (ids != null && ids.length > 0) {
            Iterable<AgentServiceSummary> statusEventList = serviceSummaryRes.findAllById(Arrays.asList(ids));
            MetadataTable table = metadataRes.findByTablename("uk_servicesummary");
            List<Map<String, Object>> values = new ArrayList<>();
            for (AgentServiceSummary event : statusEventList) {
                values.add(MainUtils.transBean2Map(event));
            }

            response.setHeader("content-disposition", "attachment;filename=CSKeFu-Summary-History-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xls");

            ExcelExporterProcess excelProcess = new ExcelExporterProcess(values, table, response.getOutputStream());
            excelProcess.process();
        }

        return;
    }

    @RequestMapping("/expall")
    @Menu(type = "agent", subtype = "processed")
    public void expall(ModelMap map, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Organ currentOrgan = super.getOrgan(request);
        Map<String, Organ> organs = organProxy.findAllOrganByParent(currentOrgan);
        Iterable<AgentServiceSummary> statusEventList = serviceSummaryRes.findByChannelNotAndProcessTrueAndSkillIn(
                MainContext.ChannelType.PHONE.toString(), organs.keySet(), PageRequest.of(0, 10000));

        MetadataTable table = metadataRes.findByTablename("uk_servicesummary");
        List<Map<String, Object>> values = new ArrayList<>();
        for (AgentServiceSummary statusEvent : statusEventList) {
            values.add(MainUtils.transBean2Map(statusEvent));
        }

        response.setHeader("content-disposition", "attachment;filename=CSKeFu-Summary-History-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xls");

        ExcelExporterProcess excelProcess = new ExcelExporterProcess(values, table, response.getOutputStream());
        excelProcess.process();
        return;
    }

    @RequestMapping("/expsearch")
    @Menu(type = "agent", subtype = "processed")
    public void expall(ModelMap map, HttpServletRequest request, HttpServletResponse response, @Valid final String ani, @Valid final String called, @Valid final String begin, @Valid final String end, @Valid final String direction) throws IOException {
        Page<AgentServiceSummary> page = serviceSummaryRes.findAll((root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            list.add(cb.equal(root.get("process").as(boolean.class), 1));
            if (!StringUtils.isBlank(ani)) {
                list.add(cb.equal(root.get("ani").as(String.class), ani));
            }
            if (!StringUtils.isBlank(called)) {
                list.add(cb.equal(root.get("called").as(String.class), called));
            }
            try {
                if (!StringUtils.isBlank(begin) && begin.matches("[\\d]{4}-[\\d]{2}-[\\d]{2} [\\d]{2}:[\\d]{2}:[\\d]{2}")) {
                    list.add(cb.greaterThanOrEqualTo(root.get("createtime").as(Date.class), MainUtils.dateFormate.parse(begin)));
                }
                if (!StringUtils.isBlank(end) && end.matches("[\\d]{4}-[\\d]{2}-[\\d]{2} [\\d]{2}:[\\d]{2}:[\\d]{2}")) {
                    list.add(cb.lessThanOrEqualTo(root.get("createtime").as(Date.class), MainUtils.dateFormate.parse(end)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Predicate[] p = new Predicate[list.size()];
            return cb.and(list.toArray(p));
        }, PageRequest.of(0, 10000, Sort.Direction.DESC, "createtime"));

        List<Map<String, Object>> values = new ArrayList<>();
        for (AgentServiceSummary summary : page) {
            values.add(MainUtils.transBean2Map(summary));
        }

        response.setHeader("content-disposition", "attachment;filename=CSKeFu-Summary-History-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xls");

        MetadataTable table = metadataRes.findByTablename("uk_servicesummary");

        ExcelExporterProcess excelProcess = new ExcelExporterProcess(values, table, response.getOutputStream());
        excelProcess.process();

        return;
    }
}
