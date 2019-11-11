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
package com.chatopera.cc.controller.admin.system;

import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.SysDic;
import com.chatopera.cc.persistence.repository.SysDicRepository;
import com.chatopera.cc.util.Menu;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin/sysdic")
public class SysDicController extends Handler {


    @Autowired
    private SysDicRepository sysDicRes;

    @Autowired
    private Cache cache;

    @RequestMapping("/index")
    @Menu(type = "admin", subtype = "sysdic")
    public ModelAndView index(ModelMap map, HttpServletRequest request) {
        map.addAttribute("sysDicList", sysDicRes.findByParentid("0", new PageRequest(super.getP(request), super.getPs(request), Direction.DESC, "createtime")));
        return request(super.createAdminTempletResponse("/admin/system/sysdic/index"));
    }

    @RequestMapping("/add")
    @Menu(type = "admin", subtype = "sysdic")
    public ModelAndView add(ModelMap map, HttpServletRequest request) {
        return request(super.createRequestPageTempletResponse("/admin/system/sysdic/add"));
    }

    @RequestMapping("/save")
    @Menu(type = "admin", subtype = "sysdic")
    public ModelAndView save(HttpServletRequest request, @Valid SysDic dic) {
        List<SysDic> sysDicList = sysDicRes.findByCodeOrName(dic.getCode(), dic.getName());
        String msg = null;
        String orgi = super.getOrgi(request);
        if (sysDicList.size() == 0) {
            dic.setParentid("0");
            dic.setHaschild(true);
            dic.setCreater(super.getUser(request).getId());
            dic.setCreatetime(new Date());
            sysDicRes.save(dic);
            reloadSysDicItem(dic, orgi);
        } else {
            msg = "exist";
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/sysdic/index.html" + (msg != null ? "?msg=" + msg : "")));
    }

    @RequestMapping("/edit")
    @Menu(type = "admin", subtype = "sysdic")
    public ModelAndView edit(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String p) {
        map.addAttribute("sysDic", sysDicRes.findById(id));
        map.addAttribute("p", p);
        return request(super.createRequestPageTempletResponse("/admin/system/sysdic/edit"));
    }

    @RequestMapping("/update")
    @Menu(type = "admin", subtype = "sysdic")
    public ModelAndView update(HttpServletRequest request, @Valid SysDic dic, @Valid String p) {
        List<SysDic> sysDicList = sysDicRes.findByCodeOrName(dic.getCode(), dic.getName());
        if (sysDicList.size() == 0 || (sysDicList.size() == 1 && sysDicList.get(0).getId().equals(dic.getId()))) {
            SysDic sysDic = sysDicRes.findById(dic.getId());
            sysDic.setName(dic.getName());
            sysDic.setCode(dic.getCode());
            sysDic.setCtype(dic.getCtype());
            sysDic.setIconskin(dic.getIconskin());
            sysDic.setIconstr(dic.getIconstr());
            sysDic.setDescription(dic.getDescription());
            sysDicRes.save(sysDic);
            String orgi = super.getOrgi(request);
            reloadSysDicItem(sysDic, orgi);
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/sysdic/index.html?p=" + p));
    }

    @RequestMapping("/delete")
    @Menu(type = "admin", subtype = "sysdic")
    public ModelAndView delete(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String p) {
        SysDic sysDic = sysDicRes.findById(id);
        sysDicRes.delete(sysDicRes.findByDicid(id));
        sysDicRes.delete(sysDic);

        reloadSysDicItem(sysDic, super.getOrgi(request));

        return request(super.createRequestPageTempletResponse("redirect:/admin/sysdic/index.html?p=" + p));
    }

    @RequestMapping("/dicitem")
    @Menu(type = "admin", subtype = "sysdic")
    public ModelAndView dicitem(ModelMap map, HttpServletRequest request, @Valid String id) {
        map.addAttribute("sysDic", sysDicRes.findById(id));
        map.addAttribute("sysDicList", sysDicRes.findByParentid(id, new PageRequest(super.getP(request), super.getPs(request), Direction.DESC, "createtime")));
        return request(super.createAdminTempletResponse("/admin/system/sysdic/dicitem"));
    }

    @RequestMapping("/dicitem/add")
    @Menu(type = "admin", subtype = "sysdic")
    public ModelAndView dicitemadd(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String p) {
        map.addAttribute("sysDic", sysDicRes.findById(id));
        map.addAttribute("p", p);
        return request(super.createRequestPageTempletResponse("/admin/system/sysdic/dicitemadd"));
    }

    @RequestMapping("/dicitem/save")
    @Menu(type = "admin", subtype = "sysdic")
    public ModelAndView dicitemsave(HttpServletRequest request, @Valid SysDic dic, @Valid String p) {
        List<SysDic> sysDicList = sysDicRes.findByDicidAndName(dic.getDicid(), dic.getName());
        String msg = null;
        String orgi = super.getOrgi(request);
        if (sysDicList.size() == 0) {
            dic.setHaschild(true);
            dic.setOrgi(orgi);
            dic.setCreater(super.getUser(request).getId());
            dic.setCreatetime(new Date());
            sysDicRes.save(dic);
            reloadSysDicItem(dic, orgi);
        } else {
            msg = "exist";
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/sysdic/dicitem.html?id=" + dic.getParentid() + (msg != null ? "&p=" + p + "&msg=" + msg : "")));
    }

    /**
     * 更新系统词典缓存
     * @param dic
     * @param orgi
     */
    public void reloadSysDicItem(final SysDic dic, final String orgi) {
        cache.putSysDicByOrgi(dic.getId(), orgi, dic);
        if (StringUtils.isNotBlank(dic.getDicid())) { // 该数据为某词典的子项
            // 首先获得根词典
            SysDic root = cache.findOneSysDicByIdAndOrgi(dic.getDicid(), orgi);
            // 获得其目前的全部子项，此处是从数据库提取
            // 而不是缓存，因为缓存的数据已经旧了
            List<SysDic> sysDicList = sysDicRes.findByDicid(dic.getDicid());
            // 更新其全部子项数据到缓存
            cache.putSysDicByOrgi(root.getCode(), orgi, sysDicList);
        } else if (dic.getParentid().equals("0")) {
            // 如果该数据为某根词典
            List<SysDic> sysDicList = sysDicRes.findByDicid(dic.getId());
            cache.putSysDicByOrgi(dic.getCode(), orgi, sysDicList);
        }
    }

    @RequestMapping("/dicitem/batadd")
    @Menu(type = "admin", subtype = "sysdic")
    public ModelAndView dicitembatadd(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String p) {
        map.addAttribute("sysDic", sysDicRes.findById(id));
        map.addAttribute("p", p);
        return request(super.createRequestPageTempletResponse("/admin/system/sysdic/batadd"));
    }

    @RequestMapping("/dicitem/batsave")
    @Menu(type = "admin", subtype = "sysdic")
    public ModelAndView dicitembatsave(HttpServletRequest request, @Valid SysDic sysDic, @Valid String content, @Valid String p) {
        String[] dicitems = content.split("[\n\r\n]");
        int count = 0;
        String orig = super.getOrgi(request);
        for (String dicitem : dicitems) {
            String[] dicValues = dicitem.split("[\t, ;]{1,}");
            if (dicValues.length == 2 && dicValues[0].length() > 0 && dicValues[1].length() > 0) {
                SysDic dic = new SysDic();
                dic.setOrgi(orig);

                dic.setName(dicValues[0]);
                dic.setCode(dicValues[1]);
                dic.setCreater(super.getUser(request).getId());
                dic.setParentid(sysDic.getParentid());
                dic.setDicid(sysDic.getDicid());
                dic.setSortindex(++count);
                dic.setCreatetime(new Date());
                dic.setUpdatetime(new Date());
                if (sysDicRes.findByDicidAndName(dic.getDicid(), dic.getName()).size() == 0) {
                    sysDicRes.save(dic);
                }

            }
        }
        reloadSysDicItem(sysDicRes.getOne(sysDic.getParentid()), orig);

        return request(super.createRequestPageTempletResponse("redirect:/admin/sysdic/dicitem.html?id=" + sysDic.getParentid() + "&p=" + p));
    }

    @RequestMapping("/dicitem/edit")
    @Menu(type = "admin", subtype = "sysdic")
    public ModelAndView dicitemedit(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String p) {
        map.addAttribute("sysDic", sysDicRes.findById(id));
        map.addAttribute("p", p);
        return request(super.createRequestPageTempletResponse("/admin/system/sysdic/dicitemedit"));
    }

    @RequestMapping("/dicitem/update")
    @Menu(type = "admin", subtype = "sysdic")
    public ModelAndView dicitemupdate(HttpServletRequest request, @Valid SysDic dic, @Valid String p) {
        List<SysDic> sysDicList = sysDicRes.findByDicidAndName(dic.getDicid(), dic.getName());
        String orgi = super.getOrgi(request);
        if (sysDicList.size() == 0 || (sysDicList.size() == 1 && sysDicList.get(0).getId().equals(dic.getId()))) {
            SysDic sysDic = sysDicRes.findById(dic.getId());
            sysDic.setName(dic.getName());
            sysDic.setCode(dic.getCode());
            sysDic.setCtype(dic.getCtype());
            sysDic.setOrgi(orgi);
            sysDic.setIconskin(dic.getIconskin());
            sysDic.setIconstr(dic.getIconstr());
            sysDic.setDiscode(dic.isDiscode());
            sysDic.setDescription(dic.getDescription());
            sysDicRes.save(sysDic);

            reloadSysDicItem(sysDic, orgi);
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/sysdic/dicitem.html?id=" + dic.getParentid() + "&p=" + p));
    }

    @RequestMapping("/dicitem/delete")
    @Menu(type = "admin", subtype = "sysdic")
    public ModelAndView dicitemdelete(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String p) {
        sysDicRes.delete(sysDicRes.findByDicid(id));
        SysDic dic = sysDicRes.getOne(id);
        sysDicRes.delete(dic);
        reloadSysDicItem(dic, super.getOrgi(request));
        return request(super.createRequestPageTempletResponse("redirect:/admin/sysdic/dicitem.html?id=" + dic.getParentid() + "&p=" + p));
    }

}