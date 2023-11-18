package com.mashanlote;

import com.mashanlote.services.WeatherApiService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class IntegrationTests {

    @Autowired
    private WeatherApiService service;

    // Интеграционный тест на взаимодействие со внешним API, нужно указать реальный токен в параметрах запуска теста
    @Test
    public void fetchWeatherFromExternalApiTest() {
        var weather = service.fetchWeatherFromExternalApi("Tomsk");
        Assertions.assertThat(weather).isNotNull();
    }

}
