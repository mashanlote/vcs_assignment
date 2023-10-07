package com.mashanlote.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashanlote.WeatherApiErrorHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WeatherApiClientConfiguration {

    ObjectMapper mapper;

    public WeatherApiClientConfiguration(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Bean
    @Qualifier("weather")
    RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new WeatherApiErrorHandler(mapper));
        return restTemplate;
    }

}
