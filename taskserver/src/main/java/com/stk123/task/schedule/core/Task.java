package com.stk123.task.schedule.core;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;

@CommonsLog
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class Task {

    public enum EnumStatus {
        RUNNING, NOT_RUNNING
    }

    @Getter
    private String id;
    @Getter
    private boolean canStop;

    private EnumStatus status = EnumStatus.NOT_RUNNING;
    @Getter
    private LocalDateTime startTime;
    @Getter
    private LocalDateTime endTime;

    @Getter
    private TaskResult taskResult;

    public Task(){
        this(true);
    }

    public Task(boolean canStop){
        this.canStop = canStop;
        this.id = String.valueOf(System.currentTimeMillis());
    }

    public void run(String... args) {
        this.startTime = LocalDateTime.now();
        log.info("task............start, id:"+this.id);
        try {
            status = EnumStatus.RUNNING;
            execute(args);
            taskResult = TaskResult.success();
        } catch(Exception e){
            log.error(e);
            taskResult = TaskResult.failure(e.getMessage());
        } finally {
            this.endTime = LocalDateTime.now();
            status = EnumStatus.NOT_RUNNING;
            log.info("task............end");
        }
    }

    public EnumStatus status() {
        return status;
    }

    public abstract void execute(String... args) throws Exception;

    public long getStartTimeToLong(){
        return this.startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
    public long getEndTimeToLong(){
        return this.startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}
