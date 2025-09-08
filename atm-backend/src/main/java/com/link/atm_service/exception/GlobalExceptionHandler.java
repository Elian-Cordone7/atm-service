package com.link.atm_service.exception;

import com.link.atm_service.config.ContentCachingRequestWrapper;
import com.link.atm_service.service.TransactionService;
import com.link.atm_service.dto.TransaccionDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final TransactionService transactionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GlobalExceptionHandler(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @ExceptionHandler({
            TarjetaNoExisteException.class,
            TarjetaInactivaException.class
    })
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleAuthExceptions(RuntimeException ex, HttpServletRequest request) {
        String errorType = getErrorType(ex);
        LOGGER.warn("Error de autenticación: {}", ex.getMessage());

        registrarTransaccionFallida(ex, request, "AUTH_ERROR");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "errorType", errorType,
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler({
            CuentaNoExisteException.class,
    })
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleNotFoundExceptions(RuntimeException ex, HttpServletRequest request) {
        String errorType = getErrorType(ex);
        LOGGER.warn("Recurso no encontrado: {}", ex.getMessage());

        registrarTransaccionFallida(ex, request, "NOT_FOUND_ERROR");

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "errorType", errorType,
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler({
            TarjetaNoAsociadaException.class,
            CuentaInactivaException.class,
            SaldoInsuficienteException.class,
            MontoNegativoException.class,
            IllegalArgumentException.class
    })
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleBusinessExceptions(RuntimeException ex, HttpServletRequest request) {
        String errorType = getErrorType(ex);
        LOGGER.warn("Error de negocio: {}", ex.getMessage());

        registrarTransaccionFallida(ex, request, "BUSINESS_ERROR");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "errorType", errorType,
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, HttpServletRequest request) {
        LOGGER.error("Error inesperado: {}", ex.getMessage(), ex);

        registrarTransaccionFallida(ex, request, "GENERIC_ERROR");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "errorType", "ERROR_INTERNO",
                        "message", "Ocurrió un error interno en el servidor"
                ));
    }

    private String getErrorType(RuntimeException ex) {
        if (ex instanceof TarjetaNoExisteException) return "TARJETA_NO_EXISTE";
        if (ex instanceof TarjetaInactivaException) return "TARJETA_INACTIVA";
        if (ex instanceof CuentaNoExisteException) return "CUENTA_NO_EXISTE";
        if (ex instanceof TarjetaNoAsociadaException) return "TARJETA_NO_ASOCIADA";
        if (ex instanceof CuentaInactivaException) return "CUENTA_INACTIVA";
        if (ex instanceof SaldoInsuficienteException) return "SALDO_INSUFICIENTE";
        if (ex instanceof MontoNegativoException) return "MONTO_NEGATIVO";
        if (ex instanceof IllegalArgumentException) return "PARAMETRO_INVALIDO";
        return "ERROR_NEGOCIO";
    }

    private void registrarTransaccionFallida(Exception ex, HttpServletRequest request, String errorCategory) {
        try {
            String numeroTarjeta = null;
            String cbu = null;
            Double monto = 0.0;

            String method = request.getMethod();
            String contentType = request.getContentType();

            LOGGER.debug("Metodo: {}, Content-Type: {}, URI: {}", method, contentType, request.getRequestURI());

            if ("GET".equalsIgnoreCase(method) && request.getRequestURI().contains("/saldo")) {
                numeroTarjeta = request.getParameter("tarjeta");
                cbu = request.getParameter("cbu");
                LOGGER.debug("GET - tarjeta: {}, cbu: {}", numeroTarjeta, cbu);
            }
            else if (("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method))
                    && contentType != null && contentType.contains("application/json")) {

                if (request instanceof ContentCachingRequestWrapper) {
                    ContentCachingRequestWrapper wrappedRequest = (ContentCachingRequestWrapper) request;
                    byte[] requestBody = wrappedRequest.getContentAsByteArray();

                    if (requestBody != null && requestBody.length > 0) {
                        try {
                            String body = new String(requestBody, wrappedRequest.getCharacterEncoding());
                            LOGGER.debug("JSON Body: {}", body);

                            JsonNode jsonNode = objectMapper.readTree(body);

                            if (jsonNode.has("tarjeta")) {
                                numeroTarjeta = jsonNode.get("tarjeta").asText();
                            }
                            if (jsonNode.has("cbu")) {
                                cbu = jsonNode.get("cbu").asText();
                            }
                            if (jsonNode.has("monto")) {
                                monto = jsonNode.get("monto").asDouble();
                            }

                            LOGGER.debug("JSON parsed - tarjeta: {}, cbu: {}, monto: {}", numeroTarjeta, cbu, monto);

                        } catch (Exception e) {
                            LOGGER.warn("Error parsing JSON: {}", e.getMessage());
                        }
                    }
                }
            }

            if (numeroTarjeta == null) {
                numeroTarjeta = request.getParameter("tarjeta");
            }
            if (cbu == null) {
                cbu = request.getParameter("cbu");
            }
            if (monto == null || monto == 0.0) {
                String montoParam = request.getParameter("monto");
                if (montoParam != null) {
                    try {
                        monto = Double.parseDouble(montoParam);
                    } catch (NumberFormatException e) {
                        monto = 0.0;
                    }
                }
            }

            if (numeroTarjeta == null) {
                numeroTarjeta = "NO_PROVIDED";
                LOGGER.warn("tarjeta no encontrado en request");
            }
            if (cbu == null) {
                cbu = "NO_PROVIDED";
                LOGGER.warn("cbu no encontrado en request");
            }

            LOGGER.debug("Final values - tarjeta: {}, cbu: {}, monto: {}", numeroTarjeta, cbu, monto);

            String requestURI = request.getRequestURI();
            String tipoTransaccion = determinarTipoTransaccion(requestURI);

            TransaccionDTO transaccion = transactionService.crearTransaccionBase(
                    tipoTransaccion, numeroTarjeta, cbu, monto
            );
            transactionService.registrarTransaccionFallida(transaccion, ex);

            LOGGER.info("Transaccion fallida registrada: {}", errorCategory);

        } catch (Exception e) {
            LOGGER.error("Error critico al registrar transaccion fallida: {}", e.getMessage(), e);
        }
    }

    private String determinarTipoTransaccion(String requestURI) {
        if (requestURI.contains("/saldo")) return "CONSULTA_SALDO";
        if (requestURI.contains("/extraer")) return "RETIRO_EFECTIVO";
        if (requestURI.contains("/depositar")) return "DEPOSITO_EFECTIVO";
        if (requestURI.contains("/transferencia")) return "TRANSFERENCIA";
        return "OPER_DESCONOCIDA";
    }
}