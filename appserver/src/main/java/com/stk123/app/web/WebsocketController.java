package com.stk123.app.web;

import com.stk123.app.model.RequestResult;
import com.stk123.common.CommonConstant;
import com.stk123.model.ws.ClientMessage;
import com.stk123.model.ws.ServerMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Controller
public class WebsocketController {

    @Autowired
    private SimpMessagingTemplate template;

    static Map<String, CountDownLatch> latch;
    static Map<String, ClientMessage> clientMessage;

    @MessageMapping(CommonConstant.WS_MAPPING)
    @SendTo(CommonConstant.WS_TOPIC)
    public ServerMessage greeting(ClientMessage clientMessage) throws Exception {
        // 模拟延时，以便测试客户端是否在异步工作
        Thread.sleep(1000);
        String uuid = clientMessage.getMessageId();
        return new ServerMessage(null, uuid, "init connection, " + HtmlUtils.htmlEscape(clientMessage.getType()) + "!");
    }

    @RequestMapping("/ws/hello")
    @ResponseBody
    public RequestResult hello() throws InterruptedException {
        String uuid = UUID.randomUUID().toString();
        template.convertAndSend(CommonConstant.WS_TOPIC, new ServerMessage("hello", uuid, "Hello, test" ));
        CountDownLatch cdl = new CountDownLatch(1);
        latch.put(uuid, cdl);
        cdl.await();
        return RequestResult.success(clientMessage.get(uuid));
    }

}
