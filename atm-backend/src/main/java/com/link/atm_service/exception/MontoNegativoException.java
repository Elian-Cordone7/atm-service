package com.link.atm_service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MontoNegativoException extends AtmException {

    private static final Logger LOGGER = LoggerFactory.getLogger(MontoNegativoException.class);

    public MontoNegativoException(String message) {
        super(message);
        LOGGER.info(message);
    }
}