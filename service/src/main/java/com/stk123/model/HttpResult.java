package com.stk123.model;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
public class HttpResult<R> implements Serializable {
    private HttpStatus status;
    private R body;
}
