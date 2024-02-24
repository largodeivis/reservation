package com.backend.reservation.service;

import com.backend.reservation.model.AppointmentSlot;
import com.backend.reservation.model.AvailabilityStatus;
import com.backend.reservation.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AppointmentServiceTests {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @Captor
    private ArgumentCaptor<AppointmentSlot> slotArgumentCaptor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getProviderAppointmentsTest() {
        AppointmentSlot slot = new AppointmentSlot();
        when(appointmentRepository.findByProviderId(anyLong())).thenReturn(Collections.singletonList(slot));

        List<AppointmentSlot> result = appointmentService.getProviderAppointments(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void getAllAvailableAppointmentsTest() {
        when(appointmentRepository.findByStatus(AvailabilityStatus.RESERVED)).thenReturn(Collections.emptyList());
        when(appointmentRepository.findAvailableAppointmentsAfter24Hours(any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        List<AppointmentSlot> result = appointmentService.getAllAvailableAppointments();
        assertTrue(result.isEmpty());
    }

    @Test
    public void reserveAppointmentTest() {
        AppointmentSlot slot = new AppointmentSlot();
        slot.setAppointmentTime(LocalDateTime.now().plusHours(25));
        slot.setStatus(AvailabilityStatus.AVAILABLE);

        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(slot));
        when(appointmentRepository.save(any(AppointmentSlot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String response = appointmentService.reserveAppointment(1L);
        assertTrue(response.contains("reserved"));
    }

    @Test
    public void reserveAppointment_UnableToReserve_Not24HoursInAdvanceTest() {
        // Given
        AppointmentSlot slot = new AppointmentSlot();
        slot.setAppointmentTime(LocalDateTime.now().plusHours(23)); // Less than 24 hours ahead
        slot.setStatus(AvailabilityStatus.AVAILABLE);

        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(slot));

        // When
        String response = appointmentService.reserveAppointment(1L);

        // Then
        assertTrue(response.contains("Unable to reserve"));
        verify(appointmentRepository, never()).save(any(AppointmentSlot.class));
    }

    @Test
    public void reserveAppointment_AlreadyReserved_ReturnsErrorMessage() {
        AppointmentSlot appointmentSlot = new AppointmentSlot();
        appointmentSlot.setStatus(AvailabilityStatus.RESERVED);
        appointmentSlot.setReservationTime(LocalDateTime.now());
        appointmentSlot.setAppointmentTime(LocalDateTime.now().plusHours(25));

        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(appointmentSlot));

        String result = appointmentService.reserveAppointment(1L);

        assertEquals("Appointment 1 is unavailable for reservation.", result);
        verify(appointmentRepository, never()).save(appointmentSlot);
    }

    @Test
    public void confirmAppointmentTest() {
        AppointmentSlot slot = new AppointmentSlot();
        slot.setReservationTime(LocalDateTime.now());
        slot.setStatus(AvailabilityStatus.RESERVED);

        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(slot));
        when(appointmentRepository.save(any(AppointmentSlot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String response = appointmentService.confirmAppointment(1L);
        assertTrue(response.contains("confirmed"));
    }

    @Test
    public void confirmAppointment_UnableToConfirm_NotReservedTest() {
        // Given
        AppointmentSlot slot = new AppointmentSlot();
        slot.setStatus(AvailabilityStatus.AVAILABLE); // The appointment is available but not reserved

        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(slot));

        // When
        String response = appointmentService.confirmAppointment(1L);

        // Then
        assertTrue(response.contains("Unable to confirm"));
        verify(appointmentRepository, never()).save(any(AppointmentSlot.class));
    }

    @Test
    public void confirmAppointment_UnableToConfirm_ExpiredReservationTest() {
        // Given
        AppointmentSlot slot = new AppointmentSlot();
        slot.setReservationTime(LocalDateTime.now().minusMinutes(31)); // Reservation time is more than 30 minutes ago
        slot.setStatus(AvailabilityStatus.RESERVED);

        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(slot));

        // When
        String response = appointmentService.confirmAppointment(1L);

        // Then
        assertTrue(response.contains("This appointment is expired"));
        verify(appointmentRepository).save(slotArgumentCaptor.capture());
        assertEquals(AvailabilityStatus.AVAILABLE, slotArgumentCaptor.getValue().getStatus());
        assertTrue(slotArgumentCaptor.getValue().getReservationTime().isEmpty());
    }

    @Test
    public void getAllReservedAppointmentsTest() {
        when(appointmentRepository.findByStatus(AvailabilityStatus.RESERVED)).thenReturn(Collections.emptyList());

        List<AppointmentSlot> result = appointmentService.getAllReservedAppointments();
        assertTrue(result.isEmpty());
    }

    @Test
    public void getAllConfirmedAppointmentsTest() {
        when(appointmentRepository.findByStatus(AvailabilityStatus.CONFIRMED)).thenReturn(Collections.emptyList());

        List<AppointmentSlot> result = appointmentService.getAllConfirmedAppointments();
        assertTrue(result.isEmpty());
    }
}