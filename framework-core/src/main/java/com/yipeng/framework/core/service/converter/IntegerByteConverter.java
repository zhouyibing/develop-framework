package com.yipeng.framework.core.service.converter;


/**
 * @author: yibingzhou
 */
public class IntegerByteConverter implements Converter<Integer, Byte> {

    @Override
    public Class<Integer> sourceClass() {
        return Integer.class;
    }

    @Override
    public Class<Byte> targetClass() {
        return Byte.class;
    }

    @Override
    public Byte convert(Integer source) {
        if (source == null) {
            return null;
        }
        return source.byteValue();
    }

    @Override
    public Integer reverse(Byte target) {
        if (target == null) {
            return null;
        }
        return target.intValue();
    }
}
