package com.stk123.task.ws;

import com.stk123.common.CommonConstant;
import com.stk123.model.ws.ClientMessage;
import com.stk123.model.ws.ServerMessage;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class MyStompSessionHandler extends StompSessionHandlerAdapter {

    public MyStompSessionHandler() {
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        System.err.println("Connected! Headers:");
        showHeaders(connectedHeaders);

        subscribeTopic(CommonConstant.WS_TOPIC, session);
    }

    private void showHeaders(StompHeaders headers) {
        for (Map.Entry<String, List<String>> e : headers.entrySet()) {
            System.err.print("  " + e.getKey() + ": ");
            boolean first = true;
            for (String v : e.getValue()) {
                if (!first) {
                    System.err.print(", ");
                }
                System.err.print(v);
                first = false;
            }
            System.err.println();
        }
    }


    private void subscribeTopic(String topic, StompSession session) {
        session.subscribe(topic, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ServerMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.err.println(payload.getClass());
                System.err.println(payload.toString());
            }
        });
    }


}
