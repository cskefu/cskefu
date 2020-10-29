package com.chatopera.cc.proxy;

import com.chatopera.cc.acd.ACDPolicyService;
import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.exception.CSKefuException;
import com.chatopera.cc.model.*;
import com.chatopera.cc.peer.PeerSyncIM;
import com.chatopera.cc.persistence.blob.JpaBlobHelper;
import com.chatopera.cc.persistence.repository.*;
import com.chatopera.cc.socketio.message.ChatMessage;
import com.chatopera.cc.socketio.message.Message;
import com.chatopera.cc.util.HashMapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

@Component
public class AgentProxy {
    private final static Logger logger = LoggerFactory.getLogger(AgentProxy.class);

    @Value("${web.upload-path}")
    private String webUploadPath;

    @Autowired
    private ACDPolicyService acdPolicyService;

    @Autowired
    private AttachmentRepository attachementRes;

    @Autowired
    private JpaBlobHelper jpaBlobHelper;

    @Autowired
    private StreamingFileRepository streamingFileRepository;

    @Autowired
    private PeerSyncIM peerSyncIM;

    @Autowired
    private SNSAccountRepository snsAccountRes;

    @Autowired
    private Cache cache;

    @Autowired
    private AgentStatusRepository agentStatusRes;

    @Autowired
    private AgentUserTaskRepository agentUserTaskRes;

    /**
     * 设置一个坐席为就绪状态
     * 不牵扯ACD
     *
     * @param user
     * @param agentStatus
     */
    public void ready(final User user, final AgentStatus agentStatus, final boolean busy) {
        agentStatus.setOrgi(user.getOrgi());
        agentStatus.setUserid(user.getId());
        agentStatus.setUsername(user.getUname());
        agentStatus.setAgentno(user.getId());
        agentStatus.setLogindate(new Date());
        agentStatus.setOrgi(agentStatus.getOrgi());
        agentStatus.setUpdatetime(new Date());
        agentStatus.setSkills(user.getSkills());
        // TODO 对于busy的判断，其实可以和AgentStatus maxuser以及users结合
        // 现在为了配合前端的行为：从未就绪到就绪设置为置闲
        agentStatus.setBusy(busy);
//        SessionConfig sessionConfig = acdPolicyService.initSessionConfig(agentStatus.getOrgi());
//        agentStatus.setMaxusers(sessionConfig.getMaxuser());

        /**
         * 更新当前用户状态
         */
        agentStatus.setUsers(
                cache.getInservAgentUsersSizeByAgentnoAndOrgi(agentStatus.getAgentno(), agentStatus.getOrgi()));
        agentStatus.setStatus(MainContext.AgentStatusEnum.READY.toString());

        logger.info(
                "[ready] set agent {}, status {}", agentStatus.getAgentno(),
                MainContext.AgentStatusEnum.READY.toString());

        // 更新数据库
        agentStatusRes.save(agentStatus);
    }


    /**
     * 将消息发布到接收端
     *
     * @param chatMessage
     * @param agentUser
     */
    public void sendChatMessageByAgent(final ChatMessage chatMessage, final AgentUser agentUser) {
        Message outMessage = new Message();
        outMessage.setMessage(chatMessage.getMessage());
        outMessage.setCalltype(chatMessage.getCalltype());
        outMessage.setAgentUser(agentUser);

        // 设置SNSAccount信息
        if (StringUtils.isNotBlank(agentUser.getAppid())) {
            snsAccountRes.findOneBySnsTypeAndSnsIdAndOrgi(
                    agentUser.getChannel(), agentUser.getAppid(), agentUser.getOrgi()).ifPresent(
                    p -> outMessage.setSnsAccount(p));
        }

        outMessage.setContextid(chatMessage.getContextid());
        outMessage.setAttachmentid(chatMessage.getAttachmentid());
        outMessage.setMessageType(chatMessage.getMsgtype());
        outMessage.setCreatetime(Constants.DISPLAY_DATE_FORMATTER.format(chatMessage.getCreatetime()));
        outMessage.setChannelMessage(chatMessage);

        // 处理超时回复
        AgentUserTask agentUserTask = agentUserTaskRes.getOne(agentUser.getId());
        agentUserTask.setWarnings("0");
        agentUserTask.setWarningtime(null);

        agentUserTask.setReptime(null);
        agentUserTask.setReptimes("1");
        agentUserTask.setLastmessage(new Date());

        agentUserTaskRes.save(agentUserTask);

        // 发送消息给在线访客(此处也会生成对话聊天历史和会话监控消息)
        peerSyncIM.send(
                MainContext.ReceiverType.VISITOR,
                MainContext.ChannelType.toValue(agentUser.getChannel()),
                agentUser.getAppid(),
                MainContext.MessageType.MESSAGE,
                chatMessage.getTouser(),
                outMessage,
                true);

        // 发送消息给坐席（返回消息给坐席自己）
        peerSyncIM.send(
                MainContext.ReceiverType.AGENT,
                MainContext.ChannelType.WEBIM,
                agentUser.getAppid(),
                MainContext.MessageType.MESSAGE,
                agentUser.getAgentno(),
                outMessage,
                true);
    }

