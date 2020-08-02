package com.fido.framework.core.configuration;

import com.fido.framework.core.thread.MDCTaskExecutor;
import com.fido.framework.core.thread.MDCTaskScheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 线程池配置
 * @author: yibingzhou
 */
@Configuration
public class ThreadPoolConfig {


    @Bean("singleThreadPool")
    public ThreadPoolTaskExecutor singleThreadPool() {
        ThreadPoolTaskExecutor executor = new MDCTaskExecutor();
        //设置核心线程数
        executor.setCorePoolSize(1);
        //设置最大线程数
        executor.setMaxPoolSize(1);
        //线程池所使用的缓冲队列
        executor.setQueueCapacity(0);
        //设置线程名
        executor.setThreadNamePrefix("single-pool-");
        //设置多余线程等待的时间，单位：秒
        executor.setKeepAliveSeconds(0);
        // 初始化线程
        executor.initialize();
        return executor;
    }

    @Bean
    public TaskScheduler taskScheduler(){
        ThreadPoolTaskScheduler taskScheduler = new MDCTaskScheduler();
        ////定义线程池数量为5 个
        taskScheduler.setPoolSize(5);
        return taskScheduler ;
    }


}
