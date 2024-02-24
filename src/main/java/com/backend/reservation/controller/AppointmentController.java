package com.backend.reservation.controller;

import com.backend.reservation.model.AppointmentSlot;
import com.backend.reservation.model.AvailabilityStatus;
import com.backend.reservation.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
            if(LocalDateTime.now().isAfter(appointment.getReservationTime().plusMinutes(30))){
                appointment.setStatus(AvailabilityStatus.AVAILABLE);
                appointment.setReservationTime(null);
                appointmentRepository.save(appointment);
            }
        }

        List<AppointmentSlot> availableAppointments = appointmentRepository.findAvailableAppointmentsAfter24Hours(LocalDateTime.now().plusHours(24));
        if(availableAppointments.isEmpty()){
            return ResponseEntity.ok("No available appointments");
        }
        return ResponseEntity.ok(availableAppointments);
    }

    @PostMapping("/appointments/reserve/{appointmentId}")
    public ResponseEntity<?> reserveAppointment(@PathVariable long appointmentId){
        Optional<AppointmentSlot> appointmentSlot = appointmentRepository.findById(appointmentId);
        if(appointmentSlot.isPresent()) {
            AppointmentSlot appointment = appointmentSlot.get();
            if(appointment.getAppointmentTime().isBefore(LocalDateTime.now().plusHours(24))){
                return ResponseEntity.ok("Unable to reserve. Reservations have to be made 24 hours in advance.");
            } else {
                if (appointment.getStatus().equals(AvailabilityStatus.AVAILABLE)) {
                    appointment.setStatus(AvailabilityStatus.RESERVED);
                    appointment.setReservationTime(LocalDateTime.now());
                    appointmentRepository.save(appointment);
                    return ResponseEntity.ok("Appointment " + appointmentId + " reserved.");
                } else if ((appointment.getStatus().equals(AvailabilityStatus.RESERVED)) &&
                        (LocalDateTime.now().isAfter(appointment.getReservationTime().plusMinutes(30)))) {
                    appointment.setReservationTime(LocalDateTime.now());
                    appointmentRepository.save(appointment);
                    return ResponseEntity.ok("Appointment " + appointmentId + " reserved.");
                } else {
                    return ResponseEntity.ok("Appointment " + appointmentId + " is unavailable for reservation.");
                }
            }
        } else {
            return new ResponseEntity<>("This appointment doesn't exist.", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/appointments/confirm/{appointmentId}")
    public ResponseEntity<?> confirmAppointment(@PathVariable long appointmentId){
        Optional<AppointmentSlot> appointmentSlot = appointmentRepository.findById(appointmentId);

        if(appointmentSlot.isPresent()) {
            AppointmentSlot appointment = appointmentSlot.get();
            if (appointment.getStatus().equals(AvailabilityStatus.AVAILABLE)) {
                return ResponseEntity.ok("Unable to confirm " + appointmentId + ". It must be reserved first.");
            } else if (appointment.getStatus().equals(AvailabilityStatus.RESERVED)) {
                if ((LocalDateTime.now().isAfter(appointment.getReservationTime().plusMinutes(30)))) {
                    appointment.setStatus(AvailabilityStatus.AVAILABLE);
                    appointment.setReservationTime(null);
                    appointmentRepository.save(appointment);
                    return ResponseEntity.ok("Unable to confirm " + appointmentId + ".This appointment is expired.");
                } else {
                    appointment.setStatus(AvailabilityStatus.CONFIRMED);
                    appointmentRepository.save(appointment);
                    return ResponseEntity.ok("Appointment " + appointmentId + " confirmed!");
                }
            } else {
                return ResponseEntity.ok("Appointment " + appointmentId + " is already confirmed.");
            }
        } else {
            return new ResponseEntity<>("This appointment doesn't exist.", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/appointments/reserve")
    public ResponseEntity<?> getAllReservedAppointments(){
        List<AppointmentSlot> reservedAppointments = appointmentRepository.findByStatus(AvailabilityStatus.RESERVED);
        if(reservedAppointments.isEmpty()){
            return ResponseEntity.ok("No reserved appointments");
        }
        return ResponseEntity.ok(reservedAppointments);

    }

    @GetMapping("/appointments/confirm")
    public ResponseEntity<?> getAllConfirmedAppointments(){
        List<AppointmentSlot> confirmedAppointments = appointmentRepository.findByStatus(AvailabilityStatus.CONFIRMED);
        if(confirmedAppointments.isEmpty()){
            return ResponseEntity.ok("No reserved appointments");
        }
        return ResponseEntity.ok(confirmedAppointments);

    }
}
