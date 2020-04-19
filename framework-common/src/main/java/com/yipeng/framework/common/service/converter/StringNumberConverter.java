package com.yipeng.framework.common.service.converter;


/**
 * @author: yibingzhou
 */
public abstract class StringNumberConverter<T extends Number> implements Converter<String, T> {

    @Override
    public String reverse(T t) {
        if(t == null) return null;
        return t.toString();
    }
}
