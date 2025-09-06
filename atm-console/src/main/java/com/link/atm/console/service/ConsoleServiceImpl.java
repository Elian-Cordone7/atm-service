package com.link.atm.console.service;

import com.link.atm.console.client.BackendClient;

public class ConsoleServiceImpl implements ConsoleService {

    private final BackendClient client;

    public ConsoleServiceImpl() {
        this.client = new BackendClient("http://localhost:8080/api"); // URL del microservicio
    }

    @Override
    public void login(String tarjeta) {
        boolean success = client.login(tarjeta);
        System.out.println(success ? "Ingreso exitoso" : "Ingreso no exitoso");
    }

    @Override
    public void extraer(String tarjeta, String cbu, double amount) {
        boolean success = client.extraer(tarjeta, cbu, amount);
        System.out.println(success ? "Retire su dinero" : "Error al extraer");
    }

    @Override
    public void depositar(String tarjeta, String cbu, double amount) {
        boolean success = client.depositar(tarjeta, cbu, amount);
        System.out.println(success ? "Depósito exitoso" : "Error al depositar");
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
