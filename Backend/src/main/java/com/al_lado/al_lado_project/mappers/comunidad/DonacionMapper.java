package com.al_lado.al_lado_project.mappers.comunidad;

import com.al_lado.al_lado_project.mappers.usuario.UsuarioResumenMapper;

import com.al_lado.al_lado_project.dto.comunidad.DonacionDTO;
import com.al_lado.al_lado_project.entity.comunidad.Donacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DonacionMapper {

    private final UsuarioResumenMapper usuarioResumenMapper;

    public DonacionDTO toDTO(Donacion entity) {
        if (entity == null) return null;

        DonacionDTO dto = new DonacionDTO();
        dto.setId(entity.getId());
        dto.setMonto(entity.getMonto());
        dto.setMoneda(entity.getMoneda());
        dto.setMecanismoPago(entity.getMecanismoPago());
        dto.setFechaDonacion(entity.getFechaDonacion());
        dto.setUsuario(usuarioResumenMapper.toDTO(entity.getUsuario()));
        return dto;
    }

    public Donacion toEntity(DonacionDTO dto) {
        if (dto == null) return null;

        Donacion entity = new Donacion();
        entity.setId(dto.getId());
        entity.setMonto(dto.getMonto());
        entity.setMoneda(dto.getMoneda() != null ? dto.getMoneda() : "ARS");
        entity.setMecanismoPago(dto.getMecanismoPago());
        return entity;
    }
}
