package com.backend.reservation.service;

import com.backend.reservation.model.AppointmentSlot;
import com.backend.reservation.model.Provider;
import com.backend.reservation.repository.AppointmentRepository;
import com.backend.reservation.repository.ProviderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProviderServiceTests {

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private ProviderService providerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addProviderAndAvailability_createsProviderAndAppointments() {
        // Arrange
        String providerName = "Test Provider";
        LocalDate date = LocalDate.now();
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);
        Provider savedProvider = new Provider(providerName);

        when(providerRepository.save(any(Provider.class))).thenReturn(savedProvider);
        when(appointmentRepository.save(any(AppointmentSlot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Provider result = providerService.addProviderAndAvailability(providerName, date, startTime, endTime);

        // Assert
        verify(providerRepository, times(1)).save(any(Provider.class));
        verify(appointmentRepository, times(4)).save(any(AppointmentSlot.class)); // Because it's 9:00 to 10:00, 15 minutes interval creates 4 slots
        assertNotNull(result);
    }

    @Test
    void getProvider_returnsProviderIfExists() {
        // Arrange
        long providerId = 1L;
        Provider provider = new Provider("Existing Provider");
        when(providerRepository.findById(providerId)).thenReturn(Optional.of(provider));

        // Act
        Optional<Provider> result = providerService.getProvider(providerId);

        // Assert
        verify(providerRepository, times(1)).findById(providerId);
        assertTrue(result.isPresent());
        assertEquals("Existing Provider", result.get().getName());
    }
}
