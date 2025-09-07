package com.link.atm_service.service;

import com.link.atm_service.controller.AtmController;
import com.link.atm_service.dto.CuentaDTO;
import com.link.atm_service.dto.LoginResponse;
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

}
