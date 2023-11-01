package com.mashanlote.model.weatherapi;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Location(
        String country,
        Double lat,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd H:mm")
        LocalDateTime localtime,
        Integer localtime_epoch,
        Double lon,
        String name,
        String region,
        String tz_id
) { }
