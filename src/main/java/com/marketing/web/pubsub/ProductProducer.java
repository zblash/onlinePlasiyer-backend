package com.marketing.web.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.marketing.web.dtos.product.ReadableProductSpecify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductProducer {

    @Autowired
    private RedisTemplate<String, String > template;

    private Logger logger = LoggerFactory.getLogger(ProductProducer.class);

    public void sendProductSpecify(ReadableProductSpecify readableProductSpecify) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(readableProductSpecify);
        template.convertAndSend("products", jsonInString);
    }

}
