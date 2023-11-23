package com.mashanlote;

import com.mashanlote.model.entities.City;
import com.mashanlote.model.entities.WeatherObservation;
import com.mashanlote.services.WeatherCache;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CacheTest {

    public static WeatherObservation observation1 = WeatherObservation.builder()
            .city(City.builder().name("Tomsk").build())
            .build();
    public static WeatherObservation observation2 = WeatherObservation.builder()
            .city(City.builder().name("Moscow").build())
            .build();

    @Test
    void testCapacity() {
        var weatherCache = new WeatherCache(1, 60);
        weatherCache.put("Tomsk", observation1);
        assertThat(weatherCache.get("Tomsk").get().equals(observation1));
        weatherCache.put("Moscow", observation2);
        assertThat(weatherCache.get("Tomsk").isEmpty());
    }

    @Test
    void testTimeout() throws InterruptedException {
        var weatherCache = new WeatherCache(1, 60);
        weatherCache.put("Tomsk", observation1);
        Thread.sleep(1500);
        assertThat(weatherCache.get("Tomsk").isEmpty());
    }

}
