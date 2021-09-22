package com.stk123.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonView(View.Default.class)
public class RequestResult<T> implements Serializable {

    private static final long serialVersionUID = -5478950010968748377L;

    public static RequestResult SUCCESS = new RequestResult(true);
    public static RequestResult FAIL = new RequestResult(false);

    private Boolean success;
    private Integer code; //error code, update number, counter
    private T data;

    public static <T> RequestResult<T> success() {
        return new RequestResult(true, null);
    }
    public static <T> RequestResult<T> success(T data) {
        return new RequestResult(true, data);
    }

    public static <T> RequestResult<T> failure(T data) {
        return new RequestResult(false, data);
    }

    public static <T> RequestResult<T> failure() {
        return new RequestResult(false, null);
    }

    public RequestResult(){}

    public RequestResult(boolean success){
        this.success = success;
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
