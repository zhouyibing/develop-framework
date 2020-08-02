package com.fido.framework.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 异步逻辑封装，解决内部方法调用@Async方法问题
 * @author: yibingzhou
 */
@Component
@Slf4j
@ConditionalOnBean(name = "singleThreadPool")
public class AsyncHelper implements InitializingBean {

    private DelayQueue<DelayElement> delayQueue = new DelayQueue();
    @Autowired
    private AsyncHelper that;
    @Autowired
    private ThreadPoolTaskExecutor singleThreadPool;

    private Runnable delayExecutor = new Runnable() {

        @Override
        public void run() {
            DelayElement delayElement = null;
            try {
                while ((delayElement = delayQueue.take()) != null) {
                    if(log.isDebugEnabled()) {
                        log.debug("execute task of '{}' at {}", delayElement.name, delayElement.getDelayTime());
                    }
                    that.async(delayElement.runnable);
                }
            }catch (Throwable e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 异步执行逻辑
     * @param runnable 你的代码逻辑
     */
    @Async
    public void async(Runnable runnable) {
        runnable.run();
    }

    /**
     * 异步延迟处理
     * @param runnable
     */
    public void asyncDelay(Runnable runnable, long ms) {
        asyncDelay(null, runnable, ms);
    }

    public void asyncDelay(String name, Runnable runnable, long ms) {
        delayQueue.put(new DelayElement(name, runnable,ms));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        singleThreadPool.submit(delayExecutor);
    }


    public class DelayElement implements Delayed{

        // 延迟截止时间（单面：毫秒）
        long delayTime = System.currentTimeMillis();
        Runnable runnable;
        String name;
        public DelayElement(Runnable runnable, long delayTime) {
            this(null, runnable, delayTime);
        }

        public DelayElement(String name, Runnable runnable, long delayTime) {
            this.name = name;
            this.runnable = runnable;
            this.delayTime = (this.delayTime + delayTime);
        }

        @Override
        // 获取剩余时间
        public long getDelay(TimeUnit unit) {
            return unit.convert(delayTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        // 队列里元素的排序依据
        public int compareTo(Delayed o) {
            if (this.getDelay(TimeUnit.MILLISECONDS) > o.getDelay(TimeUnit.MILLISECONDS)) {
                return 1;
            } else if (this.getDelay(TimeUnit.MILLISECONDS) < o.getDelay(TimeUnit.MILLISECONDS)) {
                return -1;
            } else {
                return 0;
            }
        }

        public String getDelayTime() {
            return DateFormat.getDateTimeInstance().format(new Date(delayTime));
        }
    }
}
