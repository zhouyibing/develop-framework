package com.yipeng.framework.common.constants.annotation;

import com.yipeng.framework.common.constants.ConvertRule;

import java.lang.annotation.*;

/**
 * 标记类是否需要被转换，同时配置那些字段不需要转换
 * @author: yibingzhou
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Convert {

    /**
     * 转换规则
     * @return
     */
    ConvertRule rule() default ConvertRule.FULL_NAME;

    /**
     * 哪些字段需要忽略
     * @return
     */
    String[] ignores() default {};

    /**
     * 哪些字段需要转换
     * @return
     */
    String[] value() default {};
}
