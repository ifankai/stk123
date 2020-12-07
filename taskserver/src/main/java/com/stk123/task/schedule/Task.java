package com.stk123.task.schedule;

import org.springframework.stereotype.Service;

@Service
public interface Task {

    void run(String... args);

}
