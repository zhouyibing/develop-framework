package com.yipeng.framework.common.constants.annotation;

import com.yipeng.framework.common.service.converter.Converter;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.*;

/**
 * 字段转换时设置映射关系
 * @author: yibingzhou
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FieldMapping {

    /**
     * 映射的source对象字段,默认和source字段同名
     * @return
     */
    String value() default StringUtils.EMPTY;

    /***
     * 自定义转换器，class必须实现 @see com.yipeng.framework.common.service.converter.Converter接口
     * @return
     */
    Class converter() default Converter.class;
}
