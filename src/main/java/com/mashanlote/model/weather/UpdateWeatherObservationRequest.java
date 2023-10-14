package com.mashanlote.model.weather;

import java.util.UUID;

public record UpdateWeatherObservationRequest(
        UUID weatherObservationId,
        Integer temperature,
        Integer weatherTypeId
) { }
