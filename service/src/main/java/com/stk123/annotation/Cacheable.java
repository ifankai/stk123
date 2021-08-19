package com.stk123.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cacheable {

    /**
     * key的获取方式
     * 0：通过spel表达式从入参中获取
     * 1：fieldKey的值作为key
     */
    String keyType() default "0";
    /**
     * key，spel表达式，eg：#id（获取形参中id的值）
     */
    String fieldKey();

    /**
     * key过期时间
     */
    int expireTime() default 24 * 60 * 60;


}
