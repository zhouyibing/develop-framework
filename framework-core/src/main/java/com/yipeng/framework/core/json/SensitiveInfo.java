package com.yipeng.framework.core.json;

import com.yipeng.framework.core.constants.StrPosition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 敏感信息注解
 * 至少隐藏1/3长度
 * @author: yibingzhou
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SensitiveInfo {
    SensitiveType type();
    StrPosition pos() default StrPosition.CENTER;
    boolean hidden() default true;
    int count() default 0;
}
