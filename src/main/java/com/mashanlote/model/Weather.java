package com.mashanlote.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Builder
public class Weather {

    private UUID regionId;
    private String regionName;
    private int temperature;
    private LocalDateTime dateTime;

    public Weather(UUID regionId, String regionName, int temperature, LocalDateTime dateTime) {
        this.regionId = regionId;
        this.regionName = regionName;
        this.temperature = temperature;
        this.dateTime = dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Weather weather = (Weather) o;
        return Objects.equals(regionId, weather.regionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regionId);
    }
}
