package com.mashanlote.model.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
public class City {

    @Id
    UUID id;

    String name;

    @OneToMany(mappedBy = "city")
    Set<WeatherObservation> weatherObservations;

}
