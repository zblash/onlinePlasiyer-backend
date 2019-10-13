package com.marketing.web.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketing.web.dtos.product.ReadableProductSpecify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static java.lang.String.format;

@Component
public class ProductConsumer {

    @Autowired
    private SimpMessagingTemplate webSocket;

    private Logger logger = LoggerFactory.getLogger(ProductConsumer.class);

    public void onReceiveProduct(String object) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ReadableProductSpecify readableProductSpecify= objectMapper.readValue(object,ReadableProductSpecify.class);
        readableProductSpecify.getStates().stream().forEach(state -> {
            logger.info("Sended to clients "+ state);
            webSocket.convertAndSend(format("/channel/%s", state), readableProductSpecify);
        });
    }


}
