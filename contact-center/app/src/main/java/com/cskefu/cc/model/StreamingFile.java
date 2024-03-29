/* 
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2019-Jun. 2023 Chatopera Inc, <https://www.chatopera.com>, 
 * Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package com.cskefu.cc.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Proxy;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.sql.Blob;

@Entity
@Table(name = "cs_stream_file")
@Proxy(lazy = false)
public class StreamingFile implements java.io.Serializable {

    private String id;

    @NotNull
    private String name;

    private String mime;      // Media Type over HTTP

    @NotNull
    private Blob data;

    private Blob thumbnail;   // 图片缩略图

    private Blob cooperation; // 图片协作图

    private String fileUrl;   // 提供网络访问时的URL

    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "assigned")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public Blob getData() {
        return data;
    }

    public void setData(@NotNull Blob data) {
        this.data = data;
    }

    public Blob getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Blob thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public Blob getCooperation() {
        return cooperation;
    }

    public void setCooperation(Blob cooperation) {
        this.cooperation = cooperation;
    }

    @Column(name = "fileurl")
    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
