package com.marketing.web.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketing.web.dtos.websockets.WrapperWsNotification;
import com.marketing.web.services.websocket.WebSocketNotificationSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NotificationConsumer {

    @Autowired
    WebSocketNotificationSenderService webSocket;

    public void onReceiveNotification(String object) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        WrapperWsNotification wrapperWsNotification = objectMapper.readValue(object, WrapperWsNotification.class);
        webSocket.convertAndSendToUser(wrapperWsNotification.getUser().getUsername(), wrapperWsNotification);
    }
}
