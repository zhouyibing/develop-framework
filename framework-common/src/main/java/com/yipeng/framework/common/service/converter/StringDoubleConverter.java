package com.yipeng.framework.common.service.converter;

/**
 * @author: yibingzhou
 */
public class StringDoubleConverter extends StringNumberConverter<Double>{

    @Override
    public Class<String> sourceClass() {
        return String.class;
    }

    @Override
    public Class<Double> targetClass() {
        return Double.class;
    }

    @Override
    public Double convert(String s) {
        if(s == null) return null;
        return Double.valueOf(s);
    }
}
