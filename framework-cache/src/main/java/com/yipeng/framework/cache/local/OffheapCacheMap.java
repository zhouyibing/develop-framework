package com.yipeng.framework.cache.local;


import com.yipeng.framework.cache.CacheMap;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 堆外内存缓存（支持并发）
 *
 * TODO 暂未实现
 * @param <K>
 * @param <V>
 */
public class OffheapCacheMap<K extends Serializable, V extends Serializable> implements CacheMap<K,V> {
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
