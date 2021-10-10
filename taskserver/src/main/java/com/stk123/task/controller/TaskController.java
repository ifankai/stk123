package com.stk123.task.controller;

import com.stk123.model.RequestResult;
import com.stk123.model.dto.TaskDto;
import com.stk123.service.support.SpringApplicationContext;
import com.stk123.service.task.Task;
import com.stk123.service.task.TaskContainer;
import com.stk123.task.Tasks;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskContainer taskContainer;
    @Autowired
    private Tasks tasks;

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
                taskDto.setCountOfRunning(0);
                taskDtos.add(taskDto);
            }
            if(task.status() == Task.EnumStatus.RUNNING) {
                //taskDto.setCountOfRunning(taskDto.getCountOfRunning()+1);
                if(taskDto.getStartTime() < task.getStartTimeToLong())
                    taskDto.setStartTime(task.getStartTimeToLong());
            }else{
                if(task.getTaskResult() != null && task.getTaskResult().getSuccess()){
                    if(taskDto.getEndTimeSucc() < task.getEndTimeToLong()) {
                        taskDto.setEndTimeSucc(task.getEndTimeToLong());
                        taskDto.setSuccMsg(task.getTaskResult().getData() == null ? null : task.getTaskResult().getData().toString());
                    }
                }else{
                    if(taskDto.getEndTimeFail() < task.getEndTimeToLong()) {
                        taskDto.setEndTimeFail(task.getEndTimeToLong());
                        taskDto.setFailMsg(task.getTaskResult().getData() == null ? null : task.getTaskResult().getData().toString());
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
    @RequestMapping(path={"/{methodName}"})
    public RequestResult tasks(@PathVariable("methodName") String methodName){
        Set<Method> methods = ReflectionUtils.getMethods(Tasks.class, method -> method.getName().equalsIgnoreCase(methodName));
        log.info("method invoke: {} {}", methodName, methods.size());
        if(!methods.isEmpty()){
            Method method = methods.iterator().next();
            method.invoke(tasks, null);
        }
        return RequestResult.success();
    }

    @SneakyThrows
    @RequestMapping(path={"/start/{name}", "/start/{name}/**"})
    public RequestResult runTask(HttpServletRequest request, @PathVariable("name") String taskName, @RequestParam Map<String,String> allRequestParams){
        String mvcPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        Task task = SpringApplicationContext.getBeanByClassName(taskName);
        String value = StringUtils.substringAfter(mvcPath, "/task/start/"+taskName);
        List<String> params = Arrays.stream(StringUtils.split(value, "/")).filter(e -> StringUtils.isNotEmpty(e)).collect(Collectors.toList());
        if(allRequestParams != null)
            params.addAll(allRequestParams.entrySet().stream().map(e -> e.getKey()+"="+e.getValue()).collect(Collectors.toList()));
        System.out.println(task.hashCode()+", url="+mvcPath+", params="+params);
        taskContainer.start(task, params.stream().toArray(String[]::new));
        return RequestResult.success(task.getId());
    }

    @SneakyThrows
    @RequestMapping("/stop/{id}")
    public RequestResult stopTask(@PathVariable("id") String id){
        taskContainer.stop(id);
        return RequestResult.success();
    }

}
