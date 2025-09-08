package com.link.atm_service.service;

import com.link.atm_service.dto.CuentaDTO;
import com.link.atm_service.dto.TransaccionDTO;
import com.link.atm_service.exception.*;
import com.link.atm_service.mapper.CuentaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AtmServiceTest {

    @Mock
    private CuentaMapper cuentaMapper;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private AtmService atmService;

    @BeforeEach
    void setUp() {
        TransaccionDTO transaccionMock = new TransaccionDTO();
        when(transactionService.crearTransaccionBase(anyString(), anyString(), anyString(), anyDouble()))
                .thenReturn(transaccionMock);
    }

    @Test
    void testExtraerExitoso() {
        CuentaDTO cuenta = new CuentaDTO();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta("0001");
        cuenta.setSaldo(5000.0);
        cuenta.setActiva(true);

        when(cuentaMapper.obtenerCuentaPorNumero("0001")).thenReturn(cuenta);
        when(cuentaMapper.obtenerCuentaPorTarjetaYNumero("1234567890123456", "0001")).thenReturn(cuenta);

        boolean resultado = atmService.extraer("1234567890123456", "0001", 1000.0);

        assertTrue(resultado);
        verify(cuentaMapper).actualizarSaldo(any(CuentaDTO.class));
    }

    @Test
    void testExtraerSaldoInsuficiente() {
        CuentaDTO cuenta = new CuentaDTO();
        cuenta.setNumeroCuenta("0004");
        cuenta.setSaldo(100.0);
        cuenta.setActiva(true);

        when(cuentaMapper.obtenerCuentaPorNumero("0004")).thenReturn(cuenta);
        when(cuentaMapper.obtenerCuentaPorTarjetaYNumero("1234567890123456", "0004")).thenReturn(cuenta);

        assertThrows(SaldoInsuficienteException.class, () -> {
            atmService.extraer("1234567890123456", "0004", 200.0);
        });
    }

    @Test
    void testExtraerCuentaInactiva() {
        CuentaDTO cuenta = new CuentaDTO();
        cuenta.setNumeroCuenta("0003");
        cuenta.setSaldo(1000.0);
        cuenta.setActiva(false);

        when(cuentaMapper.obtenerCuentaPorNumero("0003")).thenReturn(cuenta);
        when(cuentaMapper.obtenerCuentaPorTarjetaYNumero(anyString(), eq("0003"))).thenReturn(cuenta);

        assertThrows(CuentaInactivaException.class, () -> {
            atmService.extraer("1111222233334444", "0003", 100.0);
        });
    }

    @Test
    void testConsultarSaldoTarjetaInactiva() {
        CuentaDTO cuenta = new CuentaDTO();
        cuenta.setNumeroCuenta("0003");
        cuenta.setSaldo(1000.0);
        cuenta.setActiva(true);

        when(cuentaMapper.obtenerCuentaPorNumero("0003")).thenReturn(cuenta);
        when(cuentaMapper.existeTarjeta("1111222233334444")).thenReturn(true);
        when(cuentaMapper.obtenerCuentaPorTarjetaYNumero("1111222233334444", "0003")).thenReturn(null);

        TransaccionDTO transaccionMock = new TransaccionDTO();
        when(transactionService.crearTransaccionBase(eq("CONSULTA_SALDO"), eq("1111222233334444"), eq("0003"), isNull()))
                .thenReturn(transaccionMock);

        assertThrows(TarjetaNoAsociadaException.class, () -> {
            atmService.consultarSaldo("1111222233334444", "0003");
        });
    }
}