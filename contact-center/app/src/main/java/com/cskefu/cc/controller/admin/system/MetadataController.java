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

import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.model.*;
import com.cskefu.cc.persistence.hibernate.BaseService;
import com.cskefu.cc.persistence.repository.MetadataRepository;
import com.cskefu.cc.persistence.repository.SysDicRepository;
import com.cskefu.cc.persistence.repository.TablePropertiesRepository;
import com.cskefu.cc.util.Menu;
import com.cskefu.cc.util.metadata.DatabaseMetaDataHandler;
import com.cskefu.cc.util.metadata.UKColumnMetadata;
import com.cskefu.cc.util.metadata.UKTableMetaData;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@Controller
@RequestMapping("/admin/metadata")
public class MetadataController extends Handler {

    @Autowired
    private MetadataRepository metadataRes;

    @Autowired
    private BaseService<?> service;

    @Autowired
    private SysDicRepository sysDicRes;

    @Autowired
    private TablePropertiesRepository tablePropertiesRes;

    private static final Logger logger = LoggerFactory.getLogger(MetadataController.class);

    @Autowired
    @PersistenceContext
    private EntityManager em;

    @RequestMapping("/index")
    @Menu(type = "admin", subtype = "metadata", admin = true)
    public ModelAndView index(ModelMap map, HttpServletRequest request) throws SQLException {
        map.addAttribute("metadataList", metadataRes.findAll(PageRequest.of(super.getP(request), super.getPs(request))));
        return request(super.createView("/admin/system/metadata/index"));
    }

    @RequestMapping("/edit")
    @Menu(type = "admin", subtype = "metadata", admin = true)
    public ModelAndView edit(ModelMap map, HttpServletRequest request, @Valid String id) {
        map.addAttribute("metadata", metadataRes.findById(id).orElse(null));
        return request(super.createView("/admin/system/metadata/edit"));
    }

    @RequestMapping("/update")
    @Menu(type = "admin", subtype = "metadata", admin = true)
    public ModelAndView update(ModelMap map, HttpServletRequest request, @Valid MetadataTable metadata) throws SQLException {
        MetadataTable table = metadataRes.findById(metadata.getId()).orElse(null);
        table.setName(metadata.getName());
        table.setFromdb(metadata.isFromdb());
        table.setListblocktemplet(metadata.getListblocktemplet());
        table.setPreviewtemplet(metadata.getPreviewtemplet());
        metadataRes.save(table);
        return request(super.createView("redirect:/admin/metadata/index.html"));
    }

    @RequestMapping("/properties/edit")
    @Menu(type = "admin", subtype = "metadata", admin = true)
    public ModelAndView propertiesedit(ModelMap map, HttpServletRequest request, @Valid String id) {
        map.addAttribute("tp", tablePropertiesRes.findById(id).orElse(null));
        map.addAttribute("sysdicList", sysDicRes.findByParentid("0"));
        map.addAttribute("dataImplList", Dict.getInstance().getDic("com.dic.data.impl"));

        return request(super.createView("/admin/system/metadata/tpedit"));
    }

    @RequestMapping("/properties/update")
    @Menu(type = "admin", subtype = "metadata", admin = true)
    public ModelAndView propertiesupdate(ModelMap map, HttpServletRequest request, @Valid TableProperties tp) throws SQLException {
        TableProperties tableProperties = tablePropertiesRes.findById(tp.getId()).orElse(null);
        tableProperties.setName(tp.getName());
        tableProperties.setSeldata(tp.isSeldata());
        tableProperties.setSeldatacode(tp.getSeldatacode());

        tableProperties.setReffk(tp.isReffk());
        tableProperties.setReftbid(tp.getReftbid());

        tableProperties.setDefaultvaluetitle(tp.getDefaultvaluetitle());
        tableProperties.setDefaultfieldvalue(tp.getDefaultfieldvalue());

        tableProperties.setModits(tp.isModits());
        tableProperties.setPk(tp.isPk());

        tableProperties.setSystemfield(tp.isSystemfield());

        tableProperties.setImpfield(tp.isImpfield());

        tablePropertiesRes.save(tableProperties);
        return request(super.createView("redirect:/admin/metadata/table.html?id=" + tableProperties.getDbtableid()));
    }

