package com.marketing.web;

import com.marketing.web.models.City;
import com.marketing.web.models.State;
import com.marketing.web.pubsub.ProductConsumer;
import com.marketing.web.repositories.CityRepository;
import com.marketing.web.repositories.StateRepository;
import com.marketing.web.services.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@Configuration
public class WebApplication implements CommandLineRunner {

	@Autowired
	StorageService storageService;

	@Bean
	RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
											MessageListenerAdapter listenerAdapter) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(listenerAdapter, new PatternTopic("products"));

		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter(ProductConsumer consumer) {
		return new MessageListenerAdapter(consumer, "onReceiveProduct");
	}

	@Bean
	StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
		return new StringRedisTemplate(connectionFactory);
	}

	@Bean
	RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
		RedisTemplate redisTemplate = new RedisTemplate();
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


	@Autowired
	private StateRepository stateRepository;

	@Autowired
	private CityRepository cityRepository;

	@Override
	public void run(String... args) throws Exception {
		storageService.init();

		if (cityRepository.findAll().isEmpty()) {
			List<City> cities = new ArrayList<>();
			City city = new City();
			city.setCode(6);
			city.setTitle("ANKARA");
			cities.add(city);
			City city1 = new City();
			city1.setCode(7);
			city1.setTitle("ANTALYA");
			cities.add(city1);
			City city2 = new City();
			city2.setCode(34);
			city2.setTitle("ISTANBUL");
			cities.add(city2);
			cityRepository.saveAll(cities);

			List<State> states = new ArrayList<>();
			State state = new State();
			state.setCity(cityRepository.findByTitle("ANTALYA").orElse(null));
			state.setCode(700);
			state.setTitle("MANAVGAT");
			states.add(state);
			State state1 = new State();
			state1.setCity(cityRepository.findByTitle("ANTALYA").orElse(null));
			state1.setCode(750);
			state1.setTitle("ALANYA");
			states.add(state1);
			State state2 = new State();
			state2.setCity(cityRepository.findByTitle("ANKARA").orElse(null));
			state2.setCode(600);
			state2.setTitle("MAMAK");
			states.add(state2);
			State state3 = new State();
			state3.setCity(cityRepository.findByTitle("ANKARA").orElse(null));
			state3.setCode(600);
			state3.setTitle("CANKAYA");
			states.add(state3);
			State state4 = new State();
			state4.setCity(cityRepository.findByTitle("ISTANBUL").orElse(null));
			state4.setCode(340);
			state4.setTitle("BEYKOZ");
			states.add(state4);
			stateRepository.saveAll(states);
		}
	}
}
