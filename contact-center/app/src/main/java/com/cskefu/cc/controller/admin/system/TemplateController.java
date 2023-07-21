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
package com.cskefu.cc.controller.admin.system;

import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.model.Dict;
import com.cskefu.cc.model.SysDic;
import com.cskefu.cc.model.Template;
import com.cskefu.cc.persistence.repository.SysDicRepository;
import com.cskefu.cc.persistence.repository.TemplateRepository;
import com.cskefu.cc.util.Menu;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin/template")
public class TemplateController extends Handler {


    @Autowired
    private TemplateRepository templateRes;

    @Autowired
    private SysDicRepository dicRes;

    @Autowired
    private Cache cache;

    @RequestMapping("/index")
    @Menu(type = "admin", subtype = "template", admin = true)
    public ModelAndView index(ModelMap map, HttpServletRequest request) {
        map.addAttribute("sysDicList", Dict.getInstance().getDic(Constants.CSKEFU_SYSTEM_DIC));
        return request(super.createView("/admin/system/template/index"));
    }

    @RequestMapping("/expall")
    @Menu(type = "admin", subtype = "template", admin = true)
    public void expall(ModelMap map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Template> templateList = templateRes.findAll();
        response.setHeader("content-disposition", "attachment;filename=CSKeFu-Template-Export-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".data");
        response.getOutputStream().write(MainUtils.toBytes(templateList));
        return;
    }

    @RequestMapping("/imp")
    @Menu(type = "admin", subtype = "template", admin = true)
    public ModelAndView imp(ModelMap map, HttpServletRequest request) {
        return request(super.createView("/admin/system/template/imp"));
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/impsave")
    @Menu(type = "admin", subtype = "template", admin = true)
    public ModelAndView impsave(ModelMap map, HttpServletRequest request, @RequestParam(value = "dataFile", required = false) MultipartFile dataFile) throws Exception {
        if (dataFile != null && dataFile.getSize() > 0) {
            List<Template> templateList = (List<Template>) MainUtils.toObject(dataFile.getBytes());
            if (templateList != null && templateList.size() > 0) {
                templateRes.deleteInBatch(templateList);
                for (Template template : templateList) {
                    templateRes.save(template);
                }
            }
        }
        return request(super.createView("redirect:/admin/template/index.html"));
    }

    @RequestMapping("/list")
    @Menu(type = "admin", subtype = "template", admin = true)
    public ModelAndView list(ModelMap map, HttpServletRequest request, @Valid String type) {
        map.addAttribute("sysDic", dicRes.findById(type).orElse(null));
        map.addAttribute("templateList", templateRes.findByTemplettype(type));
        return request(super.createView("/admin/system/template/list"));
    }

    @RequestMapping("/add")
    @Menu(type = "admin", subtype = "template", admin = true)
    public ModelAndView add(ModelMap map, HttpServletRequest request, @Valid String type) {
        map.addAttribute("sysDic", dicRes.findById(type).orElse(null));
        return request(super.createView("/admin/system/template/add"));
    }

    @RequestMapping("/save")
    @Menu(type = "admin", subtype = "template", admin = true)
    public ModelAndView save(HttpServletRequest request, @Valid Template template) {
        template.setCreatetime(new Date());

        SysDic dic = dicRes.findById(template.getTemplettype()).orElse(null);
        if (dic != null && StringUtils.isBlank(template.getCode())) {
            template.setCode(dic.getCode());
        }
        templateRes.save(template);

        return request(super.createView("redirect:/admin/template/list.html?type=" + template.getTemplettype()));
    }

    @RequestMapping("/edit")
    @Menu(type = "admin", subtype = "template", admin = true)
    public ModelAndView edit(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String type) {
        map.addAttribute("sysDic", dicRes.findById(type).orElse(null));
        map.addAttribute("template", templateRes.findById(id).orElse(null));
        return request(super.createView("/admin/system/template/edit"));
    }

    @RequestMapping("/update")
    @Menu(type = "admin", subtype = "template", admin = true)
    public ModelAndView update(HttpServletRequest request, @Valid Template template) {
        Template oldTemplate = templateRes.findById(template.getId()).orElse(null);
        if (oldTemplate != null) {
            SysDic dic = dicRes.findById(oldTemplate.getTemplettype()).orElse(null);
            if (dic != null) {
                oldTemplate.setCode(dic.getCode());
            }
            if (!StringUtils.isBlank(template.getCode())) {
                oldTemplate.setCode(template.getCode());
            }
            oldTemplate.setName(template.getName());
            oldTemplate.setLayoutcols(template.getLayoutcols());
            oldTemplate.setIconstr(template.getIconstr());
            oldTemplate.setDatatype(template.getDatatype());
            oldTemplate.setCharttype(template.getCharttype());
            templateRes.save(oldTemplate);

            cache.deleteSystembyId(template.getId());
        }
        return request(super.createView("redirect:/admin/template/list.html?type=" + template.getTemplettype()));
    }

    @RequestMapping("/code")
    @Menu(type = "admin", subtype = "template", admin = true)
    public ModelAndView code(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String type) {
        map.addAttribute("sysDic", dicRes.findById(type).orElse(null));
        map.addAttribute("template", templateRes.findById(id).orElse(null));
        return request(super.createView("/admin/system/template/code"));
    }

    @RequestMapping("/codesave")
    @Menu(type = "admin", subtype = "template", admin = true)
    public ModelAndView codesave(HttpServletRequest request, @Valid Template template) {
        Template oldTemplate = templateRes.findById(template.getId()).orElse(null);
        if (oldTemplate != null) {
            oldTemplate.setTemplettext(template.getTemplettext());
            oldTemplate.setTemplettitle(template.getTemplettitle());
            templateRes.save(oldTemplate);

            cache.deleteSystembyId(template.getId());
        }
        return request(super.createView("redirect:/admin/template/list.html?type=" + template.getTemplettype()));
    }

    @RequestMapping("/delete")
    @Menu(type = "admin", subtype = "template", admin = true)
    public ModelAndView delete(HttpServletRequest request, @Valid Template template) {
        if (template != null) {
            templateRes.delete(template);

            cache.deleteSystembyId(template.getId());
        }
        return request(super.createView("redirect:/admin/template/list.html?type=" + template.getTemplettype()));
    }

}