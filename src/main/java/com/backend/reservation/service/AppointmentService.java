package com.backend.reservation.service;

import com.backend.reservation.model.AppointmentSlot;
import com.backend.reservation.model.AvailabilityStatus;
import com.backend.reservation.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService implements IAppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<AppointmentSlot> getProviderAppointments(long providerId){
        List<AppointmentSlot> segments = appointmentRepository.findByProviderId(providerId);
        return segments;
    }

    public List<AppointmentSlot> getAllAvailableAppointments(){
        List<AppointmentSlot> reservedAppointments = appointmentRepository.findByStatus(AvailabilityStatus.RESERVED);
        for(AppointmentSlot appointment : reservedAppointments){
            if(!appointment.getReservationTime().isPresent() || LocalDateTime.now().isAfter(appointment.getReservationTime().get().plusMinutes(30))){
                appointment.setStatus(AvailabilityStatus.AVAILABLE);
                appointment.setReservationTime(Optional.empty());
                appointmentRepository.save(appointment);
            }
        }

        List<AppointmentSlot> availableAppointments = appointmentRepository.findAvailableAppointmentsAfter24Hours(LocalDateTime.now().plusHours(24));
        return availableAppointments;
    }

    public String reserveAppointment(long appointmentId){
        Optional<AppointmentSlot> appointmentSlot = appointmentRepository.findById(appointmentId);
        if(appointmentSlot.isPresent()) {
            AppointmentSlot appointment = appointmentSlot.get();
            if(appointment.getAppointmentTime().isBefore(LocalDateTime.now().plusHours(24))){
                return "Unable to reserve. Reservations have to be made 24 hours in advance.";
            } else {
                if (appointment.getStatus().equals(AvailabilityStatus.AVAILABLE)) {
                    appointment.setStatus(AvailabilityStatus.RESERVED);
                    appointment.setReservationTime(Optional.of(LocalDateTime.now()));
                    appointmentRepository.save(appointment);
                    return "Appointment " + appointmentId + " reserved.";
                } else if ((appointment.getStatus().equals(AvailabilityStatus.RESERVED)) &&
                        (LocalDateTime.now().isAfter(appointment.getReservationTime().get().plusMinutes(30)))) {
                    appointment.setReservationTime(Optional.of(LocalDateTime.now()));
                    appointmentRepository.save(appointment);
                    return "Appointment " + appointmentId + " reserved.";
                } else {
                    return "Appointment " + appointmentId + " is unavailable for reservation.";
                }
            }
        } else {
            return "This appointment doesn't exist.";
        }
    }

    public String confirmAppointment(long appointmentId){
        Optional<AppointmentSlot> appointmentSlot = appointmentRepository.findById(appointmentId);

        if(appointmentSlot.isPresent()) {
            AppointmentSlot appointment = appointmentSlot.get();
            if (appointment.getStatus().equals(AvailabilityStatus.AVAILABLE)) {
                return "Unable to confirm " + appointmentId + ". It must be reserved first.";
            } else if (appointment.getStatus().equals(AvailabilityStatus.RESERVED)) {
                if (appointment.getReservationTime().isEmpty() || (LocalDateTime.now().isAfter(appointment.getReservationTime().get().plusMinutes(30)))) {
                    appointment.setStatus(AvailabilityStatus.AVAILABLE);
                    appointment.setReservationTime(Optional.empty());
                    appointmentRepository.save(appointment);
                    return "Unable to confirm " + appointmentId + ".This appointment is expired.";
                } else {
                    appointment.setStatus(AvailabilityStatus.CONFIRMED);
                    appointmentRepository.save(appointment);
                    return "Appointment " + appointmentId + " confirmed!";
                }
            } else {
                return "Appointment " + appointmentId + " is already confirmed.";
            }
        } else {
            return "This appointment doesn't exist.";
        }
    }

    public List<AppointmentSlot> getAllReservedAppointments() {
        return appointmentRepository.findByStatus(AvailabilityStatus.RESERVED);
    }

    public List<AppointmentSlot> getAllConfirmedAppointments() {
        return appointmentRepository.findByStatus(AvailabilityStatus.CONFIRMED);
    }
}
