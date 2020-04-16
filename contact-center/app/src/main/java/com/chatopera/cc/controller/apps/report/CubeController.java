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

import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.*;
import com.chatopera.cc.persistence.repository.*;
import com.chatopera.cc.util.Menu;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/apps/report/cube")
@RequiredArgsConstructor
public class CubeController extends Handler {

    @NonNull
    private final CubeTypeRepository cubeTypeRes;

    @NonNull
    private final CubeRepository cubeRes;

    @NonNull
    private final DimensionRepository dimensionRes;

    @NonNull
    private final CubeMeasureRepository cubeMeasureRes;

    @NonNull
    private final CubeLevelRepository cubeLevelRes;

    @NonNull
    private final CubeMetadataRepository cubeMetadataRes;

    @NonNull
    private final MetadataRepository metadataRes;

    @NonNull
    private final PublishedCubeRepository publishedCubeRes;

    @RequestMapping({"/type/add"})
    @Menu(type = "report", subtype = "cube")
    public ModelAndView addtype(ModelMap map, HttpServletRequest request, @Valid String typeid) {
        map.addAttribute("cubeTypeList", cubeTypeRes.findByOrgi(super.getOrgi(request)));
        if (!StringUtils.isBlank(typeid)) {
            map.addAttribute("cubeType", cubeTypeRes.findByIdAndOrgi(typeid, super.getOrgi(request)));
        }
        return request(super.createRequestPageTempletResponse("/apps/business/report/cube/type/add"));
    }

