package com.mashanlote.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class WeatherUpdater {

    public static final List<String> CITIES = List.of("Tomsk", "Moscow", "London", "Paris", "Beijing");
    private final WeatherApiService apiService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;
    private AtomicInteger iteration = new AtomicInteger();

    public WeatherUpdater(
            WeatherApiService apiService,
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${topic.name}") String topic
    ) {
        this.apiService = apiService;
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Scheduled(cron = "* * * * * *")
    public void updateCity() {
        String currentCity = CITIES.get(iteration.getAndIncrement() % CITIES.size());
        apiService.saveWeatherForCity(currentCity);
        kafkaTemplate.send(topic, currentCity);
    }


}
