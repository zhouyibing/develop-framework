package com.fido.framework.core.service.converter;

/**
 * @author: yibingzhou
 */
public class StringByteConverter extends StringNumberConverter<Byte>{

    @Override
    public Class<String> sourceClass() {
        return String.class;
    }

    @Override
    public Class<Byte> targetClass() {
        return Byte.class;
    }

    @Override
    public Byte convert(String s) {
        if (s == null) {
            return null;
        }
        return Byte.valueOf(s);
    }
}
