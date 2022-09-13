/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
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
package com.cskefu.cc.persistence.es;

import com.cskefu.cc.model.UKAgg;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;

import java.util.ArrayList;
import java.util.List;


public class UKAggTopResultExtractor extends UKResultMapper{
	
	private String term  , name ;
	
	public UKAggTopResultExtractor(String term , String name){
		this.term = term ;
		this.name = name ;
	}

	@Override
	public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
		Aggregations aggregations = response.getAggregations();
		Terms agg = aggregations.get(term) ;
		long total = agg.getSumOfOtherDocCounts() ;
		List<T> results = new ArrayList<T>();
		if(agg.getBuckets()!=null && agg.getBuckets().size()>0){
			for (int i = pageable.getPageNumber()*pageable.getPageSize();i<agg.getBuckets().size()  ; i++) {
				Terms.Bucket entry = agg.getBuckets().get(i) ;
				if(!StringUtils.isBlank(name) && entry.getAggregations().get(name)!=null){
					TopHits topHits = entry.getAggregations().get(name);
					for (SearchHit hit : topHits.getHits().getHits()) {
						T data = mapEntity(hit.getSourceAsString() , hit , clazz) ;
						if(data instanceof UKAgg){
							((UKAgg) data).setRowcount((int) topHits.getHits().getTotalHits());
						}
						results.add(data) ;
					}
				}
			}
		}
		return new AggregatedPageImpl<T>(results, pageable, total);
	}
}
