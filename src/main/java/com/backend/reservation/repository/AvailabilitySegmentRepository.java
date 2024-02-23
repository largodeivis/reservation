package com.backend.reservation.repository;

import com.backend.reservation.model.AvailabilitySegment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilitySegmentRepository extends JpaRepository<AvailabilitySegment, Long> {
    List<AvailabilitySegment> findByProviderId(long providerId);
    List<AvailabilitySegment> findByDate(LocalDate date);
}
