package com.mashanlote;

import com.mashanlote.model.CreateRegionRequest;
import com.mashanlote.model.ErrorDetails;
import com.mashanlote.model.Weather;
import com.mashanlote.model.WeatherUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get a list of available regions")
    @ApiResponses(
        @ApiResponse(responseCode = "200", description = "Return the list of UUIDs of the available regions")
    )
    @GetMapping("")
    public ResponseEntity<List<UUID>> getAvailableRegionUUIDs() {
        var availableRegions = weatherService.getAvailableRegionUUIDs();
        return ResponseEntity.ok()
                .body(availableRegions);
    }

    @Operation(summary = "Add a new region")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Most recent weather for the city"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "409", description = "Region already exists", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @PostMapping("")
    public ResponseEntity<?> addCity(@RequestBody CreateRegionRequest region) {
        var regionId = weatherService.addNewRegion(region);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("regionId", regionId));
    }

    @Operation(summary = "Get weather for the provided city")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Most recent weather for the city"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "404", description = "Region does not exist", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @GetMapping("/{regionId}")
    public ResponseEntity<Weather> getCityWeather(
            @Parameter(description = "UUID of the region")
            @PathVariable UUID regionId
    ) {
        var weather = weatherService.getCityWeatherByRegionId(regionId);
        return ResponseEntity.ok()
                .body(weather);
    }

    @Operation(summary = "Remove a region from database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Most recent weather for the city"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "404", description = "Region does not exists", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @DeleteMapping("/{regionId}")
    public ResponseEntity<?> deleteCity(@PathVariable UUID regionId) {
        weatherService.removeRegion(regionId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Updates weather for the provided city")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Most recent weather for the city"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "404", description = "Region does not exists", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @PutMapping("/{regionId}")
    public ResponseEntity<?> updateCity(@PathVariable UUID regionId, @RequestBody WeatherUpdate weatherUpdate) {
        weatherService.updateWeatherData(regionId, weatherUpdate);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
