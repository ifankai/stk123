package com.stk123.task.controller;

import com.stk123.model.RequestResult;
import com.stk123.model.dto.TaskDto;
import com.stk123.service.support.SpringApplicationContext;
import com.stk123.service.task.Task;
import com.stk123.service.task.TaskContainer;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
    @RequestMapping(path={"/start/{name}/**","/start/{name}"})
    public RequestResult runTask(HttpServletRequest request, @PathVariable("name") String name){
        String mvcPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        Task task = SpringApplicationContext.getBeanByForName(name);
        System.out.println(task.hashCode()+",url="+mvcPath);
        String params = StringUtils.substringAfter(mvcPath, "/task/start/"+name);
        taskContainer.start(task, Arrays.stream(StringUtils.split(params, "/")).filter(e -> StringUtils.isNotEmpty(e)).toArray(String[]::new));
        return RequestResult.success(task.getId());
    }

    @SneakyThrows
    @RequestMapping("/stop/{id}")
    public RequestResult stopTask(@PathVariable("id") String id){
        taskContainer.stop(id);
        return RequestResult.success();
    }

}
