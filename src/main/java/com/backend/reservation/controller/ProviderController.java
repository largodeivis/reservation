package com.backend.reservation.controller;

import com.backend.reservation.model.AvailabilitySegment;
import com.backend.reservation.model.AvailabilityStatus;
import com.backend.reservation.model.Provider;
import com.backend.reservation.model.ProviderAvailabilityRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.backend.reservation.repository.AvailabilitySegmentRepository;
import com.backend.reservation.repository.ProviderRepository;

import java.time.LocalTime;

@RestController
public class ProviderController {

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private AvailabilitySegmentRepository availabilitySegmentRepository;

    @PostMapping("/provider")
    public ResponseEntity<?> addProviderAndAvailability(@RequestBody ProviderAvailabilityRequest request){
        if(request.getStartTime().isAfter(request.getEndTime())){
            return new ResponseEntity<>("Start Time can't be before end time.", HttpStatus.BAD_REQUEST);
        }

        if(request.getName().isBlank()){
            return new ResponseEntity<>("Provider name can't be blank", HttpStatus.BAD_REQUEST);
        }

        Provider provider = new Provider(request.getName());

        provider = providerRepository.save(provider);


        LocalTime startTime = request.getStartTime();

        while(!startTime.isAfter(request.getEndTime())){
            AvailabilitySegment segment = new AvailabilitySegment(provider,request.getDate(), startTime, AvailabilityStatus.AVAILABLE);
            availabilitySegmentRepository.save(segment);
            startTime = startTime.plusMinutes(15);
        }

        return ResponseEntity.ok(provider);

    }



}
