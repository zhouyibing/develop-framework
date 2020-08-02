package com.fido.framework.core.constants.annotation;

import org.apache.commons.lang3.StringUtils;

/**
 * 标记需要权限检查
 * @author: yibingzhou
 */
public @interface CheckAuth {
    /**
     * 需要什么角色
     * @return
     */
    String roleName() default StringUtils.EMPTY;

    /**
     * 需要什么权限
     * @return
     */
    String[] rights() default {};
}
