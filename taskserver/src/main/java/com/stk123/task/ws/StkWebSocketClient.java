package com.stk123.task.ws;

import com.stk123.common.CommonConstant;
import com.stk123.model.ws.ClientMessage;
import com.stk123.model.ws.ServerMessage;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

public class StkWebSocketClient {

    public static final String SEND_URL = CommonConstant.WS_PREFIX + CommonConstant.WS_MAPPING;

    private static StompSession session;

    private StkWebSocketClient() {}

    public static void init() throws Exception {
        WebSocketClient simpleWebSocketClient = new StandardWebSocketClient();
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(simpleWebSocketClient));

        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        String url = "ws://localhost:8080" + CommonConstant.WS_ENDPOINT;

        StompSessionHandler sessionHandler = new MyStompSessionHandler();
        session = stompClient.connect(url, sessionHandler).get();
    }

    public static StompSession.Receiptable send(ClientMessage clientMessage){
        return session.send(SEND_URL, clientMessage);
    }

    public static void main(String[] args) throws Exception {
        StkWebSocketClient.init();

        //发送消息
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        for (; ; ) {
            System.out.print("Client" + " >> ");
            System.out.flush();
            String line = in.readLine();
            if (line == null) {
                break;
            }
            if (line.length() == 0) {
                continue;
            }
            ClientMessage msg = new ClientMessage("", "From client : I have a new name [" + line + "]");
            StkWebSocketClient.send(msg);
        }
        //session.disconnect();
    }

}
