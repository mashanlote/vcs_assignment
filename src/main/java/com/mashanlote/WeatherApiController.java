package com.mashanlote;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather")
public class WeatherApiController {

    WeatherApiService weatherService;

    public WeatherApiController(WeatherApiService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/{city}")
    public ResponseEntity<?> getCityWeather(@PathVariable String city) {
        var weather = weatherService.getCityWeather(city);
        return ResponseEntity.ok(weather);
    }

}
