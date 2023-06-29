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

package com.cskefu.cc.controller.apps;

import com.alibaba.fastjson.JSONObject;
import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.util.restapi.RestUtils;
import com.cskefu.cc.model.*;
import com.cskefu.cc.peer.PeerSyncEntIM;
import com.cskefu.cc.persistence.blob.JpaBlobHelper;
import com.cskefu.cc.persistence.repository.*;
import com.cskefu.cc.proxy.AttachmentProxy;
import com.cskefu.cc.proxy.UserProxy;
import com.cskefu.cc.socketio.client.NettyClients;
import com.cskefu.cc.socketio.message.ChatMessage;
import com.cskefu.cc.util.Menu;
import com.cskefu.cc.util.StreamingFileUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/ent/im")
public class EntIMController extends Handler {
    @Value("${web.upload-path}")
    private String path;

    @Autowired
    private OrganRepository organRes;

    @Autowired
    private UserRepository userRes;

    @Autowired
    private IMGroupRepository imGroupRes;

    @Autowired
    private IMGroupUserRepository imGroupUserRes;

    @Autowired
    private ChatMessageRepository chatMessageRes;

    @Autowired
    private RecentUserRepository recentUserRes;

    @Autowired
    private StreamingFileRepository streamingFileRepository;

    @Autowired
    private JpaBlobHelper jpaBlobHelper;

    @Autowired
    AttachmentProxy attachmentProxy;

    @Autowired
    PeerSyncEntIM peerSyncEntIM;

    @Autowired
    private UserProxy userProxy;

    private Map<String, Organ> getChatOrgans(User user) {
        Map<String, Organ> organs = new HashMap<>();
        user.getOrgans().values().stream().forEach(o -> {
            if (!StringUtils.equals(o.getParent(), "0")) {
                Organ parent = organRes.findById(o.getParent()).orElse(null);
                organs.put(parent.getId(), parent);
            }

            List<Organ> brother = organRes.findByParent(o.getParent());
            brother.stream().forEach(b -> {
                if (!organs.containsKey(b.getId())) {
                    organs.put(b.getId(), b);
                }
            });
        });

        user.getAffiliates().stream().forEach(p -> {
            if (!organs.containsKey(p)) {
                Organ organ = organRes.findById(p).orElse(null);
                organs.put(p, organ);
            }
        });

        return organs;
    }

    @RequestMapping("/index")
    @Menu(type = "im", subtype = "entim")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = request(super.createView("/apps/entim/index"));

        User logined = super.getUser(request);

        Map<String, Organ> targetOrgans = getChatOrgans(logined);

        view.addObject("organList", targetOrgans.values());
        List<User> users = userRes.findByDatastatus(false);

        // TODO: 优化性能
        for (User u : users) {
            userProxy.attachOrgansPropertiesForUser(u);
        }

        view.addObject("userList", users);
        view.addObject("groupList", imGroupRes.findByCreater(super.getUser(request).getId()));
        view.addObject("joinGroupList", imGroupUserRes.findByUser(super.getUser(request)));
        view.addObject("recentUserList", recentUserRes.findByCreater(super.getUser(request).getId()));

