package com.link.atm.console.client;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
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

}
