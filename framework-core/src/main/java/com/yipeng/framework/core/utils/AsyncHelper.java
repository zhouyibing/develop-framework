package com.yipeng.framework.core.utils;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 异步逻辑封装，解决内部方法调用@Async方法问题
 * @author: yibingzhou
 */
@Component
public class AsyncHelper {

    /**
     * 异步执行逻辑
     * @param runnable 你的代码逻辑
     */
    @Async
    public void async(Runnable runnable) {
        runnable.run();
    }
}
