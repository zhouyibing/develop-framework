package com.yipeng.framework.core.service.converter;

/**
 * @author: yibingzhou
 */
public class StringLongConverter extends StringNumberConverter<Long>{

    @Override
    public Class<String> sourceClass() {
        return String.class;
    }

    @Override
    public Class<Long> targetClass() {
        return Long.class;
    }

    @Override
    public Long convert(String s) {
        if (s == null) {
            return null;
        }
        return Long.valueOf(s);
    }
}
