package com.mashanlote.controllers;

import com.mashanlote.model.entities.City;
import com.mashanlote.model.entities.WeatherObservation;
import com.mashanlote.model.entities.WeatherType;
import com.mashanlote.model.weather.CreateCityRequest;
import com.mashanlote.model.weather.CreateWeatherObservationRequest;
import com.mashanlote.model.weather.UpdateCityRequest;
import com.mashanlote.model.weather.UpdateWeatherObservationRequest;
import com.mashanlote.services.WeatherService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController()
@RequestMapping("/api")
public class WeatherController {

    WeatherService weatherService;

    public WeatherController(@Qualifier("getService") WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @PostMapping(value = {"/cities", "/cities/"})
    public ResponseEntity<UUID> createCity(@RequestBody CreateCityRequest createCityRequest) {
        var id = weatherService.createCity(createCityRequest.name());
        return ResponseEntity.ok(id);
    }

    @GetMapping(value = {"/cities", "/cities/"})
    public ResponseEntity<List<City>> getAllCities() {
        var cities = weatherService.getCities();
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/cities/{id}")
    public ResponseEntity<City> getCity(@PathVariable UUID id) {
        City city = weatherService.getCity(id);
        return ResponseEntity.ok(city);
    }

    @GetMapping("/cities/all/{name}")
    public ResponseEntity<List<UUID>> getCity(@PathVariable String name) {
        var city = weatherService.getCities(name);
        return ResponseEntity.ok(city);
    }

    @PutMapping("/cities/{id}")
    public ResponseEntity<?> updateCity(
            @PathVariable UUID id,
            @RequestBody UpdateCityRequest updateCityRequest
    ) {
        weatherService.updateCity(id, updateCityRequest.name());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cities/{id}")
    public ResponseEntity<?> deleteCity(@PathVariable UUID id) {
        weatherService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = {"/weather-types", "/weather-types/"})
    public ResponseEntity<List<WeatherType>> getWeatherTypes() {
        var types = weatherService.getWeatherTypes();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/weather-types/{id}")
    public ResponseEntity<WeatherType> getWeatherType(@PathVariable Integer id) {
        var type = weatherService.getWeatherType(id);
        return ResponseEntity.ok(type);
    }

    @PutMapping(value = {"/weather-types", "/weather-types/"})
    public ResponseEntity<?> createOrUpdateWeatherType(@RequestBody WeatherType type) {
        weatherService.createOrUpdateWeatherType(type);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/weather-types/{id}")
    public ResponseEntity<?> deleteWeatherType(@PathVariable Integer id) {
        weatherService.deleteWeatherType(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/weather/{cityId}")
    public ResponseEntity<List<WeatherObservation>> getWeatherObservations(@PathVariable UUID cityId) {
        var observations = weatherService.getCityWeatherObservation(cityId);
        return ResponseEntity.ok(observations);
    }

    @PostMapping(value = {"/weather", "/weather/"})
    public ResponseEntity<?> createWeatherObservation(
            @RequestBody CreateWeatherObservationRequest request
    ) {
        weatherService.createWeatherObservation(request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = {"/weather", "/weather/"})
    public ResponseEntity<?> updateWeatherObservation(
            @RequestBody UpdateWeatherObservationRequest request
    ) {
        weatherService.updateWeatherObservation(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/weather/observations/{id}")
    public ResponseEntity<?> deleteWeatherObservation(@PathVariable UUID id) {
        weatherService.deleteWeatherObservation(id);
        return ResponseEntity.noContent().build();
    }

}
