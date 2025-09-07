package com.link.atm_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransaccionRequest {

    private String tarjeta;
    private String cbu;
    private double monto;

}
