package com.backend.reservation.controller;

import com.backend.reservation.model.AppointmentSlot;
import com.backend.reservation.model.AvailabilityStatus;
import com.backend.reservation.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping("/appointments/{providerId}")
    public ResponseEntity<?> getProviderAppointments(@PathVariable Long providerId){
        List<AppointmentSlot> segments = appointmentRepository.findByProviderId(providerId);
        if(segments.isEmpty()){
            return new ResponseEntity<>("No availability for this provider.", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(segments);
    }

    @GetMapping("/appointments/{date}")
    public ResponseEntity<?> getAppointmentsByDate(@PathVariable LocalDate date){
        List<AppointmentSlot> segments = appointmentRepository.findByDate(date);
        if(segments.isEmpty()){
            return new ResponseEntity<>("No availability for this Date: " + date.toString(), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(segments);
    }


    @GetMapping("/appointments")
    public ResponseEntity<?> getAllAvailableAppointments(){
        List<AppointmentSlot> reservedAppointments = appointmentRepository.findByStatus(AvailabilityStatus.RESERVED);
        for(AppointmentSlot appointment : reservedAppointments){
            if(LocalDateTime.now().isAfter(appointment.getAppointmentTime().plusMinutes(30))){
                appointment.setStatus(AvailabilityStatus.AVAILABLE);
                appointmentRepository.save(appointment);
            }
        }

        List<AppointmentSlot> availableAppointments = appointmentRepository.findByStatus(AvailabilityStatus.AVAILABLE);
        if(availableAppointments.isEmpty()){
            return ResponseEntity.ok("No available appointments");
        }
        return ResponseEntity.ok(availableAppointments);
    }
}
