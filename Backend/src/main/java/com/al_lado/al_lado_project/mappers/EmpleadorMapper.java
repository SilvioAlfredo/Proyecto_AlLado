package com.al_lado.al_lado_project.mappers;

import com.al_lado.al_lado_project.dto.EmpleadorDTO;
import com.al_lado.al_lado_project.entity.usuario.Empleador;
import org.springframework.stereotype.Component;

@Component
public class EmpleadorMapper {

    public EmpleadorDTO toDTO(Empleador entity) {
        if (entity == null) return null;

        EmpleadorDTO dto = new EmpleadorDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setEmail(entity.getEmail());
        dto.setPassword(entity.getPassword());
        return dto;
    }

    public Empleador toEntity(EmpleadorDTO dto) {
        if (dto == null) return null;

        Empleador entity = new Empleador();
        entity.setId(dto.getId());
        entity.setNombre(dto.getNombre());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        return entity;
    }
}