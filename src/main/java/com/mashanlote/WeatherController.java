package com.mashanlote;

import com.mashanlote.model.RegionNew;
import com.mashanlote.model.Weather;
import com.mashanlote.model.WeatherUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController("/api/weather/cities")
public class WeatherController {

    WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("")
    public ResponseEntity<List<UUID>> getAvailableRegionUUIDs() {
        var availableRegions = weatherService.getAvailableRegionUUIDs();
        return ResponseEntity.ok()
                .body(availableRegions);
    }

    @GetMapping("/{regionId}")
    public ResponseEntity<Weather> getCityWeather(@PathVariable UUID regionId) {
        var weather = weatherService.getCityWeatherByRegionId(regionId);
        return ResponseEntity.ok()
                .body(weather);
    }

    @PostMapping("")
    public ResponseEntity<?> addCity(@RequestBody RegionNew region) {
        var weather = weatherService.addNewRegion(region);
        // TODO: return UUID of the added city
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{regionId}")
    public ResponseEntity<?> deleteCity(@PathVariable UUID regionId) {
        weatherService.removeRegion(regionId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{regionId}")
    public ResponseEntity<?> updateCity(@PathVariable UUID regionId, @RequestBody WeatherUpdate weatherUpdate) {
        weatherService.updateWeatherData(regionId, weatherUpdate);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
