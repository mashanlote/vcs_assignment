package com.mashanlote.repositories;

import com.mashanlote.model.entities.WeatherObservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Repository
public interface WeatherObservationRepository extends JpaRepository<WeatherObservation, UUID> {
    public boolean existsByCityIdAndDateTime(UUID cityId, LocalDateTime dateTime);
    List<WeatherObservation> findAllByCityId(UUID cityId);
}
