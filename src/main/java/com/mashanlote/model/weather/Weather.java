package com.mashanlote.model.weather;

import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
public class Weather {

    private UUID regionId;
    private String regionName;
    private int temperature;
    private LocalDateTime dateTime;

}
