package com.backend.reservation.service;

import com.backend.reservation.model.Provider;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface IProviderService {
    Provider addProviderAndAvailability(String name, LocalDate date, LocalTime startTime, LocalTime endTime);
    Optional<Provider> getProvider(long providerId);
}
