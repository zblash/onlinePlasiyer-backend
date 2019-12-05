package com.marketing.web.configs.websockets;

import com.marketing.web.services.websocket.WebSocketProductSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Objects;

public class ProductsWebSocketHandler extends TextWebSocketHandler {

    WebSocketProductSenderService senderService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public ProductsWebSocketHandler(WebSocketProductSenderService senderService){
        this.senderService = senderService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        senderService.addToSession(session);
        logger.info(Objects.requireNonNull(session.getUri()).toString());
        logger.info(session.getAttributes().toString());

    }
}
