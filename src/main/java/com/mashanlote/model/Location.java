package com.mashanlote.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record Location(
        String country,
        Double lat,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime localtime,
        Integer localtime_epoch,
        Double lon,
        String name,
        String region,
        String tz_id
) { }
