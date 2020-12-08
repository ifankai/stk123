package com.stk123.task.schedule.core;

import lombok.Getter;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Service
@CommonsLog
public class TaskContainer {

    @Getter
    private List<Task> tasks = new ArrayList<>();

    private final Map<String, ScheduledFuture<?>> scheduledTasks = new HashMap<>();

    @Autowired
    private TaskScheduler taskScheduler;

    public void start(Task task, String... args) {
        try {
            tasks.add(task);
            if(task.isCanStop()) {
                ScheduledFuture future = taskScheduler.schedule(
                        () -> task.run(args),
                        triggerContext -> new Date());
                scheduledTasks.put(task.getId(), future);
            }else{
                task.run(args);
            }
        }finally{
            clean(task);
        }
    }

    public boolean stop(String id) {
        ScheduledFuture future = scheduledTasks.get(id);
        if(future == null){
            log.info("Task cannot stop.");
            return false;
        }
        Task task = this.getTaskById(id);
        task.stop("Stop manually.");
        return future.cancel(true);
    }

    private void clean(Task task) {
        List<Task> tasks = this.getTask(task.getClass());
        boolean success = task.getTaskResult().getSuccess();
        for(Task t : tasks) {
            if(t.getTaskResult() != null && t.getTaskResult().getSuccess() == success){
                this.tasks.remove(t);
            }
        }
        scheduledTasks.remove(task.getId());
    }

    public Task getTaskById(String id){
        return null;
    }

    public List<Task> getTask(Class cls) {
        List<Task> results = new ArrayList<>();
        for(Task task : tasks) {
            if(cls.isInstance(task)){
                results.add(task);
            }
        }
        return results;
    }
}
