package com.link.atm_service.service;

import com.link.atm_service.dto.CuentaDTO;
import com.link.atm_service.dto.LoginResponse;
import com.link.atm_service.exception.*;
import com.link.atm_service.mapper.CuentaMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AtmService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtmService.class);

    private final CuentaMapper cuentaMapper;

    public AtmService(CuentaMapper cuentaMapper) {
        this.cuentaMapper = cuentaMapper;
    }

    public LoginResponse login(String numeroTarjeta) {
        CuentaDTO cuenta = cuentaMapper.obtenerPorTarjeta(numeroTarjeta);

        LOGGER.info("Cuenta: "+ cuenta);
        if (cuenta != null && cuenta.isActiva()) {
            return new LoginResponse(true, "Login exitoso", numeroTarjeta);
        } else {
            return new LoginResponse(false, "Tarjeta invalida o inactiva", numeroTarjeta);
        }
    }

    public boolean extraer(String numeroTarjeta, String numeroCuenta, double monto) {

        if (monto <= 0) {
            throw new MontoNegativoException("El monto a extraer debe ser mayor a cero");
        }

        CuentaDTO cuenta = cuentaMapper.obtenerCuentaPorNumero(numeroCuenta);
        if (cuenta == null) {
            throw new CuentaNoExisteException("La cuenta " + numeroCuenta + " no existe");
        }
        if (!cuenta.isActiva()) {
            throw new CuentaInactivaException("La cuenta " + numeroCuenta + " esta inactiva");
        }

        CuentaDTO cuentaAsociada = cuentaMapper.obtenerCuentaPorTarjetaYNumero(numeroTarjeta, numeroCuenta);
        if (cuentaAsociada == null) {
            throw new TarjetaNoAsociadaException("La tarjeta no está asociada a la cuenta");
        }

        if (cuenta.getSaldo() < monto) {
            throw new SaldoInsuficienteException("Saldo insuficiente");
        }

        cuenta.setSaldo(cuenta.getSaldo() - monto);
        cuentaMapper.actualizarSaldo(cuenta);

        return true;
    }

    public boolean depositar(String numeroTarjeta, String numeroCuenta, double monto) {

        if (monto <= 0) {
            throw new MontoNegativoException("El monto a depositar debe ser mayor a cero");
        }

        CuentaDTO cuenta = cuentaMapper.obtenerCuentaPorNumero(numeroCuenta);
        if (cuenta == null) {
            throw new CuentaNoExisteException("La cuenta " + numeroCuenta + " no existe");
        }
        if (!cuenta.isActiva()) {
            throw new CuentaInactivaException("La cuenta esta inactiva");
        }

        CuentaDTO cuentaAsociada = cuentaMapper.obtenerCuentaPorTarjetaYNumero(numeroTarjeta, numeroCuenta);
        if (cuentaAsociada == null) {
            throw new TarjetaNoAsociadaException("La tarjeta no esta asociada a la cuenta");
        }

        cuenta.setSaldo(cuenta.getSaldo() + monto);
        cuentaMapper.actualizarSaldo(cuenta);

        return true;
    }

    public double consultarSaldo(String numeroTarjeta, String numeroCuenta) {

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

        return cuentaAsociada.getSaldo();
    }

}
