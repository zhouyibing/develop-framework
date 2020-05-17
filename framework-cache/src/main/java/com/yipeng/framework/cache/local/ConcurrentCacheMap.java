/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yipeng.framework.cache.local;
import com.yipeng.framework.cache.CacheMap;
import com.yipeng.framework.core.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 基于ConcurrentHashMap实现的缓存
 */
@Slf4j
public class ConcurrentCacheMap<K, V> implements CacheMap<K, V> {
	@Getter
	private String name = "";
	private Function<V, K> getKey;
	private Function<V, Date> getUpdateTime;
	private Function<Date, List<V>> queryUpdatedFrom;
	@Setter
	private Function<V, V> mapping;
	@Setter
	private Consumer<List<V>> onRefresh;
	/** 上次执行lastRefreshTime时间 */
	private volatile long lastRefreshTime = -1;
	
	// 以下是运行时状态
	/** 最大记录时间 */
	@Setter
	@Getter
	private Date maxUpdateTime;
	private final Map<K, V> cache = new ConcurrentHashMap<>();
	
	public ConcurrentCacheMap(String name, Function<V, K> getKey, Function<V, Date> getUpdateTime, Function<Date, List<V>> queryUpdatedFrom) {
		this.name = name;
		this.getKey = getKey;
		this.getUpdateTime = getUpdateTime;
		this.queryUpdatedFrom = queryUpdatedFrom;
	}

	@Override
	public void putIfUpdated(V value) {
		if (value != null) {
			putIfUpdated(Arrays.asList(value));
		}
	}

	@Override
	public void putIfUpdated(List<V> values) {
        if (values == null || values.isEmpty()) {
        	return;
		}
        
        List<V> updateds = new ArrayList<>(values.size());
        for (V value : values) {
            K key = getKey.apply(value);
            V target = mapping == null ? value : mapping.apply(value);
            
            if (target == null) {
                log.info("remove key={}, value={}", key, value);
                cache.remove(key);
            } else {
                V old = cache.get(key);
                if (old != null && getUpdateTime.apply(old).getTime() >= getUpdateTime.apply(target).getTime()) {
					log.info("ignore key={}, value={}", key, value);
                    continue;
                }
				log.info("{} key={}, target={}", old == null ? "add" : "update",  key, target);
                cache.put(key, target);
            }
            updateds.add(value);
        }
        if (onRefresh != null && !updateds.isEmpty()) {
        	onRefresh.accept(updateds);
		}
    }
	
	/** 根据maxUpdateTime自动刷新缓存 */
	@Override
	public void refresh() {
		long beginTime = System.currentTimeMillis();
		List<V> values = queryUpdatedFrom.apply(maxUpdateTime);
		if (values.isEmpty()) {
			return;
		}
		
		update(beginTime, values);
		lastRefreshTime = beginTime;
	}

	/** 手动更新当前的缓存 */
	@Override
	public void update(List<V> values) {
	    update(0, values);
	}

	/** 手动更新当前的缓存(beginTime是查询开始时间，帮助日志更好显示整个缓存更新时间) */
	@Override
	public void update(long beginTime, List<V> values) {
		if (values.isEmpty()) {
			return;
		}

		long beginProc = System.currentTimeMillis();
		int oldSize = cache.size();
		int add = 0, updated = 0;
		Date maxTime = maxUpdateTime;
		final long lastMaxUpdateTime = maxUpdateTime == null ? 0 : maxUpdateTime.getTime();
		for (V value : values) {
			K key = getKey.apply(value);
			Date updateTime = getUpdateTime.apply(value);
			if (updateTime.getTime() < lastMaxUpdateTime) {
				continue;	//老记录
			}
			
			if (DateUtils.gt(updateTime, maxTime)) {
				maxTime = updateTime;
			}
			V old = cache.get(key);
			if (old != null && Objects.equals(updateTime, getUpdateTime.apply(old))) {
				continue;	//记录时间未变，则记录未变
			}
			
			if (mapping != null) {
				value = mapping.apply(value);
			}
			if (value == null) {
				cache.remove(key);
			} else {
				cache.put(key, value);
			}
			if (old == null) {
				add++;
			} else {
				updated++;
			}
		}
		int changed = add + updated;
		if (changed == 0) {
		    if (DateUtils.gt(maxTime, maxUpdateTime)) {
		        maxUpdateTime = maxTime;
		    }
		    return;
		}
		int procSize = cache.size();

		long beginOnRefresh = System.currentTimeMillis();
		if (onRefresh != null) {
			onRefresh.accept(values);
		}
		int onRefreshSize = cache.size();
		
		if (cache.size() != oldSize || !Objects.equals(maxTime, maxUpdateTime)) {
			long endTime = System.currentTimeMillis();
			log.info("{} 刷新 ({} -> {}({}), 更新: {}, {} -> {}), query: {}(add={}, update={}, keep={}), (old={}, proc={}({}), onRefresh={}({})), 耗时: {}(query={}, proc={}, onRefresh={})",
					name, oldSize, cache.size(), (cache.size() - oldSize), changed,
					DateUtils.formatDate(maxUpdateTime, "yyyy-MM-dd HH:mm:ss.SSS"), DateUtils.formatDate(maxTime, "yyyy-MM-dd HH:mm:ss.SSS"),
					values.size(), add, updated, values.size() - changed,
					oldSize, procSize, (procSize - oldSize), onRefreshSize, (onRefreshSize - procSize),
					DateUtils.prettyTime(endTime - (beginTime > 0 ? beginTime : beginProc)),
					(beginTime > 0 ? DateUtils.prettyTime(beginProc - beginTime) : "?"),
					DateUtils.prettyTime(beginOnRefresh - beginProc),
					DateUtils.prettyTime(endTime - beginOnRefresh));
		}
		maxUpdateTime = maxTime;
	}

	@Override
	public K getKey(V value) {
		return getKey.apply(value);
	}

	@Override
	public int size() {
		return cache.size();
	}

	@Override
	public boolean isEmpty() {
	    return cache.isEmpty();
	}

	@Override
	public V get(K key) {
		return key == null ? null : cache.get(key);
	}

	@Override
	public V remove(K key) {
		return key == null ? null : cache.remove(key);
	}

	@Override
	public Collection<V> values() {
		return cache.values();
	}

	public long getLastRefreshTime() {
		return lastRefreshTime;
	}
	/** @return {@link #cache} */
	public Map<K, V> getCache() {
		return cache;
	}
}
