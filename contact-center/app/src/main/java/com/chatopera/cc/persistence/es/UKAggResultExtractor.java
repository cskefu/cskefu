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
package com.chatopera.cc.persistence.es;

import com.chatopera.cc.model.KbsTopic;
import com.chatopera.cc.model.KbsTopicComment;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;

import java.util.ArrayList;
import java.util.List;


public class UKAggResultExtractor extends UKResultMapper{
	
	private String term ;
	
	public UKAggResultExtractor(String term){
		this.term = term ;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
		Aggregations aggregations = response.getAggregations();
		List<T> results = new ArrayList<T>();
		long total = 0 ;
		if(aggregations!=null && aggregations.get(term)!=null){
			if(aggregations.get(term) instanceof Terms){
				Terms agg = aggregations.get(term) ;
				if(agg!=null){
					total = agg.getSumOfOtherDocCounts() ;
					if(agg.getBuckets()!=null && agg.getBuckets().size()>0){
						for (Terms.Bucket entry : agg.getBuckets()) {
							if(clazz.equals(KbsTopic.class)){
								KbsTopic topic = new KbsTopic();
								topic.setCreater(entry.getKeyAsString());
								topic.setRowcount((int) entry.getDocCount());
								results.add((T) topic) ;
							}else if(clazz.equals(KbsTopicComment.class)){
								KbsTopicComment topicComment = new KbsTopicComment();
								topicComment.setCreater(entry.getKeyAsString());
								topicComment.setRowcount((int) entry.getDocCount());
								results.add((T) topicComment) ;
							}
						}
					}
				}
			}else if(aggregations.get(term) instanceof InternalDateHistogram){
				InternalDateHistogram agg = aggregations.get(term) ;
				total = response.getHits().getTotalHits() ;
				if(agg!=null){
	//				if(agg.getBuckets()!=null && agg.getBuckets().size()>0){
	//					for (DateHistogram.Bucket entry : agg.getBuckets()) {
	//						if(clazz.equals(KbsTopic.class)){
	//							KbsTopic topic = new KbsTopic();
	//							topic.setKey(entry.getKey().substring(0 , 10));
	//							topic.setRowcount((int) entry.getDocCount());
	//							results.add((T) topic) ;
	//						}	
	//					}
	//				}
				}
			}
		}
		return new AggregatedPageImpl<T>(results, pageable, total);
	}
}
