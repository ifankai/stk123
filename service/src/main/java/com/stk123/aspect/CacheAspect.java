package com.stk123.aspect;

import com.stk123.annotation.Cacheable;
import com.stk123.common.util.CacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class CacheAspect {

    @Pointcut(value = "@annotation(com.stk123.annotation.Cacheable)")
    public void cachePointcut(){}

    @Around(value="cachePointcut()")
    private Object cache(ProceedingJoinPoint point) throws Throwable {
        try{
            Method method = ((MethodSignature)point.getSignature()).getMethod();
            Cacheable cacheable = method.getAnnotation(Cacheable.class);

            Class<?> returnType = method.getReturnType();

            Object[] args = point.getArgs();

            String keyType = cacheable.keyType();

            String key = null;
            if("0".equals(keyType)){
                key = cacheable.fieldKey();
            }else{
                key = parseKey(cacheable.fieldKey(), method, args);
            }

            if(StringUtils.isEmpty(key)){
                return point.proceed();
            }

            Object obj = CacheUtils.get("", key);

            if(obj != null){
                return obj;
            }else {
                Object result = null;
                try {
                    result = point.proceed();
                    return result;
                }finally {
                    int expireTime = cacheable.expireTime();
                    CacheUtils.put("", key, result);
                }
            }

        }catch (Exception e){
            log.error("cache", e);
        }
        return point.proceed();
    }

    private String parseKey(String fieldKey, Method method, Object[] args){
        //获取被拦截方法参数名列表
        LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);

        //使用Spel表达式对key解析
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        for(int i=0; i<parameterNames.length; i++){
            context.setVariable(parameterNames[i], args[i]);
        }
        return spelExpressionParser.parseExpression(fieldKey).getValue(context, String.class);
    }
}
