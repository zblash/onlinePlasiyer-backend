package com.marketing.web.pubsub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductProducer {

    @Autowired
    private StringRedisTemplate template;

    public void sendProduct(Long id){
        String productId = id.toString();
        template.convertAndSend("products",productId);
    }

}
