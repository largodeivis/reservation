package com.backend.reservation.controller;

import com.backend.reservation.model.Provider;
import com.backend.reservation.model.ProviderAvailabilityRequest;
import com.backend.reservation.service.ProviderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(ProviderController.class)
public class ProviderControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProviderService providerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void addProviderAndAvailability_ValidRequest_ReturnsProvider() throws Exception {
        ProviderAvailabilityRequest request = new ProviderAvailabilityRequest("Test Provider", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(17, 0));
        Provider provider = new Provider(); // Assume Provider has an appropriate constructor

        given(providerService.addProviderAndAvailability(any(), any(), any(), any())).willReturn(provider);

        mockMvc.perform(post("/provider")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void addProviderAndAvailability_InvalidStartTime_ReturnsBadRequest() throws Exception {
        ProviderAvailabilityRequest request = new ProviderAvailabilityRequest("Test Provider", LocalDate.now(), LocalTime.of(17, 0), LocalTime.of(9, 0));

        mockMvc.perform(post("/provider")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Start Time can't be before end time.")));
    }

    @Test
    public void getProvider_Found_ReturnsProvider() throws Exception {
        Provider provider = new Provider(); // Assume Provider has an appropriate constructor
        given(providerService.getProvider(anyLong())).willReturn(Optional.of(provider));

        mockMvc.perform(get("/provider/{providerId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getProvider_NotFound_ReturnsNotFound() throws Exception {
        given(providerService.getProvider(anyLong())).willReturn(Optional.empty());

        mockMvc.perform(get("/provider/{providerId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("No Provider Found")));
    }
}