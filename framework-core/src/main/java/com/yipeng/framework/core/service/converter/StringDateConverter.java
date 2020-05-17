package com.yipeng.framework.core.service.converter;

import com.yipeng.framework.core.utils.DateUtils;

import java.util.Date;

/**
 * 日期字符串转换
 * @author: yibingzhou
 */
public class StringDateConverter implements Converter<String, Date> {

    @Override
    public Class<String> sourceClass() {
        return String.class;
    }

    @Override
    public Class<Date> targetClass() {
        return Date.class;
    }

    @Override
    public Date convert(String string) {
        return DateUtils.parseDate(string);
    }

    /**
     * 将Date转换为默认yyyy-MM-dd HH:mm:ss 类型的字符串
     * @param date
     * @return
     */
    @Override
    public String reverse(Date date) {
        return DateUtils.formatDateTime(date);
    }
}
