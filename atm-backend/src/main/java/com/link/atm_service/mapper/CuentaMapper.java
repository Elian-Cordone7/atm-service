package com.link.atm_service.mapper;

import com.link.atm_service.dto.CuentaDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Mapper
public interface CuentaMapper {

    CuentaDTO obtenerPorTarjeta(String numeroTarjeta);

}

