package com.link.atm_service.controller;

import com.link.atm_service.dto.LoginResponse;
import com.link.atm_service.service.AtmService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AtmControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AtmService atmService;

    @InjectMocks
    private AtmController atmController;

    @Test
    void testLoginEndpointExitoso() throws Exception {
        // Configurar MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(atmController).build();

        when(atmService.login("1234567890123456"))
                .thenReturn(new LoginResponse(true, "Login exitoso", "1234567890123456"));

        mockMvc.perform(get("/api/login")
                        .param("numeroTarjeta", "1234567890123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Login exitoso"));
    }

    @Test
    void testLoginEndpointTarjetaInactiva() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(atmController).build();

        when(atmService.login("1111222233334444"))
                .thenReturn(new LoginResponse(false, "Tarjeta invalida o inactiva", "1111222233334444"));

        mockMvc.perform(get("/api/login")
                        .param("numeroTarjeta", "1111222233334444"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("Tarjeta invalida o inactiva"));
    }

    @Test
    void testExtraerEndpointExitoso() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(atmController).build();

        when(atmService.extraer("1234567890123456", "0001", 1000.0)).thenReturn(true);

        mockMvc.perform(post("/api/extraer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tarjeta\": \"1234567890123456\", \"cbu\": \"0001\", \"monto\": 1000.0}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Retire su dinero"));
    }

    @Test
    void testConsultarSaldoEndpoint() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(atmController).build();

        when(atmService.consultarSaldo("1234567890123456", "0001")).thenReturn(5000.0);

        mockMvc.perform(get("/api/saldo")
                        .param("tarjeta", "1234567890123456")
                        .param("cbu", "0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(5000.0));
    }
}