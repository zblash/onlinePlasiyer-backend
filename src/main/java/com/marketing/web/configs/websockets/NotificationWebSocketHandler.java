package com.marketing.web.configs.websockets;

import com.marketing.web.services.websocket.WebSocketNotificationSenderService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    WebSocketNotificationSenderService senderService;

    public NotificationWebSocketHandler(WebSocketNotificationSenderService senderService){
        this.senderService = senderService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        senderService.addToSession(session);
    }
}

