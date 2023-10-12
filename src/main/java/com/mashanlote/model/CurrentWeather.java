package com.mashanlote.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record CurrentWeather(
    Integer cloud,
    Condition condition,
    Double feelslike_c,
    Double feelslike_f,
    Double gust_kph,
    Double gust_mph,
    Integer humidity,
    Integer is_day,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    LocalDateTime last_updated,
    Integer last_updated_epoch,
    Double precip_in,
    Double precip_mb,
    Double temp_c,
    Double temp_f,
    Double vis_km,
    Double vis_miles,
    Integer wind_degree,
    String wind_dir,
    Double wind_kph,
    Double wind_mph
) { }
