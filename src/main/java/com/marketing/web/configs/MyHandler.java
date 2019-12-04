package com.marketing.web.configs;

import com.marketing.web.dtos.websockets.WrapperWsNotification;
import com.marketing.web.services.websocket.WebSocketNotificationSenderService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class MyHandler extends TextWebSocketHandler {

    WebSocketNotificationSenderService senderService;

    public MyHandler(WebSocketNotificationSenderService senderService){
        this.senderService = senderService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        senderService.addToSession(session);
    }
}

