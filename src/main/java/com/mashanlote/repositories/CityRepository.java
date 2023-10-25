package com.mashanlote.repositories;

import com.mashanlote.model.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CityRepository extends JpaRepository<City, UUID> {
    List<City> findCitiesByName(String name);
    Optional<City> findByName(String name);
    boolean existsByName(String name);
}
