package com.mashanlote.model.weather;

import java.util.UUID;

public record UpdateWeatherObservationRequest(
        UUID weatherObservationId,
        Double temperature,
        Integer weatherTypeId
) { }
