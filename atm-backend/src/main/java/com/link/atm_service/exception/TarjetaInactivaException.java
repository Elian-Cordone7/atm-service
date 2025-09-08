package com.link.atm_service.exception;

import com.link.atm_service.controller.AtmController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TarjetaInactivaException extends RuntimeException{

    private static final Logger LOGGER = LoggerFactory.getLogger(AtmController.class);

    public TarjetaInactivaException(String message) {
        super(message);
        LOGGER.info(message);
    }
}
