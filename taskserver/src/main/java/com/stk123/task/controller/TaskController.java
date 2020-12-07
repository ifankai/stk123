package com.stk123.task.controller;

import com.stk123.task.schedule.Task;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@RestController
@CommonsLog
public class TaskController {

    private final Map<String, ScheduledFuture<?>> scheduledTasks = new HashMap<>();

    @Autowired
    private TaskScheduler taskScheduler;

    @SneakyThrows
    @RequestMapping("/task/{name}/{args}")
    public void runTask(@PathVariable("name") String name, @PathVariable(value = "args", required = false) String args){
        Task task = (Task) Class.forName(name).newInstance();
        ScheduledFuture future = taskScheduler.schedule(
                () -> task.run(StringUtils.split(args == null ? "" : args, " ")),
                triggerContext -> new Date());
        scheduledTasks.put(name, future);
    }

    @SneakyThrows
    @RequestMapping("/task/stop/{name}")
    public void stopTask(@PathVariable("name") String name){
        ScheduledFuture future = scheduledTasks.get(name);
        future.cancel(true);
    }

}