    /**
     * 发送坐席的图片消息给访客和坐席自己
     *
     * @param creator
     * @param agentUser
     * @param multipart
     * @param sf
     */
    public void sendFileMessageByAgent(final User creator, final AgentUser agentUser, final MultipartFile multipart, final StreamingFile sf) {
        // 消息体
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setFilename(multipart.getOriginalFilename());
        chatMessage.setFilesize((int) multipart.getSize());
        chatMessage.setAttachmentid(sf.getId());
        chatMessage.setMessage(sf.getFileUrl());
        chatMessage.setId(MainUtils.getUUID());
        chatMessage.setContextid(agentUser.getContextid());
        chatMessage.setAgentserviceid(agentUser.getAgentserviceid());
        chatMessage.setChannel(agentUser.getChannel());
        chatMessage.setUsession(agentUser.getUserid());
        chatMessage.setAppid(agentUser.getAppid());
        chatMessage.setUserid(creator.getId());
        chatMessage.setOrgi(creator.getOrgi());
        chatMessage.setCreater(creator.getId());
        chatMessage.setUsername(creator.getUname());

        chatMessage.setCalltype(MainContext.CallType.OUT.toString());
        if (StringUtils.isNotBlank(agentUser.getAgentno())) {
            chatMessage.setTouser(agentUser.getUserid());
        }

        if (multipart.getContentType() != null && multipart.getContentType().indexOf(
                Constants.ATTACHMENT_TYPE_IMAGE) >= 0) {
            chatMessage.setMsgtype(MainContext.MediaType.IMAGE.toString());
        } else {
            chatMessage.setMsgtype(MainContext.MediaType.FILE.toString());
        }

        Message outMessage = new Message();
        outMessage.setCalltype(chatMessage.getCalltype());
        outMessage.setMessage(sf.getFileUrl());

        if (agentUser != null && !StringUtils.equals(
                agentUser.getStatus(), MainContext.AgentUserStatusEnum.END.toString())) {
            // 发送消息
            outMessage.setFilename(multipart.getOriginalFilename());
            outMessage.setFilesize((int) multipart.getSize());
            outMessage.setChannelMessage(chatMessage);
            outMessage.setAgentUser(agentUser);
            outMessage.setCreatetime(Constants.DISPLAY_DATE_FORMATTER.format(new Date()));
            outMessage.setMessageType(chatMessage.getMsgtype());

            /**
             * 通知文件上传消息
             */
            // 发送消息给访客
            peerSyncIM.send(MainContext.ReceiverType.VISITOR,
                    MainContext.ChannelType.toValue(agentUser.getChannel()),
                    agentUser.getAppid(), MainContext.MessageType.MESSAGE,
                    agentUser.getUserid(),
                    outMessage,
                    true);

            // 发送给坐席自己
            peerSyncIM.send(MainContext.ReceiverType.AGENT,
                    MainContext.ChannelType.WEBIM,
                    agentUser.getAppid(),
                    MainContext.MessageType.MESSAGE,
                    agentUser.getAgentno(), outMessage, true);

        } else {
            logger.warn("[sendFileMessageByAgent] agent user chat is end, disable forward files.");
        }
    }


