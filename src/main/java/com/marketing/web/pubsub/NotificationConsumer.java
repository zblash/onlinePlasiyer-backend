package com.marketing.web.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketing.web.dtos.websockets.WrapperWsNotification;
import com.marketing.web.services.websocket.WebSocketNotificationSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketMessage;

import java.io.IOException;

@Component
public class NotificationConsumer {

    @Autowired
    WebSocketNotificationSenderService webSocket;

    private Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);

    public void onReceiveNotification(String object) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        WrapperWsNotification wrapperWsNotification= objectMapper.readValue(object, WrapperWsNotification.class);
        logger.info("User where coming from redis " + wrapperWsNotification.getUser().getUsername());
        webSocket.convertAndSendToUser(wrapperWsNotification.getUser().getUsername(), wrapperWsNotification);
    }


}
