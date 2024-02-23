package com.backend.reservation.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class AvailabilitySegment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    @Getter
    private Provider provider;

    @Getter
    private LocalDate date;
    @Getter
    private LocalTime startTime;
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Getter
    private AvailabilityStatus status;

    public AvailabilitySegment() {

    }

    public AvailabilitySegment(Provider provider, LocalDate date, LocalTime startTime,AvailabilityStatus status){
        this.provider = provider;
        this.date = date;
        this.startTime = startTime;
        this.status = status;
    }
}