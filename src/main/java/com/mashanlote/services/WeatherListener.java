package com.mashanlote.services;

import com.mashanlote.model.entities.WeatherObservation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WeatherListener {

    private final WeatherApiService apiService;

    public WeatherListener(WeatherApiService apiService) {
        this.apiService = apiService;
    }

    @KafkaListener(topics = "${topic.name}", groupId = "${group.id}")
    public void listenToIncomingCities(String city) {
        var observations = apiService.getRecentObservations(city, 30);
        var average = observations.stream()
                .mapToDouble(WeatherObservation::getTemperature)
                .average().orElse(-999);
        log.info(
                "Average temperature calculated over last {} observation(s) for city {} is {}",
                observations.size(),
                city,
                average
        );
    }

}
