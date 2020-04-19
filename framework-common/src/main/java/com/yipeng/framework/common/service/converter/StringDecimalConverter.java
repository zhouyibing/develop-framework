package com.yipeng.framework.common.service.converter;

import com.yipeng.framework.common.utils.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;


/**
 * @author: yibingzhou
 */
public class StringDecimalConverter implements Converter<String, BigDecimal> {

    @Override
    public Class<String> sourceClass() {
        return String.class;
    }

    @Override
    public Class<BigDecimal> targetClass() {
        return BigDecimal.class;
    }

    @Override
    public BigDecimal convert(String s) {
        if(StringUtils.isBlank(s)) return BigDecimal.ZERO;
        return new BigDecimal(s);
    }

    @Override
    public String reverse(BigDecimal bigDecimal) {
        if(bigDecimal == null) return null;
        return NumberUtils.formatNum(bigDecimal, 2);
    }
}
