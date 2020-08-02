package com.fido.framework.core.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;

/**
 * BigDecimal 格式化输出
 * @author: yibingzhou
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BigDecimalFormat {
    //默认两位精度
    int scale() default 2;
    //0是否输出空值
    boolean zeroToEmpty() default true;
    //Rounding mode
    int mode() default BigDecimal.ROUND_HALF_UP;
}
