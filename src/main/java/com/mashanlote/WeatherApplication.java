package com.mashanlote;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WeatherApplication {

    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(WeatherApplication.class, args);
    }

}
