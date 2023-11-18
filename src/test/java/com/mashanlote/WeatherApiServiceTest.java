package com.mashanlote;

import com.mashanlote.model.entities.City;
import com.mashanlote.model.entities.WeatherObservation;
import com.mashanlote.model.entities.WeatherType;
import com.mashanlote.model.weatherapi.WeatherDTO;
import com.mashanlote.services.WeatherApiService;
import com.mashanlote.services.WeatherCache;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class WeatherApiServiceTest {

    @Test
    void testCache() {
        var jdbcTemplate = mock(JdbcTemplate.class);
        var weatherCache = new WeatherCache(5, 60);
        var weatherApiService = new WeatherApiService(null, null, null, null, jdbcTemplate, null, null, weatherCache);
        var spiedService = spy(weatherApiService);
        var weatherType = WeatherType.builder().build();
        var city = City.builder().name("Tomsk").id(UUID.randomUUID()).build();
        var observation1 = WeatherObservation.builder().weatherType(weatherType).temperature(18.0).city(city).build();
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), anyString()))
                .thenReturn(city);
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), any(UUID.class)))
                .thenReturn(observation1);

        // Получаем погоду, в кэше нет, убедимся, что добавилось значение в кэш
        spiedService.getMostRecentObservation("Tomsk");
        assertThat(weatherCache.get("Tomsk").isPresent());
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), any(RowMapper.class), anyString());

        // Получаем погоду, есть в кэше, убедимся, что не ходили в базу
        spiedService.getMostRecentObservation("Tomsk");
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), any(RowMapper.class), anyString());

        // Изменяем погоду, данные удаляются из кэша
        var observation2 = WeatherObservation.builder().weatherType(weatherType).temperature(19.0).city(city).build();
        doReturn(WeatherDTO.builder().build()).when(spiedService).fetchWeatherFromExternalApi(anyString());
        doReturn(observation2).when(spiedService).saveWeatherObservation(any(WeatherDTO.class));
        spiedService.updateWeatherIfNecessary("Tomsk");
        assertThat(weatherCache.get("Tomsk").isEmpty());
    }

}
