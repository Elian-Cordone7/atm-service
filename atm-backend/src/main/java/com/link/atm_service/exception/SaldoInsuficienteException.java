package com.link.atm_service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaldoInsuficienteException extends AtmException {

    private static final Logger LOGGER = LoggerFactory.getLogger(SaldoInsuficienteException.class);

    public SaldoInsuficienteException(String message) {
        super(message);
        LOGGER.info(message);
    }
}