package com.link.atm.console.service;

import com.link.atm.console.client.BackendClient;
import com.link.atm.console.client.BackendClientException;
import com.link.atm.console.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleServiceImpl implements ConsoleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleServiceImpl.class);

    private final BackendClient client;

    public ConsoleServiceImpl() {
        String baseUrl = Config.get("backend.baseUrl");
        this.client = new BackendClient(baseUrl);
    }

    @Override
    public void login(String tarjeta) {
        String last4 = tarjeta.length() > 4 ? tarjeta.substring(tarjeta.length() - 4) : tarjeta;
        LOGGER.info("Intento de login para tarjeta ****{}", last4);
        try {
            boolean success = client.login(tarjeta);
            LOGGER.info(success ? "Ingreso exitoso" : "Ingreso no exitoso");
        } catch (BackendClientException e) {
            LOGGER.error("Error: {}", e.getMessage());
        }
    }

    @Override
    public void extraer(String tarjeta, String cbu, double amount) {
        try {
            client.extraer(tarjeta, cbu, amount);
            LOGGER.info("Retire su dinero");
        } catch (BackendClientException e) {
            LOGGER.error("Error en extraccion: {}", e.getMessage());
        }
    }

    @Override
    public void depositar(String tarjeta, String cbu, double amount) {
        try {
            client.depositar(tarjeta, cbu, amount);
            LOGGER.info("Deposito exitoso");
        } catch (BackendClientException e) {
            LOGGER.info("Error en deposito: {}", e.getMessage());
        }
    }

    @Override
    public void consultarSaldo(String tarjeta, String cbu) {
        Double saldo = client.consultarSaldo(tarjeta, cbu);
        if (saldo != null) {
            LOGGER.info("Su saldo es $ {}", saldo);
        } else {
            LOGGER.info("Error al consultar saldo (Cuenta no valida o inactiva)");
        }
    }

}
