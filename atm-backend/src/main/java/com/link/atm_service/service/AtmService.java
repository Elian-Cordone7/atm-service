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

        return new LoginResponse(true, "Login exitoso", numeroTarjeta);
    }

    public boolean extraer(String numeroTarjeta, String numeroCuenta, double monto) {
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

        cuenta.setSaldo(cuenta.getSaldo() - monto);
        cuentaMapper.actualizarSaldo(cuenta);
        return true;
    }

    public boolean depositar(String numeroTarjeta, String numeroCuenta, double monto) {
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
