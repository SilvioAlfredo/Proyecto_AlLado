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
}