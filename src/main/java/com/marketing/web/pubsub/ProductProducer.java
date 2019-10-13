package com.marketing.web.pubsub;

import com.marketing.web.dtos.product.ReadableProductSpecify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductProducer {

    @Autowired
    private RedisTemplate< String, Object > template;

    private Logger logger = LoggerFactory.getLogger(ProductProducer.class);

    public void sendProductSpecify(ReadableProductSpecify readableProductSpecify){
        logger.info("ProductSpecify produced");
        template.convertAndSend("products", readableProductSpecify);
    }

}
