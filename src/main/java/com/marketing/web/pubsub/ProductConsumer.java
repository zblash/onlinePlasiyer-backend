package com.marketing.web.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketing.web.dtos.websockets.WrapperWsProductSpecify;
import com.marketing.web.services.websocket.WebSocketProductSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class ProductConsumer {

    @Autowired
    private WebSocketProductSenderService webSocket;

    public void onReceiveProduct(String object) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        WrapperWsProductSpecify wrapperWsProductSpecify = objectMapper.readValue(object, WrapperWsProductSpecify.class);
        webSocket.convertAndSend(wrapperWsProductSpecify);
    }
}
