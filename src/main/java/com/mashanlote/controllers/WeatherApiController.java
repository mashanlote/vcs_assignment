package com.mashanlote.controllers;

import com.mashanlote.model.entities.WeatherObservation;
import com.mashanlote.services.WeatherApiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/weather")
public class WeatherApiController {

    WeatherApiService weatherService;

    public WeatherApiController(WeatherApiService weatherService) {
        this.weatherService = weatherService;
    }

    // Данная ручка идёт в API и добавляет данные в БД, если они отсутствуют в БД; используется JDBC
    @PostMapping("/cities/{city}/update")
    public ResponseEntity<WeatherObservation> updateWeatherIfNecessary(@PathVariable String city) {
        var weather = weatherService.updateWeatherIfNecessary(city);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(weather);
    }

    // Данная ручка возвращает последние 10 наблюдений для конкретного города. Использует дефолтную аннотацию
    // @Transactional вместе с JpaRepository
    @GetMapping("/cities/{city}/weather")
    public ResponseEntity<?> getMostRecentWeatherObservations(@PathVariable String city,
                                                              @RequestParam(required = false) Integer limit) {
        var weatherList = weatherService.getRecentObservations(city, limit != null ? limit : 10);
        return ResponseEntity.ok(weatherList);
    }

    // Данная ручка возвращает текущую погоду для города, использует @Transaction с JdbcTemplate
    @GetMapping("/cities/{city}/current")
    public ResponseEntity<?> getCurrentWeather(@PathVariable String city) {
        var weather = weatherService.getMostRecentObservation(city);
        return ResponseEntity.ok(weather);
    }

    // Данная ручка удаляет наблюдение, использует TransactionTemplate с JdbcTemplate
    @DeleteMapping("/observations/{id}")
    public ResponseEntity<?> deleteWeatherObservation(@PathVariable UUID id) {
        weatherService.deleteObservation(id);
        return ResponseEntity.noContent().build();
    }

    // Данная ручка выдает список городов, использует PlatformTransactionManager
    @GetMapping(value = {"/cities/", "/cities"})
    public ResponseEntity<?> getCityList() {
        var weather = weatherService.getCityList();
        return ResponseEntity.ok(weather);
    }

}
