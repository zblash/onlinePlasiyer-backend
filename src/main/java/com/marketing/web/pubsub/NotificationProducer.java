package com.marketing.web.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketing.web.dtos.websockets.WrapperWsNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationProducer {

    @Autowired
    private RedisTemplate<String, String > template;

    private Logger logger = LoggerFactory.getLogger(ProductProducer.class);

    public void sendNotification(WrapperWsNotification wrapperWsNotification) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(wrapperWsNotification);
        logger.info("Notification sended to redis");
        template.convertAndSend("notification", jsonInString);
    }
}
