package com.stk123.app.web;

import com.stk123.model.RequestResult;
import com.stk123.common.CommonConstant;
import com.stk123.model.ws.ClientMessage;
import com.stk123.model.ws.ServerMessage;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setMessageId(uuid);
        serverMessage.setData("init connection");
        return serverMessage;
    }

    @RequestMapping("/ws/**")
    @ResponseBody
    public RequestResult wsGet(HttpServletRequest request
                              //,@RequestParam(required=false) Map<String,String> params
    ) throws Exception {
        String mvcPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        log.info(mvcPath);

        String type = StringUtils.substringAfter(mvcPath, "/ws/");
        String uuid = UUID.randomUUID().toString();
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setType(type);
        serverMessage.setMessageId(uuid);
        serverMessage.setRequestMethod(RequestMethod.GET);
        serverMessage.setData(request.getQueryString());
        Map<String, Object> headers = new HashMap<>();
        headers.put("messageId", uuid);
        template.convertAndSend(CommonConstant.WS_TOPIC, serverMessage, headers);
        CountDownLatch cdl = new CountDownLatch(1);
        latch.put(uuid, cdl);
        cdl.await(60, TimeUnit.SECONDS);
        latch.remove(uuid);
        return clientMessageMap.get(uuid).getData();
    }

}
