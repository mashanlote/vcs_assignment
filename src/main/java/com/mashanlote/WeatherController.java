package com.mashanlote;

import com.mashanlote.model.CreateRegionRequest;
import com.mashanlote.model.Weather;
import com.mashanlote.model.WeatherUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController()
@RequestMapping("/api/weather/cities")
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
    public ResponseEntity<?> addCity(@RequestBody CreateRegionRequest region) {
        var regionId = weatherService.addNewRegion(region);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("regionId", regionId));
    }

    @DeleteMapping("/{regionId}")
    public ResponseEntity<?> deleteCity(@PathVariable UUID regionId) {
        weatherService.removeRegion(regionId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // В моём случае put mapping не позволяет создать новый объект с погодой, потому что
    // принимает regionId уже существующего региона.
    @PutMapping("/{regionId}")
    public ResponseEntity<?> updateCity(@PathVariable UUID regionId, @RequestBody WeatherUpdate weatherUpdate) {
        weatherService.updateWeatherData(regionId, weatherUpdate);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
