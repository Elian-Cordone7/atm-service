package com.link.atm_service.controller;

import com.link.atm_service.dto.LoginResponse;
import com.link.atm_service.service.AtmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
