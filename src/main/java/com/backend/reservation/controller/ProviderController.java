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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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

        if(request.getDate() == null){
            return new ResponseEntity<>("Date Can't be null", HttpStatus.BAD_REQUEST);
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

    @GetMapping("/availability/{providerId}")
    public ResponseEntity<?> getProviderAvailability(@PathVariable Long providerId){
        List<AvailabilitySegment> segments = availabilitySegmentRepository.findByProviderId(providerId);
        if(segments.isEmpty()){
            return new ResponseEntity<>("No availability for this provider.", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(segments);
    }

    @GetMapping("/availabilities/{date}")
    public ResponseEntity<?> getAvailabilityByDate(@PathVariable LocalDate date){
        List<AvailabilitySegment> segments = availabilitySegmentRepository.findByDate(date);
        if(segments.isEmpty()){
            return new ResponseEntity<>("No availability for this Date: " + date.toString(), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(segments);
    }


    @GetMapping("/availabilities")
    public ResponseEntity<?> getAllAvailability(){
        return ResponseEntity.ok(availabilitySegmentRepository.findAll());
    }







}
