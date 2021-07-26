package com.stk123.task.config;

import com.stk123.common.CommonUtils;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

@CommonsLog
public class TaskCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return CommonUtils.isDevelopment();
    }

}
