package com.yipeng.framework.cache;
import java.util.Collection;
import java.util.List;

/**
 * 本地缓存
 * @param <K> key
 * @param <V> value
 */
public interface CacheMap<K,V> {

    /**
     * 更新多值
     * @param values
     */
    void update(List<V> values);

    /**
     *手动更新当前的缓存(beginTime是查询开始时间，帮助日志更好显示整个缓存更新时间)
     * @param beginTime
     * @param values
     */
    void update(long beginTime, List<V> values);

    /**
     *
     * @param value
     * @return
     */
    K getKey(V value);

    /**
     * 获取缓存值集合
     * @return
     */
    Collection<V> values();

    /**
     * 缓存size
     * @return
     */
    int size();

    /**
     * 缓存集合是否为空
     * @return
     */
    boolean isEmpty();

    /**
     * 获取值
     * @param key
     * @return
     */
    V get(K key);

    /**
     * 删除值
     * @param key
     * @return
     */
    V remove(K key);

    /**
     * 更新才put（根据getUpdateTime funcion判断）
     * @param value
     */
    void putIfUpdated(V value);

    /**
     * 更新才put（根据getUpdateTime funcion判断）
     * @param values
     */
    void putIfUpdated(List<V> values);

    /**
     * 根据maxUpdateTime自动刷新缓存
     */
    void refresh();
}
