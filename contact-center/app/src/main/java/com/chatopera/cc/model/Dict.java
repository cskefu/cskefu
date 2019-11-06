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
package com.chatopera.cc.model;

import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.cache.RedisKey;
import com.chatopera.cc.util.SerializeUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Dict<K, V> extends HashMap<K, V> {

    private static final long serialVersionUID = 2110217015030751243L;
    private static Dict<Object, Object> dict = new Dict<Object, Object>();
    private static final Logger logger = LoggerFactory.getLogger(Dict.class);


    public static Dict<?, ?> getInstance() {
        return dict;
    }


    @Override
    @SuppressWarnings("unchecked")
    public V get(final Object key) {
        String keystr = String.valueOf(key);
        // TODO 从日志中看到，有时会查找key为空的调用，这是为什么？
        logger.debug("[get] key {}", keystr);
        Object result = null;

        String serialized = MainContext.getRedisCommand().getHashKV(RedisKey.getSysDicHashKeyByOrgi(MainContext.SYSTEM_ORGI), keystr);

        if (StringUtils.isNotBlank(serialized)) {
            Object obj = SerializeUtil.deserialize(serialized);
            if (obj instanceof List) {
                result = getDic(keystr);
            } else {
                result = obj;
            }
        } else if (keystr.endsWith(".subdic") && keystr.lastIndexOf(".subdic") > 0) {
            String id = keystr.substring(0, keystr.lastIndexOf(".subdic"));
            SysDic dic = MainContext.getCache().findOneSysDicByIdAndOrgi(id, MainContext.SYSTEM_ORGI);
            if (dic != null) {
                SysDic sysDic = MainContext.getCache().findOneSysDicByIdAndOrgi(dic.getDicid(), MainContext.SYSTEM_ORGI);
                result = getDic(sysDic.getCode(), dic.getParentid());
            }
        }

        return (V) result;
    }

    @SuppressWarnings("unchecked")
    public List<SysDic> getDic(final String code) {
        List<SysDic> result = new ArrayList<SysDic>();
        String serialized = MainContext.getRedisCommand().getHashKV(RedisKey.getSysDicHashKeyByOrgi(MainContext.SYSTEM_ORGI), code);

        if (StringUtils.isNotBlank(serialized)) {
            Object obj = SerializeUtil.deserialize(serialized);
            if (obj instanceof List) {
                List<SysDic> sysDics = (List<SysDic>) obj;
                for (SysDic dic : sysDics) {
                    if (dic.getDicid().equals(dic.getParentid())) {
                        result.add(dic);
                    }
                }
            } else {
                logger.warn("[getDic list] nothing found for code or id {} with deserialize, this is a potential error.", code);
            }
        } else {
            logger.debug("[getDic list] nothing found for code or id {}", code);
        }

        logger.debug("[getDic list] code or id: {}, dict size {}", code, result.size());

        return result;
    }

    /**
     * 获得一个词典的所有子项，并且每个子项的父都是id
     *
     * @param code
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<SysDic> getDic(final String code, final String id) {
        List<SysDic> result = new ArrayList<SysDic>();
        String serialized = MainContext.getRedisCommand().getHashKV(RedisKey.getSysDicHashKeyByOrgi(MainContext.SYSTEM_ORGI), code);

        if (StringUtils.isNotBlank(serialized)) {
            Object obj = SerializeUtil.deserialize(serialized);
            if (obj instanceof List) {
                List<SysDic> sysDics = (List<SysDic>) obj;
                for (SysDic dic : sysDics) {
                    if (dic.getParentid().equals(id)) {
                        result.add(dic);
                    }
                }
            } else if (obj instanceof SysDic) {
                result.add((SysDic) obj);
            } else {
                logger.warn("[getDic] nothing found for code or id {} with deserialize, this is a potential error.", code);
            }
        } else {
            logger.warn("[getDic] nothing found for code or id {}", code);
        }

        logger.debug("[getDic list] code or id: {}, dict size {}", code, result.size());

        return result;
    }


    /**
     * 获得一个根词典的所有子项
     *
     * @param code
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<SysDic> getSysDic(String code) {
        return MainContext.getCache().getSysDicItemsByCodeAndOrgi(code, MainContext.SYSTEM_ORGI);
    }

    /**
     * 获得一个词典子项
     *
     * @param code
     * @return
     */
    public SysDic getDicItem(String code) {
        return MainContext.getCache().findOneSysDicByCodeAndOrgi(code, MainContext.SYSTEM_ORGI);
    }
}
