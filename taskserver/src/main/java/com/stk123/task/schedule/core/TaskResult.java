package com.stk123.task.schedule.core;

import com.stk123.model.RequestResult;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskResult<T> extends RequestResult<T> {

    private Task task;

    public TaskResult(boolean success, T data){
        super(success, data);
    }

    public static TaskResult<String> success() {
        return new TaskResult(true, "Task run Successfully.");
    }

    public static TaskResult<Exception> failure(String e) {
        return new TaskResult(false, e);
    }

}
