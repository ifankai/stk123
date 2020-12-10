package com.stk123.service.task;

import com.stk123.model.RequestResult;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskResult<T> extends RequestResult<T> {

    private Task task;

    public TaskResult(boolean success, T data, Task task){
        super(success, data);
        this.task = task;
    }

    public static TaskResult success(Task task) {
        return new TaskResult(true, null, task);
    }

    public static <R> TaskResult success(R data, Task task) {
        return new TaskResult(true, data, task);
    }

    public static TaskResult<String> failure(String e, Task task) {
        return new TaskResult(false, e, task);
    }

}