    @RequestMapping("/type/save")
    @Menu(type = "report", subtype = "cube")
    public ModelAndView typesave(HttpServletRequest request, @Valid CubeType cubeType) {
        CubeType ct = cubeTypeRes.findByOrgiAndName(super.getOrgi(request), cubeType.getName());
        if (ct == null) {
            cubeType.setOrgi(super.getOrgi(request));
            cubeType.setCreater(super.getUser(request).getId());
            cubeType.setCreatetime(new Date());
            cubeTypeRes.save(cubeType);
        } else {
            return request(super.createRequestPageTempletResponse("redirect:/apps/business/report/cube/index.html?msg=ct_type_exist"));
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/report/cube/index.html"));
    }

    @RequestMapping({"/type/edit"})
    @Menu(type = "report", subtype = "cube")
    public ModelAndView edittype(ModelMap map, HttpServletRequest request, String id) {
        map.addAttribute("cubeType", cubeTypeRes.findByIdAndOrgi(id, super.getOrgi(request)));
        map.addAttribute("cubeTypeList", cubeTypeRes.findByOrgi(super.getOrgi(request)));
        return request(super.createRequestPageTempletResponse("/apps/business/report/cube/type/edit"));
    }

    @RequestMapping("/type/update")
    @Menu(type = "report", subtype = "cube")
    public ModelAndView typeupdate(HttpServletRequest request, @Valid CubeType cubeType) {
        CubeType tempCubeType = cubeTypeRes.findByIdAndOrgi(cubeType.getId(), super.getOrgi(request));
        if (tempCubeType != null) {
            //判断名称是否重复
            CubeType ct = cubeTypeRes.findByOrgiAndName(super.getOrgi(request), cubeType.getName());
            if (ct != null && !ct.getId().equals(cubeType.getId())) {
                return request(super.createRequestPageTempletResponse("redirect:/apps/business/report/cube/index.html?msg=ct_type_exist&typeid=" + cubeType.getId()));
            }
            tempCubeType.setName(cubeType.getName());
            tempCubeType.setDescription(cubeType.getDescription());
            tempCubeType.setInx(cubeType.getInx());
            tempCubeType.setParentid(cubeType.getParentid());
            cubeTypeRes.save(tempCubeType);
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/business/report/cube/index.html?typeid=" + cubeType.getId()));
    }

    @RequestMapping({"/type/delete"})
    @Menu(type = "report", subtype = "cube")
    public ModelAndView deletetype(HttpServletRequest request, @Valid String id) {
        if (!StringUtils.isBlank(id)) {
            CubeType tempCubeType = cubeTypeRes.findByIdAndOrgi(id, super.getOrgi(request));
            cubeTypeRes.delete(tempCubeType);
            List<Cube> cubeList = cubeRes.findByOrgiAndTypeid(super.getOrgi(request), id);
            if (!cubeList.isEmpty()) {
                cubeRes.deleteAll(cubeList);
                for (Cube c : cubeList) {
                    Cube cube = getCube(c.getId());
                    cubeMetadataRes.deleteAll(cube.getMetadata());
                    cubeMeasureRes.deleteAll(cube.getMeasure());
                    dimensionRes.deleteAll(cube.getDimension());
                }
            }
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/report/cube/index.html"));
    }

    @RequestMapping("/index")
    @Menu(type = "report", subtype = "cube")
    public ModelAndView index(ModelMap map, HttpServletRequest request, @Valid String typeid, @Valid String msg) {
        List<CubeType> cubeTypeList = cubeTypeRes.findByOrgi(super.getOrgi(request));
        if (!StringUtils.isBlank(typeid)) {
            map.put("cubeType", cubeTypeRes.findByIdAndOrgi(typeid, super.getOrgi(request)));
            map.put("cubeList", cubeRes.getByOrgiAndTypeid(super.getOrgi(request), typeid, PageRequest.of(super.getP(request), super.getPs(request))));
        } else {
            map.put("cubeList", cubeRes.getByOrgi(super.getOrgi(request), PageRequest.of(super.getP(request), super.getPs(request))));
        }
        map.put("pubCubeTypeList", cubeTypeList);
        map.put("typeid", typeid);
        map.put("msg", msg);
        return request(super.createAppsTempletResponse("/apps/business/report/cube/index"));
    }

    @RequestMapping("/list")
    @Menu(type = "report", subtype = "cube")
    public ModelAndView list(ModelMap map, HttpServletRequest request, @Valid String typeid) {
        //List<CubeType> cubeTypeList = cubeTypeRes.findByOrgi(super.getOrgi(request)) ;
        if (!StringUtils.isBlank(typeid)) {
            map.put("cubeType", cubeTypeRes.findByIdAndOrgi(typeid, super.getOrgi(request)));
            map.put("cubeList", cubeRes.getByOrgiAndTypeid(super.getOrgi(request), typeid, PageRequest.of(super.getP(request), super.getPs(request))));
        } else {
            map.put("cubeList", cubeRes.getByOrgi(super.getOrgi(request), PageRequest.of(super.getP(request), super.getPs(request))));
        }
        //map.put("pubCubeTypeList", cubeTypeList) ;
        map.put("typeid", typeid);
        return request(super.createRequestPageTempletResponse("/apps/business/report/cube/list"));
    }

    @RequestMapping("/add")
    @Menu(type = "report", subtype = "cube")
    public ModelAndView cubeadd(ModelMap map, HttpServletRequest request, @Valid String typeid) {
        if (!StringUtils.isBlank(typeid)) {
            map.addAttribute("cubeType", cubeTypeRes.findByIdAndOrgi(typeid, super.getOrgi(request)));
        }
        map.addAttribute("cubeTypeList", cubeTypeRes.findByOrgi(super.getOrgi(request)));
        map.addAttribute("typeid", typeid);
        return request(super.createRequestPageTempletResponse("/apps/business/report/cube/add"));
    }

    @RequestMapping("/save")
    @Menu(type = "report", subtype = "cube")
    public ModelAndView cubesave(HttpServletRequest request, @Valid Cube cube) {
        if (!StringUtils.isBlank(cube.getName())) {
            cube.setOrgi(super.getOrgi(request));
            cube.setCreater(super.getUser(request).getId());
            cubeRes.save(cube);
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/report/cube/index.html?typeid=" + cube.getTypeid()));
    }

    @RequestMapping("/delete")
    @Menu(type = "report", subtype = "cube")
    public ModelAndView quickreplydelete(@Valid String id) {
        Cube cube = cubeRes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Cube %s not found", id)));
        cubeRes.delete(cube);
        dimensionRes.deleteAll(dimensionRes.findByCubeid(cube.getId()));
        cubeLevelRes.deleteAll(cubeLevelRes.findByCubeid(cube.getId()));
        cubeMeasureRes.deleteAll(cubeMeasureRes.findByCubeid(cube.getId()));
        cubeMetadataRes.deleteAll(cubeMetadataRes.findByCubeid(cube.getId()));
        return request(super.createRequestPageTempletResponse("redirect:/apps/report/cube/index.html?typeid=" + cube.getTypeid()));
    }

    @RequestMapping("/edit")
    @Menu(type = "report", subtype = "cube", admin = true)
    public ModelAndView cubeedit(ModelMap map, HttpServletRequest request, @Valid String id) {
        Cube cube = cubeRes.findById(id).orElse(null);
        map.put("cube", cube);
        if (cube != null) {
            map.put("cubeType", cubeTypeRes.findByIdAndOrgi(cube.getTypeid(), super.getOrgi(request)));
        }
        map.addAttribute("cubeTypeList", cubeTypeRes.findByOrgi(super.getOrgi(request)));
        return request(super.createRequestPageTempletResponse("/apps/business/report/cube/edit"));
    }

    @RequestMapping("/update")
    @Menu(type = "report", subtype = "cube", admin = true)
    public ModelAndView cubeupdate(HttpServletRequest request, @Valid Cube cube) {
        if (!StringUtils.isBlank(cube.getId())) {
            cube.setOrgi(super.getOrgi(request));
            cube.setCreater(super.getUser(request).getId());
            cubeRes.findById(cube.getId()).ifPresent(temp -> cube.setCreatetime(temp.getCreatetime()));
            cube.setUpdatetime(new Date());
            cubeRes.save(cube);
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/report/cube/index.html?typeid=" + cube.getTypeid()));
    }

    @RequestMapping("/imptb")
    @Menu(type = "report", subtype = "metadata", admin = true)
    public ModelAndView imptb(final ModelMap map, HttpServletRequest request, @Valid String cubeid) {

        map.put("tablesList", metadataRes.findByOrgi(super.getOrgi(request)));
        map.put("cubeid", cubeid);
        return request(super.createRequestPageTempletResponse("/apps/business/report/cube/cubemetadata/imptb"));
    }

    @RequestMapping("/imptbsave")
    @Menu(type = "report", subtype = "metadata", admin = true)
    public ModelAndView imptb(HttpServletRequest request, final @Valid String[] tables, @Valid String cubeid) {
        final User user = super.getUser(request);
        for (String tableid : tables) {
            MetadataTable tb = new MetadataTable();
            tb.setId(tableid);
            int count = cubeMetadataRes.countByTbAndCubeid(tb, cubeid);
            if (count == 0) {
                CubeMetadata cubeMetaData = new CubeMetadata();
                cubeMetaData.setCubeid(cubeid);
                cubeMetaData.setOrgi(super.getOrgi(request));
                cubeMetaData.setTb(tb);
                cubeMetaData.setCreater(user.getId());
                cubeMetaData.setMtype("1");
                cubeMetadataRes.save(cubeMetaData);
            }
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/report/cube/detail.html?id=" + cubeid));
    }

    @RequestMapping("/metadata/edit")
    @Menu(type = "report", subtype = "metadata", admin = true)
    public ModelAndView metadataedit(ModelMap map, final @Valid String id) {
        map.put("cubeMetadata", cubeMetadataRes.findById(id).orElse(null));
        return request(super.createRequestPageTempletResponse("/apps/business/report/cube/cubemetadata/edit"));
    }

    @RequestMapping("/metadata/update")
    @Menu(type = "report", subtype = "metadata", admin = true)
    public ModelAndView metadataedit(@Valid CubeMetadata cubeMetadata) {
        if (!StringUtils.isBlank(cubeMetadata.getId())) {
            cubeMetadataRes.findById(cubeMetadata.getId())
                    .ifPresent(temp -> {
                        temp.setNamealias(cubeMetadata.getNamealias());
                        if ("0".equals(cubeMetadata.getMtype())) {
                            List<CubeMetadata> list = cubeMetadataRes.findByCubeid(temp.getCubeid());
                            if (!list.isEmpty()) {
                                //设置其他数据表为从表
                                for (CubeMetadata cm : list) {
                                    if (!cm.getId().equals(temp.getId())) {
                                        cm.setMtype("1");
                                        cubeMetadataRes.save(cm);
                                    }
                                }
                            }
                        }
                        temp.setMtype(cubeMetadata.getMtype());
                        temp.setParameters(cubeMetadata.getParameters());
                        cubeMetadataRes.save(temp);
                    });
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/report/cube/detail.html?id=" + cubeMetadata.getCubeid()));
    }

    @RequestMapping("/metadata/del")
    @Menu(type = "report", subtype = "metadata", admin = true)
    public ModelAndView metadatadel(@Valid CubeMetadata cubeMetadata) {
        String msg = "";
        String id = cubeMetadata.getId();
        if (!StringUtils.isBlank(id)) {
            boolean flag = true;
            CubeMetadata temp = cubeMetadataRes.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Cube %s not found", id)));
            String tablename = null;
            String tableid = null;
            if (temp.getTb() != null) {
                tablename = temp.getTb().getName();
                tableid = temp.getTb().getId();
            }
            if (!StringUtils.isBlank(tableid)) {
                if (dimensionRes.countByFktable(tableid) > 0) {
                    flag = false;
                }
            }
            if (!StringUtils.isBlank(tablename)) {
                if (cubeLevelRes.countByTablename(tablename) > 0) {
                    flag = false;
                }
                if (cubeMeasureRes.countByTablename(tablename) > 0) {
                    flag = false;
                }
            }
            if (flag) {
                cubeMetadataRes.delete(temp);
            } else {
                msg = "CM_DEL_FAILED";
            }

        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/report/cube/detail.html?id=" + cubeMetadata.getCubeid() + "&msg=" + msg));
    }

    @RequestMapping("/detail")
    @Menu(type = "report", subtype = "cube")
    public ModelAndView detail(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String dimensionId, @Valid String msg) {
        List<Dimension> dimensionList = dimensionRes.findByCubeid(id);
        map.put("dimensionList", dimensionList);
        if (!dimensionList.isEmpty()) {
            if (StringUtils.isBlank(dimensionId)) {
                dimensionId = dimensionList.get(0).getId();
            }
            map.put("cubeLevelList", cubeLevelRes.findByOrgiAndDimid(super.getOrgi(request), dimensionId));
        }
        if (!StringUtils.isBlank(dimensionId) && "cubemeasure".equals(dimensionId)) {
            List<CubeMeasure> cubeMeasureList = cubeMeasureRes.findByCubeid(id);
            map.put("cubeMeasureList", cubeMeasureList);
        }
        map.put("cubeMetadataList", cubeMetadataRes.findByCubeid(id));
        map.put("cubeid", id);
        map.put("dimensionId", dimensionId);
        map.put("msg", msg);
        return request(super.createAppsTempletResponse("/apps/business/report/cube/detail"));
    }

    /**
     * 模型验证
     */
    @RequestMapping("/cubevalid")
    @Menu(type = "report", subtype = "cube")
    public ModelAndView cubevalid(ModelMap map, @Valid String id) {
        boolean hasMasterTable = false;
        Cube cube = cubeRes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Cube %s not found", id)));
        List<CubeMetadata> cubeMetadataList = cubeMetadataRes.findByCubeid(id);
        if (!cubeMetadataList.isEmpty()) {
            for (CubeMetadata cm : cubeMetadataList) {
                //至少一个主表
                if ("0".equals(cm.getMtype())) {
                    hasMasterTable = true;
                    break;
                }
            }
        }
        boolean hasLeastMeasure = false;
        if ("cube".equals(cube.getModeltype())) {
            //立方体必须至少一个指标
            List<CubeMeasure> cubeMeasureList = cubeMeasureRes.findByCubeid(id);
            if (!cubeMeasureList.isEmpty()) {
                hasLeastMeasure = true;
            }
        }
        String msg = "";
        if (!hasMasterTable) {
            msg = "CUBE_VALID_FAILED_1";
        } else if (!hasLeastMeasure) {
            msg = "CUBE_VALID_FAILED_2";
        }
        map.put("msg", msg);
        return request(super.createRequestPageTempletResponse("redirect:/apps/report/cube/detail.html?id=" + id + "&msg=" + msg));
    }

    /**
     * 模型发布页面加载
     */
    @RequestMapping("/cubepublish")
    @Menu(type = "report", subtype = "cube")
    public ModelAndView cubepublish(ModelMap map, @Valid String cubeid) {
        map.put("cubeid", cubeid);
        return request(super.createRequestPageTempletResponse("/apps/business/report/cube/cubepublish"));
    }

    /**
     * 模型发布
     */
    @RequestMapping("/cubepublished")
    @Menu(type = "report", subtype = "cube")
    public ModelAndView cubepublished(ModelMap map, HttpServletRequest request, @Valid String cubeid, @Valid String isRecover) throws Exception {
        this.cubevalid(map, cubeid);
        if (!StringUtils.isBlank((String) map.get("msg"))) {
            map.put("cubeid", cubeid);
            return request(super.createRequestPageTempletResponse("redirect:/apps/report/cube/index.html?msg=" + map.get("msg")));
        }
        User user = super.getUser(request);
        Cube cube = this.getCube(cubeid);
        PublishedCube publishCube = new PublishedCube();
        MainUtils.copyProperties(cube, publishCube, "");
        publishCube.setId(null);
        Base64 base64 = new Base64();
        publishCube.setCubecontent(base64.encodeToString(MainUtils.toBytes(cube)));
        publishCube.setDataid(cubeid);
        publishCube.setUserid(user.getId());
        publishCube.setUsername(user.getUsername());
        publishCube.setCreatetime(new Date());

        List<PublishedCube> pbCubeList = publishedCubeRes.findByOrgiAndDataidOrderByDataversionDesc(super.getOrgi(request), cubeid);
        if (!pbCubeList.isEmpty()) {
            int maxVersion = pbCubeList.get(0).getDataversion();
            if ("yes".equals(isRecover)) {
                publishCube.setId(pbCubeList.get(0).getId());
                publishCube.setDataversion(pbCubeList.get(0).getDataversion());
                publishedCubeRes.save(publishCube);
            } else if ("no".equals(isRecover)) {
                publishCube.setDataversion(maxVersion + 1);
                publishedCubeRes.save(publishCube);
            } else {
                publishedCubeRes.deleteAll(pbCubeList);
                publishCube.setDataversion(1);
                publishedCubeRes.save(publishCube);
            }
        } else {
            publishCube.setDataversion(1);
            publishedCubeRes.save(publishCube);
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/report/cube/index.html?typeid=" + cube.getTypeid()));
    }

    /**
     * 已发布模型列表
     */
    @RequestMapping("/pbcubeindex")
    @Menu(type = "report", subtype = "pbcube")
    public ModelAndView pbcubeindex(ModelMap map, HttpServletRequest request, @Valid String typeid) {
        List<CubeType> cubeTypeList = cubeTypeRes.findByOrgi(super.getOrgi(request));
        if (!StringUtils.isBlank(typeid)) {
            map.put("cubeType", cubeTypeRes.findByIdAndOrgi(typeid, super.getOrgi(request)));
            map.put("cubeList", publishedCubeRes.getByOrgiAndTypeid(super.getOrgi(request), typeid, PageRequest.of(super.getP(request), super.getPs(request))));
        } else {
            map.put("cubeList", publishedCubeRes.getByOrgi(super.getOrgi(request), PageRequest.of(super.getP(request), super.getPs(request))));
        }
        map.put("pubCubeTypeList", cubeTypeList);
        map.put("typeid", typeid);
        return request(super.createAppsTempletResponse("/apps/business/report/cube/pbCubeIndex"));
    }

    /**
     * 已发布模型列表
     */
    @RequestMapping("/pbcubelist")
    @Menu(type = "report", subtype = "pbcube")
    public ModelAndView pbcubelist(ModelMap map, HttpServletRequest request, @Valid String typeid) {
        if (!StringUtils.isBlank(typeid)) {
            map.put("cubeType", cubeTypeRes.findByIdAndOrgi(typeid, super.getOrgi(request)));
            map.put("cubeList", publishedCubeRes.getByOrgiAndTypeid(super.getOrgi(request), typeid, PageRequest.of(super.getP(request), super.getPs(request))));
        } else {
            map.put("cubeList", publishedCubeRes.getByOrgi(super.getOrgi(request), PageRequest.of(super.getP(request), super.getPs(request))));
        }
        map.put("typeid", typeid);
        return request(super.createRequestPageTempletResponse("/apps/business/report/cube/pbcubelist"));
    }

    /**
     * 已发布模型列表
     */
    @RequestMapping("/pbcubedelete")
    @Menu(type = "report", subtype = "pbcube")
    public ModelAndView pbcubedelete(@Valid String id) {
        PublishedCube pbCube = publishedCubeRes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Cube %s not found", id)));
        String typeid = "";
        if (pbCube != null) {
            typeid = pbCube.getTypeid();
            publishedCubeRes.delete(pbCube);
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/report/cube/pbcubeindex.html?typeid=" + typeid));
    }

    /**
     * 选择已发布模型列表
     */
    @RequestMapping("/selpbcubeindex")
    @Menu(type = "report", subtype = "pbcube")
    public ModelAndView selpbcubeindex(ModelMap map, HttpServletRequest request, @Valid String typeid, @Valid String mid) {
        List<CubeType> cubeTypeList = cubeTypeRes.findByOrgi(super.getOrgi(request));
        if (!StringUtils.isBlank(typeid)) {
            map.put("cubeType", cubeTypeRes.findByIdAndOrgi(typeid, super.getOrgi(request)));
            map.put("cubeList", publishedCubeRes.getByOrgiAndTypeid(super.getOrgi(request), typeid, PageRequest.of(super.getP(request), super.getPs(request))));
        } else {
            map.put("cubeList", publishedCubeRes.getByOrgi(super.getOrgi(request), PageRequest.of(super.getP(request), super.getPs(request))));
        }
        map.put("pubCubeTypeList", cubeTypeList);
        map.put("typeid", typeid);
        map.put("mid", mid);
        return request(super.createRequestPageTempletResponse("/apps/business/report/design/cube/pbCubeIndex"));
    }

    /**
     * 选择已发布模型列表
     */
    @RequestMapping("/selpbcubelist")
    @Menu(type = "report", subtype = "pbcube")
    public ModelAndView selpbcubelist(ModelMap map, HttpServletRequest request, @Valid String typeid, @Valid String mid) {
        if (!StringUtils.isBlank(typeid)) {
            map.put("cubeType", cubeTypeRes.findByIdAndOrgi(typeid, super.getOrgi(request)));
            map.put("cubeList", publishedCubeRes.getByOrgiAndTypeid(super.getOrgi(request), typeid, PageRequest.of(super.getP(request), super.getPs(request))));
        } else {
            map.put("cubeList", publishedCubeRes.getByOrgi(super.getOrgi(request), PageRequest.of(super.getP(request), super.getPs(request))));
        }
        map.put("typeid", typeid);
        map.put("mid", mid);
        return request(super.createRequestPageTempletResponse("/apps/business/report/design/cube/pbcubelist"));
    }

    private Cube getCube(String id) {
        Cube cube = cubeRes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Cube %s not found", id)));
        if (cube != null) {
            cube.setMetadata(cubeMetadataRes.findByCubeid(id));
            cube.setMeasure(cubeMeasureRes.findByCubeid(id));
            cube.setDimension(dimensionRes.findByCubeid(id));
        }
        return cube;

    }


}
