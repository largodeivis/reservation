package com.backend.reservation.model;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

public class ProviderAvailabilityRequest {
    @Getter
    private String name;

    @Getter
    private LocalDate date;

    @Getter
    private LocalTime startTime;

    @Getter
    private LocalTime endTime;
}
