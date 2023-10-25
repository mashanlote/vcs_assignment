package com.mashanlote.services;

import com.mashanlote.model.entities.City;
import com.mashanlote.model.entities.WeatherObservation;
import com.mashanlote.model.entities.WeatherType;
import com.mashanlote.model.weather.CreateWeatherObservationRequest;
import com.mashanlote.model.weather.UpdateWeatherObservationRequest;

import java.util.List;
import java.util.UUID;

public interface WeatherService {
    UUID createCity(String name);

    City getCity(UUID id);

    List<City> getCities();

    List<UUID> getCities(String name);

    void updateCity(UUID id, String name);

    void deleteCity(UUID id);

    void createOrUpdateWeatherType(WeatherType type);

    WeatherType getWeatherType(Integer id);

    List<WeatherType> getWeatherTypes();

    void deleteWeatherType(Integer id);

    void createWeatherObservation(CreateWeatherObservationRequest request);

    List<WeatherObservation> getCityWeatherObservation(UUID cityId);

    WeatherObservation getMostRecentWeatherObservation(UUID cityId);

    void updateWeatherObservation(UpdateWeatherObservationRequest request);

    void deleteWeatherObservation(UUID id);
}
