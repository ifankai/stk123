package com.stk123.task.controller;

import com.stk123.model.RequestResult;
import com.stk123.model.dto.TaskDto;
import com.stk123.task.schedule.core.Task;
import com.stk123.task.schedule.core.TaskContainer;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

@RestController
@CommonsLog
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskContainer taskContainer;

    @RequestMapping(path={"","/"})
    public RequestResult task() {
        List<Task> tasks = taskContainer.getTasks();
        List<TaskDto> taskDtos = new ArrayList<>();
        for(Task task : tasks) {
            String cls = task.getClass().getName();
            TaskDto taskDto = this.getTaskDto(taskDtos, cls);
            if(taskDto != null){
                if(task.status() == Task.EnumStatus.RUNNING) {
                    taskDto.setCountOfRunning(taskDto.getCountOfRunning() + 1);
                }
            }else{
                taskDto = new TaskDto();
                taskDto.setName(cls);
                taskDto.setCountOfRunning(1);
                taskDtos.add(taskDto);
            }
            if(task.status() == Task.EnumStatus.RUNNING) {
                if(taskDto.getStartTime() < task.getStartTimeToLong())
                    taskDto.setStartTime(task.getStartTimeToLong());
            }else{
                if(task.getTaskResult() != null && task.getTaskResult().getSuccess()){
                    if(taskDto.getEndTimeSucc() < task.getEndTimeToLong())
                        taskDto.setEndTimeSucc(task.getEndTimeToLong());
                }else{
                    if(taskDto.getEndTimeFail() < task.getEndTimeToLong()) {
                        taskDto.setEndTimeFail(task.getEndTimeToLong());
                        taskDto.setFailMsg(String.valueOf(task.getTaskResult().getData()));
                    }
                }
            }
        }
        return RequestResult.success(taskDtos);
    }

    private TaskDto getTaskDto(List<TaskDto> taskDtos, String cls) {
        for(TaskDto taskDto : taskDtos) {
            if(taskDto.getName().equals(cls)){
                return taskDto;
            }
        }
        return null;
    }

    @SneakyThrows
    @RequestMapping(path={"/start/{name}/{args}","/start/{name}"})
    public RequestResult runTask(@PathVariable("name") String name, @PathVariable(value = "args",
            required = false) String args){
        Task task = (Task) Class.forName(name).newInstance();
        taskContainer.start(task, StringUtils.split(args == null ? "" : args, " "));
        return RequestResult.success();
    }

    @SneakyThrows
    @RequestMapping("/stop/{id}")
    public RequestResult stopTask(@PathVariable("id") String id){
        taskContainer.stop(id);
        return RequestResult.success();
    }

}