        return view;
    }

    @RequestMapping("/skin")
    @Menu(type = "im", subtype = "entim")
    public ModelAndView skin(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = request(super.createView("/apps/entim/skin"));

        return view;
    }

    @RequestMapping("/point")
    @Menu(type = "im", subtype = "entim")
    public ModelAndView point(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = request(super.createView("/apps/entim/point"));
        view.addObject(
                "recentUserList",
                recentUserRes.findByCreater(super.getUser(request).getId())
        );
        return view;
    }

    @RequestMapping("/expand")
    @Menu(type = "im", subtype = "entim")
    public ModelAndView expand(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = request(super.createView("/apps/entim/expand"));
        return view;
    }

    @RequestMapping("/chat")
    @Menu(type = "im", subtype = "entim")
    public ModelAndView chat(HttpServletRequest request, HttpServletResponse response, @Valid String userid) {
        ModelAndView view = request(super.createView("/apps/entim/chat"));
        User entImUser = userRes.findById(userid).orElse(null);

        if (entImUser != null) {
            userProxy.attachOrgansPropertiesForUser(entImUser);
            view.addObject("organs", entImUser.getOrgans().values());
        }

        view.addObject("entimuser", entImUser);
        view.addObject("contextid", MainUtils.genNewID(super.getUser(request).getId(), userid));
        view.addObject("online", NettyClients.getInstance().getEntIMClientsNum(userid) > 0);

        Page<ChatMessage> chatMessageList = chatMessageRes.findByContextidAndUserid(userid,
                super.getUser(request).getId(),
                PageRequest.of(0, 20, Sort.Direction.DESC, "createtime")
        );

        view.addObject("chatMessageList", chatMessageList);

        RecentUser recentUser = recentUserRes.findByCreaterAndUser(super.getUser(request).getId(),
                new User(userid)
        ).orElseGet(() -> {
            RecentUser u = new RecentUser();
            u.setCreater(super.getUser(request).getId());
            u.setUser(new User(userid));
            return u;
        });
        /**
         * 我的最近联系人
         */
        recentUser.setNewmsg(0);

        recentUserRes.save(recentUser);
        /**
         * 对方的最近联系人
         */
        recentUserRes.findByCreaterAndUser(userid, super.getUser(request)).orElseGet(() -> {
            RecentUser u = new RecentUser();
            u.setCreater(userid);
            u.setUser(super.getUser(request));
            recentUserRes.save(u);
            return u;
        });

        return view;
    }

    @RequestMapping("/chat/more")
    @Menu(type = "im", subtype = "entim")
    public ModelAndView chatMore(
            HttpServletRequest request, HttpServletResponse response, @Valid String userid,
            @Valid Date createtime
    ) {
        ModelAndView view = request(super.createView("/apps/entim/more"));

        Page<ChatMessage> chatMessageList = chatMessageRes.findByContextidAndUseridAndCreatetimeLessThan(userid,
                super.getUser(request).getId(), createtime,
                PageRequest.of(0, 20, Sort.Direction.DESC, "createtime")
        );
        view.addObject("chatMessageList", chatMessageList);

        return view;
    }

    @RequestMapping("/group")
    @Menu(type = "im", subtype = "entim")
    public ModelAndView groupMore(HttpServletRequest request, HttpServletResponse response, @Valid String id) {
        ModelAndView view = request(super.createView("/apps/entim/group/index"));
        IMGroup imGroup = imGroupRes.findById(id).orElse(null);
        view.addObject("imGroup", imGroup);
        view.addObject("imGroupUserList", imGroupUserRes.findByImgroup(imGroup));
        view.addObject("contextid", id);
        view.addObject("chatMessageList", chatMessageRes.findByContextid(id,
                PageRequest.of(0, 20, Sort.Direction.DESC, "createtime")
        ));
        return view;
    }

    @RequestMapping("/group/more")
    @Menu(type = "im", subtype = "entim")
    public ModelAndView group(
            HttpServletRequest request, HttpServletResponse response, @Valid String id,
            @Valid Date createtime
    ) {
        ModelAndView view = request(super.createView("/apps/entim/group/more"));
        view.addObject("chatMessageList", chatMessageRes.findByContextidAndCreatetimeLessThan(id,
                createtime, PageRequest.of(0, 20, Sort.Direction.DESC, "createtime")
        ));
        return view;
    }

    @RequestMapping("/group/user")
    @Menu(type = "im", subtype = "entim")
    public ModelAndView user(HttpServletRequest request, HttpServletResponse response, @Valid String id) {
        ModelAndView view = request(super.createView("/apps/entim/group/user"));
        User logined = super.getUser(request);
        HashSet<String> affiliates = logined.getAffiliates();

        List<User> users = userProxy.findByOrganInAndDatastatus(new ArrayList<>(affiliates), false);
        users.stream().forEach(u -> userProxy.attachOrgansPropertiesForUser(u));
        view.addObject("userList", users);

        IMGroup imGroup = imGroupRes.findById(id).orElse(null);
        List<Organ> organs = organRes.findAllById(affiliates);

        view.addObject("imGroup", imGroup);
        view.addObject("organList", organs);
        view.addObject("imGroupUserList", imGroupUserRes.findByImgroup(imGroup));

        return view;
    }

    @RequestMapping("/group/seluser")
    @Menu(type = "im", subtype = "entim")
    public void seluser(
            HttpServletRequest request, HttpServletResponse response, @Valid String id,
            @Valid String user
    ) {
        IMGroup imGroup = new IMGroup();
        imGroup.setId(id);
        User curUser = new User();
        curUser.setId(user);
        IMGroupUser imGroupUser = imGroupUserRes.findByImgroupAndUser(imGroup, curUser);
        if (imGroupUser == null) {
            imGroupUser = new IMGroupUser();
            imGroupUser.setImgroup(imGroup);
            imGroupUser.setUser(curUser);
            imGroupUser.setCreater(super.getUser(request).getId());
            imGroupUserRes.save(imGroupUser);
        }
    }

    @RequestMapping("/group/rmuser")
    @Menu(type = "im", subtype = "entim")
    public void rmluser(
            HttpServletRequest request, HttpServletResponse response, @Valid String id,
            @Valid String user
    ) {
        IMGroup imGroup = new IMGroup();
        imGroup.setId(id);
        User curUser = new User();
        curUser.setId(user);
        IMGroupUser imGroupUser = imGroupUserRes.findByImgroupAndUser(imGroup, curUser);
        if (imGroupUser != null) {
            imGroupUserRes.delete(imGroupUser);
        }
    }

    @RequestMapping("/group/tipmsg")
    @Menu(type = "im", subtype = "entim")
    public ModelAndView tipmsg(
            HttpServletRequest request, HttpServletResponse response, @Valid String id,
            @Valid String tipmsg
    ) {
        ModelAndView view = request(super.createView("/apps/entim/group/tipmsg"));
        IMGroup imGroup = imGroupRes.findById(id).orElse(null);
        if (imGroup != null) {
            imGroup.setTipmessage(tipmsg);
            imGroupRes.save(imGroup);
        }
        view.addObject("imGroup", imGroup);
        return view;
    }

    @RequestMapping("/group/save")
    @Menu(type = "im", subtype = "entim")
    public ModelAndView groupsave(HttpServletRequest request, HttpServletResponse response, @Valid IMGroup group) {
        ModelAndView view = request(super.createView("/apps/entim/group/grouplist"));
        if (!StringUtils.isBlank(group.getName())
                && imGroupRes.countByName(group.getName()) == 0) {
            group.setCreater(super.getUser(request).getId());
            imGroupRes.save(group);

            IMGroupUser imGroupUser = new IMGroupUser();
            imGroupUser.setUser(super.getUser(request));
            imGroupUser.setImgroup(group);
            imGroupUser.setAdmin(true);
            imGroupUser.setCreater(super.getUser(request).getId());
            imGroupUserRes.save(imGroupUser);
        }
        view.addObject(
                "groupList",
                imGroupRes.findByCreater(super.getUser(request).getId())
        );

        view.addObject(
                "joinGroupList",
                imGroupUserRes.findByUser(super.getUser(request))
        );

        return view;
    }

    private ChatMessage createFileMessage(String message, int length, String name, String msgtype, String userid, String attachid) {
        ChatMessage data = new ChatMessage();
        data.setFilesize(length);
        data.setFilename(name);
        data.setAttachmentid(attachid);
        data.setMessage(message);
        data.setMsgtype(msgtype);
        data.setType(MainContext.MessageType.MESSAGE.toString());
        data.setCalltype(MainContext.CallType.OUT.toString());
        data.setTouser(userid);

        return data;
    }

    @RequestMapping("/image/upload")
    @Menu(type = "im", subtype = "image", access = true)
    public ResponseEntity<String> upload(
            ModelMap map, HttpServletRequest request,
            @RequestParam(value = "imgFile", required = false) MultipartFile multipart, @Valid String group,
            @Valid String userid, @Valid String username, @Valid String paste
    ) throws IOException {
        ModelAndView view = request(super.createView("/apps/im/upload"));
        final User logined = super.getUser(request);

        String fileName = null;
        JSONObject result = new JSONObject();
        HttpHeaders headers = RestUtils.header();

        if (multipart != null && multipart.getOriginalFilename().lastIndexOf(".") > 0
                && StringUtils.isNotBlank(userid)) {
            File uploadDir = new File(path, "upload");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String fileid = MainUtils.getUUID();
            StreamingFile sf = new StreamingFile();
            sf.setId(fileid);
            sf.setName(multipart.getOriginalFilename());
            sf.setMime(multipart.getContentType());
            if (multipart.getContentType() != null
                    && multipart.getContentType().contains(Constants.ATTACHMENT_TYPE_IMAGE)) {
                String invalid = StreamingFileUtil.getInstance().validate(Constants.ATTACHMENT_TYPE_IMAGE, multipart.getOriginalFilename());
                if (invalid == null) {
                    fileName = "upload/" + fileid + "_original";
                    File imageFile = new File(path, fileName);
                    FileCopyUtils.copy(multipart.getBytes(), imageFile);
                    String thumbnailsFileName = "upload/" + fileid;
                    File thumbnail = new File(path, thumbnailsFileName);
                    MainUtils.processImage(thumbnail, imageFile);

                    sf.setData(jpaBlobHelper.createBlob(multipart.getInputStream(), multipart.getSize()));
                    sf.setThumbnail(jpaBlobHelper.createBlobWithFile(thumbnail));
                    streamingFileRepository.save(sf);
                    String fileUrl = "/res/image.html?id=" + fileid;
                    result.put("error", 0);
                    result.put("url", fileUrl);

                    if (paste == null) {
                        ChatMessage fileMessage = createFileMessage(fileUrl, (int) multipart.getSize(), multipart.getName(), MainContext.MediaType.IMAGE.toString(), userid, fileid);
                        fileMessage.setUsername(logined.getUname());
                        peerSyncEntIM.send(logined.getId(), group, MainContext.MessageType.MESSAGE, fileMessage);
                    }
                } else {
                    result.put("error", 1);
                    result.put("message", invalid);
                }
            } else {
                String invalid = StreamingFileUtil.getInstance().validate(Constants.ATTACHMENT_TYPE_FILE, multipart.getOriginalFilename());
                if (invalid == null) {
                    sf.setData(jpaBlobHelper.createBlob(multipart.getInputStream(), multipart.getSize()));
                    streamingFileRepository.save(sf);

                    String id = attachmentProxy.processAttachmentFile(multipart,
                            fileid, logined.getId()
                    );
                    result.put("error", 0);
                    result.put("url", "/res/file.html?id=" + id);

                    String file = "/res/file.html?id=" + id;

                    ChatMessage fileMessage = createFileMessage(file, (int) multipart.getSize(), multipart.getOriginalFilename(), MainContext.MediaType.FILE.toString(), userid, fileid);
                    fileMessage.setUsername(logined.getUname());
                    peerSyncEntIM.send(logined.getId(), group, MainContext.MessageType.MESSAGE, fileMessage);
                } else {
                    result.put("error", 1);
                    result.put("message", invalid);
                }
            }
        } else {
            result.put("error", 1);
            result.put("message", "请选择文件");
        }


        return new ResponseEntity<>(result.toString(), headers, HttpStatus.OK);
    }
}
