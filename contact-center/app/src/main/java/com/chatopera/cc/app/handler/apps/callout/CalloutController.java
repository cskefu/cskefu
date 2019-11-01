/*
 * Copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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

package com.chatopera.cc.app.handler.apps.callout;

import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.app.schedule.CallOutSheetTask;
import com.chatopera.cc.exception.CallOutRuntimeException;
import com.chatopera.cc.app.persistence.repository.*;
import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.model.CallOutDialplan;
import com.chatopera.cc.app.model.CallOutLogDialPlan;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;

@Controller
@RequestMapping(value = "/apps/callout")
public class CalloutController extends Handler {
    static final Logger logger = LoggerFactory.getLogger(CalloutController.class);

    @Autowired
    private CallOutDialplanRepository callOutDialplanRes;

    @Autowired
    private CallOutLogDialPlanRepository dialPlanLogRepository;

    @Autowired
    private OrganRepository organRes;

    @Autowired
    private SNSAccountRepository snsAccountRes;

    @Autowired
    private CallOutSheetTask callOutSheetTask;

    @Autowired
    private UserRepository userRepositoryRes;

    /**
     * 处理潜在客户Excel数据
     *
     * @param file
     * @return
     */
    private boolean processTargetList(final CallOutDialplan dp, final MultipartFile file) {
        try {
            // store the bytes somewhere
            final Sheet sheet;
            final String filename = file.getOriginalFilename();
            if(filename.endsWith(".xlsx")){
                callOutSheetTask.run(dp.getId(),
                        dp.getOrgi(),
                        dp.getOrgan().getId(),
                        file);
            } else {
                throw new CallOutRuntimeException("[callout] Invalid file format, 仅支持 .xlsx的文件。");
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @RequestMapping(value = "/index")
    @Menu(type = "callout", subtype = "index", access = true)
    public ModelAndView index(ModelMap map, HttpServletRequest request, @Valid String hostname, @Valid String key_value) {
        return request(super.createAppsTempletResponse("/apps/callout/index"));
    }

    @RequestMapping(value = "/resources/agents")
    @Menu(type = "callout", subtype = "agents", access = true)
    public ModelAndView agents(ModelMap map, HttpServletRequest request, @Valid String hostname, @Valid String key_value) {
        return request(super.createAppsTempletResponse("/apps/callout/resources/agents"));
    }

    @RequestMapping(value = "/resources/switchboard")
    @Menu(type = "callout", subtype = "switchboard", access = true)
    public ModelAndView switchboard(ModelMap map, HttpServletRequest request, @Valid String hostname, @Valid String key_value) {
        return request(super.createAppsTempletResponse("/apps/callout/resources/switchboard"));
    }

    @RequestMapping(value = "/resources/skillgroups")
    @Menu(type = "callout", subtype = "skillgroups", access = true)
    public ModelAndView skillgroups(ModelMap map, HttpServletRequest request, @Valid String hostname, @Valid String key_value) {
        return request(super.createAppsTempletResponse("/apps/callout/resources/skillgroups"));
    }

    @RequestMapping(value = "/dialplan/index")
    @Menu(type = "callout", subtype = "dialplan", access = true)
    public ModelAndView dialplan(ModelMap map, HttpServletRequest request) {
        Page<CallOutDialplan> data = callOutDialplanRes.findAllByIsarchiveNot(
                true,
                new PageRequest(
                        super.getP(request),
                        super.getPs(request),
                        Sort.Direction.ASC,
                        "createtime"
                )
        );
        map.addAttribute("vm", data);
        return request(super.createAppsTempletResponse("/apps/callout/dialplan/index"));
    }

    @RequestMapping(value = "/dialplan/add")
    @Menu(type = "callout", subtype = "dialplan", access = true)
    public ModelAndView add(ModelMap map, HttpServletRequest request) {

        // 添加技能组
        map.addAttribute("departments", organRes.findByOrgiAndSkill(MainContext.SYSTEM_ORGI, true));

        // 添加语音渠道
        map.addAttribute("voicechannels",
                snsAccountRes.findBySnstypeAndOrgi(MainContext.ChannelTypeEnum.PHONE.toString(), MainContext.SYSTEM_ORGI));

        return request(super.createRequestPageTempletResponse("/apps/callout/dialplan/add"));
    }

    @RequestMapping(value = "/dialplan/edit")
    @Menu(type = "callout", subtype = "dialplan", access = true)
    public ModelAndView edit(ModelMap map, HttpServletRequest request, @Valid String id) {
        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/callout/dialplan/edit"));
        view.addObject("callOutDialplanData", callOutDialplanRes.findOne(id));
        return view;
    }

    @RequestMapping(value = "/dialplan/save", method = RequestMethod.POST)
    @Menu(type = "callout", subtype = "dialplan", access = true)
    public ModelAndView save(ModelMap map, HttpServletRequest request,
                             @Valid String name,
                             @Valid String voicechannelid,
                             @Valid String organid,
                             @Valid boolean isrecord,
                             @Valid int maxconcurrence,
                             @Valid float concurrenceratio,
                             @RequestParam("target") MultipartFile target) throws IOException, CallOutRuntimeException {
        logger.info("save name {}, voicechannelid {}, organid {}, isrecord {} concurrenceratio {}", name, voicechannelid, organid, isrecord, concurrenceratio);
        if(StringUtils.isBlank(name))
            throw new CallOutRuntimeException("[callout] 呼叫计划名字不能为空");

        CallOutDialplan dp = new CallOutDialplan();
        Date dt = new Date();
        dp.setCreater(super.getUser(request));
        dp.setCreatetime(dt);
        dp.setUpdatetime(dt);
        dp.setConcurrenceratio(concurrenceratio);
        dp.setMaxconcurrence(maxconcurrence);
        dp.setIsrecord(isrecord);
        dp.setExecuted(0);
        dp.setVoicechannel(snsAccountRes.findByIdAndOrgi(voicechannelid, MainContext.SYSTEM_ORGI));
        dp.setOrgan(organRes.findByIdAndOrgi(organid, MainContext.SYSTEM_ORGI));
        dp.setName(name);
        dp.setIsarchive(false);
        dp.setOrgi(MainContext.SYSTEM_ORGI);
        dp.setStatus(MainContext.CallOutDialplanStatusEnum.INITIALIZATION.toString());
        callOutDialplanRes.save(dp);
        logger.info("[callout] dialplan id {}", dp.getId());

        if (!target.isEmpty()) {
            if (!processTargetList(dp, target)) {
                // TODO 文件格式不对
            }
        } else {
            // TODO 异常返回
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/callout/dialplan/index.html"));
    }

    @RequestMapping(value = "/dialplan/update", method = RequestMethod.POST)
    @Menu(type = "callout", subtype = "dialplan", access = true)
    public ModelAndView update(HttpServletRequest request, @Valid CallOutDialplan callOutDialplan) {
        CallOutDialplan callOutDialplanTmp = callOutDialplanRes.getOne(callOutDialplan.getId());
        if(callOutDialplanTmp != null){
            callOutDialplanTmp.setMaxconcurrence(callOutDialplan.getMaxconcurrence());
            callOutDialplanTmp.setConcurrenceratio(callOutDialplan.getConcurrenceratio());
            callOutDialplanTmp.setUpdatetime(new Date());
            callOutDialplanRes.save(callOutDialplanTmp);
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/callout/dialplan/index.html"));
    }

    @RequestMapping(value = "/dialplan/archive")
    @Menu(type = "callout", subtype = "dialplan-archive", access = true)
    public ModelAndView dialplanhistory(ModelMap map, HttpServletRequest request) {
        Page<CallOutDialplan> data = callOutDialplanRes.findByIsarchive(
                true,
                new PageRequest(
                        super.getP(request),
                        super.getPs(request),
                        Sort.Direction.ASC,
                        "createtime"
                )
        );
        map.addAttribute("vm", data);
        return request(super.createAppsTempletResponse("/apps/callout/dialplan/archive"));
    }

    @RequestMapping(value = "/reports/agents")
    @Menu(type = "callout", subtype = "reports-agents", access = true)
    public ModelAndView reportsagents(ModelMap map, HttpServletRequest request) {
        // 技能组list
        map.addAttribute("departments", organRes.findByOrgiAndSkill(MainContext.SYSTEM_ORGI, true));
        // 添加语音渠道
        map.addAttribute("voicechannels",
                snsAccountRes.findBySnstypeAndOrgi(MainContext.ChannelTypeEnum.PHONE.toString(), MainContext.SYSTEM_ORGI));
        return request(super.createAppsTempletResponse("/apps/callout/reports/agents"));
    }

    @RequestMapping(value = "/reports/daily-summary")
    @Menu(type = "callout", subtype = "reports-ds", access = true)
    public ModelAndView dailysummary(ModelMap map, HttpServletRequest request) {
        // 添加语音渠道
        map.addAttribute("voicechannels",
                snsAccountRes.findBySnstypeAndOrgi(MainContext.ChannelTypeEnum.PHONE.toString(), MainContext.SYSTEM_ORGI));
        return request(super.createAppsTempletResponse("/apps/callout/reports/daily-summary"));
    }

    @RequestMapping(value = "/reports/agent-monitor")
    @Menu(type = "callout", subtype = "reports-agent-monitor", access = true)
    public ModelAndView agentMonitor(ModelMap map, HttpServletRequest request, @Valid String channel, @Valid String organ) {
        // 技能组list
        map.addAttribute("departments", organRes.findByOrgiAndSkill(MainContext.SYSTEM_ORGI, true));

        // 添加语音渠道
        map.addAttribute("voicechannels",
                snsAccountRes.findBySnstypeAndOrgi(MainContext.ChannelTypeEnum.PHONE.toString(), MainContext.SYSTEM_ORGI));

        return request(super.createAppsTempletResponse("/apps/callout/reports/agent-monitor"));
    }

    @RequestMapping(value = "/reports/system")
    @Menu(type = "callout", subtype = "reports-system", access = true)
    public ModelAndView system(ModelMap map, HttpServletRequest request, @Valid String hostname, @Valid String key_value) {
        return request(super.createAppsTempletResponse("/apps/callout/reports/system"));
    }

    @RequestMapping(value = "/reports/recording")
    @Menu(type = "callout", subtype = "reports-rec", access = true)
    public ModelAndView recording(ModelMap map, HttpServletRequest request, @Valid String hostname, @Valid String key_value) {
        return request(super.createAppsTempletResponse("/apps/callout/reports/recording"));
    }

    @RequestMapping(value = "/reports/communicate")
    @Menu(type = "callout", subtype = "reports-comm", access = true)
    public ModelAndView communicate(ModelMap map, HttpServletRequest request, @Valid String hostname, @Valid String key_value) {
        // 技能组list
        map.addAttribute("departments", organRes.findByOrgiAndSkill(MainContext.SYSTEM_ORGI, true));

        // 坐席list
        map.addAttribute("sipAccounts", userRepositoryRes.findBySipaccountIsNotNullAndDatastatusIsFalse());

        return request(super.createAppsTempletResponse("/apps/callout/reports/communicate"));
    }

    @RequestMapping(value = "/reports/dialplan")
    @Menu(type = "callout", subtype = "reports-dail", access = true)
    public ModelAndView reportsdialplan(ModelMap map, HttpServletRequest request) {
        Page<CallOutLogDialPlan> dialPlanList = dialPlanLogRepository.findAll(
                new PageRequest(
                        super.getP(request),
                        super.getPs(request),
                        Sort.Direction.ASC,
                        "createtime"
                )

        );

        map.addAttribute("dialPlanList", dialPlanList);

        return request(
                super.createAppsTempletResponse(
                        "/apps/callout/reports/dialplan"
                )
        );
    }

}
