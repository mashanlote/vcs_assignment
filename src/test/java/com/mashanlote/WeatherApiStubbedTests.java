package com.mashanlote;

import com.mashanlote.model.weatherapi.CurrentWeather;
import com.mashanlote.model.weatherapi.WeatherDTO;
import com.mashanlote.services.WeatherApiService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
public class WeatherApiStubbedTests {

    @MockBean
    private RestTemplate restTemplateMock;

    @Autowired
    private WeatherApiService weatherApiService;

    // Проверяем, что метод запроса внешнего ресурса вообще вызывается
    @Test
    public void CallExternalApiTest() {
        var city = "Tomsk";
        var mockedWeather = WeatherDTO.builder()
                .current(CurrentWeather.builder().temp_c(17.0).build())
                .build();

        doReturn(mockedWeather).when(restTemplateMock).getForObject(anyString(), eq(WeatherDTO.class), anyString());
        var result = weatherApiService.fetchWeatherFromExternalApi(city);
        verify(restTemplateMock, times(1)).getForObject(anyString(), eq(WeatherDTO.class), anyString());
        Assertions.assertThat(result).isInstanceOf(WeatherDTO.class);
        Assertions.assertThat(result).hasFieldOrProperty("current");
    }


}


