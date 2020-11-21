package com.stk123.task.ws;

import com.stk123.common.CommonConstant;
import com.stk123.model.ws.ClientMessage;
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

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        log.info("Websocket Connected!");
        subscribeTopic(CommonConstant.WS_TOPIC, session);
    }

    public void handleException(StompSession session, @Nullable StompCommand command,
                                StompHeaders headers, byte[] payload, Throwable exception) {
        log.info(headers);
        log.info(new String(payload));
        log.error("MyStompSessionHandler.handleException", exception);
        ClientMessage cm = new ClientMessage();
        cm.setMessageId(headers.getFirst("messageId"));
        cm.setData(exception.getMessage());
        log.info(cm);
        session.send(StkWebSocketClient.SEND_URL, cm);
    }

    private void subscribeTopic(String topic, StompSession session) {
        session.subscribe(topic, stkStompFrameHandler);
    }



}
