package com.yipeng.framework.core.service.converter;


/**
 * @author: yibingzhou
 */
public class BooleanIntegerConverter implements Converter<Boolean,Integer> {

    @Override
    public Class<Boolean> sourceClass() {
        return Boolean.class;
    }

    @Override
    public Class<Integer> targetClass() {
        return Integer.class;
    }

    @Override
    public Integer convert(Boolean aBoolean) {
        return (null == aBoolean || aBoolean.equals(Boolean.FALSE)) ? 0 : 1;
    }

    @Override
    public Boolean reverse(Integer integer) {
        return (null == integer || integer ==0) ? Boolean.FALSE : Boolean.TRUE;
    }
}
