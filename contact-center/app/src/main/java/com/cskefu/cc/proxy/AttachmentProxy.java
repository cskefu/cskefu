/* 
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2019-2022 Chatopera Inc, All rights reserved. 
 * <https://www.chatopera.com>
 */

package com.cskefu.cc.proxy;

import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.model.AttachmentFile;
import com.cskefu.cc.persistence.repository.AttachmentRepository;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

@Component
public class AttachmentProxy {
    @Value("${web.upload-path}")
    private String path;

    @Autowired
    private AttachmentRepository attachementRes;

    public String processAttachmentFile(
            final MultipartFile file,
            final String fileid,
            final String creator
    ) throws IOException {
        String id = null;

        if (file.getSize() > 0) {            //文件尺寸 限制 ？在 启动 配置中 设置 的最大值，其他地方不做限制
            AttachmentFile attachmentFile = new AttachmentFile();
            attachmentFile.setCreater(creator);
            attachmentFile.setModel(MainContext.ModelType.WEBIM.toString());
            attachmentFile.setFilelength((int) file.getSize());
            if (file.getContentType() != null && file.getContentType().length() > 255) {
                attachmentFile.setFiletype(file.getContentType().substring(0, 255));
            } else {
                attachmentFile.setFiletype(file.getContentType());
            }
            String originalFilename = URLDecoder.decode(file.getOriginalFilename(), "utf-8");
            File uploadFile = new File(originalFilename);
            if (uploadFile.getName() != null && uploadFile.getName().length() > 255) {
                attachmentFile.setTitle(uploadFile.getName().substring(0, 255));
            } else {
                attachmentFile.setTitle(uploadFile.getName());
            }
            if (StringUtils.isNotBlank(attachmentFile.getFiletype()) && attachmentFile.getFiletype().contains("image")) {
                attachmentFile.setImage(true);
            }
            attachmentFile.setFileid(fileid);
            attachementRes.save(attachmentFile);
            FileUtils.writeByteArrayToFile(new File(path, "upload/" + fileid), file.getBytes());
            id = attachmentFile.getId();
        }
        return id;
    }
}
