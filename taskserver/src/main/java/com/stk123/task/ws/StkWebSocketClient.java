package com.stk123.task.ws;

import com.stk123.common.CommonConstant;
import com.stk123.model.ws.ClientMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;

@Component
public class StkWebSocketClient {

    public static final String SEND_URL = CommonConstant.WS_PREFIX + CommonConstant.WS_MAPPING;

    private StompSession session;

    @Autowired
    private WebSocketSessionHandler myStompSessionHandler;

    @Value("${stk.appserver.ip}")
    private String ip;
    @Value("${stk.appserver.port}")
    private String port;

    public StkWebSocketClient() {}

    public void init() throws Exception {
        WebSocketClient simpleWebSocketClient = new StandardWebSocketClient();
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(simpleWebSocketClient));

        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        String url = "ws://"+ip+":"+port +CommonConstant.WS_ENDPOINT;

        session = stompClient.connect(url, myStompSessionHandler).get();
    }

    public StompSession.Receiptable send(ClientMessage clientMessage){
        return session.send(SEND_URL, clientMessage);
    }

    public boolean isConnected() {
        if(session != null){
            return session.isConnected();
        }
        return false;
    }

}
