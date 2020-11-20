package com.stk123.task.ws;

import com.stk123.app.model.RequestResult;
import com.stk123.model.ws.ClientMessage;
import com.stk123.model.ws.ServerMessage;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;

@CommonsLog
@Component
public class StkStompFrameHandler implements StompFrameHandler {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    StkWebSocketClient stkWebSocketClient;

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return ServerMessage.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
//        System.err.println(payload.getClass()); //com.stk123.model.ws.ServerMessage
//        System.err.println("subscribeTopic:"+payload.toString());
        ServerMessage sm = (ServerMessage)payload;
        if(sm.getType() != null) {
//                    RequestMappingHandlerMapping rmhm = SpringUtils.getApplicationContext().getBean(RequestMappingHandlerMapping.class);
//                    for(RequestMappingInfo bean : rmhm.getHandlerMethods().keySet()){
//                        Log.info("bean:"+bean);
//                    }


            String url = "http://localhost:8081/"+sm.getType();
            RequestResult requestResult = restTemplate.getForObject(url, RequestResult.class);
            log.info("restTemplate url:" + url + ", result:" + requestResult);

            ClientMessage cm = new ClientMessage();
            cm.setType(sm.getType());
            cm.setMessageId(sm.getMessageId());
            cm.setData(requestResult.getData());
            stkWebSocketClient.send(cm);

        }
    }
}
