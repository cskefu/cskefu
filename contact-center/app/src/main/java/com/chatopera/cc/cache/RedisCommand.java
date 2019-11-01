/*
 * Copyright (C) 2019 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Component
public class RedisCommand {

    final static private Logger logger = LoggerFactory.getLogger(RedisCommand.class);

    private ListOperations<String, String> redisListOps;
    private HashOperations<String, String, String> redisHashOps;
    private ValueOperations<String, String> redisValOps;
    private SetOperations<String, String> redisSetOps;

    /**
     * 使用StringRedisTemplate而不是RedisTemplate解决序列化问题
     * https://stackoverflow.com/questions/13215024/weird-redis-key-with-spring-data-jedis
     */
    @Autowired
    private StringRedisTemplate redis;

    @PostConstruct
    private void init() {
        redisListOps = redis.opsForList();
        redisHashOps = redis.opsForHash();
        redisValOps = redis.opsForValue();
        redisSetOps = redis.opsForSet();
    }


    /*****************************
     * String 相关
     *****************************/

    /**
     * 设置一个KEY
     *
     * @param key
     * @param serialized
     * @return
     */
    public boolean put(final String key, final String serialized) {
        boolean result = true;
        redisValOps.set(key, serialized);
        return result;
    }

    public String get(final String key) {
        return redisValOps.get(key);
    }

    /**
     * 删除一个KEY
     *
     * @param key
     */
    public void delete(final String key) {
        redis.delete(key);
    }

    /**
     * 设置一个KEY的过期时间
     *
     * @param key
     * @param seconds
     */
    public void expire(final String key, final long seconds) {
        redis.expire(key, seconds, TimeUnit.SECONDS);
    }

    /**
     * 获得一个KEY的过期时间
     *
     * @param key
     * @return
     */
    public long ttl(final String key) {
        return redis.getExpire(key);
    }


    /**
     * 是否存在一个KEY
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redis.hasKey(key);
    }


    /*****************************
     * List 相关
     *****************************/


    /**
     * 在列表右侧追加
     *
     * @param key
     * @param val
     * @return
     */
    public long appendList(final String key, final String val) {
        return redisListOps.rightPush(key, val);
    }

    /**
     * 获得一个列表的所有元素
     *
     * @param key
     * @return
     */
    public List<String> getList(final String key) {
        return redisListOps.range(key, 0, redisListOps.size(key));
    }

    /**
     * 获得一个类标的长度
     *
     * @param key
     * @return
     */
    public long listSize(final String key) {
        return redisListOps.size(key);
    }

    /**
     * Remove all value = val in key
     *
     * @param key
     * @param val
     */
    public void listRemove(final String key, final String val) {
        redisListOps.remove(key, 0, val);
    }


    /*****************************
     * Hash 相关
     *****************************/

    /**
     * 获得一个Hash的列表长度
     *
     * @param hashKey
     * @return
     */
    public int getHashSize(final String hashKey) {
        return Math.toIntExact(redisHashOps.size(hashKey));
    }

    /**
     * 获得多个Hash的全部成员
     *
     * @param keys
     * @return
     */
    public Map<String, String> getAllMembersInMultiHash(final List<String> keys) {
        return redis.execute((RedisCallback<Map<String, String>>) con -> {
            Map<String, String> ans = new HashMap<>();
            for (String key : keys) {
                Map<byte[], byte[]> result = con.hGetAll(key.getBytes());
                if (!CollectionUtils.isEmpty(result)) {
                    for (Map.Entry<byte[], byte[]> entry : result.entrySet()) {
                        ans.put(new String(entry.getKey()), new String(entry.getValue()));
                    }
                }
            }
            return ans;
        });
    }

    /**
     * 获得一个Hash中所有的值
     * https://juejin.im/post/5c1399a7f265da61764ac526
     *
     * @param hashKey
     * @return
     */
    public Map<String, String> getHash(final String hashKey) {
        return redis.execute((RedisCallback<Map<String, String>>) con -> {
            Map<byte[], byte[]> result = con.hGetAll(hashKey.getBytes());
            if (CollectionUtils.isEmpty(result)) {
                return new HashMap<>(0);
            }

            Map<String, String> ans = new HashMap<>(result.size());
            for (Map.Entry<byte[], byte[]> entry : result.entrySet()) {
                ans.put(new String(entry.getKey()), new String(entry.getValue()));
            }
            return ans;
        });
    }

    /**
     * 设置Hash Map KV
     *
     * @param hashKey
     * @param childKey
     * @param childVal
     */
    public void setHashKV(final String hashKey, final String childKey, final String childVal) {
        redisHashOps.put(hashKey, childKey, childVal);
    }

    /**
     * 指定的Hash实存存在键
     *
     * @param key
     * @param childKey
     * @return
     */
    public boolean hasHashKV(String key, String childKey) {
        return redisHashOps.hasKey(key, childKey);
    }

    /**
     * 获得Hash Map KV的值
     *
     * @param hashKey
     * @param childKey
     * @return
     */
    public String getHashKV(final String hashKey, final String childKey) {
        return redisHashOps.get(hashKey, childKey);
    }

    /**
     * 删除Hash Map KV的值
     *
     * @param hashKey
     * @param childKey
     */
    public void delHashKV(final String hashKey, final String childKey) {
        redisHashOps.delete(hashKey, childKey);
    }

    /**
     * HashSet
     * https://www.cnblogs.com/hongdada/p/9141125.html
     * 寻求使用 hmset 提升大量HASH KEY的存储 https://redis.io/commands/hmset
     * TODO 查看 putAll源代码确定是用hmset
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(final String key, final Map<String, String> map) {
        try {
            redisHashOps.putAll(key, map);
            return true;
        } catch (Exception e) {
            logger.error("hmset bad things happen", e);
            return false;
        }
    }


    /*****************************
     * Set 相关
     *****************************/

    public void insertSetVal(final String key, final String val) {
        redisSetOps.add(key, val);
    }

    public void removeSetVal(final String key, final String val) {
        redisSetOps.remove(key, val);
    }

    public int getSetSize(final String key) {
        return Math.toIntExact(redisSetOps.size(key));
    }

    public List<String> getSet(final String key) {
        Set<String> s = redisSetOps.members(key);

        if (s != null & s.size() > 0) {
            return new ArrayList<>(s);
        } else {
            return new ArrayList<>();
        }
    }

}
