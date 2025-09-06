package com.link.atm.console;

import com.link.atm.console.service.ConsoleService;
import com.link.atm.console.service.ConsoleServiceImpl;

public class App {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso incorrecto. Ejemplo: login <tarjeta>");
            return;
        }

        String comando = args[0];
        ConsoleService consoleService = new ConsoleServiceImpl();

        try {
            switch (comando.toLowerCase()) {
                case "login":
                    String tarjeta = args[1];
                    consoleService.login(tarjeta);
                    break;
                case "extraer":
                    consoleService.extraer(args[1], args[2], Double.parseDouble(args[3]));
                    break;
                case "depositar":
                    consoleService.depositar(args[1], args[2], Double.parseDouble(args[3]));
                    break;
                case "saldo":
                    consoleService.consultarSaldo(args[1], args[2]);
                    break;
                default:
                    System.out.println("Comando no reconocido.");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
