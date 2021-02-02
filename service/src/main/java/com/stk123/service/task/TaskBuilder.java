package com.stk123.service.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TaskBuilder {

    private Class task;
    private String[] args;

    public static TaskBuilder of(Class cls, String... args){
        return new TaskBuilder(cls, args);
    }
}
