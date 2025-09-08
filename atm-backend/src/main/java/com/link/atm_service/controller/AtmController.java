package com.link.atm_service.controller;

import com.link.atm_service.dto.TransaccionRequest;
import com.link.atm_service.dto.LoginResponse;
import com.link.atm_service.service.AtmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class AtmController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtmController.class);

    private final AtmService atmService;

    @GetMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestParam String numeroTarjeta) {
        LoginResponse response = atmService.login(numeroTarjeta);

        LOGGER.info("Response: "+ response);
        return response.isExito()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(401).body(response);
    }

    @PostMapping("/extraer")
    public ResponseEntity<String> extraer(@RequestBody TransaccionRequest request) {
        boolean exito = atmService.extraer(
                request.getTarjeta(),
                request.getCbu(),
                request.getMonto()
        );

        if (exito) {
            LOGGER.info("Retire su dinero");
            return ResponseEntity.ok("Retire su dinero");
        } else {
            LOGGER.info("Error: no se pudo realizar la extraccion (tarjeta invalida, cuenta inactiva o saldo insuficiente)");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: no se pudo realizar la extraccion (tarjeta invalida, cuenta inactiva o saldo insuficiente)");
        }
    }

    @PostMapping("/depositar")
    public ResponseEntity<String> depositar(@RequestBody TransaccionRequest request) {
        boolean exito = atmService.depositar(
                request.getTarjeta(),
                request.getCbu(),
                request.getMonto()
        );

        if (exito) {
            LOGGER.info("Deposito realizado correctamente");
            return ResponseEntity.ok("Deposito realizado correctamente");
        } else {
            LOGGER.info("Error: no se pudo realizar el deposito (tarjeta invalida o cuenta inactiva)");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: no se pudo realizar el deposito (tarjeta invalida o cuenta inactiva)");
        }
    }

    @GetMapping("/saldo")
    public ResponseEntity<Map<String, Object>> consultarSaldo(
            @RequestParam String tarjeta,
            @RequestParam String cbu) {

        Double saldo = atmService.consultarSaldo(tarjeta, cbu);

        if (saldo != null) {
            LOGGER.info("Saldo consultado: Tarjeta {}, CBU {}, Saldo {}", tarjeta, cbu, saldo);
            return ResponseEntity.ok(Map.of("saldo", saldo));
        } else {
            LOGGER.warn("Error al consultar saldo: Tarjeta {} o CBU {} inválidos", tarjeta, cbu);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Tarjeta o cuenta inválida"));
        }
    }



}
