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
import com.chatopera.cc.persistence.repository.CubeLevelRepository;
import com.chatopera.cc.persistence.repository.CubeMetadataRepository;
import com.chatopera.cc.persistence.repository.DimensionRepository;
import com.chatopera.cc.persistence.repository.TablePropertiesRepository;
import com.chatopera.cc.util.Menu;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/apps/report/dimension")
@RequiredArgsConstructor
public class DimensionController extends Handler {

    @org.springframework.lang.NonNull
    private final DimensionRepository dimensionRes;

    @org.springframework.lang.NonNull
    private final CubeLevelRepository cubeLevelRes;

    @org.springframework.lang.NonNull
    private final CubeMetadataRepository cubeMetadataRes;

    @org.springframework.lang.NonNull
    private final TablePropertiesRepository tablePropertiesRes;

    @RequestMapping("/add")
    @Menu(type = "report", subtype = "dimension")
    public ModelAndView dimensionadd(ModelMap map, @Valid String cubeid) {
        map.addAttribute("cubeid", cubeid);
        map.addAttribute("fkfieldList", cubeMetadataRes.findByCubeidAndMtype(cubeid, "0"));
        map.addAttribute("fktableList", cubeMetadataRes.findByCubeidAndMtypeNot(cubeid, "0"));
        return request(super.createRequestPageTempletResponse("/apps/business/report/cube/dimension/add"));
    }

    @RequestMapping("/save")
    @Menu(type = "report", subtype = "dimension")
    public ModelAndView dimensionsave(HttpServletRequest request, @Valid Dimension dimension) {
        if (!StringUtils.isBlank(dimension.getName())) {
            dimension.setOrgi(super.getOrgi(request));
            dimension.setCreater(super.getUser(request).getId());
            dimensionRes.save(dimension);
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/report/cube/detail.html?id=" + dimension.getCubeid() + "&dimensionId=" + dimension.getId()));
    }

    @RequestMapping("/delete")
    @Menu(type = "report", subtype = "dimension")
    public ModelAndView quickreplydelete(HttpServletRequest request, @Valid String id) {
        Dimension dimension = dimensionRes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Dimension %s not found", id)));
        dimensionRes.delete(dimension);
        List<CubeLevel> cubeLevelList = cubeLevelRes.findByOrgiAndDimid(super.getOrgi(request), id);
        if (!cubeLevelList.isEmpty()) {
            cubeLevelRes.deleteAll(cubeLevelList);
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/report/cube/detail.html?id=" + dimension.getCubeid()));
    }

    @RequestMapping("/edit")
    @Menu(type = "report", subtype = "dimension", admin = true)
    public ModelAndView quickreplyedit(ModelMap map, @Valid String id) {
        Dimension dimension = dimensionRes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Dimension %s not found", id)));
        map.put("dimension", dimension);
        String cubeid = dimension.getCubeid();
        map.addAttribute("cubeid", cubeid);
        map.addAttribute("fkfieldList", cubeMetadataRes.findByCubeidAndMtype(cubeid, "0"));
        List<CubeMetadata> fktableList = cubeMetadataRes.findByCubeidAndMtypeNot(cubeid, "0");
        map.addAttribute("fktableList", fktableList);
        map.put("fktableidList", tablePropertiesRes.findByDbtableid(dimension.getFktable()));
        return request(super.createRequestPageTempletResponse("/apps/business/report/cube/dimension/edit"));
    }

    @RequestMapping("/update")
    @Menu(type = "report", subtype = "dimension", admin = true)
    public ModelAndView quickreplyupdate(HttpServletRequest request, @Valid Dimension dimension) {
        if (!StringUtils.isBlank(dimension.getId())) {
            dimension.setOrgi(super.getOrgi(request));
            dimension.setCreater(super.getUser(request).getId());
            dimensionRes.findById(dimension.getId()).ifPresent(it -> dimension.setCreatetime(it.getCreatetime()));
            dimensionRes.save(dimension);
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/report/cube/detail.html?id=" + dimension.getCubeid() + "&dimensionId=" + dimension.getId()));
    }

    @RequestMapping("/fktableid")
    @Menu(type = "report", subtype = "dimension", admin = true)
    public ModelAndView fktableid(ModelMap map, @Valid String tableid) {
        if (!StringUtils.isBlank(tableid)) {
            map.put("fktableidList", tablePropertiesRes.findByDbtableid(tableid));
        }
        return request(super.createRequestPageTempletResponse("/apps/business/report/cube/dimension/fktableiddiv"));
    }
}
