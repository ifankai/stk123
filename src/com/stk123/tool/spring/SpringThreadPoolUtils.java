package com.stk123.tool.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class SpringThreadPoolUtils {

    @Autowired
    ThreadPoolTaskExecutor taskExecutor;

    public ThreadPoolTaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    public void setTaskExecutor(ThreadPoolTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
}
