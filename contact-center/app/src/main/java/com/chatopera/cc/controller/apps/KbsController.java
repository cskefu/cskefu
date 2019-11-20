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

package com.chatopera.cc.controller.apps;

import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.AttachmentFile;
import com.chatopera.cc.model.KbsTopic;
import com.chatopera.cc.model.KbsType;
import com.chatopera.cc.persistence.es.KbsTopicRepository;
import com.chatopera.cc.persistence.repository.AttachmentRepository;
import com.chatopera.cc.persistence.repository.KbsTypeRepository;
import com.chatopera.cc.persistence.repository.TagRepository;
import com.chatopera.cc.util.Menu;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Date;

@Controller
@RequestMapping({"/apps/kbs"})
public class KbsController extends Handler {
	
	@Autowired
	private TagRepository tagRes ;
	
	@Autowired
	private KbsTypeRepository kbsTypeRes ;
	
	@Autowired
	private KbsTopicRepository kbsTopicRes ;
	
	@Autowired
	private AttachmentRepository attachementRes;
	
	@Value("${web.upload-path}")
    private String path;
	
	@RequestMapping({"/index"})
	@Menu(type="apps", subtype="kbs")
	public ModelAndView index(ModelMap map , HttpServletRequest request){
		return request(super.createAppsTempletResponse("/apps/business/kbs/index"));
	}
	
	@RequestMapping({"/list"})
	@Menu(type="apps", subtype="kbs")
	public ModelAndView list(ModelMap map , HttpServletRequest request){
		map.addAttribute("kbsTypeResList", kbsTypeRes.findByOrgi(super.getOrgi(request))) ;
		return request(super.createAppsTempletResponse("/apps/business/kbs/list"));
	}
	
	@RequestMapping({"/list/type"})
	@Menu(type="apps", subtype="kbs")
	public ModelAndView listtype(ModelMap map , HttpServletRequest request,@Valid String typeid){
		if(!StringUtils.isBlank(typeid) && !typeid.equals("0")){
			map.addAttribute("kbsType", kbsTypeRes.findByIdAndOrgi(typeid, super.getOrgi(request))) ;
		}
		return request(super.createRequestPageTempletResponse("/apps/business/kbs/typelist"));
	}
	
	@RequestMapping({"/addtype"})
	@Menu(type="apps", subtype="kbs")
	public ModelAndView addtype(ModelMap map , HttpServletRequest request ){
		map.addAttribute("kbsTypeResList", kbsTypeRes.findByOrgi(super.getOrgi(request))) ;
		return request(super.createRequestPageTempletResponse("/apps/business/kbs/addtype"));
	}
	 
    @RequestMapping("/type/save")
    @Menu(type = "apps" , subtype = "kbs")
    public ModelAndView typesave(HttpServletRequest request ,@Valid KbsType kbsType) {
    	int count = kbsTypeRes.countByOrgiAndNameAndParentid(super.getOrgi(request), kbsType.getName(), kbsType.getParentid()) ;
    	if(count == 0){
    		kbsType.setOrgi(super.getOrgi(request));
    		kbsType.setCreater(super.getUser(request).getId());
    		kbsType.setCreatetime(new Date());
    		kbsTypeRes.save(kbsType) ;
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/apps/kbs/list.html"));
    }
    
    @RequestMapping({"/add"})
	@Menu(type="apps", subtype="kbs")
	public ModelAndView add(ModelMap map , HttpServletRequest request ,@Valid String typeid){
    	map.addAttribute("kbsTypeResList", kbsTypeRes.findByOrgi(super.getOrgi(request))) ;
    	map.addAttribute("tags", tagRes.findByOrgiAndTagtype(super.getOrgi(request) , MainContext.ModelType.KBS.toString())) ;
    	if(!StringUtils.isBlank(typeid) && !typeid.equals("0")){
			map.addAttribute("kbsType", kbsTypeRes.findByIdAndOrgi(typeid, super.getOrgi(request))) ;
		}
		return request(super.createRequestPageTempletResponse("/apps/business/kbs/add"));
	}
    
    @RequestMapping("/save")
    @Menu(type = "topic" , subtype = "save" , access = false)
    public ModelAndView save(HttpServletRequest request ,
							 final @Valid KbsTopic topic ,
							 @RequestParam(value = "files", required = false) MultipartFile[] files) throws IOException {
    	ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/apps/kbs/index.html")); 
    	topic.setOrgi(super.getOrgi(request));
    	topic.setCreater(super.getUser(request).getId());
    	topic.setUsername(super.getUser(request).getUsername());

    	processAttachmentFile(files, topic, request, topic.getId(), topic.getId());
    	
    	KbsType workOrderType = kbsTypeRes.findByIdAndOrgi(topic.getTptype(), super.getOrgi(request)) ;
    	// 知识处理流程，如果知识分类需要审批，则触发知识流程
    	if(workOrderType.isApproval()){
    		topic.setApproval(false);
    	}else{
    		topic.setApproval(true);
    	}
    	kbsTopicRes.save(topic) ;
    	return view;
    }
    
    
    private void  processAttachmentFile(MultipartFile[] files , KbsTopic topic, HttpServletRequest request  , String dataid , String modelid) throws IOException{
    	if(files!=null && files.length > 0){
    		topic.setAttachment("");		//序列化 附件文件，方便显示，避免多一次查询 附件的操作
    		//保存附件
    		for(MultipartFile file : files){
    			if(file.getSize() > 0){			//文件尺寸 限制 ？在 启动 配置中 设置 的最大值，其他地方不做限制
    				String fileid = MainUtils.md5(file.getBytes()) ;	//使用 文件的 MD5作为 ID，避免重复上传大文件
    				if(!StringUtils.isBlank(fileid)){
		    			AttachmentFile attachmentFile = new AttachmentFile() ;
		    			attachmentFile.setCreater(super.getUser(request).getId());
		    			attachmentFile.setOrgi(super.getOrgi(request));
		    			attachmentFile.setDataid(dataid);
		    			attachmentFile.setModelid(modelid);
		    			attachmentFile.setModel(MainContext.ModelType.WORKORDERS.toString());
		    			attachmentFile.setFilelength((int) file.getSize());
		    			if(file.getContentType()!=null && file.getContentType().length() > 255){
		    				attachmentFile.setFiletype(file.getContentType().substring(0 , 255));
		    			}else{
		    				attachmentFile.setFiletype(file.getContentType());
		    			}
		    			if(file.getOriginalFilename()!=null && file.getOriginalFilename().length() > 255){
		    				attachmentFile.setTitle(file.getOriginalFilename().substring(0 , 255));
		    			}else{
		    				attachmentFile.setTitle(file.getOriginalFilename());
		    			}
		    			if(!StringUtils.isBlank(attachmentFile.getFiletype()) && attachmentFile.getFiletype().indexOf("image") >= 0){
		    				attachmentFile.setImage(true);
		    			}
		    			attachmentFile.setFileid(fileid);
		    			attachementRes.save(attachmentFile) ;
		    			FileUtils.writeByteArrayToFile(new File(path , "app/kbs/"+fileid), file.getBytes());
    				}
    			}
    		}
    		
    	}
    }
}

