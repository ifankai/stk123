package com.stk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestResult<T> implements Serializable {

    private static final long serialVersionUID = -5478950010968748377L;

    public static RequestResult SUCCESS = new RequestResult(true);
    public static RequestResult FAIL = new RequestResult(false);

    private Boolean success;
    private String msg;
    private Integer code; //error code, update number, counter
    private T data;


    public static <T> RequestResult<T> success(T data) {
        return new RequestResult(true, data);
    }


    public RequestResult(boolean success){
        this.success = success;
    }

    public RequestResult(boolean success, String msg){
        this.success = success;
        this.msg = msg;
    }

    public RequestResult(boolean success, T data){
        this.success = success;
        this.data = data;
    }

    public RequestResult(boolean success, int code){
        this.success = success;
        this.code = code;
    }


}
