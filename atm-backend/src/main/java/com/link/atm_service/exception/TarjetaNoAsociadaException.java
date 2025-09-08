package com.link.atm_service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TarjetaNoAsociadaException extends AtmException {

    private static final Logger LOGGER = LoggerFactory.getLogger(TarjetaNoAsociadaException.class);

    public TarjetaNoAsociadaException(String message) {
        super(message);
        LOGGER.info(message);
    }
}