package com.link.atm_service.mapper;

import com.link.atm_service.dto.TransaccionDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransaccionMapper {
    void insertarTransaccion(TransaccionDTO transaccion);
}