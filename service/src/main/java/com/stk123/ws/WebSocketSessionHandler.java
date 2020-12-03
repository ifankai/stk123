package com.stk123.ws;

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
public class WebSocketSessionHandler extends StompSessionHandlerAdapter {

    @Autowired
    private WebSocketServiceHandler stkStompFrameHandler;

    @Autowired
    private StkWebSocketClient stkWebSocketClient;

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        log.info("Websocket Connected!");
        session.subscribe(CommonConstant.WS_TOPIC, stkStompFrameHandler);
    }

    @Override
    public void handleException(StompSession session, @Nullable StompCommand command,
                                StompHeaders headers, byte[] payload, Throwable exception) {
        log.error(headers);
        log.error(new String(payload));
        log.error("MyStompSessionHandler.handleException", exception);
        ClientMessage cm = new ClientMessage();
        cm.setMessageId(headers.getFirst("messageId"));
        cm.setData(exception.getMessage());
        if(!session.isConnected()) {
            try {
                stkWebSocketClient.init();
            } catch (Exception e) {
                log.error("stkWebSocketClient init error", e);
                return ;
            }
        }
        session.send(StkWebSocketClient.SEND_URL, cm);
    }


}
