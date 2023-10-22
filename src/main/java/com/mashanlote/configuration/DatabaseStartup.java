package com.mashanlote.configuration;

import com.mashanlote.services.WeatherApiService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseStartup implements ApplicationRunner {

    private final WeatherApiService service;

    public DatabaseStartup(WeatherApiService service) {
        this.service = service;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var cities = List.of("Tomsk", "Moscow", "London", "Paris", "Beijing");
        for (var city : cities) {
            var response = service.fetchWeatherAndStoreInDb(city);
            System.out.println(response);
        }
    }
}
