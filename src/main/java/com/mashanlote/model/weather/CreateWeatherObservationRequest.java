package com.mashanlote.model.weather;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateWeatherObservationRequest(
        UUID cityId,
        Integer weatherTypeId,
        Double temperature,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime dateTime
) { }
