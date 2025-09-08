package com.link.atm_service.exception;

import com.link.atm_service.controller.AtmController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtmController.class);

    @ExceptionHandler({
            TarjetaNoExisteException.class,
            CuentaNoExisteException.class,
            TarjetaNoAsociadaException.class,
            CuentaInactivaException.class,
            TarjetaInactivaException.class,
            SaldoInsuficienteException.class,
            MontoNegativoException.class,
            IllegalArgumentException.class
    })
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleBusinessExceptions(RuntimeException ex) {
        String errorType = getErrorType(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "errorType", errorType,
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "errorType", "ERROR_INTERNO",
                        "message", "Error interno del servidor"
                ));
    }

    private String getErrorType(RuntimeException ex) {
        if (ex instanceof TarjetaNoExisteException) return "TARJETA_NO_EXISTE";
        if (ex instanceof CuentaNoExisteException) return "CUENTA_NO_EXISTE";
        if (ex instanceof TarjetaNoAsociadaException) return "TARJETA_NO_ASOCIADA";
        if (ex instanceof CuentaInactivaException) return "CUENTA_INACTIVA";
        if (ex instanceof TarjetaInactivaException) return "TARJETA_INACTIVA";
        if (ex instanceof SaldoInsuficienteException) return "SALDO_INSUFICIENTE";
        if (ex instanceof MontoNegativoException) return "MONTO_NEGATIVO";
        if (ex instanceof IllegalArgumentException) return "PARAMETRO_INVALIDO";
        return "ERROR_NEGOCIO";
    }
}