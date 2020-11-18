package com.stk123.model.ws;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientMessage {

    private String type;

    private String messageId;

    public ClientMessage() {
    }

}
