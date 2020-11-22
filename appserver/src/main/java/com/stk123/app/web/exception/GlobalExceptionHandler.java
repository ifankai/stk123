package com.stk123.app.web.exception;

import com.stk123.model.app.RequestResult;
import com.stk123.util.ExceptionUtils;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@CommonsLog
@RestControllerAdvice
public class GlobalExceptionHandler {

    //其他未处理的异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public RequestResult exceptionHandler(Exception e){
        log.error("全局异常信息:", e);
        return new RequestResult(false, e.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public RequestResult nullPointerExceptionHandler(Exception e){
        log.error("全局异常信息:", e);
        return new RequestResult(false, ExceptionUtils.getExceptionAsString(e));
    }

}
