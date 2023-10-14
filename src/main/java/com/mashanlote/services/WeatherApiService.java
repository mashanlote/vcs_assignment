package com.mashanlote.services;

import com.mashanlote.model.weatherapi.WeatherDTO;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherApiService {

    private final String URL;

    @Qualifier("weather")
    RestTemplate weatherApi;

    public WeatherApiService(
            RestTemplate weatherApi,
            @Value("${api-key}") String apiKey
    ) {
        this.weatherApi = weatherApi;
        URL = "/current.json?key=" + apiKey + "&q={city}";
    }

    @RateLimiter(name = "api")
    public WeatherDTO getCityWeather(String city) {
        return weatherApi.getForObject(URL, WeatherDTO.class, city);
    }

}