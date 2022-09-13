/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
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
package com.cskefu.cc.controller.resource;

import com.alibaba.fastjson.JSONObject;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.controller.api.request.RestUtils;
import com.cskefu.cc.model.AttachmentFile;
import com.cskefu.cc.model.StreamingFile;
import com.cskefu.cc.persistence.blob.JpaBlobHelper;
import com.cskefu.cc.persistence.repository.AttachmentRepository;
import com.cskefu.cc.persistence.repository.StreamingFileRepository;
import com.cskefu.cc.util.Menu;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.SQLException;

@Controller
@RequestMapping("/res")
public class MediaController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(MediaController.class);

    @Value("${web.upload-path}")
    private String path;

    @Autowired
    private StreamingFileRepository streamingFileRes;

    @Autowired
    private JpaBlobHelper jpaBlobHelper;

    private String TEMPLATE_DATA_PATH = "WEB-INF/data/templates/";

    @Autowired
    private AttachmentRepository attachementRes;

    @RequestMapping("/image")
    @Menu(type = "resouce", subtype = "image", access = true)
    public void index(HttpServletResponse response,
                      @Valid String id,
                      @RequestParam(value = "original", required = false) boolean original,
                      @RequestParam(value = "cooperation", required = false) boolean cooperation) throws IOException, SQLException {
        StreamingFile sf = streamingFileRes.findOne(id);
        if (sf != null) {
            response.setHeader("Content-Type", sf.getMime());
            response.setContentType(sf.getMime());
            if (cooperation && (sf.getCooperation() != null)) { // 协作文件
                IOUtils.copy(sf.getCooperation().getBinaryStream(), response.getOutputStream());
            } else if (original && sf.getData() != null) { // 源文件
                IOUtils.copy(sf.getData().getBinaryStream(), response.getOutputStream());
            } else if (sf.getThumbnail() != null) { // 缩略图
                IOUtils.copy(sf.getThumbnail().getBinaryStream(), response.getOutputStream());
            } else if (sf.getData() != null) {
                IOUtils.copy(sf.getData().getBinaryStream(), response.getOutputStream());
            } else {
                logger.warn("[index] can not get streaming file id {}, original {}, cooperation {}", id, original, cooperation);
            }
        }
    }

    @RequestMapping("/url")
    @Menu(type = "resouce", subtype = "image", access = true)
    public void url(HttpServletResponse response, @Valid String url) throws IOException {
        if (StringUtils.isBlank(url)) {
            return;
        }
        byte[] data = new byte[1024];
        int length = 0;
        OutputStream out = response.getOutputStream();
        try (InputStream input = new URL(url).openStream()) {
            while ((length = input.read(data)) > 0) {
                out.write(data, 0, length);
            }
        }

    }

    @RequestMapping("/image/upload")
    @Menu(type = "resouce", subtype = "imageupload", access = false)
    public ResponseEntity<String> upload(ModelMap map,
                                         HttpServletRequest request,
                                         @RequestParam(value = "imgFile", required = false) MultipartFile multipart) throws IOException {
        JSONObject result = new JSONObject();
        HttpHeaders headers = RestUtils.header();
        if (multipart != null && multipart.getOriginalFilename().lastIndexOf(".") > 0) {
            File uploadDir = new File(path, "upload");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            String fileid = MainUtils.getUUID();
            StreamingFile sf = new StreamingFile();
            sf.setId(fileid);
            sf.setName(multipart.getOriginalFilename());
            sf.setMime(multipart.getContentType());
            sf.setData(jpaBlobHelper.createBlob(multipart.getInputStream(), multipart.getSize()));
            streamingFileRes.save(sf);
            String fileURL = "/res/image.html?id=" + fileid;
            result.put("error", 0);
            result.put("url", fileURL); //图片直接发送给 客户，不用返回
        } else {
            result.put("error", 1);
            result.put("message", "请选择图片文件");
        }

        return new ResponseEntity<>(result.toString(), headers, HttpStatus.OK);

    }

    @RequestMapping("/file")
    @Menu(type = "resouce", subtype = "file", access = false)
    public void file(HttpServletResponse response, HttpServletRequest request, @Valid String id) throws IOException, SQLException {
        if (StringUtils.isNotBlank(id)) {
            AttachmentFile attachmentFile = attachementRes.findByIdAndOrgi(id, super.getOrgi(request));
            if (attachmentFile != null && attachmentFile.getFileid() != null) {
                StreamingFile sf = streamingFileRes.findOne(attachmentFile.getFileid());
                if (sf != null) {
                    response.setContentType(attachmentFile.getFiletype());
                    response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(attachmentFile.getTitle(), "UTF-8"));
                    IOUtils.copy(sf.getData().getBinaryStream(), response.getOutputStream());
                } else {
                    logger.warn("[streaming file] can not get file id {}", attachmentFile.getFileid());
                }
            } else {
                logger.warn("[attachment file] can not find attachment file id {}", id);
            }
        }
    }

    @RequestMapping("/template")
    @Menu(type = "resouce", subtype = "template", access = false)
    public void template(HttpServletResponse response, HttpServletRequest request, @Valid String filename) throws IOException {
        if (StringUtils.isNotBlank(filename)) {
            InputStream is = MediaController.class.getClassLoader().getResourceAsStream(TEMPLATE_DATA_PATH + filename);
            if (is != null) {
                response.setContentType("text/plain");
                response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(filename, "UTF-8"));
                int length;
                byte[] data = new byte[1024];
                while ((length = is.read(data)) > 0) {
                    response.getOutputStream().write(data, 0, length);
                }
                is.close();
            }
        }
        return;
    }

}