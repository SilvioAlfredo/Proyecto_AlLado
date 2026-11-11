package com.al_lado.al_lado_project.mappers.usuario;

import com.al_lado.al_lado_project.dto.usuario.AdministradorDTO;
import com.al_lado.al_lado_project.entity.usuario.Administrador;
import org.springframework.stereotype.Component;

@Component
public class AdministradorMapper {

    public AdministradorDTO toDTO(Administrador entity) {
        if (entity == null) return null;

        AdministradorDTO dto = new AdministradorDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setEmail(entity.getEmail());
        dto.setTelefono(entity.getTelefono());
        dto.setHabilitado(entity.isHabilitado());
        dto.setNivelAcceso(entity.getNivelAcceso());
        return dto;
    }

    public Administrador toEntity(AdministradorDTO dto) {
        if (dto == null) return null;

        Administrador entity = new Administrador();
        entity.setId(dto.getId());
        entity.setNombre(dto.getNombre());
        entity.setEmail(dto.getEmail());
        entity.setTelefono(dto.getTelefono());
        entity.setPassword(dto.getPassword());
        entity.setNivelAcceso(dto.getNivelAcceso());
        return entity;
    }
}
