package com.backend.reservation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.hamcrest.Matchers.*;

import com.backend.reservation.model.AppointmentSlot;
import com.backend.reservation.model.AvailabilityStatus;
import com.backend.reservation.model.Provider;
import com.backend.reservation.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@WebMvcTest(AppointmentController.class)
public class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    private AppointmentSlot createMockAppointmentSlot() {
        Provider provider = new Provider();
        return new AppointmentSlot(provider, LocalDate.now(), LocalDateTime.now(), AvailabilityStatus.AVAILABLE);
    }

    @Test
    public void getProviderAppointments_ReturnsAppointmentsList() throws Exception {
        AppointmentSlot slot = new AppointmentSlot();
        List<AppointmentSlot> slots = Arrays.asList(slot);

        given(appointmentService.getProviderAppointments(anyLong())).willReturn(slots);

        mockMvc.perform(get("/appointments/{providerId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void getProviderAppointments_ReturnsNotFound() throws Exception {
        given(appointmentService.getProviderAppointments(anyLong())).willReturn(Arrays.asList());

        mockMvc.perform(get("/appointments/{providerId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("No availability for this provider.")));
    }

    @Test
    public void getAllAvailableAppointments_ReturnsEmptyMessage() throws Exception {
        given(appointmentService.getAllAvailableAppointments()).willReturn(Arrays.asList());

        mockMvc.perform(get("/appointments"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No available appointments")));
    }

    @Test
    public void reserveAppointment_ReturnsSuccessMessage() throws Exception {
        String successMessage = "Appointment reserved successfully";
        given(appointmentService.reserveAppointment(anyLong())).willReturn(successMessage);

        mockMvc.perform(post("/appointments/reserve/{appointmentId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage));
    }

    @Test
    public void confirmAppointment_ReturnsSuccessMessage() throws Exception {
        String confirmationMessage = "Appointment confirmed successfully";
        given(appointmentService.confirmAppointment(anyLong())).willReturn(confirmationMessage);

        mockMvc.perform(post("/appointments/confirm/{appointmentId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string(confirmationMessage));
    }

    @Test
    public void getAllReservedAppointments_ReturnsAppointmentsList() throws Exception {
        List<AppointmentSlot> reservedSlots = Arrays.asList(createMockAppointmentSlot());

        given(appointmentService.getAllReservedAppointments()).willReturn(reservedSlots);

        mockMvc.perform(get("/appointments/reserve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void getAllConfirmedAppointments_ReturnsAppointmentsList() throws Exception {
        List<AppointmentSlot> confirmedSlots = Arrays.asList(createMockAppointmentSlot());

        given(appointmentService.getAllConfirmedAppointments()).willReturn(confirmedSlots);

        mockMvc.perform(get("/appointments/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}

