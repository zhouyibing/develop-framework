package com.yipeng.framework.cache.remote;

import com.yipeng.framework.cache.CacheMap;

import java.util.Collection;
import java.util.List;

/**
 * @author: yibingzhou
 */
public class RedisCacheMap<K,V> implements CacheMap<K,V> {
    @Override
    public void update(List<V> values) {

    }

    @Override
    public void update(long beginTime, List<V> values) {

    }

    @Override
    public K getKey(V value) {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public V get(K key) {
        return null;
    }

    @Override
    public V remove(K key) {
        return null;
    }

    @Override
    public void putIfUpdated(V value) {

    }

    @Override
    public void putIfUpdated(List<V> values) {

    }

    @Override
    public void refresh() {

    }
}
