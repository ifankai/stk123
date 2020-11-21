package com.stk123.model.ws;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestMethod;

@Data
@AllArgsConstructor
public class ServerMessage<T> {

    private String type;

    private RequestMethod requestMethod;

    private String messageId;

    private T data;

    public ServerMessage() {
    }

}
