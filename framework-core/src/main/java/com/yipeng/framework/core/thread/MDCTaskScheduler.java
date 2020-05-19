package com.yipeng.framework.core.thread;

import org.slf4j.MDC;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

/**
 * 加入MDC的包装TaskScheduler
 * 方便链路追踪
 */
public class MDCTaskScheduler extends ThreadPoolTaskScheduler {
        private static final long serialVersionUID = 5849131060339559287L;

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

        @Override
        public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
            return super.schedule(MDCTaskWrapper.wrapRunnable(task, MDCTaskWrapper.getContextForTask()), trigger);
        }

        @Override
        public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
            return super.schedule(MDCTaskWrapper.wrapRunnable(task, MDCTaskWrapper.getContextForTask()), startTime);
        }

        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
            return super.scheduleAtFixedRate(MDCTaskWrapper.wrapRunnable(task, MDCTaskWrapper.getContextForTask()), startTime, period);
        }

        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
            return super.scheduleAtFixedRate(MDCTaskWrapper.wrapRunnable(task, MDCTaskWrapper.getContextForTask()), period);
        }

        @Override
        public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
            return super.scheduleWithFixedDelay(MDCTaskWrapper.wrapRunnable(task, MDCTaskWrapper.getContextForTask()), startTime, delay);
        }

        @Override
        public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
            return super.scheduleWithFixedDelay(MDCTaskWrapper.wrapRunnable(task, MDCTaskWrapper.getContextForTask()), delay);
        }
    };