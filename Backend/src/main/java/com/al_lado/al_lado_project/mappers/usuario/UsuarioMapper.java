package com.al_lado.al_lado_project.mappers.usuario;

import com.al_lado.al_lado_project.dto.usuario.UsuarioDTO;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    // Solo toDTO por diseno: Usuario es abstracta y no puede instanciarse
    // en un toEntity generico. La creacion se delega a los mappers
    // concretos (TrabajadorMapper, EmpleadorMapper, AdministradorMapper).
    public UsuarioDTO toDTO(Usuario entity) {
        if (entity == null) return null;

        UsuarioDTO dto = new UsuarioDTO() {};
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setEmail(entity.getEmail());
        dto.setTelefono(entity.getTelefono());
        dto.setHabilitado(entity.isHabilitado());
        return dto;
    }
}