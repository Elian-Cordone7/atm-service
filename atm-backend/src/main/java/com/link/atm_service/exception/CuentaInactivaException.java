package com.link.atm_service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CuentaInactivaException extends AtmException {

    private static final Logger LOGGER = LoggerFactory.getLogger(CuentaInactivaException.class);

    public CuentaInactivaException(String message) {
        super(message);
        LOGGER.info(message);
    }
}