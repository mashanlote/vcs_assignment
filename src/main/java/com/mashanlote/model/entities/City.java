package com.mashanlote.model.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = {"id", "weatherObservations"})
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    String name;

    @JsonManagedReference(value = "cityToObservation")
    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL)
    @JsonIgnore
    Set<WeatherObservation> weatherObservations;

}
