package com.mashanlote.controllers;

import com.mashanlote.model.entities.City;
import com.mashanlote.model.entities.WeatherObservation;
import com.mashanlote.model.entities.WeatherType;
import com.mashanlote.model.exceptions.NotFoundException;
import com.mashanlote.services.WeatherApiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class WeatherApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private WeatherApiService weatherApiService;

    WeatherObservation weatherObservation1 = WeatherObservation.builder()
                .weatherType(WeatherType.builder()
                        .id(1000)
                        .dayDescription("Sunny")
                        .nightDescription("Clear")
                        .build()
                )
                .dateTime(LocalDateTime.of(2003, 12, 11, 10, 9, 8))
                .city(City.builder()
                        .name("Tomsk")
                        .id(UUID.fromString("aa0c4adc-2195-4ccf-bc33-aeca2cea6f7b"))
                        .build()
                )
                .temperature(17.0)
                .id(UUID.fromString("aa0c4adc-2195-4ccf-bc33-aeca2cea6f7a"))
                .build();
        WeatherObservation weatherObservation2 = WeatherObservation.builder()
                .weatherType(WeatherType.builder()
                        .id(1000)
                        .dayDescription("Sunny")
                        .nightDescription("Clear")
                        .build()
                )
                .dateTime(LocalDateTime.of(2003, 12, 11, 11, 9, 8))
                .city(City.builder()
                        .name("Tomsk")
                        .id(UUID.fromString("aa0c4adc-2195-4ccf-bc33-aeca2cea6f7b"))
                        .build()
                )
                .temperature(17.0)
                .id(UUID.fromString("aa0c4adc-2195-4ccf-bc33-aeca2cea6f7c"))
                .build();

    private final List<WeatherObservation> weatherObservations = List.of(
            weatherObservation1,
            weatherObservation2
    );

    List<City> cities = List.of(
            City.builder().name("Tomsk").id(UUID.randomUUID()).build(),
            City.builder().name("Moscow").id(UUID.randomUUID()).build()
    );

    @Test
    public void fetchFromExternalApi() throws Exception {
        when(weatherApiService.fetchWeatherAndStoreInDb("Tomsk"))
                        .thenReturn(weatherObservation1);

        mockMvc.perform(get("/weather/cities/Tomsk/update"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.temperature", is(17.0)));
    }

    // Отрицательный сценарий
    @Test
    public void fetchFromExternalApi_CityDoesNotExist() throws Exception {
        when(weatherApiService.fetchWeatherAndStoreInDb("IDONTEXIST"))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/weather/cities/IDONTEXIST/update"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getMostRecentWeatherObservations() throws Exception {
        when(weatherApiService.getRecentObservations("Tomsk", 10))
                .thenReturn(weatherObservations);
        mockMvc.perform(get("/weather/cities/Tomsk/weather"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void getMostRecentWeatherObservation() throws Exception {
        when(weatherApiService.getMostRecentObservation("Tomsk"))
                .thenReturn(weatherObservations.get(0));
        mockMvc.perform(get("/weather/cities/Tomsk/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.dateTime").exists())
                .andExpect(jsonPath("$.temperature").exists());
    }

    @Test
    public void getCityList() throws Exception {
        when(weatherApiService.getCityList())
                .thenReturn(cities);
        mockMvc.perform(get("/weather/cities/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void deleteWeatherObservation() throws Exception {
        doNothing().when(weatherApiService).deleteObservation(Mockito.any(UUID.class));
        mockMvc.perform(delete("/weather/observations/" + UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteWeatherObservation_NoSuchObservation() throws Exception {
        doThrow(NotFoundException.class).when(weatherApiService).deleteObservation(Mockito.any(UUID.class));
        mockMvc.perform(delete("/weather/observations/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

}
