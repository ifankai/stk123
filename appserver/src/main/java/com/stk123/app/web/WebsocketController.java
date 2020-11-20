package com.stk123.app.web;

import com.stk123.app.model.RequestResult;
import com.stk123.common.CommonConstant;
import com.stk123.model.ws.ClientMessage;
import com.stk123.model.ws.ServerMessage;
import lombok.extern.apachecommons.CommonsLog;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Controller
@CommonsLog
public class WebsocketController {

    @Autowired
    private SimpMessagingTemplate template;

    static Map<String, CountDownLatch> latch = new HashMap<>();
    static Map<String, ClientMessage<RequestResult>> clientMessageMap = new HashMap<>();;

    @MessageMapping(CommonConstant.WS_MAPPING)
    @SendTo(CommonConstant.WS_TOPIC)
    public ServerMessage greeting(ClientMessage<RequestResult> clientMessage) throws Exception {
        String uuid = clientMessage.getMessageId();
        clientMessageMap.put(uuid, clientMessage);
        latch.get(uuid).countDown();
        return new ServerMessage(null, uuid, "init connection");
    }

    @RequestMapping("/ws/{type}")
    @ResponseBody
    public RequestResult hello(@PathVariable String type) throws InterruptedException {
        log.info("hello start:"+type);
        String uuid = UUID.randomUUID().toString();
        template.convertAndSend(CommonConstant.WS_TOPIC, new ServerMessage(type.replace("|","/"), uuid, "Hello, test" ));
        CountDownLatch cdl = new CountDownLatch(1);
        latch.put(uuid, cdl);
        cdl.await();
        log.info("hello end:"+uuid);
        return RequestResult.success(clientMessageMap.get(uuid).getData());
    }

}
