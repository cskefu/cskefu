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
package com.chatopera.cc.controller.apps.service;

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.Dict;
import com.chatopera.cc.model.SysDic;
import com.chatopera.cc.persistence.repository.CubeService;
import com.chatopera.cc.persistence.repository.DataSourceService;
import com.chatopera.cc.proxy.OnlineUserProxy;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.bi.ReportData;
import com.chatopera.cc.util.bi.UKExcelUtil;
import com.chatopera.cc.util.bi.model.Level;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/service")
public class StatsController extends Handler {

    private final static Logger logger = LoggerFactory.getLogger(StatsController.class);

    @Value("${web.upload-path}")
    private String path;

    @Autowired
    private DataSourceService dataSource;

    @RequestMapping("/stats/coment")
    @Menu(type = "service", subtype = "statcoment", admin = true)
    public ModelAndView statcoment(ModelMap map, HttpServletRequest request,
                                   @Valid String agent,
                                   @Valid String skill,
                                   @Valid String begin,
                                   @Valid String end) throws Exception {
        logger.info("[statcoment] agent {}, skill {}, begin {}, end {}", agent, skill, begin, end);
        Map<String, Object> mapR = MainUtils.getRequestParam(request);
        mapR.put("orgi", super.getOrgi(request));
        ReportData reportData = new CubeService("coment.xml", path, dataSource, mapR).execute("SELECT [comment].[满意度].members on columns , NonEmptyCrossJoin([time].[日期].members , NonEmptyCrossJoin([skill].[技能组].members,[agent].[坐席].members)) on rows  FROM [满意度]");

        List<SysDic> dicList = Dict.getInstance().getDic(Constants.CSKEFU_SYSTEM_COMMENT_DIC);
        for (Level title : reportData.getCol().getChilderen()) {
            for (SysDic dic : dicList) {
                if (dic.getCode().equals(title.getName())) {
                    title.setName(dic.getName());
                }
            }
        }

        map.addAttribute("reportData", reportData);
        if (StringUtils.isNotBlank(agent)) {
            map.addAttribute("agent", agent);
        }
        if (StringUtils.isNotBlank(skill)) {
            map.addAttribute("skill", skill);
        }
        if (StringUtils.isNotBlank(begin)) {
            map.addAttribute("begin", begin);
        }
        if (StringUtils.isNotBlank(end)) {
            map.addAttribute("end", end);
        }
        map.addAttribute("orgi", super.getOrgi(request));
        /***
         * 查询 技能组 ， 缓存？
         */
        map.addAttribute("skillGroups", OnlineUserProxy.organ(super.getOrgi(request), true));
        /**
         * 查询坐席 ， 缓存？
         */
        map.addAttribute("agentList", OnlineUserProxy.agents(super.getOrgi(request), true));

        return request(super.createAppsTempletResponse("/apps/service/stats/coment"));
    }

    @RequestMapping("/stats/coment/exp")
    @Menu(type = "service", subtype = "statcoment", admin = true)
    public void statcomentexp(ModelMap map, HttpServletRequest request, HttpServletResponse response, @Valid String agent, @Valid String skill, @Valid String begin, @Valid String end) throws Exception {
        Map<String, Object> mapR = MainUtils.getRequestParam(request);
        mapR.put("orgi", super.getOrgi(request));
        ReportData reportData = new CubeService("coment.xml", path, dataSource, mapR).execute("SELECT [comment].[满意度].members on columns , NonEmptyCrossJoin([time].[日期].members , NonEmptyCrossJoin([skill].[技能组].members,[agent].[坐席].members)) on rows  FROM [满意度]");

        List<SysDic> dicList = Dict.getInstance().getDic(Constants.CSKEFU_SYSTEM_COMMENT_DIC);
        for (Level title : reportData.getCol().getChilderen()) {
            for (SysDic dic : dicList) {
                if (dic.getCode().equals(title.getName())) {
                    title.setName(dic.getName());
                }
            }
        }

        response.setHeader("content-disposition", "attachment;filename=UCKeFu-Report-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xls");
        new UKExcelUtil(reportData, response.getOutputStream(), "满意度统计").createFile();

        return;
    }

    @RequestMapping("/stats/agent")
    @Menu(type = "service", subtype = "statagent", admin = true)
    public ModelAndView statagent(ModelMap map, HttpServletRequest request, @Valid String agent, @Valid String skill, @Valid String begin, @Valid String end) throws Exception {
        Map<String, Object> mapR = MainUtils.getRequestParam(request);
        mapR.put("orgi", super.getOrgi(request));
        ReportData reportData = new CubeService("consult.xml", path, dataSource, mapR).execute("SELECT {[Measures].[咨询数量],[Measures].[平均等待时长（秒）],[Measures].[平均咨询时长（秒）]} on columns , NonEmptyCrossJoin([time].[日期].members , NonEmptyCrossJoin([skill].[技能组].members,[agent].[坐席].members)) on rows  FROM [咨询]");
        map.addAttribute("reportData", reportData);

        if (StringUtils.isNotBlank(agent)) {
            map.addAttribute("agent", agent);
        }
        if (StringUtils.isNotBlank(skill)) {
            map.addAttribute("skill", skill);
        }
        if (StringUtils.isNotBlank(begin)) {
            map.addAttribute("begin", begin);
        }
        if (StringUtils.isNotBlank(end)) {
            map.addAttribute("end", end);
        }
        /***
         * 查询 技能组 ， 缓存？
         */
        map.addAttribute("skillGroups", OnlineUserProxy.organ(super.getOrgi(request), true));
        /**
         * 查询坐席 ， 缓存？
         */
        map.addAttribute("agentList", OnlineUserProxy.agents(super.getOrgi(request), true));

        return request(super.createAppsTempletResponse("/apps/service/stats/consult"));
    }

    @RequestMapping("/stats/agent/exp")
    @Menu(type = "service", subtype = "statagent", admin = true)
    public void statagentexp(ModelMap map, HttpServletRequest request, HttpServletResponse response, @Valid String agent, @Valid String skill, @Valid String begin, @Valid String end) throws Exception {
        Map<String, Object> mapR = MainUtils.getRequestParam(request);
        mapR.put("orgi", super.getOrgi(request));
        ReportData reportData = new CubeService("consult.xml", path, dataSource, mapR).execute("SELECT {[Measures].[咨询数量],[Measures].[平均等待时长（秒）],[Measures].[平均咨询时长（秒）]} on columns , NonEmptyCrossJoin([time].[日期].members , NonEmptyCrossJoin([skill].[技能组].members,[agent].[坐席].members)) on rows  FROM [咨询]");
        response.setHeader("content-disposition", "attachment;filename=UCKeFu-Report-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xls");
        new UKExcelUtil(reportData, response.getOutputStream(), "客服坐席统计").createFile();

        return;
    }
}
