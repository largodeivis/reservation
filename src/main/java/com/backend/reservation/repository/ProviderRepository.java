package com.backend.reservation.repository;

import com.backend.reservation.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProviderRepository extends JpaRepository<Provider, long> {
}
