package com.marketing.web.configs.websockets;

import com.marketing.web.services.websocket.WebSocketProductSenderService;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class ProductsWebSocketHandler extends TextWebSocketHandler {

    WebSocketProductSenderService senderService;

    public ProductsWebSocketHandler(WebSocketProductSenderService senderService){
        this.senderService = senderService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        senderService.addToSession(session);
    }
}
