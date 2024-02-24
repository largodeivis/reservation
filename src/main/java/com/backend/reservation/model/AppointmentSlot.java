package com.backend.reservation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
public class AppointmentSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private long id;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    @Getter
    private Provider provider;

    @Getter
    private LocalDate date;

    @Getter
    @JsonFormat(pattern = "HH:mm")
    private LocalDateTime appointmentTime;

    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private AvailabilityStatus status;

    @Setter
    @JsonIgnore
    private LocalDateTime reservationTime;

    public AppointmentSlot() {

    }

    public AppointmentSlot(Provider provider, LocalDate date, LocalDateTime appointmentTime, AvailabilityStatus status){
        this.provider = provider;
        this.date = date;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    public Optional<LocalDateTime> getReservationTime(){
        return Optional.ofNullable(reservationTime);
    }
}