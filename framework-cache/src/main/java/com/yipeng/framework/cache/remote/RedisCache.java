package com.yipeng.framework.cache.remote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author: yibingzhou
 */
public class RedisCache<K,V> {

    @Autowired
    private RedisTemplate<K, V> redisTemplate;

    public V get(K key){
        return redisTemplate.opsForValue().get(key);
    }

    public void set(K key, V value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Boolean setIfAbsent(K key, V value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

}
