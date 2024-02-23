package com.backend.reservation.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class AvailabilitySegment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
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