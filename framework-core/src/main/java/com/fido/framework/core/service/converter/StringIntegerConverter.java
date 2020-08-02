package com.fido.framework.core.service.converter;

/**
 * @author: yibingzhou
 */
public class StringIntegerConverter extends StringNumberConverter<Integer>{

    @Override
    public Class<String> sourceClass() {
        return String.class;
    }

    @Override
    public Class<Integer> targetClass() {
        return Integer.class;
    }

    @Override
    public Integer convert(String s) {
        if (s == null) {
            return null;
        }
        return Integer.valueOf(s);
    }
}
