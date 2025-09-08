package com.link.atm_service.controller;

import com.link.atm_service.dto.TransaccionRequest;
import com.link.atm_service.dto.LoginResponse;
import com.link.atm_service.service.AtmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        LOGGER.info("Response: {}", response);

        return response.isExito()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(401).body(response);
    }

    @PostMapping("/extraer")
    public ResponseEntity<String> extraer(@RequestBody TransaccionRequest request) {
        atmService.extraer(
                request.getTarjeta(),
                request.getCbu(),
                request.getMonto()
        );

        LOGGER.info("Retire su dinero");
        return ResponseEntity.ok("Retire su dinero");
    }

    @PostMapping("/depositar")
    public ResponseEntity<String> depositar(@RequestBody TransaccionRequest request) {
        atmService.depositar(
                request.getTarjeta(),
                request.getCbu(),
                request.getMonto()
        );

        LOGGER.info("Deposito realizado correctamente");
        return ResponseEntity.ok("Deposito realizado correctamente");
    }

    @GetMapping("/saldo")
    public ResponseEntity<Map<String, Object>> consultarSaldo(
            @RequestParam String tarjeta,
            @RequestParam String cbu) {

        Double saldo = atmService.consultarSaldo(tarjeta, cbu);
        LOGGER.info("Saldo consultado: Tarjeta {}, CBU {}, Saldo {}", tarjeta, cbu, saldo);

        return ResponseEntity.ok(Map.of("saldo", saldo));
    }
}