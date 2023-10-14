package com.mashanlote.model.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WeatherObservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @JsonBackReference(value = "cityToObservation")
    @ManyToOne
    @JoinColumn(name = "city_id")
    City city;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime dateTime;

    Double temperature;

    @JsonBackReference(value = "typeToObservation")
    @ManyToOne
    @JoinColumn(name = "weather_type_id")
    WeatherType weatherType;

}
