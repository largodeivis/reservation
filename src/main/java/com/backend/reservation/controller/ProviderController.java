package com.backend.reservation.controller;

import com.backend.reservation.model.AppointmentSlot;
import com.backend.reservation.model.AvailabilityStatus;
import com.backend.reservation.model.Provider;
import com.backend.reservation.model.ProviderAvailabilityRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.backend.reservation.repository.AppointmentRepository;
import com.backend.reservation.repository.ProviderRepository;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
public class ProviderController {

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @PostMapping("/provider")
    public ResponseEntity<?> addProviderAndAvailability(@RequestBody ProviderAvailabilityRequest request){
        if(request.getStartTime().isAfter(request.getEndTime())){
            return new ResponseEntity<>("Start Time can't be before end time.", HttpStatus.BAD_REQUEST);
        }

        if(request.getName().isBlank()){
            return new ResponseEntity<>("Provider name can't be blank", HttpStatus.BAD_REQUEST);
        }

        if(request.getDate() == null){
            return new ResponseEntity<>("Date Can't be null", HttpStatus.BAD_REQUEST);
        }

        Provider provider = new Provider(request.getName());

        provider = providerRepository.save(provider);

        LocalTime startTime = request.getStartTime();
        LocalDate date = request.getDate();

        while(!startTime.isAfter(request.getEndTime().minusMinutes(15))){
            AppointmentSlot segment = new AppointmentSlot(provider, date, date.atTime(startTime), AvailabilityStatus.AVAILABLE);
            appointmentRepository.save(segment);
            startTime = startTime.plusMinutes(15);
        }

        return ResponseEntity.ok(provider);

    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<?> getProvider(@PathVariable Long providerId){
        Provider provider = providerRepository.getReferenceById(providerId);
        if (provider == null){
            return new ResponseEntity<>("No Provider Found", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(provider);

    }
}
