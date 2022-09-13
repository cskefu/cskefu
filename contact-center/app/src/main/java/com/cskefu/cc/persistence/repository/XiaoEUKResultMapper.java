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
package com.cskefu.cc.persistence.repository;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.highlight.HighlightField;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.ElasticsearchException;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.ScriptedField;
import org.springframework.data.elasticsearch.core.AbstractResultMapper;
import org.springframework.data.elasticsearch.core.DefaultEntityMapper;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentProperty;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.MappingContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.*;

public class XiaoEUKResultMapper extends AbstractResultMapper {

	private MappingContext<? extends ElasticsearchPersistentEntity<?>, ElasticsearchPersistentProperty> mappingContext;

	public XiaoEUKResultMapper() {
		super(new DefaultEntityMapper());
	}
	
	public XiaoEUKResultMapper(MappingContext<? extends ElasticsearchPersistentEntity<?>, ElasticsearchPersistentProperty> mappingContext) {
		super(new DefaultEntityMapper());
		this.mappingContext = mappingContext;
	}

	public XiaoEUKResultMapper(EntityMapper entityMapper) {
		super(entityMapper);
	}

	public XiaoEUKResultMapper(
			MappingContext<? extends ElasticsearchPersistentEntity<?>, ElasticsearchPersistentProperty> mappingContext,
			EntityMapper entityMapper) {
		super(entityMapper);
		this.mappingContext = mappingContext;
	}

	@Override
	public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
		long totalHits = response.getHits().totalHits();
		List<T> results = new ArrayList<T>();
		for (SearchHit hit : response.getHits()) {
			if (hit != null) {
				T result = null;
				if (StringUtils.isNotBlank(hit.sourceAsString())) {
					result = mapEntity(hit.sourceAsString() , hit , clazz);
				} else {
					result = mapEntity(hit.getFields().values() , hit , clazz);
				}
				setPersistentEntityId(result, hit.getId(), clazz);
				populateScriptFields(result, hit);
				results.add(result);
			}
		}

		return new AggregatedPageImpl<T>(results, pageable, totalHits);
	}
	
	public <T> T mapEntity(String source , SearchHit hit , Class<T> clazz) {
		T t = mapEntity(source , clazz) ;
		
		Map<String, HighlightField> highlightFields = hit.getHighlightFields();
		HighlightField highlightNameField = highlightFields.get("title");
		HighlightField contentHightlightField = highlightFields.get("content");
		try {
			if(highlightNameField!=null&&highlightNameField.fragments()!=null){
				PropertyUtils.setProperty(t, "title" , highlightNameField.fragments()[0].string());
			}
			if(contentHightlightField!=null){
				PropertyUtils.setProperty(t, "content" , contentHightlightField.fragments()[0].string());
			}
			PropertyUtils.setProperty(t, "id" , hit.getId());
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		} 
		return t;
	}

	private <T> void populateScriptFields(T result, SearchHit hit) {
		if (hit.getFields() != null && !hit.getFields().isEmpty() && result != null) {
			for (java.lang.reflect.Field field : result.getClass().getDeclaredFields()) {
				ScriptedField scriptedField = field.getAnnotation(ScriptedField.class);
				if (scriptedField != null) {
					String name = scriptedField.name().isEmpty() ? field.getName() : scriptedField.name();
					SearchHitField searchHitField = hit.getFields().get(name);
					if (searchHitField != null) {
						field.setAccessible(true);
						try {
							if(name.equals("title") && hit.getHighlightFields().get("title")!=null){
								field.set(result, hit.getHighlightFields().get("title").fragments()[0].string());
							}else{
								field.set(result, searchHitField.getValue());
							}
						} catch (IllegalArgumentException e) {
							throw new ElasticsearchException("failed to set scripted field: " + name + " with value: "
									+ searchHitField.getValue(), e);
						} catch (IllegalAccessException e) {
							throw new ElasticsearchException("failed to access scripted field: " + name, e);
						}
					}
				}
			}
		}
	}


	public <T> T mapEntity(Collection<SearchHitField> values, SearchHit hit , Class<T> clazz) {
		return mapEntity(buildJSONFromFields(values) , hit , clazz);
	}

	private String buildJSONFromFields(Collection<SearchHitField> values) {
		JsonFactory nodeFactory = new JsonFactory();
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
			 JsonGenerator generator = nodeFactory.createGenerator(stream, JsonEncoding.UTF8);) {

			generator.writeStartObject();
			for (SearchHitField value : values) {
				if (value.getValues().size() > 1) {
					generator.writeArrayFieldStart(value.getName());
					for (Object val : value.getValues()) {
						generator.writeObject(val);
					}
					generator.writeEndArray();
				} else {
					generator.writeObjectField(value.getName(), value.getValue());
				}
			}
			generator.writeEndObject();
			generator.flush();
			return new String(stream.toByteArray(), Charset.forName("UTF-8"));
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public <T> T mapResult(GetResponse response, Class<T> clazz) {
		T result = mapEntity(response.getSourceAsString(), clazz);
		if (result != null) {
			setPersistentEntityId(result, response.getId(), clazz);
		}
		return result;
	}

	@Override
	public <T> LinkedList<T> mapResults(MultiGetResponse responses, Class<T> clazz) {
		LinkedList<T> list = new LinkedList<T>();
		for (MultiGetItemResponse response : responses.getResponses()) {
			if (!response.isFailed() && response.getResponse().isExists()) {
				T result = mapEntity(response.getResponse().getSourceAsString(), clazz);
				setPersistentEntityId(result, response.getResponse().getId(), clazz);
				list.add(result);
			}
		}
		return list;
	}

	private <T> void setPersistentEntityId(T result, String id, Class<T> clazz) {

		if (mappingContext != null && clazz.isAnnotationPresent(Document.class)) {

			ElasticsearchPersistentEntity<?> persistentEntity = mappingContext.getPersistentEntity(clazz);
			PersistentProperty<?> idProperty = persistentEntity.getIdProperty();
			
			// Only deal with String because ES generated Ids are strings !
			if (idProperty != null && idProperty.getType().isAssignableFrom(String.class)) {
				persistentEntity.getPropertyAccessor(result).setProperty(idProperty, id);
			}
		}
	}
}