    /**
     * 将http的multipart保存到数据库
     *
     * @param creator
     * @param multipart
     * @return
     * @throws IOException
     * @throws CSKefuException
     */
    public StreamingFile saveFileIntoMySQLBlob(final User creator, final MultipartFile multipart) throws
            IOException, CSKefuException {
        /**
         * 准备文件夹
         */
        File uploadDir = new File(webUploadPath, "upload");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String fileid = MainUtils.getUUID();
        StreamingFile sf = new StreamingFile();

        /**
         * 保存到本地
         */
        if (multipart.getContentType() != null && multipart.getContentType().indexOf(
                Constants.ATTACHMENT_TYPE_IMAGE) >= 0) {
            // 图片
            // process thumbnail
            File original = new File(webUploadPath, "upload/" + fileid + "_original");
            File thumbnail = new File(webUploadPath, "upload/" + fileid);
            FileCopyUtils.copy(multipart.getBytes(), original);
            MainUtils.processImage(thumbnail, original);
            sf.setThumbnail(jpaBlobHelper.createBlobWithFile(thumbnail));
            sf.setFileUrl("/res/image.html?id=" + fileid);
        } else {
            // 其它类型的文件
            AttachmentFile attachmentFile = processAttachmentFile(creator, multipart, fileid);
            sf.setFileUrl("/res/file.html?id=" + attachmentFile.getId());
        }

        /**
         * 保存文件到MySQL数据库
         */
        sf.setId(fileid);
        sf.setData(jpaBlobHelper.createBlob(multipart.getInputStream(), multipart.getSize()));
        sf.setName(multipart.getOriginalFilename());
        sf.setMime(multipart.getContentType());

        streamingFileRepository.save(sf);

        return sf;
    }


    /**
     * 处理multi part为本地文件
     *
     * @param owner
     * @param multipart
     * @param fileid
     * @return
     * @throws IOException
     * @throws CSKefuException
     */
    public AttachmentFile processAttachmentFile(
            final User owner, final MultipartFile multipart,
            final String fileid) throws IOException, CSKefuException {
        if (multipart.getSize() == 0) {
            throw new CSKefuException("Empty upload file size.");
        }

        // 文件尺寸 限制 ？在 启动 配置中 设置 的最大值，其他地方不做限制
        AttachmentFile attachmentFile = new AttachmentFile();
        attachmentFile.setCreater(owner.getId());
        attachmentFile.setOrgi(owner.getOrgi());
        attachmentFile.setModel(MainContext.ModelType.WEBIM.toString());
        attachmentFile.setFilelength((int) multipart.getSize());
        if (multipart.getContentType() != null && multipart.getContentType().length() > 255) {
            attachmentFile.setFiletype(multipart.getContentType().substring(0, 255));
        } else {
            attachmentFile.setFiletype(multipart.getContentType());
        }
        File uploadFile = new File(multipart.getOriginalFilename());
        if (uploadFile.getName() != null && uploadFile.getName().length() > 255) {
            attachmentFile.setTitle(uploadFile.getName().substring(0, 255));
        } else {
            attachmentFile.setTitle(uploadFile.getName());
        }
        if (StringUtils.isNotBlank(attachmentFile.getFiletype()) && attachmentFile.getFiletype().indexOf(
                Constants.ATTACHMENT_TYPE_IMAGE) >= 0) {
            attachmentFile.setImage(true);
        }
        attachmentFile.setFileid(fileid);
        attachementRes.save(attachmentFile);
        FileUtils.writeByteArrayToFile(new File(webUploadPath, "upload/" + fileid), multipart.getBytes());
        return attachmentFile;
    }

    /**
     * 获得一个User的AgentStatus
     * 先从缓存读取，再从数据库，还没有就新建
     *
     * @param agentno
     * @param orgi
     * @return
     */
    public AgentStatus resolveAgentStatusByAgentnoAndOrgi(final String agentno, final String orgi, final HashMap<String, String> skills) {
        logger.info(
                "[resolveAgentStatusByAgentnoAndOrgi] agentno {}, skills {}", agentno,
                HashMapUtils.concatKeys(skills, "|"));
        AgentStatus agentStatus = cache.findOneAgentStatusByAgentnoAndOrig(agentno, orgi);

        if (agentStatus == null) {
            agentStatus = agentStatusRes.findOneByAgentnoAndOrgi(agentno, orgi).orElseGet(AgentStatus::new);
        }

        if (skills != null) {
            agentStatus.setSkills(skills);
        }

        return agentStatus;
    }

}
