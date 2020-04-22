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
package com.chatopera.cc.persistence.impl;

import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.model.MetadataTable;
import com.chatopera.cc.util.dsdata.process.JPAProcess;
import com.chatopera.cc.util.es.UKDataBean;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.client.RequestOptions;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.Map;

public class BatchDataProcess implements JPAProcess {

    private final MetadataTable metadata;
    private final ESDataExchangeImpl esDataExchangeImpl;
    @NonNull
    private final BulkRequest request;

    public BatchDataProcess(MetadataTable metadata, ESDataExchangeImpl esDataExchangeImpl) {
        this.metadata = metadata;
        this.esDataExchangeImpl = esDataExchangeImpl;
        request = new BulkRequest();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(Object data) {
        UKDataBean dataBean = new UKDataBean();
        if (data instanceof UKDataBean) {
            dataBean = (UKDataBean) data;
        } else {
            dataBean.setTable(this.metadata);
            dataBean.setValues((Map<String, Object>) data);
        }
        try {
            request.add(esDataExchangeImpl.saveBulk(dataBean));
            if (request.numberOfActions() % 1000 == 0) {
                end();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void end() throws IOException {
        MainContext.getTemplet().getClient().bulk(request, RequestOptions.DEFAULT);
        request.requests().clear();
    }
}
