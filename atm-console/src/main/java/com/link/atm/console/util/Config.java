package com.link.atm.console.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final Properties props = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("No se encontro application.properties");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error cargando configuracion", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
