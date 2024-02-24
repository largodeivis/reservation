package com.backend.reservation.service;

import com.backend.reservation.model.AppointmentSlot;

import java.util.List;

public interface IAppointmentService {
    List<AppointmentSlot> getProviderAppointments(long providerId);
    List<AppointmentSlot> getAllAvailableAppointments();
    String reserveAppointment(long appointmentId);
    String confirmAppointment(long appointmentId);
    List<AppointmentSlot> getAllReservedAppointments();
    List<AppointmentSlot> getAllConfirmedAppointments();
}
