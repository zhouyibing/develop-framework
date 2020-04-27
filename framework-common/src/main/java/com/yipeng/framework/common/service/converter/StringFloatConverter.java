package com.yipeng.framework.common.service.converter;

/**
 * @author: yibingzhou
 */
public class StringFloatConverter extends StringNumberConverter<Float>{

    @Override
    public Class<String> sourceClass() {
        return String.class;
    }

    @Override
    public Class<Float> targetClass() {
        return Float.class;
    }

    @Override
    public Float convert(String s) {
        if (s == null) {
            return null;
        }
        return Float.valueOf(s);
    }
}
