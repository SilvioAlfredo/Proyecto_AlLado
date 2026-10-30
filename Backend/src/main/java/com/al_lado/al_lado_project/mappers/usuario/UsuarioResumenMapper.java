package com.al_lado.al_lado_project.mappers.usuario;

import com.al_lado.al_lado_project.dto.usuario.UsuarioResumenDTO;
import com.al_lado.al_lado_project.entity.usuario.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioResumenMapper {

    // Este metodo funciona perfecto porque UsuarioResumenDTO NO es abstracto
    public UsuarioResumenDTO toDTO(Usuario entity) {
        if (entity == null) return null;

        UsuarioResumenDTO dto = new UsuarioResumenDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setEmail(entity.getEmail());
        return dto;
    }

    /**
     * Proyección pública del resumen de usuario (AL-SEC-006).
     *
     * <p>Sólo identifica quién es (id y nombre): el email es dato privado del titular
     * o del administrador, así que las lecturas de terceros lo reciben nulo.</p>
     *
     * @param entity entidad a convertir, o {@code null}
     * @return DTO sin email, o {@code null}
     */
    public UsuarioResumenDTO toDTOPublic(Usuario entity) {
        if (entity == null) return null;

        UsuarioResumenDTO dto = new UsuarioResumenDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        return dto;
    }

    // Quitamos el toEntity que hacía 'new Usuario()' porque rompía al ser abstracta.
    // Si en el futuro necesitas guardar un usuario desde este DTO, se maneja
    // en los mappers específicos de Empleador o Trabajador.
}