package com.yipeng.framework.core.constants.annotation;


import java.lang.annotation.*;

/**
 * 标记哪些字段在转换时需要
 * 标记在字段上表示该字段需要被转换
 * 标记在类上表示该类里哪些字段需要被转换
 * @author: yibingzhou
 */
@Target({ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConvertInclude {
    String[] value() default {};
}
