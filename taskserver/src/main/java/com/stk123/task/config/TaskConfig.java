package com.stk123.task.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
@Conditional(TaskCondition.class)
public class TaskConfig implements ApplicationContextAware {

    private ApplicationContext context;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private ThreadPoolTaskScheduler scheduler;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.context = ctx;
    }


    @Scheduled(cron = "0 0 0 ? * *")  //每天0点shutdown
    public void exit(){
        scheduler.shutdown();
        SpringApplication.exit(this.context, () -> 0);
    }
}
