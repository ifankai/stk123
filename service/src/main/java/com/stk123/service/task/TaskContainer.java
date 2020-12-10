package com.stk123.service.task;

import com.stk123.service.task.Task;
import lombok.Getter;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

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
                        () -> task.run(args), new Date());
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
        return future.cancel(true);
    }

    private void clean(Task task) {
        List<Task> tasks = this.getTask(task.getClass());
        if(tasks.size() > 1){
            List<Task> tasksSucc = tasks.stream().filter(task1 -> task1.getTaskResult()!=null && task1.getTaskResult().getSuccess()).collect(Collectors.toList());
            tasksSucc.sort(Comparator.comparingLong(Task::getEndTimeToLong));
            for (int i=0; i<tasksSucc.size()-1; i++) {
                final String id = tasksSucc.get(i).getId();
                this.tasks.removeIf(task1 -> task1.getId().equals(id));
            }
            List<Task> tasksFail = tasks.stream().filter(task1 -> task1.getTaskResult()!=null && !task1.getTaskResult().getSuccess()).collect(Collectors.toList());
            tasksSucc.sort(Comparator.comparingLong(Task::getEndTimeToLong));
            for (int i=0; i<tasksFail.size()-1; i++) {
                final String id = tasksFail.get(i).getId();
                this.tasks.removeIf(task1 -> task1.getId().equals(id));
            }
        }
        if(!task.isCanStop()) {
            scheduledTasks.remove(task.getId());
        }
    }

    public Task getTaskById(String id){
        return tasks.stream().filter(task -> task.getId().equals(id)).findFirst().get();
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
