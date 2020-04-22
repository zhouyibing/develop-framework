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

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 基于{@linkplain ConcurrentCacheMap}的二级索引
 */
@Slf4j
public class ConcurrentCacheMapIndex<K, V, S> {
    private final ConcurrentCacheMap<K, V> cache;
    private Function<V, S> getIndex;
    private final Map<S, Map<K, V>> indexMap = new ConcurrentHashMap<>();
    
	public ConcurrentCacheMapIndex(ConcurrentCacheMap<K, V> cache, Function<V, S> getIndex) {
	    this.cache = cache;
	    this.getIndex = getIndex;
	}
	
	public Map<K, V> getValues(S index) {
	    if (index == null) return null;
	    
	    return indexMap.get(index);
	}
	
	public void update(List<V> list) {
        Set<S> changed = new HashSet<>();
        Map<S, Map<K, V>> tmp = new HashMap<>();
        Map<S, Set<K>> removed = new HashMap<>();
        
        for (V v : list) {
            S index = getIndex.apply(v);
            K k = cache.getKey(v);
            
            changed.add(index);
            if (cache.get(k) == null) {
                removed.computeIfAbsent(index, kk -> new HashSet<>()).add(k);
            } else {
                tmp.computeIfAbsent(index, kk -> new HashMap<>()).put(k, v);
            }
        }
        
        int indexCount = 0;
        int addCount = 0;
        int removeCount = 0;
        synchronized (this) {
            for (S index : changed) {
                Map<K, V> old = indexMap.get(index);
                Map<K, V> add = tmp.get(index);
                if (old == null) {
                    if (add != null) {
                        indexCount++;
                        addCount += add.size();
                        indexMap.put(index, add);
                    }
                    continue;
                }
                
                Set<K> remove = removed.remove(index);
                if (add == null) {
                    add = new HashMap<>(old);
                } else {
                    add.putAll(old);
                }
                if (remove != null) {
                    for (K k : remove) {
                        if (add.remove(k) != null) {
                            removeCount++;
                        }
                    }
                }
                int detSize = add.size() + removeCount - old.size();
                if (detSize != 0) {
                    indexCount++;
                    addCount += detSize;
                }
                indexMap.put(index, add);
            }
        }
        if (indexCount > 0) {
            log.info("size={}, index={}, add={}, remove={}", changed.size(), indexCount, addCount, removeCount);
        }
    }
}
