package com.yipeng.framework.common.constants.annotation;

import java.lang.annotation.*;

/**
 * 标记字段在转换时忽略
 * @author: yibingzhou
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConvertIgnore {
}