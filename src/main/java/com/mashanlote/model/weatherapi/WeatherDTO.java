package com.mashanlote.model.weatherapi;

import lombok.Builder;

@Builder
public record WeatherDTO(CurrentWeather current, Location location) { }
