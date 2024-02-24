package com.backend.reservation.repository;

import com.backend.reservation.model.AppointmentSlot;
import com.backend.reservation.model.AvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentSlot, Long> {
    List<AppointmentSlot> findByProviderId(long providerId);
    List<AppointmentSlot> findByDate(LocalDate date);
    List<AppointmentSlot> findByStatus(AvailabilityStatus status);
    @Query("SELECT a from AppointmentSlot a Where a.status = 'AVAILABLE' AND a.appointmentTime > :cutoff")
    List<AppointmentSlot> findAvailableAppointmentsAfter24Hours(LocalDateTime cutoff);

}
