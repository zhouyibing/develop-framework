package com.yipeng.framework.common.constants.annotation;

import java.lang.annotation.*;

/**
 * 标记哪些字段在转换时忽略
 * 标记在字段上表示该字段不需要被转换
 * 标记在类上表示该类里哪些字段不需要被转换
 * @author: yibingzhou
 */
@Target({ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConvertExclude {
    String[] value() default {};
}