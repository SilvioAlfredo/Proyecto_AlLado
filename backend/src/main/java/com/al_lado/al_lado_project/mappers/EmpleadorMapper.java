package com.al_lado.al_lado_project.mappers;

import com.al_lado.al_lado_project.dto.UsuarioDTO;
import com.al_lado.al_lado_project.entity.Empleador;
import org.springframework.stereotype.Component;

@Component
public class EmpleadorMapper {

    public UsuarioDTO toDTO(Empleador entity) {
        if (entity == null) return null;

        UsuarioDTO dto = new UsuarioDTO() {};
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setEmail(entity.getEmail());
        dto.setPassword(entity.getPassword());
        //
        return dto;
    }

    public Empleador toEntity(UsuarioDTO dto) {
        if (dto == null) return null;

        Empleador entity = new Empleador();
        entity.setId(dto.getId());
        entity.setNombre(dto.getNombre());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        return entity;
    }
}