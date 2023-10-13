package com.mashanlote.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.Set;

@Entity
public class WeatherType {

    @Id
    Integer id;

    String dayDescription;

    String nightDescription;

    @OneToMany(mappedBy = "weatherType")
    Set<WeatherObservation> weatherObservation;


}
