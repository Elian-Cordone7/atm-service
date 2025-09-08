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
            LOGGER.info("Login para tarjeta '{}'. Resultado: {}", tarjeta, code);

            if (code == 200) return true;
            if (code == 401) return false;

            String errorMessage = getStructuredErrorMessage(conn);
            throw new BackendClientException("Error en login: " + errorMessage);

        } catch (IOException e) {
            throw new BackendClientException("Error de conexion en login", e);
        }
    }

    public void extraer(String tarjeta, String cbu, double monto) {
        postJson("/extraer", tarjeta, cbu, monto, "extraccion");
    }

    public void depositar(String tarjeta, String cbu, double monto) {
        postJson("/depositar", tarjeta, cbu, monto, "deposito");
    }

    public Double consultarSaldo(String tarjeta, String cbu) {
        try {
            URL url = new URL(baseUrl + "/saldo?tarjeta=" + tarjeta + "&cbu=" + cbu);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int code = conn.getResponseCode();
            LOGGER.info("GET /saldo. -Tarjeta: {}, -CBU: {}, -Codigo: {}", tarjeta, cbu, code);

            if (code != 200) {
                String errorMessage = getStructuredErrorMessage(conn);
                handleSaldoError(code, errorMessage, tarjeta, cbu);
            }

            Scanner scanner = new Scanner(conn.getInputStream());
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            Map<?, ?> map = mapper.readValue(response, Map.class);
            return ((Number) map.get("saldo")).doubleValue();

        } catch (BackendClientException e) {
            throw e;
        } catch (Exception e) {
            throw new BackendClientException("Error al consultar saldo", e);
        }
    }

    private void postJson(String path, String tarjeta, String cbu, double monto, String operation) {
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
            LOGGER.info("POST {} -Tarjeta: {}, -CBU: {}, -Monto: {}, -Codigo: {}",
                    path, tarjeta, cbu, monto, code);

            if (code != 200) {
                String errorMessage = getStructuredErrorMessage(conn);
                handleOperationError(code, errorMessage, operation, tarjeta, cbu);
            }

        } catch (BackendClientException e) {
            throw e;
        } catch (IOException e) {
            LOGGER.error("Error de conexion en " + operation, e);
            throw new BackendClientException("Error de conexion en " + operation, e);
        }
    }

    private String getStructuredErrorMessage(HttpURLConnection conn) {
        try {
            if (conn.getErrorStream() != null) {
                Scanner errorScanner = new Scanner(conn.getErrorStream());
                String errorResponse = errorScanner.useDelimiter("\\A").next();
                errorScanner.close();

                try {
                    Map<?, ?> errorMap = mapper.readValue(errorResponse, Map.class);
                    String errorType = (String) errorMap.get("errorType");
                    String message = (String) errorMap.get("message");

                    if (errorType != null && message != null) {
                        return errorType + ": " + message;
                    }
                    return errorResponse;

                } catch (Exception e) {
                    return errorResponse;
                }
            }
            return conn.getResponseMessage();
        } catch (IOException e) {
            return "Error desconocido";
        }
    }

    private void handleSaldoError(int code, String errorMessage, String tarjeta, String cbu) {
        if (errorMessage.contains("TARJETA_NO_EXISTE")) {
            throw new BackendClientException("La tarjeta " + tarjeta + " no existe");
        } else if (errorMessage.contains("CUENTA_NO_EXISTE")) {
            throw new BackendClientException("La cuenta " + cbu + " no existe");
        } else if (errorMessage.contains("TARJETA_NO_ASOCIADA")) {
            throw new BackendClientException("La tarjeta no esta asociada a esta cuenta");
        } else if (errorMessage.contains("CUENTA_INACTIVA")) {
            throw new BackendClientException("La cuenta " + cbu + " esta inactiva");
        } else {
            throw new BackendClientException(errorMessage);
        }
    }

    private void handleOperationError(int code, String errorMessage, String operation, String tarjeta, String cbu) {
        if (errorMessage.contains("TARJETA_NO_EXISTE")) {
            throw new BackendClientException("La tarjeta " + tarjeta + " no existe");
        } else if (errorMessage.contains("CUENTA_NO_EXISTE")) {
            throw new BackendClientException("La cuenta " + cbu + " no existe");
        } else if (errorMessage.contains("TARJETA_NO_ASOCIADA")) {
            throw new BackendClientException("La tarjeta no esta asociada a esta cuenta");
        } else if (errorMessage.contains("CUENTA_INACTIVA")) {
            throw new BackendClientException("La cuenta " + cbu + " esta inactiva");
        } else if (errorMessage.contains("SALDO_INSUFICIENTE")) {
            throw new BackendClientException("Saldo insuficiente para la " + operation);
        } else {
            throw new BackendClientException(errorMessage);
        }
    }
}
