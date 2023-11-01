package com.mashanlote;

import com.mashanlote.model.entities.WeatherObservation;
import com.mashanlote.model.weatherapi.WeatherDTO;
import com.mashanlote.services.WeatherApiService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static com.mashanlote.controllers.TestData.weatherDTO1;
import static com.mashanlote.controllers.TestData.weatherObservation1;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
public class SpiedTest {

    @Test
    public void fetchWeatherFromExternalApiTest() {
        RestTemplate weatherApi = mock(RestTemplate.class);
        WeatherApiService weatherApiService = new WeatherApiService(
            null,
                weatherApi,
                null,
                null,
                null,
                null,
                null
        );
        WeatherApiService weatherApiServiceSpy = spy(weatherApiService);
        doReturn(weatherDTO1).when(weatherApiServiceSpy)
                .fetchWeatherFromExternalApi(anyString());
        doReturn(weatherObservation1).when(weatherApiServiceSpy)
                .saveWeatherObservation(any(WeatherDTO.class));
        weatherApiServiceSpy.fetchWeatherAndStoreInDb("Tomsk");
        verify(weatherApiServiceSpy).fetchWeatherFromExternalApi("Tomsk");
        verify(weatherApiServiceSpy).saveWeatherObservation(any(WeatherDTO.class));
    }

}