    @RequestMapping("/delete")
    @Menu(type = "admin", subtype = "metadata", admin = true)
    public ModelAndView delete(ModelMap map, HttpServletRequest request, @Valid String id) throws SQLException {
        MetadataTable table = metadataRes.findById(id).orElse(null);
        metadataRes.delete(table);
        return request(super.createView("redirect:/admin/metadata/index.html"));
    }

    @RequestMapping("/batdelete")
    @Menu(type = "admin", subtype = "metadata", admin = true)
    public ModelAndView batdelete(ModelMap map, HttpServletRequest request, @Valid String[] ids) throws SQLException {
        if (ids != null && ids.length > 0) {
            metadataRes.deleteAll(metadataRes.findAllById(Arrays.asList(ids)));
        }
        return request(super.createView("redirect:/admin/metadata/index.html"));
    }

    @RequestMapping("/properties/delete")
    @Menu(type = "admin", subtype = "metadata", admin = true)
    public ModelAndView propertiesdelete(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String tbid) throws SQLException {
        TableProperties prop = tablePropertiesRes.findById(id).orElse(null);
        tablePropertiesRes.delete(prop);
        return request(super.createView("redirect:/admin/metadata/table.html?id=" + (!StringUtils.isBlank(tbid) ? tbid : prop.getDbtableid())));
    }

    @RequestMapping("/properties/batdelete")
    @Menu(type = "admin", subtype = "metadata", admin = true)
    public ModelAndView propertiesbatdelete(ModelMap map, HttpServletRequest request, @Valid String[] ids, @Valid String tbid) throws SQLException {
        if (ids != null && ids.length > 0) {
            tablePropertiesRes.deleteAll(tablePropertiesRes.findAllById(Arrays.asList(ids)));
        }
        return request(super.createView("redirect:/admin/metadata/table.html?id=" + tbid));
    }

    @RequestMapping("/table")
    @Menu(type = "admin", subtype = "metadata", admin = true)
    public ModelAndView table(ModelMap map, HttpServletRequest request, @Valid String id) throws SQLException {
        map.addAttribute("propertiesList", tablePropertiesRes.findByDbtableid(id));
        map.addAttribute("tbid", id);
        map.addAttribute("table", metadataRes.findById(id).orElse(null));
        return request(super.createView("/admin/system/metadata/table"));
    }

    @RequestMapping("/imptb")
    @Menu(type = "admin", subtype = "metadata", admin = true)
    public ModelAndView imptb(final ModelMap map, HttpServletRequest request) throws Exception {

        Session session = (Session) em.getDelegate();
        session.doWork(connection -> {
            try {
                map.addAttribute("tablesList",
                        DatabaseMetaDataHandler.getTables(connection));
            } catch (Exception e) {
                logger.error("When import metadata", e);
            }
        });

        return request(super
                .createView("/admin/system/metadata/imptb"));
    }

    @RequestMapping("/imptbsave")
    @Menu(type = "admin", subtype = "metadata", admin = true)
    public ModelAndView imptb(ModelMap map, HttpServletRequest request, final @Valid String[] tables) throws Exception {
        final User user = super.getUser(request);
        if (tables != null && tables.length > 0) {
            Session session = (Session) em.getDelegate();
            session.doWork(
                    connection -> {
                        try {
                            for (String table : tables) {
                                int count = metadataRes.countByTablename(table);
                                if (count == 0) {
                                    MetadataTable metaDataTable = new MetadataTable();
                                    //当前记录没有被添加过，进行正常添加
                                    metaDataTable.setTablename(table);
                                    metaDataTable.setId(MainUtils.md5(metaDataTable.getTablename()));
                                    metaDataTable.setTabledirid("0");
                                    metaDataTable.setCreater(user.getId());
                                    metaDataTable.setCreatername(user.getUsername());
                                    metaDataTable.setName(table);
                                    metaDataTable.setUpdatetime(new Date());
                                    metaDataTable.setCreatetime(new Date());
                                    metadataRes.save(processMetadataTable(DatabaseMetaDataHandler.getTable(connection, metaDataTable.getTablename()), metaDataTable));
                                }
                            }
                        } catch (Exception e) {
                            logger.error("When import metadata", e);
                        }
                    }
            );

        }

        return request(super.createView("redirect:/admin/metadata/index.html"));
    }

