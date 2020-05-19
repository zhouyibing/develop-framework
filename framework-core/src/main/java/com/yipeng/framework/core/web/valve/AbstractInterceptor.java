package com.yipeng.framework.core.web.valve;

import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author: yibingzhou
 */
public interface AbstractInterceptor extends HandlerInterceptor {
    default String[] pathPatterns() {
        return new String[]{"/**"};
    }
}
