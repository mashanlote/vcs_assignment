package com.mashanlote.model.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class WeatherObservation {

    @Id
    UUID id;

    @ManyToOne
    @JoinColumn(name = "city_id")
    City city;

    LocalDateTime dateTime;

    Double temperature;

    @ManyToOne
    @JoinColumn(name = "weather_type_id")
    WeatherType weatherType;

}
