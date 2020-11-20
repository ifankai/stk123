package com.stk123.model.ws;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientMessage<T> {

    private String type;

    private String messageId;

    private T data;

    public ClientMessage() {
    }

}
