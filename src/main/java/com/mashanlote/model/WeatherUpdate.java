package com.mashanlote.model;

import java.util.UUID;

public record WeatherUpdate(UUID regionId, int temperature) { }
