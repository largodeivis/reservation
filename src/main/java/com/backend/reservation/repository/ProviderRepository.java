package com.backend.reservation.repository;

import com.backend.reservation.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRepository extends JpaRepository<Provider, Long> {
}
