package com.stk123.task.aop;

import com.stk123.entity.StkTextEntity;
import com.stk123.service.core.EsService;
import lombok.extern.apachecommons.CommonsLog;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@CommonsLog
public class AopElasticsearch {

    @Autowired
    private EsService esService;

    /**
     * 切入点
     */
    @Pointcut("execution(public * com.stk123.repository.Stk*Repository.save(..))")
    public void createDocument() {

    }

    @AfterReturning(returning = "ret", pointcut = "createDocument()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容
        if(ret instanceof StkTextEntity){
            log.info("createDocumentIfNotExisting : " + ((StkTextEntity) ret).getCode());
            esService.createDocumentIfNotExisting((StkTextEntity) ret);
        }
    }
}
