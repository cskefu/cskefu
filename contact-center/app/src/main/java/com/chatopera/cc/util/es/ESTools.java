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
package com.chatopera.cc.util.es;


import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.model.MetadataTable;
import com.chatopera.cc.model.TableProperties;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class ESTools {
    private static final Logger log = LoggerFactory.getLogger(ESTools.class);

    public static boolean checkMapping(String tb, String orgi) {
        return MainContext.getTemplet().typeExists(orgi, tb);
    }

    public static void mapping(MetadataTable tb, String orgi) throws ElasticsearchException, IOException {
        log.info(tb.getTablename() + " ORGI : " + orgi + " Mapping Not Exists , Waiting Form init ......");

        XContentBuilder builder = jsonBuilder().startObject()
                .startObject(tb.getTablename().toLowerCase())
                .startObject("properties");
        for (TableProperties tp : tb.getTableproperty()) {
            builder.startObject(tp.getFieldname().toLowerCase());
            if (tp.getDatatypename().equalsIgnoreCase("text") && !tp.getFieldname().equalsIgnoreCase("id")) {
                builder.field("type", "string").field("index", tp.isToken() ? "analyzed" : "not_analyzed");
                if (tp.isToken() && "keyword".equalsIgnoreCase(tp.getTokentype())) {
                    builder.field("analyzer", "whitespace");
                }
                if (!tp.isToken()) {
                    builder.field("ignore_above", "256");
                }
            } else if (tp.getDatatypename().toLowerCase().equals("date")) {
                builder.field("type", "long").field("index", "not_analyzed");
            } else if (tp.getDatatypename().toLowerCase().equals("datetime")) {
                builder.field("type", "long").field("index", "not_analyzed");
            } else if (tp.getDatatypename().toLowerCase().equals("long")) {
                builder.field("type", "long").field("index", "not_analyzed");
            } else if (tp.getDatatypename().toLowerCase().equals("textarea")) {
                builder.field("type", "string").field("index", "analyzed");
            } else if (tp.getDatatypename().toLowerCase().equals("nlp")) {
                builder.field("type", "string").field("index", "not_analyzed").field("ignore_above", "256");
            } else if (tp.getDatatypename().toLowerCase().equals("url")) {
                builder.field("type", "string").field("index", "not_analyzed");
            } else if (tp.getDatatypename().toLowerCase().equals("email")) {
                builder.field("type", "string").field("index", "not_analyzed");
            } else if (tp.getDatatypename().toLowerCase().equals("number")) {
                builder.field("type", "float").field("index", "not_analyzed");
            } else if (tp.getDatatypename().toLowerCase().equals("boolean")) {
                builder.field("type", "boolean").field("index", "not_analyzed");
            } else {
                builder.field("type", "string").field("index", tp.isToken() ? "analyzed" : "not_analyzed");
            }
            builder.endObject();
        }
        builder.endObject().endObject().endObject();
        MainContext.getTemplet().putMapping(Constants.SYSTEM_INDEX, tb.getTablename(), builder);

    }
}
