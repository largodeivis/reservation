package com.backend.reservation.controller;

import com.backend.reservation.model.AppointmentSlot;
import com.backend.reservation.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AppointmentController {
    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService){
        this.appointmentService = appointmentService;
    }

    @GetMapping("/appointments/{providerId}")
    public ResponseEntity<?> getProviderAppointments(@PathVariable Long providerId){
        List<AppointmentSlot> segments = appointmentService.getProviderAppointments(providerId);
        if(segments.isEmpty()){
            return new ResponseEntity<>("No availability for this provider.", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(segments);
    }

    @GetMapping("/appointments")
    public ResponseEntity<?> getAllAvailableAppointments(){
        List<AppointmentSlot> availableAppointments = appointmentService.getAllAvailableAppointments();
        if(availableAppointments.isEmpty()){
            return ResponseEntity.ok("No available appointments");
        }
        return ResponseEntity.ok(availableAppointments);
    }

    @PostMapping("/appointments/reserve/{appointmentId}")
    public ResponseEntity<String> reserveAppointment(@PathVariable long appointmentId){
        return ResponseEntity.ok(appointmentService.reserveAppointment(appointmentId));
    }

    @PostMapping("/appointments/confirm/{appointmentId}")
    public ResponseEntity<String> confirmAppointment(@PathVariable long appointmentId){
        return ResponseEntity.ok(appointmentService.confirmAppointment(appointmentId));
    }

    @GetMapping("/appointments/reserve")
    public ResponseEntity<?> getAllReservedAppointments(){
        List<AppointmentSlot> reservedAppointments = appointmentService.getAllReservedAppointments();
        if(reservedAppointments.isEmpty()){
            return ResponseEntity.ok("No reserved appointments");
        }
        return ResponseEntity.ok(reservedAppointments);

    }

    @GetMapping("/appointments/confirm")
    public ResponseEntity<?> getAllConfirmedAppointments(){
        List<AppointmentSlot> confirmedAppointments = appointmentService.getAllConfirmedAppointments();
        if(confirmedAppointments.isEmpty()){
            return ResponseEntity.ok("No reserved appointments");
        }
        return ResponseEntity.ok(confirmedAppointments);

    }
}
