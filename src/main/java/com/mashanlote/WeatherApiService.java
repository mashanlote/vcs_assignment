package com.mashanlote;

import com.mashanlote.model.WeatherPOJO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URL;

@Service
public class WeatherApiService {

    final private String URL = "https://api.weatherapi.com/v1/current.json" +
            "?key=" + System.getenv("API_KEY") +
            "&q=";

    @Qualifier("weather")
    RestTemplate weatherApi;

    public WeatherApiService(RestTemplate weatherApi) {
        this.weatherApi = weatherApi;
    }

    public WeatherPOJO getCityWeather(String city) {
        WeatherPOJO weather = weatherApi
                .getForObject(URL + city, WeatherPOJO.class);
        return weather;
    }
}
