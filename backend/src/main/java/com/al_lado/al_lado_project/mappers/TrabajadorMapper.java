package com.al_lado.al_lado_project.mappers;

import com.al_lado.al_lado_project.dto.UsuarioDTO;
import com.al_lado.al_lado_project.entity.Trabajador;
import org.springframework.stereotype.Component;

@Component
public class TrabajadorMapper {

    public UsuarioDTO toDTO(Trabajador entity) {
        if (entity == null) return null;

        UsuarioDTO dto = new UsuarioDTO() {};
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setEmail(entity.getEmail());
        dto.setPassword(entity.getPassword());
        return dto;
    }

    public Trabajador toEntity(UsuarioDTO dto) {
        if (dto == null) return null;

        Trabajador entity = new Trabajador();
        entity.setId(dto.getId());
        entity.setNombre(dto.getNombre());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        return entity;
    }
}