    private MetadataTable processMetadataTable(UKTableMetaData metaData, MetadataTable table) {
        table.setTableproperty(new ArrayList<>());
        if (metaData != null) {
            for (UKColumnMetadata colum : metaData.getColumnMetadatas()) {
                TableProperties tablePorperties = new TableProperties(colum.getName().toLowerCase(), colum.getTypeName(), colum.getColumnSize(), metaData.getName().toLowerCase());
                tablePorperties.setDatatypecode(0);
                tablePorperties.setLength(colum.getColumnSize());
                tablePorperties.setDatatypename(getDataTypeName(colum.getTypeName()));
                tablePorperties.setName(colum.getTitle().toLowerCase());
                if (tablePorperties.getFieldname().equals("create_time") || tablePorperties.getFieldname().equals("createtime") || tablePorperties.getFieldname().equals("update_time")) {
                    tablePorperties.setDatatypename(getDataTypeName("datetime"));
                }
                if (colum.getName().startsWith("field")) {
                    tablePorperties.setFieldstatus(false);
                } else {
                    tablePorperties.setFieldstatus(true);
                }
                table.getTableproperty().add(tablePorperties);
            }
            table.setTablename(table.getTablename().toLowerCase());//转小写
        }
        return table;
    }

    public String getDataTypeName(String type) {
        String typeName = "text";
        if (type.contains("varchar")) {
            typeName = "text";
        } else if (type.equalsIgnoreCase("date") || type.equalsIgnoreCase("datetime")) {
            typeName = type.toLowerCase();
        } else if (type.equalsIgnoreCase("int") || type.equalsIgnoreCase("float") || type.equalsIgnoreCase("number")) {
            typeName = "number";
        }
        return typeName;
    }

    @RequestMapping("/clean")
    @Menu(type = "admin", subtype = "metadata", admin = true)
    public ModelAndView clean(ModelMap map, HttpServletRequest request, @Valid String id) throws SQLException, BeansException, ClassNotFoundException {
        if (!StringUtils.isBlank(id)) {
            MetadataTable table = metadataRes.findById(id).orElse(null);
        }
        return request(super.createView("redirect:/admin/metadata/index.html"));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping("/synctoes")
    @Menu(type = "admin", subtype = "metadata", admin = true)
    public ModelAndView synctoes(ModelMap map, HttpServletRequest request, @Valid String id) throws SQLException, BeansException, ClassNotFoundException {
        if (!StringUtils.isBlank(id)) {
            MetadataTable table = metadataRes.findById(id).orElse(null);
            if (table.isFromdb() && !StringUtils.isBlank(table.getListblocktemplet())) {
                SysDic dic = Dict.getInstance().getDicItem(table.getListblocktemplet());
            }
        }
        return request(super.createView("redirect:/admin/metadata/index.html"));
    }

    @SuppressWarnings({"rawtypes"})
    @RequestMapping("/synctodb")
    @Menu(type = "admin", subtype = "metadata", admin = true)
    public ModelAndView synctodb(ModelMap map, HttpServletRequest request, @Valid String id) throws SQLException, BeansException, ClassNotFoundException {
        if (!StringUtils.isBlank(id)) {
            MetadataTable table = metadataRes.findById(id).orElse(null);
            if (table.isFromdb() && !StringUtils.isBlank(table.getListblocktemplet())) {
                SysDic dic = Dict.getInstance().getDicItem(table.getListblocktemplet());
            }
        }
        return request(super.createView("redirect:/admin/metadata/index.html"));
    }

}