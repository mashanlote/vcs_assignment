package com.mashanlote.services;

import com.mashanlote.model.entities.City;
import com.mashanlote.model.entities.WeatherObservation;
import com.mashanlote.model.entities.WeatherType;
import com.mashanlote.model.exceptions.ConflictException;
import com.mashanlote.model.exceptions.NotFoundException;
import com.mashanlote.model.weather.CreateWeatherObservationRequest;
import com.mashanlote.model.weather.UpdateWeatherObservationRequest;
import com.mashanlote.repositories.CityRepository;
import com.mashanlote.repositories.WeatherObservationRepository;
import com.mashanlote.repositories.WeatherTypeRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service("JPA")
public class WeatherServiceJpa implements WeatherService {

    CityRepository cityRepository;
    WeatherTypeRepository weatherTypeRepository;
    WeatherObservationRepository weatherObservationRepository;

    public WeatherServiceJpa(
            CityRepository cityRepository,
            WeatherTypeRepository weatherTypeRepository,
            WeatherObservationRepository weatherObservationRepository
    ) {
        this.cityRepository = cityRepository;
        this.weatherTypeRepository = weatherTypeRepository;
        this.weatherObservationRepository = weatherObservationRepository;
    }

    @Override
    public UUID createCity(String name) {
        City city = City.builder()
                .name(name)
                .build();
        if (cityRepository.existsByName(name)) throw new ConflictException();
        var savedCity = cityRepository.save(city);
        return savedCity.getId();
    }

    @Override
    public City getCity(UUID id) {
        return cityRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public List<City> getCities() {
        return cityRepository.findAll();
    }

    @Override
    public List<UUID> getCities(String name) {
        return cityRepository.findCitiesByName(name).stream()
                .map(City::getId)
                .toList();
    }

    @Override
    public void updateCity(UUID id, String name) {
        var result = cityRepository.findById(id);
        if (result.isEmpty()) throw new NotFoundException();
        City city = result.get();
        city.setName(name);
        cityRepository.save(city);
    }

    @Override
    public void deleteCity(UUID id) {
        if (!cityRepository.existsById(id)) {
            throw new NotFoundException();
        }
        cityRepository.deleteById(id);
    }

    @Override
    public void createOrUpdateWeatherType(WeatherType type) {
        var oldWeatherType = weatherTypeRepository.findById(type.getId());
        if (oldWeatherType.isEmpty()) {
            weatherTypeRepository.save(type);
        } else {
            var updatedWeatherType = WeatherType.builder()
                    .id(oldWeatherType.get().getId())
                    .dayDescription(type.getDayDescription().isEmpty() ?
                            oldWeatherType.get().getDayDescription() :
                            type.getDayDescription())
                    .nightDescription(type.getNightDescription().isEmpty() ?
                            oldWeatherType.get().getNightDescription() :
                            type.getNightDescription())
                    .build();
            weatherTypeRepository.save(updatedWeatherType);
        }
    }

    @Override
    public WeatherType getWeatherType(Integer id) {
        var type = weatherTypeRepository.findById(id);
        return type.orElseThrow(NotFoundException::new);
    }

    @Override
    public List<WeatherType> getWeatherTypes() {
        return weatherTypeRepository.findAll();
    }

    @Override
    public void deleteWeatherType(Integer id) {
        if (!weatherTypeRepository.existsById(id)) {
            throw new NotFoundException();
        }
        weatherTypeRepository.deleteById(id);
    }

    @Override
    public void createWeatherObservation(CreateWeatherObservationRequest request) {
        if (!cityRepository.existsById(request.cityId())) {
            throw new NotFoundException();
        }
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

    @Override
    public List<WeatherObservation> getCityWeatherObservation(UUID cityId) {
        return weatherObservationRepository.findAllByCityId(cityId);
    }

    @Override
    public WeatherObservation getMostRecentWeatherObservation(UUID cityId) {
        return weatherObservationRepository.findAllByCityId(cityId).stream()
                .max(Comparator.comparing(WeatherObservation::getDateTime))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public void updateWeatherObservation(UpdateWeatherObservationRequest request) {
        var weather = weatherObservationRepository.findById(request.weatherObservationId());
        var updatedWeather = weather.orElseThrow(NotFoundException::new);
        if (!weatherTypeRepository.existsById(request.weatherTypeId())) throw new NotFoundException();
        WeatherType type = weatherTypeRepository.getReferenceById(request.weatherTypeId());
        updatedWeather.setWeatherType(type);
        updatedWeather.setTemperature(request.temperature());
        weatherObservationRepository.save(updatedWeather);
    }

    @Override
    public void deleteWeatherObservation(UUID id) {
        if (!weatherObservationRepository.existsById(id)) {
            throw new NotFoundException();
        }
        weatherObservationRepository.deleteById(id);
    }

}
