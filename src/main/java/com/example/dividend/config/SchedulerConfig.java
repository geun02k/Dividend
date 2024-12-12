package com.example.dividend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 스레드풀 생성
        ThreadPoolTaskScheduler threadPool = new ThreadPoolTaskScheduler();

        // 스레드풀 사이즈 설정
        int n = Runtime.getRuntime().availableProcessors(); // cpu의 core수
        threadPool.setPoolSize(n);
        threadPool.initialize();

        // 스케줄러에서 해당 스레드 풀 사용
        taskRegistrar.setTaskScheduler(threadPool);
    }
}
