package com.mashanlote.controllers;

import com.mashanlote.model.exceptions.NotFoundException;
import com.mashanlote.services.WeatherApiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.mashanlote.controllers.TestData.cities;
import static com.mashanlote.controllers.TestData.weatherObservations;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.properties")
public class WeatherApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private WeatherApiService weatherApiService;

    @Test
    public void fetchFromExternalApi() throws Exception {
        when(weatherApiService.updateWeatherIfNecessary("Tomsk"))
                        .thenReturn(weatherObservations.get(0));

        mockMvc.perform(get("/weather/cities/Tomsk/update"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.dateTime").isNotEmpty())
                .andExpect(jsonPath("$.temperature", is(17.0)));
    }

    // Отрицательный сценарий
    @Test
    public void fetchFromExternalApi_CityDoesNotExist() throws Exception {
        when(weatherApiService.updateWeatherIfNecessary("IDONTEXIST"))
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
