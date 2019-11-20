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
import com.chatopera.cc.util.dsdata.process.JPAProcess;
import com.chatopera.cc.util.es.UKDataBean;
import org.elasticsearch.action.bulk.BulkRequestBuilder;

public class DataBatProcess implements JPAProcess{
	
	private ESDataExchangeImpl esDataExchangeImpl ;
	private BulkRequestBuilder builder ;
	
	public DataBatProcess(ESDataExchangeImpl esDataExchangeImpl) {
		this.esDataExchangeImpl = esDataExchangeImpl ;
		builder = MainContext.getTemplet().getClient().prepareBulk() ;
	}
	
	@Override
	public void process(Object data) {
		if(data instanceof UKDataBean){
			UKDataBean dataBean = (UKDataBean)data;
			try {
				if(builder!=null) {
					builder.add(esDataExchangeImpl.saveBulk(dataBean)) ;
				}else {
					esDataExchangeImpl.saveIObject(dataBean);
				}
				if(builder.numberOfActions() % 1000 ==0) {
					builder.execute().actionGet();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void end() {
		if(builder!=null) {
			builder.execute().actionGet();
		}
	}
}
