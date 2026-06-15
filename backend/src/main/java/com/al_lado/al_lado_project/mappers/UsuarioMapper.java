package com.al_lado.al_lado_project.mappers;

import com.al_lado.al_lado_project.dto.UsuarioDTO;
import com.al_lado.al_lado_project.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioDTO toDTO(Usuario entity) {
        if (entity == null) return null;

        UsuarioDTO dto = new UsuarioDTO() {};
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setEmail(entity.getEmail());
        dto.setPassword(entity.getPassword());
        return dto;
    }

    public Usuario toEntity(UsuarioDTO dto) {
        if (dto == null) return null;

        Usuario entity = new Usuario() {};
        entity.setId(dto.getId());
        entity.setNombre(dto.getNombre());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        return entity;
    }
}
