package com.yipeng.framework.core.utils;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 事务逻辑封装，解决内部方法调用@Transactional方法问题
 * @author: yibingzhou
 * TODO 该类还没验证过
 */

@Component
public class TransactionalHelper {

    /**
     * 事务逻辑封装
     * @param runnable 你的事务代码逻辑
     */
    @Transactional
    public void transactional(Runnable runnable) {
        runnable.run();
    }
}
