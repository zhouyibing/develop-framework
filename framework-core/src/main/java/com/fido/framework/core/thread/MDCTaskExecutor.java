package com.fido.framework.core.thread;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 加入MDC的包装TaskExecutor
 * 方便链路追踪
 */
public class MDCTaskExecutor extends ThreadPoolTaskExecutor {
    private static final long serialVersionUID = 5673853155997465792L;

    @Override
    public void execute(Runnable task) {
        super.execute(MDCTaskWrapper.wrapRunnable(task, MDCTaskWrapper.getContextForTask()));
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        super.execute(MDCTaskWrapper.wrapRunnable(task, MDCTaskWrapper.getContextForTask()), startTimeout);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(MDCTaskWrapper.wrapRunnable(task, MDCTaskWrapper.getContextForTask()));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(MDCTaskWrapper.wrapCallable(task, MDCTaskWrapper.getContextForTask()));
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        return super.submitListenable(MDCTaskWrapper.wrapRunnable(task, MDCTaskWrapper.getContextForTask()));
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        return super.submitListenable(MDCTaskWrapper.wrapCallable(task, MDCTaskWrapper.getContextForTask()));
    }
}
