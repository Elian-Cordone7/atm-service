package com.link.atm_service.mapper;

import com.link.atm_service.dto.CuentaDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CuentaMapper {

    CuentaDTO obtenerPorTarjeta(String numeroTarjeta);

    CuentaDTO obtenerCuentaPorTarjetaYNumero(String numeroTarjeta, String numeroCuenta);

    void actualizarSaldo(CuentaDTO cuenta);

}

