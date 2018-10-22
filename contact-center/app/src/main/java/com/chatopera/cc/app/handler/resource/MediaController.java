/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.app.handler.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.chatopera.cc.util.Menu;
import com.chatopera.cc.app.basic.MainUtils;
import com.chatopera.cc.app.persistence.repository.AttachmentRepository;
import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.model.AttachmentFile;
import com.chatopera.cc.app.model.UploadStatus;

@Controller
@RequestMapping("/res")
public class MediaController extends Handler {

    @Value("${web.upload-path}")
    private String path;

    private String TEMPLATE_DATA_PATH = "WEB-INF/data/templates/";

    @Autowired
    private AttachmentRepository attachementRes;

    @RequestMapping("/image")
    @Menu(type = "resouce", subtype = "image", access = true)
    public void index(HttpServletResponse response, @Valid String id) throws IOException {
        File file = new File(path, id);
        if (!StringUtils.isBlank(id) && !(id.endsWith(".png") || id.endsWith(".jpg"))) {
            if (id.endsWith("_original") && !file.exists()) {
                File orgFile = new File(path, id.substring(0, id.indexOf("_original")));
                if (orgFile.exists()) {
                    MainUtils.processImage(file = new File(path, id), orgFile);
                }
            } else if (!StringUtils.isBlank(id) && file.exists() && !id.endsWith("_original")) {
                File originalFile = new File(path, id + "_original");
                if (!originalFile.exists()) {
                    MainUtils.processImage(new File(path, id + "_original"), file);
                }
            } else if (!StringUtils.isBlank(id) && !file.exists() && !id.endsWith("_original")) {
                File destFile = new File(path, id + "_original");
                if (destFile.exists()) {
                    MainUtils.processImage(new File(path + id), destFile);
                }
                file = new File(path, id);
            }
        }
        if (file.exists() && file.isFile()) {
            response.setHeader("Content-Type", "image/png");
            response.setContentType("image/png");
            response.getOutputStream().write(FileUtils.readFileToByteArray(new File(path, id)));
        }
    }

    @RequestMapping("/voice")
    @Menu(type = "resouce", subtype = "voice", access = true)
    public void voice(HttpServletResponse response, @Valid String id) throws IOException {
        File file = new File(path, id);
        if (file.exists() && file.isFile()) {
            response.getOutputStream().write(FileUtils.readFileToByteArray(new File(path, id)));
        }
    }

    @RequestMapping("/url")
    @Menu(type = "resouce", subtype = "image", access = true)
    public void url(HttpServletResponse response, @Valid String url) throws IOException {
        byte[] data = new byte[1024];
        int length = 0;
        OutputStream out = response.getOutputStream();
        if (!StringUtils.isBlank(url)) {
            InputStream input = new URL(url).openStream();
            while ((length = input.read(data)) > 0) {
                out.write(data, 0, length);
            }
            input.close();
        }
    }

    @RequestMapping("/image/upload")
    @Menu(type = "resouce", subtype = "imageupload", access = false)
    public ModelAndView upload(ModelMap map, HttpServletRequest request, @RequestParam(value = "imgFile", required = false) MultipartFile imgFile) throws IOException {
        ModelAndView view = request(super.createRequestPageTempletResponse("/public/upload"));
        UploadStatus upload = null;
        String fileName = null;
        if (imgFile != null && imgFile.getOriginalFilename().lastIndexOf(".") > 0) {
            File uploadDir = new File(path, "upload");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            fileName = "upload/" + MainUtils.md5(imgFile.getBytes()) + imgFile.getOriginalFilename().substring(imgFile.getOriginalFilename().lastIndexOf(".")).toLowerCase();
            FileCopyUtils.copy(imgFile.getBytes(), new File(path, fileName));

            String fileURL = request.getScheme() + "://" + request.getServerName() + "/res/image.html?id=" + fileName;
            if (request.getServerPort() == 80) {
                fileURL = request.getScheme() + "://" + request.getServerName() + "/res/image.html?id=" + fileName;
            } else {
                fileURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/res/image.html?id=" + fileName;
            }
            upload = new UploadStatus("0", fileURL); //图片直接发送给 客户，不用返回
        } else {
            upload = new UploadStatus("请选择图片文件");
        }
        map.addAttribute("upload", upload);
        return view;
    }

    @RequestMapping("/file")
    @Menu(type = "resouce", subtype = "file", access = false)
    public void file(HttpServletResponse response, HttpServletRequest request, @Valid String id) throws IOException {
        if (!StringUtils.isBlank(id)) {
            AttachmentFile attachmentFile = attachementRes.findByIdAndOrgi(id, super.getOrgi(request));
            if (attachmentFile != null) {
                response.setContentType(attachmentFile.getFiletype());
                response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(attachmentFile.getTitle(), "UTF-8"));
                if (StringUtils.isNotBlank(attachmentFile.getModel()) && attachmentFile.getModel().equals("app")) {
                    response.getOutputStream().write(FileUtils.readFileToByteArray(new File(path, "app/" + attachmentFile.getFileid())));
                } else {
                    response.getOutputStream().write(FileUtils.readFileToByteArray(new File(path, "upload/" + attachmentFile.getFileid())));
                }
            }
        }
    }

    @RequestMapping("/template")
    @Menu(type = "resouce", subtype = "template", access = false)
    public void template(HttpServletResponse response, HttpServletRequest request, @Valid String filename) throws IOException {
        if (!StringUtils.isBlank(filename)) {
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