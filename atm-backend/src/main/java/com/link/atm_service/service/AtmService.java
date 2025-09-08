package com.link.atm_service.service;

import com.link.atm_service.dto.CuentaDTO;
import com.link.atm_service.dto.LoginResponse;
import com.link.atm_service.dto.TransaccionDTO;
import com.link.atm_service.exception.*;
import com.link.atm_service.mapper.CuentaMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class AtmService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtmService.class);

    private final CuentaMapper cuentaMapper;
    private final TransactionService transactionService;

    public AtmService(CuentaMapper cuentaMapper, TransactionService transactionService) {
        this.cuentaMapper = cuentaMapper;
        this.transactionService = transactionService;
    }

    public LoginResponse login(String numeroTarjeta) {

        TransaccionDTO transaccion = transactionService.crearTransaccionBase("LOGIN", numeroTarjeta, null, null);
        try {
            boolean tarjetaExiste = cuentaMapper.existeTarjeta(numeroTarjeta);
            if (!tarjetaExiste) {
                throw new TarjetaNoExisteException("La tarjeta " + numeroTarjeta + " no existe");
            }

            boolean tarjetaActiva = cuentaMapper.estaTarjetaActiva(numeroTarjeta);
            if (!tarjetaActiva) {
                throw new TarjetaInactivaException("La tarjeta esta inactiva");
            }

            CuentaDTO cuenta = cuentaMapper.obtenerPorTarjeta(numeroTarjeta);
            if (cuenta == null) {
                throw new TarjetaNoAsociadaException("La tarjeta no tiene cuentas asociadas");
            }
            if (cuenta != null && cuenta.isActiva()) {
                transaccion.setDetallesAdicionales("LOGIN_EXITOSO");
                transactionService.registrarTransaccionExitosa(transaccion, null, null);
                return new LoginResponse(true, "Login exitoso", numeroTarjeta);
            } else {
                throw new TarjetaInactivaException("Tarjeta invalida o inactiva");
            }

        } catch (Exception e) {
            transactionService.registrarTransaccionFallida(transaccion, e);
            throw e;
        }
    }

    public boolean extraer(String numeroTarjeta, String numeroCuenta, double monto) {
        TransaccionDTO transaccion = transactionService.crearTransaccionBase("EXTRACCION", numeroTarjeta, numeroCuenta, monto);
        Double saldoAnterior = null;

        try {
            if (monto <= 0) {
                throw new MontoNegativoException("El monto a extraer debe ser mayor a cero");
            }

            CuentaDTO cuenta = cuentaMapper.obtenerCuentaPorTarjetaYNumero(numeroTarjeta, numeroCuenta);

            if (cuenta == null) {
                boolean cuentaExiste = cuentaMapper.existeCuenta(numeroCuenta);
                if (!cuentaExiste) {
                    throw new CuentaNoExisteException("La cuenta " + numeroCuenta + " no existe");
                }

                boolean tarjetaAsociada = cuentaMapper.existeAsociacionTarjetaCuenta(numeroTarjeta, numeroCuenta);
                if (!tarjetaAsociada) {
                    throw new TarjetaNoAsociadaException("La tarjeta no esta asociada a la cuenta");
                }

                throw new TarjetaInactivaException("La tarjeta esta inactiva");
            }

            if (!cuenta.isActiva()) {
                throw new CuentaInactivaException("La cuenta " + numeroCuenta + " esta inactiva");
            }

            if (cuenta.getSaldo() < monto) {
                throw new SaldoInsuficienteException("Saldo insuficiente");
            }

            saldoAnterior = cuenta.getSaldo();
            cuenta.setSaldo(cuenta.getSaldo() - monto);
            cuentaMapper.actualizarSaldo(cuenta);

            transactionService.registrarTransaccionExitosa(transaccion, saldoAnterior, cuenta.getSaldo());
            return true;

        } catch (Exception e) {
            transactionService.registrarTransaccionFallida(transaccion, e);
            throw e;
        }
    }

    public boolean depositar(String numeroTarjeta, String numeroCuenta, double monto) {
        TransaccionDTO transaccion = transactionService.crearTransaccionBase("DEPOSITO", numeroTarjeta, numeroCuenta, monto);
        Double saldoAnterior = null;

        try {
            if (monto <= 0) {
                throw new MontoNegativoException("El monto a depositar debe ser mayor a cero");
            }

            boolean tarjetaExiste = cuentaMapper.existeTarjeta(numeroTarjeta);
            if (!tarjetaExiste) {
                throw new TarjetaNoExisteException("La tarjeta " + numeroTarjeta + " no existe");
            }

            CuentaDTO cuenta = cuentaMapper.obtenerCuentaPorNumero(numeroCuenta);

            if (cuenta == null) {
                throw new CuentaNoExisteException("La cuenta " + numeroCuenta + " no existe");
            }
            if (!cuenta.isActiva()) {
                throw new CuentaInactivaException("La cuenta esta inactiva");
            }

            saldoAnterior = cuenta.getSaldo();
            cuenta.setSaldo(cuenta.getSaldo() + monto);
            cuentaMapper.actualizarSaldo(cuenta);

            transactionService.registrarTransaccionExitosa(transaccion, saldoAnterior, cuenta.getSaldo());
            return true;

        } catch (Exception e) {
            transactionService.registrarTransaccionFallida(transaccion, e);
            throw e;
        }
    }

    public double consultarSaldo(String numeroTarjeta, String numeroCuenta) {
        TransaccionDTO transaccion = transactionService.crearTransaccionBase("CONSULTA_SALDO", numeroTarjeta, numeroCuenta, null);

        try {
            if (!cuentaMapper.existeTarjeta(numeroTarjeta)) {
                throw new TarjetaNoExisteException("La tarjeta " + numeroTarjeta + " no existe");
            }

            CuentaDTO cuentaSola = cuentaMapper.obtenerCuentaPorNumero(numeroCuenta);
            if (cuentaSola == null) {
                throw new CuentaNoExisteException("La cuenta " + numeroCuenta + " no existe");
            }
            if (!cuentaSola.isActiva()) {
                throw new CuentaInactivaException("La cuenta esta inactiva");
            }

            CuentaDTO cuentaAsociada = cuentaMapper.obtenerCuentaPorTarjetaYNumero(numeroTarjeta, numeroCuenta);
            if (cuentaAsociada == null) {
                throw new TarjetaNoAsociadaException("La tarjeta " + numeroTarjeta + " no esta asociada a la cuenta " + numeroCuenta);
            }

            transactionService.registrarTransaccionExitosa(transaccion, cuentaAsociada.getSaldo(), cuentaAsociada.getSaldo());
            return cuentaAsociada.getSaldo();

        } catch (Exception e) {
            transactionService.registrarTransaccionFallida(transaccion, e);
            throw e;
        }
    }

}
