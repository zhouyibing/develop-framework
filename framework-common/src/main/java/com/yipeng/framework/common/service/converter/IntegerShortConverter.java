package com.yipeng.framework.common.service.converter;


/**
 * @author: yibingzhou
 */
public class IntegerShortConverter implements Converter<Integer, Short> {

    @Override
    public Class<Integer> sourceClass() {
        return Integer.class;
    }

    @Override
    public Class<Short> targetClass() {
        return Short.class;
    }

    @Override
    public Short convert(Integer source) {
        if (source == null) {
            return null;
        }
        return source.shortValue();
    }

    @Override
    public Integer reverse(Short target) {
        if (target == null) {
            return null;
        }
        return target.intValue();
    }
}
