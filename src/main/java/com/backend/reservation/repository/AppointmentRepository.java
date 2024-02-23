package com.backend.reservation.repository;

import com.backend.reservation.model.AppointmentSlot;
import com.backend.reservation.model.AvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentSlot, Long> {
    List<AppointmentSlot> findByProviderId(long providerId);
    List<AppointmentSlot> findByDate(LocalDate date);
    List<AppointmentSlot> findByStatus(AvailabilityStatus status);
}
