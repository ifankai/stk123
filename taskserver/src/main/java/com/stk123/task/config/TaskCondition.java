package com.stk123.task.config;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

@CommonsLog
public class TaskCondition implements Condition {

    public final static String taskNeedToRun = "stk.task";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String task = context.getEnvironment().getProperty(taskNeedToRun);
        return task == null;
    }

}
