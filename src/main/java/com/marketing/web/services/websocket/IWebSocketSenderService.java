package com.marketing.web.services.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.socket.WebSocketSession;

public interface IWebSocketSenderService<T> {

    void addToSession(WebSocketSession session);

    void convertAndSend(T payload) throws JsonProcessingException;

    void convertAndSendToUser(String user, T payload) throws JsonProcessingException;
}
