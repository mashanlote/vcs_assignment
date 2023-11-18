package com.mashanlote.services;

import com.mashanlote.model.entities.WeatherObservation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class WeatherCache {

    private final int timeoutInSeconds;
    private final LinkedHashMap<String, ObservationWithTimeout> map;

    public WeatherCache(
            @Value("${course.cache.size}") int cacheSize,
            @Value("${course.cache.timeout}") int timeoutInSeconds
    ) {
        this.timeoutInSeconds = timeoutInSeconds;
        this.map = new LinkedHashMap<>(cacheSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, ObservationWithTimeout> eldest) {
                return size() > cacheSize;
            }
        };
    }

    public Optional<WeatherObservation> get(String city) {
        synchronized (map) {
            var observationWithTimeout = map.get(city);
            if (observationWithTimeout == null) {
                return Optional.empty();
            } else if (observationWithTimeout.timeoutAt.isBefore(Instant.now())) {
                map.remove(city);
                return Optional.empty();
            }
            return Optional.of(observationWithTimeout.observation);
        }
    }

    public void put(String city, WeatherObservation observation) {
        synchronized (map) {
            map.put(city, new ObservationWithTimeout(observation, Instant.now().plus(timeoutInSeconds, ChronoUnit.SECONDS)));
        }
    }

    public void invalidateObservation(String city) {
        synchronized (map) {
            map.remove(city);
        }
    }

    private record ObservationWithTimeout(WeatherObservation observation, Instant timeoutAt) { }
}
