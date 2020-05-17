package com.yipeng.framework.core.constants.annotation;

import java.lang.annotation.*;

/**
 * @author: yibingzhou
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface FieldMappings {
    FieldMapping[] value();
}
