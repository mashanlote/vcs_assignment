package com.mashanlote.services;

import com.mashanlote.model.entities.City;
import com.mashanlote.model.entities.WeatherObservation;
import com.mashanlote.model.entities.WeatherType;
import com.mashanlote.model.exceptions.ConflictException;
import com.mashanlote.model.exceptions.NotFoundException;
import com.mashanlote.model.weather.CreateWeatherObservationRequest;
import com.mashanlote.repositories.CityRepository;
import com.mashanlote.repositories.WeatherObservationRepository;
import com.mashanlote.repositories.WeatherTypeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// TODO: create DTOs
@Service
public class WeatherService {

    CityRepository cityRepository;
    WeatherTypeRepository weatherTypeRepository;
    WeatherObservationRepository weatherObservationRepository;

    public WeatherService(
            CityRepository cityRepository,
            WeatherTypeRepository weatherTypeRepository,
            WeatherObservationRepository weatherObservationRepository
    ) {
        this.cityRepository = cityRepository;
        this.weatherTypeRepository = weatherTypeRepository;
        this.weatherObservationRepository = weatherObservationRepository;
    }

    public UUID createCity(String name) {
        City city = City.builder()
                .name(name)
                .build();
        var savedCity = cityRepository.save(city);
        return savedCity.getId();
    }

    public City getCity(UUID id) {
        return cityRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public List<City> getCities() {
        return cityRepository.findAll();
    }

    public List<UUID> getCities(String name) {
        return cityRepository.findCitiesByName(name).stream()
                .map(City::getId)
                .toList();
    }

    public void updateCity(UUID id, String name) {
        var result = cityRepository.findById(id);
        if (result.isEmpty()) throw new NotFoundException();
        City city = result.get();
        city.setName(name);
        cityRepository.save(city);
    }

    public void deleteCity(UUID id) {
        if (!cityRepository.existsById(id)) {
            throw new NotFoundException();
        }
        cityRepository.deleteById(id);
    }

    public void createOrUpdateWeatherType(WeatherType type) {
        weatherTypeRepository.save(type);
    }

    public WeatherType getWeatherType(Integer id) {
        var type = weatherTypeRepository.findById(id);
        return type.orElseThrow(NotFoundException::new);
    }

    public List<WeatherType> getWeatherTypes() {
        return weatherTypeRepository.findAll();
    }

    public void deleteWeatherType(Integer id) {
        if (!weatherTypeRepository.existsById(id)) {
            throw new NotFoundException();
        }
        weatherTypeRepository.deleteById(id);
    }

    public void createWeatherObservation(CreateWeatherObservationRequest request) {
        if (weatherObservationRepository.existsByCityIdAndDateTime(
                request.cityId(), request.dateTime())
        ) {
            throw new ConflictException();
        }
        WeatherType weatherType = getWeatherType(request.weatherTypeId());
        City city = getCity(request.cityId());
        WeatherObservation weatherObservation = WeatherObservation.builder()
                .dateTime(request.dateTime())
                .weatherType(weatherType)
                .city(city)
                .temperature(request.temperature())
                .build();
        weatherObservationRepository.save(weatherObservation);
    }

    // TODO: return DTO
    public List<WeatherObservation> getCityWeatherObservation(UUID cityId) {
        return weatherObservationRepository.findAllByCityId(cityId);
    }

    public void getMostRecentWeatherObservation(UUID cityId) {}

    public void updateWeatherObservation(
    ) {}

    public void deleteWeatherObservation(UUID id) {

    }

}
