package com.link.atm_service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TarjetaInactivaException extends AtmException{

    private static final Logger LOGGER = LoggerFactory.getLogger(TarjetaInactivaException.class);

    public TarjetaInactivaException(String message) {
        super(message);
        LOGGER.info(message);
    }
}
