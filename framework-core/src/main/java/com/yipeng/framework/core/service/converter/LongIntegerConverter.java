package com.yipeng.framework.core.service.converter;


/**
 * @author: yibingzhou
 */
public class LongIntegerConverter implements Converter<Long, Integer> {
    @Override
    public Class<Long> sourceClass() {
        return Long.class;
    }

    @Override
    public Class<Integer> targetClass() {
        return Integer.class;
    }

    @Override
    public Integer convert(Long aLong) {
        if (aLong == null) {
            return null;
        }
        return aLong.intValue();
    }

    @Override
    public Long reverse(Integer integer) {
        if (integer == null) {
            return null;
        }
        return integer.longValue();
    }
}
