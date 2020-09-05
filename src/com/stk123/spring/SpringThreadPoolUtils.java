package com.stk123.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class SpringThreadPoolUtils {

    private static ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    public SpringThreadPoolUtils(ThreadPoolTaskExecutor taskExecutor){
        SpringThreadPoolUtils.taskExecutor = taskExecutor;
    }

    public static ThreadPoolTaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

}
