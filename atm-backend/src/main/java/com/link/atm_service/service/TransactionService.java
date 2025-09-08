package com.link.atm_service.service;

import com.link.atm_service.exception.AtmException;
import com.link.atm_service.dto.TransaccionDTO;
import com.link.atm_service.mapper.TransaccionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class TransactionService {

    private final TransaccionMapper transaccionMapper;

    public TransactionService(TransaccionMapper transaccionMapper) {
        this.transaccionMapper = transaccionMapper;
    }

    public TransaccionDTO crearTransaccionBase(String tipoTransaccion, String numeroTarjeta,
                                               String numeroCuenta, Double monto) {
        TransaccionDTO transaccion = new TransaccionDTO();
        transaccion.setTipoTransaccion(tipoTransaccion);
        transaccion.setNumeroTarjeta(numeroTarjeta);
        transaccion.setNumeroCuenta(numeroCuenta);
        transaccion.setMonto(monto);
        transaccion.setFechaTransaccion(LocalDateTime.now());
        transaccion.setEstado("PREPROCESS");
        return transaccion;
    }

    public void registrarTransaccionExitosa(TransaccionDTO transaccion, Double saldoAnterior, Double saldoPosterior) {
        transaccion.setSaldoAnterior(saldoAnterior);
        transaccion.setSaldoPosterior(saldoPosterior);
        transaccion.setEstado("EXITOSA");
        transaccionMapper.insertarTransaccion(transaccion);
        logAuditoria(transaccion);
    }

    public void registrarTransaccionFallida(TransaccionDTO transaccion, Exception exception) {
        transaccion.setEstado("FALLIDA");
        transaccion.setMotivoError(exception.getMessage());
        transaccion.setCodigoError(exception.getClass().getSimpleName());

        if (exception instanceof AtmException) {
            transaccion.setDetallesAdicionales("ERROR_NEGOCIO");
        } else {
            transaccion.setDetallesAdicionales("ERROR_TECNICO");
        }

        transaccionMapper.insertarTransaccion(transaccion);
        logAuditoria(transaccion);
    }

    private void logAuditoria(TransaccionDTO transaccion) {
        String tarjetaMasked = transaccion.getNumeroTarjeta().substring(transaccion.getNumeroTarjeta().length() - 4);

        log.info("AUDITORIA|{}|Tarjeta:{}|Cuenta:{}|Estado:{}|Monto:{}|SaldoAnt:{}|SaldoPost:{}|Error:{}",
                transaccion.getTipoTransaccion(),
                tarjetaMasked,
                transaccion.getNumeroCuenta(),
                transaccion.getEstado(),
                transaccion.getMonto(),
                transaccion.getSaldoAnterior(),
                transaccion.getSaldoPosterior(),
                transaccion.getMotivoError());
    }
}