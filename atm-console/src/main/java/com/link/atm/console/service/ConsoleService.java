package com.link.atm.console.service;

public interface ConsoleService {
    void login(String tarjeta);
    void extraer(String tarjeta, String cbu, double amount);
    void depositar(String tarjeta, String cbu, double amount);
    void consultarSaldo(String tarjeta, String cbu);
}
