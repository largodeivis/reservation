package com.backend.reservation.repository;

import com.backend.reservation.model.AvailabilitySegment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AvailabilitySegmentRepository extends JpaRepository<AvailabilitySegment, long> {
    List<AvailabilitySegment> findByProviderName(String providerName);
}
