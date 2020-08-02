package com.fido.framework.core.service.converter;

/**
 * @author: yibingzhou
 */
public class BooleanByteConverter implements Converter<Boolean, Byte> {

    @Override
    public Class<Boolean> sourceClass() {
        return Boolean.class;
    }

    @Override
    public Class<Byte> targetClass() {
        return Byte.class;
    }

    @Override
    public Byte convert(Boolean aBoolean) {
        return (null == aBoolean || aBoolean.equals(Boolean.FALSE)) ? (byte)0 : (byte)1;
    }

    @Override
    public Boolean reverse(Byte b) {
        return (null == b || b ==0) ? Boolean.FALSE : Boolean.TRUE;
    }
}
