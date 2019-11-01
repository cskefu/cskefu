/*
 * Copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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

package com.chatopera.cc.app.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Proxy;

import java.sql.Blob;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "cs_stream_file")
@Proxy(lazy = false)
public class StreamingFile implements java.io.Serializable {

    private String id;

    @NotNull
    private String name;

    private String mime; // Media Type over HTTP

    @NotNull
    private Blob data;

    private Blob thumbnail; // 图片缩略图

    private Blob cooperation; // 图片协作图

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
}
