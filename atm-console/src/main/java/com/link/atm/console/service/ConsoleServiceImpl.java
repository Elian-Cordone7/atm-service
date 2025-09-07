package com.link.atm.console.service;

import com.link.atm.console.client.BackendClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleServiceImpl implements ConsoleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleServiceImpl.class);

    private final BackendClient client;

    public ConsoleServiceImpl() {
        this.client = new BackendClient("http://localhost:8080/api");
    }

    @Override
    public void login(String tarjeta) {
        String last4 = tarjeta.length() > 4 ? tarjeta.substring(tarjeta.length() - 4) : tarjeta;
        LOGGER.info("Intento de login para tarjeta ****{}", last4);
        boolean success = client.login(tarjeta);
        LOGGER.info(success ? "Ingreso exitoso" : "Ingreso no exitoso");
    }

    @Override
    public void extraer(String tarjeta, String cbu, double amount) {
        boolean success = client.extraer(tarjeta, cbu, amount);
        LOGGER.info(success ? "Retire su dinero" : "Error al extraer (tarjeta invalida, cuenta inactiva o saldo insuficiente)");
    }

    @Override
    public void depositar(String tarjeta, String cbu, double amount) {
        boolean success = client.depositar(tarjeta, cbu, amount);
        System.out.println(success ? "Deposito exitoso" : "Error al depositar (tarjeta invalida o cuenta inactiva)");
    }

    @Override
    public void consultarSaldo(String tarjeta, String cbu) {
        Double saldo = client.consultarSaldo(tarjeta, cbu);
        if (saldo != null) {
            System.out.printf("Su saldo es $ %.2f%n", saldo);
        } else {
            System.out.println("Error al consultar saldo");
        }
    }
}
