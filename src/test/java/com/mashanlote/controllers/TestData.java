package com.mashanlote.controllers;

import com.mashanlote.model.entities.City;
import com.mashanlote.model.entities.WeatherObservation;
import com.mashanlote.model.entities.WeatherType;
import com.mashanlote.model.weatherapi.Condition;
import com.mashanlote.model.weatherapi.CurrentWeather;
import com.mashanlote.model.weatherapi.Location;
import com.mashanlote.model.weatherapi.WeatherDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TestData {

    public static WeatherObservation weatherObservation1 = WeatherObservation.builder()
            .weatherType(WeatherType.builder()
                    .id(1000)
                    .dayDescription("Sunny")
                    .nightDescription("Clear")
                    .build()
            )
            .dateTime(LocalDateTime.of(2003, 12, 11, 10, 9, 8))
            .city(City.builder()
                    .name("Tomsk")
                    .id(UUID.fromString("aa0c4adc-2195-4ccf-bc33-aeca2cea6f7b"))
                    .build()
            )
            .temperature(17.0)
            .id(UUID.fromString("aa0c4adc-2195-4ccf-bc33-aeca2cea6f7a"))
            .build();

    public static WeatherObservation weatherObservation2 = WeatherObservation.builder()
            .weatherType(WeatherType.builder()
                    .id(1000)
                    .dayDescription("Sunny")
                    .nightDescription("Clear")
                    .build()
            )
            .dateTime(LocalDateTime.of(2003, 12, 11, 11, 9, 8))
            .city(City.builder()
                    .name("Tomsk")
                    .id(UUID.fromString("aa0c4adc-2195-4ccf-bc33-aeca2cea6f7b"))
                    .build()
            )
            .temperature(17.0)
            .id(UUID.fromString("aa0c4adc-2195-4ccf-bc33-aeca2cea6f7c"))
            .build();

    public static List<WeatherObservation> weatherObservations = List.of(
            weatherObservation1,
            weatherObservation2
    );

    public static List<City> cities = List.of(
            City.builder().name("Tomsk").id(UUID.randomUUID()).build(),
            City.builder().name("Moscow").id(UUID.randomUUID()).build()
    );

    public static WeatherDTO weatherDTO1 = WeatherDTO.builder()
            .current(CurrentWeather.builder()
                    .temp_c(17.0)
                    .condition(Condition.builder()
                            .text("Sunny")
                            .code(1000)
                            .build()
                    )
                    .last_updated(LocalDateTime.of(2023, 10, 29, 15, 47, 30))
                    .build()
            )
            .location(Location.builder()
                    .name("Tomsk")
                    .build()
            )
            .build();

}
