package com.link.atm_service.service;

import com.link.atm_service.dto.CuentaDTO;
import com.link.atm_service.exception.*;
import com.link.atm_service.mapper.CuentaMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AtmServiceTest {

    @Mock
    private CuentaMapper cuentaMapper;

    @InjectMocks
    private AtmService atmService;

    // 1. TEST DE ÉXITO PARA EXTRAER - Tarjeta 1234567890123456 con Cuenta 0001 (saldo 5000)
    @Test
    void testExtraerExitoso() {
        // Given - Tarjeta activa 1234567890123456 con cuenta 0001 (saldo 5000)
        CuentaDTO cuenta = new CuentaDTO();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta("0001");
        cuenta.setSaldo(5000.0);
        cuenta.setActiva(true);

        when(cuentaMapper.obtenerCuentaPorNumero("0001")).thenReturn(cuenta);
        when(cuentaMapper.obtenerCuentaPorTarjetaYNumero("1234567890123456", "0001")).thenReturn(cuenta);

        // When - Extraer 1000 de cuenta con 5000
        boolean resultado = atmService.extraer("1234567890123456", "0001", 1000.0);

        // Then
        assertTrue(resultado);
        verify(cuentaMapper).actualizarSaldo(any(CuentaDTO.class));
    }

    // 2. TEST DE ERROR - SALDO INSUFICIENTE - Cuenta 0004 (saldo 100)
    @Test
    void testExtraerSaldoInsuficiente() {
        // Given - Cuenta 0004 con solo 100 de saldo
        CuentaDTO cuenta = new CuentaDTO();
        cuenta.setNumeroCuenta("0004");
        cuenta.setSaldo(100.0);
        cuenta.setActiva(true);

        when(cuentaMapper.obtenerCuentaPorNumero("0004")).thenReturn(cuenta);
        when(cuentaMapper.obtenerCuentaPorTarjetaYNumero("1234567890123456", "0004")).thenReturn(cuenta);

        // When & Then - Intentar extraer 200 de cuenta con 100
        assertThrows(SaldoInsuficienteException.class, () -> {
            atmService.extraer("1234567890123456", "0004", 200.0);
        });
    }

    // 3. TEST DE ERROR - CUENTA INACTIVA - Cuenta 0003 (inactiva)
    @Test
    void testExtraerCuentaInactiva() {
        // Given - Cuenta 0003 inactiva
        CuentaDTO cuenta = new CuentaDTO();
        cuenta.setNumeroCuenta("0003");
        cuenta.setSaldo(1000.0);
        cuenta.setActiva(false);

        when(cuentaMapper.obtenerCuentaPorNumero("0003")).thenReturn(cuenta);

        // When & Then - Intentar extraer de cuenta inactiva
        assertThrows(CuentaInactivaException.class, () -> {
            atmService.extraer("1111222233334444", "0003", 100.0);
        });
    }

    // TEST ADICIONAL: Tarjeta inactiva 1111222233334444
    @Test
    void testExtraerTarjetaInactiva() {
        // Given - Tarjeta 1111222233334444 inactiva
        CuentaDTO cuenta = new CuentaDTO();
        cuenta.setNumeroCuenta("0003");
        cuenta.setSaldo(1000.0);
        cuenta.setActiva(true); // Cuenta activa pero tarjeta inactiva

        when(cuentaMapper.obtenerCuentaPorNumero("0003")).thenReturn(cuenta);
        when(cuentaMapper.existeTarjeta("1111222233334444")).thenReturn(true);

        // Cuando la tarjeta está inactiva, obtenerCuentaPorTarjetaYNumero debería retornar null
        when(cuentaMapper.obtenerCuentaPorTarjetaYNumero("1111222233334444", "0003")).thenReturn(null);

        // When & Then - Verificar que se lanza excepción de tarjeta no asociada
        assertThrows(TarjetaNoAsociadaException.class, () -> {
            atmService.consultarSaldo("1111222233334444", "0003");
        });
    }
}