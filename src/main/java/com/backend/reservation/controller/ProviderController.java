package com.backend.reservation.controller;

import com.backend.reservation.model.Provider;
import com.backend.reservation.model.ProviderAvailabilityRequest;
import com.backend.reservation.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class ProviderController {
    private ProviderService providerService;

    @Autowired
    public ProviderController(ProviderService providerService){
        this.providerService = providerService;
    }

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

        Provider provider = providerService.addProviderAndAvailability(request.getName(), request.getDate(), request.getStartTime(), request.getEndTime());

        return ResponseEntity.ok(provider);

    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<?> getProvider(@PathVariable Long providerId){
        Optional<Provider> provider = providerService.getProvider(providerId);
        if (provider.isEmpty()){
            return new ResponseEntity<>("No Provider Found", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(provider);

    }
}
