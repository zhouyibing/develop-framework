package com.yipeng.framework.core.service.converter;

/**
 * @author: yibingzhou
 */
public interface Converter<S,T> {
    Class<S> sourceClass();

    Class<T> targetClass();

    T convert(S source);

    S reverse(T target);
}
