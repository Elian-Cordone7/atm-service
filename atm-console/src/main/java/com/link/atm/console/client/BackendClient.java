package com.link.atm.console.client;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class BackendClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackendClient.class);

    private final String baseUrl;
    private final ObjectMapper mapper;

    public BackendClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.mapper = new ObjectMapper();
    }

    public boolean login(String tarjeta) {
        try {
            URL url = new URL(baseUrl + "/login?numeroTarjeta=" + tarjeta);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int code = conn.getResponseCode();
            LOGGER.info("Se realizo una solicitud de login para la tarjeta '{}'. Resultado HTTP: {}", tarjeta, code);

            return code == 200;
        } catch (IOException e) {
            LOGGER.info("Error en login", e);
            return false;
        }
    }

    private boolean postJson(String path, String tarjeta, String cbu, double monto) {
        try {
            URL url = new URL(baseUrl + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            Map<String, Object> body = new HashMap<>();
            body.put("tarjeta", tarjeta);
            body.put("cbu", cbu);
            body.put("monto", monto);

            String json = mapper.writeValueAsString(body);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
                os.flush();
            }

            int code = conn.getResponseCode();
            LOGGER.info("POST {} sent. Tarjeta: {}, Cuenta: {}, Monto: {}, Response code: {}",
                    path, tarjeta, cbu, monto, code);

            return code == 200;
        } catch (IOException e) {
            LOGGER.error("Error en POST " + path, e);
            return false;
        }
    }

    public boolean extraer(String tarjeta, String cbu, double monto) {
        return postJson("/extraer", tarjeta, cbu, monto);
    }

    public boolean depositar(String tarjeta, String cbu, double monto) {
        return postJson("/depositar", tarjeta, cbu, monto);
    }
    
}
