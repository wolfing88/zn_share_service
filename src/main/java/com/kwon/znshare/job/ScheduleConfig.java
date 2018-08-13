package com.kwon.znshare.job;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;

@Configuration
@EnableScheduling
public class ScheduleConfig implements SchedulingConfigurer, AsyncConfigurer {

    /*
     * 并行任务
     */
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        TaskScheduler taskScheduler = taskScheduler();
        taskRegistrar.setTaskScheduler(taskScheduler);
    }

    /**
     * 并行任务使用策略：多线程处理（配置线程数等）
     *
     * @return ThreadPoolTaskScheduler 线程池
     */
    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler taskScheduler() {
        //创建一个线程池调度器
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        //设置线程池容量
        scheduler.setPoolSize(9);
        //线程名前缀
        scheduler.setThreadNamePrefix("task-");
        //等待时常
        scheduler.setAwaitTerminationSeconds(60);
        //当调度器shutdown被调用时等待当前被调度的任务完成
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        return scheduler;
    }


    /*
     * 异步任务
     */
    public Executor getAsyncExecutor() {
        Executor executor = taskScheduler();
        return executor;
    }

    /*
     * 异步任务 异常处理
     */
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}