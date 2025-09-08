package com.link.atm_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionDTO {
    private Long id;
    private String tipoTransaccion;
    private String numeroTarjeta;
    private String numeroCuenta;
    private Double monto;
    private Double saldoAnterior;
    private Double saldoPosterior;
    private LocalDateTime fechaTransaccion;
    private String estado;
    private String motivoError;
    private String codigoError;
    private String detallesAdicionales;
}