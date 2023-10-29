package com.mashanlote.model.weatherapi;

import lombok.Builder;

@Builder
public record Condition(Integer code, String icon, String text) { }
