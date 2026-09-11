package com.al_lado.al_lado_project.mappers;

import com.al_lado.al_lado_project.dto.UsuarioResumenDTO;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioResumenMapper {

    // Este método funciona perfecto porque UsuarioResumenDTO NO es abstracto
    public UsuarioResumenDTO toDTO(Usuario entity) {
        if (entity == null) return null;

        UsuarioResumenDTO dto = new UsuarioResumenDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setEmail(entity.getEmail());
        return dto;
    }

    // Quitamos el toEntity que hacía 'new Usuario()' porque rompía al ser abstracta.
    // Si en el futuro necesitas guardar un usuario desde este DTO, se maneja
    // en los mappers específicos de Empleador o Trabajador.
}