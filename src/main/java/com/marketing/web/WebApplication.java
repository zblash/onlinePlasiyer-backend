package com.marketing.web;

import com.marketing.web.pubsub.NotificationConsumer;
import com.marketing.web.pubsub.ProductConsumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.validation.Validator;
import java.util.Arrays;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class WebApplication {

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
        methodValidationPostProcessor.setValidator(validator());
        return methodValidationPostProcessor;
    }

	@Bean
	RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
											@Qualifier("productListenerAdapter") MessageListenerAdapter productListenerAdapter,
											@Qualifier("notificationListenerAdapter") MessageListenerAdapter notificationListenerAdapter) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(productListenerAdapter, new PatternTopic("products"));
		container.addMessageListener(notificationListenerAdapter,  new PatternTopic("notification"));

		return container;
	}

	@Bean("productListenerAdapter")
	MessageListenerAdapter productListenerAdapter(ProductConsumer consumer) {
		return new MessageListenerAdapter(consumer, "onReceiveProduct");
	}

	@Bean("notificationListenerAdapter")
	MessageListenerAdapter notificationListenerAdapter(NotificationConsumer consumer) {
		return new MessageListenerAdapter(consumer, "onReceiveNotification");
	}

	@Bean
	RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		return redisTemplate;
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addExposedHeader(HttpHeaders.AUTHORIZATION);
		config.setAllowedMethods(Arrays.asList("POST", "PUT", "PATCH", "DELETE", "GET", "OPTIONS"));
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(WebApplication.class, args);
	}
}
