package com.mashanlote;

import com.mashanlote.model.exceptions.ConflictException;
import com.mashanlote.model.exceptions.NotFoundException;
import com.mashanlote.model.CreateRegionRequest;
import com.mashanlote.model.Weather;
import com.mashanlote.model.WeatherUpdate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class WeatherService {

    Map<UUID, Weather> regionUUIDtoWeatherMap = new HashMap<>();
    Map<String, UUID> regionNameToRegionIdMap = new HashMap<>();

    public List<UUID> getAvailableRegionUUIDs() {
        return regionUUIDtoWeatherMap.values().stream()
                .map(Weather::getRegionId)
                .toList();
    }

    public Weather getCityWeatherByRegionId(UUID regionId) {
        Weather weather = regionUUIDtoWeatherMap.get(regionId);
        if (weather == null) throw new NotFoundException();
        return weather;
    }

    public UUID addNewRegion(CreateRegionRequest region) {
        if (regionNameToRegionIdMap.containsKey(region.regionName())) throw new ConflictException();
        LocalDateTime dateTime = LocalDateTime.now();
        UUID regionId = UUID.randomUUID();
        Weather weather = Weather.builder()
                .regionId(regionId)
                .regionName(region.regionName())
                .dateTime(dateTime)
                .temperature(region.temperature())
                .build();
        regionNameToRegionIdMap.put(region.regionName(), regionId);
        regionUUIDtoWeatherMap.put(regionId, weather);
        return regionId;
    }

    public void updateWeatherData(UUID regionId, WeatherUpdate weatherUpdate) {
        if (!regionUUIDtoWeatherMap.containsKey(regionId)) throw new NotFoundException();
        Weather weather = Weather.builder()
                .regionId(regionId)
                .regionName(regionUUIDtoWeatherMap.get(regionId).getRegionName())
                .dateTime(LocalDateTime.now())
                .temperature(weatherUpdate.temperature())
                .build();
        regionUUIDtoWeatherMap.put(regionId, weather);
    }

    public void removeRegion(UUID regionId) {
        if (!regionUUIDtoWeatherMap.containsKey(regionId)) throw new NotFoundException();
        String regionName = regionUUIDtoWeatherMap.get(regionId).getRegionName();
        regionUUIDtoWeatherMap.remove(regionId);
        regionNameToRegionIdMap.remove(regionName);
    }

}
