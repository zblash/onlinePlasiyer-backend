package com.marketing.web.pubsub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Component
public class ProductConsumer {

    @Autowired
    private ProductSubscriber productSubscriber;

    public void onReceiveProduct(String productId){
        productSubscriber.forEach( sseEmitter -> sendProduct(sseEmitter,productId));
    }

    private void sendProduct(SseEmitter sseEmitter, String productId ){
        try{
            sseEmitter.send( productId );
        }catch( IOException ex ){
            throw new RuntimeException( ex );
        }
    }

}
