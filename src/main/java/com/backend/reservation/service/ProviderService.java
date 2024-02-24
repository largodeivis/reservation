package com.backend.reservation.service;

import com.backend.reservation.model.AppointmentSlot;
import com.backend.reservation.model.AvailabilityStatus;
import com.backend.reservation.model.Provider;
import com.backend.reservation.model.ProviderAvailabilityRequest;
import com.backend.reservation.repository.AppointmentRepository;
import com.backend.reservation.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class ProviderService implements IProviderService{

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public Provider addProviderAndAvailability(String name, LocalDate date, LocalTime startTime, LocalTime endTime){
        Provider provider = new Provider(name);

        provider = providerRepository.save(provider);

        while(!startTime.isAfter(endTime.minusMinutes(15))){
            AppointmentSlot segment = new AppointmentSlot(provider, date, date.atTime(startTime), AvailabilityStatus.AVAILABLE);
            appointmentRepository.save(segment);
            startTime = startTime.plusMinutes(15);
        }

        return provider;
    }

    public Optional<Provider> getProvider(long providerId){
        Optional<Provider> provider = providerRepository.findById(providerId);
        return provider;
    }
}
