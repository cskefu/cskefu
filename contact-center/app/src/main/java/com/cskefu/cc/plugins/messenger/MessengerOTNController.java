package com.cskefu.cc.plugins.messenger;

import com.alibaba.fastjson.JSONObject;
import com.cskefu.cc.activemq.BrokerPublisher;
import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.controller.api.request.RestUtils;
import com.cskefu.cc.exception.CSKefuException;
import com.cskefu.cc.model.*;
import com.cskefu.cc.persistence.repository.FbMessengerRepository;
import com.cskefu.cc.persistence.repository.FbOTNRepository;
import com.cskefu.cc.proxy.AgentProxy;
import com.cskefu.cc.proxy.OrganProxy;
import com.cskefu.cc.util.Menu;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/apps/messenger/otn")
public class MessengerOTNController extends Handler {
    @Autowired
    private FbMessengerRepository fbMessengerRepository;

    @Autowired
    private FbOTNRepository otnRepository;

    @Autowired
    private OrganProxy organProxy;

    @Autowired
    private AgentProxy agentProxy;

    @Autowired
    private BrokerPublisher brokerPublisher;

    private Map<String, Organ> getOwnOrgan(HttpServletRequest request) {
        return organProxy.findAllOrganByParentAndOrgi(super.getOrgan(request), super.getOrgi(request));
    }

    @RequestMapping("/index")
    @Menu(type = "admin", subtype = "messenger")
    public ModelAndView index(ModelMap map, HttpServletRequest request, @Valid String queryPageId) {
        Map<String, Organ> organs = getOwnOrgan(request);
        List<FbMessenger> fbMessengers = fbMessengerRepository.findByOrganIn(organs.keySet());
        List<String> pageIds = fbMessengers.stream().map(p -> p.getPageId()).collect(Collectors.toList());

        map.addAttribute("fbMessengers", fbMessengers);

        if (StringUtils.isNotBlank(queryPageId)) {
            map.addAttribute("queryPageId", queryPageId);
            pageIds = Arrays.asList(queryPageId);
        }

        Page<FbOTN> otns = otnRepository.findByPageIdIn(pageIds, new PageRequest(super.getP(request), super.getPs(request), Sort.Direction.DESC, "createtime"));
        map.addAttribute("otns", otns);
        return request(super.createView("/admin/channel/messenger/otn/index"));
    }

    @RequestMapping("/add")
    @Menu(type = "admin", subtype = "messenger")
    public ModelAndView add(ModelMap map, HttpServletRequest request) {
        Map<String, Organ> organs = getOwnOrgan(request);
        List<FbMessenger> fbMessengers = fbMessengerRepository.findByOrganIn(organs.keySet());
        map.addAttribute("fbMessengers", fbMessengers);
        return request(super.createView("/admin/channel/messenger/otn/add"));
    }

    @RequestMapping("/save")
    @Menu(type = "admin", subtype = "messenger")
    public ModelAndView save(ModelMap map, @Valid FbOTN otn, HttpServletRequest request) {
        String msg = "save_ok";
        otn.setId(MainUtils.getUUID());
        otn.setCreatetime(new Date());
        otn.setUpdatetime(new Date());
        otn.setSubNum(0);
        otn.setMelinkNum(0);

        otn.setStatus("create");
        otnRepository.save(otn);

        if (otn.getSendtime() != null) {
            delaySend(otn);
        }

        return request(super.createView("redirect:/apps/messenger/otn/index.html?msg=" + msg));
    }

    @RequestMapping("/edit")
    @Menu(type = "admin", subtype = "messenger")
    public ModelAndView edit(ModelMap map, @Valid String id, HttpServletRequest request) {
        map.addAttribute("otn", otnRepository.getOne(id));
        return request(super.createView("/admin/channel/messenger/otn/edit"));
    }

    @RequestMapping("/update")
    @Menu(type = "admin", subtype = "messenger")
    public ModelAndView update(ModelMap map, @Valid FbOTN otn, HttpServletRequest request) {
        String msg = "update_ok";
        FbOTN oldOtn = otnRepository.findOne(otn.getId());
        if (oldOtn != null) {
            Date oldSendtime = oldOtn.getSendtime();

            oldOtn.setName(otn.getName());
            oldOtn.setPreSubMessage(otn.getPreSubMessage());
            oldOtn.setSubMessage(otn.getSubMessage());
            oldOtn.setOtnMessage(otn.getOtnMessage());
            oldOtn.setSuccessMessage(otn.getSuccessMessage());
            oldOtn.setSendtime(otn.getSendtime());
            oldOtn.setUpdatetime(new Date());
            otnRepository.save((oldOtn));

            if (otn.getSendtime() != null && !otn.getSendtime().equals(oldSendtime)) {
                delaySend(otn);
            }
        }

        return request(super.createView("redirect:/apps/messenger/otn/index.html?msg=" + msg));
    }

    private void delaySend(FbOTN otn) {
        long delaySeconds = (otn.getSendtime().getTime() - new Date().getTime()) / 1000;
        JSONObject payload = new JSONObject();
        payload.put("otnId", otn.getId());
        payload.put("sendtime", otn.getSendtime());
        brokerPublisher.send(Constants.INSTANT_MESSAGING_MQ_QUEUE_FACEBOOK_OTN, payload.toJSONString(), false, (int) delaySeconds);
    }

    @RequestMapping("/send")
    @Menu(type = "admin", subtype = "faceb ook")
    public ModelAndView send(ModelMap map, @Valid String id, HttpServletRequest request) {
        String msg = "send_ok";

        FbOTN otn = otnRepository.getOne(id);
        FbMessenger fbMessenger = fbMessengerRepository.findOneByPageId(otn.getPageId());
        if (fbMessenger != null && otn != null && otn.getStatus().equals("create")) {
            otn.setStatus("sending");
            otn.setSendtime(new Date());
            otnRepository.save(otn);
            JSONObject payload = new JSONObject();
            payload.put("otnId", otn.getId());
            brokerPublisher.send(Constants.INSTANT_MESSAGING_MQ_QUEUE_FACEBOOK_OTN, payload.toJSONString());
        }

        return request(super.createView("redirect:/apps/messenger/otn/index.html?msg=" + msg));
    }

    @RequestMapping("/image/upload")
    @Menu(type = "admin", subtype = "image", access = false)
    public ResponseEntity<String> upload(
            ModelMap map,
            HttpServletRequest request,
            @RequestParam(value = "imgFile", required = false) MultipartFile multipart
    ) throws IOException {
        final User logined = super.getUser(request);
        JSONObject result = new JSONObject();
        HttpHeaders headers = RestUtils.header();
        if (multipart != null && multipart.getOriginalFilename().lastIndexOf(".") > 0) {
            try {
                StreamingFile sf = agentProxy.saveFileIntoMySQLBlob(logined, multipart);
                result.put("error", 0);
                result.put("url", sf.getFileUrl());
            } catch (CSKefuException e) {
                result.put("error", 1);
                result.put("message", "请选择文件");
            }
        } else {
            result.put("error", 1);
            result.put("message", "请选择图片文件");
        }
        return new ResponseEntity<>(result.toString(), headers, HttpStatus.OK);
    }

    @RequestMapping("/delete")
    @Menu(type = "admin", subtype = "messenger")
    public ModelAndView delete(ModelMap map, HttpServletRequest request, @Valid String id) {
        String msg = "delete_ok";
        otnRepository.delete(id);

        return request(super.createView("redirect:/apps/messenger/otn/index.html?msg=" + msg));
    }
}
