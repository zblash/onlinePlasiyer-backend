package com.marketing.web.pubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static java.lang.String.format;

@Component
public class NotificationConsumer {
    @Autowired
    private SimpMessagingTemplate webSocket;

    private Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);

    public void onReceiveNotification(String object) throws IOException {

    }


}
