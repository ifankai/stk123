package com.stk123.service.task;

import com.stk123.util.ExceptionUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;

@CommonsLog
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class Task<R> {

    public enum EnumStatus {
        RUNNING, NOT_RUNNING
    }

    @Getter
    private String id;
    @Getter@Setter
    private boolean async; //if true, 就是异步

    private EnumStatus status = EnumStatus.NOT_RUNNING;
    @Getter
    private LocalDateTime startTime;
    @Getter
    private LocalDateTime endTime;

    @Getter
    private TaskResult<?> taskResult;

    public Task(){
        this(true);
    }

    public Task(boolean async){
        this.async = async;
        this.id = String.valueOf(System.currentTimeMillis());
    }

    public void run(String... args) {
        this.startTime = LocalDateTime.now();
        log.info("["+this.getClass().getSimpleName()+"]..start, id:"+this.id+", param:"+ StringUtils.join(args,","));
        try {
            status = EnumStatus.RUNNING;
            execute(args);
            taskResult = TaskResult.success(success(), this);
        } catch(Exception e){
            log.error("task execute error:", e);
            taskResult = TaskResult.failure(ExceptionUtils.getExceptionAsString(e), this);
        } finally {
            this.endTime = LocalDateTime.now();
            status = EnumStatus.NOT_RUNNING;
            log.info("["+this.getClass().getSimpleName()+"]....end, id:"+this.id+", cost time:"+ (getEndTimeToLong()-getStartTimeToLong())/1000.0 + "s");
        }
    }

    public EnumStatus status() {
        return status;
    }

    public abstract void execute(String... args) throws Exception;

    public R success(){
        return (R) "";
    }

    public long getStartTimeToLong(){
        return this.startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
    public long getEndTimeToLong(){
        return this.endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}
