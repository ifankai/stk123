package com.stk123.model.ws;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServerMessage<T> {

    private String type;

    private String messageId;

    private T data;

    public ServerMessage() {
    }

}
