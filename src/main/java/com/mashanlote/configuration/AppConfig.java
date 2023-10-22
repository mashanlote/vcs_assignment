package com.mashanlote.configuration;

import com.mashanlote.services.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AppConfig {

    @Autowired
    private ApplicationContext context;

    @Bean
    public WeatherService getService(@Value("${database.access.api}") String qualifier) {
        log.info("Using " + qualifier + " weatherService");
        return (WeatherService) context.getBean(qualifier);
    }
}
