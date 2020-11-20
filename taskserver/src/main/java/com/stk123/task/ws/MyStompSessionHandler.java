package com.stk123.task.ws;

import com.stk123.common.CommonConstant;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;

@CommonsLog
@Component
public class MyStompSessionHandler extends StompSessionHandlerAdapter {

    @Autowired
    private StkStompFrameHandler stkStompFrameHandler;

    public MyStompSessionHandler() {
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        log.info("Websocket Connected!");
        subscribeTopic(CommonConstant.WS_TOPIC, session);
    }

    public void handleException(StompSession session, @Nullable StompCommand command,
                                StompHeaders headers, byte[] payload, Throwable exception) {
        exception.printStackTrace();
    }

    private void subscribeTopic(String topic, StompSession session) {
        session.subscribe(topic, stkStompFrameHandler);
    }



}
