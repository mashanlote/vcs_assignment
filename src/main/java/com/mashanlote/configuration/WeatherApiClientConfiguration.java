package com.mashanlote.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
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
        return new RestTemplateBuilder()
                .rootUri("https://api.weatherapi.com/v1/")
                .errorHandler(new WeatherApiErrorHandler(mapper))
                .build();
    }

}
