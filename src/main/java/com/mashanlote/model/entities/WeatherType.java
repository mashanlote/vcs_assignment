package com.mashanlote.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherType {

    @Id
    Integer id;

    String dayDescription;

    String nightDescription;

    @JsonManagedReference(value = "typeToObservation")
    @OneToMany(mappedBy = "weatherType", cascade = CascadeType.ALL)
    @JsonIgnore
    Set<WeatherObservation> weatherObservation;

}
