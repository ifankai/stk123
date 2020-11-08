package com.stk123.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class ThreadPoolService {

    private static ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    public ThreadPoolService(ThreadPoolTaskExecutor taskExecutor){
        ThreadPoolService.taskExecutor = taskExecutor;
    }

    public static ThreadPoolTaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

}
