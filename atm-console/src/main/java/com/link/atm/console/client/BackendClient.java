package com.link.atm.console.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BackendClient {

    private static final Logger LOGGER = Logger.getLogger(BackendClient.class.getName());

    private final String baseUrl;
    private final ObjectMapper mapper;

    public BackendClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.mapper = new ObjectMapper();
    }

    public boolean login(String tarjeta) {
        try {
            URL url = new URL(baseUrl + "/login?card=" + tarjeta);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            LOGGER.info("Login request sent for tarjeta: " + tarjeta + ", response code: " + conn.getResponseCode());

            return conn.getResponseCode() == 200;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error en login", e);
            return false;
        }
    }

    public boolean extraer(String tarjeta, String cbu, double monto) {
        return postJson("/extraer", tarjeta, cbu, monto);
    }

    public boolean depositar(String tarjeta, String cbu, double monto) {
        return postJson("/depositar", tarjeta, cbu, monto);
    }

    public Double consultarSaldo(String tarjeta, String cbu) {
        try {
            URL url = new URL(baseUrl + "/consultar_saldo?card=" + tarjeta + "&cbu=" + cbu);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int code = conn.getResponseCode();
            LOGGER.info("Consultar saldo request sent for tarjeta: " + tarjeta + ", cbu: " + cbu + ", response code: " + code);

            if (code != 200) return null;

            Scanner scanner = new Scanner(conn.getInputStream());
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            Map<?, ?> map = mapper.readValue(response, Map.class);
            return ((Number) map.get("saldo")).doubleValue();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error en consultar saldo", e);
            return null;
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
            LOGGER.info("POST " + path + " sent. Tarjeta: " + tarjeta + ", CBU: " + cbu + ", monto: " + monto + ", response code: " + code);

            return code == 200;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error en POST " + path, e);
            return false;
        }
    }
}
