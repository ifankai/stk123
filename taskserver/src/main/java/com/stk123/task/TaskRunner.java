package com.stk123.task;

import cn.hutool.core.bean.BeanUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TaskRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("TaskRunner......");
    }

}
